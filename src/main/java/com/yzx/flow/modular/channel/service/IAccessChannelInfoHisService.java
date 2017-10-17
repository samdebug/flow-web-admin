package com.yzx.flow.modular.channel.service;

import java.util.List;

import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.page.PageInfoBT;
import com.yzx.flow.common.persistence.model.AccessChannelInfo;

public interface IAccessChannelInfoHisService {

	public abstract PageInfoBT<AccessChannelInfo>  pageQuery(Page<AccessChannelInfo> page);

	/**
	 * 
	 * @param data
	 * @param operateCode 操作编码 0 修改通道信息 1 通道有效无效 2 删除
	 */
	public abstract void insert(AccessChannelInfo data, String operateCode);

	public abstract AccessChannelInfo get(Long channelSeqId);

	public abstract void update(AccessChannelInfo data);

}