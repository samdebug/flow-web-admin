package com.yzx.flow.modular.order.service;

import java.util.List;
import java.util.Map;

import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.persistence.model.CustomerInfo;
import com.yzx.flow.common.persistence.model.FlowProductInfo;
import com.yzx.flow.common.persistence.model.OrderDealRecordWithBLOBs;
import com.yzx.flow.common.persistence.model.OrderDetail;
import com.yzx.flow.common.persistence.model.OrderInfo;
import com.yzx.flow.common.persistence.model.PartnerProduct;
import com.yzx.flow.core.shiro.ShiroUser;

public interface IOrderInfoService {

	/**
	 * 应用信息接入状态 有效：1
	 */
	String FLOWAPPEFFECTIVESTATUS = "1";
	/**
	 * 应用信息接入 默认不需要支持短信 ：0
	 */
	Integer FLOWAPPNOTNEEDSMS = 0;
	/**
	 * 订单类型 2:流量+卡 1: 流量包
	 */
	Integer ORDERTYPEFLOWPLUS = 2;

	Page<OrderInfo> pageQuery(Page<OrderInfo> page);

	void insert(OrderInfo data);

	OrderInfo get(Long orderId);

	void saveAndUpdate(OrderInfo data);

	void update(OrderInfo data);

	int delete(Long orderId);

	/**
	 * 根据合作伙伴ID取出客户信息
	 * 
	 * @param partnerId
	 * @return
	 */
	List<CustomerInfo> selectByPartnerId(Long partnerId);

	/**
	 * 取出所有客户信息
	 * 
	 * @param partnerId
	 * @return
	 */
	List<CustomerInfo> selectCustomerInfoList();

	//	@DataSource(DataSourceType.READ)
	List<CustomerInfo> selectCustomerInfoByName(String customerName, Long partnerId);

	/**
	 * 取得订单总数
	 */
	long getOrderInfoCount();

	/**
	 * 提交订单
	 */
	void addOrUpdateOrderInfo(OrderInfo orderInfo, ShiroUser staff, Long updId);

	List<OrderDetail> getOrderDetailByOrderId(Long orderId);

	List<OrderDetail> getOrderDetailByOrderIdStatus(Long orderId);

	PartnerProduct getSettlementAmount(Long partnerId, Long productId);

	List<OrderInfo> getByPartnerIdAndCustomerId(Long partnerId, Long customerId);

	List<OrderInfo> getByCustomerIdAndOrderType(Long customerId, Integer orderType);

	OrderInfo getOrderInfoByOrderDetailId(Long detailId);

	List<OrderInfo> getByCustomerId(Long customerId);

	List<OrderInfo> queryOrderInfoByCustomer(Map<String, Object> map);

	List<OrderInfo> getByOrderType(OrderInfo orderInfo);

	/**
	 * 构造【订单处理记录】对象 客户订单
	 */
	OrderDealRecordWithBLOBs createOrderDealRecordWithBLOBs(OrderInfo orderInfo, ShiroUser staff);

	/**
	 * 批量插入订单明细
	 */
	void insertBatchOrderDetails(OrderInfo orderInfo);

	/**
	 * 构造订单明细
	 * 
	 * @param fpi
	 *            FlowProductInfo
	 */
	OrderDetail createOrderDetail(FlowProductInfo fpi, OrderInfo orderInfo, OrderDetail orderDetail);

	/**
	 * 新增APP应用接入信息
	 */
	void insertFlowAppInfo(OrderInfo orderInfo);

	/**
	 * 更新订单明细表
	 */
	void updateOrderDetails(OrderInfo orderInfo, ShiroUser staff);

	/**
	 * 获取表中原始数据的Map
	 * 
	 * @param originOrderDetails
	 *            表中的原始数据
	 * @return
	 */
	Map<String, OrderDetail> getOriginProductMap(List<OrderDetail> originOrderDetails);

	/**
	 * 获取页面传递过来数据的Map
	 * 
	 * @param orderInfo
	 *            当前订单
	 * @return
	 */
	Map<String, FlowProductInfo> getfinalProductMap(OrderInfo orderInfo);

	/**
	 * 删除订单详情
	 * 
	 * @param orderDetail
	 *            当前订单详情
	 */
	void deleteOrderDetails(OrderDetail orderDetail);

}