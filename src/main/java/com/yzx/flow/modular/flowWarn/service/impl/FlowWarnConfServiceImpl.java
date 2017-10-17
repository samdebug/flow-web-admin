package com.yzx.flow.modular.flowWarn.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.persistence.model.FlowWarnConf;
import com.yzx.flow.core.shiro.ShiroKit;
import com.yzx.flow.modular.flowWarn.service.IFlowWarnConfService;
import com.yzx.flow.modular.system.dao.FlowWarnConfDao;

/**
 * 
 * @author max
 * @date 2017年8月17日
 */
@Service("flowWarnConfService")
public class FlowWarnConfServiceImpl implements IFlowWarnConfService {
	@Autowired
	private FlowWarnConfDao flowWarnConfDao;

	@Override
	public List<FlowWarnConf> pageQuery(Page<FlowWarnConf> page) {
		List<FlowWarnConf> list = flowWarnConfDao.pageQuery(page);
		return list;
	}

	@Override
	public void insert(FlowWarnConf data) {
		flowWarnConfDao.insert(data);
	}

	@Override
	public FlowWarnConf get(Long warnConfId) {
		return flowWarnConfDao.selectByPrimaryKey(warnConfId);
	}

	@Override
	public void saveAndUpdate(FlowWarnConf data) throws Exception {
		if (null != data.getWarnConfId()) {// 判断有没有传主键，如果传了为更新，否则为新增
			data.setUpdateTime(new Date());
			data.setUpdator(ShiroKit.getUser().getName());
			this.update(data);
		} else {
			data.setCreateTime(new Date());
			data.setCreator(ShiroKit.getUser().getName());
			this.insert(data);
		}
	}

	@Override
	public void update(FlowWarnConf data) {
		flowWarnConfDao.updateByPrimaryKey(data);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.yzx.flow.modular.flowWarn.service.impl.IFlowWarnConfService#delete
	 * (java.lang.Long)
	 */
	@Override
	public int delete(Long warnConfId) {
		return flowWarnConfDao.deleteByPrimaryKey(warnConfId);
	}
}