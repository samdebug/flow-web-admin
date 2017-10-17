package com.yzx.flow.modular.statistics.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
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
import com.yzx.flow.common.persistence.model.SupplierFlowPack;
import com.yzx.flow.modular.statistics.service.ISupplierFlowPackService;

/**
 * 供应商流量包对账明细控制器
 *
 * @author liuyufeng
 * @Date 2017-08-15 10:20:51
 */
@Controller
@RequestMapping("/supplierFlowPack")
public class SupplierFlowPackController extends BaseController {

    private String PREFIX = "/supplierFlowPack/";

    private static Logger logger = Logger.getLogger(SupplierFlowPackController.class);
    
    @Autowired
    private  ISupplierFlowPackService supplierFlowPackService;
    /**
     * 跳转到供应商流量包对账明细首页
     */
    @RequestMapping("")
    public String index() {
        return PREFIX + "supplierFlowPack.html";
    }

    /**
     * 跳转到添加供应商流量包对账明细
     */
    @RequestMapping("/supplierFlowPack_add")
    public String supplierFlowPackAdd() {
        return PREFIX + "supplierFlowPack_add.html";
    }

    /**
     * 跳转到修改供应商流量包对账明细
     */
    @RequestMapping("/supplierFlowPack_update/{supplierFlowPackId}")
    public String supplierFlowPackUpdate(@PathVariable Integer supplierFlowPackId, Model model) {
        return PREFIX + "supplierFlowPack_edit.html";
    }

    /**
     * 获取供应商流量包对账明细列表
     */
    @RequestMapping(value = "/list")
    @ResponseBody
    public Object list(Page<SupplierFlowPack> page) {
    	logger.info("供应商流量包统计记录分页查询:" + page.toString());
    	
    	PageInfoBT<SupplierFlowPack> resultPage = null;
		try {
			resultPage = supplierFlowPackService.pageQuery(page);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
        return resultPage;
    }

	/**
     * 新增供应商流量包对账明细
     */
    @RequestMapping(value = "/add")
    @ResponseBody
    public Object add() {
        return super.SUCCESS_TIP;
    }

    /**
     * 删除供应商流量包对账明细
     */
    @RequestMapping(value = "/delete")
    @ResponseBody
    public Object delete() {
        return SUCCESS_TIP;
    }


    /**
     * 修改供应商流量包对账明细
     */
    @RequestMapping(value = "/update")
    @ResponseBody
    public Object update() {
        return super.SUCCESS_TIP;
    }

    /**
     * 供应商流量包对账明细详情
     */
    @RequestMapping(value = "/detail")
    @ResponseBody
    public Object detail() {
        return null;
    }
}
