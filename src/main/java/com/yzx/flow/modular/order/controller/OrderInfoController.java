package com.yzx.flow.modular.order.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
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
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yzx.flow.common.constant.tips.ErrorTip;
import com.yzx.flow.common.controller.BaseController;
import com.yzx.flow.common.excel.TemplateExcel;
import com.yzx.flow.common.excel.TemplateExcelManager;
import com.yzx.flow.common.exception.BizExceptionEnum;
import com.yzx.flow.common.exception.BussinessException;
import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.page.PageInfoBT;
import com.yzx.flow.common.persistence.model.CustomerInfo;
import com.yzx.flow.common.persistence.model.FlowAppInfo;
import com.yzx.flow.common.persistence.model.FlowCardInfo;
import com.yzx.flow.common.persistence.model.FlowProductInfo;
import com.yzx.flow.common.persistence.model.OrderDetail;
import com.yzx.flow.common.persistence.model.OrderInfo;
import com.yzx.flow.common.persistence.model.PartnerInfo;
import com.yzx.flow.common.util.Constant;
import com.yzx.flow.common.util.OrderSeqGen;
import com.yzx.flow.core.portal.PortalParamSetter;
import com.yzx.flow.core.portal.annotation.AutoSetPortalCustomer;
import com.yzx.flow.core.portal.annotation.PortalParamMeta;
import com.yzx.flow.core.shiro.ShiroKit;
import com.yzx.flow.core.shiro.ShiroUser;
import com.yzx.flow.modular.flow.service.IFlowProductInfoService;
import com.yzx.flow.modular.flowOrder.Service.IFlowAppInfoService;
import com.yzx.flow.modular.flowOrder.Service.IFlowCardInfoService;
import com.yzx.flow.modular.order.service.IOrderDetailService;
import com.yzx.flow.modular.order.service.IOrderInfoService;
import com.yzx.flow.modular.partner.service.IPartnerService;
import com.yzx.flow.modular.system.dao.CustomerInfoDao;

/**
 * 
 * <b>Title：</b>OrderInfoController.java<br/>
 * <b>Description：</b> <br/>
 * <b>@author： </b>v5480<br/>
 * <b>@date：</b>2015-07-08 18:05:18<br/>
 * <b>Copyright (c) 2015 szwisdom Tech.</b>
 * 
 */
@Controller
@RequestMapping("/orderInfo")
public class OrderInfoController extends BaseController {
	
	private static final Logger LOG = LoggerFactory.getLogger(OrderInfoController.class);
	
	private String PREFIX = "/order/";
	
	
	@Autowired
	private IOrderInfoService orderInfoService;

	@Autowired
	private IPartnerService partnerService;
	
	@Autowired
    private CustomerInfoDao customerInfoDao;
	
	@Autowired
	private IOrderDetailService orderDetailService;
	
	@Autowired
	private IFlowProductInfoService flowProductInfoService;

	@Autowired
	private IFlowAppInfoService flowAppInfoService;

	@Autowired
	private IFlowCardInfoService flowCardInfoService;
	
	/**
     * 订单类型  2:流量+卡   1: 流量包
     */
    public static final Integer ORDERTYPEFLOWPACKAGE = 1;
    
    /**
     * 合作伙伴状态  1: 商用
     */
    public static final String PARTNERSTATUS = "1";
    
    /**
     * 客户状态  1: 商用
     */
    public static final Integer CUSTOMERSTATUS = 1;
    @InitBinder
	public void initListBinder(WebDataBinder binder) {
		// 设置需要包裹的元素个数，默认为256
	    binder.setAutoGrowCollectionLimit(5000);
	}
    
    
    /**
     * 跳转到订单添加页面
     * @return
     */
    @RequestMapping(value = "/add")
    public String orderAdd() {
    	return String.format("%s%s", PREFIX, "orderInfo_add.html");
    }
    
    /**
     * 进入订单列表（客户管理配置列表）
     * @return
     */
    @RequestMapping(value = "/list")
    public String toList() {
    	return String.format("%s%s", PREFIX, "orderInfo.html");
    }
    
    
    /**
     * 跳转到 订单详情
     * @return
     */
    @RequestMapping(value = "/view")
    public String toView() {
    	return String.format("%s%s", PREFIX, "orderInfo_view.html");
    }
    
    
    
	@RequestMapping(value = "/query")
	@ResponseBody
	@AutoSetPortalCustomer({ @PortalParamMeta(setter = PortalParamSetter.PAGE)})
	public Object pageQuery(Page<OrderInfo> page) {
		// TODO 
//		Staff staff = getCurrentLogin();
//	    if (isAdmin()) {
//	        page.getParams().put("partnerId", null);
//			if(staff.getDepartmentId() == 6){
//				page.getParams().put("realName", staff.getRealName());
//			}
//	    } else {
//        	PartnerInfo partnerInfo = partnerInfoService.getByAccount(staff.getLoginName());
//        	if (partnerInfo == null) {
//        		return super.fail("合作伙伴不存在");
//        	}
//        	page.getParams().put("partnerId", partnerInfo.getPartnerId());
//	    }
		Page<OrderInfo> list = orderInfoService.pageQuery(page);
		return new PageInfoBT<>(list.getDatas(), list.getTotal());
	}
	
	@RequestMapping(value = "/orderInfoViewInit")
	@ResponseBody
	public Map<String, Object> orderInfoViewInit() {
	    Map<String, Object> map = new HashMap<String, Object>();
	    // TODO
//      map.put("isWYAdmin", isAdmin());
        map.put("isWYAdmin", isAdmin());
        return map;
	}
	
   protected boolean isAdmin() {
        try {
            return ShiroKit.isAdmin();
        } catch (Exception e) {
        	LOG.error(e.getMessage(), e);
            return false;
        }
    }

	@RequestMapping(value = "/add.ajax", method = RequestMethod.POST)
	@ResponseBody
	public Object add(OrderInfo orderInfo, @RequestParam(value = "updId", required = false) Long updId) {
	    
		ShiroUser staff = ShiroKit.getUser();
	    try {
	    	// 合法性校验及参数构造
		    authenticationChcekAndCreateParams(orderInfo, staff);
		    
		    // 【订单提交】基础校验和业务逻辑校验
	        paramCheck(orderInfo, updId);
	        
	        // 新增操作
	        if (updId == null) {
	            orderInfo.setStatus(Constant.ORDER_STATUS_EFFECTIVE);
	            orderInfo.setCreator(staff.getAccount());
	            orderInfo.setCreateTime(new Date());
	        } else {
	            orderInfo.setStatus(Constant.ORDER_STATUS_INIT);
	            orderInfo.setUpdator(staff.getAccount());
	            orderInfo.setUpdateTime(new Date());
	        }
	        orderInfoService.addOrUpdateOrderInfo(orderInfo, staff, updId);
	    } catch (BussinessException be) {
	    	return ErrorTip.buildErrorTip(be.getMessage());
	    } catch (Exception e) {
	    	throw e;// 后续的异常日志处理
	    }
		return SUCCESS_TIP;
	}
	
	/**
	 *  合法性校验及参数构造
	 *  
	 * @param customerId 当前订单所属客户的ID
	 * @return
	 */
    private void authenticationChcekAndCreateParams(OrderInfo orderInfo, ShiroUser staff) {
        if (null == orderInfo.getCustomerId()) {
        	throw new BussinessException(BizExceptionEnum.CUSTOMER_FORMAT_ERROR, "请选择客户名称");
        }
        CustomerInfo customerInfo = customerInfoDao.getCustomerInfoByCustomerId(orderInfo.getCustomerId());
        
        // 客户及合作伙伴身份合法性校验
        authenticationChcek(customerInfo);
        // 参数构造
        createParams(customerInfo, orderInfo, staff);
    }
	
	/**
	 * 客户及合作伙伴身份合法性校验
	 * 
	 * @param customerId 当前订单所属客户
	 */
	private void authenticationChcek(CustomerInfo customerInfo) {
        if (null == customerInfo) {
            throw new BussinessException(BizExceptionEnum.CUSTOMER_FORMAT_ERROR, "非法客户");
        }
        if (customerInfo.getStatus() != CUSTOMERSTATUS) {
        	throw new BussinessException(BizExceptionEnum.CUSTOMER_FORMAT_ERROR, "当前客户是非商用状态，不能提交订单。");
        }
        PartnerInfo pi = partnerService.get(customerInfo.getPartnerId());
        if (pi != null && !PARTNERSTATUS.equals(pi.getStatus())) {
        	throw new BussinessException(BizExceptionEnum.CUSTOMER_FORMAT_ERROR, "当前客户所属合作伙伴是非商用状态，不能提交订单。");
        }
	}
	
	/**
	 * 构造订单所属的【合作伙伴】参数
	 * 
	 * @param customerInfo 当前订单所属客户
	 * @return
	 */
	private void createParams(CustomerInfo customerInfo, OrderInfo orderInfo, ShiroUser staff) {
		// TODO 
//        if (isAdmin()) {
            orderInfo.setPartnerId(customerInfo.getPartnerId());
//        } else {
//            // 合作伙伴ID
//            PartnerInfo pInfo = partnerInfoService.getByAccount(staff.getLoginName());
//            if (pInfo == null) {
//                return super.fail("合作伙伴不存在");
//            }
//            orderInfo.setPartnerId(pInfo.getPartnerId());
//        }
//        return super.success("构造参数成功");
	}
	
	/**
	 * 【订单提交】基础校验和业务逻辑校验
	 * 
	 * @param orderInfo 订单对象
	 * @return
	 */
    private void paramCheck(OrderInfo orderInfo, Long updId) {
        // 关联产品为空校验
        if (orderInfo.getFlowProductInfoList() == null) {
        	throw new BussinessException(BizExceptionEnum.CUSTOMER_FORMAT_ERROR, "请选择要订购的产品");
        }
        // 订单中产品的基础校验和业务逻辑校验
        productBaseAndBusinessCheck(orderInfo);
        // 最小日期校验
        if (ORDERTYPEFLOWPACKAGE != orderInfo.getOrderType() && orderInfo.getDeliveryTime() != null && orderInfo.getDeliveryTime().getTime() < new Date().getTime()) {
        	throw new BussinessException(BizExceptionEnum.CUSTOMER_FORMAT_ERROR, "不能选择今天之前的日期");
        }
        // 流量包订单不可重复提交校验
        packageOrderResubmitCheck(orderInfo, updId);
    }
    
    /**
     * 订单中产品的基础校验和业务逻辑校验
     * 
     * @param orderInfo 当前订单
     * @return
     */
    private void productBaseAndBusinessCheck(OrderInfo orderInfo) {
        for (FlowProductInfo fpi : orderInfo.getFlowProductInfoList()) {
            if (null == fpi.getProductId()) {
                continue;
            }
            if (fpi.getSettlementAmount() == null) {
            	throw new BussinessException(BizExceptionEnum.CUSTOMER_FORMAT_ERROR, "请输入结算价格");
            }
            if (!fpi.getSettlementAmount().toString().matches("^[0-9]+(\\.[0-9]*)?$")) {
            	throw new BussinessException(BizExceptionEnum.CUSTOMER_FORMAT_ERROR, "请输入合法的价格");
            }
            if (!String.valueOf(fpi.getProductCount()).matches("^[0-9]+(\\.[0-9]*)?$")) {
            	throw new BussinessException(BizExceptionEnum.CUSTOMER_FORMAT_ERROR, "请输入合法的数量");
            }
            //流量加做判断
            if (2 == orderInfo.getOrderType() && !String.valueOf(fpi.getProductCount()).matches("^[1-9]\\d*$")) {
            	throw new BussinessException(BizExceptionEnum.CUSTOMER_FORMAT_ERROR, "订购数量必须为大于0的整数，请重新输入。");
            }
            // 校验价格是否倒挂
            // 客户订单优化处理-客户订单产品价格不能低于合作伙伴订单价格
            List<FlowProductInfo> listAll = flowProductInfoService.getByPartnerInfoType(orderInfo.getPartnerId(), null, null,null,null);
            for (FlowProductInfo fpiDB : listAll) {
                if (fpi.getProductId().longValue() == fpiDB.getProductId().longValue() && fpi.getSettlementAmount().compareTo(fpiDB.getSettlementAmount()) == -1) {
                	throw new BussinessException(BizExceptionEnum.CUSTOMER_FORMAT_ERROR, "产品名称为【" + fpi.getProductName() + "】的结算价格(" + fpi.getSettlementAmount() + "元)小于当前合作伙伴的结算价格("+ fpiDB.getSettlementAmount() +"元), 请重新输入。");
                }
            }
        }
    }
    
    /**
     * 流量包订单不可重复提交校验
     * 
     * @param orderInfo 当前订单信息
     * @param updId 更新操作标识符
     * @return
     */
    private void packageOrderResubmitCheck(OrderInfo orderInfo, Long updId) {
        // 订单新增操作
        if (updId == null) {
            PartnerInfo partnerInfo = partnerService.get(orderInfo.getPartnerId());
            // 渠道直充合作伙伴
            if (partnerInfo.getPartnerType() == PartnerInfo.PARTNER_TYPE_CHANNEL) {
                List<OrderInfo> infos = orderInfoService.getByPartnerIdAndCustomerId(orderInfo.getPartnerId(), orderInfo.getCustomerId());
                if (!infos.isEmpty()) {
                    throw new BussinessException(BizExceptionEnum.CUSTOMER_FORMAT_ERROR, "该客户已有订单，不可重复提交");
                }
            }
            if (ORDERTYPEFLOWPACKAGE == orderInfo.getOrderType()) {
                List<OrderInfo> orderInfos = orderInfoService.getByCustomerIdAndOrderType(orderInfo.getCustomerId(), ORDERTYPEFLOWPACKAGE);
                if (!orderInfos.isEmpty()) {
                	throw new BussinessException(BizExceptionEnum.CUSTOMER_FORMAT_ERROR, "该客户已有流量包订单，不可重复提交");
                }
            }
        }
    }
    
	@RequestMapping(value = "/delete")
	@ResponseBody
	public Object delete(Long orderId) {
	    // 是否有App接入
	    List<FlowAppInfo> flowAppInfoList = flowAppInfoService.getAppInfoListByOrderId(orderId);
	    if (!flowAppInfoList.isEmpty()) {
	        return ErrorTip.buildErrorTip("当前订单关联有App接入信息，不可删除");
	    }
	    // 是否有激活或已兑换的流量加卡
	    List<OrderDetail> orderDetails = orderDetailService.getByOrderId(orderId);
	    for (OrderDetail orderDetail : orderDetails) {
	        List<FlowCardInfo> flowCardInfoList = flowCardInfoService.selectActiveAndExchangeCardInfoList(orderDetail.getOrderDetailId());
	        if (!flowCardInfoList.isEmpty()) {
	            return ErrorTip.buildErrorTip("当前订单的详情ID【" + orderDetail.getOrderDetailId() + "】关联有激活或已兑换的流量加卡，不可删除");
	        }
	    }
		orderInfoService.delete(orderId);
		return SUCCESS_TIP;
	}

	@RequestMapping(value = "/get.ajax")
	@ResponseBody
	public Object get(Long orderId) {
		OrderInfo data = orderInfoService.get(orderId);
		List<OrderDetail> orderDetailList = orderInfoService.getOrderDetailByOrderId(orderId);
		data.setOrderDetailList(orderDetailList);
		return data;
	}

	/**
	 * 更新的时候需额外传递updId,值跟主键值一样,被@ModelAttribute注释的方法会在此controller每个方法执行前被执行，
	 * 要谨慎使用
	 */
	@ModelAttribute("orderInfo")
	public void getForUpdate(@RequestParam(value = "updId", required = false) Long updId, Model model) {
		if (null != updId) {
			model.addAttribute("orderInfo", orderInfoService.get(updId));
		} else {
			model.addAttribute("orderInfo", new OrderInfo());
		}
	}
	
	/**
	 * 取出客户信息
	 * @return
	 */
	@RequestMapping(value = "/selectByPartnerId")
    @ResponseBody
    public Map<String, Object> selectByPartnerId() {
	    // 合作伙伴ID
	    ShiroUser staff = ShiroKit.getUser();
        Map<String, Object> map = new HashMap<String, Object>();
        boolean isMarketing = false;
        // 是否允许下发计费
        boolean isBillIssued = false;
        // TODO
        // 云之讯管理员
//        if (!isAdmin()) {
//            // 合作伙伴
//            PartnerInfo partnerInfo = partnerInfoService.getByAccount(staff.getLoginName());
//            if (partnerInfo == null) {
//                map.put("errMsg", "合作伙伴不存在");
//                return map;
//            }
//            // 流量营销合作伙伴
//            if (partnerInfo.getPartnerType() == PartnerInfo.PARTNER_TYPE_FLOW) {
//                isMarketing = true;
//            }
//            // 当前合作伙伴是否允许下发计费
//            if (Constant.ORDER_BILLING_TYPE_ISSUED == partnerInfo.getOrderBillingType()) {
//                isBillIssued = true;
//            }
//        }
//        map.put("isWYAdmin", isAdmin());
        map.put("isWYAdmin", true);
        map.put("isMarketing", isMarketing);
        map.put("isBillIssued", isBillIssued);
        return map;
    }
	
	/**
	 * 取出订单总数
	 * @return
	 */
	@RequestMapping(value = "/getOrderNumber.ajax")
	@ResponseBody
	public String getOrderNumber() {
	    // 取出订单总数
//	    long orderCount = orderInfoService.getOrderInfoCount();
//	    String timeStr = new SimpleDateFormat("yyyyMMdd").format(new Date());
//	    String orderNumber = timeStr + orderCount;
	    String orderNo = OrderSeqGen.createApplyId();
	    return orderNo;
	}
	
    /**
     * 修改订单状态
     * @return
     */
    @RequestMapping(value = "/changeStatus")
    @ResponseBody
    public Object changeStatus(Integer status, Long orderId) {
    	
    	OrderInfo data = null;
    	
    	if (status < 1 || status > 4 || (data = orderInfoService.get(orderId)) == null ) {
            return ErrorTip.buildErrorTip(BizExceptionEnum.REQUEST_NULL);
        }
    	
    	ShiroUser staff = ShiroKit.getUser();
        data.setStatus(status);
        data.setUpdator(staff.getAccount());
        data.setUpdateTime(new Date());
        
        orderInfoService.saveAndUpdate(data);
        
        return SUCCESS_TIP;
    }
    
    /**
     * 导出订单
     * @return
     */
    @RequestMapping(value = "/downLoadOrder")
    @ResponseBody
    public void downLoadOrder(Page<OrderInfo> page) {
    	// TODO
//        if (isAdmin()) {
            page.getParams().put("partnerId", null);
//        } else {
//            Staff staff = getCurrentLogin();
//            PartnerInfo partnerInfo = partnerInfoService.getByAccount(staff.getLoginName());
//            if (partnerInfo == null) {
//                throw new MyException("合作伙伴不存在");
//            }
//            page.getParams().put("partnerId", partnerInfo.getPartnerId());
//        }
        page.setRows(10000);
        Page<OrderInfo> list = orderInfoService.pageQuery(page);
        // 构造excel用字段
        for (OrderInfo orderInfo : list.getDatas()) {
            List<OrderDetail> orderDetails = orderDetailService.getODAndFPIByOrderId(orderInfo.getOrderId());
            orderInfo.setOrderDetailList(orderDetails);
        }
        // 生成Excel
        Map<String, Object> beanParams = new HashMap<String, Object>();
        beanParams.put("orderInfoList", list.getDatas());
        
        try {
            TemplateExcelManager.getInstance().createExcelFileAndDownload(TemplateExcel.ORDER_LIST, beanParams);
        } catch (Exception e) {
            LOG.error("导出报表出错！！！【"+ e.getMessage() +"】", e);
            throw new BussinessException(BizExceptionEnum.CUSTOMER_FORMAT_ERROR, "导出报表出错！！！【" + e.getMessage() + "】");
        }
    }

    /**
     * 导出指定订单
     * @return
     */
    @RequestMapping(value = "/downLoadOrderByOrderId")
    @ResponseBody
    public void downLoadOrderByOrderId(Long orderId) {
    	if ( orderId == null || orderId.compareTo(0L) <= 0 ) {
    		throw new BussinessException(BizExceptionEnum.CUSTOMER_FORMAT_ERROR, "参数错误");
    	}
        List<OrderInfo> orderInfos = new ArrayList<OrderInfo>();
        OrderInfo orderInfo = orderInfoService.get(orderId);
        // 构造excel用字段
        List<OrderDetail> orderDetails = orderDetailService.getODAndFPIByOrderId(orderInfo.getOrderId());
        orderInfo.setOrderDetailList(orderDetails);
        orderInfos.add(orderInfo);
        // 生成Excel
        Map<String, Object> beanParams = new HashMap<String, Object>();
        beanParams.put("orderInfoList", orderInfos);
        
        try {
            TemplateExcelManager.getInstance().createExcelFileAndDownload(TemplateExcel.ORDER_INFO, beanParams);
        } catch (Exception e) {
            LOG.error("导出报表出错！！！【"+ e.getMessage() +"】", e);
            throw new BussinessException(BizExceptionEnum.CUSTOMER_FORMAT_ERROR, "导出报表出错！！！【" + e.getMessage() + "】");
        }
    }
    
    @RequestMapping(value = "/queryOrderInfoByCustomer")
    @ResponseBody
    public Object queryOrderInfoByCustomer(String customerName,Integer partnerType,Integer orderType) {
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("customerName", customerName);
        map.put("partnerType", partnerType);
        map.put("orderType", orderType);
        List<OrderInfo> list = orderInfoService.queryOrderInfoByCustomer(map);
        return list;
    }
    
    /**
     * 校验当前客户是否允许下发计费
     * @return
     */
    @RequestMapping(value = "/checkAllowBillIssued")
    @ResponseBody
    public Object checkAllowBillIssued(Long customerId) {
        Map<String, Object> map = new HashMap<String, Object>();
        // 是否允许下发计费
        boolean isBillIssued = false;
        CustomerInfo customerInfo = customerInfoDao.getCustomerInfoByCustomerId(customerId);
        if (customerInfo == null) {
            return ErrorTip.buildErrorTip("非法客户");
        }
        PartnerInfo partnerInfo = partnerService.get(customerInfo.getPartnerId());
        if (partnerInfo == null) {
            return ErrorTip.buildErrorTip("未找到当前客户的合作伙伴");
        }
        // 当前合作伙伴是否允许下发计费
        if (Constant.ORDER_BILLING_TYPE_ISSUED == partnerInfo.getOrderBillingType()) {
            isBillIssued = true;
        }
        map.put("isBillIssued", isBillIssued);
        return SUCCESS_TIP;
    }

    /**
     * 取出客户信息
     * @return
     */
    @RequestMapping(value = "/selectCustomerInfoByName")
    @ResponseBody
    public Map<String, Object> selectCustomerInfoByName(String customerName) {
        // 合作伙伴ID
        ShiroUser staff = ShiroKit.getUser();
        Map<String, Object> map= new HashMap<String, Object>();
        List<CustomerInfo> list = new ArrayList<CustomerInfo>();
        
        if (StringUtils.isEmpty(customerName)) {
            map.put("customerList", list);
            return map;
        }
        // TODO 
        // 云之讯管理员
//        if (isAdmin()) {
            list = orderInfoService.selectCustomerInfoByName(customerName, null);
//        } else {
//            // 合作伙伴
//            PartnerInfo partnerInfo = partnerInfoService.getByAccount(staff.getLoginName());
//            if (partnerInfo == null) {
//                return fail("合作伙伴不存在");
//            }
//            list = orderInfoService.selectCustomerInfoByName(customerName, partnerInfo.getPartnerId());
//        }
        map.put("customerList", list);
        return map;
    }
    
    
//    /**
//     * 取出订单价格信息
//     * @return
//     */
//    @RequestMapping(value = "/selectDirectChargeProdOrder.ajax")
//    @ResponseBody
//    public Map<String, Object> selectDirectChargeProdOrder() {
//        Staff staff = getCurrentLogin();
//        // 云之讯管理员
//        Long partnerId = null;
    /**
     * 取出合作伙伴信息
     * @return
     */
    @RequestMapping(value = "/selectPartnerInfoByName")
    @ResponseBody
    public Object selectPartnerInfoByName(String partnerName) {
        // 合作伙伴ID
        ShiroUser staff = ShiroKit.getUser();
        Map<String, Object> map= new HashMap<String, Object>();
        List<PartnerInfo> list = new ArrayList<PartnerInfo>();
        
        // 云之讯管理员
        // TODO 
//        if (isAdmin()) {
            list = partnerService.selectPartnerInfoByName(partnerName, null);
//        } else {
//            // 合作伙伴
//            PartnerInfo partnerInfo = partnerInfoService.getByAccount(staff.getLoginName());
//            if (partnerInfo == null) {
//                return fail("合作伙伴不存在");
//            }
//            list = partnerInfoService.selectPartnerInfoByName(partnerName, partnerInfo.getPartnerId());
//        }
        map.put("partnerList", list);
        return map;
    }
    
    /**
     * 取出订单价格信息
     * @return
     */
    @RequestMapping(value = "/selectDirectChargeProdOrder")
    @ResponseBody
    public Object selectDirectChargeProdOrder() {
//        ShiroUser staff = ShiroKit.getUser();
        // 云之讯管理员
        Long partnerId = null;
        // TODO 
//        if (!isAdmin()) {
//            // 合作伙伴
//            PartnerInfo partnerInfo = partnerInfoService.getByAccount(staff.getLoginName());
//            if (partnerInfo == null) {
//                return fail("合作伙伴不存在");
//            }
//            partnerId = partnerInfo.getPartnerId();
//        }
        List<OrderDetail> list = orderDetailService.selectDistinctDirectChargeProdOrder(partnerId);
        return list;
    }
    
    /**
     * 修改订单价格信息
     * @return
     */
    @RequestMapping(value = "/updateODByProdId.ajax")
    @ResponseBody
    public Object updateODByProdId(Long productId, BigDecimal price, BigDecimal settlementPrice) {
        if (productId == null || productId < 0) {
            return ErrorTip.buildErrorTip("非法操作");
        }
        int finalPrice = price.compareTo(new BigDecimal(0));
        int finalSettlementPrice = settlementPrice.compareTo(new BigDecimal(0));
        if (finalPrice < 0 || finalSettlementPrice < 0) {
            return ErrorTip.buildErrorTip("请输入合法的价格");
        }
        ShiroUser staff = ShiroKit.getUser();
        List<OrderDetail> orderDetails = new ArrayList<OrderDetail>();
        // 云之讯管理员
        Long partnerId = null;
        // TODO 
//        if (isAdmin()) {
            orderDetails = orderDetailService.selectDirectChargeProdOrder(productId, null);
//        } else {
//            // 合作伙伴
//            PartnerInfo partnerInfo = partnerInfoService.getByAccount(staff.getLoginName());
//            if (partnerInfo == null) {
//                return fail("合作伙伴不存在");
//            }
//            partnerId = partnerInfo.getPartnerId();
//            orderDetails = orderDetailService.selectDirectChargeProdOrder(productId, partnerInfo.getPartnerId());
//        }
        // 校验价格是否倒挂
        // 客户订单优化处理-客户订单产品价格不能低于合作伙伴订单价格
        List<FlowProductInfo> listAll = flowProductInfoService.getByPartnerInfoType(partnerId, null, null, null, null);
        for (FlowProductInfo fpiDB : listAll) {
            if (productId.longValue() == fpiDB.getProductId().longValue() && price.compareTo(fpiDB.getSettlementAmount()) == -1) {
                return ErrorTip.buildErrorTip("产品名称为【" + fpiDB.getProductName() + "】的结算价格(" + price + "元)小于当前合作伙伴的结算价格("+ fpiDB.getSettlementAmount() +"元), 请重新输入。");
            }
        }
        
        // 更新订单详情中的价格
        for (OrderDetail od : orderDetails) {
            od.setPrice(price);
            od.setSettlementPrice(settlementPrice);
            od.setPriceTotal(price.multiply(new BigDecimal(od.getAmount())));
            orderDetailService.update(od);
        }
        FlowProductInfo flowProductInfo = flowProductInfoService.getFlowProductInfoByProductId(productId);
        LOG.error("[{}]修改了产品ID为[{}]产品名称为[{}]的价格信息。结算价格修改为[{}]，销售价格修改为[{}]", staff.getAccount(), productId,
                flowProductInfo.getProductName(), price, settlementPrice);
        // 更新订单主表的订单总价
        List<OrderDetail> orderDetailList = new ArrayList<OrderDetail>();
        for (OrderDetail od2 : orderDetails) {
            orderDetailList = orderDetailService.getByOrderId(od2.getOrderId());
            BigDecimal orderPriceTotal = new BigDecimal(0);
            // 计算订单总价
            for (OrderDetail orderDetail : orderDetailList) {
            	orderPriceTotal=orderPriceTotal.add(orderDetail.getPriceTotal());
            }
            OrderInfo orderInfo = orderInfoService.get(od2.getOrderId());
            orderInfo.setPriceTotal(orderPriceTotal);
            orderInfo.setUpdator(staff.getAccount());
            orderInfo.setUpdateTime(new Date());
            orderInfoService.update(orderInfo);
        }
        return SUCCESS_TIP;
    }
    
    /**
     * 批量修改订单价格信息
     * @return
     */
    @RequestMapping(value = "/updateODPriceAll", method = RequestMethod.POST)
    @ResponseBody
    public Object updateODPriceAll(OrderInfo orderInfo) {
        ShiroUser staff = ShiroKit.getUser();
        if (orderInfo == null) {
            return ErrorTip.buildErrorTip("非法操作");
        }
        for (OrderDetail odFront : orderInfo.getOrderDetailList()) {
            if (odFront.getPrice().compareTo(new BigDecimal(0)) ==-1 || odFront.getSettlementPrice().compareTo(new BigDecimal(0))==-1) {
                return ErrorTip.buildErrorTip("请输入合法的价格");
            }
            // 取出要更新价格的订单详情对象
            List<OrderDetail> orderDetails = new ArrayList<OrderDetail>();
            // 云之讯管理员
            Long partnerId = null;
            // TODO 
//            if (isAdmin()) {
                orderDetails = orderDetailService.selectDirectChargeProdOrder(odFront.getProductId(), null);
//            } else {
//                // 合作伙伴
//                PartnerInfo partnerInfo = partnerInfoService.getByAccount(staff.getLoginName());
//                if (partnerInfo == null) {
//                    return fail("合作伙伴不存在");
//                }
//                partnerId = partnerInfo.getPartnerId();
//                orderDetails = orderDetailService.selectDirectChargeProdOrder(odFront.getProductId(), partnerInfo.getPartnerId());
//            }
            // 校验价格是否倒挂
            // 客户订单优化处理-客户订单产品价格不能低于合作伙伴订单价格
            List<FlowProductInfo> listAll = flowProductInfoService.getByPartnerInfoType(partnerId, null, null,null,null);
            for (FlowProductInfo fpiDB : listAll) {
                if (odFront.getProductId().longValue() == fpiDB.getProductId().longValue() && odFront.getPrice().compareTo(fpiDB.getSettlementAmount()) == -1) {
                    return ErrorTip.buildErrorTip("产品名称为【" + fpiDB.getProductName() + "】的结算价格(" + odFront.getPrice() + "元)小于当前合作伙伴的结算价格("+ fpiDB.getSettlementAmount() +"元), 请重新输入。");
                }
            }
            
            // 更新订单详情中的价格
            for (OrderDetail od : orderDetails) {
                od.setPrice(odFront.getPrice());
                od.setSettlementPrice(odFront.getSettlementPrice());
                od.setPriceTotal(odFront.getPrice().multiply(new BigDecimal(od.getAmount())));
                orderDetailService.update(od);
            }
            FlowProductInfo flowProductInfo = flowProductInfoService.getFlowProductInfoByProductId(odFront.getProductId());
            LOG.error("[{}]修改了产品ID为[{}]产品名称为[{}]的价格信息。结算价格修改为[{}]，销售价格修改为[{}]", staff.getAccount(), odFront.getProductId(),
                    flowProductInfo.getProductName(), odFront.getPrice(), odFront.getSettlementPrice());
            // 更新订单主表的订单总价
            List<OrderDetail> orderDetailList = new ArrayList<OrderDetail>();
            for (OrderDetail od2 : orderDetails) {
                orderDetailList = orderDetailService.getByOrderId(od2.getOrderId());
                BigDecimal orderPriceTotal = new BigDecimal(0);
                // 计算订单总价
                for (OrderDetail orderDetail : orderDetailList) {
                    orderPriceTotal=orderPriceTotal.add(orderDetail.getPriceTotal());
                }
                OrderInfo orderInfoData = orderInfoService.get(od2.getOrderId());
                orderInfoData.setPriceTotal(orderPriceTotal);
                orderInfoData.setUpdator(staff.getAccount());
                orderInfoData.setUpdateTime(new Date());
                orderInfoService.update(orderInfoData);
            }
        }
        return SUCCESS_TIP;
    }
    
    @RequestMapping(value = "/queryByCustomerId.ajax")
	@ResponseBody
	public Object queryByCustomerId(Long customerId) {
	    if(null == customerId){
	    	return ErrorTip.buildErrorTip(BizExceptionEnum.REQUEST_NULL);
	    }
	    Page<OrderInfo> page = new Page<>();
	    Map<String, Object> map = new HashMap<String,Object>();
	    map.put("customerId", customerId);
	    page.setParams(map);
		Page<OrderInfo> list = orderInfoService.pageQuery(page);
		return new PageInfoBT<OrderInfo>(list.getDatas(), list.getTotal());
	}
}