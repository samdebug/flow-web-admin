package com.yzx.flow.modular.channel.service;

import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.page.PageInfoBT;
import com.yzx.flow.common.persistence.model.AccessChannelGroup;
import com.yzx.flow.common.persistence.model.ChannelGroupToGroup;
import com.yzx.flow.common.persistence.model.ChannelToGroup;

public interface IAccessChannelGroupService {

	public abstract PageInfoBT<AccessChannelGroup> pageQuery(
			Page<AccessChannelGroup> page);

	public abstract void insert(AccessChannelGroup data);

	public abstract AccessChannelGroup get(Long channelGroupId);

	public abstract List<AccessChannelGroup> selectByInfo(
			AccessChannelGroup info);

	public abstract List<ChannelToGroup> getChannelToGroupsByChannelGroupId(
			Long channelGroupId);

	public abstract void saveAndUpdate(AccessChannelGroup data);

	public abstract void update(AccessChannelGroup data);

	public abstract int delete(Long channelGroupId);

	/**
	 * 获取可引用的通道组信息
	 * @param params
	 * @return
	 */
	public abstract List<AccessChannelGroup> selectQuoteChannelGroup(
			Map<String, Object> params);

	/**
	 * 获取关联通道组
	 * @param channelGroupId
	 * @return
	 */
	public abstract List<ChannelGroupToGroup> getGroupToGroupsByChannelGroupId(
			Long channelGroupId);

	/**
	 * 获取关联通道组
	 * @param channelGroupId
	 * @return
	 */
	public abstract List<ChannelGroupToGroup> getGroupToGroupsByQuoteGroupId(
			Long quoteGroupId);

	/**
	 * 自定义通道组添加到通道组
	 * @param data
	 */
	public abstract void groupAddToGroup(AccessChannelGroup data);

}