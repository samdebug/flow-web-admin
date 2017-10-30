package com.yzx.flow.modular.order.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import com.yzx.flow.common.constant.tips.ErrorTip;
import com.yzx.flow.common.exception.BizExceptionEnum;
import com.yzx.flow.common.exception.BussinessException;
import com.yzx.flow.common.exception.MyException;
import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.persistence.model.CustomerInfo;
import com.yzx.flow.common.persistence.model.FlowAppInfo;
import com.yzx.flow.common.persistence.model.FlowCardBatchInfo;
import com.yzx.flow.common.persistence.model.FlowCardInfo;
import com.yzx.flow.common.persistence.model.FlowProductInfo;
import com.yzx.flow.common.persistence.model.OrderDealRecordWithBLOBs;
import com.yzx.flow.common.persistence.model.OrderDetail;
import com.yzx.flow.common.persistence.model.OrderInfo;
import com.yzx.flow.common.persistence.model.PartnerInfo;
import com.yzx.flow.common.persistence.model.PartnerProduct;
import com.yzx.flow.common.util.Constant;
import com.yzx.flow.common.util.DateUtil;
import com.yzx.flow.common.util.RedisHttpUtil;
import com.yzx.flow.common.util.URLConstants;
import com.yzx.flow.core.aop.dbrouting.DataSource;
import com.yzx.flow.core.aop.dbrouting.DataSourceType;
import com.yzx.flow.core.shiro.ShiroKit;
import com.yzx.flow.core.shiro.ShiroUser;
import com.yzx.flow.modular.flow.service.IFlowProductInfoService;
import com.yzx.flow.modular.order.service.IOrderInfoService;
import com.yzx.flow.modular.partner.service.IPartnerService;
import com.yzx.flow.modular.system.dao.CustomerInfoDao;
import com.yzx.flow.modular.system.dao.FlowAppInfoDao;
import com.yzx.flow.modular.system.dao.FlowCardBatchInfoDao;
import com.yzx.flow.modular.system.dao.FlowCardInfoDao;
import com.yzx.flow.modular.system.dao.FlowProductExchangeRecDao;
import com.yzx.flow.modular.system.dao.FlowProductInfoDao;
import com.yzx.flow.modular.system.dao.OrderDealRecordDao;
import com.yzx.flow.modular.system.dao.OrderDetailDao;
import com.yzx.flow.modular.system.dao.OrderInfoDao;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.JSONUtils;

/**
 * 
 * <b>Title：</b>OrderInfoService.java<br/>
 * <b>Description：</b> <br/>
 * <b>@author： </b>v5480<br/>
 * <b>@date：</b>2015-07-08 18:05:18<br/>
 * <b>Copyright (c) 2015 szwisdom Tech.</b>
 * 
 */
@Service("orderInfoService")
public class OrderInfoServiceImpl implements IOrderInfoService {
	private static final Logger logger=LoggerFactory.getLogger(OrderInfoServiceImpl.class);
	@Autowired
	private OrderInfoDao orderInfoDao;

	@Autowired
	private CustomerInfoDao customerInfoDao;

	@Autowired
	private OrderDetailDao orderDetailDao;

	@Autowired
	private IPartnerService partnerService;

	@Autowired
	private FlowAppInfoDao flowAppInfoDao;

	@Autowired
	private FlowCardBatchInfoDao flowCardBatchInfoDao;

	@Autowired
	private FlowCardInfoDao flowCardInfoDao;

	@Autowired
	private FlowProductInfoDao flowProductInfoDao;
	
	@Autowired
	private IFlowProductInfoService flowProductInfoService;

	@Autowired
	private OrderDealRecordDao orderDealRecordDao;

	@Autowired
	private FlowProductExchangeRecDao flowProductExchangeRecDao;

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.order.service.IOrderInfoService#pageQuery(com.yzx.flow.common.page.Page)
	 */
	public Page<OrderInfo> pageQuery(Page<OrderInfo> page) {
		List<OrderInfo> list = orderInfoDao.pageQuery(page);
		page.setDatas(list);
		return page;
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.order.service.IOrderInfoService#insert(com.yzx.flow.common.persistence.model.OrderInfo)
	 */
	public void insert(OrderInfo data) {
		orderInfoDao.insert(data);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.order.service.IOrderInfoService#get(java.lang.Long)
	 */
	public OrderInfo get(Long orderId) {
		OrderInfo orderInfo = orderInfoDao.selectByPrimaryKey(orderId);
		List<OrderDetail> orderDetails = getOrderDetailByOrderId(orderId);
		orderInfo.setOrderDetailList(orderDetails);
		return orderInfo;
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.order.service.IOrderInfoService#saveAndUpdate(com.yzx.flow.common.persistence.model.OrderInfo)
	 */
	public void saveAndUpdate(OrderInfo data) {
		if (null != data.getOrderId()) {// 判断有没有传主键，如果传了为更新，否则为新增
			this.update(data);
		} else {
			this.insert(data);
		}
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.order.service.IOrderInfoService#update(com.yzx.flow.common.persistence.model.OrderInfo)
	 */
	@Transactional(rollbackFor = Exception.class)
	public void update(OrderInfo data) {
		orderInfoDao.updateByPrimaryKey(data);
		String result = RedisHttpUtil.sendGet(
				URLConstants.DEL_REDIS_URL, URLConstants.T_ORDER_INFO,
				data.getOrderId() + "");
		if (!"OK".equals(result)) {
			throw new MyException("删除Redis中信息出错,其请求URL参数为:"
					+ URLConstants.T_ORDER_INFO + "\t" + data.getOrderId());
		}
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.order.service.IOrderInfoService#delete(java.lang.Long)
	 */
	@Transactional(rollbackFor = Exception.class)
	public int delete(Long orderId) {
		List<Long> orderDetailIdList = new ArrayList<Long>();
		List<Long> batchIdList = new ArrayList<Long>();
		List<Long> cardIdList = new ArrayList<Long>();
		// 物理删除
		List<OrderDetail> orderDetails = orderDetailDao
				.getByOrderId(orderId);
		for (OrderDetail orderDetail : orderDetails) {
			orderDetailIdList.add(orderDetail.getOrderDetailId());
			// 流量加卡批次信息
			List<FlowCardBatchInfo> flowCardBatchInfos = flowCardBatchInfoDao
					.selectFCBIByOrderDetailId(orderDetail.getOrderDetailId());
			for (FlowCardBatchInfo flowCardBatchInfo : flowCardBatchInfos) {
				batchIdList.add(flowCardBatchInfo.getBatchId());
				// 构造流量加卡ID集合
				genCardIdsAndExchangeRecIds(flowCardBatchInfo, cardIdList);
			}
		}
		// 批量删除流量加产品兑换记录表
		if (!cardIdList.isEmpty()) {
			flowProductExchangeRecDao.deleteBatchByCardId(cardIdList);
		}
		// 批量删除流量加卡信息
		if (!batchIdList.isEmpty()) {
			flowCardInfoDao.deleteBatchByBatchId(batchIdList);
		}
		// 批量删除流量加卡批次表
		if (!orderDetailIdList.isEmpty()) {
			flowCardBatchInfoDao
					.deleteBatchByOrderDetailId(orderDetailIdList);
		}
		// 删除订单明细表
		orderDetailDao.deleteByOrderId(orderId);
		List<OrderDetail> orderDetaillist = null;
		orderDetaillist = orderDetailDao.getByOrderId(orderId);
		for (OrderDetail orderDetail : orderDetaillist) {
			String result = RedisHttpUtil.sendGet(
					URLConstants.DEL_REDIS_URL, URLConstants.T_ORDER_DETAIL,
					orderDetail.getOrderDetailId() + "");
			if (!"OK".equals(result)) {
				throw new MyException("删除Redis中信息出错,其请求URL参数为:"
						+ URLConstants.T_ORDER_DETAIL + "\t"
						+ orderDetail.getOrderDetailId());
			}
		}
		int i = orderInfoDao.deleteByPrimaryKey(orderId);
		String result = RedisHttpUtil.sendGet(
				URLConstants.DEL_REDIS_URL, URLConstants.T_ORDER_INFO,
				orderId + "");
		if (!"OK".equals(result)) {
			throw new MyException("删除Redis中信息出错,其请求URL参数为:"
					+ URLConstants.T_ORDER_INFO + "\t" + orderId);
		}
		return i;
	}

	/**
	 * 构造流量加卡ID集合和兑换ID集合
	 */
	private void genCardIdsAndExchangeRecIds(
			FlowCardBatchInfo flowCardBatchInfo, List<Long> cardIdList) {
		// 流量加卡信息
		List<FlowCardInfo> flowCardInfos = flowCardInfoDao
				.selectFlowCardInfoByBatchId(flowCardBatchInfo.getBatchId());
		for (FlowCardInfo flowCardInfo : flowCardInfos) {
			cardIdList.add(flowCardInfo.getCardId());
			// 流量加产品兑换记录信息
			// List<FlowProductExchangeRec> flowProductExchangeRecs =
			// flowProductExchangeRecDao.selectByCardId(flowCardInfo.getCardId());
			// for (FlowProductExchangeRec flowProductExchangeRec :
			// flowProductExchangeRecs) {
			// exchangeRecIdList.add(flowProductExchangeRec.getExchangeRecId());
			// }
		}
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.order.service.IOrderInfoService#selectByPartnerId(java.lang.Long)
	 */
	public List<CustomerInfo> selectByPartnerId(Long partnerId) {
		return customerInfoDao.selectByPartnerId(partnerId);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.order.service.IOrderInfoService#selectCustomerInfoList()
	 */
	public List<CustomerInfo> selectCustomerInfoList() {
		return customerInfoDao.selectCustomerInfoList();
	}

	@DataSource(DataSourceType.READ)
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.order.service.IOrderInfoService#selectCustomerInfoByName(java.lang.String, java.lang.Long)
	 */
	public List<CustomerInfo> selectCustomerInfoByName(String customerName,
			Long partnerId) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("customerName", customerName);
		map.put("partnerId", partnerId);
		return customerInfoDao.selectCustomerInfoByName(map);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.order.service.IOrderInfoService#getOrderInfoCount()
	 */
	public long getOrderInfoCount() {
		return orderInfoDao.getOrderInfoCount();
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.order.service.IOrderInfoService#addOrUpdateOrderInfo(com.yzx.flow.common.persistence.model.OrderInfo, com.yzx.flow.common.persistence.model.Staff, java.lang.Long)
	 */
	@Transactional(rollbackFor = Exception.class)
	public void addOrUpdateOrderInfo(OrderInfo orderInfo, ShiroUser staff, Long updId) {
		// 重新组合订单（计算产品总价格）
		regroupOrderInfo(orderInfo);
		// 订单处理记录-记录前数据
		String startRecord = "";
		if (updId == null) {
			if (orderInfo.getPriceTotal() == null) {
				orderInfo.setPriceTotal(new BigDecimal(0));
			}
			// 新增订单主表
			this.insert(orderInfo);
			// 批量新增订单明细表
			insertBatchOrderDetails(orderInfo);
			// 新增APP应用接入信息表
			insertFlowAppInfo(orderInfo);
		} else {
			// 构造订单处理记录-记录前数据
			JsonConfig jsonConfig = new JsonConfig();
			// TODO
//			jsonConfig.registerJsonValueProcessor(Date.class,
//					new JsonDateValueProcessor());
			startRecord = JSONObject.fromObject(get(orderInfo.getOrderId()),
					jsonConfig).toString();

			// 更新客户订单主表
			this.update(orderInfo);
			// 更新订单明细表
			updateOrderDetails(orderInfo,staff);
		}
		// 新增订单处理记录
		OrderInfo info = get(orderInfo.getOrderId());
		OrderDealRecordWithBLOBs record = createOrderDealRecordWithBLOBs(info, staff);
		record.setStartRecord(startRecord);
		orderDealRecordDao.insert(record);
	}

	/**
	 * 重新组合订单（计算产品总价格）
	 * 
	 * @param orderInfo
	 *            当前订单信息
	 */
	private void regroupOrderInfo(OrderInfo orderInfo) {
		BigDecimal priceTotal = new BigDecimal(0);
		for (FlowProductInfo fpi : orderInfo.getFlowProductInfoList()) {
			if (null == fpi.getProductId()) {
				continue;
			}
			fpi.setProductTotalPrice(new BigDecimal(fpi.getProductCount())
					.multiply(fpi.getSettlementAmount()));
			priceTotal = priceTotal.add(fpi.getProductTotalPrice());
		}
		orderInfo.setPriceTotal(priceTotal);
	}

	private String genAppkey() {
		return UUID.randomUUID().toString().replace("-", "");
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.order.service.IOrderInfoService#getOrderDetailByOrderId(java.lang.Long)
	 */
	public List<OrderDetail> getOrderDetailByOrderId(Long orderId) {
		return orderDetailDao.getOrderDetailByOrderId(orderId);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.order.service.IOrderInfoService#getOrderDetailByOrderIdStatus(java.lang.Long)
	 */
	public List<OrderDetail> getOrderDetailByOrderIdStatus(Long orderId) {
		return orderDetailDao.getOrderDetailByOrderIdStatus(orderId);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.order.service.IOrderInfoService#getSettlementAmount(java.lang.Long, java.lang.Long)
	 */
	public PartnerProduct getSettlementAmount(Long partnerId, Long productId) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("partnerId", partnerId);
		map.put("productId", productId);
		return orderDetailDao.getSettlementAmount(map);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.order.service.IOrderInfoService#getByPartnerIdAndCustomerId(java.lang.Long, java.lang.Long)
	 */
	public List<OrderInfo> getByPartnerIdAndCustomerId(Long partnerId,
			Long customerId) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("partnerId", partnerId);
		map.put("customerId", customerId);
		return orderInfoDao.getByPartnerIdAndCustomerId(map);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.order.service.IOrderInfoService#getByCustomerIdAndOrderType(java.lang.Long, java.lang.Integer)
	 */
	public List<OrderInfo> getByCustomerIdAndOrderType(Long customerId,
			Integer orderType) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("customerId", customerId);
		map.put("orderType", orderType);
		return orderInfoDao.getByCustomerIdAndOrderType(map);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.order.service.IOrderInfoService#getOrderInfoByOrderDetailId(java.lang.Long)
	 */
	public OrderInfo getOrderInfoByOrderDetailId(Long detailId) {
		return orderInfoDao.getOrderInfoByOrderDetailId(detailId);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.order.service.IOrderInfoService#getByCustomerId(java.lang.Long)
	 */
	public List<OrderInfo> getByCustomerId(Long customerId) {
		return orderInfoDao.getByCustomerId(customerId);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.order.service.IOrderInfoService#queryOrderInfoByCustomer(java.util.Map)
	 */
	public List<OrderInfo> queryOrderInfoByCustomer(Map<String, Object> map) {
		return orderInfoDao.queryOrderInfoByCustomer(map);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.order.service.IOrderInfoService#getByOrderType(com.yzx.flow.common.persistence.model.OrderInfo)
	 */
	public List<OrderInfo> getByOrderType(OrderInfo orderInfo) {
		return orderInfoDao.getByOrderType(orderInfo);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.order.service.IOrderInfoService#createOrderDealRecordWithBLOBs(com.yzx.flow.common.persistence.model.OrderInfo, com.yzx.flow.common.persistence.model.Staff)
	 */
	public OrderDealRecordWithBLOBs createOrderDealRecordWithBLOBs( OrderInfo orderInfo, ShiroUser staff) {
		OrderDealRecordWithBLOBs data = new OrderDealRecordWithBLOBs();
		data.setType(Constant.RECORD_TYPE_CUSTOMER);
		data.setCreator(staff.getAccount());
		data.setInputTime(new Date());
		data.setRemark("");
		data.setSourceId(String.valueOf(orderInfo.getOrderId()));
		JsonConfig jsonConfig = new JsonConfig();
		// TODO 
//		jsonConfig.registerJsonValueProcessor(Date.class,
//				new JsonDateValueProcessor());
		String endRecord = JSONObject.fromObject(orderInfo, jsonConfig).toString();
		data.setEndRecord(endRecord);
		return data;
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.order.service.IOrderInfoService#insertBatchOrderDetails(com.yzx.flow.common.persistence.model.OrderInfo)
	 */
	public void insertBatchOrderDetails(OrderInfo orderInfo) {
		List<OrderDetail> orderDetails = new ArrayList<OrderDetail>();
		for (FlowProductInfo fpi : orderInfo.getFlowProductInfoList()) {
			if (null == fpi.getProductId()) {
				continue;
			}
			// 构造订单明细对象
			OrderDetail od = createOrderDetail(fpi, orderInfo, null);
			//如果quarzTime为空，直接进行插入操作
			orderDetails.add(od);
		}
		if (!orderDetails.isEmpty()) {
			orderDetailDao.insertBatch(orderDetails);
		}
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.order.service.IOrderInfoService#createOrderDetail(com.yzx.flow.common.persistence.model.FlowProductInfo, com.yzx.flow.common.persistence.model.OrderInfo, com.yzx.flow.common.persistence.model.OrderDetail)
	 */
	public OrderDetail createOrderDetail(FlowProductInfo fpi,
			OrderInfo orderInfo, OrderDetail orderDetail) {
		OrderDetail od = new OrderDetail();
		od.setOrderId(orderInfo.getOrderId());
		od.setOrderType(orderInfo.getOrderType());
		od.setProductId(fpi.getProductId());
		od.setPackageId(fpi.getPackageId());
		// 如果定时生效时间为空，则立即修改结算价格和销售价格，否则结算价格和销售价格不修改
		if (null != orderDetail&&!StringUtils.isBlank(fpi.getQuarzTime())) {
			od.setPrice(orderDetail.getPrice());
			od.setSettlementPrice(orderDetail.getSettlementPrice());
		} else {
			od.setPrice(fpi.getSettlementAmount());
			od.setSettlementPrice(fpi.getSettlementPrice());
		}
		od.setQuarzTime(fpi.getQuarzTime());
		od.setAmount((float) fpi.getProductCount());
		od.setPriceTotal(fpi.getProductTotalPrice());
		od.setUsedAmount(0);
		if (null != orderDetail) {
			od.setOrderDetailId(orderDetail.getOrderDetailId());
			od.setUsedAmount(orderDetail.getUsedAmount());
		}
		return od;
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.order.service.IOrderInfoService#insertFlowAppInfo(com.yzx.flow.common.persistence.model.OrderInfo)
	 */
	public void insertFlowAppInfo(OrderInfo orderInfo) {
		// 渠道直充合作伙伴 或 流量+卡订单
		FlowAppInfo flowAppInfo = new FlowAppInfo();
		flowAppInfo.setCustomerId(orderInfo.getCustomerId());
		flowAppInfo.setOrderId(orderInfo.getOrderId());
		// flowAppInfo.setAppId(orderInfo.getOrderId().toString());
		flowAppInfo.setAppKey(genAppkey());
		flowAppInfo.setStartDate(new Date());
		// 结束日期：两年后
		flowAppInfo.setEndDate(DateUtil.getDateAfterYear(2));
		flowAppInfo.setStatus(FLOWAPPEFFECTIVESTATUS);
		// 构造APP应用名称
		CustomerInfo customerInfo = customerInfoDao
				.selectByPrimaryKey(orderInfo.getCustomerId());
		if (ORDERTYPEFLOWPLUS == orderInfo.getOrderType()) {
			flowAppInfo.setAppName(customerInfo.getAccount() + "流量+卡");
		} else {
			flowAppInfo.setAppName(customerInfo.getAccount() + "流量包APP");
		}
		// 以客户账号当做接入应用ID
		flowAppInfo.setAppId(customerInfo.getAccount());
		// 
//		String callbackUrl = SystemConfig.getInstance().getFlowAppCallbackUrl();
		String callbackUrl = "";
		if (null == callbackUrl) {
			throw new MyException("新增应用接入信息出错(CallbackUrl)，请检查配置文件！");
		} else {
			flowAppInfo.setCallbackUrl(callbackUrl);
		}
		// TODO
//		String ipAddress = SystemConfig.getInstance().getFlowAppIpAddress();
		String ipAddress = "";
		if (null == ipAddress) {
			throw new MyException("新增应用接入信息出错(IpAddress)，请检查配置文件！");
		} else {
			flowAppInfo.setIpAddress(ipAddress);
		}
		// 默认不需要短信
		flowAppInfo.setNeedSms(FlowAppInfo.SMS_UNNEED);
		flowAppInfo.setFailNeedSms(FlowAppInfo.ISRESEND_NO);
		flowAppInfo.setSmsContent("");
		flowAppInfo.setAllowPackages("");
		flowAppInfo.setDispatchChannel("");
		flowAppInfoDao.insert(flowAppInfo);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.order.service.IOrderInfoService#updateOrderDetails(com.yzx.flow.common.persistence.model.OrderInfo, com.yzx.flow.common.persistence.model.Staff)
	 */
	@Transactional(rollbackFor = Exception.class)
	public void updateOrderDetails(OrderInfo orderInfo,ShiroUser staff) {
		// 表中原始数据
		List<OrderDetail> originOrderDetails = orderDetailDao
				.getByOrderId(orderInfo.getOrderId());
		// 获取表中原始数据的Map
		Map<String, OrderDetail> originProductMap = getOriginProductMap(originOrderDetails);
		// 获取页面传递过来数据的Map
		Map<String, FlowProductInfo> finalProductMap = getfinalProductMap(orderInfo);

		List<OrderDetail> orderDetailsWithUpdate = new ArrayList<OrderDetail>();
		List<OrderDetail> orderDetailsWithInsert = new ArrayList<OrderDetail>();
		//有定时执行生效
		List<OrderDetail> orderDetailsQuarz=new ArrayList<OrderDetail>();
		
		// 1. 以【页面传递过来数据的Map】为基准：如果包含DB中的原始数据，则做更新操作；否则做删除操作
		for (OrderDetail orderDetail : originOrderDetails) {
			String key = orderDetail.getOrderId() + "_"
					+ orderDetail.getProductId();
			if (finalProductMap.keySet().contains(key)) {
				// 更新
				FlowProductInfo fpi = finalProductMap.get(key);
				OrderDetail od = createOrderDetail(fpi, orderInfo, orderDetail);
				orderDetailsWithUpdate.add(od);
				if(!StringUtils.isBlank(od.getQuarzTime())){
					OrderDetail quarz=new OrderDetail();
					quarz.setOrderDetailId(od.getOrderDetailId());
					quarz.setPrice(fpi.getSettlementAmount());
					quarz.setSettlementPrice(fpi.getSettlementPrice());
					quarz.setQuarzTime(od.getQuarzTime());
					orderDetailsQuarz.add(quarz);	
				}
			} else {
				// 删除
				deleteOrderDetails(orderDetail);
			}
		}
		// 批量更新
		if (null != orderDetailsWithUpdate && !orderDetailsWithUpdate.isEmpty()) {
			orderDetailDao.updateBatch(orderDetailsWithUpdate);
			//如果定时生效的list长度大于0，则执行插入到定时任务表中的操作
			//删除定时任务表中的数据
			orderDetailDao.deleteQuarzBatch(orderDetailsWithUpdate);
			if(orderDetailsQuarz.size()>0){
				logger.info("【新增定时修改订单价格】,更改人={},更改信息为",staff.getAccount(),JSONUtils.valueToString(orderDetailsQuarz));
				orderDetailDao.insertQuarzBatch(orderDetailsQuarz);
			}
		}
		if (!orderDetailsWithUpdate.isEmpty()) {
			for (OrderDetail orderDetail : orderDetailsWithUpdate) {
				String result = RedisHttpUtil.sendGet(
						URLConstants.DEL_REDIS_URL,
						URLConstants.T_ORDER_DETAIL,
						orderDetail.getOrderDetailId() + "");
				if (!"OK".equals(result)) {
					throw new MyException("删除Redis中信息出错,其请求URL参数为:"
							+ URLConstants.T_ORDER_DETAIL + "\t"
							+ orderDetail.getOrderDetailId());
				}
			}
		}

		// 2. 以【表中原始数据的Map】为基准：如果不包含页面传递过来数据，则做新增操作
		for (FlowProductInfo fpi : orderInfo.getFlowProductInfoList()) {
			if (null == fpi.getProductId()) {
				continue;
			}
			String key = orderInfo.getOrderId() + "_" + fpi.getProductId();
			if (!originProductMap.keySet().contains(key)) {
				// 新增
				OrderDetail od = createOrderDetail(fpi, orderInfo, null);
				orderDetailsWithInsert.add(od);
			}
		}
		// 批量新增
		if (!orderDetailsWithInsert.isEmpty()) {
			orderDetailDao.insertBatch(orderDetailsWithInsert);
		}
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.order.service.IOrderInfoService#getOriginProductMap(java.util.List)
	 */
	public Map<String, OrderDetail> getOriginProductMap(
			List<OrderDetail> originOrderDetails) {
		Map<String, OrderDetail> originProductMap = new HashMap<String, OrderDetail>();
		for (OrderDetail orderDetail : originOrderDetails) {
			originProductMap
					.put(orderDetail.getOrderId() + "_"
							+ orderDetail.getProductId(), orderDetail);
		}
		return originProductMap;
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.order.service.IOrderInfoService#getfinalProductMap(com.yzx.flow.common.persistence.model.OrderInfo)
	 */
	public Map<String, FlowProductInfo> getfinalProductMap(OrderInfo orderInfo) {
		Map<String, FlowProductInfo> finalProductMap = new HashMap<String, FlowProductInfo>();
		for (FlowProductInfo fpi : orderInfo.getFlowProductInfoList()) {
			if (null == fpi.getProductId()) {
				continue;
			}
			finalProductMap.put(
					orderInfo.getOrderId() + "_" + fpi.getProductId(), fpi);
		}
		return finalProductMap;
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.order.service.IOrderInfoService#deleteOrderDetails(com.yzx.flow.common.persistence.model.OrderDetail)
	 */
	@Transactional(rollbackFor = Exception.class)
	public void deleteOrderDetails(OrderDetail orderDetail) {
		List<FlowCardInfo> flowCardInfoList = flowCardInfoDao
				.selectExchangeByOrderDetailId(orderDetail.getOrderDetailId());
		// 已兑换过卡
		if (!flowCardInfoList.isEmpty()) {
			FlowProductInfo flowProductInfo = flowProductInfoDao
					.getFlowProductInfoByProductId(orderDetail.getProductId());
			throw new MyException("无法删除已兑换过卡的订单产品【"
					+ flowProductInfo.getProductName() + "】，请重新选择产品。");
		}
		// 未兑换过卡
		List<FlowCardBatchInfo> flowCardBatchInfos = flowCardBatchInfoDao
				.selectFCBIByOrderDetailId(orderDetail.getOrderDetailId());
		if (!flowCardBatchInfos.isEmpty()) {
			for (FlowCardBatchInfo info : flowCardBatchInfos) {
				flowCardInfoDao.deleteByBatchId(info.getBatchId());
			}
			flowCardBatchInfoDao.deleteByOrderDetailId(orderDetail
					.getOrderDetailId());
		}
		orderDetailDao.deleteByPrimaryKey(orderDetail.getOrderDetailId());
		String result = RedisHttpUtil.sendGet(
				URLConstants.DEL_REDIS_URL, URLConstants.T_ORDER_DETAIL,
				orderDetail.getOrderDetailId() + "");
		if (!"OK".equals(result)) {
			throw new MyException("删除Redis中信息出错,其请求URL参数为:"
					+ URLConstants.T_ORDER_DETAIL + "\t"
					+ orderDetail.getOrderDetailId());
		}
	}
	
	
	public void saveOrderAndOrderDetail(OrderInfo orderInfo, Long updId) {
	    
		ShiroUser staff = ShiroKit.getUser();
	    try {
	    	// 合法性校验及参数构造
		    authenticationChcekAndCreateParams(orderInfo, staff);
		    
		    // 【订单提交】基础校验和业务逻辑校验
	        paramCheck(orderInfo, updId);
	        
	        // 新增操作
	        if (updId == null) {
	            orderInfo.setStatus(Constant.ORDER_STATUS_EFFECTIVE);
	            orderInfo.setCreator(staff.getAccount());
	            orderInfo.setCreateTime(new Date());
	        } else {
	            orderInfo.setStatus(Constant.ORDER_STATUS_EFFECTIVE);// 更新直接改成生效 - 2017.10.19 需求如此
	            orderInfo.setUpdator(staff.getAccount());
	            orderInfo.setUpdateTime(new Date());
	        }
	        addOrUpdateOrderInfo(orderInfo, staff, updId);
	    } catch (BussinessException be) {
	    	throw be;
	    } catch (Exception e) {
	    	throw e;// 后续的异常日志处理
	    }
	}
	
	/**
	 *  合法性校验及参数构造
	 *  
	 * @param customerId 当前订单所属客户的ID
	 * @return
	 */
    private void authenticationChcekAndCreateParams(OrderInfo orderInfo, ShiroUser staff) {
        if (null == orderInfo.getCustomerId()) {
        	throw new BussinessException(BizExceptionEnum.CUSTOMER_FORMAT_ERROR, "请选择客户名称");
        }
        CustomerInfo customerInfo = customerInfoDao.getCustomerInfoByCustomerId(orderInfo.getCustomerId());
        
        // 客户及合作伙伴身份合法性校验
        authenticationChcek(customerInfo);
        // 参数构造
        createParams(customerInfo, orderInfo, staff);
    }
    
    /**
	 * 构造订单所属的【合作伙伴】参数
	 * 
	 * @param customerInfo 当前订单所属客户
	 * @return
	 */
	private void createParams(CustomerInfo customerInfo, OrderInfo orderInfo, ShiroUser staff) {
		// TODO 
//        if (isAdmin()) {
            orderInfo.setPartnerId(customerInfo.getPartnerId());
//        } else {
//            // 合作伙伴ID
//            PartnerInfo pInfo = partnerInfoService.getByAccount(staff.getLoginName());
//            if (pInfo == null) {
//                return super.fail("合作伙伴不存在");
//            }
//            orderInfo.setPartnerId(pInfo.getPartnerId());
//        }
//        return super.success("构造参数成功");
	}
    
    /**
     * 订单类型  2:流量+卡   1: 流量包
     */
    public static final Integer ORDERTYPEFLOWPACKAGE = 1;
    
    /**
     * 合作伙伴状态  1: 商用
     */
    public static final String PARTNERSTATUS = "1";
    
    /**
     * 客户状态  1: 商用
     */
    public static final Integer CUSTOMERSTATUS = 1;
	
	/**
	 * 客户及合作伙伴身份合法性校验
	 * 
	 * @param customerId 当前订单所属客户
	 */
	private void authenticationChcek(CustomerInfo customerInfo) {
        if (null == customerInfo) {
            throw new BussinessException(BizExceptionEnum.CUSTOMER_FORMAT_ERROR, "非法客户");
        }
        if (customerInfo.getStatus() != CUSTOMERSTATUS) {
        	throw new BussinessException(BizExceptionEnum.CUSTOMER_FORMAT_ERROR, "当前客户是非商用状态，不能提交订单。");
        }
        PartnerInfo pi = partnerService.get(customerInfo.getPartnerId());
        if (pi != null && !PARTNERSTATUS.equals(pi.getStatus())) {
        	throw new BussinessException(BizExceptionEnum.CUSTOMER_FORMAT_ERROR, "当前客户所属合作伙伴是非商用状态，不能提交订单。");
        }
	}
	
	/**
	 * 【订单提交】基础校验和业务逻辑校验
	 * 
	 * @param orderInfo 订单对象
	 * @return
	 */
    private void paramCheck(OrderInfo orderInfo, Long updId) {
        // 关联产品为空校验
        if (orderInfo.getFlowProductInfoList() == null) {
        	throw new BussinessException(BizExceptionEnum.CUSTOMER_FORMAT_ERROR, "请选择要订购的产品");
        }
        // 订单中产品的基础校验和业务逻辑校验
        productBaseAndBusinessCheck(orderInfo);
        // 最小日期校验
        if (ORDERTYPEFLOWPACKAGE != orderInfo.getOrderType() && orderInfo.getDeliveryTime() != null && orderInfo.getDeliveryTime().getTime() < new Date().getTime()) {
        	throw new BussinessException(BizExceptionEnum.CUSTOMER_FORMAT_ERROR, "不能选择今天之前的日期");
        }
        // 流量包订单不可重复提交校验
        packageOrderResubmitCheck(orderInfo, updId);
    }
    
    /**
     * 订单中产品的基础校验和业务逻辑校验
     * 
     * @param orderInfo 当前订单
     * @return
     */
    private void productBaseAndBusinessCheck(OrderInfo orderInfo) {
        for (FlowProductInfo fpi : orderInfo.getFlowProductInfoList()) {
            if (null == fpi.getProductId()) {
                continue;
            }
            if (fpi.getSettlementAmount() == null) {
            	throw new BussinessException(BizExceptionEnum.CUSTOMER_FORMAT_ERROR, "请输入结算价格");
            }
            if (!fpi.getSettlementAmount().toString().matches("^[0-9]+(\\.[0-9]*)?$")) {
            	throw new BussinessException(BizExceptionEnum.CUSTOMER_FORMAT_ERROR, "请输入合法的价格");
            }
            if (!String.valueOf(fpi.getProductCount()).matches("^[0-9]+(\\.[0-9]*)?$")) {
            	throw new BussinessException(BizExceptionEnum.CUSTOMER_FORMAT_ERROR, "请输入合法的数量");
            }
            //流量加做判断
            if (2 == orderInfo.getOrderType() && !String.valueOf(fpi.getProductCount()).matches("^[1-9]\\d*$")) {
            	throw new BussinessException(BizExceptionEnum.CUSTOMER_FORMAT_ERROR, "订购数量必须为大于0的整数，请重新输入。");
            }
            // 校验价格是否倒挂
            // 客户订单优化处理-客户订单产品价格不能低于合作伙伴订单价格
            if ( orderInfo.getPartnerId() != null && orderInfo.getPartnerId() > 0L ) {
            	List<FlowProductInfo> listAll = flowProductInfoService.getByPartnerInfoType(orderInfo.getPartnerId(), null, null,null,null);
                for (FlowProductInfo fpiDB : listAll) {
                    if (fpi.getProductId().longValue() == fpiDB.getProductId().longValue() && fpi.getSettlementAmount().compareTo(fpiDB.getSettlementAmount()) == -1) {
                    	throw new BussinessException(BizExceptionEnum.CUSTOMER_FORMAT_ERROR, "产品名称为【" + fpi.getProductName() + "】的结算价格(" + fpi.getSettlementAmount() + "元)小于当前合作伙伴的结算价格("+ fpiDB.getSettlementAmount() +"元), 请重新输入。");
                    }
                }
            }
        }
    }
    
    /**
     * 流量包订单不可重复提交校验
     * 
     * @param orderInfo 当前订单信息
     * @param updId 更新操作标识符
     * @return
     */
    private void packageOrderResubmitCheck(OrderInfo orderInfo, Long updId) {
        // 订单新增操作
        if (updId == null) {
        	CustomerInfo customer = customerInfoDao.getCustomerInfoByCustomerId(orderInfo.getCustomerId());
        	if ( customer == null )
        		throw new BussinessException(BizExceptionEnum.CUSTOMER_FORMAT_ERROR, "暂无客户信息");
        	
            // 渠道直充合作伙伴
            if (customer.getPartnerType() == PartnerInfo.PARTNER_TYPE_CHANNEL) {
                List<OrderInfo> infos = getByPartnerIdAndCustomerId(orderInfo.getPartnerId() != null ? orderInfo.getPartnerId() : null, orderInfo.getCustomerId());
                if (!infos.isEmpty()) {
                    throw new BussinessException(BizExceptionEnum.CUSTOMER_FORMAT_ERROR, "该客户已有订单，不可重复提交");
                }
            }
            if (ORDERTYPEFLOWPACKAGE == orderInfo.getOrderType()) {
                List<OrderInfo> orderInfos = getByCustomerIdAndOrderType(orderInfo.getCustomerId(), ORDERTYPEFLOWPACKAGE);
                if (!orderInfos.isEmpty()) {
                	throw new BussinessException(BizExceptionEnum.CUSTOMER_FORMAT_ERROR, "该客户已有流量包订单，不可重复提交");
                }
            }
        }
    }

}
