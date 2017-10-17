package com.yzx.flow.modular.channel.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.yzx.flow.common.constant.tips.ErrorTip;
import com.yzx.flow.common.controller.BaseController;
import com.yzx.flow.common.exception.BizExceptionEnum;
import com.yzx.flow.common.exception.BussinessException;
import com.yzx.flow.common.exception.MyException;
import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.page.PageInfoBT;
import com.yzx.flow.common.persistence.model.AccessChannelGroup;
import com.yzx.flow.common.persistence.model.AccessChannelInfo;
import com.yzx.flow.common.persistence.model.BaseEntity;
import com.yzx.flow.common.persistence.model.ChannelGroupToGroup;
import com.yzx.flow.common.persistence.model.ChannelToGroup;
import com.yzx.flow.common.persistence.model.Staff;
import com.yzx.flow.common.util.RedisHttpUtil;
import com.yzx.flow.common.util.URLConstants;
import com.yzx.flow.core.shiro.ShiroKit;
import com.yzx.flow.core.shiro.ShiroUser;
import com.yzx.flow.core.util.ToolUtil;
import com.yzx.flow.modular.channel.service.IAccessChannelGroupService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 接入通道组控制器
 *
 * @author liuyufeng
 * @Date 2017-08-18 12:21:37
 */
@Controller
@RequestMapping("/accessChannelGroup")
public class AccessChannelGroupController extends BaseController {

	private static final Logger LOG = LoggerFactory.getLogger(AccessChannelGroupController.class);
	
	
    private String PREFIX = "/channel/accessChannelGroup/";

    @Autowired
	@Qualifier("accessChannelGroupService")
	private IAccessChannelGroupService accessChannelGroupService;
    /**
     * 跳转到接入通道组首页
     */
    @RequestMapping("")
    public String index() {
        return PREFIX + "accessChannelGroup.html";
    }

    /**
     * 跳转到给已有通道组增加接入通道组
     */
    @RequestMapping("/accessChannelGroup_toGroup")
    public String accessChannelGroup_toGroup(@RequestParam(name="channelGroupId") String channelGroupId,Model model) {
    	model.addAttribute("channelGroupId", channelGroupId);
        return PREFIX + "accessChannelGroup_toGroup.html";
    }

    /**
     * 跳转到添加接入通道组
     */
    @RequestMapping("/accessChannelGroup_add")
    public String accessChannelGroupAdd() {
        return PREFIX + "accessChannelGroup_add.html";
    }
    
    /**
     * 跳转到修改接入通道组
     */
    @RequestMapping("/accessChannelGroup_update/{accessChannelGroupId}")
    public String accessChannelGroupUpdate(@PathVariable String accessChannelGroupId, Model model) {
    	model.addAttribute("channelGroupId", accessChannelGroupId);
        return PREFIX + "accessChannelGroup_edit.html";
    }
    
    /**
     * 跳转到修改接入通道组
     */
    @RequestMapping("/detail/{accessChannelGroupId}")
    public String detail(@PathVariable String accessChannelGroupId,@RequestParam(name="isShow") String isShow, Model model) {
    	if(!ToolUtil.isEmpty(isShow)){
    		model.addAttribute("isShow", Integer.parseInt(isShow));
    	}
    	model.addAttribute("channelGroupId", accessChannelGroupId);
        return PREFIX + "accessChannelGroup_view.html";
    }
    
    /**
     * 获取接入通道组列表
     */
    @RequestMapping(value = "/list")
    @ResponseBody
    public PageInfoBT<AccessChannelGroup> list(Page<AccessChannelGroup> page) {
    	if (page.getSort()==null) {
			page.setSort("create_time");
		}
    	PageInfoBT<AccessChannelGroup> resultPage = accessChannelGroupService.pageQuery(page);
        return resultPage;
    }
    
	@RequestMapping(value = "/get")
	@ResponseBody
	public Map<String, Object> get(Long channelGroupId) {
		AccessChannelGroup data = accessChannelGroupService.get(channelGroupId);
		List<ChannelToGroup> channelToGroupList = accessChannelGroupService.getChannelToGroupsByChannelGroupId(channelGroupId);
		data.setChannelToGroupList(channelToGroupList);
		//查询关联通道
		List<ChannelGroupToGroup> groupToGroupList = accessChannelGroupService.getGroupToGroupsByChannelGroupId(channelGroupId);
		data.setGroupToGroupList(groupToGroupList);
		Map<String, Object> resultMap=new HashMap<String, Object>();
		resultMap.put("data", data);
		resultMap.put("success", 1);
		return resultMap;
	}

    /**
     * 新增接入通道组
     */
    @RequestMapping(value = "/add")
    @ResponseBody
    public Object add(AccessChannelGroup data) {
    	ShiroUser user = ShiroKit.getUser();
    	Staff staff = new Staff();
    	staff.setLoginName(user.getName());
    	//默认通过质量指标为为价格优先 如果以后有多种支持 去掉此处即可
    	data.setQuotaId(Long.parseLong("1"));
    	//下发流量通道默认跟通道名称一致
    	data.setDispatchChannel(data.getGroupName());
		if (data.getChannelGroupId() == null) {
			AccessChannelGroup info = new AccessChannelGroup();
			info.setDispatchChannel(data.getDispatchChannel());
			List<AccessChannelGroup> channelGroupList = accessChannelGroupService.selectByInfo(info);
			if (!channelGroupList.isEmpty()) {
				return new ErrorTip(0,"当前下发通道已存在，请重新输入");
			}
		} else {
			AccessChannelGroup accessChannelGroup = accessChannelGroupService.get(data.getChannelGroupId());
			data.setDispatchChannel(accessChannelGroup.getDispatchChannel());
		}
		// 校验
		StringBuilder errorMessage = new StringBuilder();
		if (!isParamCheck(data, errorMessage)) {
			return new ErrorTip(0,errorMessage.toString());
		}
		setBaseEntity(data, staff, data.getChannelGroupId());
		accessChannelGroupService.saveAndUpdate(data);
		String result = RedisHttpUtil.sendGet(URLConstants.DEL_REDIS_URL,
				data.getDispatchChannel(),URLConstants.DELALL);
		if (!"OK".equals(result)) {
			throw new MyException("删除Redis中信息出错,其请求URL参数为:" + data.getDispatchChannel()+"\t"+URLConstants.DELALL);
		}
		
		//修改被引用的通道组删除引用通道组的redis
		if (1 == data.getIsQuote()) {
			List<ChannelGroupToGroup> list = accessChannelGroupService.getGroupToGroupsByQuoteGroupId(data.getChannelGroupId());
			for (ChannelGroupToGroup channelGroupToGroup : list) {
				AccessChannelGroup accessChannelGroup = accessChannelGroupService.get(channelGroupToGroup.getChannelGroupId());
				result = RedisHttpUtil.sendGet(URLConstants.DEL_REDIS_URL,
						accessChannelGroup.getDispatchChannel(),URLConstants.DELALL);
				if (!"OK".equals(result)) {
					throw new MyException("删除Redis中信息出错,其请求URL参数为:" + data.getDispatchChannel()+"\t"+URLConstants.DELALL);
				}
			}
		}
        return SUCCESS_TIP;
    }

    /**
     * 删除接入通道组
     */
    @RequestMapping(value = "/delete")
    @ResponseBody
    public Object delete(@RequestParam(name="accessChannelGroupId") String channelGroupId) {
    	if (ToolUtil.isEmpty(channelGroupId)) {
			throw new BussinessException(BizExceptionEnum.REQUEST_INVALIDATE);
		}
    	AccessChannelGroup data=accessChannelGroupService.get(Long.parseLong(channelGroupId));
		int i = accessChannelGroupService.delete(Long.parseLong(channelGroupId));
		String result = RedisHttpUtil.sendGet(URLConstants.DEL_REDIS_URL,
				data.getDispatchChannel(),URLConstants.DELALL);
		if (!"OK".equals(result)) {
			throw new MyException("删除Redis中信息出错,其请求URL参数为:" + data.getDispatchChannel()+"\t"+URLConstants.DELALL);
		}
		if (i<0) {
			return new ErrorTip(0, "删除失败");
		}
        return SUCCESS_TIP;
    }


    /**
     * 修改接入通道组
     */
    @RequestMapping(value = "/update")
    @ResponseBody
    public Object update(AccessChannelGroup data) {
    	ShiroUser user = ShiroKit.getUser();
    	Staff staff = new Staff();
    	staff.setLoginName(user.getName());
    	//默认通过质量指标为为价格优先 如果以后有多种支持 去掉此处即可
    	data.setQuotaId(Long.parseLong("1"));
    	//下发流量通道默认跟通道名称一致
    	data.setDispatchChannel(data.getGroupName());
		if (data.getChannelGroupId() == null) {
			AccessChannelGroup info = new AccessChannelGroup();
			info.setDispatchChannel(data.getDispatchChannel());
			List<AccessChannelGroup> channelGroupList = accessChannelGroupService.selectByInfo(info);
			if (!channelGroupList.isEmpty()) {
				return new ErrorTip(0,"当前下发通道已存在，请重新输入");
			}
		} else {
			AccessChannelGroup accessChannelGroup = accessChannelGroupService.get(data.getChannelGroupId());
			data.setDispatchChannel(accessChannelGroup.getDispatchChannel());
		}
		// 校验
		StringBuilder errorMessage = new StringBuilder();
		if (!isParamCheck(data, errorMessage)) {
			return new ErrorTip(0,errorMessage.toString());
		}
		setBaseEntity(data, staff, data.getChannelGroupId());
		accessChannelGroupService.saveAndUpdate(data);
		String result = RedisHttpUtil.sendGet(URLConstants.DEL_REDIS_URL,
				data.getDispatchChannel(),URLConstants.DELALL);
		if (!"OK".equals(result)) {
			throw new MyException("删除Redis中信息出错,其请求URL参数为:" + data.getDispatchChannel()+"\t"+URLConstants.DELALL);
		}
		
		//修改被引用的通道组删除引用通道组的redis
		if (1 == data.getIsQuote()) {
			List<ChannelGroupToGroup> list = accessChannelGroupService.getGroupToGroupsByQuoteGroupId(data.getChannelGroupId());
			for (ChannelGroupToGroup channelGroupToGroup : list) {
				AccessChannelGroup accessChannelGroup = accessChannelGroupService.get(channelGroupToGroup.getChannelGroupId());
				result = RedisHttpUtil.sendGet(URLConstants.DEL_REDIS_URL,
						accessChannelGroup.getDispatchChannel(),URLConstants.DELALL);
				if (!"OK".equals(result)) {
					throw new MyException("删除Redis中信息出错,其请求URL参数为:" + data.getDispatchChannel()+"\t"+URLConstants.DELALL);
				}
			}
		}
        return super.SUCCESS_TIP;
    }

    private boolean isParamCheck(AccessChannelGroup data, StringBuilder errorMessage) {
		for (AccessChannelInfo aci : data.getAccessChannelInfoList()) {
			if (null == aci.getChannelSeqId()) {
				continue;
			}
			if (aci.getWeight() == null) {
				errorMessage.append("请输入权重");
				return false;
			}
			if (!aci.getWeight().toString().matches("^(0|([1-9]\\d*))$")) {
				errorMessage.append("请输入大于0的整数");
				return false;
			}
		}
		return true;
	}
    
    
    /**
	 * 查询可引用的通道组信息
	 * @param channelSeqIds
	 * @param text
	 * @return
	 */
	@RequestMapping(value = "/selectQuoteGroupList", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> selectAccessChannelInfoList(@RequestParam(value = "channelGroupIds", required = false) Long[] channelGroupIds,
    		@RequestParam(value = "groupText", required = false) String groupText,@RequestParam(value = "isQuote", required = false) String isQuote){
        List<AccessChannelGroup> list = new ArrayList<AccessChannelGroup>();
        Map<String, Object> params = new HashMap<String, Object>();
        if (channelGroupIds != null && channelGroupIds.length == 0) {
        	channelGroupIds = null;
        }
        params.put("channelGroupIds", channelGroupIds);
        params.put("groupText", groupText);
        if ("0".equals(isQuote)) {
        	params.put("isQuote", null);
		}else {
			params.put("isQuote", 1);
		}
        LOG.info("获取可引用的通道组信息,params={}",params);
        list = accessChannelGroupService.selectQuoteChannelGroup(params);
        Map<String, Object> modelMap = new HashMap(2);
        modelMap.put("data", list);
        modelMap.put("success", Boolean.valueOf(true));
        return modelMap;
    }
    
	
	/**
	 * 获取自定义通道通道组对应通道组
	 * @param channelSeqId
	 * @return
	 */
	@RequestMapping(value = "/findGroupToGroup")
    @ResponseBody
    public Map<String, Object> findGroupToGroup(Long channelGroupId) {
		AccessChannelGroup data = accessChannelGroupService.get(channelGroupId);
		Map<String, Object> params = new HashMap<String, Object>();
		//查询关联通道
		List<ChannelGroupToGroup> groupToGroupList = accessChannelGroupService.getGroupToGroupsByQuoteGroupId(channelGroupId);
		data.setGroupToGroupList(groupToGroupList);
		params.put("data", data);
		params.put("success", Boolean.valueOf(true));
        return params;
    }
	
	/**
	 * 自定义通道组添加到通道组
	 * @param data
	 * @return
	 */
	@RequestMapping(value = "/groupAddToGroup", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> groupAddToGroup( AccessChannelGroup data) {
		ShiroUser user = ShiroKit.getUser();
		Staff staff = new Staff();
		staff.setLoginName(user.getName());
		AccessChannelGroup  QuoteChannelGroup= accessChannelGroupService.get(data.getChannelGroupId());
		Map<String, Object> params = new HashMap<String, Object>();
		if (null == QuoteChannelGroup || 1 != QuoteChannelGroup.getIsQuote()) {
			params.put("message", "自定义通道组不存在，添加通道组失败");
			params.put("success", Boolean.valueOf(false));
			return params;
		}
		// 校验
		StringBuilder errorMessage = new StringBuilder();
		if (!isParamCheck(data, errorMessage)) {
			params.put("message", errorMessage.toString());
			params.put("success", Boolean.valueOf(false));
			return params;
		}
		setBaseEntity(data, staff, data.getChannelGroupId());
		accessChannelGroupService.groupAddToGroup(data);
		params.put("message", "新增成功");
		params.put("success", Boolean.valueOf(true));
		return params;
	}
    
    protected void setBaseEntity(BaseEntity entity ,Staff staff,Object id){
		if(id==null){
			entity.setCreator(staff.getLoginName());
			entity.setCreateTime(new Date());
		}else{
			entity.setUpdator(staff.getLoginName());
			entity.setUpdateTime(new Date());
		}
		
	}
}
