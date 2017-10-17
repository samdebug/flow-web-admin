package com.yzx.flow.modular.partner.controller;

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
import com.yzx.flow.core.shiro.ShiroKit;
import com.yzx.flow.core.shiro.ShiroUser;
import com.yzx.flow.modular.partner.service.IBusinessBoardService;
import com.yzx.flow.modular.partner.service.IPartnerService;

/**
 *
 * @author liuyufeng
 * @Date 2017-08-16 16:23:21
 */
@Controller
@RequestMapping("/businessBoard")
public class BusinessBoardController extends BaseController {
	
	private static final Logger LOG = LoggerFactory.getLogger(BusinessBoardController.class);

	@Autowired
	private IPartnerService partnerService;

	@Autowired
	private IBusinessBoardService businessBoardService;

	/**
	 * 报表内容展示
	 */
	@RequestMapping(value = "/initReportForm")
	@ResponseBody
	public Object initReportForm(Integer type,Integer status) {
		BoardResponseParameterInfo boardResponseParameterInfo  = new BoardResponseParameterInfo(); 
		// TODO 
		// 判断是否为合作伙伴登录
//		if (!isAdmin()) {
//			// 合作伙伴ID
//			Staff staff = getCurrentLogin();
//			PartnerInfo partnerInfo = partnerService.getByAccount(staff.getLoginName());
//			if (partnerInfo == null) {
//				return super.fail("合作伙伴不存在");
//			}
//			boardResponseParameterInfo  = businessBoardService.returnAdminOrPartnerData(type,partnerInfo.getPartnerId()+"",status);
//		} else {
			// 管理员
			boardResponseParameterInfo  = businessBoardService.returnAdminOrPartnerData(type, null, status);
//		}
		return boardResponseParameterInfo;
	}

	/**
	 * 合作伙伴、客户top5数据
	 * type : 区分日，月，年   1,2,3
	 */
	@RequestMapping(value = "/initData")
	@ResponseBody
	public Object initData() {
		
		ShiroUser current = ShiroKit.getUser();
		
//		Staff staff = getCurrentLogin();
//		if (staff == null) {
//			return fail("非法请求");
//		}
		try {
			List<BusinessBoardInfo> businessBoardInfoCustomer = new ArrayList<BusinessBoardInfo>();
			List<BusinessBoardInfo> businessBoardInfoPartner = businessBoardService.getPartnerTop5();
			InitIssuedData initData = new InitIssuedData();
			
			// TODO 
			/**
			 * 区分是否是管理员或是合作伙伴
			 */
//			if (!isAdmin()) {
//				PartnerInfo partnerInfo = partnerService.getByAccount(staff.getLoginName());
//				if (partnerInfo == null) {
//					return super.fail("合作伙伴不存在");
//				}
//				businessBoardInfoCustomer = businessBoardService.getCustomerTop5(partnerInfo.getPartnerId()+"");
//				initData = new InitIssuedData(businessBoardInfoCustomer, null);
//			}else{
				businessBoardInfoCustomer = businessBoardService.getCustomerTop5(null);
				initData = new InitIssuedData(businessBoardInfoCustomer, businessBoardInfoPartner);
//			}
			return initData;
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return new ErrorTip(BizExceptionEnum.SERVER_ERROR);
		}
	}

//	protected Staff getCurrentLogin() {
//		Staff staff = null;
//		try {
//			staff = StaffUtil.getLoginStaff();
//		} catch (Exception e) {
//			LOG.error(e.getMessage(), e);
//			return null;
//		}
//		return staff;
//	}

//	protected boolean isAdmin() {
//		try {
//			return StaffUtil.isAdmin();
//		} catch (Exception e) {
//			LOG.error(e.getMessage(), e);
//			return false;
//		}
//	}
	

}
