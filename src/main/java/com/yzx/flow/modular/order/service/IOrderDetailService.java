package com.yzx.flow.modular.order.service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.persistence.model.OrderDetail;

public interface IOrderDetailService {

	Page<OrderDetail> pageQuery(Page<OrderDetail> page);

	void insert(OrderDetail data);

	OrderDetail get(Long orderDetailId);

	void saveAndUpdate(OrderDetail data);

	void update(OrderDetail data);

	int delete(Long orderDetailId);

	List<OrderDetail> getOrderDetailProdByOrderId(Long orderId);

	List<OrderDetail> getByOrderId(Long orderId);

	int deleteByOrderId(Long orderId);

	List<OrderDetail> getByOrderTypeAndStatus(Integer orderType, Integer status);

	List<OrderDetail> getODAndFlowPlusProdByOrderId(Long orderId);

	OrderDetail getOrderDetailByOrderIdProductId(Long orderId, Long productId);

	List<OrderDetail> getODAndFPIByOrderId(Long orderId);

	OrderDetail getOrderDetailByDetailId(Long detailId);

	List<OrderDetail> selectDistinctDirectChargeProdOrder(Long partnerId);

	List<OrderDetail> selectDirectChargeProdOrder(Long productId, Long partnerId);

	int getCountODByProductId(Long productId);

}