package com.yzx.flow.modular.sms.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yzx.flow.common.controller.BaseController;
import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.persistence.model.SmsChannelInfo;
import com.yzx.flow.modular.sms.service.ISmsChannelInfoService;


/**
 * 
 * <b>Title：</b>SmsChannelInfoController.java<br/>
 * <b>Description：</b> <br/>
 * <b>@author： </b>v5480<br/>
 * <b>@date：</b>2015-08-01 11:38:16<br/>
 * <b>Copyright (c) 2015 szwisdom Tech.</b>
 * 
 */
@Controller
@RequestMapping("/smsChannelInfo")
public class SmsChannelInfoController extends BaseController{
	private static final Logger LOG = LoggerFactory.getLogger(SmsChannelInfoController.class);
	
    @Autowired
    @Qualifier("smsChannelInfoService")
    private ISmsChannelInfoService smsChannelInfoService;
    
    @RequestMapping(value = "/query")
	@ResponseBody
	 public Object pageQuery(Page<SmsChannelInfo> page) {
	    Page<SmsChannelInfo> list = smsChannelInfoService.pageQuery(page);
	    Map<String, Object> modelMap = new HashMap<String, Object>(5);
		modelMap.put("records", Integer.valueOf(list.getTotal()));
		modelMap.put("rows", list.getDatas());
		modelMap.put("page", list.getPage());
		modelMap.put("total", Integer.valueOf(list.getTotalPage()));
		modelMap.put("success", Boolean.valueOf(true));
		return modelMap;
    }
    
    @RequestMapping(value = "/add", method = RequestMethod.POST)
	@ResponseBody
	public Object add(@ModelAttribute("smsChannelInfo") SmsChannelInfo data) {
    	smsChannelInfoService.saveAndUpdate(data);
		return SUCCESS_TIP;
    }
   
    @RequestMapping(value = "/delete")
	@ResponseBody
	 public Object delete(Long smsChannelId) {
    		smsChannelInfoService.delete(smsChannelId);
			return SUCCESS_TIP;
	  }
	 @RequestMapping(value = "/get")
	 @ResponseBody
	 public Object get(Long smsChannelId) {
	 	SmsChannelInfo data = smsChannelInfoService.get(smsChannelId);
		return SUCCESS_TIP;
    }
	 
    /**
    *	更新的时候需额外传递updId,值跟主键值一样,被@ModelAttribute注释的方法会在此controller每个方法执行前被执行，要谨慎使用
    */
    @ModelAttribute("smsChannelInfo")
	public void getForUpdate(@RequestParam(value = "updId",required=false) Long updId,
			Model model) {
    	if (null != updId) {
    		SmsChannelInfo smsChannelInfo = smsChannelInfoService.get(updId);
    		if (null != smsChannelInfo){
    			model.addAttribute("smsChannelInfo",smsChannelInfo);
    			return;
    		}
		}
		model.addAttribute("smsChannelInfo",new SmsChannelInfo());
	}
}