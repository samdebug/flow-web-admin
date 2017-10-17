package com.yzx.flow.modular.statistics.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yzx.flow.common.controller.BaseController;
import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.page.PageInfoBT;
import com.yzx.flow.common.persistence.model.CustomerStatisticsDay;
import com.yzx.flow.modular.statistics.service.ICustomerAccountAnalyseService;

/**
 * 客户结算账单控制器
 *
 * @author liuyufeng
 * @Date 2017-08-21 15:17:23
 */
@Controller
@RequestMapping("/customerAccountAnalyse")
public class CustomerAccountAnalyseController extends BaseController {
	
    private static Logger logger = Logger.getLogger(CustomerAccountAnalyseController.class);
	@Autowired
	private ICustomerAccountAnalyseService customerAccountAnalyseService;
	
    private String PREFIX = "/customerAccountAnalyse/";

    /**
     * 跳转到客户结算账单首页
     */
    @RequestMapping("")
    public String index() {
        return PREFIX + "customerAccountAnalyse.html";
    }

    /**
     * 跳转到添加客户结算账单
     */
    @RequestMapping("/customerAccountAnalyse_add")
    public String customerAccountAnalyseAdd() {
        return PREFIX + "customerAccountAnalyse_add.html";
    }

    /**
     * 跳转到修改客户结算账单
     */
    @RequestMapping("/customerAccountAnalyse_update/{customerAccountAnalyseId}")
    public String customerAccountAnalyseUpdate(@PathVariable Integer customerAccountAnalyseId, Model model) {
        return PREFIX + "customerAccountAnalyse_edit.html";
    }

    /**
     * 获取客户结算账单列表
     */
    @RequestMapping(value = "/list")
    @ResponseBody
    public Object list(Page<CustomerStatisticsDay> page) {
		logger.info("分页查询流量分发记录:" + page.toString());
		try {
			PageInfoBT<CustomerStatisticsDay> resultPage = customerAccountAnalyseService.pageQuery(page);
			return resultPage;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
    }

    /**
     * 新增客户结算账单
     */
    @RequestMapping(value = "/add")
    @ResponseBody
    public Object add() {
        return super.SUCCESS_TIP;
    }

    /**
     * 删除客户结算账单
     */
    @RequestMapping(value = "/delete")
    @ResponseBody
    public Object delete() {
        return SUCCESS_TIP;
    }


    /**
     * 修改客户结算账单
     */
    @RequestMapping(value = "/update")
    @ResponseBody
    public Object update() {
        return super.SUCCESS_TIP;
    }

    /**
     * 客户结算账单详情
     */
    @RequestMapping(value = "/detail")
    @ResponseBody
    public Object detail() {
        return null;
    }
}
