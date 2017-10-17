package com.yzx.flow.modular.customer.controller;

import java.io.File;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yzx.flow.common.controller.BaseController;
import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.page.PageInfoBT;
import com.yzx.flow.common.persistence.model.CustomerAccountSettlement;
import com.yzx.flow.common.util.Constant;
import com.yzx.flow.common.util.FileDownLoadUtils;
import com.yzx.flow.core.util.download.DownloadUtil;
import com.yzx.flow.modular.customer.service.ICustomerAccountSettlementService;
import com.yzx.flow.modular.customer.service.ICustomerInfoService;

@Controller
@RequestMapping("/customerAccountSettlement")
public class CustomerAccountSettlementController extends BaseController {
	  private static final Logger LOG = LoggerFactory.getLogger(CustomerAccountSettlementController.class);
	    @Autowired
	    @Qualifier("customerAccountSettlementService")
	    private ICustomerAccountSettlementService customerAccountSettlementService;
	    
	    @Autowired
	    @Qualifier("customerInfoService")
	    private ICustomerInfoService customerInfoService;

	    private String PREFIX = "/customerAccountSettlement/";
	    
	    /**
	     * 跳转到客户账单列表页面
	     */
	    @RequestMapping("")
	    public String customerAccountSettlement(@RequestParam String customerId,Model model) {
	    	model.addAttribute("customerId", customerId);
	        return PREFIX + "customerAccountSettlement_list.html";
	    }
	    /**
	     * 分页查找
	     * @param page
	     * @return
	     * @throws Exception
	     */
	    @RequestMapping(value = "/query")
	    @ResponseBody
	    public PageInfoBT<CustomerAccountSettlement> pageQuery(Page<CustomerAccountSettlement> page, String customerId) throws Exception {
	    	LOG.info("分页查找结算记录");
	        page.getParams().put("customerId", customerId);
	        if (page.getOrder()==null) {
				page.setOrder("DESC");
			}
	    	if (page.getSort()==null) {
				page.setSort("settlement_time");
			}
	        PageInfoBT<CustomerAccountSettlement> resPage = customerAccountSettlementService.pageQuery(page);
	        return resPage;
	    }
	    
	    /**
	     * 导出客户账单
	     * @param response
	     * @param dateStr
	     * @throws Exception
	     */
	    @RequestMapping(value = "/downloadReport")
		public void downloadReport(HttpServletRequest request, HttpServletResponse response,String customerId,String dateStr) throws Exception {
			String pathname = Constant.getCustomerAccoutReportPath(dateStr, customerId);
			File report = new File(pathname);
			if(!report.exists()){
				throw new Exception(dateStr+"没有流量分发记录");
			}
			FileDownLoadUtils.downloadFile(request,response, report);
		}
}
