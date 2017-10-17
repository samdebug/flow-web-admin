package com.yzx.flow.modular.flowOrder.Service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.persistence.model.FlowPackageInfoProduct;
import com.yzx.flow.modular.flowOrder.Service.IFlowPackageInfoProductService;
import com.yzx.flow.modular.system.dao.FlowPackageInfoProductDao;

/**
 * 
 * <b>Title：</b>FlowPackageInfoProductService.java<br/>
 * <b>Description：</b> <br/>
 * <b>@author： </b>v5480<br/>
 * <b>@date：</b>2015-07-08 18:05:18<br/>
 * <b>Copyright (c) 2015 szwisdom Tech.</b>
 * 
 */
@Service("flowPackageInfoProductService")
public class FlowPackageInfoProductService implements IFlowPackageInfoProductService {
	@Autowired
	private FlowPackageInfoProductDao flowPackageInfoProductDao;

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.flowOrder.Service.impl.IFlowPackageInfoProductService#pageQuery(com.yzx.flow.common.page.Page)
	 */
	public Page<FlowPackageInfoProduct> pageQuery(Page<FlowPackageInfoProduct> page) {
		List<FlowPackageInfoProduct> list = flowPackageInfoProductDao.pageQuery(page);
		page.setDatas(list);
		return page;
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.flowOrder.Service.impl.IFlowPackageInfoProductService#insert(com.yzx.flow.common.persistence.model.FlowPackageInfoProduct)
	 */
	public void insert(FlowPackageInfoProduct data) {
		flowPackageInfoProductDao.insert(data);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.flowOrder.Service.impl.IFlowPackageInfoProductService#get(java.lang.Long)
	 */
	public FlowPackageInfoProduct get(Long seqId) {
		return flowPackageInfoProductDao.selectByPrimaryKey(seqId);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.flowOrder.Service.impl.IFlowPackageInfoProductService#saveAndUpdate(com.yzx.flow.common.persistence.model.FlowPackageInfoProduct)
	 */
	public void saveAndUpdate(FlowPackageInfoProduct data) {
		if (null != data.getSeqId()) {// 判断有没有传主键，如果传了为更新，否则为新增
			this.update(data);
		} else {
			this.insert(data);
		}
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.flowOrder.Service.impl.IFlowPackageInfoProductService#update(com.yzx.flow.common.persistence.model.FlowPackageInfoProduct)
	 */
	public void update(FlowPackageInfoProduct data) {
		flowPackageInfoProductDao.updateByPrimaryKey(data);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.flowOrder.Service.impl.IFlowPackageInfoProductService#delete(java.lang.Long)
	 */
	public int delete(Long seqId) {
		return flowPackageInfoProductDao.deleteByPrimaryKey(seqId);
	}
}