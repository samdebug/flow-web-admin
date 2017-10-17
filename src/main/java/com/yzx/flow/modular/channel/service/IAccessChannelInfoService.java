package com.yzx.flow.modular.channel.service;

import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.page.PageInfoBT;
import com.yzx.flow.common.persistence.model.AccessChannelInfo;
import com.yzx.flow.common.persistence.model.AreaCode;
import com.yzx.flow.common.persistence.model.ChannelToGroup;
import com.yzx.flow.common.persistence.model.FlowPackageInfo;
import com.yzx.flow.common.persistence.model.Staff;

public interface IAccessChannelInfoService {

	public abstract PageInfoBT<AccessChannelInfo> pageQuery(
			Page<AccessChannelInfo> page);

	public abstract void insert(AccessChannelInfo data);

	public abstract AccessChannelInfo get(Long channelSeqId);

	public abstract void saveAndUpdate(AccessChannelInfo data, Staff staff);

	public abstract void update(AccessChannelInfo data);
	
	public  void updateChannelMaintainZone(Map params);

	public abstract int delete(Long channelSeqId, AccessChannelInfo data);

	/**
	 * 获取所有的地区信息
	 * @return
	 */
	public abstract List<AreaCode> selectALL();

	public abstract List<AccessChannelInfo> selectByInfo(AccessChannelInfo info);

	public abstract List<AccessChannelInfo> selectAllChannelInfo(
			Long[] channelSeqIds, String text);

	public abstract List<FlowPackageInfo> getFlowPackageInfoList(
			String packageType, String[] packageIds, String zone,
			String operator);

	/**
	 * 查询通道对应通道组
	 * @return
	 */
	public abstract List<ChannelToGroup> selectByChannelSeqId(Long channelSeqId);

	/**
	 * 通道添加到通道组
	 * @param data
	 */
	public abstract void addToGroup(AccessChannelInfo data);

}