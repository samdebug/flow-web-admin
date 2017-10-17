package com.yzx.flow.modular.flowOrder.Service.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yzx.flow.common.exception.BizExceptionEnum;
import com.yzx.flow.common.exception.BussinessException;
import com.yzx.flow.common.exception.MyException;
import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.persistence.model.CExchangeurlInfo;
import com.yzx.flow.common.persistence.model.CustomerBalanceDay;
import com.yzx.flow.common.persistence.model.CustomerInfo;
import com.yzx.flow.common.persistence.model.CustomerTradeFlow;
import com.yzx.flow.common.persistence.model.FlowCardBatchInfo;
import com.yzx.flow.common.persistence.model.FlowCardInfo;
import com.yzx.flow.common.persistence.model.FlowCardSettlement;
import com.yzx.flow.common.persistence.model.FlowPackageInfo;
import com.yzx.flow.common.persistence.model.FlowPlusProduct;
import com.yzx.flow.common.persistence.model.OrderDetail;
import com.yzx.flow.common.persistence.model.OrderInfo;
import com.yzx.flow.common.persistence.model.PartnerInfo;
import com.yzx.flow.common.persistence.model.PartnerTradeFlow;
import com.yzx.flow.common.persistence.model.RecordFlowCardInfo;
import com.yzx.flow.common.persistence.model.Staff;
import com.yzx.flow.common.util.Constant;
import com.yzx.flow.common.util.DateUtil;
import com.yzx.flow.common.util.RedisHttpUtil;
import com.yzx.flow.common.util.URLConstants;
import com.yzx.flow.core.shiro.ShiroKit;
import com.yzx.flow.core.shiro.ShiroUser;
import com.yzx.flow.modular.customer.service.ICustomerInfoService;
import com.yzx.flow.modular.flowOrder.Service.ICExchangeurlInfoService;
import com.yzx.flow.modular.flowOrder.Service.IFlowCardBatchInfoService;
import com.yzx.flow.modular.flowOrder.Service.IFlowCardInfoService;
import com.yzx.flow.modular.flowOrder.Service.IFlowPackageInfoService;
import com.yzx.flow.modular.flowOrder.card.CardManager;
import com.yzx.flow.modular.order.service.IOrderDetailService;
import com.yzx.flow.modular.system.dao.CustomerBalanceDayDao;
import com.yzx.flow.modular.system.dao.CustomerTradeFlowDao;
import com.yzx.flow.modular.system.dao.FlowCardInfoDao;
import com.yzx.flow.modular.system.dao.FlowCardSettlementDao;
import com.yzx.flow.modular.system.dao.OrderInfoDao;
import com.yzx.flow.modular.system.dao.PartnerInfoDao;
import com.yzx.flow.modular.system.dao.PartnerTradeFlowDao;

/**
 * 
 * <b>Title：</b>FlowCardInfoService.java<br/>
 * <b>Description：</b> <br/>
 * <b>@author： </b>v5480<br/>
 * <b>@date：</b>2015-07-08 18:05:18<br/>
 * <b>Copyright (c) 2015 szwisdom Tech.</b>
 * 
 */
@Service("flowCardInfoService")
public class FlowCardInfoService implements IFlowCardInfoService {
	private static final Logger LOG = LoggerFactory.getLogger(FlowCardInfoService.class);
	@Autowired
	private FlowCardInfoDao flowCardInfoDao;

	@Autowired
	private IOrderDetailService orderDetailService;

	@Autowired
	private IFlowPackageInfoService flowPackageInfoService;

	@Autowired
	private ICustomerInfoService customerInfoService;
	
//	@Autowired
//	private IAttachmentInterface attachmentInterface;
	
	@Autowired
	private ICExchangeurlInfoService cExchangeurlInfoService;

	@Autowired
	private IFlowCardBatchInfoService flowCardBatchInfoService;
	
	@Autowired
	private FlowCardSettlementDao flowCardSettlementDao;
	
	@Autowired
	private CustomerTradeFlowDao customerTradeFlowDao;
	
	@Autowired
	private PartnerInfoDao partnerInfoDao;
	
	@Autowired
	private PartnerTradeFlowDao partnerTradeFlowDao;
	
	@Autowired
	private OrderInfoDao orderInfoDao;
	
	@Autowired
	private CustomerBalanceDayDao customerBalanceDayDao;
	
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.flowOrder.Service.impl.IFlowCardInfoService#pageQuery(com.yzx.flow.common.page.Page)
	 */
	public Page<FlowCardInfo> pageQuery(Page<FlowCardInfo> page) {
		page.getParams().put("nowDate", new Date());
		List<FlowCardInfo> list = flowCardInfoDao.pageQuery(page);
		for (int i = 0; i < list.size(); i++) {
			FlowCardInfo flowCardInfo = list.get(i);
			if (Constant.CARD_STATUS_EXCHANGED != flowCardInfo.getCardState() && flowCardInfo.getCardExp().getTime() < (new Date().getTime())) {
				list.get(i).setCardState(Constant.CARD_STATUS_EXPIRE);
			}
		}
		page.setDatas(list);
		return page;
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.flowOrder.Service.impl.IFlowCardInfoService#insert(com.yzx.flow.common.persistence.model.FlowCardInfo)
	 */
	public void insert(FlowCardInfo data) {
		flowCardInfoDao.insert(data);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.flowOrder.Service.impl.IFlowCardInfoService#get(java.lang.Long)
	 */
	public FlowCardInfo get(Long cardId) {
		return flowCardInfoDao.selectByPrimaryKey(cardId);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.flowOrder.Service.impl.IFlowCardInfoService#saveAndUpdate(com.yzx.flow.common.persistence.model.FlowCardInfo)
	 */
	public void saveAndUpdate(FlowCardInfo data) {
		if (null != data.getCardId()) {// 判断有没有传主键，如果传了为更新，否则为新增
			this.update(data);
		} else {
			this.insert(data);
		}
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.flowOrder.Service.impl.IFlowCardInfoService#update(com.yzx.flow.common.persistence.model.FlowCardInfo)
	 */
	public void update(FlowCardInfo data) {
		flowCardInfoDao.updateByPrimaryKey(data);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.flowOrder.Service.impl.IFlowCardInfoService#delete(java.lang.Long)
	 */
	public int delete(Long cardId) {
		return flowCardInfoDao.deleteByPrimaryKey(cardId);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.flowOrder.Service.impl.IFlowCardInfoService#selectMaxCardNo(java.lang.String)
	 */
	public String selectMaxCardNo(String prefix) {
		return flowCardInfoDao.selectMaxCardNo(prefix);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.flowOrder.Service.impl.IFlowCardInfoService#createCard(java.util.Map, java.lang.Long, java.lang.Long, com.yzx.flow.common.persistence.model.FlowCardBatchInfo, com.yzx.flow.common.persistence.model.Staff)
	 */
	@Transactional
	public Long createCard(Map<String, Integer> countMap, Long customId, Long orderId,
			FlowCardBatchInfo flowCardBatchInfo, Staff staff) {
		List<OrderDetail> orderDetailList = orderDetailService.getOrderDetailProdByOrderId(orderId);
		for (int i = 0; i < orderDetailList.size(); i++) {
			OrderDetail orderDetail = orderDetailList.get(i);
			if (orderDetail == null) {
				continue;
			}
			if (orderDetail.getFlowProductInfo() == null) {
				continue;
			}
			if (orderDetail.getFlowProductInfo().getFlowPlusProduct() == null) {
				continue;
			}
			FlowPlusProduct flowPlusProduct = orderDetail.getFlowProductInfo().getFlowPlusProduct();
			if (orderDetail.getFlowProductInfo().getPackageId() == null) {
				continue;
			}
			int count = orderDetail.getAmount().intValue() - orderDetail.getUsedAmount();
			Integer makeCount = countMap.get(orderDetail.getOrderDetailId() + "");
			if (makeCount == null || makeCount == 0 || makeCount > count) {
				LOG.debug("订单[{}]制作失败，数量异常制作数量为[{}],已制作数量为[{}],总订单数量为[{}]", orderDetail.getOrderDetailId(), makeCount,
						count, orderDetail.getAmount());
				continue;
			}
			flowCardBatchInfo.setBatchId(null);
			flowCardBatchInfo.setCreateUser(staff.getLoginName());
			flowCardBatchInfo.setCreateTime(new Date());
			flowCardBatchInfo.setCardCount(makeCount);
			flowCardBatchInfo.setCustomId(customId);
			flowCardBatchInfo.setCardState(1);
			flowCardBatchInfo.setBorderId("");
			flowCardBatchInfo.setIp("");
			flowCardBatchInfo.setExtOrder("");
			flowCardBatchInfo.setPublishUser("");
			flowCardBatchInfo.setCardType(flowPlusProduct.getCardType());
			flowCardBatchInfo.setOrderDetailId(orderDetail.getOrderDetailId());
			flowCardBatchInfoService.insert(flowCardBatchInfo);
			FlowPackageInfo flowPackageInfo = orderDetail.getFlowProductInfo().getFlowPackageInfo();
			String cardSupplier = "";
			Map<String, Boolean> map = new HashMap<String, Boolean>();
			if (StringUtils.isNotBlank(flowPackageInfo.getComboPackageStr())
					&& flowPackageInfo.getIsCombo().equals("1")) {
				List<FlowPackageInfo> flowPackageInfoList = flowPackageInfoService
						.selectInPackageId(flowPackageInfo.getComboPackageStr());
				for (int j = 0; j < flowPackageInfoList.size(); j++) {
					map.put(flowPackageInfoList.get(j).getOperatorCode(), true);
				}
			} else {
				map.put(flowPackageInfo.getOperatorCode(), true);
			}
			for (String key : map.keySet()) {
				cardSupplier += key + ",";
			}
			if (StringUtils.isBlank(cardSupplier)) {
				LOG.debug("数据异常,支持运营商数量为空");
				throw new MyException("数据异常,支持运营商数量为空");
			}

			cardSupplier = cardSupplier.substring(0, cardSupplier.length() - 1);
			String prefix = "";
			switch (flowPlusProduct.getCardType()) {
			case "A":
				prefix = "A";
				break;
			case "B":
				prefix = "B";
				break;
			case "C":
				prefix = "C";
				break;
			default:
				try {
					throw new Exception("数据库数据异常");
				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
				}
				return null;
			}
			prefix = getPrefix(flowPlusProduct.getChannelType(), prefix);
			String maxCardNo = selectMaxCardNo(prefix);
			if (maxCardNo == null) {
				maxCardNo = prefix + CardManager.WEISHU.get(0);
			}
			getCardId(maxCardNo, makeCount, prefix, flowCardBatchInfo, flowPlusProduct.getCardType(),
					flowCardBatchInfo.getValidTime(), customId, staff.getLoginName(), cardSupplier,
					flowPackageInfo.getFlowAmount());
			orderDetail.setUsedAmount(makeCount + orderDetail.getUsedAmount());
			orderDetailService.update(orderDetail);
		}
		return flowCardBatchInfo.getBatchId();
	}


	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.flowOrder.Service.impl.IFlowCardInfoService#createCard(int, com.yzx.flow.common.persistence.model.OrderInfo, java.lang.Long, com.yzx.flow.common.persistence.model.FlowCardBatchInfo, com.yzx.flow.common.persistence.model.Staff)
	 */
	public Long createCard(int count, OrderInfo orderinfo, Long productId, FlowCardBatchInfo flowCardBatchInfo,
			Staff staff) {
		OrderDetail orderDetail = orderDetailService.getOrderDetailByOrderIdProductId(orderinfo.getOrderId(),
				productId);
		if (orderDetail == null) {
			return null;
		}
		if (orderDetail.getFlowProductInfo() == null) {
			return null;
		}
		if (orderDetail.getFlowProductInfo().getFlowPlusProduct() == null) {
			return null;
		}
		FlowPlusProduct flowPlusProduct = orderDetail.getFlowProductInfo().getFlowPlusProduct();
		if (orderDetail.getFlowProductInfo().getPackageId() == null) {
			return null;
		}
		flowCardBatchInfo.setBatchId(null);
		flowCardBatchInfo.setCreateUser(staff.getLoginName());
		flowCardBatchInfo.setCreateTime(new Date());
		flowCardBatchInfo.setCardCount(count);
		flowCardBatchInfo.setCustomId(orderinfo.getCustomerId());
		flowCardBatchInfo.setCardState(2);
		flowCardBatchInfo.setPublishUser("");
		flowCardBatchInfo.setCardType(flowPlusProduct.getCardType());
		flowCardBatchInfo.setOrderDetailId(orderDetail.getOrderDetailId());
		flowCardBatchInfoService.insert(flowCardBatchInfo);
		FlowPackageInfo flowPackageInfo = orderDetail.getFlowProductInfo().getFlowPackageInfo();
		String cardSupplier = "";
		Map<String, Boolean> map = new HashMap<String, Boolean>();
		if (StringUtils.isNotBlank(flowPackageInfo.getComboPackageStr()) && flowPackageInfo.getIsCombo().equals("1")) {
			List<FlowPackageInfo> flowPackageInfoList = flowPackageInfoService
					.selectInPackageId(flowPackageInfo.getComboPackageStr());
			for (int j = 0; j < flowPackageInfoList.size(); j++) {
				map.put(flowPackageInfoList.get(j).getOperatorCode(), true);
			}
		} else {
			map.put(flowPackageInfo.getOperatorCode(), true);
		}
		for (String key : map.keySet()) {
			cardSupplier += key + ",";
		}
		if (StringUtils.isBlank(cardSupplier)) {
			LOG.debug("数据异常,支持运营商数量为空");
			throw new MyException("数据异常,支持运营商数量为空");
		}

		cardSupplier = cardSupplier.substring(0, cardSupplier.length() - 1);
		String prefix = "";
		switch (flowPlusProduct.getCardType()) {
		case "A":
			prefix = "A";
			break;
		case "B":
			prefix = "B";
			break;
		case "C":
			prefix = "C";
			break;
		default:
			try {
				throw new Exception("数据库数据异常");
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
			}
			return null;
		}
		prefix = getPrefix(flowPlusProduct.getChannelType(), prefix);
		String maxCardNo = selectMaxCardNo(prefix);
		if (maxCardNo == null) {
			maxCardNo = prefix + CardManager.WEISHU.get(0);
		}
		getCardId(maxCardNo, count, prefix, flowCardBatchInfo, flowPlusProduct.getCardType(),
				flowCardBatchInfo.getValidTime(), orderinfo.getCustomerId(), staff.getLoginName(), cardSupplier,
				flowPackageInfo.getFlowAmount());
		orderDetail.setUsedAmount(count + orderDetail.getUsedAmount());
		orderDetailService.update(orderDetail);
		return flowCardBatchInfo.getBatchId();
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.flowOrder.Service.impl.IFlowCardInfoService#updateCustomerInfoActiveCard(java.lang.Long)
	 */
	@Transactional
	public boolean updateCustomerInfoActiveCard(Long batchId) throws BussinessException {
		if (batchId == null) {
			return false;
		}
		//激活结算
		try {
			flowCardInfoActivatedBalance(batchId);
		} catch (BussinessException e) {
			throw e;
		}
		FlowCardBatchInfo flowCardBatchInfo = flowCardBatchInfoService.get(batchId);
		if(flowCardBatchInfo.getValidTime().getTime()<new Date().getTime()){
			return false;
		}
		FlowCardInfo flowCardInfo = new FlowCardInfo();
		flowCardInfo.setBatchId(flowCardBatchInfo.getBatchId());
		flowCardInfo.setCardState(2);
		flowCardBatchInfo.setCardState(2);
		flowCardInfo.setCardExp(new Date());
		flowCardBatchInfoService.update(flowCardBatchInfo);
		updateCardState(flowCardInfo);
		
		return true;
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.flowOrder.Service.impl.IFlowCardInfoService#inserAll(java.util.List)
	 */
	public int inserAll(List<FlowCardInfo> flowCardInfoList) {
		return flowCardInfoDao.insertAll(flowCardInfoList);
	}

	@Transactional
	private List<FlowCardInfo> getCardId(String cardNo, int count, String prefix, FlowCardBatchInfo flowCardBatchInfo,
			String cardType, Date cardExp, Long customId, String actionUser, String cardSupplier, Float cardFlow) {
		String numberStr = cardNo.substring(cardNo.length() - CardManager.CARD_NUM_LENGTH, cardNo.length());
		List<FlowCardInfo> allList = new ArrayList<FlowCardInfo>();
		int number = 0;
		try {
			number = Integer.valueOf(numberStr);
		} catch (Exception e) {
		}
		List<FlowCardInfo> list = new ArrayList<FlowCardInfo>();
		int inserCount = 0;
		Random random = ThreadLocalRandom.current();
		Map<String, Boolean> map = new HashMap<String, Boolean>();
		for (int i = number + 1; i < count + number + 1; i++) {
			String bt = "";
			bt += CardManager.WEISHU.get((i + "").length());
			bt = prefix + bt + i;
			FlowCardInfo flowCardInfo = new FlowCardInfo();
			flowCardInfo.setCardNo(bt);
			flowCardInfo.setCardPass(getPassWd(random, map, 0));
			flowCardInfo.setBatchId(flowCardBatchInfo.getBatchId());
			flowCardInfo.setCardType(cardType);
			flowCardInfo.setCardState(flowCardBatchInfo.getCardState());
			flowCardInfo.setCardExp(cardExp);
			flowCardInfo.setCardFlow(cardFlow);
			flowCardInfo.setCustomId(customId);
			flowCardInfo.setCardSupplier(cardSupplier);
			flowCardInfo.setCreateUser(actionUser);
			flowCardInfo.setCreateTime(new Date());
			flowCardInfo.setPublishUser("");
			flowCardInfo.setCause("");
			list.add(flowCardInfo);
			inserCount++;
			if (inserCount > 100) {
				inserCount = 0;
				inserAll(list);
				allList.addAll(list);
				list.clear();
			}
		}
		if (list.size() > 0) {
			inserAll(list);
			allList.addAll(list);
			list.clear();
		}
		return allList;
	}

	private String getPassWd(Random randomObj, Map<String, Boolean> map, int count) {
		count++;
		long time = new Date().getTime();
		int random = randomObj.nextInt(9000) + 1000;
		String timeStr = "" + time;
		String randomStr = "" + random;
		String str1 = timeStr.substring(0, 3);
		String str2 = timeStr.substring(3, 5);
		String str3 = timeStr.substring(5, 7);
		String str4 = timeStr.substring(7, timeStr.length());
		String result = str1 + randomStr.charAt(0) + str2 + randomStr.charAt(1) + str3 + randomStr.charAt(2) + str4
				+ randomStr.charAt(3);
		if (map.containsKey(result)) {
			if (count > 100) {
				throw new MyException("重复值太多");
			}
			return getPassWd(randomObj, map, count);
		}
		map.put(result, true);
		return result;
	}

	private String getPrefix(String value, String prefix) {
		if (value.equals("1")) {
			return "" + prefix;
		} else if (value.equals("2")) {
			return "D" + prefix;
		} else {
			try {
				throw new Exception("数据库数据异常");
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
			}
		}
		return "";
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.flowOrder.Service.impl.IFlowCardInfoService#updateCardState(com.yzx.flow.common.persistence.model.FlowCardInfo)
	 */
	public void updateCardState(FlowCardInfo flowCardInfo) {
		flowCardInfoDao.updateCardState(flowCardInfo);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.flowOrder.Service.impl.IFlowCardInfoService#updateCauseAll(java.lang.Long, java.lang.String)
	 */
	@Transactional
	public boolean updateCauseAll(Long batchId, String cause) throws BussinessException {
		FlowCardSettlement flowCardSettlement = new FlowCardSettlement(); 
		ShiroUser staff = getCurrentLogin();
		if(staff == null){
			LOG.error("激活 操作 未获取到对应的操作用户信息");
			return false;
		}
		String logName = staff.getAccount();
		
		FlowCardBatchInfo flowCardBatchInfo  =flowCardBatchInfoService.get(batchId);
		if(flowCardBatchInfo != null && flowCardBatchInfo.getCardState() == 2){
			try {
				flowCardSettlement = getFlowCardSettlementByCancel(batchId);
				
				//客户余额回滚
				if(flowCardSettlement.getCustomerPrice() == null){
					LOG.error("流量+ 作废 ：客户统计余额为null");
					return false;
				}
				//判断订单的类型为何种状态（计费方式 1:按激活计费 2:按下发计费）
				OrderInfo orderInfo = orderInfoDao.selectByPrimaryKey(flowCardSettlement.getOrderId());
				if(orderInfo != null && orderInfo.getBillingType() == 1){
					//生成日结算账单 （余额 为负）
					insertCustomerBalanceDay(flowCardSettlement, flowCardSettlement.getCustomerId(), flowCardSettlement.getPartnerId(), logName, false);
					//客户扣减	+ 日志流水记录
					customerSettlement(flowCardSettlement,logName,false);
					//合作伙伴扣减 + 日志流水记录
					partnerSettlement(flowCardSettlement,logName,false);
				}
			} catch (BussinessException e) {
				throw e;
			}catch (Exception e) {
				LOG.error("客户余额回滚失败，作废失败",e);
				return false;
			}
		}
		if(flowCardBatchInfo != null){
			//验证是否过期
			if(flowCardBatchInfo.getValidTime() != null && flowCardBatchInfo.getValidTime().getTime() < new Date().getTime()){
				LOG.error("当前批次对象已失效");
				return false;
			}
		}else{
			LOG.error("当前批次对象为null,作废失败");
			return false;
		}
		
		flowCardBatchInfo.setCardState(3);
		FlowCardInfo flowCardInfo = new FlowCardInfo();
		flowCardInfo.setBatchId(batchId);
		flowCardInfo.setCause(cause);
		flowCardInfo.setCardState(6);
		flowCardInfo.setCardExp(new Date());
		flowCardBatchInfoService.update(flowCardBatchInfo);
		//判断是否成功作废
		return flowCardInfoDao.updateCauseAll(flowCardInfo);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.flowOrder.Service.impl.IFlowCardInfoService#updateCause(java.lang.String[], java.lang.String)
	 */
	@Transactional
	public boolean updateCause(String[] ids, String cause) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("cause", cause);
		map.put("array", ids);
		map.put("cardState", 6);
		map.put("cardExp",new Date());
		try {
			ShiroUser staff = getCurrentLogin();
			if(staff == null){
				LOG.error("激活 操作 未获取到对应的操作用户信息");
				return false;
			}
			String logName = staff.getAccount();
			cancelFlowCard(ids,logName);
		} catch (Exception e) {
			LOG.error("流量加卡 作废  余额回滚异常："+e.getMessage(),e);
			return false;
		}
		return flowCardInfoDao.updateCause(map);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.flowOrder.Service.impl.IFlowCardInfoService#updateCustomerInfo(com.yzx.flow.common.persistence.model.CustomerInfo, com.yzx.flow.common.persistence.model.CExchangeurlInfo, com.yzx.flow.common.persistence.model.Staff, boolean, boolean)
	 */
	@Transactional
	public boolean updateCustomerInfo(CustomerInfo data, CExchangeurlInfo cExchangeurlInfo, Staff staff, boolean bFlag,
			boolean cFlag) {
//		if (data == null) {
//			return false;
//		}
//		if (data.getCustomerId() == null) {
//			return false;
//		}
//
//		CustomerInfo customerInfo = customerInfoService.get(data.getCustomerId());
//
//		if (bFlag) {
//			customerInfo.setUpdateTime(new Date());
//			customerInfo.setUpdator(staff.getLoginName());
//			customerInfo.setWechatNo(data.getWechatNo());
//			attachmentInterface.formalAttachmentGroup(data.getLogoGroupId());
//			customerInfo.setLogoGroupId(data.getLogoGroupId());
//			List<AttachmentFile> smAttachmentFileList1 = attachmentInterface.listAttachmentFile(data.getLogoGroupId());
//			if (smAttachmentFileList1 == null || smAttachmentFileList1.size() == 0) {
//				return false;
//			}
//			customerInfo.setLogoUrl(smAttachmentFileList1.get(0).getFileSaveName());
//			attachmentInterface.formalAttachmentGroup(data.getTwoCode());
//			customerInfo.setTwoCode(data.getTwoCode());
//			List<AttachmentFile> smAttachmentFileList2 = attachmentInterface.listAttachmentFile(data.getTwoCode());
//			if (smAttachmentFileList2 == null || smAttachmentFileList2.size() == 0) {
//				return false;
//			}
//			customerInfo.setTwoCodeUrl((smAttachmentFileList2.get(0).getFileSaveName()));
//			customerInfoService.update(customerInfo);
//		}
//
//		if (cFlag) {
//			if (cExchangeurlInfo.getSeqId() != null) {
//				CExchangeurlInfo cExchangeurl = cExchangeurlInfoService.get(cExchangeurlInfo.getSeqId());
//				cExchangeurl.setUpdateTime(new Date());
//				cExchangeurl.setUpdator(staff.getLoginName());
//				if (StringUtils.isNotBlank(cExchangeurlInfo.getBackgroudImgGroupId())) {
//					cExchangeurl.setBackgroudImgGroupId(cExchangeurlInfo.getBackgroudImgGroupId());
//
//					List<AttachmentFile> backsmAttachmentFileList = attachmentInterface
//							.listAttachmentFile(cExchangeurlInfo.getBackgroudImgGroupId());
//					if (backsmAttachmentFileList == null || backsmAttachmentFileList.size() == 0) {
//						return false;
//					}
//					cExchangeurl.setBackgroudImg(backsmAttachmentFileList.get(0).getFileSaveName());
//				} else {
//					cExchangeurl.setBackgroudImgGroupId("");
//					cExchangeurl.setBackgroudImg("");
//				}
//				if(StringUtils.isNotBlank(cExchangeurlInfo.getSuccImgGroupId())){
//					cExchangeurl.setSuccImgGroupId(cExchangeurlInfo.getSuccImgGroupId());
//					List<AttachmentFile> backsmAttachmentFileList = attachmentInterface
//							.listAttachmentFile(cExchangeurlInfo.getSuccImgGroupId());
//					if (backsmAttachmentFileList == null || backsmAttachmentFileList.size() == 0) {
//						return false;
//					}
//					cExchangeurl.setSuccImg(backsmAttachmentFileList.get(0).getFileSaveName());
//				}else{
//					cExchangeurl.setSuccImgGroupId("");
//					cExchangeurl.setSuccImg("");
//				}
//				cExchangeurl.setBodyColor(cExchangeurlInfo.getBodyColor());
//				cExchangeurl.setFontBottom(cExchangeurlInfo.getFontBottom());
//				cExchangeurl.setFontTitle(cExchangeurlInfo.getFontTitle());
//				cExchangeurl.setButtonColor(cExchangeurlInfo.getButtonColor());
//				cExchangeurl.setRedirectUrl(StringUtils.isBlank(cExchangeurlInfo.getRedirectUrl()) ? ""
//						: cExchangeurlInfo.getRedirectUrl());
//				cExchangeurl.setFooterUrl(cExchangeurlInfo.getFooterUrl());
//				cExchangeurl.setOperatorCode(cExchangeurlInfo.getOperatorCode());
//				cExchangeurl.setZone(cExchangeurlInfo.getZone());
//				cExchangeurlInfoService.update(cExchangeurl);
//			} else {
//				if (StringUtils.isNotBlank(cExchangeurlInfo.getBackgroudImgGroupId())) {
//					List<AttachmentFile> backsmAttachmentFileList = attachmentInterface
//							.listAttachmentFile(cExchangeurlInfo.getBackgroudImgGroupId());
//					if (backsmAttachmentFileList == null || backsmAttachmentFileList.size() == 0) {
//						return false;
//					}
//					cExchangeurlInfo.setBackgroudImg(backsmAttachmentFileList.get(0).getFileSaveName());
//				} else {
//					cExchangeurlInfo.setBackgroudImgGroupId("");
//					cExchangeurlInfo.setBackgroudImg("");
//				}
//				if(StringUtils.isNotBlank(cExchangeurlInfo.getSuccImgGroupId())){
//					cExchangeurlInfo.setSuccImgGroupId(cExchangeurlInfo.getSuccImgGroupId());
//					List<AttachmentFile> backsmAttachmentFileList = attachmentInterface
//							.listAttachmentFile(cExchangeurlInfo.getSuccImgGroupId());
//					if (backsmAttachmentFileList == null || backsmAttachmentFileList.size() == 0) {
//						return false;
//					}
//					cExchangeurlInfo.setSuccImg(backsmAttachmentFileList.get(0).getFileSaveName());
//				}else{
//					cExchangeurlInfo.setSuccImgGroupId("");
//					cExchangeurlInfo.setSuccImg("");
//				}
//				cExchangeurlInfo.setCustomerId(customerInfo.getCustomerId());
//				cExchangeurlInfo.setCreateTime(new Date());
//				cExchangeurlInfo.setCreator(staff.getLoginName());
//				cExchangeurlInfo.setUuid(customerInfo.getIdentityId());
//				cExchangeurlInfo.setUpdator("");
//				if (StringUtils.isBlank(cExchangeurlInfo.getRedirectUrl())) {
//					cExchangeurlInfo.setRedirectUrl("");
//				}
//				cExchangeurlInfoService.insert(cExchangeurlInfo);
//			}
//		}
//		return true;
		// TODO 
		return false;
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.flowOrder.Service.impl.IFlowCardInfoService#excelQuery(java.util.Map)
	 */
	public List<FlowCardInfo> excelQuery(Map<String, Object> params) {
		params.put("nowDate", new Date());
		return flowCardInfoDao.excelQuery(params);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.flowOrder.Service.impl.IFlowCardInfoService#convertListMap(java.util.List)
	 */
	public List<Map<String, Object>> convertListMap(List<FlowCardInfo> list) {
		List<Map<String, Object>> excelList = new ArrayList<Map<String, Object>>();
		SimpleDateFormat sdf = new SimpleDateFormat(DateUtil.YYYY_MM_DD_HH_MM_SS_EN);
		for (int i = 0; i < list.size(); i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			FlowCardInfo flowCardInfo = list.get(i);
			// 卡号
			map.put("cardNo", flowCardInfo.getCardNo());
			// 卡密
			map.put("cardPass", flowCardInfo.getCardPass());
			//卡容量大小
			map.put("cardFlow", flowCardInfo.getCardFlow());
			// 卡类型
			map.put("cardType", flowCardInfo.getCardType());
			// 创建时间
			map.put("createTime", sdf.format(flowCardInfo.getCreateTime()));
			// 有效期
			map.put("cardExp", sdf.format(flowCardInfo.getCardExp()));
			// 卡状态
			String cardState = "";
			if (flowCardInfo.getCardExp().getTime() < (new Date().getTime())) {
				list.get(i).setCardState(4);
			}
			switch (flowCardInfo.getCardState()) {
			case 1:
				cardState = "初始化";
				break;
			case 2:
				cardState = "激活";
				break;
			case 3:
				cardState = "已兑换";
				break;
			case 4:
				cardState = "过期";
				break;
			case 5:
				cardState = "锁定";
				break;
			case 6:
				cardState = "作废";
				break;
			}
			map.put("cardState", cardState);
			// 客户名称
			map.put("customerNmae", flowCardInfo.getCustomerInfo().getCustomerName());
			// 创建人
			map.put("createUser", flowCardInfo.getCreateUser());
			
			map.put("cardUrl", flowCardInfo.getCardUrl());
			
			map.put("cardImage",flowCardInfo.getCardImage());
			excelList.add(map);
		}
		return excelList;
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.flowOrder.Service.impl.IFlowCardInfoService#excelQueryByBatchId(java.lang.Long)
	 */
	public List<FlowCardInfo> excelQueryByBatchId(Long batchId) {
		return flowCardInfoDao.excelQueryByBatchId(batchId);
	}
	
	protected ShiroUser getCurrentLogin(){
		return ShiroKit.getUser();
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.flowOrder.Service.impl.IFlowCardInfoService#flowCardInfoActivatedBalance(java.lang.Long)
	 */
	public void flowCardInfoActivatedBalance(Long batchId) throws BussinessException{
		ShiroUser staff = getCurrentLogin();
		if(staff == null){
			LOG.error("激活 操作 未获取到对应的操作用户信息");
			throw new BussinessException(BizExceptionEnum.CUSTOMER_FORMAT_ERROR, "非法请求");// 1
		}
		String logName = staff.getAccount();
		//获取批次信息
		FlowCardBatchInfo flowCardBatchInfo = flowCardBatchInfoService.get(batchId);
		if(flowCardBatchInfo == null){
			LOG.error("流量加 订单批次无效，批次Id:"+batchId);
			throw new BussinessException(BizExceptionEnum.CUSTOMER_FORMAT_ERROR,"订单批次无效");
		}
		//判断订单的类型为何种状态（计费方式 1:按激活计费 2:按下发计费）
		OrderInfo orderInfo = orderInfoDao.selectByPrimaryKey(flowCardBatchInfo.getOrderId());
		if(orderInfo == null || orderInfo.getBillingType() == 2 || orderInfo.getBillingType() == 0){
			LOG.debug("流量加 激活未扣费，订单类型为:2 or object is null");
			return ;
		}
		//获取结算信息
		FlowCardSettlement flowCardBalance = flowCardSettlementDao.getFlowCardSettlementByBatchId(batchId);
		if(flowCardBalance == null){
			LOG.error("流量+ 结算错误，null");
			throw new BussinessException(BizExceptionEnum.CUSTOMER_FORMAT_ERROR,"获取结算信息错误");
		}
		//判断客户余额是否支持  结算
		CustomerInfo customerInfo = customerInfoService.get(flowCardBalance.getCustomerId());
		BigDecimal customerMoney = customerInfo.getBalance().add(customerInfo.getCreditAmount()).subtract(customerInfo.getCurrentAmount()).setScale(2,BigDecimal.ROUND_HALF_UP);
		if(customerMoney.compareTo(flowCardBalance.getCustomerPrice()) == -1){
			LOG.error("流量+ 结算 错误 ！账户余额不足。客户 [ "+customerInfo.getCustomerName()+ " ]可用额度："+customerMoney.setScale(2,BigDecimal.ROUND_HALF_UP)+"\t 待扣余额：" +flowCardBalance.getCustomerPrice().setScale(2,BigDecimal.ROUND_HALF_UP));
			throw new BussinessException(BizExceptionEnum.CUSTOMER_FORMAT_ERROR,"当前客户账户余额不足");// code 99
		}
		//判断合作伙伴是否支持  结算
		PartnerInfo partnerInfo = partnerInfoDao.selectByPrimaryKey(flowCardBalance.getPartnerId());
		BigDecimal partnerMoney = partnerInfo.getBalance().add(partnerInfo.getCreditAmount()).subtract(partnerInfo.getCurrentAmount()).setScale(2,BigDecimal.ROUND_HALF_UP);
		if(partnerMoney.compareTo(flowCardBalance.getPartnerPrice()) == -1){
			LOG.error("流量+ 结算 错误 ！账户余额不足。合作伙伴 [ "+partnerInfo.getPartnerName()+" ]可用额度："+partnerMoney.setScale(2,BigDecimal.ROUND_HALF_UP)+"\t 待扣余额：" +flowCardBalance.getCustomerPrice().setScale(2,BigDecimal.ROUND_HALF_UP));
			throw new BussinessException(BizExceptionEnum.CUSTOMER_FORMAT_ERROR,"合作伙伴 [ "+partnerInfo.getPartnerName()+" ] 账户余额不足。");
		}
		try {
			//生成日账单
			insertCustomerBalanceDay(flowCardBalance,customerInfo.getCustomerId(),partnerInfo.getPartnerId(),logName,true);
			//客户扣减	+ 日志流水记录
			customerSettlement(flowCardBalance,logName,true);
			//合作伙伴扣减 + 日志流水记录
			partnerSettlement(flowCardBalance,logName,true);
		} catch (Exception e) {
			LOG.error("流量加卡结算异常："+e.getMessage(),e);
			throw new BussinessException(BizExceptionEnum.CUSTOMER_FORMAT_ERROR,"流量加卡结算异常");
		}
	}
	
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.flowOrder.Service.impl.IFlowCardInfoService#customerSettlement(com.yzx.flow.common.persistence.model.FlowCardSettlement, java.lang.String, boolean)
	 */
	public void customerSettlement(FlowCardSettlement flowCardBalance,String logName,boolean bool){
		//获取对应的客户信息
		CustomerInfo customerInfo = customerInfoService.get(flowCardBalance.getCustomerId());
		if(bool){
			//修改对应的账户余额
			customerInfo.setBalance(customerInfo.getBalance().subtract(flowCardBalance.getCustomerPrice()).setScale(2,BigDecimal.ROUND_HALF_UP));
		}else{
			customerInfo.setBalance(customerInfo.getBalance().add(flowCardBalance.getCustomerPrice()).setScale(2,BigDecimal.ROUND_HALF_UP));
		}
		customerInfoService.update(customerInfo);
		//新增客户交易流水
		CustomerTradeFlow data = new CustomerTradeFlow();
        data.setTradeTime(new Date());
        data.setBalance(customerInfo.getBalance());
        data.setInputTime(new Date());
        String dataTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        if(bool){
        	data.setTradeAmount(BigDecimal.valueOf(0).subtract(flowCardBalance.getCustomerPrice()));
        	data.setTradeType(CustomerTradeFlow.TRADETYPE_ACTIVATION);
        	data.setOperatorName("流量+激活扣费");
        	data.setRemark(dataTime+" ,流量加卡,结算 ："+BigDecimal.valueOf(0).subtract(flowCardBalance.getCustomerPrice()));
        }else{
        	data.setTradeAmount(flowCardBalance.getCustomerPrice());
        	data.setTradeType(CustomerTradeFlow.TRADETYPE_CANCEL_UNTREAD);
        	data.setOperatorName("作废回退余额");
        	data.setRemark(dataTime+" ,流量加卡作废,余额回滚 ："+flowCardBalance.getCustomerPrice());
        }
        data.setCreditAmount(customerInfo.getCreditAmount());
        data.setCustomerId(customerInfo.getCustomerId());
        data.setLoginName(logName);
        int row = customerTradeFlowDao.insert(data);
        LOG.debug("[ "+customerInfo.getCustomerName()+" ]客户流水新增 ："+row);
	}
	
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.flowOrder.Service.impl.IFlowCardInfoService#partnerSettlement(com.yzx.flow.common.persistence.model.FlowCardSettlement, java.lang.String, boolean)
	 */
	public void partnerSettlement(FlowCardSettlement flowCardBalance,String logName,boolean bool){
		PartnerInfo partnerInfo = partnerInfoDao.selectByPrimaryKey(flowCardBalance.getPartnerId());
		if(bool){
			partnerInfo.setBalance(partnerInfo.getBalance().subtract(flowCardBalance.getPartnerPrice()).setScale(2,BigDecimal.ROUND_HALF_UP));
		}else{
			partnerInfo.setBalance(partnerInfo.getBalance().add(flowCardBalance.getPartnerPrice()).setScale(2,BigDecimal.ROUND_HALF_UP));
		}
		String result = RedisHttpUtil.sendGet(URLConstants.DEL_REDIS_URL,
				URLConstants.T_PARTNER_INFO, partnerInfo.getPartnerId()+"");
		if (!"OK".equals(result)) {
			throw new MyException("删除Redis中信息出错,其请求URL参数为:" + URLConstants.T_PARTNER_INFO+"\t"+partnerInfo.getPartnerId());
		}
		partnerInfoDao.updateByPrimaryKeyBill(partnerInfo);
		// 新增交易流水记录
		PartnerTradeFlow data = new PartnerTradeFlow();
        data.setTradeTime(new Date());
        data.setBalance(partnerInfo.getBalance());
        data.setCreditAmount(partnerInfo.getCreditAmount());
        String dataTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        if(bool){
        	data.setTradeAmount(BigDecimal.valueOf(0).subtract(flowCardBalance.getPartnerPrice()));
        	data.setOperatorName("流量+激活扣费");
            data.setTradeType(PartnerTradeFlow.TRADETYPE_ACTIVATION);
            data.setRemark(dataTime+"，流量加,结算："+BigDecimal.valueOf(0).subtract(flowCardBalance.getPartnerPrice()));
        }else{
        	data.setTradeAmount(flowCardBalance.getPartnerPrice());
        	data.setOperatorName("作废回退余额");
            data.setTradeType(PartnerTradeFlow.TRADETYPE_CANCEL_UNTREAD);
            data.setRemark(dataTime+"，流量加作废,余额回滚："+flowCardBalance.getPartnerPrice());
        }
        data.setPartnerId(partnerInfo.getPartnerId());
        data.setLoginName(logName);
        data.setInputTime(new Date());
        int row = partnerTradeFlowDao.insert(data);
        LOG.debug("[ "+partnerInfo.getPartnerName()+" ]合作伙伴流水新增 ："+row);
	}
	
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.flowOrder.Service.impl.IFlowCardInfoService#getFlowCardSettlementByCancel(java.lang.Long)
	 */
	public FlowCardSettlement getFlowCardSettlementByCancel(Long batchId) throws BussinessException{
		FlowCardSettlement flowCardSettlement = flowCardSettlementDao.getFlowCardSettlementByBatchIdCancel(batchId);
		if(flowCardSettlement == null){
			LOG.debug("作废  客户余额回滚异常：null（无回滚数据）");
			throw new BussinessException(BizExceptionEnum.CUSTOMER_FORMAT_ERROR,"流量加卡结算异常");
		}
		return flowCardSettlement;
	}
	
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.flowOrder.Service.impl.IFlowCardInfoService#cancelFlowCard(java.lang.String[], java.lang.String)
	 */
	public void cancelFlowCard(String[] ids,String logName){
		Map<String, Object> map = new HashMap<String,Object>();
		map.put("flowCardId", ids);
		map.put("date", new Date());
		List<FlowCardInfo> flowCardList = flowCardInfoDao.selectFlowCardBatchIdByCardIdList(map);
		if(flowCardList.isEmpty()){
			LOG.debug("作废余额回滚  异常：根据卡Id获取对应数据错误。");
			return ;
		}
		List<RecordFlowCardInfo> RecordFlowCardInfoList = new ArrayList<RecordFlowCardInfo>();
		FlowCardSettlement flowCardSettlement = new FlowCardSettlement();
		for (FlowCardInfo flowCardInfo : flowCardList) {
			try {
				getIsFlag(flowCardInfo.getBatchId());
				map.put("batchId", flowCardInfo.getBatchId());
				map.put("cardId", flowCardInfo.getCardId());
				flowCardSettlement = flowCardSettlementDao.selectFlowCardCancelInfo(map);
				if(RecordFlowCardInfoList.isEmpty()){		//新增第一个
					RecordFlowCardInfo recordFlowCardInfo = new RecordFlowCardInfo();
					recordFlowCardInfo.setCustomerId(flowCardSettlement.getCustomerId());
					recordFlowCardInfo.setPartnerId(flowCardSettlement.getPartnerId());
					recordFlowCardInfo.setCustomerPrice(flowCardSettlement.getCustomerPrice());
					recordFlowCardInfo.setPartnerPrice(flowCardSettlement.getPartnerPrice());
					recordFlowCardInfo.setOperCostPrice(flowCardSettlement.getOperCostPrice());
					RecordFlowCardInfoList.add(recordFlowCardInfo);
				}else{
					RecordFlowCardInfoList = updateRecordFlowCardInfo(RecordFlowCardInfoList,flowCardSettlement);
				}
			} catch (BussinessException e) {
				continue;
			}
		}
		//作废 余额回滚
		for (RecordFlowCardInfo recordFlowCardInfo : RecordFlowCardInfoList) {
			flowCardSettlement.setCustomerId(recordFlowCardInfo.getCustomerId());
			flowCardSettlement.setCustomerPrice(recordFlowCardInfo.getCustomerPrice());
			flowCardSettlement.setPartnerId(recordFlowCardInfo.getPartnerId());
			flowCardSettlement.setPartnerPrice(recordFlowCardInfo.getPartnerPrice());
			flowCardSettlement.setOperCostPrice(recordFlowCardInfo.getOperCostPrice());
			//生成日结算账单 （余额 为负）
			insertCustomerBalanceDay(flowCardSettlement, flowCardSettlement.getCustomerId(), flowCardSettlement.getPartnerId(), logName, false);
			customerSettlement(flowCardSettlement,logName, false);
			partnerSettlement(flowCardSettlement, logName, false);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.flowOrder.Service.impl.IFlowCardInfoService#getIsFlag(java.lang.Long)
	 */
	public boolean getIsFlag(Long batchId) throws BussinessException{
		//获取批次信息
		FlowCardBatchInfo flowCardBatchInfo = flowCardBatchInfoService.get(batchId);
		//判断订单的类型为何种状态（计费方式 1:按激活计费 2:按下发计费）
		OrderInfo orderInfo = orderInfoDao.selectByPrimaryKey(flowCardBatchInfo.getOrderId());
		if(orderInfo == null || orderInfo.getBillingType() == 2 || orderInfo.getBillingType() == 0){
			LOG.debug("流量加 激活未扣费，订单类型为:2 or object is null");
			throw new BussinessException(BizExceptionEnum.CUSTOMER_FORMAT_ERROR,"订单类型为:2 or object is null");
		}
		return true;
	}
	
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.flowOrder.Service.impl.IFlowCardInfoService#updateRecordFlowCardInfo(java.util.List, com.yzx.flow.common.persistence.model.FlowCardSettlement)
	 */
	public List<RecordFlowCardInfo> updateRecordFlowCardInfo(List<RecordFlowCardInfo> RecordFlowCardInfoList,FlowCardSettlement flowCardSettlement){
		for (RecordFlowCardInfo recordFlowCardInfos : RecordFlowCardInfoList) {
			if(recordFlowCardInfos.getCustomerId().equals(flowCardSettlement.getCustomerId())){
				recordFlowCardInfos.setCustomerPrice(recordFlowCardInfos.getCustomerPrice().add(flowCardSettlement.getCustomerPrice()).setScale(2,BigDecimal.ROUND_HALF_UP));
				recordFlowCardInfos.setPartnerPrice(recordFlowCardInfos.getPartnerPrice().add(flowCardSettlement.getPartnerPrice()).setScale(2,BigDecimal.ROUND_HALF_UP));
				recordFlowCardInfos.setOperCostPrice(recordFlowCardInfos.getOperCostPrice().add(flowCardSettlement.getOperCostPrice()).setScale(2,BigDecimal.ROUND_HALF_UP));
			}else{
				RecordFlowCardInfo recordFlowCardInfo = new RecordFlowCardInfo();
				recordFlowCardInfo.setCustomerId(flowCardSettlement.getCustomerId());
				recordFlowCardInfo.setPartnerId(flowCardSettlement.getPartnerId());
				recordFlowCardInfo.setCustomerPrice(flowCardSettlement.getCustomerPrice());
				recordFlowCardInfo.setPartnerPrice(flowCardSettlement.getPartnerPrice());
				recordFlowCardInfo.setOperCostPrice(flowCardSettlement.getOperCostPrice());
				RecordFlowCardInfoList.add(recordFlowCardInfo);
			}
			break;
		}
		return RecordFlowCardInfoList;
	}
	
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.flowOrder.Service.impl.IFlowCardInfoService#insertCustomerBalanceDay(com.yzx.flow.common.persistence.model.FlowCardSettlement, java.lang.Long, java.lang.Long, java.lang.String, boolean)
	 */
	public void insertCustomerBalanceDay(FlowCardSettlement flowCardBalance,Long customerId,Long partnerId,String logName,boolean bool){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("customerId", customerId);
		map.put("productId", flowCardBalance.getProductId());
		map.put("status", 1);
		CustomerBalanceDay customerBalanceDay = customerBalanceDayDao.getCustomerBalanceDayByCustomerIdAndProductId(map);
		//判断是否存在对应产品结算信息
		if(null == customerBalanceDay){
			customerBalanceDay = new CustomerBalanceDay();
			customerBalanceDay.setCreateTime(new Date());
			customerBalanceDay.setCreator(logName);
			customerBalanceDay.setCustomerId(customerId);
			customerBalanceDay.setFlowAmount(flowCardBalance.getCardFlow());
			customerBalanceDay.setInputStartTime(new SimpleDateFormat(DateUtil.YYYY_MM_DD_HH_MM_SS_EN).format(new Date()));
		}
		//日结算单
		customerBalanceDay.setBalanceDay(new SimpleDateFormat(DateUtil.YYYYMMDD_EN).format(new Date()));
		customerBalanceDay.setInputEndTime(new SimpleDateFormat(DateUtil.YYYY_MM_DD_HH_MM_SS_EN).format(new Date()));
		customerBalanceDay.setInputTime(new Date());
		customerBalanceDay.setMobileOperator(flowCardBalance.getOperatorCode().equals("WY") ? "YD/LT/DX" : flowCardBalance.getOperatorCode());
		customerBalanceDay.setCustomerBalancePrice(flowCardBalance.getPrice());
		customerBalanceDay.setPartnerBalancePrice(flowCardBalance.getSettPrice());
		customerBalanceDay.setOperatorBalancePrice(flowCardBalance.getOperCostPrice());
		customerBalanceDay.setPackageId(flowCardBalance.getPackageId());
		customerBalanceDay.setProductId(flowCardBalance.getProductId());
		customerBalanceDay.setPartnerId(partnerId);
		customerBalanceDay.setProductName(flowCardBalance.getProductName());
		customerBalanceDay.setStatus(1);
		customerBalanceDay.setZone(flowCardBalance.getZone());
		//判断主键   null则新增
		if(null == customerBalanceDay.getBalanceDayId()){
			//区分余额是增还是减
			if(bool){
				customerBalanceDay.setCustomerAmount(flowCardBalance.getCustomerPrice());
				customerBalanceDay.setPartnerAmount(flowCardBalance.getPartnerPrice());
				customerBalanceDay.setSendNum(flowCardBalance.getCount());
				customerBalanceDay.setRemark("流量加卡,激活生成账单");
			}else{
				customerBalanceDay.setCustomerAmount(BigDecimal.valueOf(0).subtract(flowCardBalance.getCustomerPrice()));
				customerBalanceDay.setPartnerAmount(BigDecimal.valueOf(0).subtract(flowCardBalance.getPartnerPrice()));
				customerBalanceDay.setSendNum(0-flowCardBalance.getCount());
				customerBalanceDay.setRemark("流量加卡作废,回滚账单");
			}
			customerBalanceDayDao.insert(customerBalanceDay);
			return ;
		}
		//区分余额是增还是减
		if(bool){
			customerBalanceDay.setCustomerAmount(customerBalanceDay.getCustomerAmount().add(flowCardBalance.getCustomerPrice()));
			customerBalanceDay.setPartnerAmount(customerBalanceDay.getPartnerAmount().add(flowCardBalance.getPartnerPrice()));
			customerBalanceDay.setSendNum((customerBalanceDay.getSendNum()+flowCardBalance.getCount()));
			customerBalanceDay.setRemark("流量加卡激活,修改账单");
		}else{
			customerBalanceDay.setCustomerAmount(customerBalanceDay.getCustomerAmount().subtract(flowCardBalance.getCustomerPrice()));
			customerBalanceDay.setPartnerAmount(customerBalanceDay.getPartnerAmount().subtract(flowCardBalance.getPartnerPrice()));
			customerBalanceDay.setRemark("流量加卡作废回滚,修改账单");
			customerBalanceDay.setSendNum((customerBalanceDay.getSendNum()-flowCardBalance.getCount()));
		}
		customerBalanceDayDao.updateByPrimaryKey(customerBalanceDay);
	}
	
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.flowOrder.Service.impl.IFlowCardInfoService#selectActiveAndExchangeCardInfoList(java.lang.Long)
	 */
	public List<FlowCardInfo> selectActiveAndExchangeCardInfoList(Long orderDetailId) {
	    return flowCardInfoDao.selectActiveAndExchangeCardInfoList(orderDetailId);
	}
	
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.flowOrder.Service.impl.IFlowCardInfoService#selectByCardNo(java.lang.String, java.lang.String)
	 */
	public List<FlowCardInfo> selectByCardNo(String startCardNo, String endCardNo) {
	    Map<String, Object> map = new HashMap<String, Object>();
	    map.put("startCardNo", startCardNo);
	    map.put("endCardNo", endCardNo);
	    return flowCardInfoDao.selectByCardNo(map);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.flowOrder.Service.impl.IFlowCardInfoService#updateByCardNo(java.lang.String, java.lang.String, java.util.Date)
	 */
	public void updateByCardNo(String startCardNo, String endCardNo, Date cardExp) {
	    Map<String, Object> map = new HashMap<String, Object>();
	    map.put("startCardNo", startCardNo);
	    map.put("endCardNo", endCardNo);
	    map.put("cardExp", cardExp);
	    flowCardInfoDao.updateByCardNo(map);
	    flowCardInfoDao.updateActiveByCardNo(map);
	}
}