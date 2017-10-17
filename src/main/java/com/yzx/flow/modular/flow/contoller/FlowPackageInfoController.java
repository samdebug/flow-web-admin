package com.yzx.flow.modular.flow.contoller;

import com.alibaba.fastjson.JSONObject;
import com.yzx.flow.common.constant.tips.ErrorTip;
import com.yzx.flow.common.constant.tips.SuccessTip;
import com.yzx.flow.common.controller.BaseController;
import com.yzx.flow.common.exception.BizExceptionEnum;
import com.yzx.flow.common.exception.BussinessException;
import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.page.PageInfoBT;
import com.yzx.flow.common.persistence.model.AreaCode;
import com.yzx.flow.common.persistence.model.FlowPackageInfo;
import com.yzx.flow.common.persistence.model.FlowProductInfo;
import com.yzx.flow.core.shiro.ShiroKit;
import com.yzx.flow.core.util.ToolUtil;
import com.yzx.flow.modular.flow.service.IFlowProductInfoService;
import com.yzx.flow.modular.flowOrder.Service.IFlowPackageInfoService;

import net.sf.ehcache.util.ProductInfo;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.Null;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 流量包控制器
 *
 * @author liuyufeng
 * @Date 2017-08-24 19:40:02
 */
@Controller
@RequestMapping("/flowPackageInfo")
public class FlowPackageInfoController extends BaseController {

	private static final Logger LOG = LoggerFactory.getLogger(FlowPackageInfoController.class);
	
    private String PREFIX = "/flow/flowPackageInfo/";
    
    @Autowired
    private IFlowPackageInfoService flowPackageInfoService;

	@Autowired
	private IFlowProductInfoService flowProductInfoService;
    
    /**
     * 跳转到流量包首页
     */
    @RequestMapping("")
    public String index(Model model) {
    	List<AreaCode> list = flowPackageInfoService.selectAllArea();
    	model.addAttribute("area",list);
        return PREFIX + "flowPackageInfo.html";
    }

    /**
     * 跳转到添加流量包
     */
    @RequestMapping("/flowPackageInfo_add")
    public String flowPackageInfoAdd(Model model) {
    	List<AreaCode> list = flowPackageInfoService.selectAllArea();
    	model.addAttribute("area",list);
    	model.addAttribute("packageType",FlowPackageInfo.PACKAGE_TYPE_MAP);
        return PREFIX + "flowPackageInfo_add.html";
    }
    
    /**
     * 通过流量包名称获得流量包列表
     */
    @RequestMapping("/selectPackageByName")
    @ResponseBody
    public Map<String, Object> getPackage(Page<FlowPackageInfo> page) {
		Page<FlowPackageInfo> pageQuery = this.flowPackageInfoService.pageQuery(page);
    	Map<String, Object> map = new HashMap<String, Object>();
    	map.put("packageList", pageQuery.getDatas().toArray());
    	return map;
    }
    
    /**
     * 跳转到修改流量包
     */
    @RequestMapping("/flowPackageInfo_update/{flowPackageInfoId}")
    public String flowPackageInfoUpdate(@PathVariable String flowPackageInfoId ,Model model) {
    	List<AreaCode> list = flowPackageInfoService.selectAllArea();
    	model.addAttribute("area",list);
		FlowPackageInfo flowPackageInfo = this.flowPackageInfoService.get(flowPackageInfoId);
		if (null != flowPackageInfo) {
			flowPackageInfo.setFlowProductRules(null);// 清空规则列表，防止更新时，页面删除了，这里还有
		}
    	model.addAttribute("flowPackageInfo", flowPackageInfo);
        return PREFIX + "flowPackageInfo_edit.html";
    }

    /**
     * 获取流量包列表
     */
    @RequestMapping(value = "/list")
    @ResponseBody
    public Object list(Page<FlowPackageInfo> page) {
    	
    	Page<FlowPackageInfo> list = flowPackageInfoService.pageQuery(page);
		for (int i = 0; i < list.getDatas().size(); i++) {
			FlowPackageInfo flowPackageInfo = list.getDatas().get(i);
			if (("1").equals(flowPackageInfo.getIsCombo())) {
				List<FlowPackageInfo> lpList = flowPackageInfoService
						.selectInPackageId(flowPackageInfo.getComboPackageStr());
				list.getDatas().get(i).setFlowPackageInfos(lpList);
			}
		}
        return new PageInfoBT<FlowPackageInfo>(list.getDatas(), page.getTotal());
    }
    
    
    /**
     * 新增流量包
     */
    @RequestMapping(value = "/add")
    @ResponseBody
    public Object add(FlowPackageInfo data) {
    	if(data != null && data.getPackageId() != null &&
        		data.getPackageName() != null && data.getFlowAmount() != null && 
        		data.getZone() != null && data.getActivePeriod() != null &&
        		data.getSalePrice() != null && data.getCostPrice() != null &&
        		data.getPackageType() != null && data.getPackageTypeDesc() != null) {
    			data.setIsValid(0);
    			if (data.getOperatorCode() == null) {
					data.setOperatorCode("");
				}
    			data.setCreator(ShiroKit.getLoginName());
    			data.setCreateTime(new Date());
        		this.flowPackageInfoService.saveAndUpdate(data);
        		return super.SUCCESS_TIP;
        	} else {
        		 throw new BussinessException(BizExceptionEnum.REQUEST_NULL);
        	}
    }

    /**
     * 删除流量包
     */
    @RequestMapping(value = "/delete")
    @ResponseBody
    public Object delete(@RequestParam(name="flowPackageInfoId") String flowPackageInfoId) {
		FlowPackageInfo flowPackageInfo = flowPackageInfoService.get(flowPackageInfoId);
		if (flowPackageInfo == null) {
			LOG.debug("流量包[{}]数据异常", flowPackageInfoId);
			throw new BussinessException(BizExceptionEnum.REQUEST_NULL);
		}
		if (flowPackageInfo.getComboPackageStr() != null ) {
			int count = flowPackageInfoService
					.selectCountInPackageId(flowPackageInfo
							.getComboPackageStr());
			if (count > 0) {
				LOG.debug("[{}],流量包有关联", flowPackageInfo.getComboPackageStr());
				return new ErrorTip(500,"删除失败,请先删除关联的流量包");
			}
		}
		int count = flowProductInfoService.selectCountByPackageId(flowPackageInfoId);
		if (count > 0) {
			LOG.debug("有[{}]个产品关联", count);
			return new ErrorTip(500,"删除失败,请先删除关联的产品");
		}
		flowPackageInfoService.delete(flowPackageInfoId);
		return new SuccessTip("删除成功");
    }


    /**
     * 修改流量包
     */
    @RequestMapping(value = "/update")
    @ResponseBody
    public Object update(FlowPackageInfo data) {
    	if(data != null && data.getPackageId() != null && data.getOperatorCode() != null &&
        		data.getPackageName() != null && data.getFlowAmount() != null && 
        		data.getZone() != null && data.getActivePeriod() != null &&
        		data.getSalePrice() != null && data.getCostPrice() != null &&
        		data.getPackageType() != null && data.getPackageTypeDesc() != null) {
    			data.setIsValid(0);
    			data.setUpdator(ShiroKit.getLoginName());
    			data.setUpdateTime(new Date());
        		this.flowPackageInfoService.saveAndUpdate(data);
        		return super.SUCCESS_TIP;
        	} else {
        		 throw new BussinessException(BizExceptionEnum.REQUEST_NULL);
        	}
    }

    /**
     * 流量包详情
     */
    @RequestMapping(value = "/detail/{flowPackageInfoId}")
    public Object detail(@PathVariable String flowPackageInfoId, Model model) {
    	model.addAttribute("flowPackageInfo", this.flowPackageInfoService.get(flowPackageInfoId));
    	  return PREFIX + "flowPackageInfo_detail.html";
    }
    
	@RequestMapping(value = "/operatorCode.ajax", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> getByZoneOperatorCode(
			@RequestParam(value = "zone", defaultValue = "") String zone,
			@RequestParam(value = "operatorCode", defaultValue = "") String operatorCode,
			@RequestParam(value = "packageId", defaultValue = "") String packageId) {
		Map<String, Object> hashMap = new HashMap<String, Object>();
		hashMap.put("data", flowPackageInfoService.selectByZoneOperatorCode(
				zone, operatorCode, packageId));
		return hashMap;
	}
	
	@RequestMapping(value = "/get.ajax")
	@ResponseBody
	public Map<String, Object> get(String packageId) {
		FlowPackageInfo data = flowPackageInfoService.get(packageId);
		Map<String, Object> hashMap = new HashMap<String, Object>();
		hashMap.put("data", data);
		return hashMap;
	}
	
	/**
	 * 更新的时候需额外传递updId,值跟主键值一样,被@ModelAttribute注释的方法会在此controller每个方法执行前被执行，
	 * 要谨慎使用
	 */
	@ModelAttribute("flowPackageInfo")
	public void getForUpdate(
			@RequestParam(value = "updId", required = false) String updId,
			Model model) {
		if (null != updId) {
			FlowPackageInfo flowPackageInfo = flowPackageInfoService.get(updId);
			LOG.info("流量包修改前：{}", JSONObject.toJSONString(flowPackageInfo));
			if (null != flowPackageInfo) {
				flowPackageInfo.setFlowProductRules(null);// 清空规则列表，防止更新时，页面删除了，这里还有
				model.addAttribute("flowPackageInfo", flowPackageInfo);
				return;
			}
		}
		model.addAttribute("flowPackageInfo", new FlowPackageInfo());
	}
}
