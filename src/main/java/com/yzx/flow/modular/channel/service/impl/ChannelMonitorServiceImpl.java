package com.yzx.flow.modular.channel.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.page.PageInfoBT;
import com.yzx.flow.core.aop.dbrouting.DataSource;
import com.yzx.flow.core.aop.dbrouting.DataSourceType;
import com.yzx.flow.modular.channel.service.IChannelMonitorService;
import com.yzx.flow.modular.system.dao.ChannelMonitorDao;

/**
 * 通道监控
 * @author hc4gw02
 *
 */
@Service("channelMonitorService")
public class ChannelMonitorServiceImpl implements IChannelMonitorService {
	@Autowired
	private ChannelMonitorDao channelMonitorDao;

	private static final Logger LOG = LoggerFactory.getLogger(ChannelMonitorServiceImpl.class);

	//=@DataSource(DataSourceType.READ)
	@Override
	public PageInfoBT<Map<String, Object>> pageQuery(Page<Map<String, Object>> page) {
		List<Map<String, Object>> list = channelMonitorDao.pageQuery(page);
		if (null == list) {
			list = new ArrayList<Map<String,Object>>();
		}
		LOG.info("通道监控总数,size={}",list.size());
		return new PageInfoBT<Map<String,Object>>(list, page.getTotal());
	}
	@DataSource(DataSourceType.READ)
	@Override
	public List<Map<String, Object>> queryDetail(Page<Map<String, Object>> page) {
		List<Map<String, Object>> list = channelMonitorDao.queryDetail(page);
		if (null == list) {
			list = new ArrayList<Map<String,Object>>();
		}
		LOG.info("通道监控总数,size={}",list.size());
		return list;
	}
	
	@DataSource(DataSourceType.READ)
	@Override
	public List<Map<String, Object>> queryProfitRatioDetail(Page<Map<String, Object>> page) {
		
		List<Map<String, Object>> list = channelMonitorDao.queryProfitRatioDetail(page);
		
		if (null == list) {
			list = new ArrayList<Map<String,Object>>();
		}
		
		//查询partnerName
		for(Map<String, Object> tmpMap : list){
			Long partnerId = (Long) tmpMap.get("partnerId");
			String partnerName = channelMonitorDao.queryPartnerName(partnerId);
			tmpMap.put("partnerName", partnerName);
		}
		LOG.info("通道利润率监控总数,size={}",list.size());
		return list;
	}
}