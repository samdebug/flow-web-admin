package com.yzx.flow.modular.flowOrder.Controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yzx.flow.common.constant.tips.ErrorTip;
import com.yzx.flow.common.excel.TemplateExcel;
import com.yzx.flow.common.excel.TemplateExcelManager;
import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.page.PageInfoBT;
import com.yzx.flow.common.persistence.model.AccessChannelGroup;
import com.yzx.flow.common.persistence.model.ExportAppInfo;
import com.yzx.flow.common.persistence.model.FlowAppInfo;
import com.yzx.flow.common.persistence.model.FlowOrderInfo;
import com.yzx.flow.modular.flowOrder.Service.IFlowAppInfoService;
import com.yzx.flow.modular.flowOrder.Service.IFlowOrderInfoService;

/**
 * 
 * <b>Title：</b>FlowAppInfoController.java<br/>
 * <b>Description：</b> <br/>
 * <b>@author： </b>v5480<br/>
 * <b>@date：</b>2015-07-29 11:02:41<br/>
 * <b>Copyright (c) 2015 szwisdom Tech.</b>
 * 
 */
@Controller
@RequestMapping("/flowAppInfo")
public class FlowAppInfoController extends com.yzx.flow.common.controller.BaseController {
	private static final Logger LOG = LoggerFactory.getLogger(FlowAppInfoController.class);
	@Autowired
	@Qualifier("flowAppInfoService")
	private IFlowAppInfoService flowAppInfoService;

	@Autowired
	@Qualifier("flowOrderInfoService")
	private IFlowOrderInfoService flowOrderInfoService;
	
	private String PREFIX = "/flowApp/";
	
	
	
	/**
	 * 跳转至 app接入列表页面
	 * @return
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String toList() {
		return String.format("%s%s", PREFIX, "flowApp.html");
	}
	
	/**
	 * 跳转到接入新增页
	 * @return
	 */
	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String toAdd() {
		return String.format("%s%s", PREFIX, "flowApp_edit.html");
	}
	
	/**
	 * 跳转到接入编辑页
	 * @return
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public String toEdit() {
		return String.format("%s%s", PREFIX, "flowApp_edit.html");
	}
	
	
	/**
	 * 跳转到接入详情
	 * @return
	 */
	@RequestMapping(value = "/detail", method = RequestMethod.GET)
	public String toDetail() {
		return String.format("%s%s", PREFIX, "flowApp_view.html");
	}
	
	
	@RequestMapping(value = "/query")
	@ResponseBody
	public PageInfoBT<FlowAppInfo> pageQuery(Page<FlowAppInfo> page) {
		PageInfoBT<FlowAppInfo> respage = flowAppInfoService.pageQuery(page);
		return respage;
	}

	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ResponseBody
	public Object add(@ModelAttribute("flowAppInfo") FlowAppInfo data) {
		flowAppInfoService.saveAndUpdate(data);
		return SUCCESS_TIP;
	}

	@RequestMapping(value = "/delete")
	@ResponseBody
	public Object delete(Long flowAppId) {
	    // 是否有网关日志（flow_order_info）
	    List<FlowOrderInfo> flowOrderInfos = flowOrderInfoService.selectByFlowAppId(flowAppId);
	    if (!flowOrderInfos.isEmpty()) {
	        return ErrorTip.buildErrorTip("当前App接入信息仍然有网关下发日志，不可删除");
	    }
		flowAppInfoService.delete(flowAppId);
		return SUCCESS_TIP;
	}

	@RequestMapping(value = "/get")
	@ResponseBody
	public Object get(Long flowAppId) {
		FlowAppInfo data = flowAppInfoService.get(flowAppId);
		return data;
	}
	
	/**
     * 修改接入状态
     * @return
     */
    @RequestMapping(value = "/changeStatus")
    @ResponseBody
    public Object changeStatus(Long flowAppId, Integer status) {
        FlowAppInfo data = flowAppInfoService.get(flowAppId);
        if (status < 1 || status > 3) {
            return ErrorTip.buildErrorTip("非法操作");
        }
        data.setStatus(String.valueOf(status));
        flowAppInfoService.saveAndUpdate(data);
        return SUCCESS_TIP;
    }

	/**
	 * 更新的时候需额外传递updId,值跟主键值一样,被@ModelAttribute注释的方法会在此controller每个方法执行前被执行，
	 * 要谨慎使用
	 */
	@ModelAttribute("flowAppInfo")
	public void getForUpdate(@RequestParam(value = "updId", required = false) Long updId, Model model) {
		if (null != updId) {
			model.addAttribute("flowAppInfo", flowAppInfoService.get(updId));
		} else {
			model.addAttribute("flowAppInfo", new FlowAppInfo());
		}
	}
	
	@RequestMapping(value = "/getDispatchChannel")
	@ResponseBody
	public Map<String, Object> getDispatchChannelList(String dispatchChannel) {
	    List<AccessChannelGroup> accessChannelGroups = flowAppInfoService.getDispatchChannelList(dispatchChannel);
	    return success(accessChannelGroups);
	}

	@RequestMapping(value = "/selectInfoByIdOrName")
	@ResponseBody
	public Map<String, Object> selectInfoByIdOrName(String idOrName) {
        Map<String, Object> map= new HashMap<String, Object>();
        List<FlowAppInfo> list = new ArrayList<FlowAppInfo>();
        list = flowAppInfoService.selectInfoByIdOrName(idOrName);
        map.put("flowAppInfoList", list);
        return map;
	}
	
	
	/**
	 * APP接入信息导出
	 * @param request
	 * @param response
	 * @param flowAppId
	 */
	@RequestMapping(value = "/excel")
	@ResponseBody
	public void downLoadAppInfo(@RequestParam("flowAppId") Long flowAppId) {
		try {
			//APPINFO信息
			FlowAppInfo appInfo = flowAppInfoService.get(flowAppId);
			//订单对应的产品
			List<ExportAppInfo> product = flowAppInfoService.getProductListByOrderId(appInfo.getOrderId(), appInfo.getAppId());
			/*for (ExportAppInfo flowProductInfo : product) {
				if(flowProductInfo.getZone().equals("00")){
					flowProductInfo.setZone("全国");
				}
				if(flowProductInfo.getZone().equals("44")){
					flowProductInfo.setZone("广东");
				}
			}*/
			Map<String, Object> beanParams = new HashMap<String, Object>();
			beanParams.put("appInfo", appInfo);
			beanParams.put("productList", product);
			TemplateExcelManager.getInstance().createExcelFileAndDownload(TemplateExcel.FLOW_APP_INFO_LIST, beanParams);
		}catch(Exception e){
			LOG.error("导出 [ APP接入信息 ] 出错！！！");
			LOG.error(e.getMessage(),e);
		}
	}
	
	
	
	private <T>Map<String, Object> success(T data){
		Map<String,Object> resMap =new HashMap<String, Object>();
		resMap.put("success", Boolean.valueOf(true));
		resMap.put("data", data);
		return resMap;
	}
	
	private Map<String, Object> fail(String message){
		Map<String,Object> resMap =new HashMap<String, Object>();
		resMap.put("success", Boolean.valueOf(false));
		resMap.put("message", message);
		return resMap;
	}
}