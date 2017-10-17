package com.yzx.flow.modular.customer.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yzx.flow.common.controller.BaseController;
import com.yzx.flow.common.excel.TemplateExcel;
import com.yzx.flow.common.excel.TemplateExcelManager;
import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.page.PageInfoBT;
import com.yzx.flow.common.persistence.model.CustomerTradeFlow;
import com.yzx.flow.common.persistence.model.Staff;
import com.yzx.flow.core.portal.PortalParamSetter;
import com.yzx.flow.core.portal.annotation.AutoSetPortalCustomer;
import com.yzx.flow.core.portal.annotation.PortalParamMeta;
import com.yzx.flow.core.shiro.ShiroKit;
import com.yzx.flow.modular.customer.service.ICustomerBalanceDayService;
import com.yzx.flow.modular.customer.service.ICustomerTradeFlowService;

/**
 * 
 * <b>Title：</b>CustomerTradeFlowController.java<br/>
 * <b>Description：</b> <br/>
 * <b>@author： </b>v5480<br/>
 * <b>@date：</b>2015-09-02 10:17:41<br/>
 * <b>Copyright (c) 2015 szwisdom Tech.</b>
 * 
 */
@Controller
@RequestMapping("/customerTradeFlow")
public class CustomerTradeFlowController extends BaseController {
	private static final Logger LOG = LoggerFactory.getLogger(CustomerTradeFlowController.class);
	@Autowired
	@Qualifier("customerTradeFlowService")
	private ICustomerTradeFlowService customerTradeFlowService;

	@Autowired
	@Qualifier("customerBalanceDayService")
	private ICustomerBalanceDayService customerBalanceDayService;
	
	/**
     * 交易类型 1：结算   2：充值   3：授信
     */
    public static final Integer TRADE_TYPE_RECHARGE = 2;

    
  	@RequestMapping(value = "/query")
  	@ResponseBody
  	@AutoSetPortalCustomer({ @PortalParamMeta(setter = PortalParamSetter.CUSTOMER_ID_STRING, paramIndex = 1)})
  	public PageInfoBT<CustomerTradeFlow> pageQuery(Page<CustomerTradeFlow> page, String customerId,
  			@RequestParam(value = "tradeTypes[]", required = false) Integer[] tradeTypes) {
  		List<Integer> tradeTypeList = null;
  		if (tradeTypes != null) {
  			tradeTypeList = new ArrayList<>();
  			for (Integer tradeType : tradeTypes) {
  				if (tradeType == 0) {
  					tradeTypeList = null;
  					break;
  				}
  				tradeTypeList.add(tradeType);
  			}
  		}
  		page.getParams().put("customerId", customerId);
  		page.getParams().put("tradeTypeList", tradeTypeList);
  		page.setAssembleOrderBy("trade_flow_id");
  		if (page.getOrder()==null) {
		page.setOrder("DESC");
		}
		if (page.getSort()==null) {
			page.setSort("trade_time");
		}
  		PageInfoBT<CustomerTradeFlow> resPage = customerTradeFlowService.pageQuery(page);
  		return resPage;
  	}

  	@RequestMapping(value = "/init")
  	@ResponseBody
  	@AutoSetPortalCustomer({ @PortalParamMeta(setter = PortalParamSetter.CUSTOMER_ID_LONG)})
  	public Map<String, Object> init(Long pkId) {
  	    Map<String, Object> map = new HashMap<String, Object>();
  	    // 取得已充金额之和
          double rechargeAmount = customerBalanceDayService.
                  selectCustomerSUMTraderAmount(pkId, null, null, TRADE_TYPE_RECHARGE);
          map.put("rechargeAmount", rechargeAmount);
  	    // 取得日账单之和(结算)
          double sumCustomerAmount = customerBalanceDayService.selectSUMCustomerAmount(Long.valueOf(pkId));
          map.put("sumCustomerAmount", 0 - sumCustomerAmount);
  	    return map;
  	}

  	@RequestMapping(value = "/add", method = RequestMethod.POST)
  	@ResponseBody
  	public Map<String, Object> add(@RequestBody CustomerTradeFlow data) {
  	    Staff staff = ShiroKit.getStaff();
  	    Map<String, Object> resMap=new HashMap<String, Object>();
  		customerTradeFlowService.saveCustomerTradeFlow(data, staff);
  		resMap.put("success", true);
  		resMap.put("message", "操作成功");
  		return resMap;
  	}

  	
  	@RequestMapping(value = "/delete")
  	@ResponseBody
  	public Map<String, Object> delete(Long tradeFlowId) {
  		Map<String, Object> resMap=new HashMap<String, Object>();
  		customerTradeFlowService.delete(tradeFlowId);
  		resMap.put("success", true);
  		resMap.put("message", "删除成功");
  		return resMap;
  	}

  	@RequestMapping(value = "/get")
  	@ResponseBody
  	public Map<String, Object> get(Long tradeFlowId) {
  		 Map<String, Object> resMap=new HashMap<String, Object>();
  		CustomerTradeFlow data = customerTradeFlowService.get(tradeFlowId);
  		resMap.put("success", true);
  		resMap.put("data", data);
  		resMap.put("message", "删除成功");
  		return resMap;
  	}
  	
  	
  	
  	/**
	 * 导出账单明细
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/export")
	public void export(Page<CustomerTradeFlow> page,int pkId,
			@RequestParam(value = "tradeTypes[]", required = false) Integer[] tradeTypes) throws Exception {
		try {
			// 生成Excel
			Map<String, Object> beanMap = new HashMap<String, Object>();
			page.setRows(65530);
			
			//查询数据
			List<Integer> tradeTypeList = null;
			if (tradeTypes != null) {
				tradeTypeList = new ArrayList<>();
				for (Integer tradeType : tradeTypes) {
					if (tradeType == 0) {
						tradeTypeList = null;
						break;
					}
					tradeTypeList.add(tradeType);
				}
			}
			page.getParams().put("customerId", pkId);
			page.getParams().put("tradeTypeList", tradeTypeList);
			page.setAssembleOrderBy("trade_flow_id");
			List<CustomerTradeFlow> list = customerTradeFlowService.pageQuery(page).getRows();
			
			beanMap.put("list", list);
			beanMap.put("startDate", page.getParams().get("createStartTime"));
			beanMap.put("endDate", page.getParams().get("createEndTime"));
			TemplateExcelManager.getInstance().createExcelFileAndDownload(TemplateExcel.CUSTOMER_RECHARGE_DETAIL, beanMap);
		} catch (Exception e) {
			LOG.error("导出报表出错！！！", e);
		}
	}
}