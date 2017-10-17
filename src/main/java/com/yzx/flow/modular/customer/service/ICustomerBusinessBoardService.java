package com.yzx.flow.modular.customer.service;

import java.util.List;

import com.yzx.flow.common.persistence.model.BoardResponseParameterInfo;
import com.yzx.flow.common.persistence.model.BusinessBoardInfo;
import com.yzx.flow.common.persistence.model.ReportFormDataInfo;

public interface ICustomerBusinessBoardService {

	String READYCOUNT = "1"; // 待发
	String BEENISSUEDCOUNT = "2"; // 已发
	String SUCCESSCOUNT = "6"; // 成功
	String FAILCOUNT = "4"; // 失败

	/**
		 * 客户最活跃 前5
		 * 
		 * @return
		 */
	List<BusinessBoardInfo> getCustomerTop5(String customerId);

	/**
	 * 根据日月年区分 不同的最活跃的数据显示
	 * 
	 * @param type
	 *            区分 日月年
	 * @param flag
	 *            区分客户或是合作伙伴
	 * @param businessBoardInfo
	 *            待返回数据
	 * @param _id
	 *            客户Id或合作伙伴Id
	 */
	BusinessBoardInfo statisticsData(Integer type, String flag, BusinessBoardInfo businessBoardInfo, Long _id);

	/**
	 * 待发总数
	 * 
	 * @return
	 */
	Integer getReadyCount(Integer type, String customerId);

	/**
	 * 已发总数
	 * 
	 * @return
	 */
	Integer getBeenIssuedCount(Integer type, String customerId);

	/**
	 * 成功总数
	 * 
	 * @return
	 */
	Integer getSuccessCount(Integer type, String customerId);

	/**
	 * 失败总数
	 * 
	 * @return
	 */
	Integer getFailCount(Integer type, String customerId);

	/**
	 * 总下发数
	 * 
	 * @param type
	 * @param partnerId
	 * @return
	 */
	Integer getSumCount(Integer type, String customerId);

	/**
	 * 构造看板下发信息记录
	 * 
	 * @param type
	 * @param partnerId
	 * @return
	 */
	ReportFormDataInfo createReportFormDataInfo(Integer type, String customerId);

	/**
		 * 返回管理员或合作伙伴 对应的 数据
		 * 
		 * @param type
		 *            类型：日月年
		 * @param partnerId
		 *            partnerId != "" ? 合作伙伴 : 管理员
		 * @param status
		 *            状态：成功，失败，待发...
		 * @return
		 */
	BoardResponseParameterInfo returnAdminOrCustomerData(Integer type, String customerId, Integer status);

	Integer[] getYData(String[] str, List<BusinessBoardInfo> businessBoardInfoList, Integer[] y);

	/**
	 * 获取X轴数据
	 * 
	 * @param type
	 * @param str
	 * @return
	 */
	String[] getXData(Integer type);

}