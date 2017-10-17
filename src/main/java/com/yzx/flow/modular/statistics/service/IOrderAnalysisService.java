package com.yzx.flow.modular.statistics.service;

import java.util.Map;

import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.page.PageInfoBT;
import com.yzx.flow.common.persistence.model.OrderAnalysisInfo;

public interface IOrderAnalysisService {

	public abstract PageInfoBT<OrderAnalysisInfo> pageQuery(
			Page<OrderAnalysisInfo> page) throws Exception;

	public abstract Map<String, Object> queryTotal(Page<OrderAnalysisInfo> page)
			throws Exception;

	public abstract Page<OrderAnalysisInfo> export(Page<OrderAnalysisInfo> page);

}