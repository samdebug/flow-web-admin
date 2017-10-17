package com.yzx.flow.modular.flowWarn.service;

import java.util.List;

import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.persistence.model.FlowWarnMsg;

public interface IFlowWarnMsgService {

	public abstract List<FlowWarnMsg> pageQuery(Page<FlowWarnMsg> page);

	public abstract void insert(FlowWarnMsg data);

	public abstract FlowWarnMsg get(Long warnMsgId);

	public abstract void saveAndUpdate(FlowWarnMsg data);

	public abstract void update(FlowWarnMsg data);

	public abstract int delete(Long warnMsgId);

}