package com.yzx.flow.modular.partner.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yzx.flow.common.controller.BaseController;
import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.page.PageInfoBT;
import com.yzx.flow.common.persistence.model.PartnerTradeFlow;
import com.yzx.flow.core.shiro.ShiroKit;
import com.yzx.flow.core.shiro.ShiroUser;
import com.yzx.flow.modular.partner.service.IPartnerService;
import com.yzx.flow.modular.partner.service.IPartnerTradeFlowService;

/**
 * 合作伙伴交易流水
 * <b>Title：</b>PartnerTradeFlowController.java<br/>
 * <b>Description：</b> <br/>
 * <b>@author： </b>v5480<br/>
 * <b>@date：</b>2015-09-02 10:17:41<br/>
 * <b>Copyright (c) 2015 szwisdom Tech.</b>
 * 
 */
@Controller
@RequestMapping("/partnerTradeFlow")
public class PartnerTradeFlowController extends BaseController {
	
	private static final Logger LOG = LoggerFactory.getLogger(PartnerTradeFlowController.class);
	
	@Autowired
	private IPartnerTradeFlowService partnerTradeFlowService;
	
	@Autowired
	private IPartnerService partnerService;

	
	@RequestMapping(value = "/query.ajax")
	@ResponseBody
	public PageInfoBT<PartnerTradeFlow> pageQuery(Page<PartnerTradeFlow> page, int pkId) {
	    page.getParams().put("partnerId", pkId);
		Page<PartnerTradeFlow> list = partnerTradeFlowService.pageQuery(page);
		return new PageInfoBT<PartnerTradeFlow>(list.getDatas(), list.getTotal());
	}

	@RequestMapping(value = "/add.ajax", method = RequestMethod.POST)
	@ResponseBody
	public Object add(@RequestBody PartnerTradeFlow data) {
	    ShiroUser staff = ShiroKit.getUser();
		partnerTradeFlowService.savePartnerTradeFlow(data, staff);
		return SUCCESS_TIP;
	}

	@RequestMapping(value = "/delete.ajax")
	@ResponseBody
	public Object delete(Long tradeFlowId) {
		partnerTradeFlowService.delete(tradeFlowId);
		return SUCCESS_TIP;
	}

	@RequestMapping(value = "/get.ajax")
	@ResponseBody
	public Object get(Long tradeFlowId) {
		PartnerTradeFlow data = partnerTradeFlowService.get(tradeFlowId);
		return data;
	}

	/**
	 * 更新的时候需额外传递updId,值跟主键值一样,被@ModelAttribute注释的方法会在此controller每个方法执行前被执行，
	 * 要谨慎使用
	 */
	@ModelAttribute("partnerTradeFlow")
	public void getForUpdate(@RequestParam(value = "updId", required = false) Long updId, Model model) {
		if (null != updId) {
			model.addAttribute("partnerTradeFlow", partnerTradeFlowService.get(updId));
		} else {
			model.addAttribute("partnerTradeFlow", new PartnerTradeFlow());
		}
	}
}