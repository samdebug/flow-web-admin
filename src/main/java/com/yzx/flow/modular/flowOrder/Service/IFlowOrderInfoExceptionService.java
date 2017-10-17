package com.yzx.flow.modular.flowOrder.Service;

import org.springframework.transaction.annotation.Transactional;

import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.page.PageInfoBT;
import com.yzx.flow.common.persistence.model.FlowOrderInfo;

public interface IFlowOrderInfoExceptionService {

	//@DataSource(DataSourceType.READ)
	public abstract PageInfoBT<FlowOrderInfo> pageQuery(Page<FlowOrderInfo> page);

	//@DataSource(DataSourceType.READ)
	public abstract FlowOrderInfo get(Long orderId);

	public abstract boolean reFailBack(String orderIds);

}