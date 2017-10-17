package com.yzx.flow.modular.statistics.controller;

import java.util.List;
import java.util.Map;

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
import com.yzx.flow.common.persistence.model.ProfitInfo;
import com.yzx.flow.common.util.DateUtil;
import com.yzx.flow.modular.statistics.service.IProfitStatisticsService;

/**
 * 上游通道每月消费额度控制器
 *
 * @author liuyufeng
 * @Date 2017-08-16 14:59:11
 */
@Controller
@RequestMapping("/monthlyConsumptionQuota")
public class MonthlyConsumptionQuotaController extends BaseController {
	
    private static Logger logger = Logger.getLogger(MonthlyConsumptionQuotaController.class);

    private String PREFIX = "/monthlyConsumptionQuota/";
    
	@Autowired
	private IProfitStatisticsService profitStatisticsService;

    /**
     * 跳转到上游通道每月消费额度首页
     */
    @RequestMapping("")
    public String index() {
        return PREFIX + "monthlyConsumptionQuota.html";
    }

    /**
     * 跳转到添加上游通道每月消费额度
     */
    @RequestMapping("/monthlyConsumptionQuota_add")
    public String monthlyConsumptionQuotaAdd() {
        return PREFIX + "monthlyConsumptionQuota_add.html";
    }

    /**
     * 跳转到修改上游通道每月消费额度
     */
    @RequestMapping("/monthlyConsumptionQuota_update/{monthlyConsumptionQuotaId}")
    public String monthlyConsumptionQuotaUpdate(@PathVariable Integer monthlyConsumptionQuotaId, Model model) {
        return PREFIX + "monthlyConsumptionQuota_edit.html";
    }

    /**
     * 获取上游通道每月消费额度列表
     */
    @RequestMapping(value = "/list")
    @ResponseBody
    public Object list(ProfitInfo profitInfo) {

    	Page<ProfitInfo> page = getPageInfo(JSONObject.parseObject(JSONObject.toJSONString(profitInfo), Map.class));

        try {
            String month = (String)page.getParams().get("month");
            if (StringUtils.isEmpty(month)) {
                page.getParams().put("startTime", DateUtil.formatStrDate(DateUtil.getCurMonthFirstDay(), DateUtil.YYYY_MM_DD_EN));
            } else {
                int yearParam = Integer.valueOf(month.split("-")[0]);
                int monthParam = Integer.valueOf(month.split("-")[1]) - 1;
                page.getParams().put("startTime", DateUtil.getFirstDayOfMonth(yearParam, monthParam));
            }
            List<ProfitInfo> profitList = profitStatisticsService.getMonthlyConsumptionQuota(page);
            return profitList;
        } catch (Exception e) {
            logger.error("上游通道每月消费额度统计异常：" + e.getMessage(), e);
        }
        return null;
    }

    /**
     * 新增上游通道每月消费额度
     */
    @RequestMapping(value = "/add")
    @ResponseBody
    public Object add() {
        return super.SUCCESS_TIP;
    }

    /**
     * 删除上游通道每月消费额度
     */
    @RequestMapping(value = "/delete")
    @ResponseBody
    public Object delete() {
        return SUCCESS_TIP;
    }


    /**
     * 修改上游通道每月消费额度
     */
    @RequestMapping(value = "/update")
    @ResponseBody
    public Object update() {
        return super.SUCCESS_TIP;
    }

    /**
     * 上游通道每月消费额度详情
     */
    @RequestMapping(value = "/detail")
    @ResponseBody
    public Object detail() {
        return null;
    }
}
