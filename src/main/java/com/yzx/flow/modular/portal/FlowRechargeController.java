package com.yzx.flow.modular.portal;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yzx.flow.common.annotion.PortalCustomer;
import com.yzx.flow.common.constant.tips.ErrorTip;
import com.yzx.flow.common.controller.BaseController;
import com.yzx.flow.common.excel.TemplateExcel;
import com.yzx.flow.common.excel.TemplateExcelManager;
import com.yzx.flow.common.exception.BizExceptionEnum;
import com.yzx.flow.common.exception.BussinessException;
import com.yzx.flow.common.persistence.model.ExportResultsFlowBatchRecord;
import com.yzx.flow.common.persistence.model.FlowBatchRecord;
import com.yzx.flow.common.persistence.model.FlowPackageInfo;
import com.yzx.flow.common.persistence.model.MobileHomeInfo;
import com.yzx.flow.common.persistence.model.RechargePackage;
import com.yzx.flow.common.util.Constant;
import com.yzx.flow.core.shiro.ShiroKit;
import com.yzx.flow.core.util.MobileHomeInfoUtil;
import com.yzx.flow.core.util.ToolUtil;
import com.yzx.flow.modular.portal.service.IFlowBatchRecordService;
import com.yzx.flow.modular.portal.service.IFlowRechargeService;

/**
 * 流量充值
 * @author Liulei
 *
 */
@Controller
@RequestMapping("/portalFlowRecharge")
public class FlowRechargeController extends BaseController {
	
	private static final Logger logger = LoggerFactory.getLogger(FlowRechargeController.class);
	
	@Autowired
	private IFlowRechargeService flowRechargeService;
	
	@Autowired
	private IFlowBatchRecordService flowBatchRecordService;
	
	
	
	
	
	private final static Byte[] getFlowPackageInfo = new Byte[0];

	/**
	 * 获取 流量包信息
	 * @param userMobile
	 * @param code
	 * @param isCombo
	 * @return
	 */
	@PortalCustomer
	@RequestMapping(value = "/getFlowPackageInfo", method = RequestMethod.POST)
	@ResponseBody
	public Object getFlowPackageInfo(String userMobile, String code, String isCombo) {
		synchronized (getFlowPackageInfo) {
			try {
				if (StringUtils.isBlank(isCombo)) {
					isCombo = FlowPackageInfo.BASIC_PACKAGE;
				}
				Map<String, Object> res = new HashMap<String, Object>();
				
				MobileHomeInfo m = MobileHomeInfoUtil.getMobileHomeInfo(userMobile);
				if ( m != null ) {
					res.put("areaCode", m.getAreaCode());
					res.put("areaName", m.getProvince());
				}
				res.put("data", flowRechargeService.getFlowPackageInfo(ShiroKit.getUser().getTargetId(), userMobile, code, isCombo));
				return res;
			} catch (BussinessException e) {
				return ErrorTip.buildErrorTip(e.getMessage());
			} catch (Exception e) {
				logger.error("请求获取流量包信息错误：" + e.getMessage());
				throw e;
			}
		}
	}
	
	
	
	/**
	 * 获取运营商对应流量包
	 * 
	 * @param request
	 * @return
	 */
	@PortalCustomer
	@RequestMapping(value = "/getBatchFlowPackageInfo", method = RequestMethod.POST)
	@ResponseBody
	public Object getBatchFlowPackageInfo(@RequestParam(value = "operator") String operator, Integer packageType) {
		synchronized (getFlowPackageInfo) {
			try {
				return flowRechargeService.getBatchFlowPackageInfo(operator, packageType);
			} catch (BussinessException e) {
				return ErrorTip.buildErrorTip(e.getMessage());
			} catch (Exception e) {
				logger.error("请求获取流量包信息错误：" + e.getMessage(), e);
				throw e;
			}
		}
	}
	
	
	
	/**
	 * 流量充值
	 * 
	 * @param userMobile
	 * @param packageId
	 * @param request
	 * @return
	 */
	private final static Byte[] flowRecharge = new Byte[0];

	@PortalCustomer
	@RequestMapping(value = "/flowRecharge", method = RequestMethod.POST)
	@ResponseBody
	public Object flowRecharge(String userMobile, String packageId, String isCombo) {
		synchronized (flowRecharge) {
			try {
				if (StringUtils.isBlank(isCombo)) {
					isCombo = FlowPackageInfo.BASIC_PACKAGE;
				}
				flowRechargeService.flowRecharge(ShiroKit.getUser().getTargetId(), userMobile, packageId, isCombo);
				return SUCCESS_TIP;
			} catch (BussinessException e) {
				
				return ErrorTip.buildErrorTip(e.getMessage());
			} catch (Exception e) {
				throw e;
			}
		}
	}
	
	/**
	 * 批量充值
	 */
	@PortalCustomer
	@RequestMapping(value = "/flowBatchRecharge", method = RequestMethod.POST)
	@ResponseBody
	public Object flowBatchRecharge(RechargePackage rechargePackage) {
		
		String ydPackageId = rechargePackage.getYdPackageId();
		String ltPackageId = rechargePackage.getLtPackageId();
		String dxPackageId = rechargePackage.getDxPackageId();
		
		int rechargeType = rechargePackage.getRechargeType();
		if (Constant.RECHARGE_TYPE_BASE_PACKAGE != rechargeType) 
			return ErrorTip.buildErrorTip(BizExceptionEnum.CUSTOMER_FORMAT_ERROR, "目前只支持基础流量包充值");
		
		if (StringUtils.isBlank(ydPackageId) && StringUtils.isBlank(ltPackageId) && StringUtils.isBlank(dxPackageId))
			return ErrorTip.buildErrorTip(BizExceptionEnum.CUSTOMER_FORMAT_ERROR, "请选择充值流量包");
		
		if (StringUtils.isBlank(rechargePackage.getMobiles()) || StringUtils.isBlank(rechargePackage.getNonceStr())
				|| StringUtils.isBlank(rechargePackage.getSign())) {
			return ErrorTip.buildErrorTip(BizExceptionEnum.REQUEST_NULL);
		}
		// 检查数据有效性 - 因为手机号码的解析 和 现在提交  是在两个不同的步骤。个人觉得应该校验一下之前解析的数据完整性
		if ( !ToolUtil.verifySignStr(rechargePackage.getMobiles(), rechargePackage.getNonceStr(),
				FileUploadRecordController.FLOR_RECHARGE_MOBILES_KEY, rechargePackage.getSign()) ) {
			return ErrorTip.buildErrorTip(BizExceptionEnum.REQUEST_NULL);
		}
		
		List<String> mobiles = Arrays.asList(rechargePackage.getMobiles().split(","));
		try {
			// 批量充流量主业务方法
			flowBatchRecordService.flowBatchIssued(rechargePackage, mobiles);
			return SUCCESS_TIP;
		} catch ( BussinessException e ) {
			return ErrorTip.buildErrorTip(e.getMessage());
		} catch ( Exception e ) {
			logger.error("批量充值异常", e);
		}
		return ErrorTip.buildErrorTip(BizExceptionEnum.SERVER_ERROR);
	}
	
	
	/**
	 * 检查当前用户是否有可用流量包
	 * @return
	 */
	@PortalCustomer
	@RequestMapping(value = "/checkFlowOrderInfoByCustomerId", method = RequestMethod.POST)
	@ResponseBody
	public Object getFlowOrderInfoByCustomerId() {
		try {
			flowRechargeService.checkFlowOrderInfoByCustomerId(ShiroKit.getUser().getTargetId());
			return SUCCESS_TIP;
		} catch (BussinessException e) {
			return ErrorTip.buildErrorTip(e.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
	}
	
	
	
	/**
	 * 充值结果导出
	 */
	@PortalCustomer
	@RequestMapping(value = "/exportTopUpResults")
	@ResponseBody
	public void exportTopUpResults(Long fileId) throws IOException {
		ExportResultsFlowBatchRecord erfbr = createExportResultsFlowBatchRecord(fileId);
		// 生成Excel
		Map<String, Object> beanParams = new HashMap<String, Object>();
		beanParams.put("erfbr", erfbr);
		try {
			TemplateExcelManager.getInstance().createExcelFileAndDownload(TemplateExcel.RECHARGE_BATCH_DETAIL, beanParams);
		} catch (Exception e) {
			logger.error("导出报表出错！！！", e);
			throw new BussinessException(BizExceptionEnum.CUSTOMER_FORMAT_ERROR, "导出报表出错！！！【" + e.getMessage() + "】");
		}
	}
	
	/**
	 * 构造导出用FlowBatchRecord对象
	 * 
	 * @param fileId
	 * @return
	 */
	public ExportResultsFlowBatchRecord createExportResultsFlowBatchRecord(Long fileId) {
		ExportResultsFlowBatchRecord erfbr = new ExportResultsFlowBatchRecord();
		// 取出未下发的手机号码
		List<FlowBatchRecord> notIssuedFlowBatchRecords = flowBatchRecordService.selectByFileId(fileId,
				Constant.FLOW_ISSUED_STATUS_NOT);
		// 取出下发成功手机号码
		List<FlowBatchRecord> successIssuedFlowBatchRecords = flowBatchRecordService.selectRecByFileId(fileId,
				Constant.FLOW_ISSUED_STATUS_SUCCESS);
		// 取出下发中的手机号码
		List<FlowBatchRecord> issuedingFlowBatchRecords = flowBatchRecordService.selectByFileId(fileId,
				Constant.FLOW_ISSUED_STATUS_ING);
		// 取出下发失败的手机号码
		List<FlowBatchRecord> issuedFailedFlowBatchRecords = flowBatchRecordService.selectRecByFileId(fileId,
				Constant.FLOW_ISSUED_STATUS_FAILED);
		erfbr.setIssuedFailedFlowBatchRecords(issuedFailedFlowBatchRecords);
		erfbr.setIssuedingFlowBatchRecords(issuedingFlowBatchRecords);
		erfbr.setNotIssuedFlowBatchRecords(notIssuedFlowBatchRecords);
		erfbr.setSuccessIssuedFlowBatchRecords(successIssuedFlowBatchRecords);
		return erfbr;
	}

}
