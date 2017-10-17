package com.yzx.flow.modular.statistics.service;

import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.persistence.model.ChannelAccountAnalyse;

public interface IChannelAccountAnalyseService {

	//	@DataSource(DataSourceType.READ)
	public abstract List pageQuery(Page<ChannelAccountAnalyse> page)
			throws Exception;

	//	@DataSource(DataSourceType.READ)
	public abstract List query(Map<String, Object> params) throws Exception;

}