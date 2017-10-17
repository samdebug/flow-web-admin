package com.yzx.flow.modular.statistics.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.yzx.flow.common.controller.BaseController;
import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.page.PageInfoBT;
import com.yzx.flow.common.persistence.model.ChannelAccountAnalyse;
import com.yzx.flow.common.persistence.model.ChannelAnalyse;
import com.yzx.flow.modular.statistics.service.IChannelAccountAnalyseService;

/**
 * 通道结算账单控制器
 *
 * @author liuyufeng
 * @Date 2017-08-16 16:38:47
 */
@Controller
@RequestMapping("/channelAccountAnalyse")
public class ChannelAccountAnalyseController extends BaseController {

	@Autowired
	private IChannelAccountAnalyseService channelAccountAnalyseService;
	
    private String PREFIX = "/channelAccountAnalyse/";

    /**
     * 跳转到通道结算账单首页
     */
    @RequestMapping("")
    public String index() {
        return PREFIX + "channelAccountAnalyse.html";
    }

    /**
     * 跳转到添加通道结算账单
     */
    @RequestMapping("/channelAccountAnalyse_add")
    public String channelAccountAnalyseAdd() {
        return PREFIX + "channelAccountAnalyse_add.html";
    }

    /**
     * 跳转到修改通道结算账单
     */
    @RequestMapping("/channelAccountAnalyse_update/{channelAccountAnalyseId}")
    public String channelAccountAnalyseUpdate(@PathVariable Integer channelAccountAnalyseId, Model model) {
        return PREFIX + "channelAccountAnalyse_edit.html";
    }

    /**
     * 获取通道结算账单列表
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/list") 
    @ResponseBody
    public Object list(Page<ChannelAccountAnalyse> page) {
    	
		try {
			List result = channelAccountAnalyseService.pageQuery(page);
			//分页结果
			Page<ChannelAccountAnalyse> list = (Page<ChannelAccountAnalyse>) result.get(0);
			
			PageInfoBT<ChannelAccountAnalyse> resultPage = new  PageInfoBT<>(list.getDatas(), list.getTotal());
			
/*			Map<String, Object> modelMap = new HashMap(3);
			modelMap.put("records", Integer.valueOf(list.getTotal()));
			modelMap.put("rows", list.getDatas());
			modelMap.put("page", list.getPage());
			modelMap.put("total", Integer.valueOf(list.getTotalPage()));
			modelMap.put("success", Boolean.valueOf(true));
			//合计结果
			Map<String, Object> sumMap = (Map<String, Object>) result.get(1);
			modelMap.put("userdata", sumMap);*/
			return resultPage;
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		return null;
    }

    /**
     * 新增通道结算账单
     */
    @RequestMapping(value = "/add")
    @ResponseBody
    public Object add() {
        return super.SUCCESS_TIP;
    }

    /**
     * 删除通道结算账单
     */
    @RequestMapping(value = "/delete")
    @ResponseBody
    public Object delete() {
        return SUCCESS_TIP;
    }


    /**
     * 修改通道结算账单
     */
    @RequestMapping(value = "/update")
    @ResponseBody
    public Object update() {
        return super.SUCCESS_TIP;
    }

    /**
     * 通道结算账单详情
     */
    @RequestMapping(value = "/detail")
    @ResponseBody
    public Object detail() {
        return null;
    }
}
