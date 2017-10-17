package com.yzx.flow.modular.orderDealRecord.service;

import java.util.List;

import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.persistence.model.OrderDealRecord;
import com.yzx.flow.common.persistence.model.OrderDealRecordWithBLOBs;

public interface IOrderDealRecordService {

	//	@DataSource(DataSourceType.READ)
	Page<OrderDealRecord> pageQuery(Page<OrderDealRecord> page);

	//	@DataSource(DataSourceType.READ)
	List<OrderDealRecordWithBLOBs> selectByInfo(OrderDealRecordWithBLOBs record);

	void insert(OrderDealRecordWithBLOBs data);

	//	@DataSource(DataSourceType.READ)
	OrderDealRecordWithBLOBs get(Long dealRecordId);

	void saveAndUpdate(OrderDealRecordWithBLOBs data);

	void update(OrderDealRecord data);

	int delete(Long dealRecordId);

}