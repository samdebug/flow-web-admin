package com.yzx.flow.modular.flow.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.persistence.model.FlowPlusProduct;
import com.yzx.flow.modular.flow.service.IFlowPlusProductService;
import com.yzx.flow.modular.system.dao.FlowPlusProductDao;

/**
 * 
 * <b>Title：</b>FlowPlusProductService.java<br/>
 * <b>Description：</b> <br/>
 * <b>@author： </b>v5480<br/>
 * <b>@date：</b>2015-07-08 18:05:18<br/>
 * <b>Copyright (c) 2015 szwisdom Tech.</b>
 * 
 */
@Service("flowPlusProductService")
public class FlowPlusProductServiceImpl implements IFlowPlusProductService {
	@Autowired
	private FlowPlusProductDao flowPlusProductDao;

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.flow.service.IFlowPlusProductService#pageQuery(com.yzx.flow.common.page.Page)
	 */
	public Page<FlowPlusProduct> pageQuery(Page<FlowPlusProduct> page) {
		List<FlowPlusProduct> list = flowPlusProductDao.pageQuery(page);
		page.setDatas(list);
		return page;
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.flow.service.IFlowPlusProductService#insert(com.yzx.flow.common.persistence.model.FlowPlusProduct)
	 */
	public void insert(FlowPlusProduct data) {
		flowPlusProductDao.insert(data);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.flow.service.IFlowPlusProductService#get(java.lang.Long)
	 */
	public FlowPlusProduct get(Long productPlusId) {
		return flowPlusProductDao.selectByPrimaryKey(productPlusId);
	}
	
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.flow.service.IFlowPlusProductService#getByProductId(java.lang.Long)
	 */
	public FlowPlusProduct getByProductId(Long productId) {
		return flowPlusProductDao.selectByProductId(productId);
	}
	
	

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.flow.service.IFlowPlusProductService#saveAndUpdate(com.yzx.flow.common.persistence.model.FlowPlusProduct)
	 */
	public void saveAndUpdate(FlowPlusProduct data) {
		if (null != data.getProductPlusId()) {// 判断有没有传主键，如果传了为更新，否则为新增
			this.update(data);
		} else {
			this.insert(data);
		}
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.flow.service.IFlowPlusProductService#update(com.yzx.flow.common.persistence.model.FlowPlusProduct)
	 */
	public void update(FlowPlusProduct data) {
		flowPlusProductDao.updateByPrimaryKey(data);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.flow.service.IFlowPlusProductService#delete(java.lang.Long)
	 */
	public int delete(Long productPlusId) {
		return flowPlusProductDao.deleteByPrimaryKey(productPlusId);
	}
}