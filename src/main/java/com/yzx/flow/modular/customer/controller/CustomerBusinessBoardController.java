package com.yzx.flow.modular.customer.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yzx.flow.common.constant.tips.ErrorTip;
import com.yzx.flow.common.controller.BaseController;
import com.yzx.flow.common.exception.BizExceptionEnum;
import com.yzx.flow.common.persistence.model.BoardResponseParameterInfo;
import com.yzx.flow.common.persistence.model.BusinessBoardInfo;
import com.yzx.flow.common.persistence.model.InitIssuedData;
import com.yzx.flow.modular.customer.service.ICustomerBusinessBoardService;
import com.yzx.flow.modular.customer.service.ICustomerInfoService;

@Controller
@RequestMapping("/customerBusinessBoard")
public class CustomerBusinessBoardController extends BaseController {

	private static final Logger LOG = LoggerFactory.getLogger(CustomerBusinessBoardController.class);

	@Autowired
	private ICustomerInfoService customerInfoService;

	@Autowired
	private ICustomerBusinessBoardService customerBusinessBoardService;

	/**
	 * 报表内容展示
	 */
	@RequestMapping(value = "/initReportForm")
	@ResponseBody
	public Object initReportForm(Integer type,Integer status) {
		BoardResponseParameterInfo boardResponseParameterInfo  = new BoardResponseParameterInfo(); 
		boardResponseParameterInfo  = customerBusinessBoardService.returnAdminOrCustomerData(type, null, status);
		return boardResponseParameterInfo;
	}

	/**
	 * 客户top5数据
	 * type : 区分日，月，年   1,2,3
	 */
	@RequestMapping(value = "/initData")
	@ResponseBody
	public Object initData() {
//		ShiroUser current = ShiroKit.getUser();
		try {
			List<BusinessBoardInfo> businessBoardInfoCustomer = new ArrayList<BusinessBoardInfo>();
			InitIssuedData initData = new InitIssuedData();
			businessBoardInfoCustomer = customerBusinessBoardService.getCustomerTop5(null);
			initData = new InitIssuedData(businessBoardInfoCustomer, null);
			return initData;
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return new ErrorTip(BizExceptionEnum.SERVER_ERROR);
		}
	}

}
