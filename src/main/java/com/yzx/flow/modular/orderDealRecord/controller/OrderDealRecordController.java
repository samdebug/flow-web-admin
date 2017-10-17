package com.yzx.flow.modular.orderDealRecord.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yzx.flow.common.constant.tips.ErrorTip;
import com.yzx.flow.common.controller.BaseController;
import com.yzx.flow.common.excel.ITemplateExcel;
import com.yzx.flow.common.excel.TemplateExcel;
import com.yzx.flow.common.excel.TemplateExcelManager;
import com.yzx.flow.common.exception.BizExceptionEnum;
import com.yzx.flow.common.exception.BussinessException;
import com.yzx.flow.common.model.DateMorpherEx;
import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.persistence.model.OrderDealRecord;
import com.yzx.flow.common.persistence.model.OrderDealRecordWithBLOBs;
import com.yzx.flow.common.persistence.model.OrderDetail;
import com.yzx.flow.common.persistence.model.OrderInfo;
import com.yzx.flow.common.persistence.model.PartnerInfo;
import com.yzx.flow.common.persistence.model.PartnerProduct;
import com.yzx.flow.common.util.Constant;
import com.yzx.flow.modular.orderDealRecord.service.IOrderDealRecordService;

import net.sf.json.JSONObject;
import net.sf.json.util.JSONUtils;

/**
 * 
 * <b>Title：</b>OrderDealRecordController.java<br/>
 * <b>Description：</b> <br/>
 * <b>@author： </b>v5480<br/>
 * <b>@date：</b>2015-10-21 14:17:38<br/>
 * <b>Copyright (c) 2015 szwisdom Tech.</b>
 * 
 */
@Controller
@RequestMapping("/orderDealRecord")
public class OrderDealRecordController extends BaseController {
	private static final Logger LOG = LoggerFactory.getLogger(OrderDealRecordController.class);
	
	@Autowired
	private IOrderDealRecordService orderDealRecordService;

	
	@RequestMapping(value = "/query")
	@ResponseBody
	public Object pageQuery(Page<OrderDealRecord> page) {
		return orderDealRecordService.pageQuery(page).getDatas();
	}

	
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ResponseBody
	public Object add(@ModelAttribute("orderDealRecord") OrderDealRecordWithBLOBs data) {
		orderDealRecordService.saveAndUpdate(data);
		return SUCCESS_TIP;
	}
	
	
	@RequestMapping(value = "/getOrderDealRecord")
    @ResponseBody
    public Object getOrderDealRecord(String sourceId, Integer type) {
	    
		if (StringUtils.isEmpty(sourceId) || (type < 1 || type > 2)) {
	        return new ErrorTip(BizExceptionEnum.REQUEST_NULL);
	    }
		
	    OrderDealRecordWithBLOBs info = new OrderDealRecordWithBLOBs();
	    info.setSourceId(sourceId);
	    info.setType(type);
        List<OrderDealRecordWithBLOBs> data = orderDealRecordService.selectByInfo(info);
        return data;
    }

	/**
	 * 下载订单处理记录
	 */
	@RequestMapping(value = "/download")
	@ResponseBody
	public void download(HttpServletResponse response, Long dealRecordId) {
	    Map<String, Object> beanParams = new HashMap<String, Object>();
	    try {
	        OrderDealRecordWithBLOBs data = orderDealRecordService.get(dealRecordId);
	        if (data == null) {
	            LOG.error("非法操作：OrderDealRecordController.download()");
	            throw new BussinessException(BizExceptionEnum.DB_RESOURCE_NULL);
	        }
	        
	        ITemplateExcel template = null;
	        // 合作伙伴产品协议订单
	        if (Constant.RECORD_TYPE_PARTNER == data.getType()) {
	            downloadPartnerOrder(beanParams, data);
	            template = TemplateExcel.PARTNER_ORDER_DEAL_RECORD;
	        } else {
	            // 客户订单
	            downloadCustomerOrder(beanParams, data);
	            template = TemplateExcel.CUSTOMER_ORDER_DEAL_RECORD;
	        }
	        TemplateExcelManager.getInstance().createExcelFileAndDownload(template, beanParams);
	    } catch (BussinessException e) {
	        throw e;
	    } catch (Exception e) {
	        LOG.error("导出报表出错！！！【"+ e.getMessage() +"】", e);
	        throw new BussinessException(BizExceptionEnum.CUSTOMER_FORMAT_ERROR, "导出报表出错！！！【" + e.getMessage() + "】");
	    }
	}
	
	/**
	 * 下载合作伙伴产品协议订单修改记录
	 */
	public void downloadPartnerOrder(Map<String, Object> beanParams, OrderDealRecordWithBLOBs data) {
	    Map<String, Object> classMap = new HashMap<String, Object>();
        if (StringUtils.isNotEmpty(data.getStartRecord())) {
            JSONObject jsonObjectBefore = JSONObject.fromObject(data.getStartRecord());
            classMap.put("partnerProductList", PartnerProduct.class);
            // 将JSON转换成PartnerInfo
            JSONUtils.getMorpherRegistry().registerMorpher(new DateMorpherEx(new String[] {"yyyy-MM-dd HH:mm:ss"}, (Date) null));
            PartnerInfo partnerInfoBrfore = (PartnerInfo)JSONObject.toBean(jsonObjectBefore, PartnerInfo.class, classMap);
            beanParams.put("partnerBefore", partnerInfoBrfore);
        } else {
            beanParams.put("partnerBefore", new PartnerInfo());
        }
        if (StringUtils.isNotEmpty(data.getEndRecord())) {
            JSONObject jsonObjectAfter = JSONObject.fromObject(data.getEndRecord());
            classMap.put("partnerProductList", PartnerProduct.class);
            // 将JSON转换成PartnerInfo
            JSONUtils.getMorpherRegistry().registerMorpher(new DateMorpherEx(new String[] {"yyyy-MM-dd HH:mm:ss"}, (Date) null));
            PartnerInfo partnerInfoAfter = (PartnerInfo)JSONObject.toBean(jsonObjectAfter, PartnerInfo.class, classMap);
            beanParams.put("partnerAfter", partnerInfoAfter);
        } else {
            beanParams.put("partnerAfter", new PartnerInfo());
        }
	}

	/**
	 * 下载客户订单修改记录
	 */
	public void downloadCustomerOrder(Map<String, Object> beanParams, OrderDealRecordWithBLOBs data) {
	    Map<String, Object> classMap = new HashMap<String, Object>();
	    if (StringUtils.isNotEmpty(data.getStartRecord())) {
	        JSONObject jsonObjectBefore = JSONObject.fromObject(data.getStartRecord());
	        classMap.put("orderDetailList", OrderDetail.class);
	        // 将JSON转换成OrderInfo
	        JSONUtils.getMorpherRegistry().registerMorpher(new DateMorpherEx(new String[] {"yyyy-MM-dd HH:mm:ss"}, (Date) null));
	        OrderInfo orderInfoBrfore = (OrderInfo)JSONObject.toBean(jsonObjectBefore, OrderInfo.class, classMap);
	        beanParams.put("orderBefore", orderInfoBrfore);
	        beanParams.put("orderId", String.valueOf(orderInfoBrfore.getOrderId()));
	    } else {
	        beanParams.put("orderBefore", new OrderInfo());
	    }
	    if (StringUtils.isNotEmpty(data.getEndRecord())) {
	        JSONObject jsonObjectAfter = JSONObject.fromObject(data.getEndRecord());
	        classMap.put("orderDetailList", OrderDetail.class);
	        // 将JSON转换成OrderInfo
	        JSONUtils.getMorpherRegistry().registerMorpher(new DateMorpherEx(new String[] {"yyyy-MM-dd HH:mm:ss"}, (Date) null));
	        OrderInfo orderInfoAfter = (OrderInfo)JSONObject.toBean(jsonObjectAfter, OrderInfo.class, classMap);
	        beanParams.put("orderAfter", orderInfoAfter);
	        beanParams.put("orderId", String.valueOf(orderInfoAfter.getOrderId()));
	    } else {
	        beanParams.put("orderAfter", new OrderInfo());
	    }
	}

	@RequestMapping(value = "/delete")
	@ResponseBody
	public Object delete(Long dealRecordId) {
		orderDealRecordService.delete(dealRecordId);
		return SUCCESS_TIP;
	}

	@RequestMapping(value = "/get")
	@ResponseBody
	public Object get(Long dealRecordId) {
	    OrderDealRecordWithBLOBs data = orderDealRecordService.get(dealRecordId);
		return data;
	}

	/**
	 * 更新的时候需额外传递updId,值跟主键值一样,被@ModelAttribute注释的方法会在此controller每个方法执行前被执行，
	 * 要谨慎使用
	 */
	@ModelAttribute("orderDealRecord")
	public void getForUpdate(@RequestParam(value = "updId", required = false) Long updId, Model model) {
		if (null != updId) {
			model.addAttribute("orderDealRecord", orderDealRecordService.get(updId));
		} else {
			model.addAttribute("orderDealRecord", new OrderDealRecord());
		}
	}
}