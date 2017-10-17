package com.yzx.flow.modular.statistics.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.page.PageInfoBT;
import com.yzx.flow.common.persistence.model.ChannelAnalyse;
import com.yzx.flow.common.util.DateUtil;
import com.yzx.flow.core.aop.dbrouting.DataSource;
import com.yzx.flow.core.aop.dbrouting.DataSourceType;
import com.yzx.flow.modular.statistics.service.IChannelAnalyseService;
import com.yzx.flow.modular.system.dao.ChannelAnalyseDao;

/**
 * 
 * <b>Title：</b>ChannelAnalyseService.java<br/>
 * <b>Description：</b> <br/>
 * <b>@author： </b>v5480<br/>
 * <b>@date：</b>2016-03-04 11:17:21<br/>
 * <b>Copyright (c) 2016 szwisdom Tech.</b>
 * 
 */
@Service("channelAnalyseService")
public class ChannelAnalyseServiceImpl implements IChannelAnalyseService {
	@Autowired
	private ChannelAnalyseDao channelAnalyseDao;

	@DataSource(DataSourceType.READ)
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.statistics.service.impl.IChannelAnalyseService#pageQuery(com.yzx.flow.common.page.Page)
	 */
	@Override
	@Transactional(readOnly=true)
	public PageInfoBT<ChannelAnalyse> pageQuery(Page<ChannelAnalyse> page) {
		page.getParams().put("activeDate",DateUtil.getDateStringOfHour(-1, DateUtil.YYYY_MM_DD_HH_MM_SS_EN));
		List<ChannelAnalyse> list = channelAnalyseDao.pageQuery(page);
		PageInfoBT<ChannelAnalyse> resultPage = new PageInfoBT<>(list, page.getTotal());
		return resultPage;
	}
}