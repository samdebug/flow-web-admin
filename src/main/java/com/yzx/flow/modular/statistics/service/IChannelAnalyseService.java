package com.yzx.flow.modular.statistics.service;

import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.page.PageInfoBT;
import com.yzx.flow.common.persistence.model.ChannelAnalyse;

public interface IChannelAnalyseService {

	//	@DataSource(DataSourceType.READ)
	public abstract PageInfoBT<ChannelAnalyse> pageQuery(Page<ChannelAnalyse> page);

}