package com.yzx.flow.modular.channel.controller;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSONObject;
import com.yzx.flow.common.constant.tips.ErrorTip;
import com.yzx.flow.common.controller.BaseController;
import com.yzx.flow.common.exception.BizExceptionEnum;
import com.yzx.flow.common.exception.BussinessException;
import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.page.PageInfoBT;
import com.yzx.flow.common.persistence.model.AreaCode;
import com.yzx.flow.common.persistence.model.ChannelMaintainInfo;
import com.yzx.flow.common.persistence.model.ChannelQualityQuota;
import com.yzx.flow.common.persistence.model.Staff;
import com.yzx.flow.common.util.CommonUtil;
import com.yzx.flow.common.util.DateUtil;
import com.yzx.flow.core.shiro.ShiroKit;
import com.yzx.flow.core.shiro.ShiroUser;
import com.yzx.flow.core.util.ToolUtil;
import com.yzx.flow.modular.channel.service.IChannelMaintainInfoService;
import com.yzx.flow.modular.channel.service.impl.ChannelMaintainInfoServiceImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 通道维护信息控制器
 *
 * @author liuyufeng
 * @Date 2017-08-16 16:04:00
 */
@Controller
@RequestMapping("/channelMaintainInfo")
public class ChannelMaintainInfoController extends BaseController {

	private static final Logger LOG = LoggerFactory.getLogger(ChannelMaintainInfoController.class);
	@Autowired
	private IChannelMaintainInfoService channelMaintainInfoService;
	
    private String PREFIX = "/channel/channelMaintainInfo/";

    /**
     * 跳转到通道维护信息首页
     */
    @RequestMapping("")
    public String index(Model model) {
        return PREFIX + "channelMaintainInfo.html";
    }

    /**
     * 跳转到添加通道维护信息
     */
    @RequestMapping("/channelMaintainInfo_add")
    public String channelMaintainInfoAdd(Model model) {
    	model.addAttribute("areas",  channelMaintainInfoService.selectAreaCodeAll());
        return PREFIX + "channelMaintainInfo_add.html";
    }

    /**
     * 跳转到修改通道维护信息
     */
    @RequestMapping("/channelMaintainInfo_update/{channelMaintainInfoId}")
    public String channelMaintainInfoUpdate(@PathVariable String channelMaintainInfoId, Model model) {
    	if (ToolUtil.isEmpty(channelMaintainInfoId)) {
			throw new BussinessException(BizExceptionEnum.REQUEST_NULL);
		}
    	ChannelMaintainInfo data = channelMaintainInfoService.get(Long.parseLong(channelMaintainInfoId));
    	Map<String, Object> params = JSONObject.parseObject(JSONObject.toJSONString(data), Map.class);
    	params.put("startTime", DateUtil.dateToDateString(data.getStartTime(), "yyyy-MM-dd HH:mm:ss"));
    	params.put("endTime", DateUtil.dateToDateString(data.getEndTime(), "yyyy-MM-dd HH:mm:ss"));
    	model.addAttribute("info",params);
    	model.addAttribute("areas",  channelMaintainInfoService.selectAreaCodeAll());
        return PREFIX + "channelMaintainInfo_edit.html";
    }

    /**
     * 获取通道维护信息列表
     */
    @RequestMapping(value = "/list")
    @ResponseBody
    public PageInfoBT<ChannelMaintainInfo> list(Page<ChannelMaintainInfo> page) {
    	PageInfoBT<ChannelMaintainInfo> resPage= channelMaintainInfoService.pageQuery(page);
    	return resPage;
    }

    /**
     * 新增通道维护信息
     */
    @RequestMapping(value = "/add")
    @ResponseBody
    public Object add( ChannelMaintainInfo data,HttpServletRequest request) {
    	ShiroUser user =ShiroKit.getUser(); 
    	Staff staff = new Staff();
    	staff.setLoginName(user.getName());
    	channelMaintainInfoService.saveAndUpdate(data,staff,request);
        return SUCCESS_TIP;
    }

    /**
     * 删除通道维护信息
     */
    @RequestMapping(value = "/delete")
    @ResponseBody
    public Object delete(@RequestParam String  channelMaintainInfoId) {
    	if (ToolUtil.isEmpty(channelMaintainInfoId)) {
			throw new BussinessException(BizExceptionEnum.REQUEST_NULL);
		}
    	int i = channelMaintainInfoService.delete(Long.parseLong(channelMaintainInfoId));
    	if (i<0) {
			return new  ErrorTip(200, "删除失败");
		}
        return SUCCESS_TIP;
    }


    /**
     * 修改通道维护信息
     */
    @RequestMapping(value = "/update")
    @ResponseBody
    public Object update( ChannelMaintainInfo data,HttpServletRequest request) {
    	ShiroUser user =ShiroKit.getUser(); 
    	Staff staff = new Staff();
    	staff.setLoginName(user.getName());
    	channelMaintainInfoService.saveAndUpdate(data,staff,request);
        return SUCCESS_TIP;
    }

    
    @RequestMapping(value = "/selectAreaCodeAll")
	@ResponseBody
	public List<AreaCode> selectAreaCodeAll(){
		List<AreaCode> areaCodeList = channelMaintainInfoService.selectAreaCodeAll();
		if(areaCodeList.isEmpty()){
			LOG.error("获取对应地区信息异常");
		}
		return areaCodeList;
	}
    
    
   /* *//**
	 * 更新的时候需额外传递updId,值跟主键值一样,被@ModelAttribute注释的方法会在此controller每个方法执行前被执行，
	 * 要谨慎使用
	 *//*
	@ModelAttribute("channelMaintainInfo")
	public void getForUpdate(
			@RequestParam(value = "updId", required = false) Long updId,
			Model model) {
		if (null != updId) {
			model.addAttribute("channelMaintainInfo",
					channelMaintainInfoService.get(updId));
		} else {
			model.addAttribute("channelMaintainInfo", new ChannelMaintainInfo());
		}
	}*/
}
