package com.yzx.flow.modular.flowWarn.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.persistence.model.FlowWarnMsg;
import com.yzx.flow.modular.flowWarn.service.IFlowWarnMsgService;
import com.yzx.flow.modular.system.dao.FlowWarnMsgDao;

/**
 * 
 * @author max
 * @date 2017年8月17日
 */
@Service("flowWarnMsgService")
public class FlowWarnMsgServiceImpl implements IFlowWarnMsgService {
	@Autowired
	private FlowWarnMsgDao flowWarnMsgDao;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.yzx.flow.modular.flowWarn.service.impl.IFlowWarnMsgService#pageQuery
	 * (com.yzx.flow.common.page.Page)
	 */
	@Override
	public List<FlowWarnMsg> pageQuery(Page<FlowWarnMsg> page) {
		List<FlowWarnMsg> list = flowWarnMsgDao.pageQuery(page);
		page.setDatas(list);
		return list;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.yzx.flow.modular.flowWarn.service.impl.IFlowWarnMsgService#insert
	 * (com.yzx.flow.common.persistence.model.FlowWarnMsg)
	 */
	@Override
	public void insert(FlowWarnMsg data) {
		flowWarnMsgDao.insert(data);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.yzx.flow.modular.flowWarn.service.impl.IFlowWarnMsgService#get(java
	 * .lang.Long)
	 */
	@Override
	public FlowWarnMsg get(Long warnMsgId) {
		return flowWarnMsgDao.selectByPrimaryKey(warnMsgId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.yzx.flow.modular.flowWarn.service.impl.IFlowWarnMsgService#saveAndUpdate
	 * (com.yzx.flow.common.persistence.model.FlowWarnMsg)
	 */
	@Override
	public void saveAndUpdate(FlowWarnMsg data) {
		if (null != data.getWarnMsgId()) {// 判断有没有传主键，如果传了为更新，否则为新增
			this.update(data);
		} else {
			this.insert(data);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.yzx.flow.modular.flowWarn.service.impl.IFlowWarnMsgService#update
	 * (com.yzx.flow.common.persistence.model.FlowWarnMsg)
	 */
	@Override
	public void update(FlowWarnMsg data) {
		flowWarnMsgDao.updateByPrimaryKey(data);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.yzx.flow.modular.flowWarn.service.impl.IFlowWarnMsgService#delete
	 * (java.lang.Long)
	 */
	@Override
	public int delete(Long warnMsgId) {
		return flowWarnMsgDao.deleteByPrimaryKey(warnMsgId);
	}
}