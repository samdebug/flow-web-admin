package com.yzx.flow.modular.statistics.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
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
import com.yzx.flow.common.persistence.model.SuppilerTradeDay;
import com.yzx.flow.modular.statistics.service.IChannelSupplierStatisticsService;

/**
 * 供应商出款数据统计控制器
 *
 * @author liuyufeng
 * @Date 2017-08-15 17:09:08
 */
@Controller
@RequestMapping("/supplierDataStatistical")
public class SupplierDataStatisticalController extends BaseController {

    private static Logger logger = Logger.getLogger(SupplierDataStatisticalController.class);
    
    private String PREFIX = "/supplierDataStatistical/";
    
    @Autowired
    private IChannelSupplierStatisticsService channelSupplierStatisticsService;

    /**
     * 跳转到供应商出款数据统计首页
     */
    @RequestMapping("")
    public String index() {
        return PREFIX + "supplierDataStatistical.html";
    }

    /**
     * 跳转到添加供应商出款数据统计
     */
    @RequestMapping("/supplierDataStatistical_add")
    public String supplierDataStatisticalAdd() {
        return PREFIX + "supplierDataStatistical_add.html";
    }

    /**
     * 跳转到修改供应商出款数据统计
     */
    @RequestMapping("/supplierDataStatistical_update/{supplierDataStatisticalId}")
    public String supplierDataStatisticalUpdate(@PathVariable Integer supplierDataStatisticalId, Model model) {
        return PREFIX + "supplierDataStatistical_edit.html";
    }

    /**
     * 获取供应商出款数据统计列表
     */
    @RequestMapping(value = "/list")
    @ResponseBody
    public Object list(Page<SuppilerTradeDay> page) {

    	PageInfoBT<SuppilerTradeDay> resultPage = channelSupplierStatisticsService.suppilerTradeQuery(page);
		
        return resultPage;
    }

    /**
     * 新增供应商出款数据统计
     */
    @RequestMapping(value = "/add")
    @ResponseBody
    public Object add() {
        return super.SUCCESS_TIP;
    }

    /**
     * 删除供应商出款数据统计
     */
    @RequestMapping(value = "/delete")
    @ResponseBody
    public Object delete() {
        return SUCCESS_TIP;
    }


    /**
     * 修改供应商出款数据统计
     */
    @RequestMapping(value = "/update")
    @ResponseBody
    public Object update() {
        return super.SUCCESS_TIP;
    }

    /**
     * 供应商出款数据统计详情
     */
    @RequestMapping(value = "/detail")
    @ResponseBody
    public Object detail() {
        return null;
    }
}
