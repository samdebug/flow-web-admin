package com.yzx.flow.modular.flowOrder.Service;

import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.persistence.model.FlowPackageInfoProduct;

public interface IFlowPackageInfoProductService {

	Page<FlowPackageInfoProduct> pageQuery(Page<FlowPackageInfoProduct> page);

	void insert(FlowPackageInfoProduct data);

	FlowPackageInfoProduct get(Long seqId);

	void saveAndUpdate(FlowPackageInfoProduct data);

	void update(FlowPackageInfoProduct data);

	int delete(Long seqId);

}