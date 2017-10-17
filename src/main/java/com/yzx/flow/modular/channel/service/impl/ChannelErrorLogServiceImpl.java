package com.yzx.flow.modular.channel.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;







import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.page.PageInfoBT;
import com.yzx.flow.common.persistence.model.ChannelErrorLog;
import com.yzx.flow.modular.channel.service.IChannelErrorLogService;
import com.yzx.flow.modular.system.dao.ChannelErrorLogDao;

import java.util.Date;
import java.util.List;


/**
 * 
 * <b>Title：</b>ChannelErrorLogService.java<br/>
 * <b>Description：</b> <br/>
 * <b>@author： </b>v5480<br/>
 * <b>@date：</b>2015-10-28 14:43:40<br/>
 * <b>Copyright (c) 2015 szwisdom Tech.</b>
 * 
 */
@Service("channelErrorLogService")
public class ChannelErrorLogServiceImpl implements IChannelErrorLogService {
	@Autowired
	private ChannelErrorLogDao channelErrorLogDao;

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.channel.service.impl.IChannelErrorLogService#pageQuery(com.yzx.flow.common.page.Page)
	 */
	@Override
	public PageInfoBT<ChannelErrorLog> pageQuery(Page<ChannelErrorLog> page) {
		List<ChannelErrorLog> list = channelErrorLogDao.pageQuery(page);
		PageInfoBT<ChannelErrorLog> resPage=new PageInfoBT<ChannelErrorLog>(list, page.getTotal());
		return resPage;
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.channel.service.impl.IChannelErrorLogService#insert(com.yzx.flow.common.persistence.model.ChannelErrorLog)
	 */
	@Override
	public void insert(ChannelErrorLog data) {
		channelErrorLogDao.insert(data);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.channel.service.impl.IChannelErrorLogService#get(java.lang.Long)
	 */
	@Override
	public ChannelErrorLog get(Long channelFlowId) {
		return channelErrorLogDao.selectByPrimaryKey(channelFlowId);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.channel.service.impl.IChannelErrorLogService#saveAndUpdate(com.yzx.flow.common.persistence.model.ChannelErrorLog)
	 */
	@Override
	public void saveAndUpdate(ChannelErrorLog data) {
		if (null != data.getChannelFlowId()) {// 判断有没有传主键，如果传了为更新，否则为新增
			this.update(data);
		} else {
			data.setInputTime(new Date());
			this.insert(data);
		}
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.channel.service.impl.IChannelErrorLogService#update(com.yzx.flow.common.persistence.model.ChannelErrorLog)
	 */
	@Override
	public void update(ChannelErrorLog data) {
		channelErrorLogDao.updateByPrimaryKey(data);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.channel.service.impl.IChannelErrorLogService#delete(java.lang.Long)
	 */
	@Override
	public int delete(Long channelFlowId) {
		return channelErrorLogDao.deleteByPrimaryKey(channelFlowId);
	}
}