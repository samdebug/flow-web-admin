package com.yzx.flow.modular.channel.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.yzx.flow.common.constant.tips.ErrorTip;
import com.yzx.flow.common.controller.BaseController;
import com.yzx.flow.common.exception.BizExceptionEnum;
import com.yzx.flow.common.exception.BussinessException;
import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.page.PageInfoBT;
import com.yzx.flow.common.persistence.model.ChannelAdapter;
import com.yzx.flow.common.util.RedisHttpUtil;
import com.yzx.flow.common.util.URLConstants;
import com.yzx.flow.core.util.ToolUtil;
import com.yzx.flow.modular.channel.service.IChannelAdapterService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 通道适配器控制器
 *
 * @author cd
 * @Date 2017-08-16 09:20:38
 */
@Controller
@RequestMapping("/channelAdapter")
public class ChannelAdapterController extends BaseController {

	private static final Logger LOG = LoggerFactory.getLogger(AccessChannelInfoController.class);
	   
	
	@Autowired
	IChannelAdapterService channelAdapterService;
	
    private String PREFIX = "/channel/channelAdapter/";

    /**
     * 跳转到通道适配器首页
     */
    @RequestMapping("")
    public String index() {
        return PREFIX + "channelAdapter.html";
    }

    /**
     * 跳转到添加通道适配器
     */
    @RequestMapping("/channelAdapter_add")
    public String channelAdapterAdd() {
        return PREFIX + "channelAdapter_add.html";
    }

    /**
     * 跳转到修改通道适配器
     */
    @RequestMapping("/channelAdapter_update/{channelAdapterId}")
    public String channelAdapterUpdate(@PathVariable String channelAdapterId, Model model) {
    	if (channelAdapterId==null ||"".equals(channelAdapterId)||"undefined".equals(channelAdapterId)) {
			throw new IllegalArgumentException("非法输入");
		}
    	ChannelAdapter channelAdapter = channelAdapterService.get(Long.parseLong(channelAdapterId));
    	model.addAttribute("channelAdapter", channelAdapter);
        return PREFIX + "channelAdapter_edit.html";
    }

    /**
     * 获取通道适配器列表
     */
    @RequestMapping(value = "/list")
    @ResponseBody
    public PageInfoBT<ChannelAdapter> list(Page<ChannelAdapter> page) {
    	if (page.getSort()==null) {
			page.setSort("create_time");
		}
    	PageInfoBT<ChannelAdapter> pageRes = channelAdapterService.pageQuery(page);
		return pageRes;
    }

    @RequestMapping(value = "/find")
	@ResponseBody
	public List<ChannelAdapter> find() {
		List<ChannelAdapter> list = channelAdapterService.find(new ChannelAdapter());
		return list;
	}
    
    
    
    /**
     * 新增通道适配器
     */
    @RequestMapping(value = "/add")
    @ResponseBody
    public Object add(ChannelAdapter channelAdapter) {
    	try {
			channelAdapterService.saveAndUpdate(channelAdapter);
			return SUCCESS_TIP;
		} catch (Exception e) {
			LOG.info("新增通道适配器失败", e);
			return new ErrorTip(0, "新增失败");
		}
    }

    /**
     * 删除通道适配器
     */
    @RequestMapping(value = "/delete")
    @ResponseBody
    public Object delete(@RequestParam String  channelAdapterId) {
    	
    	if(ToolUtil.isEmpty(channelAdapterId)){
    		throw new BussinessException(BizExceptionEnum.REQUEST_NULL);
    	}
    	int i = channelAdapterService.delete(Long.parseLong(channelAdapterId));
    	if (i<0) {
			return new ErrorTip(200, "删除失败");
		}
        return SUCCESS_TIP;
    }


    /**
     * 修改通道适配器
     */
    @RequestMapping(value = "/update")
    @ResponseBody
    public Object update(ChannelAdapter channelAdapter) {
    	try {
			channelAdapterService.saveAndUpdate(channelAdapter);
			return super.SUCCESS_TIP;
		} catch (Exception e) {
			LOG.info("通道适配器更新失败",e);
		}
    	return new   ErrorTip(500,"通道适配器更新失败");
    }

    /**
     * 通道适配器详情
     */
    @RequestMapping(value = "/detail/{channelAdapterId}")
    public Object detail(@PathVariable String channelAdapterId, Model model) {
    	if (channelAdapterId==null ||"".equals(channelAdapterId)||"undefined".equals(channelAdapterId)) {
			throw new IllegalArgumentException("非法输入");
		}
    	ChannelAdapter channelAdapter = channelAdapterService.get(Long.parseLong(channelAdapterId));
    	model.addAttribute("channelAdapter", channelAdapter);
        return PREFIX + "channelAdapter_view.html";
    }
    
}
