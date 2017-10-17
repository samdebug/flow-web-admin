package com.yzx.flow.modular.channel.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import com.yzx.flow.common.constant.tips.ErrorTip;
import com.yzx.flow.common.controller.BaseController;
import com.yzx.flow.common.excel.TemplateExcel;
import com.yzx.flow.common.excel.TemplateExcelManager;
import com.yzx.flow.common.exception.BizExceptionEnum;
import com.yzx.flow.common.exception.BussinessException;
import com.yzx.flow.common.exception.MyException;
import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.page.PageInfoBT;
import com.yzx.flow.common.persistence.model.AccessChannelInfo;
import com.yzx.flow.common.persistence.model.AreaCode;
import com.yzx.flow.common.persistence.model.BaseEntity;
import com.yzx.flow.common.persistence.model.ChannelAdapter;
import com.yzx.flow.common.persistence.model.ChannelProductInfo;
import com.yzx.flow.common.persistence.model.ChannelToGroup;
import com.yzx.flow.common.persistence.model.FlowPackageInfo;
import com.yzx.flow.common.persistence.model.Staff;
import com.yzx.flow.common.util.CommonUtil;
import com.yzx.flow.common.util.Constant;
import com.yzx.flow.common.util.RedisHttpUtil;
import com.yzx.flow.common.util.URLConstants;
import com.yzx.flow.core.shiro.ShiroKit;
import com.yzx.flow.core.shiro.ShiroUser;
import com.yzx.flow.core.util.ToolUtil;
import com.yzx.flow.modular.channel.service.IAccessChannelInfoHisService;
import com.yzx.flow.modular.channel.service.IAccessChannelInfoService;
import com.yzx.flow.modular.channel.service.IChannelProductInfoService;
import com.yzx.flow.modular.channel.service.IChannelSupplierService;
import com.yzx.flow.modular.system.dao.ChannelToGroupDao;
import com.yzx.flow.modular.system.dao.FlowOrderInfoDao;

import net.sf.json.JSONArray;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 接入通道控制器
 *
 * @author liuyufeng
 * @Date 2017-08-15 16:58:36
 */
@Controller
@RequestMapping("/accessChannelInfo")
public class AccessChannelInfoController extends BaseController {

	private static final Logger LOG = LoggerFactory
			.getLogger(AccessChannelInfoController.class);
	@Autowired
	@Qualifier("accessChannelInfoService")
	private IAccessChannelInfoService accessChannelInfoService;

	@Autowired
	@Qualifier("channelProductInfoService")
	private IChannelProductInfoService channelProductInfoService;

	@Autowired
	@Qualifier("channelSupplierService")
	private IChannelSupplierService channelSupplierService;
	@Autowired
	private FlowOrderInfoDao flowOrderInfoMapper;

	@Autowired
	private ChannelToGroupDao channelToGroupMapper;

	@Autowired
	private IAccessChannelInfoHisService accessChannelInfoHisService;// 记录修改日志

	private String PREFIX = "/channel/accessChannelInfo/";

	/**
	 * 跳转到接入通道首页
	 */
	@RequestMapping("")
	public String index() {
		return PREFIX + "accessChannelInfo.html";
	}

	/**
	 * 跳转到添加接入通道
	 */
	@RequestMapping("/accessChannelInfo_add")
	public String accessChannelInfoAdd() {
		return PREFIX + "accessChannelInfo_add.html";
	}

	/**
	 * 跳转到添加接入通道
	 */
	@RequestMapping("/accessChannelInfo_addGroup")
	public String accessChannelInfoAdd(
			@RequestParam(name = "channelSeqId") String channelSeqId,
			Model model) {
		model.addAttribute("channelSeqId", channelSeqId);
		return PREFIX + "accessChannelInfo_toGroup.html";
	}

	/**
	 * 跳转到修改接入通道
	 */
	@RequestMapping("/accessChannelInfo_update/{accessChannelInfoId}")
	public String accessChannelInfoUpdate(
			@PathVariable Integer accessChannelInfoId, Model model) {
		model.addAttribute("channelInfoId", accessChannelInfoId);
		return PREFIX + "accessChannelInfo_edit.html";
	}

	/**
	 * 跳转到详情接入通道
	 */
	@RequestMapping("/detail/{accessChannelInfoId}")
	public String detail(@PathVariable Integer accessChannelInfoId,
			@RequestParam(name = "isShow") String isShow, Model model) {
		if (!ToolUtil.isEmpty(isShow)) {
			model.addAttribute("isShow", Integer.parseInt(isShow));
		}
		model.addAttribute("channelInfoId", accessChannelInfoId);
		return PREFIX + "accessChannelInfo_view.html";
	}

	@RequestMapping(value = "/get")
	@ResponseBody
	public Map<String, Object> get(
			@RequestParam(name = "channelSeqId") String channelSeqId) {
		if (ToolUtil.isEmpty(channelSeqId)) {
			throw new BussinessException(BizExceptionEnum.REQUEST_INVALIDATE);
		}
		AccessChannelInfo data = accessChannelInfoService.get(Long
				.parseLong(channelSeqId));
		List<ChannelProductInfo> channelProductInfos = channelProductInfoService
				.selectByChannelSeqId(Long.parseLong(channelSeqId));
		data.setChannelProductInfoList(channelProductInfos);
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("data", JSON.toJSONString(data));
		resultMap.put("success", 1);
		return resultMap;
	}

	/**
	 * 获取接入通道列表
	 */
	@RequestMapping(value = "/list")
	@ResponseBody
	public Object list(Page<AccessChannelInfo> page) {
		if (page.getSort() == null) {
			page.setSort("create_time");
		}
		PageInfoBT<AccessChannelInfo> pageRes = accessChannelInfoService
				.pageQuery(page);
		return pageRes;
	}

	/**
	 * 新增接入通道
	 */
	@RequestMapping(value = "/add")
	@ResponseBody
	public Object add(AccessChannelInfo accessChannelInfo,
			HttpServletRequest request) {
		ShiroUser user = ShiroKit.getUser();
		Staff staff = new Staff();
		staff.setLoginName(user.getName());
		if (accessChannelInfo.getChannelSeqId() == null) {
			List<AccessChannelInfo> idList = getAccessChannelInfoList(
					accessChannelInfo, Constant.TYPE_CHANNEL_NAME);
			if (!idList.isEmpty()) {
				return new ErrorTip(0, "当前通道ID已存在，请重新输入");
			}
			/*
			 * if (!accessChannelInfo.getDiscount().toString().matches(
			 * "^(0|[1-9]\\d?|100)$")) { return super.fail("请输入0-100之间的折扣"); }
			 */
		} else {
			AccessChannelInfo data = accessChannelInfoService
					.get(accessChannelInfo.getChannelSeqId());
			accessChannelInfo.setSupplierCode(data.getSupplierCode());
			accessChannelInfo.setChannelId(data.getChannelId());
		}
		// 校验
		StringBuilder errorMessage = new StringBuilder();
		if (!isParamCheck(accessChannelInfo, errorMessage)) {
			return new ErrorTip(0, errorMessage.toString());
		}
		setBaseEntity(accessChannelInfo, staff,
				accessChannelInfo.getChannelSeqId());
		accessChannelInfo.setIp(CommonUtil.getIp(request));
		accessChannelInfoService.saveAndUpdate(accessChannelInfo, staff);
		return super.SUCCESS_TIP;
	}

	/**
	 * 删除接入通道
	 */
	@RequestMapping(value = "/delete")
	@ResponseBody
	public Object delete(
			@RequestParam(name = "accessChannelInfoId") String channelSeqId) {
		if (ToolUtil.isEmpty(channelSeqId)) {
			throw new BussinessException(BizExceptionEnum.REQUEST_INVALIDATE);
		}
		ShiroUser user = ShiroKit.getUser();
		AccessChannelInfo data = new AccessChannelInfo();
		data.setUpdator(user.getName());
		data.setUpdateTime(new Date());
		accessChannelInfoService.delete(Long.parseLong(channelSeqId), data);
		return SUCCESS_TIP;
	}

	/**
	 * 修改接入通道
	 */
	@RequestMapping(value = "/update")
	@ResponseBody
	public Object update(AccessChannelInfo accessChannelInfo,
			HttpServletRequest request) {
		ShiroUser user = ShiroKit.getUser();
		Staff staff = new Staff();
		staff.setLoginName(user.getName());
		if (accessChannelInfo.getChannelSeqId() == null) {
			List<AccessChannelInfo> idList = getAccessChannelInfoList(
					accessChannelInfo, Constant.TYPE_CHANNEL_NAME);
			if (!idList.isEmpty()) {
				return new ErrorTip(0, "当前通道ID已存在，请重新输入");
			}
			/*
			 * if (!accessChannelInfo.getDiscount().toString().matches(
			 * "^(0|[1-9]\\d?|100)$")) { return super.fail("请输入0-100之间的折扣"); }
			 */
		} else {
			AccessChannelInfo data = accessChannelInfoService
					.get(accessChannelInfo.getChannelSeqId());
			accessChannelInfo.setSupplierCode(data.getSupplierCode());
			accessChannelInfo.setChannelId(data.getChannelId());
		}
		// 校验
		StringBuilder errorMessage = new StringBuilder();
		if (!isParamCheck(accessChannelInfo, errorMessage)) {
			return new ErrorTip(0, errorMessage.toString());
		}
		setBaseEntity(accessChannelInfo, staff,
				accessChannelInfo.getChannelSeqId());
		accessChannelInfo.setIp(CommonUtil.getIp(request));
		accessChannelInfoService.saveAndUpdate(accessChannelInfo, staff);
		return super.SUCCESS_TIP;
	}

	@RequestMapping("/updatemaintainZone")
	@ResponseBody
	public Object updatemaintainZone(String channelSeqId,String maintainZoneIds ){
		if (ToolUtil.isEmpty(channelSeqId)) {
			return new ErrorTip(0, "非法请求");
		}if (ToolUtil.isEmpty(maintainZoneIds)) {
			return new ErrorTip(0, "非法请求");
		}
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("channelSeqId", channelSeqId);
		params.put("maintainZone", maintainZoneIds);
		LOG.info("维护通道区域：{}", params);
		accessChannelInfoService.updateChannelMaintainZone(params);
		return super.SUCCESS_TIP;
	}
	
	/**
	 * 通道适配器详情
	 */
	@RequestMapping(value = "/detail")
	@ResponseBody
	public String detail(@RequestParam String accessChannelInfoId, Model model) {
		if (ToolUtil.isNotEmpty(accessChannelInfoId)) {
			throw new BussinessException(BizExceptionEnum.REQUEST_INVALIDATE);
		}
		AccessChannelInfo data = accessChannelInfoService.get(Long
				.parseLong(accessChannelInfoId));
		List<ChannelProductInfo> channelProductInfos = channelProductInfoService
				.selectByChannelSeqId(Long.parseLong(accessChannelInfoId));
		data.setChannelProductInfoList(channelProductInfos);
		model.addAttribute("info", data);
		return PREFIX + "accessChannelInfo_edit.html";
	}

	@RequestMapping(value = "/selectAccessChannelInfoList", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> selectAccessChannelInfoList(
			@RequestParam(value = "channelSeqIds", required = false) Long[] channelSeqIds,
			@RequestParam(value = "text", required = false) String text) {
		List<AccessChannelInfo> list = new ArrayList<AccessChannelInfo>();
		list = accessChannelInfoService.selectAllChannelInfo(channelSeqIds,
				text);
		Map<String, Object> modelMap = new HashMap(2);
		modelMap.put("data", list);
		modelMap.put("success", Boolean.valueOf(true));
		return modelMap;
	}

	@RequestMapping(value = "/selectAreaCodeAll", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> selectAreaCodeAll() {
		Map<String, Object> resuMap = new HashMap<String, Object>();
		List<AreaCode> areaCodeList = accessChannelInfoService.selectALL();
		if (areaCodeList.isEmpty()) {
			resuMap.put("message", "地区信息为空");
			resuMap.put("code", "0");
		} else {
			resuMap.put("data", areaCodeList);
			resuMap.put("code", "200");
		}
		return resuMap;
	}

	@RequestMapping(value = "/getFlowPackageInfo")
	@ResponseBody
	public Map<String, Object> getFlowPackageInfo(
			String packageType,
			@RequestParam(value = "packageIds", required = false) String[] packageIds,
			@RequestParam(value = "zone", required = false) String zone,
			@RequestParam(value = "operator", required = false) String operator) {
		List<FlowPackageInfo> list = accessChannelInfoService
				.getFlowPackageInfoList(packageType, packageIds, zone, operator);
		Map<String, Object> resMap = new HashMap<String, Object>();
		resMap.put("success", Boolean.valueOf(true));
		resMap.put("data", JSON.toJSONString(list));
		return resMap;
	}

	@RequestMapping(value = "/jobConfig", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> getJobConfig() {
		Map<String, Object> result = new HashMap<>();
		result.put("success", false);
		try {
			String inHours = RedisHttpUtil.sendGet(URLConstants.GET_REDIS_URL,
					URLConstants.T_ACCESS_CHANNEL_INFO, "inHours");
			String stuckRows = RedisHttpUtil.sendGet(
					URLConstants.GET_REDIS_URL,
					URLConstants.T_ACCESS_CHANNEL_INFO, "stuckRows");
			if (!StringUtils.isEmpty(inHours)
					&& !"null".equalsIgnoreCase(inHours)) {
				result.put("inHours", inHours);
			} else {
				result.put("inHours", 48);
			}
			if (!StringUtils.isEmpty(stuckRows)
					&& !"null".equalsIgnoreCase(stuckRows)) {
				result.put("stuckRows", stuckRows);
			} else {
				result.put("stuckRows", 2000);
			}
			result.put("success", true);
		} catch (Exception e) {
			LOG.error("获取通道定时任务配置失败", e);
		}
		return result;
	}

	@RequestMapping(value = "/jobConfig", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> setJobConfig(
			@RequestParam("inHours") String inHoursStr,
			@RequestParam("stuckRows") String stuckRowsStr) {
		Map<String, Object> result = new HashMap<>();
		result.put("success", false);
		try {
			if (inHoursStr == null || stuckRowsStr == null)
				return result;
			int inHours = Integer.valueOf(inHoursStr);
			int stuckRows = Integer.valueOf(stuckRowsStr);
			StringBuffer sb = new StringBuffer();
			sb.append("inHours-").append(inHours).append(",stuckRows-")
					.append(stuckRows);
			String rep = RedisHttpUtil.sendGet(URLConstants.ADD_REDIS_URL,
					URLConstants.T_ACCESS_CHANNEL_INFO, sb.toString());
			if (!"OK".equalsIgnoreCase(rep))
				return result;
			result.put("inHours", inHours);
			result.put("stuckRows", stuckRows);
			result.put("success", true);
		} catch (Exception e) {
		}
		return result;
	}

	@RequestMapping(value = "/channelAlarmConfig", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> getChannelAlarmConfig() {
		Map<String, Object> result = new HashMap<>();
		result.put("success", false);
		try {
			String emailAlarmNum = RedisHttpUtil.sendGet(
					URLConstants.GET_REDIS_URL,
					URLConstants.T_CHANNEL_ALARM_INFO, "emailAlarmNum");
			String emailAlarmInterval = RedisHttpUtil.sendGet(
					URLConstants.GET_REDIS_URL,
					URLConstants.T_CHANNEL_ALARM_INFO, "emailAlarmInterval");
			String smsAlarmNum = RedisHttpUtil.sendGet(
					URLConstants.GET_REDIS_URL,
					URLConstants.T_CHANNEL_ALARM_INFO, "smsAlarmNum");
			String smsAlarmInterval = RedisHttpUtil.sendGet(
					URLConstants.GET_REDIS_URL,
					URLConstants.T_CHANNEL_ALARM_INFO, "smsAlarmInterval");
			String emailAlarmRecivers = RedisHttpUtil.sendGet(
					URLConstants.GET_REDIS_URL,
					URLConstants.T_CHANNEL_ALARM_INFO, "emailAlarmRecivers");
			String smsAlarmRecivers = RedisHttpUtil.sendGet(
					URLConstants.GET_REDIS_URL,
					URLConstants.T_CHANNEL_ALARM_INFO, "smsAlarmRecivers");
			if (!StringUtils.isEmpty(emailAlarmNum)
					&& !"null".equalsIgnoreCase(emailAlarmNum)) {
				result.put("emailAlarmNum", emailAlarmNum);
			} else {
				result.put("emailAlarmNum", 200);
			}
			if (!StringUtils.isEmpty(emailAlarmInterval)
					&& !"null".equalsIgnoreCase(emailAlarmInterval)) {
				result.put("emailAlarmInterval", emailAlarmInterval);
			} else {
				result.put("emailAlarmInterval", 600);
			}
			if (!StringUtils.isEmpty(smsAlarmNum)
					&& !"null".equalsIgnoreCase(smsAlarmNum)) {
				result.put("smsAlarmNum", smsAlarmNum);
			} else {
				result.put("smsAlarmNum", 500);
			}
			if (!StringUtils.isEmpty(smsAlarmInterval)
					&& !"null".equalsIgnoreCase(smsAlarmInterval)) {
				result.put("smsAlarmInterval", smsAlarmInterval);
			} else {
				result.put("smsAlarmInterval", 600);
			}
			if (!StringUtils.isEmpty(emailAlarmRecivers)
					&& !"null".equalsIgnoreCase(emailAlarmRecivers)) {
				result.put("emailAlarmRecivers", emailAlarmRecivers);
			} else {
				result.put("emailAlarmRecivers", "");
			}
			if (!StringUtils.isEmpty(smsAlarmRecivers)
					&& !"null".equalsIgnoreCase(smsAlarmRecivers)) {
				result.put("smsAlarmRecivers", smsAlarmRecivers);
			} else {
				result.put("smsAlarmRecivers", "");
			}
			result.put("success", true);
		} catch (Exception e) {
			LOG.error("获取通道预警配置失败", e);
		}
		return result;
	}

	@RequestMapping(value = "/channelAlarmConfig", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> setChannelAlarmConfig(
			@RequestParam("emailAlarmNum") String emailAlarmNum,
			@RequestParam("emailAlarmInterval") String emailAlarmInterval,
			@RequestParam("smsAlarmNum") String smsAlarmNum,
			@RequestParam("smsAlarmInterval") String smsAlarmInterval,
			@RequestParam("emailAlarmRecivers") String emailAlarmRecivers,
			@RequestParam("smsAlarmRecivers") String smsAlarmRecivers) {
		Map<String, Object> result = new HashMap<>();
		result.put("fail", false);
		try {
			if (StringUtils.isEmpty(emailAlarmNum)
					|| StringUtils.isEmpty(emailAlarmNum)
					|| StringUtils.isEmpty(smsAlarmNum)
					|| StringUtils.isEmpty(smsAlarmInterval))
				return result;
			int emailAlarmNumValue = Integer.valueOf(emailAlarmNum);
			int emailAlarmIntervalValue = Integer.valueOf(emailAlarmInterval);
			int smsAlarmNumValue = Integer.valueOf(smsAlarmNum);
			int smsAlarmIntervalValue = Integer.valueOf(smsAlarmInterval);
			StringBuffer sb = new StringBuffer();
			sb.append("emailAlarmNum-").append(emailAlarmNumValue)
					.append(",emailAlarmInterval-")
					.append(emailAlarmIntervalValue).append(",smsAlarmNum-")
					.append(smsAlarmNumValue).append(",smsAlarmInterval-")
					.append(smsAlarmIntervalValue)
					.append(",emailAlarmRecivers-").append(emailAlarmRecivers)
					.append(",smsAlarmRecivers-").append(smsAlarmRecivers);
			String rep = RedisHttpUtil.sendGet(URLConstants.ADD_REDIS_URL,
					URLConstants.T_CHANNEL_ALARM_INFO, sb.toString());
			if (!"OK".equalsIgnoreCase(rep))
				return result;
			result.put("emailAlarmNum", emailAlarmNumValue);
			result.put("emailAlarmInterval", emailAlarmIntervalValue);
			result.put("smsAlarmNum", smsAlarmNumValue);
			result.put("smsAlarmInterval", smsAlarmIntervalValue);
			result.put("emailAlarmRecivers", emailAlarmRecivers);
			result.put("smsAlarmRecivers", smsAlarmRecivers);
			result.put("success", true);
		} catch (Exception e) {
		}
		return result;
	}

	@RequestMapping(value = "/changeStatus")
	@ResponseBody
	public Map<String, Object> changeStatus(Integer status, Long channelSeqId) {
		ShiroUser user = ShiroKit.getUser();
		Map<String, Object> resMap = new HashMap<String, Object>();
		Staff staff = new Staff();
		staff.setLoginName(user.getName());
		AccessChannelInfo data = accessChannelInfoService.get(channelSeqId);
		if (status == null || data == null) {
			resMap.put("message", "非法请求！");
			resMap.put("success", Boolean.valueOf(false));
			return resMap;
		}
		if (status < 0 || status > 1) {
			resMap.put("message", "非法请求！");
			resMap.put("success", Boolean.valueOf(false));
			return resMap;
		}

		if (status == 1 && 0 == data.getIsValid()) { // 从无效设置为有效时，需判断该通道的发单数是否低于规定值，如果低于则允许修改，大于则返回错误
			int inHours = 48;
			int stuckRows = 2000;

			String inHoursRedis = RedisHttpUtil.sendGet(
					URLConstants.GET_REDIS_URL,
					URLConstants.T_ACCESS_CHANNEL_INFO, "inHours");
			String stuckRowsRedis = RedisHttpUtil.sendGet(
					URLConstants.GET_REDIS_URL,
					URLConstants.T_ACCESS_CHANNEL_INFO, "stuckRows");
			try {
				inHours = Integer.valueOf(inHoursRedis);
			} catch (Exception e) {
				inHours = 48;
			}
			try {
				stuckRows = Integer.valueOf(stuckRowsRedis);
			} catch (Exception e) {
				stuckRows = 2000;
			}

			Map<String, Object> parmas = new HashMap<>();
			parmas.put("inHours", inHours);
			parmas.put("stuckRows", stuckRows);
			List<Long> channelIdsInEnable = new ArrayList<>();
			channelIdsInEnable.add(data.getChannelSeqId());
			parmas.put("channelIdsInEnable", channelIdsInEnable);
			List<String> channelIdsToDisable = flowOrderInfoMapper
					.selectStuckChannelIds(parmas);
			if (channelIdsToDisable.size() != 0) {
				resMap.put("message", "该通道在" + inHours + "小时内，已发送单数仍大于"
						+ stuckRows);
				resMap.put("success", Boolean.valueOf(false));
				return resMap;
			}
		}

		data.setIsValid(status);
		data.setUpdator(staff.getLoginName());
		data.setUpdateTime(new Date());
		accessChannelInfoService.update(data);
		// 添加日志(修改)
		accessChannelInfoHisService.insert(data, "1");
		resMap.put("message", "修改成功");
		resMap.put("success", Boolean.valueOf(true));
		return resMap;
	}

	/**
	 * 获取通道对应通道组
	 * 
	 * @param channelSeqId
	 * @return
	 */
	@RequestMapping(value = "/findChannelToGroup")
	@ResponseBody
	public Map<String, Object> findChannelToGroup(Long channelSeqId) {
		Map<String, Object> resMap = new HashMap<String, Object>();
		AccessChannelInfo data = accessChannelInfoService.get(channelSeqId);
		List<ChannelToGroup> channelToGroups = accessChannelInfoService
				.selectByChannelSeqId(channelSeqId);
		data.setChannelToGroupList(channelToGroups);
		resMap.put("data", data);
		resMap.put("success", Boolean.valueOf(true));
		return resMap;
	}

	/**
	 * 通道添加到通道组
	 * 
	 * @param data
	 * @return
	 */
	@RequestMapping(value = "/addToGroup", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> addToGroup(AccessChannelInfo data) {
		ShiroUser user = ShiroKit.getUser();
		Staff staff = new Staff();
		staff.setLoginName(user.getName());
		Map<String, Object> resMap = new HashMap<String, Object>();
		// 校验
		StringBuilder errorMessage = new StringBuilder();
		if (!isParamCheck(data, errorMessage)) {
			resMap.put("success", Boolean.valueOf(false));
			resMap.put("message", errorMessage.toString());
			return resMap;
		}
		setBaseEntity(data, staff, data.getChannelSeqId());
		accessChannelInfoService.addToGroup(data);
		resMap.put("success", Boolean.valueOf(true));
		resMap.put("message", "添加成功");
		return resMap;
	}

	private List<AccessChannelInfo> getAccessChannelInfoList(
			AccessChannelInfo data, String type) {
		AccessChannelInfo info = new AccessChannelInfo();
		if (Constant.TYPE_CHANNEL_ID.equals(type)) {
			info.setChannelId(data.getChannelId());
		} else if (Constant.TYPE_CHANNEL_NAME.equals(type)) {
			info.setChannelName(data.getChannelName());
		}
		return accessChannelInfoService.selectByInfo(info);
	}

	/**
	 * 修改通道产品有效无效
	 * @param isValid
	 * @param channelSeqId
	 * @param channelProductId
	 * @return
	 */
	@RequestMapping(value = "/changeChannelProductStatus")
    @ResponseBody
    public Object changeChannelProductStatus(Integer isValid, Long channelSeqId,String channelProductIds) {
        if (ToolUtil.isEmpty(isValid)  ||ToolUtil.isEmpty(channelSeqId) || ToolUtil.isEmpty(channelProductIds)) {
            return new ErrorTip(0,"非法请求！");
        }
        if (isValid < 0 || isValid > 1) {
        	return new ErrorTip(0,"非法请求！");
        }
        for(String channelProductId :channelProductIds.split(",") ){
        	if(!ToolUtil.isEmpty(channelProductId)){
        		Map<String, Object> params = new HashMap<String, Object>();
                params.put("isValid", isValid);
                params.put("channelSeqId", channelSeqId);
                params.put("channelProductId", channelProductId);
                channelProductInfoService.changeChannelProductStatus(params);
                //删除redis
                List<String> groupidlist=channelToGroupMapper.selectChannelGroupId(channelSeqId);
            	for(String l:groupidlist){
            		String result1 = RedisHttpUtil.sendGet(URLConstants.DEL_REDIS_URL,
            				l,URLConstants.DELALL);
            		if (!"OK".equals(result1)) {
            			throw new MyException("删除Redis中信息出错,其请求URL参数为:" +l+"\t"+URLConstants.DELALL);
            		}
            	}
        	}
        }
        return SUCCESS_TIP;
    }
	
	/**
	 * 导出数据
	 * @param response
	 * @param page
	 * @throws Exception
	 */
	@RequestMapping(value = "/export")
	public void export(HttpServletResponse response, Page<AccessChannelInfo> page) throws Exception {
			try {
				  // 生成Excel
				  Map<String, Object> beanMap = new HashMap<String, Object>();
				  page.setRows(10000);
				  List<AccessChannelInfo> list = accessChannelInfoService.pageQuery(page).getRows();
					if (null != list && !list.isEmpty()) {
						for (AccessChannelInfo accessChannelInfo : list) {
							/*String channelLimitStr = "";
							if(0 == accessChannelInfo.getChannelLimit()){
								channelLimitStr = "赠送";
							}else if (1 == accessChannelInfo.getChannelLimit()) {
								channelLimitStr = "微信";
							}else if (2 == accessChannelInfo.getChannelLimit()) {
								channelLimitStr = "阿里";
							}else if (3 == accessChannelInfo.getChannelLimit()) {
								channelLimitStr = "低价";
							}*/
							List<ChannelProductInfo> channelProductInfos = channelProductInfoService.selectByChannelSeqId(accessChannelInfo.getChannelSeqId());
							List<Map<String, Object>> products = new ArrayList<Map<String, Object>>();
							for (ChannelProductInfo channelProductInfo : channelProductInfos) {
								Map<String, Object> map = new HashMap<String, Object>();
								map.put("channelProductId", channelProductInfo.getChannelProductId());
								map.put("channelProductName", channelProductInfo.getChannelProductName());
								map.put("price", channelProductInfo.getPrice());
								map.put("costPrice", channelProductInfo.getFlowPackageInfo().getCostPrice());
								products.add(map);
							}
							accessChannelInfo.setChannelProducts(JSONArray.fromObject(products).toString());
						}
				  beanMap.put("list", list);
				  TemplateExcelManager.getInstance().createExcelFileAndDownload(TemplateExcel.AccessChannel_Info, beanMap);
			   }
			} catch (Exception e) {
				LOG.error("导出报表出错！！！", e);
			}
	}
	
	private boolean isParamCheck(AccessChannelInfo accessChannelInfo,
			StringBuilder errorMessage) {
		for (FlowPackageInfo fpi : accessChannelInfo.getFlowPackageInfoList()) {
			if (null == fpi.getPackageId()) {
				continue;
			}
			if (fpi.getCostPrice() == null) {
				errorMessage.append("请输入结算价格");
				return false;
			}
			if (!fpi.getCostPrice().toString().matches("^[0-9]+(\\.[0-9]*)?$")) {
				errorMessage.append("请输入合法的价格");
				return false;
			}
		}
		return true;
	}

	protected void setBaseEntity(BaseEntity entity, Staff staff, Object id) {
		if (id == null) {
			entity.setCreator(staff.getLoginName());
			entity.setCreateTime(new Date());
		} else {
			entity.setUpdator(staff.getLoginName());
			entity.setUpdateTime(new Date());
		}

	}
}
