package com.yzx.flow.modular.flowOrder.Controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.yzx.flow.common.constant.tips.ErrorTip;
import com.yzx.flow.common.controller.BaseController;
import com.yzx.flow.common.excel.TemplateExcel;
import com.yzx.flow.common.excel.TemplateExcelManager;
import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.page.PageInfoBT;
import com.yzx.flow.common.persistence.model.FlowOrderInfo;
import com.yzx.flow.modular.flowOrder.Service.IFlowOrderInfoExceptionService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 流量分发异常订单记录控制器
 *
 * @author liuyufeng
 * @Date 2017-08-24 09:52:15
 */
@Controller
@RequestMapping("/flowOrderInfoException")
public class FlowOrderInfoExceptionController extends BaseController {

	private static final Logger LOG = LoggerFactory.getLogger(FlowOrderInfoExceptionController.class);
	
	@Autowired
	IFlowOrderInfoExceptionService flowOrderInfoExceptionService;
	
    private String PREFIX = "/flowOrder/flowOrderInfoException/";

    /**
     * 跳转到流量分发异常订单记录首页
     */
    @RequestMapping("")
    public String index() {
        return PREFIX + "flowOrderInfoException.html";
    }


    /**
     * 获取流量分发异常订单记录列表
     */
    @RequestMapping(value = "/list")
    @ResponseBody
    public PageInfoBT<FlowOrderInfo> list(Page<FlowOrderInfo> page) {
		LOG.info("分页查询流量分发记录:" + page.toString());
		return flowOrderInfoExceptionService.pageQuery(page);
    }

    
    @RequestMapping(value = "/reFailBack")
	@ResponseBody
	public Object reFailBack(String orderId) throws Exception {
		if(flowOrderInfoExceptionService.reFailBack(orderId))
			return SUCCESS_TIP;
		else
			return new ErrorTip(0, "操作失败!");
	}

    
    /**
	 * 导出网关数据
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/export")
	public void export(HttpServletResponse response, Page<FlowOrderInfo> page) throws Exception {
		try {
			// 生成Excel
			Map<String, Object> beanMap = new HashMap<String, Object>();
			page.setRows(10000);
			beanMap.put("list", flowOrderInfoExceptionService.pageQuery(page).getRows());
			beanMap.put("startDate", page.getParams().get("createStartTime"));
			beanMap.put("endDate", page.getParams().get("createEndTime"));
			TemplateExcelManager.getInstance().createExcelFileAndDownload(TemplateExcel.FLOWORDERINFOEx_TEMPLATE, beanMap);
		} catch (IOException e) {
			LOG.error("导出报表出错！！！", e);
		}
	}
    
    /**
     * 删除流量分发异常订单记录
     */
    @RequestMapping(value = "/delete")
    @ResponseBody
    public Object delete() {
        return SUCCESS_TIP;
    }


    /**
     * 流量分发异常订单记录详情
     */
    @RequestMapping(value = "/detail")
    @ResponseBody
    public Object detail() {
        return null;
    }
}
