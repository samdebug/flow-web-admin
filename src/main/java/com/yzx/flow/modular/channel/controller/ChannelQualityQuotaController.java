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
import com.yzx.flow.common.persistence.model.ChannelQualityQuota;
import com.yzx.flow.common.persistence.model.Staff;
import com.yzx.flow.core.shiro.ShiroKit;
import com.yzx.flow.core.shiro.ShiroUser;
import com.yzx.flow.core.util.ToolUtil;
import com.yzx.flow.modular.channel.service.IChannelQualityQuotaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 通道质量标准控制器
 *
 * @author liuyufeng
 * @Date 2017-08-17 16:16:24
 */
@Controller
@RequestMapping("/channelQualityQuota")
public class ChannelQualityQuotaController extends BaseController {

	@Autowired
	private IChannelQualityQuotaService channelQualityQuotaService;
	
    private String PREFIX = "/channel/channelQualityQuota/";

    /**
     * 跳转到通道质量标准首页
     */
    @RequestMapping("")
    public String index() {
        return PREFIX + "channelQualityQuota.html";
    }

    /**
     * 跳转到添加通道质量标准
     */
    @RequestMapping("/channelQualityQuota_add")
    public String channelQualityQuotaAdd() {
        return PREFIX + "channelQualityQuota_add.html";
    }

    /**
     * 跳转到修改通道质量标准
     */
    @RequestMapping("/channelQualityQuota_update/{channelQualityQuotaId}")
    public String channelQualityQuotaUpdate(@PathVariable String channelQualityQuotaId, Model model) {
    	if (ToolUtil.isEmpty(channelQualityQuotaId)) {
			throw new BussinessException(BizExceptionEnum.REQUEST_NULL);
		}
    	ChannelQualityQuota data = channelQualityQuotaService.get(Long.parseLong(channelQualityQuotaId));
    	model.addAttribute("info", data);
        return PREFIX + "channelQualityQuota_edit.html";
    }

    /**
     * 获取通道质量标准列表
     */
    @RequestMapping(value = "/list")
    @ResponseBody
    public PageInfoBT<ChannelQualityQuota> list(Page<ChannelQualityQuota> page) {
    	PageInfoBT<ChannelQualityQuota> resPage = channelQualityQuotaService.pageQuery(page);
    	return resPage;
    }

    /**
     * 新增通道质量标准
     */
    @RequestMapping(value = "/add")
    @ResponseBody
    public Object add(ChannelQualityQuota data) {
    	ShiroUser user = ShiroKit.getUser();
    	Staff staff = new Staff();
    	staff.setLoginName(user.getName());
		if (null == staff.getLoginName()) {
			throw new BussinessException(BizExceptionEnum.REQUEST_INVALIDATE);
		}
		channelQualityQuotaService.saveAndUpdate(data,staff);
        return SUCCESS_TIP;
    }

    /**
     * 删除通道质量标准
     */
    @RequestMapping(value = "/delete")
    @ResponseBody
    public Object delete(@RequestParam String channelQualityQuotaId) {
    	if (ToolUtil.isEmpty(channelQualityQuotaId)) {
			throw new BussinessException(BizExceptionEnum.REQUEST_NULL);
		}
    	int i = channelQualityQuotaService.delete(Long.parseLong(channelQualityQuotaId));
    	if (i<0) {
			return new ErrorTip(0, "刪除失敗!");
		}
        return SUCCESS_TIP;
    }


    /**
     * 修改通道质量标准
     */
    @RequestMapping(value = "/update")
    @ResponseBody
    public Object update(ChannelQualityQuota data) {
    	ShiroUser user = ShiroKit.getUser();
    	Staff staff = new Staff();
    	staff.setLoginName(user.getName());
		if (null == staff.getLoginName()) {
			throw new BussinessException(BizExceptionEnum.REQUEST_INVALIDATE);
		}
	    channelQualityQuotaService.saveAndUpdate(data,staff);
        return SUCCESS_TIP;
    }

    /**
     *  根据名称筛选通道质量指标
     * @param quotaName
     * @return
     */
    @RequestMapping(value = "/selectQualityInfo")
	@ResponseBody
	public Map<String, Object> selectQualityInfo(String quotaName){
		Map<String, Object> map = new HashMap<String,Object>();
		List<ChannelQualityQuota> qualityQuotaList = channelQualityQuotaService.selectQuotaInfo(quotaName);
		if(!qualityQuotaList.isEmpty()){
			map.put("qualityQuotaList", qualityQuotaList);
		}
		return map;
	}
    
}
