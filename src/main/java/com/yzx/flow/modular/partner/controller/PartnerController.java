package com.yzx.flow.modular.partner.controller;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yzx.flow.common.constant.tips.ErrorTip;
import com.yzx.flow.common.controller.BaseController;
import com.yzx.flow.common.exception.BizExceptionEnum;
import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.page.PageInfoBT;
import com.yzx.flow.common.persistence.model.CustomerInfo;
import com.yzx.flow.common.persistence.model.OrderDealRecordWithBLOBs;
import com.yzx.flow.common.persistence.model.PartnerInfo;
import com.yzx.flow.common.persistence.model.PartnerProduct;
import com.yzx.flow.common.util.Constant;
import com.yzx.flow.core.shiro.ShiroKit;
import com.yzx.flow.core.shiro.ShiroUser;
import com.yzx.flow.modular.customer.service.ICustomerInfoService;
import com.yzx.flow.modular.orderDealRecord.service.IOrderDealRecordService;
import com.yzx.flow.modular.partner.service.IPartnerProductService;
import com.yzx.flow.modular.partner.service.IPartnerService;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
 * 合作伙伴控制器
 *
 * @author liuyufeng
 * @Date 2017-08-15 10:28:54
 */
@Controller
@RequestMapping("/partner")
public class PartnerController extends BaseController {
	
	private static final Logger LOG = LoggerFactory.getLogger(PartnerController.class);

    private String PREFIX = "/partner/";
    
    
    @Autowired
    private IPartnerService partnerService;
    
    @Autowired
    private IOrderDealRecordService orderDealRecordService;
    
    @Autowired
	private IPartnerProductService partnerProductService;
    
    @Autowired
    private ICustomerInfoService customerInfoService;
    

    /**
     * 跳转到合作伙伴首页
     */
    @RequestMapping("")
    public String index() {
        return PREFIX + "partner.html";
    }

    /**
     * 跳转到添加合作伙伴
     */
    @RequestMapping("/partner_add")
    public String partnerAdd() {
        return PREFIX + "partner_add.html";
    }

    /**
     * 跳转到修改合作伙伴
     */
    @RequestMapping("/partner_update/{partnerId}")
    public String partnerUpdate(@PathVariable long partnerId, Model model) {
    	 
//		PartnerInfo data = partnerService.get(partnerId);
//		if ( data == null ) {
//			return String.format("%s%s", FORWARD, "/global/error");
//		}
//    	model.addAttribute("partner", data);
    	model.addAttribute("partnerId", partnerId);
//        return PREFIX + "partner_edit.html";
        return PREFIX + "partner_add.html";
    }

    /**
     * 获取合作伙伴列表
     */
    @RequestMapping(value = "/list")
    @ResponseBody
    public Object list(Page<PartnerInfo> page) {
//    	return partnerService.pageQuery(page);
    	List<PartnerInfo> list = partnerService.pageQuery(page);
    	
        return new PageInfoBT<PartnerInfo>(list, page.getTotal());
    }

//    /**
//     * 新增合作伙伴
//     */
//    @RequestMapping(value = "/add")
//    @ResponseBody
//    public Object add(PartnerInfo data) {
//    	
//    	data.setCreditAmount(new BigDecimal(0));
//    	String uuid = UUID.randomUUID().toString();
//		uuid = uuid.replaceAll("-", "");
//		data.setIdentityId(uuid);
//		data.setIsDeleted(PartnerInfo.NOT_DELETE);
//		
//		ShiroUser current = ShiroKit.getUser();
//		data.setCreator(current.getAccount());
//		data.setCreateTime(new Date());
//		data.setUpdator("");
//    	
//    	partnerService.createStaffAndSave(data);
//        return super.SUCCESS_TIP;
//    }

    /**
     * 删除合作伙伴
     */
    @RequestMapping(value = "/delete")
    @ResponseBody
    public Object delete(Long partnerId) {
    	// TODO 
//    	if (!isAdmin()) {
//			return fail("非法操作");
//		}
		// 是否存在客户
		List<CustomerInfo> customerInfoList = customerInfoService.getByPartnerId(partnerId);
		if (!customerInfoList.isEmpty()) {
		    return ErrorTip.buildErrorTip("当前合作伙伴下仍然有客户，不可删除。");
		}
		// 删除合作伙伴
		partnerService.delete(partnerId);
        return SUCCESS_TIP;
    }
    


    /**
     * 修改合作伙伴
     */
    @RequestMapping(value = "/update")
    @ResponseBody
    public Object update(PartnerInfo data) {
    	
    	partnerService.updatePartnerInfo(data);
        return super.SUCCESS_TIP;
    }

    /**
     * 合作伙伴详情
     */
    @RequestMapping(value = "/detail")
    public String detail(@RequestParam("id") long partnerId, Model model) {
    	
    	PartnerInfo self = partnerService.getAll(partnerId);
    	
    	model.addAttribute("partner", self);
    	model.addAttribute("flag", self != null);// 标志是否有数据
    	model.addAttribute("orderDealRecords", Collections.emptyList());// 产品协议变更历史
    	
    	if ( self != null ) {
    		OrderDealRecordWithBLOBs info = new OrderDealRecordWithBLOBs();
    	    info.setSourceId(self.getPartnerId().toString());
    	    info.setType(Constant.RECORD_TYPE_PARTNER);
            List<OrderDealRecordWithBLOBs> records = orderDealRecordService.selectByInfo(info);
    		
            if ( records != null && !records.isEmpty() ) 
    			model.addAttribute("orderDealRecords", records);
    	}
    	
    	return PREFIX + "partner_view.html";
    }
    
    
    /**
     * 跳转到合作伙伴 我的信息
     */
    @RequestMapping("/self")
    public String self(Model model) {
    	
    	ShiroUser current = ShiroKit.getUser();
    	String account = current.getAccount();
    	
    	PartnerInfo self = partnerService.getByAccount(account);
    	model.addAttribute("partner", self);
    	model.addAttribute("flag", self != null);// 标志是否有数据
    	
        return PREFIX + "partner_view.html";
    }
    
    
    @RequestMapping("/activityOverview")
    public String activityOverview() {
    	
    	return PREFIX + "activityOverview.html";
    }
    
    
    
    @RequestMapping(value = "/soloAccount", method = RequestMethod.POST)
	@ResponseBody
	public Object soloAccount(PartnerInfo data, @RequestParam(value = "updId", required = false) Long updId) {
		if (StringUtils.isBlank(data.getLoginName())) {
			return ErrorTip.buildErrorTip("账号不能为空");
		}
		List<PartnerInfo> partnerInfoList = partnerService.findByAccount(data.getLoginName());
		if (updId != null) {
			if (partnerInfoList != null && partnerInfoList.size() > 1) {
				return ErrorTip.buildErrorTip("当前已有相同账号");
			}
			if (partnerInfoList != null) {
				for (int i = 0; i < partnerInfoList.size(); i++) {
					if (!partnerInfoList.get(i).getPartnerId().equals(updId)) {
						return ErrorTip.buildErrorTip("当前已有相同账号");
					}
				}
			}
		} else {
			if (!partnerInfoList.isEmpty()) {
				return ErrorTip.buildErrorTip("当前已有相同账号");
			}
		}
		return SUCCESS_TIP;
	}
    
    private long[] internalConver2Long(String[] values) {
    	if ( values == null || values.length < 1 ) 
    		return null;
    	
    	long[] res = new long[values.length];
    	for (int i=0; i< res.length; i++) {
    		res[i] = Long.valueOf(values[i]);
    	}
    	return res;
    }
    
    /**
     * id列表（纯数字，多个id英文逗号分隔）
     */
    private static final Pattern PATTERN_IDS = Pattern.compile("^\\d+(,\\d+)*$");
    
    /**
	 * 
	 * 删除产品关联 - 支持批量删除
	 * 
	 * @param passwd
	 * @param customerId
	 * @return
	 */
	@RequestMapping(value = "/deleteBySeqId")
	@ResponseBody
	public Object changeStatus(String seqId) {
		if ( seqId == null || !PATTERN_IDS.matcher(seqId).matches() ) 
			return ErrorTip.buildErrorTip("参数错误");
		
		long[] ids = internalConver2Long(seqId.split(","));
		PartnerProduct data = partnerProductService.get(ids[0]);
		
		if (data == null) {
			LOG.debug("非法请求数据");
			return ErrorTip.buildErrorTip("操作失败");
		}
		ShiroUser staff = ShiroKit.getUser();
		
		PartnerInfo partnerInfo = partnerService.getAll(data.getPartnerId());
		// 校验所有的seqId是否合法
		if ( partnerInfo == null ) 
			return ErrorTip.buildErrorTip("未找到相关的合作伙伴信息");
		
		List<PartnerProduct> list = partnerInfo.getPartnerProductList();
		if ( list == null || list.isEmpty() ) 
			return ErrorTip.buildErrorTip("未查询到相关产品信息");
		
		Set<Long> existIds = new TreeSet<Long>();
		for ( PartnerProduct pp : list ) {
			existIds.add(pp.getSeqId());
		}
		// 检查要删除的所有产品是否全部存在 - 有一个不存在就提示操作失败
		for ( long id : ids ) {
			if ( !existIds.contains(id) ) {
				LOG.info("未查询到相关产品信息:" + data.getPartnerId() + "->" + id);
				return ErrorTip.buildErrorTip("未查询到相关产品信息");
			}
		}
		
		// TODO 
//		if (!isAdmin()) {
//			if (!data.getPartnerId().equals(staff.getStaffId())) {
//				return fail("没有权限操作这条数据");
//			}
//		}
		
		// 订单处理记录-记录前数据
        String startRecord = "";
        JsonConfig jsonConfig = new JsonConfig();    
//        jsonConfig.registerJsonValueProcessor(Date.class , new JsonDateValueProcessor());
        startRecord = JSONObject.fromObject(partnerInfo, jsonConfig).toString();
		// 直接批量删除
        partnerProductService.deleteByIds(ids);

		// 新增订单处理记录
		partnerInfo = partnerService.getAll(data.getPartnerId());
		OrderDealRecordWithBLOBs record = partnerService.createOrderDealRecordWithBLOBs(partnerInfo, staff);
		record.setStartRecord(startRecord);
		orderDealRecordService.insert(record);
		return SUCCESS_TIP;
	}
	
	
	/**
	 * 修改等级 
	 * @param partnerId
	 * @param partnerLevel
	 * @return
	 */
	@RequestMapping(value = "/modifyPartnerLevel.ajax")
	@ResponseBody
	public Object modifyPartnerLevel(Long partnerId,Integer partnerLevel) {
    	PartnerInfo partnerInfo = partnerService.get(partnerId);
    	if (null == partnerInfo){
    		return ErrorTip.buildErrorTip("修改的合作伙伴不存在");
    	}
    	partnerInfo.setPartnerLevel(partnerLevel);
    	partnerService.update(partnerInfo);
		return SUCCESS_TIP;
	}
	
	
	/**
	 * 
	 * 修改合作伙伴状态
	 * 
	 * @param passwd
	 * @param customerId
	 * @return
	 */
	@RequestMapping(value = "/changeStatus.ajax")
	@ResponseBody
	public Object changeStatus(Integer status, Long partnerId) {
		
		if (status < 0 || status > 2) {
			return ErrorTip.buildErrorTip("非法操作");
		}
		
		PartnerInfo data = partnerService.get(partnerId);
		ShiroUser staff = ShiroKit.getUser();
		
		data.setStatus(status + "");
		
		// 订单处理记录-记录前数据
        String startRecord = "";
        JsonConfig jsonConfig = new JsonConfig();    
//        jsonConfig.registerJsonValueProcessor(Date.class , new JsonDateValueProcessor());
        startRecord = JSONObject.fromObject(partnerService.getAll(partnerId), jsonConfig).toString();
		partnerService.update(data);
		
		// 新增订单处理记录
        PartnerInfo partnerInfo = partnerService.getAll(partnerId);
        OrderDealRecordWithBLOBs record = partnerService.createOrderDealRecordWithBLOBs(partnerInfo, staff);
        record.setStartRecord(startRecord);
        orderDealRecordService.insert(record);
		return SUCCESS_TIP;
	}
    
	
	/**
	 * 
	 * 合作伙伴密码重置
	 * 
	 * @param passwd
	 * @param customerId
	 * @return
	 */
	@RequestMapping(value = "/resetPassword.ajax")
	@ResponseBody
	public Object resetPassword(Long partnerId) {
		PartnerInfo partnerInfo = partnerService.get(partnerId);
		if (partnerInfo == null) {
			return ErrorTip.buildErrorTip("密码重置失败");
		}
		if (StringUtils.isBlank(partnerInfo.getMobile())) {
			return ErrorTip.buildErrorTip("合作伙伴手机号码为空");
		}
		boolean flag = partnerService.resetPassword(partnerInfo);
		if (flag) {
			return SUCCESS_TIP;
		} else {
			return ErrorTip.buildErrorTip("密码重置失败");
		}
	}
	
	
	@RequestMapping(value = "/get.ajax")
	@ResponseBody
	public Object get(Long partnerId) {
		PartnerInfo data = partnerService.getAll(partnerId);
		
		if ( data == null )
			return ErrorTip.buildErrorTip(BizExceptionEnum.REQUEST_NULL);
		
		return data;
	}
	
	
	
	@RequestMapping(value = "/add.ajax", method = RequestMethod.POST)
	@ResponseBody
	public Object add(PartnerInfo newData, @RequestParam(value = "updId", required = false) Long updId) {
		PartnerInfo data = null;
		if (updId != null) {
			data = partnerService.get(updId);
			if (data == null) {
				LOG.debug("更新合作伙伴id[{}]异常", updId);
				return ErrorTip.buildErrorTip("更新数据异常");
			}
			data.setRealName(newData.getRealName());
			data.setMobile(newData.getMobile());
			data.setEmail(newData.getEmail());
			data.setAddress(newData.getAddress());
			
			data.setPartnerLevel(newData.getPartnerLevel());
			data.setStatus(newData.getStatus());
			
			data.setPartnerProductList(newData.getPartnerProductList());
		} else {
			data = newData;
			data.setCreditAmount(new BigDecimal(0));
		}
		String uuid = UUID.randomUUID().toString();
		uuid = uuid.replaceAll("-", "");
		data.setIdentityId(uuid);
		data.setIsDeleted(0);
		
		ShiroUser staff = ShiroKit.getUser();
		if (staff == null) {
			return ErrorTip.buildErrorTip("用户未登录");
		}
		
		setBaseEntity(data, staff, data.getPartnerId());
		
//		if (StringTools.isNotEmptyString(data.getCoopGroupId())) {
//			attachmentInterface.formalAttachmentGroup(data.getCoopGroupId());
//		}
		partnerService.saveAndUpdate(data, staff);
		return SUCCESS_TIP;
	}
	
	
	
	private void setBaseEntity(PartnerInfo entity, ShiroUser staff, Object id) {
        if (id == null) {
            entity.setCreator(staff.getAccount());
            entity.setCreateTime(new Date());
            entity.setUpdator("");
            //add model : set default info
            entity.setShorterName(entity.getPartnerName());
            entity.setPartnerNo(entity.getIdentityId());
            entity.setPartnerType(PartnerInfo.PARTNER_TYPE_FLOW);
            entity.setSettlementPattern(PartnerInfo.SETTLEMENT_PATTERN_FIXED);
            entity.setSettlementDiscountRatio(null);
            
        } else {
            entity.setUpdator(staff.getAccount());
            entity.setUpdateTime(new Date());
        }

    }
    
}
