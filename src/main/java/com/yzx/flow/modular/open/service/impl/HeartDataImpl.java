package com.yzx.flow.modular.open.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaoleilu.hutool.date.DateUtil;
import com.yzx.flow.common.persistence.model.CompInfo;
import com.yzx.flow.common.persistence.model.CustInfo;
import com.yzx.flow.modular.open.service.IHeartData;
import com.yzx.flow.modular.open.utils.ResultData;
import com.yzx.flow.modular.system.dao.HeartDataDao;

@Service
public class HeartDataImpl implements IHeartData {
	
	@Autowired
	private HeartDataDao heartDataDao;

	@Override
	public ResultData getHeartData(int gapTime) {
		ResultData resultData = new ResultData();
		String end = DateUtil.now();
		String start = DateUtil.offsetMillisecond(new Date(), -gapTime).toString("yyyy-MM-dd HH-mm-ss");
		List<CompInfo> compInfos = heartDataDao.getCompInfo(start, end);
		List<CustInfo> custInfos = heartDataDao.getCustInfo(start, end);
		resultData.setCompInfos(compInfos);
		resultData.setCustInfos(custInfos);
		resultData.setEnd(end);
		resultData.setStart(start);
		return resultData;
	}

}
