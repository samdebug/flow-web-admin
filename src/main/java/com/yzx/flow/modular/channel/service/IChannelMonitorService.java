package com.yzx.flow.modular.channel.service;

import java.util.List;
import java.util.Map;

import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.page.PageInfoBT;

public interface IChannelMonitorService {

	/**
	 * 通道监控
	 * @param page
	 * @return
	 */
	//@DataSource(DataSourceType.READ)
	public abstract PageInfoBT<Map<String, Object>> pageQuery(
			Page<Map<String, Object>> page);

	//@DataSource(DataSourceType.READ)
	public abstract List<Map<String, Object>> queryDetail(
			Page<Map<String, Object>> page);

	//@DataSource(DataSourceType.READ)
	public abstract List<Map<String, Object>> queryProfitRatioDetail(
			Page<Map<String, Object>> page);

}