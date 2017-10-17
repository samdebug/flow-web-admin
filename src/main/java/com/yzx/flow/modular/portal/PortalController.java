package com.yzx.flow.modular.portal;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yzx.flow.common.annotion.PortalCustomer;
import com.yzx.flow.common.constant.tips.ErrorTip;
import com.yzx.flow.common.controller.BaseController;
import com.yzx.flow.common.exception.BizExceptionEnum;
import com.yzx.flow.common.persistence.model.CustomerInfo;
import com.yzx.flow.common.util.DateUtil;
import com.yzx.flow.core.shiro.ShiroKit;
import com.yzx.flow.modular.customer.service.ICustomerInfoService;

/**
 * 
 * @author Liulei
 *
 */
@Controller
@RequestMapping("/portal")
public class PortalController extends BaseController {
	
	private String PREFIX = "/portal/";
	
	
	@Autowired
    private ICustomerInfoService customerInfoService;
	
	
	
	/*
	 ************************************************************************************************
	 *                                             订单相关
	 ************************************************************************************************/
	
	/**
	 * 跳转进入订单列表
	 * @return
	 */
	@PortalCustomer
	@RequestMapping("/order_list")
	public String orderList(Model model) {
		model.addAttribute("nowStartTime", DateUtil.dateToDateString(new Date(), "yyyy-MM-dd 00:00:00"));
    	model.addAttribute("nowEndTime", DateUtil.dateToDateString(new Date(), "yyyy-MM-dd 23:59:59"));
		return String.format("%s%s", PREFIX, "order/list.html");
	}
	
	
	/**
	 * 跳转进入订单详情
	 * @return
	 */
	@PortalCustomer
	@RequestMapping("/order_view")
	public String orderView(Model model) {
		return String.format("%s%s", PREFIX, "order/view.html");
	}
	
	
	
	
	
	/*
	 ************************************************************************************************
	 *                                            账户相关
	 ************************************************************************************************/
	
	/**
	 * 跳转至账户信息
	 * @param model
	 * @return
	 */
	@PortalCustomer
	@RequestMapping("/customer_charge")
	public String customerCharge(Model model) {
		return String.format("%s%s", PREFIX, "customer/charge_list.html");
	}
	
	
	/**
	 * 跳转至账户明细
	 * @param model
	 * @return
	 */
	@PortalCustomer
	@RequestMapping("/charge_view")
	public String customerChargeView(Model model) {
		return String.format("%s%s", PREFIX, "customer/charge_view.html");
	}
	
	
	/**
	 * 保存客户信息
	 * @param model
	 * @return
	 */
	@PortalCustomer
	@RequestMapping(value = "/saveCustomerInfo", method = RequestMethod.POST)
	@ResponseBody
	public Object saveCustomerInfo(String mobile, String email) {
		
		if ( StringUtils.isBlank(mobile) || StringUtils.isBlank(email) ) {
			return ErrorTip.buildErrorTip(BizExceptionEnum.REQUEST_NULL);
		}
		// 当前登陆客户
		CustomerInfo current = customerInfoService.get(ShiroKit.getUser().getTargetId());
		if ( current == null )
			return ErrorTip.buildErrorTip(BizExceptionEnum.REQUEST_NULL);
		
		current.setLinkmanMobile(mobile);
		current.setLinkmanEmail(email);
		
		customerInfoService.saveAndUpdate(current);
		return SUCCESS_TIP;
	}
	
	
	
	/*
	 ************************************************************************************************
	 *                                          产品配置相关
	 ************************************************************************************************/
	
	/**
	 * 跳转至产品配置
	 * @param model
	 * @return
	 */
	@PortalCustomer
	@RequestMapping("/product_list")
	public String customerProducts(Model model) {
		return String.format("%s%s", PREFIX, "product/list.html");
	}
	
	
	/**
	 * 跳转至产品配置详情
	 * @param model
	 * @return
	 */
	@PortalCustomer
	@RequestMapping("/product_view")
	public String customerProductDetail(Model model) {
		return String.format("%s%s", PREFIX, "product/view.html");
	}
	
	
	
	
	
	/*
	 ************************************************************************************************
	 *                                      客户日/月账单 相关
	 ************************************************************************************************/
	
	/**
	 * 跳转至客户日账单
	 * @param model
	 * @return
	 */
	@PortalCustomer
	@RequestMapping("/billDay")
	public String billDay(Model model) {
		return String.format("%s%s", PREFIX, "bill/day.html");
	}
	
	/**
	 * 跳转至客户月账单
	 * @param model
	 * @return
	 */
	@PortalCustomer
	@RequestMapping("/billMonth")
	public String billMonth(Model model) {
		return String.format("%s%s", PREFIX, "bill/month.html");
	}
	
	
	
	
	/*
	 ************************************************************************************************
	 *                                      流量充值 相关
	 ************************************************************************************************/
	/**
	 * 跳转至 单号码充值 页面
	 * @param model
	 * @return
	 */
	@PortalCustomer
	@RequestMapping("/flowRecharge_single")
	public String rechargeSingle(Model model) {
		return String.format("%s%s", PREFIX, "flowRecharge/recharge.html");
	}
	
	/**
	 * 跳转至 批量充值事件页面
	 * @param model
	 * @return
	 */
	@PortalCustomer
	@RequestMapping("/recharge_events")
	public String rechargeEvents(Model model) {
		return String.format("%s%s", PREFIX, "flowRecharge/event_list.html");
	}
	
	
	/**
	 * 跳转至 批量充值 页面
	 * @param model
	 * @return
	 */
	@PortalCustomer
	@RequestMapping("/flowRecharge_batch")
	public String rechargeBatch(Model model) {
		return String.format("%s%s", PREFIX, "flowRecharge/recharge_batch.html");
	}

}
