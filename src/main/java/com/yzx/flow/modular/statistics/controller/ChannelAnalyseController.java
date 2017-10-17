package com.yzx.flow.modular.statistics.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yzx.flow.common.controller.BaseController;
import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.page.PageInfoBT;
import com.yzx.flow.common.persistence.model.ChannelAnalyse;
import com.yzx.flow.modular.statistics.service.IChannelAnalyseService;

/**
 * 上游通道分析控制器
 *
 * @author liuyufeng
 * @Date 2017-08-16 15:50:38
 */
@Controller
@RequestMapping("/channelAnalyse")
public class ChannelAnalyseController extends BaseController {
	
	@Autowired
	private IChannelAnalyseService channelAnalyseService;

    private String PREFIX = "/channelAnalyse/";

    /**
     * 跳转到上游通道分析首页
     */
    @RequestMapping("")
    public String index() {
        return PREFIX + "channelAnalyse.html";
    }

    /**
     * 跳转到添加上游通道分析
     */
    @RequestMapping("/channelAnalyse_add")
    public String channelAnalyseAdd() {
        return PREFIX + "channelAnalyse_add.html";
    }

    /**
     * 跳转到修改上游通道分析
     */
    @RequestMapping("/channelAnalyse_update/{channelAnalyseId}")
    public String channelAnalyseUpdate(@PathVariable Integer channelAnalyseId, Model model) {
        return PREFIX + "channelAnalyse_edit.html";
    }

    /**
     * 获取上游通道分析列表
     */
    @RequestMapping(value = "/list")
    @ResponseBody
    public Object list(Page<ChannelAnalyse> page) {

		PageInfoBT<ChannelAnalyse> resultPage = channelAnalyseService.pageQuery(page);

		return resultPage;
    }

    /**
     * 新增上游通道分析
     */
    @RequestMapping(value = "/add")
    @ResponseBody
    public Object add() {
        return super.SUCCESS_TIP;
    }

    /**
     * 删除上游通道分析
     */
    @RequestMapping(value = "/delete")
    @ResponseBody
    public Object delete() {
        return SUCCESS_TIP;
    }


    /**
     * 修改上游通道分析
     */
    @RequestMapping(value = "/update")
    @ResponseBody
    public Object update() {
        return super.SUCCESS_TIP;
    }

    /**
     * 上游通道分析详情
     */
    @RequestMapping(value = "/detail")
    @ResponseBody
    public Object detail() {
        return null;
    }
}
