package com.yzx.flow.modular.flowOrder.Controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.yzx.flow.common.controller.BaseController;
import com.yzx.flow.common.excel.TemplateExcel;
import com.yzx.flow.common.excel.TemplateExcelManager;
import com.yzx.flow.common.exception.BizExceptionEnum;
import com.yzx.flow.common.exception.BussinessException;
import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.page.PageInfoBT;
import com.yzx.flow.common.persistence.model.FlowOrderInfo;
import com.yzx.flow.common.util.Constant;
import com.yzx.flow.common.util.DateUtil;
import com.yzx.flow.common.util.FileDownLoadUtils;
import com.yzx.flow.core.portal.PortalParamSetter;
import com.yzx.flow.core.portal.annotation.AutoSetPortalCustomer;
import com.yzx.flow.core.portal.annotation.PortalParamMeta;
import com.yzx.flow.core.util.ToolUtil;
import com.yzx.flow.modular.flowOrder.Service.IFlowOrderInfoService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
/**
 * 流量订单控制器
 *
 * @author liuyufeng
 * @Date 2017-08-24 15:36:04
 */
@Controller
@RequestMapping("/flowOrderInfo")
public class FlowOrderInfoController extends BaseController {

	private static final Logger LOG = LoggerFactory.getLogger(FlowOrderInfoController.class);
	
    private String PREFIX = "/flowOrder/flowOrderInfo/";

    @Autowired
    private IFlowOrderInfoService  flowOrderInfoService;
    /**
     * 跳转到流量订单首页
     */
    @RequestMapping("")
    public String index(Model model) {
    	model.addAttribute("nowStartTime", DateUtil.dateToDateString(new Date(), "yyyy-MM-dd 00:00:00"));
    	model.addAttribute("nowEndTime", DateUtil.dateToDateString(new Date(), "yyyy-MM-dd 23:59:59"));
        return PREFIX + "flowOrderInfo.html";
    }

    /**
     * 跳转到流量订单首页
     */
    @RequestMapping("/detail")
    public String detail(@RequestParam(name="orderId") String orderId,Model model) {
    	model.addAttribute("orderId", orderId);
        return PREFIX + "flowOrderInfo_view.html";
    }

    
    

    @RequestMapping(value = "/list")
	@ResponseBody
	@AutoSetPortalCustomer({ @PortalParamMeta(setter = PortalParamSetter.PAGE)})
	public PageInfoBT<FlowOrderInfo> pageQuery(Page<FlowOrderInfo> page) throws Exception {
    	if(page.getParams().get("createStartTime")==null){
    		page.getParams().put("createStartTime", DateUtil.dateToDateString(new Date(), "yyyy-MM-dd 00:00:00"));
    	}
    	if(page.getParams().get("createEndTime")==null){
    		page.getParams().put("createEndTime", DateUtil.dateToDateString(new Date(), "yyyy-MM-dd 23:59:59"));
    	}
		LOG.info("分页查询流量分发记录:" + page.toString());
		PageInfoBT<FlowOrderInfo> resPage = flowOrderInfoService.pageQuery(page);
		return resPage;
	}
    
    @RequestMapping(value="/refundPage")
    public String  refund(@RequestParam(name="orderId") String orderId,Model model){
    	model.addAttribute("orderId", orderId);
    	return PREFIX + "flowOrderRefundPage.html";
    }
    
    
    @RequestMapping(value = "/queryForRefund")
	@ResponseBody
	public PageInfoBT<FlowOrderInfo> queryForRefund(Page<FlowOrderInfo> page) throws Exception {
		return  flowOrderInfoService.pageQueryForRefund(page);
	}
	@RequestMapping(value = "/getTotalPrice")
	@ResponseBody
	public Map<String, Object> getTotalPrice(Page<FlowOrderInfo> page) throws Exception {
		FlowOrderInfo flowOrderInfo = flowOrderInfoService.pageQueryByPrice(page);
		Map<String, Object>  resMap=new HashMap<String, Object>();
		resMap.put("success", Boolean.valueOf(true));
		resMap.put("data", flowOrderInfo);
		return resMap;
	}
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ResponseBody
	public Object add( FlowOrderInfo data) {
		flowOrderInfoService.saveAndUpdate(data);
		return SUCCESS_TIP;
	}
	
	
	@RequestMapping(value = "/refund")
	@ResponseBody
	public Object refund(String orderId, String remark) throws Exception {
		flowOrderInfoService.refund(orderId, remark);
		return SUCCESS_TIP;
	}
	
	@RequestMapping(value = "/reCallBack")
	@ResponseBody
	public Object reCallBack(String orderId) throws Exception {
		flowOrderInfoService.reCallBack(orderId);
		return SUCCESS_TIP;
	}
	@RequestMapping(value = "/reFailBack")
	@ResponseBody
	public Object reFailBack(String orderId, String operate) throws Exception {
		if ("0".equals(operate)) {// 置成功
			flowOrderInfoService.reSuccessBack(orderId);
		} else {
			flowOrderInfoService.reFailBack(orderId);
		}
		return SUCCESS_TIP;
	}
	@RequestMapping(value = "/reSend")
	@ResponseBody
	public Object reSend(String orderId) throws Exception {
		flowOrderInfoService.reSend(orderId);
		return SUCCESS_TIP;
	}
	
	@RequestMapping(value = "/get")
	@ResponseBody
	public Map<String, Object> get(@RequestParam(name="orderId")String orderId) {
		if (ToolUtil.isEmpty(orderId)) {
			throw new BussinessException(BizExceptionEnum.REQUEST_INVALIDATE);
		}
		Map<String, Object> resMap =new HashMap<String, Object>();
		FlowOrderInfo data = flowOrderInfoService.get(Long.parseLong(orderId));
		resMap.put("success", Boolean.valueOf(true));
		resMap.put("data", data);
		return resMap;
	}
	
	
	/**
	 * 查询可下载的账单
	 * 
	 * @param response
	 * @param dateStr
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/listReports")
	@ResponseBody
	public Map<String, Object> listReports(HttpServletResponse response, String type) throws Exception {
		List<String> reportDates = new ArrayList<String>();
		Map<String, Object> resMap =new HashMap<String, Object>();
 		File reportsDir = new File(Constant.REPORTS_PATH_BASE);
		if (reportsDir.exists()) {
			File[] reports = reportsDir.listFiles();
			// 天账单
			if ("day".equals(type)) {
				for (File report : reports) {
					// YYYYmmDD 天账单
					if (report.getName().length() == 8) {
						reportDates.add(report.getName());
					}
				}
			} else if ("month".equals(type)) {
				for (File report : reports) {
					// YYYYmm 天账单
					if (report.getName().length() ==6) {
						reportDates.add(report.getName());
					}
				}
			}
		}
		Collections.sort(reportDates);
		Collections.reverse(reportDates);
		resMap.put("success", Boolean.valueOf(true));
		resMap.put("data", reportDates);
		return resMap;
	}
	
	/**
	 * 下载账单
	 * 
	 * @param response
	 * @param dateStr
	 * @throws Exception
	 */
	@RequestMapping(value = "/downloadReport")
	public void downloadReport(HttpServletRequest request,HttpServletResponse response, String dateStr) throws Exception {
		String pathname = Constant.getReportFilePath(dateStr, "admin");
		FileDownLoadUtils.downloadFile(request,response, new File(pathname));
	}

	/**
	 * 导出网关数据
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/export")
	public void export(HttpServletResponse response, Page<FlowOrderInfo> page) throws Exception {
		try {
			// 生成Excel
			Map<String, Object> beanMap = new HashMap<String, Object>();
			page.setRows(65530);
			page = flowOrderInfoService.export(page);
			beanMap.put("list", page.getDatas());
			TemplateExcelManager.getInstance().createExcelFileAndDownload(TemplateExcel.FLOWORDERINFO_TEMPLATE, beanMap);
		} catch (IOException e) {
			LOG.error("导出报表出错！！！", e);
		}
	}
	
	
}
