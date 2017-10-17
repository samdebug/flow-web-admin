package com.yzx.flow.modular.channel.service;

import java.util.List;

import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.page.PageInfoBT;
import com.yzx.flow.common.persistence.model.ChannelAdapter;

public interface IChannelAdapterService {

	public abstract PageInfoBT<ChannelAdapter> pageQuery(Page<ChannelAdapter> page);

	public abstract void insert(ChannelAdapter data);

	public abstract ChannelAdapter get(Long adapterId);

	public abstract List<ChannelAdapter> find(ChannelAdapter data);

	public abstract void saveAndUpdate(ChannelAdapter data) throws Exception;

	public abstract void update(ChannelAdapter data);

	public abstract int delete(Long adapterId);

}