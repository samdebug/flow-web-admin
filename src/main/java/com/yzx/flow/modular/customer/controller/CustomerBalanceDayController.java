package com.yzx.flow.modular.customer.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

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
import com.yzx.flow.common.persistence.model.CustomerInfo;
import com.yzx.flow.common.persistence.model.PartnerInfo;
import com.yzx.flow.common.util.Constant;
import com.yzx.flow.core.portal.PortalParamSetter;
import com.yzx.flow.core.portal.annotation.AutoSetPortalCustomer;
import com.yzx.flow.core.portal.annotation.PortalParamMeta;
import com.yzx.flow.modular.customer.service.IAreaCodeService;
import com.yzx.flow.modular.customer.service.ICustomerBalanceDayService;
import com.yzx.flow.modular.customer.service.ICustomerInfoService;
import com.yzx.flow.modular.partner.service.IPartnerService;

/**
 * 
 * <b>Title：</b>CustomerBalanceDayController.java<br/>
 * <b>Description：</b> <br/>
 * <b>@author： </b>v5480<br/>
 * <b>@date：</b>2015-09-02 10:17:41<br/>
 * <b>Copyright (c) 2015 szwisdom Tech.</b>
 * 
 */
@Controller
@RequestMapping("/customerBalanceDay")
public class CustomerBalanceDayController extends BaseController {
	
	
	private static final Logger LOG = LoggerFactory.getLogger(CustomerBalanceDayController.class);
	
	
	@Autowired
	private ICustomerBalanceDayService customerBalanceDayService;

	@Autowired
	private IPartnerService partnerService;

	@Autowired
	private ICustomerInfoService customerInfoService;

	@Autowired
	private IAreaCodeService areaCodeService;
	
	
	
	/**
	 * 跳转至 客户日账单页面
	 * @return
	 */
	@RequestMapping(value = "/customerBillQuery", method = RequestMethod.GET)
	public String toCustomerBillQuery() {
		return String.format("%s%s", "/customerBill/", "customerBillQuery_view.html");
	}
	
	
	
    /**
     * 合作伙伴日账单查询
     * @param page
     * @return
     */
	@RequestMapping(value = "/queryPartner")
	@ResponseBody
	@AutoSetPortalCustomer({ @PortalParamMeta(setter = PortalParamSetter.PAGE)})
	public Object pageQueryPartner(Page<CustomerBalanceDay> page) {
		// TODO 
//	    Staff staff = getCurrentLogin();
//	    if (!isAdmin()) {
//	        // 合作伙伴
//	        PartnerInfo partnerInfo = partnerService.getByAccount(staff.getLoginName());
//	        if (partnerInfo == null) {
//	            LOG.error("合作伙伴不存在");
//	            return fail("合作伙伴不存在");
//	        }
//	        page.getParams().put("partnerId", partnerInfo.getPartnerId());
//	    }
		if ( StringUtils.isBlank(page.getSort()) ) {
			page.setSort("balance_day");
		}
	    Page<CustomerBalanceDay> list = customerBalanceDayService.pageQueryPartner(page);
	    return new PageInfoBT<CustomerBalanceDay>(list.getDatas(), list.getTotal());
	}
	
	@RequestMapping(value = "/query")
	@ResponseBody
	@AutoSetPortalCustomer({ @PortalParamMeta(setter = PortalParamSetter.PAGE)})
	public Object pageQuery(Page<CustomerBalanceDay> page) {
		// TODO 
//	    Staff staff = getCurrentLogin();
//        if (!isAdmin()) {
//            // 合作伙伴
//            PartnerInfo partnerInfo = partnerService.getByAccount(staff.getLoginName());
//            if (partnerInfo == null) {
//                LOG.error("合作伙伴不存在");
//                return fail("合作伙伴不存在");
//            }
//            page.getParams().put("partnerId", partnerInfo.getPartnerId());
//        }
		if ( StringUtils.isBlank(page.getSort()) ) {
			page.setSort("balance_day");
		}
		Page<CustomerBalanceDay> list = customerBalanceDayService.pageQuery(page);
		return new PageInfoBT<>(list.getDatas(), list.getTotal());
	}

	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ResponseBody
	public Object add(@ModelAttribute("customerBalanceDay") CustomerBalanceDay data) {
		customerBalanceDayService.saveAndUpdate(data);
		return SUCCESS_TIP;
	}

	@RequestMapping(value = "/delete")
	@ResponseBody
	public Object delete(Long balanceDayId) {
		customerBalanceDayService.delete(balanceDayId);
		return SUCCESS_TIP;
	}

	@RequestMapping(value = "/get")
	@ResponseBody
	public Object get(Long balanceDayId) {
		CustomerBalanceDay data = customerBalanceDayService.get(balanceDayId);
		return data;
	}

	/**
	 * 更新的时候需额外传递updId,值跟主键值一样,被@ModelAttribute注释的方法会在此controller每个方法执行前被执行，
	 * 要谨慎使用
	 */
	@ModelAttribute("customerBalanceDay")
	public void getForUpdate(@RequestParam(value = "updId", required = false) Long updId, Model model) {
		if (null != updId) {
			model.addAttribute("customerBalanceDay", customerBalanceDayService.get(updId));
		} else {
			model.addAttribute("customerBalanceDay", new CustomerBalanceDay());
		}
	}
	
	@RequestMapping(value = "/initPartnerCountAndPriceTotal", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> initPartnerCountAndPriceTotal( CustomerBalanceDay info) {
        
		Map<String, Object> map = new HashMap<String, Object>();
        List<CustomerBalanceDay> list = new ArrayList<CustomerBalanceDay>();
        // TODO
//        Staff staff = getCurrentLogin();
//        
//        if (!isAdmin()) {
//            // 合作伙伴
//            PartnerInfo partnerInfo = partnerService.getByAccount(staff.getLoginName());
//            if (partnerInfo == null) {
//                map.put("errMsg", "合作伙伴不存在");
//                return map;
//            }
//            info.setPartnerId(partnerInfo.getPartnerId());
//        }
        list = customerBalanceDayService.getPartnerCBDUsedByStatistics(info);

        Integer count = 0;
        BigDecimal priceTotal = new BigDecimal(0);
        for (CustomerBalanceDay cbd : list) {
            if (cbd.getSendNum() != null && cbd.getPartnerAmount() != null) {
                count += cbd.getSendNum();
                priceTotal = priceTotal.add(cbd.getPartnerAmount());
            }
        }
        map.put("count", count);
        map.put("priceTotal", priceTotal.setScale(2, BigDecimal.ROUND_HALF_UP));
        return map;
    }
	
	@RequestMapping(value = "/initCustomerCountAndPriceTotal", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> initCustomerCountAndPriceTotal(CustomerBalanceDay info) {
	    
		Map<String, Object> map = new HashMap<String, Object>();
	    List<CustomerBalanceDay> list = new ArrayList<CustomerBalanceDay>();
	    // TODO 
//	    Staff staff = getCurrentLogin();
//	    
//	    if (!isAdmin()) {
//	        // 合作伙伴
//	        PartnerInfo partnerInfo = partnerService.getByAccount(staff.getLoginName());
//	        if (partnerInfo == null) {
//	            map.put("errMsg", "合作伙伴不存在");
//	            return map;
//	        }
//	        info.setPartnerId(partnerInfo.getPartnerId());
//	    }
	    list = customerBalanceDayService.getCBDUsedByStatistics(info);
	    
	    Integer count = 0;
	    BigDecimal priceTotal = new BigDecimal(0);
	    for (CustomerBalanceDay cbd : list) {
	        if (cbd.getSendNum() != null && cbd.getCustomerAmount() != null) {
	            count += cbd.getSendNum();
	            priceTotal = priceTotal.add(cbd.getCustomerAmount());
	        }
	    }
	    map.put("count", count);
	    map.put("priceTotal", priceTotal);
	    return map;
	}
	
	/**
	 * 下载合作伙伴日账单
	 * @param response
	 */
	@RequestMapping(value = "/downLoadPartnerBill")
	@ResponseBody
	public void downLoadPartnerBill(HttpServletResponse response, Page<CustomerBalanceDay> page) {
//	    // 构造page对象参数
	    createPageParams(page);
	    boolean isPartnerSelected = false;
        isPartnerSelected = page.getParams() != null &&
                    (page.getParams().get("partnerId") != null &&
                            StringUtils.isNotEmpty(page.getParams().get("partnerId").toString()));
        // 构造数据结构 START
        List<BillsExcel> billsExcels = new ArrayList<BillsExcel>();
        String inputStartTime = String.valueOf(page.getParams().get("inputStartTime"));
        String inputEndTime = String.valueOf(page.getParams().get("inputEndTime"));
	    // 导出时未选择合作伙伴
        if (!isPartnerSelected) {
            // 取得有日账单的合作伙伴
            List<CustomerBalanceDay> partnerCBDList = customerBalanceDayService.selectPartnerCBD();
            for (CustomerBalanceDay customerBalanceDay : partnerCBDList) {
                BillsExcel billsExcel = new BillsExcel();
                billsExcel = genBillsExcel(page, customerBalanceDay.getPartnerId(), inputStartTime, inputEndTime, Constant.OBJ_TYPE_PARTNER);
                billsExcels.add(billsExcel);
            }
        } else {
        	Long pagePartnerId = Long.valueOf(String.valueOf(page.getParams().get("partnerId")));
            BillsExcel billsExcel = new BillsExcel();
            billsExcel = genBillsExcel(page, pagePartnerId, inputStartTime, inputEndTime, Constant.OBJ_TYPE_PARTNER);
            billsExcels.add(billsExcel);
        }
	    
	    
        Map<String, Object> beanParams = new HashMap<String, Object>();
        beanParams.put("pbdList", billsExcels);
        try {
            TemplateExcelManager.getInstance().createExcelFileAndDownload(TemplateExcel.PARTNER_BALANCE_DAY, beanParams);
        } catch (Exception e) {
            LOG.error("导出报表出错！！！【" + e.getMessage() + "】", e);
            throw new BussinessException(BizExceptionEnum.CUSTOMER_FORMAT_ERROR, "导出报表出错！！！【" + e.getMessage() + "】");
        }
	}

	/**
	 * 下载客户日账单
	 * @param response
	 */
	@RequestMapping(value = "/downLoadCustomerBill")
	@ResponseBody
	@AutoSetPortalCustomer({ @PortalParamMeta(setter = PortalParamSetter.PAGE)})
	public void downLoadCustomerBill(Page<CustomerBalanceDay> page) {
	    // 构造page对象参数
        createPageParams(page);
        boolean isCustomerSelected = false;
        isCustomerSelected = page.getParams() != null &&
                    (page.getParams().get("customerId") != null &&
                        StringUtils.isNotEmpty(page.getParams().get("customerId").toString()));
        // 构造数据结构 START
        List<BillsExcel> billsExcels = new ArrayList<BillsExcel>();
        String inputStartTime = String.valueOf(page.getParams().get("inputStartTime"));
        String inputEndTime = String.valueOf(page.getParams().get("inputEndTime"));
        if (!isCustomerSelected) {
            // 取得有日账单的合作伙伴
            List<CustomerBalanceDay> customerCBDList = customerBalanceDayService.selectCustomerCBD();
            for (CustomerBalanceDay customerBalanceDay : customerCBDList) {
                BillsExcel billsExcel = new BillsExcel();
                billsExcel = genBillsExcel(page, customerBalanceDay.getCustomerId(), inputStartTime, inputEndTime, Constant.OBJ_TYPE_CUSTOMER);
                billsExcels.add(billsExcel);
            }
        } else {
        	Long pageCustomerId = Long.valueOf(String.valueOf(page.getParams().get("customerId")));
            BillsExcel billsExcel = new BillsExcel();
            billsExcel = genBillsExcel(page, pageCustomerId, inputStartTime, inputEndTime, Constant.OBJ_TYPE_CUSTOMER);
            billsExcels.add(billsExcel);
        }
        
        Map<String, Object> beanParams = new HashMap<String, Object>();
        beanParams.put("cbdList", billsExcels);
        try {
            TemplateExcelManager.getInstance().createExcelFileAndDownload(TemplateExcel.CUSTOMER_BALANCE_DAY, beanParams);
        } catch (Exception e) {
            LOG.error("导出报表出错！！！【" + e.getMessage() + "】", e);
            throw new BussinessException(BizExceptionEnum.CUSTOMER_FORMAT_ERROR, "导出报表出错！！！【" + e.getMessage() + "】");
        }
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
	
	/**
	 * 构造excel用对象
	 */
	public BillsExcel genBillsExcel(Page<CustomerBalanceDay> page, Long id, String inputStartTime, String inputEndTime, String objType) {
	    BillsExcel billsExcel = new BillsExcel();
        List<BillsDetail> billsDetails = new ArrayList<BillsDetail>();
        if (Constant.OBJ_TYPE_PARTNER.equals(objType)) {
            page.getParams().put("partnerId", id);
            PartnerInfo info = partnerService.get(id);
            
            // 取得已充金额
            double rechargeAmount = customerBalanceDayService.
                    selectSUMTraderAmount(id, inputStartTime, inputEndTime, Constant.TRADE_TYPE_RECHARGE);
            // 取得授信额度
            double creditAmount = customerBalanceDayService.
                    selectSUMTraderAmount(id, inputStartTime, inputEndTime, Constant.TRADE_TYPE_CREDIT);
            // 取得账单金额
            CustomerBalanceDay cbd = new CustomerBalanceDay();
            cbd = customerBalanceDayService.genCBD(id, inputStartTime, inputEndTime);
            BigDecimal priceTotal = (BigDecimal)initPartnerCountAndPriceTotal(cbd).get("priceTotal");
            // 取得所有{YD/LT/DX}+{全国/广东/河南...}组合的数据
            billsDetails = genBillsDetailList(id, null, page, Constant.OBJ_TYPE_PARTNER);
            
            billsExcel.setPartnerInfo(info);
            billsExcel.setRechargeAmount(rechargeAmount);
            billsExcel.setCreditAmount(creditAmount);
            billsExcel.setPriceTotal(priceTotal);
        } else if (Constant.OBJ_TYPE_CUSTOMER.equals(objType)) {
            page.getParams().put("customerId", id);
            CustomerInfo info = customerInfoService.get(id);
            // 取得已充金额
            double rechargeAmount = customerBalanceDayService.
                    selectCustomerSUMTraderAmount(id, inputStartTime, inputEndTime, Constant.TRADE_TYPE_RECHARGE);
            // 取得授信额度
            double creditAmount = customerBalanceDayService.
                    selectCustomerSUMTraderAmount(id, inputStartTime, inputEndTime, Constant.TRADE_TYPE_CREDIT);
            // 取得账单金额
            CustomerBalanceDay cbd = new CustomerBalanceDay();
            cbd = customerBalanceDayService.genCustomerCBD(id, inputStartTime, inputEndTime);
            BigDecimal priceTotal = (BigDecimal)initCustomerCountAndPriceTotal(cbd).get("priceTotal");
            // 取得所有{YD/LT/DX}+{全国/广东/河南...}组合的数据
            billsDetails = genBillsDetailList(null, id, page, Constant.OBJ_TYPE_CUSTOMER);
            
            billsExcel.setCustomerInfo(info);
            billsExcel.setRechargeAmount(rechargeAmount);
            billsExcel.setCreditAmount(creditAmount);
            billsExcel.setPriceTotal(priceTotal);
        }
        
        billsExcel.setInputStartTime(inputStartTime);
        billsExcel.setInputEndTime(inputEndTime);
        billsExcel.setBillsDetails(billsDetails);
        return billsExcel;
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
                if (null == areaCode) {
                    continue;
                }
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
        return billsDetails;
	} 
	
	/**
     * 取出合作伙伴信息
     * @return
     */
    @RequestMapping(value = "/selectPartnerInfoByName")
    @ResponseBody
    public Map<String, Object> selectCustomerInfoByName(String partnerName) {
    	// TODO 
//        // 合作伙伴ID
//        Staff staff = getCurrentLogin();
        Map<String, Object> map= new HashMap<String, Object>();
        List<PartnerInfo> list = new ArrayList<PartnerInfo>();
//        
//        // 云之讯管理员
//        if (isAdmin()) {
            list = partnerService.selectPartnerInfoByName(partnerName, null);
//        } else {
//            // 合作伙伴
//            PartnerInfo partnerInfo = partnerService.getByAccount(staff.getLoginName());
//            if (partnerInfo == null) {
//                return fail("合作伙伴不存在");
//            }
//            list = partnerService.selectPartnerInfoByName(partnerName, partnerInfo.getPartnerId());
//        }
        map.put("partnerList", list);
        return map;
    }
    
    /**
     * 构造Page对象参数
     * 
     * @param page
     */
    private void createPageParams(Page<CustomerBalanceDay> page) {
    	// TODO 
//        Staff staff = getCurrentLogin();
        PartnerInfo partnerInfo = new PartnerInfo();
//        if (!isAdmin()) {
//            // 合作伙伴
//            partnerInfo = partnerService.getByAccount(staff.getLoginName());
//            if (partnerInfo == null) {
//                LOG.error("合作伙伴不存在");
//                return;
//            }
//            page.getParams().put("partnerId", partnerInfo.getPartnerId());
//        }
        page.setRows(50000);
    }
}