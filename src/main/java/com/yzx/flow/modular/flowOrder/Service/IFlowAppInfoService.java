package com.yzx.flow.modular.flowOrder.Service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.page.PageInfoBT;
import com.yzx.flow.common.persistence.model.AccessChannelGroup;
import com.yzx.flow.common.persistence.model.ExportAppInfo;
import com.yzx.flow.common.persistence.model.FlowAppInfo;

public interface IFlowAppInfoService {

	public abstract PageInfoBT<FlowAppInfo> pageQuery(Page<FlowAppInfo> page);

	public abstract void insert(FlowAppInfo data);

	public abstract FlowAppInfo get(Long flowAppId);

	public abstract void saveAndUpdate(FlowAppInfo data);

	public abstract void update(FlowAppInfo data);

	public abstract int delete(Long flowAppId);

	public abstract FlowAppInfo getFlowAppInfoByAppId(String appId);

	public abstract List<ExportAppInfo> getProductListByOrderId(Long orderId,
			String appId);

	public abstract List<FlowAppInfo> getFlowAppInfo(FlowAppInfo flowAppInfo);

	public abstract FlowAppInfo getAppInfoByOrderId(Long orderId);

	public abstract List<FlowAppInfo> getAppInfoListByOrderId(Long orderId);

	public abstract List<AccessChannelGroup> getDispatchChannelList(
			String dispatchChannel);

	//@DataSource(DataSourceType.READ)
	public abstract List<FlowAppInfo> selectInfoByIdOrName(String idOrName);

}