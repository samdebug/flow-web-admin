package com.yzx.flow.modular.customer.controller;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yzx.flow.common.constant.tips.ErrorTip;
import com.yzx.flow.common.controller.BaseController;
import com.yzx.flow.common.exception.BizExceptionEnum;
import com.yzx.flow.common.exception.BussinessException;
import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.page.PageInfoBT;
import com.yzx.flow.common.persistence.model.CustomerInfo;
import com.yzx.flow.common.persistence.model.OrderDetail;
import com.yzx.flow.common.persistence.model.OrderInfo;
import com.yzx.flow.common.persistence.model.PartnerInfo;
import com.yzx.flow.common.transformers.TransformerManager;
import com.yzx.flow.core.portal.PortalParamSetter;
import com.yzx.flow.core.portal.annotation.AutoSetPortalCustomer;
import com.yzx.flow.core.portal.annotation.PortalParamMeta;
import com.yzx.flow.core.shiro.ShiroKit;
import com.yzx.flow.core.shiro.ShiroUser;
import com.yzx.flow.core.util.NumUtil;
import com.yzx.flow.modular.customer.service.ICustomerBalanceDayService;
import com.yzx.flow.modular.customer.service.ICustomerInfoService;
import com.yzx.flow.modular.customer.transformer.CustomerVoTransformer;
import com.yzx.flow.modular.customer.transformer.PartnerNameValuePairTransformer;
import com.yzx.flow.modular.customer.vo.CustomerProductsBo;
import com.yzx.flow.modular.customer.vo.CustomerVo;
import com.yzx.flow.modular.order.service.IOrderInfoService;
import com.yzx.flow.modular.partner.service.IPartnerService;

/**
 * 客户管理控制器
 *
 * @author liuyufeng
 * @Date 2017-08-16 16:23:21
 */
@Controller
@RequestMapping("/customer")
public class CustomerController extends BaseController {
	
	private static final Logger LOG = LoggerFactory.getLogger(CustomerController.class);
	

    private String PREFIX = "/customer/";
    
    
    @Autowired
    private ICustomerInfoService customerInfoService;
    
    @Autowired
    private IPartnerService partnerService;
    
    @Autowired
    private IOrderInfoService orderInfoService;
    
    @Autowired
    private ICustomerBalanceDayService customerBalanceDayService;
    
    

    /**
     * 跳转到客户管理首页
     */
    @RequestMapping("")
    public String index() {
        return PREFIX + "customer.html";
    }

    /**
     * 跳转到添加客户管理
     */
    @RequestMapping("/customer_add")
    public String customerAdd() {
        return PREFIX + "customer_add.html";
    }

    /**
     * 跳转到修改客户管理
     */
    @RequestMapping("/customer_update/{customerId}")
    public String customerUpdate(@PathVariable long customerId, Model model) {
    	
    	CustomerInfo customer = customerInfoService.get(customerId);
    	if ( customer == null ) {
			return String.format("%s%s", FORWARD, "/global/error");
		}
    	model.addAttribute("customer", customer);
        return PREFIX + "customer_edit.html";
    }

    /**
     * 获取客户管理列表
     */
    @RequestMapping(value = "/list")
    @ResponseBody
    @AutoSetPortalCustomer({ @PortalParamMeta(setter = PortalParamSetter.PAGE)})
    public Object list(Page<CustomerInfo> page) {
    	List<CustomerInfo> data = customerInfoService.pageQuery(page);
    	List<CustomerVo> temp = TransformerManager.transforms(CustomerVoTransformer.TRANSFORMER_NAME, data);
        return new PageInfoBT<CustomerVo>(temp, page.getTotal());
    }

    /**
     * 新增客户管理
     */
    @RequestMapping(value = "/add")
    @ResponseBody
    public Object add(CustomerProductsBo bo) {
    	
    	CustomerInfo data = bo.buildCustomerInfo();
    	data.setCreditAmount(BigDecimal.ZERO);
    	
    	try {
    		saveAndUpdate(data, bo.buildOrderInfo());
    	} catch(BussinessException be) {
    		return ErrorTip.buildErrorTip(be.getMessage());
    	}
        return SUCCESS_TIP;
    }

    /**
     * 删除客户管理
     */
    @RequestMapping(value = "/delete")
    @ResponseBody
    public Object delete(Long customerId) {
    	CustomerInfo data = customerInfoService.get(customerId);
    	// TODO 
//		PartnerInfo partnerInfo = getParentInfo();
//		boolean flag=validate(data, partnerInfo);
//		if(!flag){
//			return fail("非法操作");
//		}
		// 是否有订单
		List<OrderInfo> orderInfoList = orderInfoService.getByCustomerId(customerId);
		if (!orderInfoList.isEmpty()) {
		    return ErrorTip.buildErrorTip("当前客户仍然有订单，不可删除");
		}
		try {
			customerInfoService.updateDeletedFlag(data);
    	} catch (BussinessException e) {
    		return ErrorTip.buildErrorTip(e.getMessage());
    	}
        return SUCCESS_TIP;
    }
    
    


    /**
     * 修改客户管理
     */
    @RequestMapping(value = "/update")
    @ResponseBody
    public Object update(CustomerProductsBo data) {
    	
    	CustomerInfo customer = customerInfoService.get(data.getCustomerId());
		if(customer == null)
			return new ErrorTip(BizExceptionEnum.CUSTOMER_NOT_EXIST);
		
		customer.setLinkmanName(data.getLinkmanName());
		customer.setAddress(data.getAddress());
		customer.setLinkmanEmail(data.getLinkmanEmail());
		customer.setBalanceLackConfigure(data.getBalanceLackConfigure());
		customer.setLinkmanMobile(data.getLinkmanMobile());
		
		customer.setCustomerLevel(data.getCustomerLevel());
		customer.setStatus(data.getStatus());
//		if(customer.getPartnerType()==1){
//			customer.setLinkmanMobile(data.getLinkmanMobile());
//		}
		
		try {
			saveAndUpdate(customer, data.buildOrderInfo());
		} catch(BussinessException be) {
    		return ErrorTip.buildErrorTip(be.getMessage());
    	}
        return super.SUCCESS_TIP;
    }

    /**
     * 客户管理详情
     */
    @RequestMapping(value = "/detail")
    public String detail(@RequestParam("id") long customerId, Model model) {
    	CustomerInfo customer = customerInfoService.get(customerId);
    	model.addAttribute("customer", customer);
    	model.addAttribute("flag", customer != null);// 标志是否有数据
    	model.addAttribute("products", Collections.emptyList());
    	
    	if ( customer != null ) {
    		// 只查询流量包类型
    		List<OrderInfo> infos = orderInfoService.getByCustomerIdAndOrderType(customerId, com.yzx.flow.common.util.Constant.ORDER_TYPE_PACKAGE);
    		if (infos != null && !infos.isEmpty()) {
        		List<OrderDetail> orderDetailList = orderInfoService.getOrderDetailByOrderId(infos.get(0).getOrderId());
        		if ( orderDetailList != null && !orderDetailList.isEmpty() )
        			model.addAttribute("products", orderDetailList);
    		}
    	}
    	return PREFIX + "customer_view.html";
    }
    
    
    /**
     * 客户流量下发监控
     * @return
     */
    @RequestMapping(value = "/activityOverview")
    public String activityOverview() {
    	return PREFIX + "customerActivityOverview.html";
    }
    
    
    /**
     * 所有合作伙伴name-id对
     * @return
     */
    @RequestMapping(value = "/queryPartners")
	@ResponseBody
	public Object queryAll() {
    	// TODO 
//		Map<String, Object> map =new HashMap<String, Object>();
//		if (!isAdmin()) {
//			PartnerInfo partnerInfo=getParentInfo();
//			if(partnerInfo==null){
//				return fail("不是管理员也没有合作伙伴");
//			}
//			map.put("data",partnerInfo);
//			map.put("success",true);
//			map.put("isAdmin",false);
//			return map;
//		}
		List<PartnerInfo> list = partnerService.queryAll(null);
		return TransformerManager.transforms(PartnerNameValuePairTransformer.TRANSFORMER_NAME, list);
	}
    
    
    
    
    private void saveAndUpdate(CustomerInfo customer, OrderInfo updateOrder) {
    	
    	PartnerInfo partner = null;
		if ( customer.getPartnerId() != null && customer.getPartnerId().compareTo(0L) > 0 
				&& (partner = partnerService.get(customer.getPartnerId())) == null) {
			throw new BussinessException(BizExceptionEnum.PARTNER_NOT_EXIST);
		}
		
    	if (partner != null && !PartnerInfo.STATUS_OK.equals(partner.getStatus())) 
    		throw new BussinessException(BizExceptionEnum.PARTNER_NOT_USE);
		
		if (StringUtils.isBlank(customer.getAccount())) {
			throw new BussinessException(BizExceptionEnum.REQUEST_NULL);
		}
		
		customer.setPartnerId(partner == null ? null : partner.getPartnerId());
		customer.setPartnerType(partner == null ? PartnerInfo.PARTNER_TYPE_FLOW : partner.getPartnerType());
    	
		ShiroUser current = ShiroKit.getUser();
		
		if ( customer.getCustomerId() == null || customer.getCustomerId().equals(0L) ) {
			customer.setCreator(current.getAccount());
			customer.setCreateTime(new Date());
			customer.setUpdator("");
			// dufault value;
			customer.setCompanyMobile("");
			customer.setCompanyName("");
			customer.setIsSend(CustomerInfo.IS_SEND_OFF);
			customer.setCustomerLinkman("");
			customer.setShorterName("");
		} else {
			customer.setUpdator(current.getAccount());
			customer.setUpdateTime(new Date());
		}
    	
		customerInfoService.saveAndUpdate(customer, updateOrder);
    }
    
    
    
    @RequestMapping(value = "/modifyCustomerLevel")
	@ResponseBody
	public Object modifyCustomerLevel(Long customerId, Integer customerLevel, String orderRiskSetting) {
    	CustomerInfo customerInfo = customerInfoService.get(customerId);
    	if (null == customerInfo){
    		return ErrorTip.buildErrorTip("修改的客户不存在");
    	}
    	customerInfo.setCustomerLevel(customerLevel);
    	if (customerLevel.intValue() >=4 && StringUtils.isNotEmpty(orderRiskSetting)) {
    		customerInfo.setOrderRiskSetting(new BigDecimal(orderRiskSetting));
		}else {
			customerInfo.setOrderRiskSetting(null);
		}
		customerInfoService.update(customerInfo);
		return SUCCESS_TIP;
	}
    
    
    /**
	 * 
	 * 修改客户状态
	 * 
	 * @param passwd
	 * @param customerId
	 * @return
	 */
	@RequestMapping(value = "/changeStatus")
	@ResponseBody
	public Object changeStatus(Integer status, Long customerId) {

		CustomerInfo data = customerInfoService.get(customerId);
		// TODO
//		PartnerInfo partnerInfo = getParentInfo();
//		boolean flag = validate(data, partnerInfo);
//		if(!flag){
//			return fail("非法操作");
//		}
//		if (status < 0 || status > 2) {
//			return fail("非法操作");
//		}
		data.setStatus(status);
		customerInfoService.saveAndUpdate(data, null);
		return SUCCESS_TIP;
	}
	
	
	/**
	 * 重置密码接口
	 * @return
	 */
	@RequestMapping(value = "/resetPw")
	@ResponseBody
	public Object resetpassword(Long customerId) {
		try {
			customerInfoService.resetPasswd(customerId);
			return SUCCESS_TIP;
		} catch (BussinessException e) {
			return ErrorTip.buildErrorTip(e.getMessage());
		}
	}
    
	
	/**
     * 交易类型 1：结算   2：充值   3：授信
     */
    public static final Integer TRADE_TYPE_RECHARGE = 2;
    
    @RequestMapping(value = "/initBalance.ajax")
    @ResponseBody
    public Object initBalance(Long customerId) {
        Map<String, Object> map = new HashMap<String, Object>();
        CustomerInfo customerInfo = customerInfoService.get(customerId);
        if (null == customerInfo) {
            LOG.error("未获取到用户对象，null");
            return ErrorTip.buildErrorTip("非法请求");
        }
        // 累计存款
        double rechargeAmount = customerBalanceDayService.selectCustomerSUMTraderAmount(customerId, null, null, TRADE_TYPE_RECHARGE);
        map.put("rechargeAmount", String.valueOf(rechargeAmount));
        // 累计消费
        double sumCustomerAmount = customerBalanceDayService.selectSUMCustomerAmount(customerId);
        map.put("sumCustomerAmount", BigDecimal.valueOf(0 - sumCustomerAmount).add(customerInfo.getCurrentAmount()).setScale(2, BigDecimal.ROUND_HALF_UP));
        // 可用额度
        BigDecimal money = customerInfo.getBalance().add(customerInfo.getCreditAmount()).subtract(customerInfo.getCurrentAmount());
        map.put("availableCredit", money.setScale(2, BigDecimal.ROUND_HALF_UP));
        return map;
    }
    
    
    @RequestMapping(value = "/get")
	@ResponseBody
	@AutoSetPortalCustomer({ @PortalParamMeta(setter = PortalParamSetter.CUSTOMER_ID_LONG)})
	public Object get(Long customerId) {
		CustomerInfo data = customerInfoService.get(customerId);
		Map<String, Object> resMap=new HashMap<String, Object>();
		if(data.getIsDeleted().equals(1)){
			return ErrorTip.buildErrorTip(BizExceptionEnum.REQUEST_NULL);
		}
		if(data.getBackCost() > 0d){
			
			data.setBackCost(NumUtil.round(data.getBackCost() * 100, 5));
		}
		resMap.put("data", data);
		resMap.put("success", Boolean.valueOf(true));
		return resMap;
	}
    
    
    @RequestMapping(value = "/soloAccount.ajax", method = RequestMethod.POST)
	@ResponseBody
	public Object soloAccount(CustomerInfo data,@RequestParam(value = "updId", required = false) Long updId) {
		if (StringUtils.isBlank(data.getAccount())) {
			return ErrorTip.buildErrorTip("账号不能为空");
		}
		if (updId != null) {
			List<CustomerInfo> customerInfoList = customerInfoService.getAccount(data.getAccount());
			if (customerInfoList != null && customerInfoList.size() > 1) {
				return ErrorTip.buildErrorTip("当前已有相同账号");
			}
			if (customerInfoList != null) {
				for (int i = 0; i < customerInfoList.size(); i++) {
					if (!customerInfoList.get(i).getCustomerId() .equals(updId)) {
						return ErrorTip.buildErrorTip("当前已有相同账号");
					}
				}
			}
		} else {
			List<CustomerInfo> customerInfoList = customerInfoService.getAccount(data.getAccount());
			if (!customerInfoList.isEmpty()) {
				return ErrorTip.buildErrorTip("当前已有相同账号");
			}
		}
		return SUCCESS_TIP;
	}
    
}
