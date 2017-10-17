package com.yzx.flow.modular.flowOrder.Service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.page.PageInfoBT;
import com.yzx.flow.common.persistence.model.FlowOrderInfo;
import com.yzx.flow.common.persistence.model.FlowOrderRefundRecord;

public interface IFlowOrderInfoService {

	//@DataSource(DataSourceType.READ)
	public abstract PageInfoBT<FlowOrderInfo> pageQuery(Page<FlowOrderInfo> page);

	//@DataSource(DataSourceType.READ)
	public abstract List<FlowOrderRefundRecord> excelRefund(
			Page<FlowOrderRefundRecord> page) throws Exception;

	//@DataSource(DataSourceType.READ)
	public abstract PageInfoBT<FlowOrderInfo> pageQueryForRefund(
			Page<FlowOrderInfo> page) throws Exception;

	//@DataSource(DataSourceType.READ)
	public abstract FlowOrderInfo pageQueryByPrice(Page<FlowOrderInfo> page);

	public abstract void insert(FlowOrderInfo data);

	//@DataSource(DataSourceType.READ)
	public abstract FlowOrderInfo get(Long orderId);

	public abstract void saveAndUpdate(FlowOrderInfo data);

	public abstract void refund(String orderId, String remark) throws Exception;

	public abstract void update(FlowOrderInfo data);

	public abstract void reCallBack(String orderIds);

	public abstract void reFailBack(String orderIds);

	public abstract void reSuccessBack(String orderIds);

	public abstract void reSend(String orderIds);

	public abstract int delete(Long orderId);

	public abstract List<FlowOrderInfo> selectByFlowAppId(Long flowAppId);

	//@DataSource(DataSourceType.READ)
	public abstract Page<FlowOrderInfo> export(Page<FlowOrderInfo> page)
			throws Exception;

	public abstract Page<FlowOrderInfo> exportWithoutAuth(
			Page<FlowOrderInfo> page) throws Exception;

	//@DataSource(DataSourceType.READ)
	public abstract List<String> queryStuckChannelIds(int inHours,
			int stuckRows, List<Long> channelIdsInEnable);

	public abstract List<Long> queryCustomerIds(Page<FlowOrderInfo> page);

}