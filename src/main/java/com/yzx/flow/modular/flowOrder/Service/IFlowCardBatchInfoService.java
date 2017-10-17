package com.yzx.flow.modular.flowOrder.Service;

import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.persistence.model.FlowCardBatchInfo;

public interface IFlowCardBatchInfoService {

	Page<FlowCardBatchInfo> pageQuery(Page<FlowCardBatchInfo> page);

	void insert(FlowCardBatchInfo data);

	FlowCardBatchInfo get(Long batchId);

	void saveAndUpdate(FlowCardBatchInfo data);

	void update(FlowCardBatchInfo data);

	int delete(Long batchId);

}