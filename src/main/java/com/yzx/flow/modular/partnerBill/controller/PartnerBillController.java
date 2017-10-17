package com.yzx.flow.modular.partnerBill.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.yzx.flow.common.controller.BaseController;

/**
 * 合作伙伴账单管理控制器
 *
 * @author liuyufeng
 * @Date 2017-08-18 15:03:09
 */
@Controller
@RequestMapping("/partnerBill")
public class PartnerBillController extends BaseController {

    private String PREFIX = "/partnerBill/";

    /**
     * 跳转到合作伙伴账单管理首页
     */
    @RequestMapping("")
    public String index() {
        return PREFIX + "partnerBill.html";
    }
    
    
    /**
     * 跳转到在线充值页面
     */
    @RequestMapping("/partnerBill_onlinePay")
    public String onlinePay() {
    	return PREFIX + "partnerBill_onlinePay.html";
    }
    
    /**
     * 跳转到合作伙伴账单查询
     */
    @RequestMapping("/partnerBillQuery")
    public String partnerBillQuery() {
    	return PREFIX + "partnerBillQuery_view.html";
    }
    
    
    /**
     * 跳转到合作伙伴 结算单管理
     */
    @RequestMapping("/partnerSettlementOrder")
    public String partnerSettlementOrder() {
    	return PREFIX + "partnerSettlementOrder_list.html";
    }
    

    /**
     * 跳转到添加合作伙伴账单管理
     */
    @RequestMapping("/partnerBill_add")
    public String partnerBillAdd() {
        return PREFIX + "partnerBill_add.html";
    }
    
    

    /**
     * 跳转到修改合作伙伴账单管理
     */
    @RequestMapping("/partnerBill_update/{partnerBillId}")
    public String partnerBillUpdate(@PathVariable Integer partnerBillId, Model model) {
        return PREFIX + "partnerBill_edit.html";
    }


    /**
     * 合作伙伴账户明细
     */
    @RequestMapping(value = "/partnerAccountDetail")
    public String partnerAccountDetail(Long partnerId, Model model) {
    	
    	if ( partnerId == null || partnerId.compareTo(0L) <= 0 )
    		return String.format("%s%s", FORWARD, "/global/error");
    		
    	model.addAttribute("partnerId", partnerId);
        return PREFIX + "partnerAccountDetail_view.html";
    }
}
