package com.yzx.flow.modular.orderDealRecord.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.persistence.model.OrderDealRecord;
import com.yzx.flow.common.persistence.model.OrderDealRecordWithBLOBs;
import com.yzx.flow.core.aop.dbrouting.DataSource;
import com.yzx.flow.core.aop.dbrouting.DataSourceType;
import com.yzx.flow.modular.orderDealRecord.service.IOrderDealRecordService;
import com.yzx.flow.modular.system.dao.OrderDealRecordDao;

/**
 * 
 * <b>Title：</b>OrderDealRecordService.java<br/>
 * <b>Description：</b> <br/>
 * <b>@author： </b>v5480<br/>
 * <b>@date：</b>2015-10-21 14:17:38<br/>
 * <b>Copyright (c) 2015 szwisdom Tech.</b>
 * 
 */
@Service("orderDealRecordService")
public class OrderDealRecordServiceImpl implements IOrderDealRecordService {
	
	@Autowired
	private OrderDealRecordDao orderDealRecordDao;

	@DataSource(DataSourceType.READ)
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.orderDealRecord.service.IOrderDealRecordService#pageQuery(com.yzx.flow.common.page.Page)
	 */
	@Override
	@Transactional(readOnly=true)
	public Page<OrderDealRecord> pageQuery(Page<OrderDealRecord> page) {
		List<OrderDealRecord> list = orderDealRecordDao.pageQuery(page);
		page.setDatas(list);
		return page;
	}

	@DataSource(DataSourceType.READ)
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.orderDealRecord.service.IOrderDealRecordService#selectByInfo(com.yzx.flow.common.persistence.model.OrderDealRecordWithBLOBs)
	 */
	@Override
	@Transactional(readOnly=true)
	public List<OrderDealRecordWithBLOBs> selectByInfo(OrderDealRecordWithBLOBs record) {
	    return orderDealRecordDao.selectByInfo(record);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.orderDealRecord.service.IOrderDealRecordService#insert(com.yzx.flow.common.persistence.model.OrderDealRecordWithBLOBs)
	 */
	@Override
	public void insert(OrderDealRecordWithBLOBs data) {
		orderDealRecordDao.insert(data);
	}

	@DataSource(DataSourceType.READ)
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.orderDealRecord.service.IOrderDealRecordService#get(java.lang.Long)
	 */
	@Override
	@Transactional(readOnly=true)
	public OrderDealRecordWithBLOBs get(Long dealRecordId) {
		return orderDealRecordDao.selectByPrimaryKey(dealRecordId);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.orderDealRecord.service.IOrderDealRecordService#saveAndUpdate(com.yzx.flow.common.persistence.model.OrderDealRecordWithBLOBs)
	 */
	@Override
	public void saveAndUpdate(OrderDealRecordWithBLOBs data) {
		if (null != data.getDealRecordId()) {// 判断有没有传主键，如果传了为更新，否则为新增
			this.update(data);
		} else {
			this.insert(data);
		}
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.orderDealRecord.service.IOrderDealRecordService#update(com.yzx.flow.common.persistence.model.OrderDealRecord)
	 */
	@Override
	public void update(OrderDealRecord data) {
		orderDealRecordDao.updateByPrimaryKey(data);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.orderDealRecord.service.IOrderDealRecordService#delete(java.lang.Long)
	 */
	@Override
	public int delete(Long dealRecordId) {
		return orderDealRecordDao.deleteByPrimaryKey(dealRecordId);
	}
}