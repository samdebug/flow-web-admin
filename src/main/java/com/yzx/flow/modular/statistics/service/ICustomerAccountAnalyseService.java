package com.yzx.flow.modular.statistics.service;

import java.util.Map;

import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.page.PageInfoBT;
import com.yzx.flow.common.persistence.model.CustomerStatisticsDay;

public interface ICustomerAccountAnalyseService {
	
	/**
	 * 分页统计数据
	 * @param page
	 * @return
	 * @throws Exception
	 */
	public PageInfoBT<CustomerStatisticsDay> pageQuery(Page<CustomerStatisticsDay> page) throws Exception ;
	
	/**
	 * 统计总和
	 * @param page
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> statisticsTotal(Page<CustomerStatisticsDay> page) throws Exception ;
}
