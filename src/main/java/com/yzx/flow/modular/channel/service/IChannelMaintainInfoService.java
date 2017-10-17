package com.yzx.flow.modular.channel.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.page.PageInfoBT;
import com.yzx.flow.common.persistence.model.AreaCode;
import com.yzx.flow.common.persistence.model.ChannelMaintainInfo;
import com.yzx.flow.common.persistence.model.Staff;

public interface IChannelMaintainInfoService {

	public abstract PageInfoBT<ChannelMaintainInfo> pageQuery(
			Page<ChannelMaintainInfo> page);

	public abstract void insert(ChannelMaintainInfo data);

	public abstract ChannelMaintainInfo get(Long maintainSeqId);

	public abstract void saveAndUpdate(ChannelMaintainInfo data, Staff staff,
			HttpServletRequest request);

	public abstract void update(ChannelMaintainInfo data);

	public abstract int delete(Long maintainSeqId);

	public abstract List<AreaCode> selectAreaCodeAll();

}