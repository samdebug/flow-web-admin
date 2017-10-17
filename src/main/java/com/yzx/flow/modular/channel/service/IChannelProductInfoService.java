package com.yzx.flow.modular.channel.service;

import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.persistence.model.ChannelProductInfo;

public interface IChannelProductInfoService {

	public abstract List<ChannelProductInfo> pageQuery(
			Page<ChannelProductInfo> page);

	public abstract void insert(ChannelProductInfo data);

	public abstract ChannelProductInfo get(Long channelProductSeqId);

	public abstract void saveAndUpdate(ChannelProductInfo data);

	public abstract void update(ChannelProductInfo data);

	public abstract int delete(Long channelProductSeqId);

	public abstract List<ChannelProductInfo> selectByChannelSeqId(
			Long channelSeqId);

	/**
	 * 修改通道产品有效无效
	 * @param channelSeqId
	 */
	public abstract int changeChannelProductStatus(Map<String, Object> params);

}