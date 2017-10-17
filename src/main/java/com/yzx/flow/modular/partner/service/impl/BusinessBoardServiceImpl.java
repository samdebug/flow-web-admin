package com.yzx.flow.modular.partner.service.impl;

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
import com.yzx.flow.common.persistence.model.PartnerInfo;
import com.yzx.flow.common.persistence.model.ReportFormDataInfo;
import com.yzx.flow.core.aop.dbrouting.DataSource;
import com.yzx.flow.core.aop.dbrouting.DataSourceType;
import com.yzx.flow.modular.partner.service.IBusinessBoardService;
import com.yzx.flow.modular.system.dao.BusinessBoardInfoDao;
import com.yzx.flow.modular.system.dao.CustomerInfoDao;
import com.yzx.flow.modular.system.dao.FlowOrderInfoDao;
import com.yzx.flow.modular.system.dao.PartnerInfoDao;

@Service("businessBoardService")
public class BusinessBoardServiceImpl implements IBusinessBoardService {
	@Autowired
	private BusinessBoardInfoDao businessBoardInfoDao;

	@Autowired
	private CustomerInfoDao customerInfoDao;

	@Autowired
	private PartnerInfoDao partnerInfoDao;

	@Autowired
	private FlowOrderInfoDao flowOrderInfoDao;

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.businessBoard.service.IBusinessBoardService#getCustomerTop5(java.lang.String)
	 */
@Override
	@DataSource(DataSourceType.READ)
	@Transactional(readOnly=true)
	public List<BusinessBoardInfo> getCustomerTop5(String partnerId) {
		BoardParameterInfo boardParameterInfo = new BoardParameterInfo();
		boardParameterInfo.setType(1);
		if (StringUtils.isNotEmpty(partnerId)) {
			boardParameterInfo.setFlag("5");
			boardParameterInfo.setPartnerInfo(partnerId);
		} else {
			boardParameterInfo.setFlag("1");
		}

		List<BusinessBoardInfo> businessBoardInfoList = businessBoardInfoDao
				.getBusinessBoardInfoByInitData(boardParameterInfo);
		if (!businessBoardInfoList.isEmpty()) {
			for (BusinessBoardInfo businessBoardInfo : businessBoardInfoList) {
				CustomerInfo customerInfo = customerInfoDao
						.selectByPrimaryKey(businessBoardInfo.getCustomerId());
				businessBoardInfo.setCustomerName(customerInfo
						.getCustomerName());
				businessBoardInfo.setBalance(customerInfo.getBalance());
				businessBoardInfo.setAvailableCredit(customerInfo.getBalance()
						.add(customerInfo.getCreditAmount())
						.subtract(customerInfo.getCurrentAmount()));
				businessBoardInfo = statisticsData(1, "customer",
						businessBoardInfo, customerInfo.getCustomerId());
			}
			return businessBoardInfoList;
		}
		return new ArrayList<BusinessBoardInfo>();
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.businessBoard.service.IBusinessBoardService#getPartnerTop5()
	 */
@Override
	@DataSource(DataSourceType.READ)
	@Transactional(readOnly=true)
	public List<BusinessBoardInfo> getPartnerTop5() {
		BoardParameterInfo boardParameterInfo = new BoardParameterInfo();
		boardParameterInfo.setType(1);
		boardParameterInfo.setFlag("2");
		List<BusinessBoardInfo> businessBoardInfoList = businessBoardInfoDao
				.getBusinessBoardInfoByInitData(boardParameterInfo);
		if (!businessBoardInfoList.isEmpty()) {
			for (BusinessBoardInfo businessBoardInfo : businessBoardInfoList) {
				PartnerInfo partnerInfo = partnerInfoDao
						.selectByPrimaryKey(businessBoardInfo.getPartnerId());
				businessBoardInfo.setPartnerName(partnerInfo.getPartnerName());
				businessBoardInfo.setBalance(partnerInfo.getBalance());
				businessBoardInfo.setAvailableCredit(partnerInfo.getBalance()
						.add(partnerInfo.getCreditAmount())
						.subtract(partnerInfo.getCurrentAmount()));
				businessBoardInfo = statisticsData(1, "partner",
						businessBoardInfo, partnerInfo.getPartnerId());
			}
			return businessBoardInfoList;
		}
		return new ArrayList<BusinessBoardInfo>();
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.businessBoard.service.IBusinessBoardService#statisticsData(java.lang.Integer, java.lang.String, com.yzx.flow.common.persistence.model.BusinessBoardInfo, java.lang.Long)
	 */
	@Override
	public BusinessBoardInfo statisticsData(Integer type, String flag,
			BusinessBoardInfo businessBoardInfo, Long _id) {
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
	 * @see com.yzx.flow.modular.businessBoard.service.IBusinessBoardService#getReadyCount(java.lang.Integer, java.lang.String)
	 */
	@Override
	public Integer getReadyCount(Integer type, String partnerId) {
		BoardParameterInfo boardParameterInfo = new BoardParameterInfo();
		boardParameterInfo.setType(type);
		boardParameterInfo.setStatus(READYCOUNT);
		if (StringUtils.isNotEmpty(partnerId)) {
			boardParameterInfo.setPartnerInfo(partnerId);
		}
		return flowOrderInfoDao.getCountByStatus(boardParameterInfo);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.businessBoard.service.IBusinessBoardService#getBeenIssuedCount(java.lang.Integer, java.lang.String)
	 */
	@Override
	public Integer getBeenIssuedCount(Integer type, String partnerId) {
		BoardParameterInfo boardParameterInfo = new BoardParameterInfo();
		boardParameterInfo.setType(type);
		boardParameterInfo.setStatus(BEENISSUEDCOUNT);
		if (StringUtils.isNotEmpty(partnerId)) {
			boardParameterInfo.setPartnerInfo(partnerId);
		}
		return flowOrderInfoDao.getCountByStatus(boardParameterInfo);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.businessBoard.service.IBusinessBoardService#getSuccessCount(java.lang.Integer, java.lang.String)
	 */
	@Override
	public Integer getSuccessCount(Integer type, String partnerId) {
		BoardParameterInfo boardParameterInfo = new BoardParameterInfo();
		boardParameterInfo.setType(type);
		boardParameterInfo.setStatus(SUCCESSCOUNT);
		if (StringUtils.isNotEmpty(partnerId)) {
			boardParameterInfo.setPartnerInfo(partnerId);
		}
		return flowOrderInfoDao.getCountByStatus(boardParameterInfo);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.businessBoard.service.IBusinessBoardService#getFailCount(java.lang.Integer, java.lang.String)
	 */
	@Override
	public Integer getFailCount(Integer type, String partnerId) {
		BoardParameterInfo boardParameterInfo = new BoardParameterInfo();
		boardParameterInfo.setType(type);
		boardParameterInfo.setFlag("1");
		if (StringUtils.isNotEmpty(partnerId)) {
			boardParameterInfo.setPartnerInfo(partnerId);
		}
		return flowOrderInfoDao.getCountByStatus(boardParameterInfo);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.businessBoard.service.IBusinessBoardService#getSumCount(java.lang.Integer, java.lang.String)
	 */
	@Override
	public Integer getSumCount(Integer type, String partnerId) {
		BoardParameterInfo boardParameterInfo = new BoardParameterInfo();
		boardParameterInfo.setType(type);
		if (StringUtils.isNotEmpty(partnerId)) {
			boardParameterInfo.setPartnerInfo(partnerId);
		}
		return flowOrderInfoDao.getCountByStatus(boardParameterInfo);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.businessBoard.service.IBusinessBoardService#createReportFormDataInfo(java.lang.Integer, java.lang.String)
	 */
	@Override
	public ReportFormDataInfo createReportFormDataInfo(Integer type,
			String partnerId) {
		ReportFormDataInfo reportFormDataInfo = new ReportFormDataInfo();
		reportFormDataInfo.setReadyCount(getReadyCount(type, partnerId));
		reportFormDataInfo.setBeenIssuedCount(getBeenIssuedCount(type,
				partnerId));
		reportFormDataInfo.setSuccessCount(getSuccessCount(type, partnerId));
		reportFormDataInfo.setFailCount(getFailCount(type, partnerId));
		reportFormDataInfo.setSumCount(getSumCount(type, partnerId));
		return reportFormDataInfo;
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.businessBoard.service.IBusinessBoardService#returnAdminOrPartnerData(java.lang.Integer, java.lang.String, java.lang.Integer)
	 */
@Override
	@DataSource(DataSourceType.READ)
	@Transactional(readOnly=true)
	public BoardResponseParameterInfo returnAdminOrPartnerData(Integer type,
			String partnerId, Integer status) {
		BoardResponseParameterInfo boardResponseParameterInfo = new BoardResponseParameterInfo();
		// 具体下发数据
		ReportFormDataInfo reportFormDataInfo = createReportFormDataInfo(type,
				partnerId);
		// X轴
		String[] str = getXData(type);
		// Y轴
		BoardParameterInfo boardParameterInfo = new BoardParameterInfo();
		boardParameterInfo.setType(type);
		if (status != 5) {
			// 查询对应状态的数据
			if (status == 4) {
				boardParameterInfo.setFlag("1");
			} else {
				boardParameterInfo.setStatus(status.toString());
			}
		}

		Integer[] y = new Integer[str.length];
		if (StringUtils.isNotEmpty(partnerId)) {
			boardParameterInfo.setPartnerInfo(partnerId);
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
	 * @see com.yzx.flow.modular.businessBoard.service.IBusinessBoardService#getYData(java.lang.String[], java.util.List, java.lang.Integer[])
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
	 * @see com.yzx.flow.modular.businessBoard.service.IBusinessBoardService#getXData(java.lang.Integer)
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

