package com.yzx.flow.modular.order.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yzx.flow.common.exception.MyException;
import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.persistence.model.OrderDetail;
import com.yzx.flow.common.util.RedisHttpUtil;
import com.yzx.flow.common.util.URLConstants;
import com.yzx.flow.modular.order.service.IOrderDetailService;
import com.yzx.flow.modular.system.dao.OrderDetailDao;

/**
 * 
 * <b>Title：</b>OrderDetailService.java<br/>
 * <b>Description：</b> <br/>
 * <b>@author： </b>v5480<br/>
 * <b>@date：</b>2015-07-08 18:05:18<br/>
 * <b>Copyright (c) 2015 szwisdom Tech.</b>
 * 
 */
@Service("orderDetailService")
public class OrderDetailServiceImpl implements IOrderDetailService {
	@Autowired
	private OrderDetailDao orderDetailDao;

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.order.service.impl.IOrderDetailService#pageQuery(com.yzx.flow.common.page.Page)
	 */
	public Page<OrderDetail> pageQuery(Page<OrderDetail> page) {
		List<OrderDetail> list = orderDetailDao.pageQuery(page);
		page.setDatas(list);
		return page;
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.order.service.impl.IOrderDetailService#insert(com.yzx.flow.common.persistence.model.OrderDetail)
	 */
	public void insert(OrderDetail data) {
		orderDetailDao.insert(data);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.order.service.impl.IOrderDetailService#get(java.lang.Long)
	 */
	public OrderDetail get(Long orderDetailId) {
		return orderDetailDao.selectByPrimaryKey(orderDetailId);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.order.service.impl.IOrderDetailService#saveAndUpdate(com.yzx.flow.common.persistence.model.OrderDetail)
	 */
	public void saveAndUpdate(OrderDetail data) {
		if (null != data.getOrderDetailId()) {// 判断有没有传主键，如果传了为更新，否则为新增
			this.update(data);
		} else {
			this.insert(data);
		}
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.order.service.impl.IOrderDetailService#update(com.yzx.flow.common.persistence.model.OrderDetail)
	 */
	@Transactional(rollbackFor = Exception.class)
	public void update(OrderDetail data) {
		orderDetailDao.updateByPrimaryKey(data);
		String result = RedisHttpUtil.sendGet(URLConstants.DEL_REDIS_URL,
				URLConstants.T_ORDER_DETAIL, data.getOrderDetailId()+"");
		if (!"OK".equals(result)) {
			throw new MyException("删除Redis中信息出错,其请求URL参数为:" + URLConstants.T_ORDER_DETAIL+"\t"+data.getOrderDetailId());
		}
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.order.service.impl.IOrderDetailService#delete(java.lang.Long)
	 */
	@Transactional(rollbackFor = Exception.class)
	public int delete(Long orderDetailId) {
		int i = orderDetailDao.deleteByPrimaryKey(orderDetailId);
		String result = RedisHttpUtil.sendGet(URLConstants.DEL_REDIS_URL,
				URLConstants.T_ORDER_DETAIL, orderDetailId+"");
		if (!"OK".equals(result)) {
			throw new MyException("删除Redis中信息出错,其请求URL参数为:" + URLConstants.T_ORDER_DETAIL+"\t"+orderDetailId);
		}
		return i;
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.order.service.impl.IOrderDetailService#getOrderDetailProdByOrderId(java.lang.Long)
	 */
	public List<OrderDetail> getOrderDetailProdByOrderId(Long orderId) {
		return orderDetailDao.getOrderDetailProdByOrderId(orderId);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.order.service.impl.IOrderDetailService#getByOrderId(java.lang.Long)
	 */
	public List<OrderDetail> getByOrderId(Long orderId) {
		return orderDetailDao.getByOrderId(orderId);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.order.service.impl.IOrderDetailService#deleteByOrderId(java.lang.Long)
	 */
	@Transactional(rollbackFor = Exception.class)
	public int deleteByOrderId(Long orderId) {
		List<OrderDetail> orderDetaillist = null;
		orderDetaillist = orderDetailDao.getByOrderId(orderId);
		int i = orderDetailDao.deleteByOrderId(orderId);
		for (OrderDetail orderDetail : orderDetaillist) {
			String result = RedisHttpUtil.sendGet(URLConstants.DEL_REDIS_URL,
					URLConstants.T_ORDER_DETAIL,
					orderDetail.getOrderDetailId()+"");
			if (!"OK".equals(result)) {
				throw new MyException("删除Redis中信息出错,其请求URL参数为:" + URLConstants.T_ORDER_DETAIL+"\t"+orderDetail.getOrderDetailId());
			}
		}
		return i;
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.order.service.impl.IOrderDetailService#getByOrderTypeAndStatus(java.lang.Integer, java.lang.Integer)
	 */
	public List<OrderDetail> getByOrderTypeAndStatus(Integer orderType,
			Integer status) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("orderType", orderType);
		map.put("status", status);
		return orderDetailDao.getByOrderTypeAndStatus(map);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.order.service.impl.IOrderDetailService#getODAndFlowPlusProdByOrderId(java.lang.Long)
	 */
	public List<OrderDetail> getODAndFlowPlusProdByOrderId(Long orderId) {
		return orderDetailDao.getODAndFlowPlusProdByOrderId(orderId);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.order.service.impl.IOrderDetailService#getOrderDetailByOrderIdProductId(java.lang.Long, java.lang.Long)
	 */
	public OrderDetail getOrderDetailByOrderIdProductId(Long orderId,
			Long productId) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("orderId", orderId);
		map.put("productId", productId);
		return orderDetailDao.getOrderDetailByOrderIdProductId(map);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.order.service.impl.IOrderDetailService#getODAndFPIByOrderId(java.lang.Long)
	 */
	public List<OrderDetail> getODAndFPIByOrderId(Long orderId) {
		return orderDetailDao.getODAndFPIByOrderId(orderId);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.order.service.impl.IOrderDetailService#getOrderDetailByDetailId(java.lang.Long)
	 */
	public OrderDetail getOrderDetailByDetailId(Long detailId) {
		return orderDetailDao.getOrderDetailByDetailId(detailId);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.order.service.impl.IOrderDetailService#selectDistinctDirectChargeProdOrder(java.lang.Long)
	 */
	public List<OrderDetail> selectDistinctDirectChargeProdOrder(Long partnerId) {
		return orderDetailDao.selectDistinctDirectChargeProdOrder(partnerId);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.order.service.impl.IOrderDetailService#selectDirectChargeProdOrder(java.lang.Long, java.lang.Long)
	 */
	public List<OrderDetail> selectDirectChargeProdOrder(Long productId,
			Long partnerId) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("productId", productId);
		map.put("partnerId", partnerId);
		return orderDetailDao.selectDirectChargeProdOrder(map);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.order.service.impl.IOrderDetailService#getCountODByProductId(java.lang.Long)
	 */
	public int getCountODByProductId(Long productId) {
		return orderDetailDao.getCountODByProductId(productId);
	}
}