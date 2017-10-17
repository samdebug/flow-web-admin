package com.yzx.flow.modular.customer.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yzx.flow.common.controller.BaseController;
import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.page.PageInfoBT;
import com.yzx.flow.common.persistence.model.CustomerTradeFlow;
import com.yzx.flow.common.persistence.model.Staff;
import com.yzx.flow.core.shiro.ShiroKit;
import com.yzx.flow.modular.customer.service.ICustomerBalanceDayService;
import com.yzx.flow.modular.customer.service.ICustomerTradeFlowService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 客户充值管理控制器
 *
 * @author liuyufeng
 * @Date 2017-08-25 17:48:36
 */
@Controller
@RequestMapping("/customerCharge")
public class CustomerChargeController extends BaseController {

    private String PREFIX = "/customerCharge/";

    private static final Logger LOG = LoggerFactory.getLogger(CustomerChargeController.class);
    
	@Autowired
	@Qualifier("customerTradeFlowService")
	private ICustomerTradeFlowService customerTradeFlowService;

	@Autowired
	@Qualifier("customerBalanceDayService")
	private ICustomerBalanceDayService customerBalanceDayService;
	

    /**
     * 跳转到客户充值管理列表首页
     */
    @RequestMapping("")
    public String index() {
        return PREFIX + "customerCharge.html";
    }
    
    
    /**
     * 跳转到客户充值页面
     */
    @RequestMapping("/customerChargeView")
    public String customerChargeView(@RequestParam String customerId,Model model) {
    	model.addAttribute("customerId", customerId);
        return PREFIX + "customerCharge_view.html";
    }

}
