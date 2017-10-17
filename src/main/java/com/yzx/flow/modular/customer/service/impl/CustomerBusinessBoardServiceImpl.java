package com.yzx.flow.modular.customer.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yzx.flow.common.persistence.model.BoardParameterInfo;
import com.yzx.flow.common.persistence.model.BoardResponseParameterInfo;
import com.yzx.flow.common.persistence.model.BusinessBoardInfo;
import com.yzx.flow.common.persistence.model.CustomerInfo;
import com.yzx.flow.common.persistence.model.ReportFormDataInfo;
import com.yzx.flow.core.aop.dbrouting.DataSource;
import com.yzx.flow.core.aop.dbrouting.DataSourceType;
import com.yzx.flow.modular.customer.service.ICustomerBusinessBoardService;
import com.yzx.flow.modular.system.dao.BusinessBoardInfoDao;
import com.yzx.flow.modular.system.dao.CustomerInfoDao;
import com.yzx.flow.modular.system.dao.FlowOrderInfoDao;
import com.yzx.flow.modular.system.dao.PartnerInfoDao;

@Service("customerBusinessBoardService")
public class CustomerBusinessBoardServiceImpl implements ICustomerBusinessBoardService {
	@Autowired
	private BusinessBoardInfoDao businessBoardInfoDao;

	@Autowired
	private CustomerInfoDao customerInfoDao;

	@Autowired
	private PartnerInfoDao partnerInfoDao;

	@Autowired
	private FlowOrderInfoDao flowOrderInfoDao;

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.customer.service.ICustomerBusinessBoardService#getCustomerTop5(java.lang.String)
	 */
@Override
	@DataSource(DataSourceType.READ)
	@Transactional(readOnly=true)
	public List<BusinessBoardInfo> getCustomerTop5(String customerId) {
		BoardParameterInfo boardParameterInfo = new BoardParameterInfo();
		boardParameterInfo.setType(1);
		if (StringUtils.isNotEmpty(customerId)) {
			boardParameterInfo.setFlag("3");
			boardParameterInfo.setCustomerInfo(customerId);
		} else {
			boardParameterInfo.setFlag("1");
		}

		List<BusinessBoardInfo> businessBoardInfoList = businessBoardInfoDao.getBusinessBoardInfoByInitData(boardParameterInfo);
		if (!businessBoardInfoList.isEmpty()) {
			for (BusinessBoardInfo businessBoardInfo : businessBoardInfoList) {
				CustomerInfo customerInfo = customerInfoDao.selectByPrimaryKey(businessBoardInfo.getCustomerId());
				businessBoardInfo.setCustomerName(customerInfo.getCustomerName());
				businessBoardInfo.setBalance(customerInfo.getBalance());
				businessBoardInfo.setAvailableCredit(customerInfo.getBalance()
						.add(customerInfo.getCreditAmount())
						.subtract(customerInfo.getCurrentAmount()));
				businessBoardInfo = statisticsData(1, "customer",businessBoardInfo, customerInfo.getCustomerId());
			}
			return businessBoardInfoList;
		}
		return new ArrayList<BusinessBoardInfo>();
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.customer.service.ICustomerBusinessBoardService#statisticsData(java.lang.Integer, java.lang.String, com.yzx.flow.common.persistence.model.BusinessBoardInfo, java.lang.Long)
	 */
	@Override
	public BusinessBoardInfo statisticsData(Integer type, String flag,BusinessBoardInfo businessBoardInfo, Long _id) {
		BoardParameterInfo boardParameterInfo = new BoardParameterInfo();
		if ("customer".equals(flag)) {
			boardParameterInfo.setFlag("3");
			boardParameterInfo.setCustomerInfo(_id.toString());
			if (type == 1) {
				boardParameterInfo.setType(Integer.valueOf(2));
				businessBoardInfo.setCountDay(businessBoardInfo.getCount());
				BusinessBoardInfo bbInfo = businessBoardInfoDao
						.getBusinessBoardInfoByInitData(boardParameterInfo)
						.get(0);
				businessBoardInfo.setCountMonth(bbInfo.getCount());
			}
		} else if ("partner".equals(flag)) {
			boardParameterInfo.setFlag("4");
			boardParameterInfo.setPartnerInfo(_id.toString());
			if (type == 1) {
				boardParameterInfo.setType(Integer.valueOf(2));
				businessBoardInfo.setCountDay(businessBoardInfo.getCount());
				BusinessBoardInfo bbInfo = businessBoardInfoDao
						.getBusinessBoardInfoByInitData(boardParameterInfo)
						.get(0);
				businessBoardInfo.setCountMonth(bbInfo.getCount());
			}
		}
		return businessBoardInfo;
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.customer.service.ICustomerBusinessBoardService#getReadyCount(java.lang.Integer, java.lang.String)
	 */
	@Override
	public Integer getReadyCount(Integer type, String customerId) {
		BoardParameterInfo boardParameterInfo = new BoardParameterInfo();
		boardParameterInfo.setType(type);
		boardParameterInfo.setStatus(READYCOUNT);
		if (StringUtils.isNotEmpty(customerId)) {//客户纬度
			boardParameterInfo.setCustomerInfo(customerId);
		}
		return flowOrderInfoDao.getCustomerCountByStatus(boardParameterInfo);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.customer.service.ICustomerBusinessBoardService#getBeenIssuedCount(java.lang.Integer, java.lang.String)
	 */
	@Override
	public Integer getBeenIssuedCount(Integer type, String customerId) {
		BoardParameterInfo boardParameterInfo = new BoardParameterInfo();
		boardParameterInfo.setType(type);
		boardParameterInfo.setStatus(BEENISSUEDCOUNT);
		if (StringUtils.isNotEmpty(customerId)) {
			boardParameterInfo.setCustomerInfo(customerId);
		}
		return flowOrderInfoDao.getCustomerCountByStatus(boardParameterInfo);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.customer.service.ICustomerBusinessBoardService#getSuccessCount(java.lang.Integer, java.lang.String)
	 */
	@Override
	public Integer getSuccessCount(Integer type, String customerId) {
		BoardParameterInfo boardParameterInfo = new BoardParameterInfo();
		boardParameterInfo.setType(type);
		boardParameterInfo.setStatus(SUCCESSCOUNT);
		if (StringUtils.isNotEmpty(customerId)) {
			boardParameterInfo.setCustomerInfo(customerId);
		}
		return flowOrderInfoDao.getCustomerCountByStatus(boardParameterInfo);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.customer.service.ICustomerBusinessBoardService#getFailCount(java.lang.Integer, java.lang.String)
	 */
	@Override
	public Integer getFailCount(Integer type, String customerId) {
		BoardParameterInfo boardParameterInfo = new BoardParameterInfo();
		boardParameterInfo.setType(type);
		boardParameterInfo.setStatus(FAILCOUNT);
		if (StringUtils.isNotEmpty(customerId)) {
			boardParameterInfo.setCustomerInfo(customerId);
		}
		return flowOrderInfoDao.getCustomerCountByStatus(boardParameterInfo);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.customer.service.ICustomerBusinessBoardService#getSumCount(java.lang.Integer, java.lang.String)
	 */
	@Override
	public Integer getSumCount(Integer type, String customerId) {
		BoardParameterInfo boardParameterInfo = new BoardParameterInfo();
		boardParameterInfo.setType(type);
		if (StringUtils.isNotEmpty(customerId)) {
			boardParameterInfo.setCustomerInfo(customerId);
		}
		return flowOrderInfoDao.getCustomerCountByStatus(boardParameterInfo);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.customer.service.ICustomerBusinessBoardService#createReportFormDataInfo(java.lang.Integer, java.lang.String)
	 */
	@Override
	public ReportFormDataInfo createReportFormDataInfo(Integer type,String customerId) {
		ReportFormDataInfo reportFormDataInfo = new ReportFormDataInfo();
		reportFormDataInfo.setReadyCount(getReadyCount(type, customerId));
		reportFormDataInfo.setBeenIssuedCount(getBeenIssuedCount(type,customerId));
		reportFormDataInfo.setSuccessCount(getSuccessCount(type, customerId));
		reportFormDataInfo.setFailCount(getFailCount(type, customerId));
		reportFormDataInfo.setSumCount(getSumCount(type, customerId));
		return reportFormDataInfo;
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.customer.service.ICustomerBusinessBoardService#returnAdminOrCustomerData(java.lang.Integer, java.lang.String, java.lang.Integer)
	 */
@Override
	@DataSource(DataSourceType.READ)
	@Transactional(readOnly=true)
	public BoardResponseParameterInfo returnAdminOrCustomerData(Integer type,String customerId, Integer status) {
		BoardResponseParameterInfo boardResponseParameterInfo = new BoardResponseParameterInfo();
		// 具体下发数据
		ReportFormDataInfo reportFormDataInfo = createReportFormDataInfo(type,customerId);
		// X轴
		String[] str = getXData(type);
		// Y轴
		BoardParameterInfo boardParameterInfo = new BoardParameterInfo();
		boardParameterInfo.setType(type);
		if (5 != status.intValue()) {//5总计不需要带状态
			boardParameterInfo.setStatus(status.toString());
		}

		Integer[] y = new Integer[str.length];
		if (StringUtils.isNotEmpty(customerId)) {
			boardParameterInfo.setCustomerInfo(customerId);
		}
		List<BusinessBoardInfo> businessBoardInfoList = businessBoardInfoDao.getBoardInfo(boardParameterInfo);
		if (businessBoardInfoList.isEmpty()) {
			for (int i = 0; i < y.length; i++) {
				y[i] = 0;
			}
		} else {
			y = getYData(str,businessBoardInfoList,y);
		}
		boardResponseParameterInfo.setReportFormDataInfo(reportFormDataInfo);
		boardResponseParameterInfo.setxParame(str);
		boardResponseParameterInfo.setyParame(y);
		return boardResponseParameterInfo;
	}
	
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.customer.service.ICustomerBusinessBoardService#getYData(java.lang.String[], java.util.List, java.lang.Integer[])
	 */
	@Override
	public Integer[] getYData(String[] str,List<BusinessBoardInfo> businessBoardInfoList,Integer[] y){
		for (int i = 0; i < str.length; i++) {
			for (int j = 0; j < businessBoardInfoList.size(); j++) {
				if (businessBoardInfoList.get(j).getApplyDate().equals(str[i])) {
					y[i] = businessBoardInfoList.get(j).getCount();
					break;
				} else {
					y[i] = 0;
				}
			}
		}
		return y;
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.customer.service.ICustomerBusinessBoardService#getXData(java.lang.Integer)
	 */
	@Override
	public String[] getXData(Integer type) {
		String[] str = null;
		Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int date = c.get(Calendar.DATE);
		int hour = c.get(Calendar.HOUR_OF_DAY);
		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, month);

		if (type == 1) {
			str = new String[hour + 1];
			for (int i = 0; i < str.length; i++) {
				str[i] = i + "";
			}
		} else if (type == 2) {
			str = new String[date];
			for (int i = 0; i < date; i++) {
				if (i < 9) {
					str[i] = "0" + (i + 1);
				} else {
					str[i] = (i + 1)+"";
				}
			}
		} else if (type == 3) {
			str = new String[month + 1];
			for (int i = 0; i < str.length; i++) {
				if (i < 9) {
					str[i] = "0" + (i + 1);
				} else {
					str[i] = (i + 1) + "";
				}
			}
		} 
		return str;
	}
}

