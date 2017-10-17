package com.yzx.flow.modular.channel.service;

import java.util.List;

import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.page.PageInfoBT;
import com.yzx.flow.common.persistence.model.ChannelErrorLog;

public interface IChannelErrorLogService {

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.channel.service.impl.IChannelErrorLogService#pageQuery(com.yzx.flow.common.page.Page)
	 */
	public abstract PageInfoBT<ChannelErrorLog> pageQuery(Page<ChannelErrorLog> page);

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.channel.service.impl.IChannelErrorLogService#insert(com.yzx.flow.common.persistence.model.ChannelErrorLog)
	 */
	public abstract void insert(ChannelErrorLog data);

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.channel.service.impl.IChannelErrorLogService#get(java.lang.Long)
	 */
	public abstract ChannelErrorLog get(Long channelFlowId);

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.channel.service.impl.IChannelErrorLogService#saveAndUpdate(com.yzx.flow.common.persistence.model.ChannelErrorLog)
	 */
	public abstract void saveAndUpdate(ChannelErrorLog data);

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.channel.service.impl.IChannelErrorLogService#update(com.yzx.flow.common.persistence.model.ChannelErrorLog)
	 */
	public abstract void update(ChannelErrorLog data);

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.channel.service.impl.IChannelErrorLogService#delete(java.lang.Long)
	 */
	public abstract int delete(Long channelFlowId);

}