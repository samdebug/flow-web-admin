package com.yzx.flow.modular.customerSupplier.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yzx.flow.common.controller.BaseController;
import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.page.PageInfoBT;
import com.yzx.flow.common.persistence.model.CustomerSupplierInfo;
import com.yzx.flow.modular.customerSupplier.service.ICustomerSupplierInfoService;

/**
 * 客户供应商管理控制器
 *
 * @author liuyufeng
 * @Date 2017-08-17 16:17:50
 */
@Controller
@RequestMapping("/customerSupplier")
public class CustomerSupplierController extends BaseController {

    private String PREFIX = "/customerSupplier/";
    
    
//    @Autowired
//    private IPartnerService partnerService; 
    
    @Autowired
    private ICustomerSupplierInfoService customerSupplierInfoService;
    
    

    /**
     * 跳转到客户供应商管理首页
     */
    @RequestMapping("")
    public String index() {
        return PREFIX + "customerSupplier.html";
    }


    
    /**
     * 客户充值明细
     * @param page
     * @return
     */
    @RequestMapping(value = "/customerList")
    @ResponseBody
    public Object customerList(Page<CustomerSupplierInfo> page) {
    	// TODO 
//    	Staff staff = getCurrentLogin();
//		if (isAdmin()) {
//			String partnerId=(String)page.getParams().get("partnerId");
//			if(StringUtils.isBlank(partnerId)){
//				page.getParams().put("partnerId", null);
//			}
//		
//		} else {
//			PartnerInfo partnerInfo = partnerInfoService.getByAccount(staff.getLoginName());
//			if (partnerInfo == null) {
//				return fail("没有合作伙伴");
//			}
//			page.getParams().put("partnerId", partnerInfo.getPartnerId());
//		}
		page.getParams().put("partnerId", null);
		Page<CustomerSupplierInfo> list = customerSupplierInfoService.customerPageQuery(page);
        return new PageInfoBT<CustomerSupplierInfo>(list.getDatas(), list.getTotal());
    }
    
    /**
     * 
     * @param page
     * @return
     */
    @RequestMapping(value = "/supplierList")
	@ResponseBody
	public Object supplierPageQuery(Page<CustomerSupplierInfo> page) {
		Page<CustomerSupplierInfo> list = customerSupplierInfoService.supplierPageQuery(page);
		return new PageInfoBT<CustomerSupplierInfo>(list.getDatas(), list.getTotal());
	}
    
    
    /**
	 * 充值净差值
	 * @param page
	 * @return
	 */
	@RequestMapping(value = "/chargeDiff")
	@ResponseBody
	public Object chargeDiff(Page<CustomerSupplierInfo> page) {
        Map<String, Object> map = new HashMap<String, Object>();
        try {
        	map = customerSupplierInfoService.chargeDiff(page);
		} catch (Exception e) {
			map.put("success", false);
		}
        return map;
	}
    
}
