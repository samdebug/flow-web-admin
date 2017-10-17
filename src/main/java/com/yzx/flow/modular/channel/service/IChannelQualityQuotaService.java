package com.yzx.flow.modular.channel.service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.page.PageInfoBT;
import com.yzx.flow.common.persistence.model.ChannelQualityQuota;
import com.yzx.flow.common.persistence.model.Staff;

public interface IChannelQualityQuotaService {

	public abstract PageInfoBT<ChannelQualityQuota> pageQuery(
			Page<ChannelQualityQuota> page);

	public abstract void insert(ChannelQualityQuota data);

	public abstract ChannelQualityQuota get(Long quotaId);

	public abstract void saveAndUpdate(ChannelQualityQuota data, Staff staff);

	public abstract void update(ChannelQualityQuota data);

	public abstract int delete(Long quotaId);

	public abstract List<ChannelQualityQuota> selectQuotaInfo(String quotaName);

}