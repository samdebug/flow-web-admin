package com.yzx.flow.modular.flowWarn.controller;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.yzx.flow.common.controller.BaseController;
import com.yzx.flow.common.exception.BizExceptionEnum;
import com.yzx.flow.common.exception.BussinessException;
import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.persistence.model.FlowWarnConf;
import com.yzx.flow.modular.flowWarn.service.IFlowWarnConfService;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 流量告警配置控制器
 *
 * @author max
 * @Date 2017-08-15 14:30:00
 */
@Controller
@RequestMapping("/flowWarnConf")
public class FlowWarnConfController extends BaseController {

	private static Logger logger = Logger
			.getLogger(FlowWarnConfController.class);

	private String PREFIX = "/flowWarn/flowWarnConf/";

	@Autowired
	private IFlowWarnConfService flowWarnConfService;

	/**
	 * 跳转到流量告警配置首页
	 */
	@RequestMapping("")
	public String index() {
		return PREFIX + "flowWarnConf.html";
	}

	/**
	 * 跳转到添加流量告警配置
	 */
	@RequestMapping("/flowWarnConf_add")
	public String flowWarnConfAdd() {
		return PREFIX + "flowWarnConf_add.html";
	}

	/**
	 * 跳转到修改流量告警配置
	 */
	@RequestMapping("/flowWarnConf_update/{warnConfId}")
	public String flowWarnConfUpdate(@PathVariable String warnConfId,
			Model model) {
		FlowWarnConf flowWarnConf = flowWarnConfService.get(Long
				.valueOf(warnConfId));
		model.addAttribute("flowWarnConf", flowWarnConf);

		return PREFIX + "flowWarnConf_edit.html";
	}

	/**
	 * 获取流量告警配置列表
	 */
	@RequestMapping(value = "/list")
	@ResponseBody
	public Object list(FlowWarnConf flowWarnConf) {

		Page<FlowWarnConf> page = getPageInfo(JSONObject.parseObject(
				JSONObject.toJSONString(flowWarnConf), Map.class));

		List<FlowWarnConf> list = flowWarnConfService.pageQuery(page);

		return list;
	}

	/**
	 * 新增流量告警配置
	 */
	@RequestMapping(value = "/add")
	@ResponseBody
	public Object add(FlowWarnConf flowWarnConf) {
		try {
			flowWarnConfService.saveAndUpdate(flowWarnConf);
		} catch (Exception e) {
			logger.error("新增流量告警配置错误", e);
			throw new BussinessException(BizExceptionEnum.SERVER_ERROR);
		}
		return SUCCESS_TIP;
	}

	/**
	 * 删除流量告警配置
	 */
	@RequestMapping(value = "/delete")
	@ResponseBody
	public Object delete(long warnConfId) {
		flowWarnConfService.delete(warnConfId);
		return SUCCESS_TIP;
	}

	/**
	 * 修改流量告警配置
	 */
	@RequestMapping(value = "/update")
	@ResponseBody
	public Object update(FlowWarnConf flowWarnConf) {
		try {
			flowWarnConfService.saveAndUpdate(flowWarnConf);
		} catch (Exception e) {
			logger.error("修改流量告警配置", e);
			throw new BussinessException(BizExceptionEnum.SERVER_ERROR);
		}
		return SUCCESS_TIP;
	}

	/**
	 * 流量告警配置详情
	 */
	@RequestMapping(value = "/detail")
	@ResponseBody
	public Object detail() {
		return null;
	}
}
