package com.yzx.flow.modular.statistics.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.persistence.model.ChannelAccountAnalyse;
import com.yzx.flow.core.aop.dbrouting.DataSource;
import com.yzx.flow.core.aop.dbrouting.DataSourceType;
import com.yzx.flow.modular.statistics.service.IChannelAccountAnalyseService;
import com.yzx.flow.modular.system.dao.ChannelAccountAnalyseDao;


/**
 * 
 * <b>Title：</b>ChannelAnalyseService.java<br/>
 * <b>Description：</b> <br/>
 * <b>@author： </b>v5480<br/>
 * <b>@date：</b>2016-03-04 11:17:21<br/>
 * <b>Copyright (c) 2016 szwisdom Tech.</b>
 * 
 */
@Service("channelAccountAnalyseService")
public class ChannelAccountAnalyseServiceImpl implements IChannelAccountAnalyseService {
	@Autowired
	private ChannelAccountAnalyseDao channelAccountAnalyseDao;

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.statistics.service.impl.IChannelAccountAnalyseService#pageQuery(com.yzx.flow.common.page.Page)
	 */
	@DataSource(DataSourceType.READ)
	@Override
	@Transactional(readOnly=true)
	public List pageQuery(Page<ChannelAccountAnalyse> page) throws Exception {
//		StaffUtil.isAdmin();
		List<Object> result = new ArrayList<>();
		page.setAutoCountTotal(false);
		List<ChannelAccountAnalyse> list = channelAccountAnalyseDao.pageQuery(page);
		page.setTotal(channelAccountAnalyseDao.countForPageNew(page.getParams()));
		page.setDatas(list);
		result.add(page); //第一个结果
		Map<String, Object> sumMap = new HashMap<>();
		sumMap = summaryQuery(page.getParams());
		result.add(sumMap); //第二个结果
		return result;
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.statistics.service.impl.IChannelAccountAnalyseService#query(java.util.Map)
	 */
	@DataSource(DataSourceType.READ)
	@Override
	@Transactional(readOnly=true)
	public List query(Map<String, Object> params) throws Exception {
//		StaffUtil.isAdmin();
		List<Object> result = new ArrayList<>();
		List<ChannelAccountAnalyse> list =  channelAccountAnalyseDao.query(params);
		result.add(list);
		Map<String, Object> sumMap = new HashMap<>();
		sumMap = summaryQuery(params);
		result.add(sumMap); //第二个结果
		return result;
	}

	/**
	 * 获取合计的结果
	 * @param page
	 * @return
	 */
	private Map<String, Object> summaryQuery(Map<String, Object> params) {
		Map<String,Object> sumMap = channelAccountAnalyseDao.summaryQuery(params);
		if(sumMap==null){
			sumMap = new HashMap<>();
			sumMap.put("sentCountSum", BigDecimal.ZERO);
			sumMap.put("sentOriginPriceSum", BigDecimal.ZERO);
			sumMap.put("sentCustomerPriceSum", BigDecimal.ZERO);
			sumMap.put("sentChannelPriceSum", BigDecimal.ZERO);
			sumMap.put("failCountSum", BigDecimal.ZERO);
			sumMap.put("failOriginPriceSum", BigDecimal.ZERO);
			sumMap.put("failCustomerPriceSum", BigDecimal.ZERO);
			sumMap.put("failChannelPriceSum", BigDecimal.ZERO);
			sumMap.put("succCountSum", BigDecimal.ZERO);
			sumMap.put("succOriginPriceSum", BigDecimal.ZERO);
			sumMap.put("succCustomerPriceSum", BigDecimal.ZERO);
			sumMap.put("succChannelPriceSum", BigDecimal.ZERO);
			sumMap.put("profitSum", BigDecimal.ZERO);
		}else{ 
			BigDecimal sentCountSum = (BigDecimal) sumMap.get("sentCountSum");
			BigDecimal sentOriginPriceSum = (BigDecimal) sumMap.get("sentOriginPriceSum");
			BigDecimal sentCustomerPriceSum = (BigDecimal) sumMap.get("sentCustomerPriceSum");
			BigDecimal sentChannelPriceSum = (BigDecimal) sumMap.get("sentChannelPriceSum");
			BigDecimal  failCountSum = (BigDecimal) sumMap.get("failCountSum");
			BigDecimal failOriginPriceSum = (BigDecimal) sumMap.get("failOriginPriceSum");
			BigDecimal failCustomerPriceSum = (BigDecimal) sumMap.get("failCustomerPriceSum");
			BigDecimal failChannelPriceSum = (BigDecimal) sumMap.get("failChannelPriceSum");
			BigDecimal  succCountSum = (BigDecimal) sumMap.get("succCountSum");
			BigDecimal succOriginPriceSum = (BigDecimal) sumMap.get("succOriginPriceSum");
			BigDecimal succCustomerPriceSum = (BigDecimal) sumMap.get("succCustomerPriceSum");
			BigDecimal succChannelPriceSum = (BigDecimal) sumMap.get("succChannelPriceSum");
			BigDecimal profitSum = (BigDecimal) sumMap.get("profitSum");
			

			sumMap.put("sentCountSum", sentCountSum.intValue());
			sumMap.put("sentOriginPriceSum", sentOriginPriceSum.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
			sumMap.put("sentCustomerPriceSum", sentCustomerPriceSum.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
			sumMap.put("sentChannelPriceSum", sentChannelPriceSum.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
			sumMap.put("failCountSum", failCountSum.intValue());
			sumMap.put("failOriginPriceSum", failOriginPriceSum.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
			sumMap.put("failCustomerPriceSum", failCustomerPriceSum.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
			sumMap.put("failChannelPriceSum", failChannelPriceSum.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
			sumMap.put("succCountSum", succCountSum.intValue());
			sumMap.put("succOriginPriceSum", succOriginPriceSum.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
			sumMap.put("succCustomerPriceSum", succCustomerPriceSum.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
			sumMap.put("succChannelPriceSum", succChannelPriceSum.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
			sumMap.put("profitSum", profitSum.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
		}
		return sumMap;
	} 
	
	 
}