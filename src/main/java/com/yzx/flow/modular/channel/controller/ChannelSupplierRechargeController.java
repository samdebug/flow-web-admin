package com.yzx.flow.modular.channel.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yzx.flow.common.controller.BaseController;
import com.yzx.flow.common.excel.TemplateExcel;
import com.yzx.flow.common.excel.TemplateExcelManager;
import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.page.PageInfoBT;
import com.yzx.flow.common.persistence.model.ChannelSupplierRecharge;
import com.yzx.flow.common.persistence.model.Staff;
import com.yzx.flow.common.util.CommonUtil;
import com.yzx.flow.core.shiro.ShiroKit;
import com.yzx.flow.core.shiro.ShiroUser;
import com.yzx.flow.modular.channel.service.IChannelSupplierRechargeService;

/**
 * 佛年供应商充值记录
 *
 */
@Controller
@RequestMapping("/channelSupplierRecharge")
public class ChannelSupplierRechargeController extends BaseController {
	private static final Logger LOG = LoggerFactory.getLogger(ChannelSupplierRechargeController.class);
	@Autowired
	@Qualifier("channelSupplierRechargeService")
	private IChannelSupplierRechargeService channelSupplierRechargeService;

	private String PREFIX = "/channel/channelSupplierRecharge/";
	  /**
     * 跳转到充值明细首页
     */
    @RequestMapping("")
    public String index(@RequestParam String supplierCode,Model model) {
    	model.addAttribute("supplierCode", supplierCode);
        return PREFIX + "channelSupplierRecharge_view.html";
    }
	
	@RequestMapping(value = "/query")
	@ResponseBody
	public PageInfoBT<ChannelSupplierRecharge> pageQuery(Page<ChannelSupplierRecharge> page, String supplierCode) {
		LOG.info("分页查询充值记录,params={}",page.getParams());
		if (page.getSort()==null) {
			page.setSort("input_time");
		}
		page.getParams().put("supplierCode", supplierCode);
		PageInfoBT<ChannelSupplierRecharge> resPage = channelSupplierRechargeService.pageQuery(page);
		return resPage;
	}

	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ResponseBody
	public Object add(@RequestBody ChannelSupplierRecharge data,HttpServletRequest request) {
		ShiroUser user =ShiroKit.getUser();
		Staff staff = new Staff();
		staff.setLoginName(user.getName());
	    channelSupplierRechargeService.saveSupplierRecharge(data, staff,CommonUtil.getIp(request));
		return SUCCESS_TIP;
	}

	@RequestMapping(value = "/delete")
	@ResponseBody
	public Object delete(Long supplierRechargeId) {
		channelSupplierRechargeService.delete(supplierRechargeId);
		return SUCCESS_TIP;
	}

	@RequestMapping(value = "/get")
	@ResponseBody
	public Map<String, Object> get(Long supplierRechargeId) {
		ChannelSupplierRecharge data = channelSupplierRechargeService.get(supplierRechargeId);
		Map<String, Object> resMap=new HashMap<String, Object>();
		resMap.put("data",data);
		resMap.put("success", Boolean.valueOf(true));
		return resMap;
	}

	/**
	 * 更新的时候需额外传递updId,值跟主键值一样,被@ModelAttribute注释的方法会在此controller每个方法执行前被执行，
	 * 要谨慎使用
	 */
	@ModelAttribute("ChannelSupplierRecharge")
	public void getForUpdate(@RequestParam(value = "updId", required = false) Long updId, Model model) {
		if (null != updId) {
			model.addAttribute("channelSupplierRecharge", channelSupplierRechargeService.get(updId));
		} else {
			model.addAttribute("channelSupplierRecharge", new ChannelSupplierRecharge());
		}
	}
	
	/**
	 * 报表导出
	 * @param response
	 * @param page
	 * @throws Exception
	 */
	@RequestMapping(value = "/export")
	public void export(Page<ChannelSupplierRecharge> page) throws Exception {
		try {
			// 生成Excel
			Map<String, Object> beanMap = new HashMap<String, Object>();
			page.setRows(10000);
			beanMap.put("list", channelSupplierRechargeService.pageQuery(page).getRows());
			TemplateExcelManager.getInstance().createExcelFileAndDownload(TemplateExcel.CHANNEL_SUPPLIER_RECHARGE, beanMap);
		} catch (IOException e) {
			LOG.error("导出报表出错！！！", e);
		}
	}
}