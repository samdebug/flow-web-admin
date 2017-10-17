package com.yzx.flow.modular.statistics.service;

import java.util.List;
import java.util.Map;

import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.page.PageInfoBT;
import com.yzx.flow.common.persistence.model.AreaCode;
import com.yzx.flow.common.persistence.model.ChannelSupplierStatistics;
import com.yzx.flow.common.persistence.model.SuppilerTradeDay;

public interface IChannelSupplierStatisticsService {
	public Page<ChannelSupplierStatistics> customerPageQuery(Page<ChannelSupplierStatistics> page) throws Exception;
	
	/**
	 * 统计客户详情总和
	 * @param page
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> detailTotal(Page<ChannelSupplierStatistics> page) throws Exception;
	
	/**
     * 获取所有的地区信息
     * @return
     */
    public List<AreaCode> selectALL();
    
	public Page<ChannelSupplierStatistics> view(Page<ChannelSupplierStatistics> page) throws Exception;
	
	public Map<String, Object> viewTotal(Page<ChannelSupplierStatistics> page) throws Exception ;
	
	public Page<ChannelSupplierStatistics> query(Page<ChannelSupplierStatistics> page) throws Exception;
	
	public Map<String, Object> queryTotal(Page<ChannelSupplierStatistics> page) throws Exception;
	
	public PageInfoBT<SuppilerTradeDay> suppilerTradeQuery(Page<SuppilerTradeDay> page) ;
	
	public Page<ChannelSupplierStatistics> queryProfitRate(Page<ChannelSupplierStatistics> page) throws Exception;
	
	public Map<String, Object> queryProfitRateTotal(Page<ChannelSupplierStatistics> page) throws Exception ;
	
}
