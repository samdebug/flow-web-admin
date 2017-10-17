package com.yzx.flow.modular.flowWarn.service;

import java.util.List;

import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.persistence.model.FlowWarnConf;

public interface IFlowWarnConfService {

	public abstract List<FlowWarnConf> pageQuery(Page<FlowWarnConf> page);

	public abstract void insert(FlowWarnConf data);

	public abstract FlowWarnConf get(Long warnConfId);

	public abstract void saveAndUpdate(FlowWarnConf data) throws Exception;

	public abstract void update(FlowWarnConf data);

	public abstract int delete(Long warnConfId);

}