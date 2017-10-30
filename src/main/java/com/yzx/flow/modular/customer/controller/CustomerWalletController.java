package com.yzx.flow.modular.customer.controller;

import com.yzx.flow.common.controller.BaseController;
import com.yzx.flow.common.excel.TemplateExcel;
import com.yzx.flow.common.excel.TemplateExcelManager;
import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.page.PageInfoBT;
import com.yzx.flow.common.persistence.model.CustomerWallet;
import com.yzx.flow.common.persistence.model.FlowOrderInfo;
import com.yzx.flow.common.util.DateUtil;
import com.yzx.flow.core.portal.PortalParamSetter;
import com.yzx.flow.core.portal.annotation.AutoSetPortalCustomer;
import com.yzx.flow.core.portal.annotation.PortalParamMeta;
import com.yzx.flow.modular.customer.service.ICustomerWalletService;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * customerWallet控制器
 *
 * @author liuyufeng
 * @Date 2017-10-12 17:54:29
 */
@Controller
@RequestMapping("/customerWallet")
public class CustomerWalletController extends BaseController {
	
	private static final Logger LOG = LoggerFactory.getLogger(CustomerWalletController.class);
	
	@Autowired
	ICustomerWalletService customerWalletService;
	
	private String PREFIX = "/customerWallet/";

    /**
     * 跳转到customerWallet首页
     */
    @RequestMapping("")
    public String index(Model model) {
    	model.addAttribute("nowStartTime", DateUtil.dateToDateString(new Date(), "yyyy-MM-dd 00:00:00"));
    	model.addAttribute("nowEndTime", DateUtil.dateToDateString(new Date(), "yyyy-MM-dd 23:59:59"));
        return PREFIX + "customerWallet.html";
    }
    
    /**
     * 获取资金流水明细分页
     */
    @RequestMapping(value = "/list")
	@ResponseBody
	@AutoSetPortalCustomer(@PortalParamMeta(setter=PortalParamSetter.PAGE))
    public PageInfoBT<CustomerWallet> list(Page<CustomerWallet> page) {
		LOG.info("分页查询资金流水明细记录:" + page.toString());
		PageInfoBT<CustomerWallet> resPage = customerWalletService.pageQuery(page);
		return resPage;
    }

    /**
     * 跳转到订单详情页面
     */
    @RequestMapping("/detail")
    public String detail(@RequestParam(name="orderId") String orderId,Model model) {
    	model.addAttribute("orderId", orderId);
        return PREFIX + "customerWallet_view.html";
    }
    
	/**
	 * 导出资金流水明细表
	 * @param page
	 * @return
	 */
	@RequestMapping(value = "/export")
	public void export(HttpServletRequest request,HttpServletResponse response,Page<CustomerWallet> page) {
		try {
			  // 生成Excel
			  Map<String, Object> beanMap = new HashMap<String, Object>();
			  page.setRows(10000);
			  beanMap.put("list", customerWalletService.pageQuery(page).getRows());
			  TemplateExcelManager.getInstance().createExcelFileAndDownload(TemplateExcel.ACCOUNT_DETAIL, beanMap);
		} catch (Exception e) {
			LOG.error("导出报表出错！！！", e);
		}
	}
    
    
    
    
	
	
    

    /**
     * 跳转到修改customerWallet
     */
    @RequestMapping("/customerWallet_update/{customerWalletId}")
    public String customerWalletUpdate(@PathVariable Integer customerWalletId, Model model) {
        return PREFIX + "customerWallet_edit.html";
    }

    /**
     * 新增customerWallet
     */
    @RequestMapping(value = "/add")
    @ResponseBody
    public Object add() {
        return super.SUCCESS_TIP;
    }

    /**
     * 删除customerWallet
     */
    @RequestMapping(value = "/delete")
    @ResponseBody
    public Object delete() {
        return SUCCESS_TIP;
    }


    /**
     * 修改customerWallet
     */
    @RequestMapping(value = "/update")
    @ResponseBody
    public Object update() {
        return super.SUCCESS_TIP;
    }

  
}
