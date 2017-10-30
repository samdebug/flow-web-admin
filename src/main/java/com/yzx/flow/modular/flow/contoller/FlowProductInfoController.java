package com.yzx.flow.modular.flow.contoller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.javassist.expr.NewArray;
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
import com.yzx.flow.common.constant.tips.SuccessTip;
import com.yzx.flow.common.constant.tips.Tip;
import com.yzx.flow.common.controller.BaseController;
import com.yzx.flow.common.exception.BizExceptionEnum;
import com.yzx.flow.common.exception.BussinessException;
import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.page.PageInfoBT;
import com.yzx.flow.common.persistence.model.AreaCode;
import com.yzx.flow.common.persistence.model.CustomerInfo;
import com.yzx.flow.common.persistence.model.FlowPackageInfo;
import com.yzx.flow.common.persistence.model.FlowPlusProduct;
import com.yzx.flow.common.persistence.model.FlowProductInfo;
import com.yzx.flow.core.shiro.ShiroKit;
import com.yzx.flow.core.util.ToolUtil;
import com.yzx.flow.modular.flow.service.IFlowPlusProductService;
import com.yzx.flow.modular.flow.service.IFlowProductInfoService;
import com.yzx.flow.modular.flowOrder.Service.IFlowPackageInfoService;
import com.yzx.flow.modular.order.service.IOrderDetailService;
import com.yzx.flow.modular.partner.service.IPartnerProductService;
import com.yzx.flow.modular.system.dao.CustomerInfoDao;

/**
 * 产品设置控制器
 *
 * @author wxl
 * @date 2017/08/24 11:39
 */
@Controller
@RequestMapping("/flowProductInfo")
public class FlowProductInfoController extends BaseController {
	
	private static final Logger LOG = LoggerFactory.getLogger(FlowProductInfoController.class);
	
    private String PREFIX = "/flow/flowProductInfo/";
    
    @Autowired
    private IFlowProductInfoService flowProductInfoService;

    @Autowired
    private IFlowPackageInfoService flowPackageInfoService;
    
    @Autowired
    private IFlowPlusProductService flowPlusProductService;
    
    @Autowired
	private  IPartnerProductService partnerProductService;
	
	@Autowired
	private IOrderDetailService orderDetailService;
    
    @Autowired
	private CustomerInfoDao customerInfoDao;
    
    
    /**
     * 跳转到产品设置首页
     */
    @RequestMapping("")
    public String index() {
        return PREFIX + "flowProductInfo.html";
    }

    /**
     * 跳转到添加产品设置
     */
    @RequestMapping("/flowProductInfo_add")
    public String flowProductInfoAdd(Model model) {
    	List<AreaCode> list = flowPackageInfoService.selectAllArea();
    	model.addAttribute("area",list);
        return PREFIX + "flowProductInfo_add.html";
    }
    
    @RequestMapping(value = "/soloProductName.ajax")
	@ResponseBody
	public Tip soloProductName(FlowProductInfo data) {
		if (null == data.getProductName()) {
			return new ErrorTip(BizExceptionEnum.REQUEST_NULL);
		}
		boolean flag = flowProductInfoService.solo(data);
		if (!flag) {
			return new ErrorTip(BizExceptionEnum.REQUEST_INVALIDATE);
		}
		return new SuccessTip("true");
	}
    
    @RequestMapping(value = "/soloProductCode.ajax")
	@ResponseBody
	public Tip soloProductCode(FlowProductInfo data) {
		if (null ==data.getProductCode()) {
			return new ErrorTip(BizExceptionEnum.REQUEST_NULL);
		}
		boolean flag = flowProductInfoService.solo(data);
		if (!flag) {
			return new ErrorTip(BizExceptionEnum.REQUEST_INVALIDATE);
		}
		return new SuccessTip("true");
	}
    /**
     * 通过流量包名称获得流量包列表
     */
    @RequestMapping("/selectPackageByName")
    @ResponseBody
    public Map<String, Object> getPackage(Page<FlowPackageInfo> page) {
		Page<FlowPackageInfo> pageQuery = this.flowPackageInfoService.pageQuery(page);
    	Map<String, Object> map = new HashMap<String, Object>();
    	map.put("packageList", pageQuery.getDatas());
    	return map;
    }
    
    /**
     * 跳转到修改产品设置
     */
    @RequestMapping("/flowProductInfo_update/{flowProductInfoId}")
    public String flowProductInfoUpdate(@PathVariable Long flowProductInfoId, Model model) {
    	List<AreaCode> list = flowPackageInfoService.selectAllArea();
    	model.addAttribute("area",list);
		FlowProductInfo flowProductInfo = this.flowProductInfoService.get(flowProductInfoId);
		if(flowProductInfo.getProductType()==3){
			FlowPlusProduct flowPlusProduct = flowPlusProductService.getByProductId(flowProductInfo.getProductId());
			flowProductInfo.setFlowPlusProduct(flowPlusProduct);
		}
    	model.addAttribute("flowProductInfo", flowProductInfo);
        return PREFIX + "flowProductInfo_edit.html";
    }

    /**
     * 获取产品设置列表
     */
    @RequestMapping(value = "/list")
    @ResponseBody
    public Object list(Page<FlowProductInfo> page) {
    	if(null == page.getSort()){
			page.setSort("product_id");
		}
		Page<FlowProductInfo> list = this.flowProductInfoService.pageQuery(page);
		for (int i = 0; i < list.getDatas().size(); i++) {
			FlowProductInfo flowProductInfo = list.getDatas().get(i);
			FlowPackageInfo flowPackageInfo = flowProductInfo.getFlowPackageInfo();
			if (flowPackageInfo != null) {
				if (flowPackageInfo.getIsCombo().equals(FlowPackageInfo.COMBO_PACKAGE)) {
					List<FlowPackageInfo> lpList = this.flowPackageInfoService
							.selectInPackageId(flowPackageInfo.getComboPackageStr());
					list.getDatas().get(i).getFlowPackageInfo().setFlowPackageInfos(lpList);
				}
			}
		}
		
		return new PageInfoBT<FlowProductInfo>(list.getDatas(), page.getTotal());
	
    }

    /**
     * 新增产品设置
     */
    @RequestMapping(value = "/add")
    @ResponseBody
    public Tip add(FlowProductInfo data ,String cardType ,String channelType) {
    	if(data != null && data.getProductCode() != null && data.getProductName() != null &&
    		data.getProductType() != null && data.getProductPrice() != null && 
    		data.getProductDesc() != null && data.getPackageId() != null) {
    		data.setCreator(ShiroKit.getLoginName());
    		data.setCreateTime(new Date());
    		if(cardType != null && channelType != null){
    			FlowPlusProduct flowPlusProduct = new FlowPlusProduct();
    			flowPlusProduct.setCardType(cardType);
    			flowPlusProduct.setChannelType(channelType);
    			data.setFlowPlusProduct(flowPlusProduct);
    		}
    		this.flowProductInfoService.saveAndUpdate(data);
    		LOG.info(ShiroKit.getLoginName()+"新增了产品："+data.getProductCode());
    		return super.SUCCESS_TIP;
    	} else {
    		 throw new BussinessException(BizExceptionEnum.REQUEST_NULL);
    	}
        
    }

    /**
     * 删除产品设置
     */
    @RequestMapping(value = "/delete")
    @ResponseBody
    public Object delete(@RequestParam(name="flowProductInfoId") String flowProductInfoId) {
    	int count=partnerProductService.getCountByproductId(Long.parseLong(flowProductInfoId));
		if(count>0){
			LOG.debug("不能删除,请先删除关联合作伙伴,共有[{}]关联",count);
			return new ErrorTip(500, "不能删除,请先删除关联合作伙伴");
		}
		int orderDetailCount = orderDetailService.getCountODByProductId(Long.parseLong(flowProductInfoId));
		if(orderDetailCount>0){
			LOG.debug("不能删除,请先删除关联订单,共有[{}]关联",orderDetailCount);
			return new ErrorTip(500, "不能删除,请先删除关联合作伙伴");
		}
		flowProductInfoService.delete(Long.parseLong(flowProductInfoId));
		return new SuccessTip("删除成功");
    }


    /**
     * 修改产品设置
     */
    @RequestMapping(value = "/update")
    @ResponseBody
    public Tip update(FlowProductInfo data) {
    	if(data != null && data.getProductCode() != null && data.getProductName() != null &&
    		data.getProductType() != null && data.getProductPrice() != null && 
    		data.getProductDesc() != null && data.getPackageId() != null) {
    		data.setUpdator(ShiroKit.getLoginName());
    		data.setUpdateTime(new Date());
    		this.flowProductInfoService.saveAndUpdate(data);
    		return super.SUCCESS_TIP;
    	} else {
    		 throw new BussinessException(BizExceptionEnum.REQUEST_NULL);
    	}
    }
    

    /**
     * 产品设置详情
     */
    @RequestMapping(value = "/detail/{flowProductInfoId}")
    public Object detail(@PathVariable Long flowProductInfoId, Model model) {
    	FlowProductInfo flowProductInfo = this.flowProductInfoService.get(flowProductInfoId);
		if(flowProductInfo.getProductType()==3){
			FlowPlusProduct flowPlusProduct = flowPlusProductService.getByProductId(flowProductInfo.getProductId());
			flowProductInfo.setFlowPlusProduct(flowPlusProduct);
		}
		model.addAttribute("flowProductInfo", flowProductInfo);
        return PREFIX + "flowProductInfo_detail.html";
    }
    
    
	@RequestMapping(value = "/getByProductType")
	@ResponseBody
	public Object getByProductType(@RequestParam(value = "productType") Integer productType) {
		List<FlowProductInfo> list = flowProductInfoService.getByProductType(productType);
		return list;
	}
	
	/**
	 * 按条件搜索
	 * @param productType
	 * @return
	 */
	@RequestMapping(value = "/searchProduct")
	@ResponseBody
	public Object searchProduct(@RequestParam(value = "operatorCode") String operatorCode,@RequestParam(value = "text") String text) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("operatorCode", operatorCode);
		params.put("text", text.trim());
		List<FlowProductInfo> list = flowProductInfoService.searchProduct(params);
		return list;
	}
	
	@RequestMapping(value = "/get.ajax")
	@ResponseBody
	public Map<String, Object> get(Long productId) {
		FlowProductInfo data = flowProductInfoService.get(productId);
		if (data.getProductType() == 3) {
			FlowPlusProduct plusProduct = flowPlusProductService.getByProductId(data.getProductId());
			data.setFlowPlusProduct(plusProduct);
		}
		if (data.getFlowPackageInfo().getIsCombo().equals(FlowPackageInfo.COMBO_PACKAGE)) {
			List<FlowPackageInfo> lpList = flowPackageInfoService
					.selectInPackageId(data.getFlowPackageInfo().getComboPackageStr());
			data.getFlowPackageInfo().setFlowPackageInfos(lpList);
		}
		Map<String, Object> hashMap =  new HashMap<String, Object>();
		hashMap.put("data", data);
		return hashMap;
	}
	
	@RequestMapping(value = "/flowProductInfo")
	@ResponseBody
	public Object getByPartnerInfoId(
			@RequestParam(value = "partnerInfoId", required = false) Long partnerInfoId,
			@RequestParam(value = "productType", required = false) Integer productType,
			@RequestParam(value = "productIds", required = false) Long[] productIds,
			@RequestParam(value = "customerId", required = false) Long customerId,
			@RequestParam(value = "operatorCode", required = false) String operatorCode,
			@RequestParam(value = "text", required = false) String text) {
//		if (isAdmin()) {
			if ( customerId != null && customerId > 0 ) {
				CustomerInfo customerInfo = customerInfoDao.getCustomerInfoByCustomerId(customerId);
				if (customerInfo != null) 
				    partnerInfoId = customerInfo.getPartnerId();
			}
//		} else {
//			PartnerInfo parentInfo = getParentInfo();
//			if (parentInfo == null) {
//				return fail("合作伙伴不能为空");
//			}
//			partnerInfoId = parentInfo.getPartnerId();
//		}
		List<FlowProductInfo> list = flowProductInfoService.getByPartnerInfoType(partnerInfoId, productType,
				productIds,operatorCode,text);
		return list;
	}

	@RequestMapping(value = "/operatorCode.ajax", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> getByZoneOperatorCode(@RequestParam(value = "zone", defaultValue = "") String zone,
			@RequestParam(value = "operatorCode", defaultValue = "") String operatorCode,
			@RequestParam(value = "isCombo", defaultValue = "") String isCombo,
			@RequestParam(value = "flowAmount", required = false) Integer flowAmount) {
		Map<String, Object> hashMap =  new HashMap<String, Object>();
		hashMap.put("flowPackageInfos", flowPackageInfoService.selectByZoneOperatorCode(zone, operatorCode, isCombo, flowAmount));
		return hashMap;
	}
}
