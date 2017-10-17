package com.yzx.flow.modular.flowWarn.controller;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.yzx.flow.common.controller.BaseController;
import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.persistence.model.FlowWarnMsg;
import com.yzx.flow.modular.flowWarn.service.IFlowWarnMsgService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 流量告警信息控制器
 *
 * @author max
 * @Date 2017-08-16 20:07:54
 * @msg 暂时只实现查询流量告警信息列表的功能
 */
@Controller
@RequestMapping("/flowWarnMsg")
public class FlowWarnMsgController extends BaseController {

	private String PREFIX = "/flowWarn/flowWarnMsg/";

	@Autowired
	private IFlowWarnMsgService flowWarnMsgService;

	/**
	 * 跳转到流量告警信息首页
	 */
	@RequestMapping("")
	public String index() {
		return PREFIX + "flowWarnMsg.html";
	}

	/**
	 * 跳转到添加流量告警信息
	 */
	@RequestMapping("/flowWarnMsg_add")
	public String flowWarnMsgAdd() {
		return PREFIX + "flowWarnMsg_add.html";
	}

	/**
	 * 跳转到修改流量告警信息
	 */
	@RequestMapping("/flowWarnMsg_update/{flowWarnMsgId}")
	public String flowWarnMsgUpdate(@PathVariable Integer flowWarnMsgId,
			Model model) {

		// FlowWarnConf flowWarnConf =
		// flowWarnConfService.get(Long.valueOf(warnConfId));
		// model.addAttribute("flowWarnConf",flowWarnConf);

		return PREFIX + "flowWarnMsg_edit.html";
	}

	/**
	 * 获取流量告警信息列表
	 */
	@RequestMapping(value = "/list")
	@ResponseBody
	public List<FlowWarnMsg> list(FlowWarnMsg flowWarnMsg) {
		Page<FlowWarnMsg> page = getPageInfo(JSONObject.parseObject(
				JSONObject.toJSONString(flowWarnMsg), Map.class));

		List<FlowWarnMsg> list = flowWarnMsgService.pageQuery(page);

		return list;
	}

	/**
	 * 新增流量告警信息
	 */
	@RequestMapping(value = "/add")
	@ResponseBody
	public Object add() {
		return SUCCESS_TIP;
	}

	/**
	 * 删除流量告警信息
	 */
	@RequestMapping(value = "/delete")
	@ResponseBody
	public Object delete() {
		return SUCCESS_TIP;
	}

	/**
	 * 修改流量告警信息
	 */
	@RequestMapping(value = "/update")
	@ResponseBody
	public Object update() {
		return SUCCESS_TIP;
	}

	/**
	 * 流量告警信息详情
	 */
	@RequestMapping(value = "/detail")
	@ResponseBody
	public Object detail() {
		return null;
	}
}
