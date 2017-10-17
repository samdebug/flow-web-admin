package com.yzx.flow.modular.flow.service;

import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.persistence.model.FlowPlusProduct;

public interface IFlowPlusProductService {

	Page<FlowPlusProduct> pageQuery(Page<FlowPlusProduct> page);

	void insert(FlowPlusProduct data);

	FlowPlusProduct get(Long productPlusId);

	FlowPlusProduct getByProductId(Long productId);

	void saveAndUpdate(FlowPlusProduct data);

	void update(FlowPlusProduct data);

	int delete(Long productPlusId);

}