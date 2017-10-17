package com.yzx.flow.modular.customer.controller;

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
import com.yzx.flow.common.persistence.model.AreaCode;
import com.yzx.flow.common.persistence.model.BillsDetail;
import com.yzx.flow.common.persistence.model.BillsExcel;
import com.yzx.flow.common.persistence.model.CustomerBalanceDay;
import com.yzx.flow.common.persistence.model.CustomerBalanceMonth;
import com.yzx.flow.common.persistence.model.CustomerInfo;
import com.yzx.flow.common.util.Constant;
import com.yzx.flow.common.util.DateUtil;
import com.yzx.flow.core.portal.PortalParamSetter;
import com.yzx.flow.core.portal.annotation.AutoSetPortalCustomer;
import com.yzx.flow.core.portal.annotation.PortalParamMeta;
import com.yzx.flow.core.shiro.ShiroKit;
import com.yzx.flow.core.shiro.ShiroUser;
import com.yzx.flow.modular.customer.service.IAreaCodeService;
import com.yzx.flow.modular.customer.service.ICustomerBalanceDayService;
import com.yzx.flow.modular.customer.service.ICustomerBalanceMonthService;
import com.yzx.flow.modular.customer.service.ICustomerInfoService;
import com.yzx.flow.modular.partner.service.IPartnerService;

/**
 * 
 * <b>Title：</b>CustomerBalanceMonthController.java<br/>
 * <b>Description：</b> <br/>
 * <b>@author： </b>v5480<br/>
 * <b>@date：</b>2015-09-02 10:17:41<br/>
 * <b>Copyright (c) 2015 szwisdom Tech.</b>
 * 
 */
@Controller
@RequestMapping("/customerBalanceMonth")
public class CustomerBalanceMonthController extends BaseController {
	
	private static final Logger LOG = LoggerFactory.getLogger(CustomerBalanceMonthController.class);
	
	@Autowired
	private ICustomerBalanceMonthService customerBalanceMonthService;

	@Autowired
	private IPartnerService partnerService;

	@Autowired
	private ICustomerInfoService customerInfoService;

	@Autowired
	private ICustomerBalanceDayService customerBalanceDayService;

	@Autowired
	private IAreaCodeService areaCodeService;
	
	
	/**
	 *  跳转至 客户 余额账单
	 * @return
	 */
	@RequestMapping(value="customerSettlementOrder", method = RequestMethod.GET)
	public String toCustomerSettlementOrder() {
		return String.format("%s%s", "/customerBill/", "customerSettlementOrder_list.html");
	}
	
	
	@RequestMapping(value = "/query")
	@ResponseBody
	@AutoSetPortalCustomer({ @PortalParamMeta(setter = PortalParamSetter.PAGE)})
	public Object pageQuery(Page<CustomerBalanceMonth> page) {
		// TODO 
//	    Staff staff = getCurrentLogin();
//        if (!isAdmin()) {
//            // 合作伙伴
//            PartnerInfo partnerInfo = partnerInfoService.getByAccount(staff.getLoginName());
//            if (partnerInfo == null) {
//                LOG.error("合作伙伴不存在");
//                return fail("合作伙伴不存在");
//            }
//            page.getParams().put("partnerId", partnerInfo.getPartnerId());
//        }
		if ( StringUtils.isBlank(page.getSort()) ) {
			page.setSort("month");// 结算月份
		}
		Page<CustomerBalanceMonth> list = customerBalanceMonthService.pageQuery(page);
		return new PageInfoBT<>(list.getDatas(), list.getTotal());
	}

//	@RequestMapping(value = "/add.ajax", method = RequestMethod.POST)
//	@ResponseBody
//	public Map<String, Object> add(@ModelAttribute("customerBalanceMonth") CustomerBalanceMonth data) {
//		customerBalanceMonthService.saveAndUpdate(data);
//		return super.success("新增成功");
//	}
//
//	@RequestMapping(value = "/delete.ajax")
//	@ResponseBody
//	public Map<String, Object> delete(Long balanceMonthId) {
//		customerBalanceMonthService.delete(balanceMonthId);
//		return super.success("删除成功");
//	}
//
//	@RequestMapping(value = "/get.ajax")
//	@ResponseBody
//	public Map<String, Object> get(Long balanceMonthId) {
//		CustomerBalanceMonth data = customerBalanceMonthService.get(balanceMonthId);
//		return super.success(data);
//	}

	/**
	 * 更新的时候需额外传递updId,值跟主键值一样,被@ModelAttribute注释的方法会在此controller每个方法执行前被执行，
	 * 要谨慎使用
	 */
	@ModelAttribute("customerBalanceMonth")
	public void getForUpdate(@RequestParam(value = "updId", required = false) Long updId, Model model) {
		if (null != updId) {
			model.addAttribute("customerBalanceMonth", customerBalanceMonthService.get(updId));
		} else {
			model.addAttribute("customerBalanceMonth", new CustomerBalanceMonth());
		}
	}
	
	/**
     * 修改结算状态
     * @return
     */
    @RequestMapping(value = "/changeStatus.ajax")
    @ResponseBody
    public Object changeStatus(Integer status, Long balanceMonthId) {
        ShiroUser staff = ShiroKit.getUser();
        
        // 更新customer_info表
        CustomerBalanceMonth data = customerBalanceMonthService.get(balanceMonthId);
        CustomerInfo customerInfo = customerInfoService.get(data.getCustomerId());
        customerInfo.setBalance(customerInfo.getBalance().subtract(data.getAdjustMoney()));
        customerInfo.setUpdator(staff.getAccount());
        customerInfo.setUpdateTime(new Date());
        customerInfoService.update(customerInfo);
        
        if (status < 0 || status > 1) {
            return ErrorTip.buildErrorTip(BizExceptionEnum.REQUEST_NULL);
        }
        data.setBalanceStatus(status);
        data.setUpdator(staff.getAccount());
        data.setUpdateTime(new Date());
        customerBalanceMonthService.saveAndUpdate(data);
        return SUCCESS_TIP;
    }
    
    /**
     * 调整结算金额
     * @return
     */
    @RequestMapping(value = "/changeAdjustBalance")
    @ResponseBody
    public Object changeAdjustBalance(Long balanceMonthId, BigDecimal changeBalance, String changeReason) {
        ShiroUser staff = ShiroKit.getUser();
        // 更新月结算明细表
        CustomerBalanceMonth data = customerBalanceMonthService.get(balanceMonthId);
        data.setAdjustMoney(changeBalance);
        data.setRemark(changeReason);
        data.setBalanceStatus(Constant.BALANCE_STATUS_CONFIRM);
        data.setBalanceMoney(data.getBalanceMoney().add(changeBalance));
        data.setUpdator(staff.getAccount());
        data.setUpdateTime(new Date());
        customerBalanceMonthService.saveCustomerBalanceMonth(data, staff);
        return SUCCESS_TIP;
    }
    
    /**
     * 下载客户月账单
     * @param response
     */
    @RequestMapping(value = "/downLoadSettlementOrder")
    @ResponseBody
    @AutoSetPortalCustomer({ @PortalParamMeta(setter = PortalParamSetter.PAGE)})
    public void downLoadCustomerBill(Page<CustomerBalanceDay> page, Long balanceMonthId) {
        CustomerBalanceMonth data = customerBalanceMonthService.get(balanceMonthId);
        CustomerInfo customerInfo = customerInfoService.get(data.getCustomerId());
        String startDateStr = DateUtil.formatDate(data.getStartDate());
        String endDateStr = DateUtil.formatDate(data.getEndDate());
        page.getParams().put("partnerId", data.getPartnerId());
        page.getParams().put("customerId", data.getCustomerId());
        page.getParams().put("inputStartTime", startDateStr);
        page.getParams().put("inputEndTime", endDateStr);
        page.setRows(50000);
        // 构造数据结构 START
        List<BillsExcel> billsExcels = new ArrayList<BillsExcel>();
        BillsExcel billsExcel = new BillsExcel();
        List<BillsDetail> billsDetails = new ArrayList<BillsDetail>();
        // 取得账单金额
        BigDecimal payMoney = data.getPayMoney();
        // 取得调整金额
        BigDecimal adjustMoney = data.getAdjustMoney();
        // 取得应结算金额
        BigDecimal balanceMoney = data.getBalanceMoney();
        // 取得所有{YD/LT/DX}+{全国/广东/河南...}组合的数据
        billsDetails = genBillsDetailList(data.getPartnerId(), data.getCustomerId(), page, Constant.OBJ_TYPE_CUSTOMER);
        
        billsExcel.setCustomerInfo(customerInfo);
        billsExcel.setPayMoney(payMoney);
        billsExcel.setAdjustMoney(adjustMoney);
        billsExcel.setBalanceMoney(balanceMoney);
        billsExcel.setInputStartTime(startDateStr);
        billsExcel.setInputEndTime(endDateStr);
        billsExcel.setBillsDetails(billsDetails);
        billsExcels.add(billsExcel);
        
        
        Map<String, Object> beanParams = new HashMap<String, Object>();
        beanParams.put("cbdList", billsExcels);
        try {
            TemplateExcelManager.getInstance().createExcelFileAndDownload(TemplateExcel.CUSTOMER_BALANCE_MONTH, beanParams);
        } catch (Exception e) {
            LOG.error("导出报表出错！！！【"+ e.getMessage() +"】", e);
            throw new BussinessException(BizExceptionEnum.CUSTOMER_FORMAT_ERROR, "导出报表出错！！！【" + e.getMessage() + "】");
        }
    }
    
    public List<BillsDetail> genBillsDetailList(Long partnerId, Long customerId, Page<CustomerBalanceDay> page, String objType) {
        List<BillsDetail> billsDetails = new ArrayList<BillsDetail>();
        String inputStartTime = String.valueOf(page.getParams().get("inputStartTime"));
        String inputEndTime = String.valueOf(page.getParams().get("inputEndTime"));
        // 遍历运营商
        for (String key : Constant.MOBILE_OPERATOR_MAP.keySet()) {
            List<AreaCode> acList = areaCodeService.selectAll(key, partnerId, customerId, inputStartTime, inputEndTime);
            // 遍历区域
            for (AreaCode areaCode : acList) {
                if (areaCode != null) {
                    BillsDetail bd = new BillsDetail();
                    List<CustomerBalanceDay> customerBalanceDays = new ArrayList<CustomerBalanceDay>();
                    BigDecimal cbdBalance = new BigDecimal(0);
                    page.getParams().put("mobileOperator", key);
                    page.getParams().put("zone", areaCode.getAreaCode());
                    if (Constant.OBJ_TYPE_PARTNER.equals(objType)) {
                        customerBalanceDays = customerBalanceDayService.pageQueryPartnerMonthByPartner(page).getDatas();
                    } else if (Constant.OBJ_TYPE_CUSTOMER.equals(objType)) {
                        customerBalanceDays = customerBalanceDayService.pageQueryPartnerMonth(page).getDatas();
                    }
                    cbdBalance = getBalanceTotal(customerBalanceDays, objType);
                    bd.setOperatorCN(Constant.MOBILE_OPERATOR_MAP.get(key));
                    bd.setZoneName(areaCode.getAreaName());
                    bd.setCbdList(customerBalanceDays);
                    bd.setCbdBalance(cbdBalance);
                    billsDetails.add(bd);
                }
            }
        }
        return billsDetails;
    }
    
    public BigDecimal getBalanceTotal(List<CustomerBalanceDay> list, String objType) {
        BigDecimal priceTotal = new BigDecimal(0);
        for (CustomerBalanceDay cbd : list) {
            if (Constant.OBJ_TYPE_PARTNER.equals(objType) && cbd.getPartnerAmount() != null) {
                priceTotal = priceTotal.add(cbd.getPartnerAmount());
            } else if (Constant.OBJ_TYPE_CUSTOMER.equals(objType) && cbd.getCustomerAmount() != null) {
                priceTotal = priceTotal.add(cbd.getCustomerAmount());
            }
        }
        return priceTotal;
    }
}