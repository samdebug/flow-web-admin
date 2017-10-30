package com.yzx.flow.modular.flow.contoller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.yzx.flow.common.constant.tips.ErrorTip;
import com.yzx.flow.common.constant.tips.Tip;
import com.yzx.flow.common.controller.BaseController;
import com.yzx.flow.common.exception.BizExceptionEnum;
import com.yzx.flow.common.exception.BussinessException;
import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.page.PageInfoBT;
import com.yzx.flow.common.persistence.model.AreaCode;
import com.yzx.flow.common.persistence.model.CustomerInfo;
import com.yzx.flow.common.persistence.model.FlowProductRemodel;
import com.yzx.flow.common.util.Constant;
import com.yzx.flow.core.shiro.ShiroKit;
import com.yzx.flow.core.util.ToolUtil;
import com.yzx.flow.modular.flow.service.IFlowProductRemodelService;
import com.yzx.flow.modular.order.service.IOrderDetailService;
import com.yzx.flow.modular.partner.service.IPartnerProductService;
import com.yzx.flow.modular.system.dao.CustomerInfoDao;

@Controller
@RequestMapping("/flowProductRemodel")
public class FlowProductRemodelController extends BaseController{
	
	//日志记录
	private static final Logger LOG = LoggerFactory.getLogger(FlowProductRemodelController.class);
	
	//前缀
	private String PREFIX = "/flow/flowProductRemodel/";
	
	@Autowired
	private IFlowProductRemodelService flowProductRemodelService;
	
	@Autowired
	private  IPartnerProductService partnerProductService;
	
	@Autowired
	private IOrderDetailService orderDetailService;
    
    @Autowired
	private CustomerInfoDao customerInfoDao;
    
    
    @ModelAttribute
    public void getAllArea(Model model){
    	List<AreaCode> list = this.flowProductRemodelService.selectAllArea();
    	model.addAttribute("area",list);
    }
    
    /**
     * 跳转到产品设置首页
     */
    @RequestMapping("")
    public String index() {
        return PREFIX + "flowProductRemodel.html";
    }
    
    /**
     * 查看产品设置详情
     */
    @RequestMapping("/detail/{productId}")
    public String Detail(@PathVariable("productId")String productId, Model model) {
    	
    	Long id = Long.parseLong(productId);
    	
    	FlowProductRemodel flowProductRemodel = this.flowProductRemodelService.getProductById(id);
    	
    	model.addAttribute("flowProductRemodel", flowProductRemodel);
        
    	return PREFIX + "flowProductRemodel_detail.html";
    }
    
    /**
     * 产品类别查询
     * @param productType
     * @return
     */
    @RequestMapping(value = "/getByProductType")
	@ResponseBody
	public Object getByProductType(@RequestParam(value = "productType") Integer productType) {
    	FlowProductRemodel flowProductRemodel = new FlowProductRemodel();
    	flowProductRemodel.setProductType(productType);
		List<FlowProductRemodel> list = this.flowProductRemodelService.getByFlowProductRemodel(flowProductRemodel);
		return list;
	}
    
    /**
     * 产品类型、适用区域、供应商条件查询
     * @param productIds
     * @param zone
     * @param operator
     * @return
     */
    @RequestMapping(value = "/getFlowProductRemodel")
	@ResponseBody
	public Map<String, Object> getFlowProductRemodel(
			@RequestParam(value = "productCodes", required = false) String[] productCodes,
			@RequestParam(value = "productIds", required = false) String[] productIds,
			@RequestParam(value = "zone", required = false) String zone,
			@RequestParam(value = "operator", required = false) String operator) {
    	
		List<FlowProductRemodel> list = this.flowProductRemodelService.getFlowProductRemodelList(productCodes,productIds, zone, operator);
		
		Map<String, Object> resMap = new HashMap<String, Object>();
		resMap.put("success", Boolean.valueOf(true));
		resMap.put("data", JSON.toJSONString(list));
		return resMap;
	}
    
    /**
     * 产品类型、适用区域、供应商条件查询
     * @param productIds
     * @param zone
     * @param operator
     * @return
     */
    @RequestMapping(value = "/getProductById")
	@ResponseBody
	public Map<String, Object> getFlowProductRemodel(@RequestParam(value = "productId", required = false) Long productId) {
    	
		FlowProductRemodel flowProductRemodel = this.flowProductRemodelService.getProductById(productId);
		
		Map<String, Object> resMap = new HashMap<String, Object>();
		resMap.put("success", Boolean.valueOf(true));
		resMap.put("data", JSON.toJSONString(flowProductRemodel));
		return resMap;
	}
    
	/**
	 * 按条件搜索
	 * 两表联查 flow_product_remodel,area_code
	 * @param operatorCode
	 * @param text
	 * @return
	 */
	@RequestMapping(value = "/searchProduct")
	@ResponseBody
	public Object searchProduct(@RequestParam(value = "operatorCode") String operatorCode,@RequestParam(value = "text") String text) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("operatorCode", operatorCode);
		params.put("text", text.trim());
		List<FlowProductRemodel> list = this.flowProductRemodelService.searchProduct(params);
		return list;
	}
	
	/**
	 * 客户产品条件查询
	 * 三表联查（先查customerInfo）flow_product_remodel,partner_product,area_code
	 * @param partnerInfoId
	 * @param productType
	 * @param productIds
	 * @param customerId
	 * @param operatorCode
	 * @param text
	 * @return
	 */
	@RequestMapping(value = "/flowProductRemodel")
	@ResponseBody
	public Object getByPartnerInfoId(
			@RequestParam(value = "partnerInfoId", required = false) Long partnerInfoId,
			@RequestParam(value = "productType", required = false) Integer productType,
			@RequestParam(value = "productIds", required = false) Long[] productIds,
			@RequestParam(value = "customerId", required = false) Long customerId,
			@RequestParam(value = "operatorCode", required = false) String operatorCode,
			@RequestParam(value = "text", required = false) String text) {
		
		if ( customerId != null && customerId > 0L ) {
			CustomerInfo customerInfo = customerInfoDao.getCustomerInfoByCustomerId(customerId);
			if (customerInfo != null) 
			    partnerInfoId = customerInfo.getPartnerId();
		}
		List<FlowProductRemodel> list = this.flowProductRemodelService.getByPartnerInfoType(partnerInfoId, productType,
				productIds,operatorCode,text);
		return list;
	}
	
    
    /**
     * 查询产品设置列表
     */
    @RequestMapping("/list")
    @ResponseBody
    public Object list(Page<FlowProductRemodel> page) {
    	if(null == page.getSort()){
			page.setSort("product_id");
		}
		Page<FlowProductRemodel> list = this.flowProductRemodelService.pageQuery(page);
		return new PageInfoBT<FlowProductRemodel>(list.getDatas(), page.getTotal());
	
    }
    
    /**
     * 查询code是否可用
     * @param flowProductRemodel
     * @return
     */
    @RequestMapping(value = "/soloProductCode.ajax")
   	@ResponseBody
   	public Tip soloProductCode(FlowProductRemodel flowProductRemodel) {
   		if (null == flowProductRemodel.getProductCode()) {
   			return new ErrorTip(BizExceptionEnum.REQUEST_NULL);
   		}
   		boolean flag = this.flowProductRemodelService.solo(flowProductRemodel);
   		if (!flag) {
   			return super.SUCCESS_TIP;
   		} else {
   			return new ErrorTip(BizExceptionEnum.REQUEST_INVALIDATE);
   		}
   		
   	}
    
    /**
     * 查询名称是否可用
     * @param flowProductRemodel
     * @return
     */
    @RequestMapping(value = "/soloProductName.ajax")
   	@ResponseBody
   	public Tip soloProductName(FlowProductRemodel flowProductRemodel) {
   		if (null == flowProductRemodel.getProductName()) {
   			return new ErrorTip(BizExceptionEnum.REQUEST_NULL);
   		}
   		boolean flag = this.flowProductRemodelService.solo(flowProductRemodel);
   		if (!flag) {
   			return super.SUCCESS_TIP;
   		} else {
   			return new ErrorTip(BizExceptionEnum.REQUEST_INVALIDATE);
   		}
   	}
    
    /**
     * 跳转到添加产品设置
     */
    @RequestMapping("/flowProductRemodel_add")
    public String flowProductRemodelAdd(Model model) {
        return PREFIX + "flowProductRemodel_add.html";
    }
    
    /**
     * 添加产品设置
     */
    @RequestMapping("/add")
    @ResponseBody
    public Tip add(FlowProductRemodel flowProductRemodel) {
    	//校验参数
    	if(ToolUtil.isEmpty(flowProductRemodel.getProductCode()) || ToolUtil.isEmpty(flowProductRemodel.getProductName())
    		|| ToolUtil.isEmpty(flowProductRemodel.getZone()) || ToolUtil.isEmpty(flowProductRemodel.getOperatorCode())
    		|| null == flowProductRemodel.getFlowAmount() || null == flowProductRemodel.getProductPrice()
    		|| null == flowProductRemodel.getActivePeriod()) {
    
    		throw new BussinessException(BizExceptionEnum.REQUEST_NULL);
    	
    	} else {
    		
    		//产品类型默认为叠加包
    		if(null == flowProductRemodel.getProductType()) {
    			flowProductRemodel.setProductType(Constant.PACKAGE_TYPE_SUPERPOSITION);
    		}
    		
    		//产品类型默认为有效
    		if(null == flowProductRemodel.getIsValid()) {
    			//0--false为有效，1--true为无效
    			flowProductRemodel.setIsValid(false);
    		}
    		
    		String loginName = ShiroKit.getLoginName();
    		flowProductRemodel.setCreator(loginName);
    		flowProductRemodel.setCreateTime(new Date());
    		this.flowProductRemodelService.saveAndUpdate(flowProductRemodel);
    		
    		LOG.info("用户:"+loginName+"添加产品>>产品代码:"+flowProductRemodel.getProductCode()+",产品名称:"+flowProductRemodel.getProductName()
    		+",运营商:"+flowProductRemodel.getOperatorCodeDesc());
    		
    		return super.SUCCESS_TIP;
    	}
    }

    /**
     * 跳转到修改产品设置
     */
    @RequestMapping("/flowProductRemodel_update/{productId}")
    public String flowProductRemodelUpdate(@PathVariable("productId") String productId, Model model) {
    	
    	Long id = Long.parseLong(productId);
    	
    	FlowProductRemodel flowProductRemodel = this.flowProductRemodelService.getProductById(id);
    	
    	model.addAttribute("flowProductRemodel", flowProductRemodel);
        
    	return PREFIX + "flowProductRemodel_update.html";
    }
    
    /**
     * 修改产品设置
     */
    @RequestMapping("/update")
    @ResponseBody
    public Tip update(FlowProductRemodel flowProductRemodel) {
    	//校验参数
    	if(ToolUtil.isEmpty(flowProductRemodel.getProductCode()) || ToolUtil.isEmpty(flowProductRemodel.getProductName())
        	|| ToolUtil.isEmpty(flowProductRemodel.getZone()) || ToolUtil.isEmpty(flowProductRemodel.getOperatorCode())
        	|| null == flowProductRemodel.getFlowAmount() || null == flowProductRemodel.getProductPrice()
        	|| null == flowProductRemodel.getActivePeriod() || null == flowProductRemodel.getProductId()) {
    		
    		throw new BussinessException(BizExceptionEnum.REQUEST_NULL);
    	
    	} else {
    		
    		String loginName = ShiroKit.getLoginName();
    		flowProductRemodel.setUpdator(loginName);
    		flowProductRemodel.setUpdateTime(new Date());
    		this.flowProductRemodelService.saveAndUpdate(flowProductRemodel);
    		
    		LOG.info("用户:"+loginName+"修改产品>>产品id:"+flowProductRemodel.getProductId()+",产品代码:"+flowProductRemodel.getProductCode()
    					+",产品名称:"+flowProductRemodel.getProductName()+",运营商:"+flowProductRemodel.getOperatorCodeDesc());
    		
    		return super.SUCCESS_TIP;
    	}
    }
    
    /**
     * 删除产品设置
     */
    @RequestMapping("/delete")
    @ResponseBody
    public Tip delete(Long productId) {
    	//校验参数
    	if(null == productId) {
    		throw new BussinessException(BizExceptionEnum.REQUEST_NULL);
    	}
    	FlowProductRemodel flowProductRemodel = this.flowProductRemodelService.getProductById(productId);
    	if(null == flowProductRemodel) {
    		throw new BussinessException(BizExceptionEnum.REQUEST_NULL);
    	}
    	
    	this.flowProductRemodelService.delete(productId);
    	
    	String loginName = ShiroKit.getLoginName();
    	LOG.info("用户:"+loginName+"删除产品>>产品id:"+flowProductRemodel.getProductId()+",产品代码:"+flowProductRemodel.getProductCode());
    	
    	return super.SUCCESS_TIP;
    }
}
