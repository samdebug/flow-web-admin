package com.yzx.flow.modular.portal.service.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yzx.flow.common.exception.BizExceptionEnum;
import com.yzx.flow.common.exception.BussinessException;
import com.yzx.flow.common.persistence.dao.AreaCodeMapper;
import com.yzx.flow.common.persistence.model.CustomerInfo;
import com.yzx.flow.common.persistence.model.FlowCardBatchInfo;
import com.yzx.flow.common.persistence.model.FlowProductExchangeRec;
import com.yzx.flow.common.persistence.model.OrderInfo;
import com.yzx.flow.common.persistence.model.PartnerInfo;
import com.yzx.flow.modular.portal.service.IExchangeFlowCardService;
import com.yzx.flow.modular.portal.service.IFlowCardUtilService;
import com.yzx.flow.modular.system.dao.CExchangeurlInfoDao;
import com.yzx.flow.modular.system.dao.CustomerInfoDao;
import com.yzx.flow.modular.system.dao.FlowAppInfoDao;
import com.yzx.flow.modular.system.dao.FlowCardBatchInfoDao;
import com.yzx.flow.modular.system.dao.FlowCardInfoDao;
import com.yzx.flow.modular.system.dao.FlowProductExchangeRecDao;
import com.yzx.flow.modular.system.dao.FlowProductInfoDao;
import com.yzx.flow.modular.system.dao.MobileHomeInfoDao;
import com.yzx.flow.modular.system.dao.OrderInfoDao;
import com.yzx.flow.modular.system.dao.PartnerInfoDao;

/**
 * 流量下发service操作类
 * @author wml
 */
@Service
public class ExchangeFlowCardServiceImpl implements IExchangeFlowCardService {
//	@Autowired
//	private FlowCardInfoDao flowCardDao;
	
	@Autowired
	private CustomerInfoDao customerInfoDao;
	
	@Autowired
	private FlowProductExchangeRecDao FlowProductExchangeRecDao;
	
	@Autowired
	private FlowCardBatchInfoDao flowCardBatchDao;
	
	@Autowired
	private PartnerInfoDao PartnerInfoDao;
	
	@Autowired
	private OrderInfoDao orderInfoDao;
	
//	@Autowired
//	private FlowAppInfoDao flowAppInfoDao;
//	
//	@Autowired
//	private FlowProductInfoDao flowProductInfoDao;
	
	@Autowired
	private IFlowCardUtilService flowCardUtilService;
	
	@Autowired
	private FlowProductExchangeRecDao floProductExchangeDao;
	
//	@Autowired
//	private AttachmentInterface attachmentInterface;
//	
//	@Autowired
//	private CExchangeurlInfoDao cExchnageDao;
//	
//	@Autowired
//	private MobileHomeInfoDao mobileHomeInfoDao;
//	
//	@Autowired
//	private AreaCodeMapper areaCodeDao;
	
	private static final Logger logger = LoggerFactory.getLogger(ExchangeFlowCardServiceImpl.class);
//	/**
//	 * 兑换下发流量
//	 * @param flowCard
//	 * @param userPhone
//	 * @param IP
//	 * @return
//	 * @throws ParseException
//	 * @throws JsonGenerationException
//	 * @throws JsonMappingException
//	 * @throws IOException
//	 */
//	@SuppressWarnings("static-access")
//	@Transactional
//	public WebResponseMsgJSON exchangeFlow(FlowCardInfo flowCard,String userPhone,String IP) throws BussinessException, ParseException, JsonGenerationException, JsonMappingException, IOException{
//		WebResponseMsgJSON result = new WebResponseMsgJSON();
//		try {
//			//风控，每月单个手机号码可兑换次数为3
//			boolean isRigst = flowCardUtilService.isRegist(userPhone);
//			if(isRigst){
//				  	//允许兑换则修改状态为9，表示已处于兑换状态中，不允许后面的进行兑换
//				  	int a = flowCardDao.updateBystatus(flowCard);
//				  	if(a != 1){
//				  		result.setCode("03");
//				  		result.setMsg("卡片已兑换!");
//				  		return result;
//				  	}
//				  	//获取APPID和APPKey
//				  	WebResponseMsgJSON res = new WebResponseMsgJSON();
//				  	res = flowCardUtilService.checkFlowCardInfo(flowCard);
//				  	if("00".equals(res.getCode())){
//				  		String appId = "";
//				  		String appKey = "";
//				  		CustomerInfo priseInfo = customerInfoDao.getCustomerInfoByCustomerId(flowCard.getCustomId());
//				  		if(priseInfo == null){
//				  			result.setCode("99");
//					  		result.setMsg("客户信息错误");
//					  		return result;
//				  		}
//				  		
//				  		//判断卡信息是否包含APPId和appKey,没有则通过客户Id及订单信息查询对应的APPId和APPKEY完成网关流量下发操作
//				  		Long orderId = Long.parseLong(res.getData().get("orderId").toString());
//				  		FlowAppInfo flowAppInfo = new FlowAppInfo();
//				  		flowAppInfo.setOrderId(orderId);
//				  		flowAppInfo.setCustomerId(priseInfo.getCustomerId());
//				  		flowAppInfo = flowAppInfoDao.getFlowAppInfoByOrderIdAndCustomerId(flowAppInfo).get(0);
//				  		if(StringUtils.isEmpty(flowAppInfo.getAppId()) || StringUtils.isEmpty(flowAppInfo.getAppKey())){
//				  			logger.error("流量兑换  订单Id："+orderId+"\t 和customerId:"+priseInfo.getCustomerId()+"未找到对应的 app接入信息");
//				  			result.setCode("99");
//					  		result.setMsg("流量兑换失败");
//					  		flowCard.setCardState(2);
//							flowCardDao.updateByPrimaryKeySelective(flowCard);
//					  		return result;
//				  		}
//				  		appId = flowAppInfo.getAppId();
//				  		appKey = flowAppInfo.getAppKey();
//				  		logger.debug("------APPID:"+appId+"\t APPKEY:"+appKey);
//				  		//
//				  		if(StringUtils.isNotEmpty(appId) && StringUtils.isNotEmpty(appKey)){
//				  			//用兑现卡去兑现
//						   	int exchangeResult = flowCardUtilService.exchangFlowViaOperator(userPhone, flowCard,appId,appKey,"1","1");
//						   	if (exchangeResult == 0){
//						   		  //成功验证风控
//						   		  boolean isRigsts = flowCardUtilService.getFlowCardInfoAddCount(userPhone);
//						   		  if(isRigsts){
//							   		  flowCard.setCardState(3);
//									  this.flowCardDao.updateByPrimaryKeySelective(flowCard);
//									  
//									  //新增账户兑换流水
//							   		  try {
//							   			FlowProductExchangeRec cardLogs = new FlowProductExchangeRec();
//										  cardLogs.setMobileNo(userPhone);
//										  cardLogs.setCardId(flowCard.getCardId());
//										  cardLogs.setCardNo(flowCard.getCardNo());
//										  cardLogs.setCardPass(flowCard.getCardPass());
//										  cardLogs.setLogTime(new Date());
//										  cardLogs.setReceiveIp(IP);
//										  cardLogs.setCardFlow(flowCard.getCardFlow());
//										  cardLogs.setUserId("");
//										  cardLogs.setCardSupplier(CheckPhone.getMobileOpr(userPhone));
//										  cardLogs.setIsNotify(0);
//										  cardLogs.setOrderId(flowCardUtilService.create_randomId(32));
//										  cardLogs.setExtOrder("");
//										  FlowProductExchangeRecDao.insert(cardLogs);
//									} catch (Exception e) {
//										logger.error("新增兑换记录异常："+e.getMessage(),e);
//									}
//									  // 根据企业商家id查询对应的商家信息，用于页面返回
//									  if(priseInfo != null){
//										  FlowVoucher flowVoucher = new FlowVoucher();
//										  flowVoucher.setEnterpriseName(priseInfo.getCustomerName());
//										  flowVoucher.setFlowCount(flowCard.getCardFlow());
//										  //二维码链接
//										  String iamgeUrl = SystemConfigs.getInstance().getString("flow-card.imageUrl"); 
//										  flowVoucher.setEnterUrl(iamgeUrl+priseInfo.getTwoCodeUrl());
//										  flowVoucher.setUserPhone(userPhone);
//										  //logo链接
//										  //boolean httpLogo = logoUrl.startsWith("http");
//										  flowVoucher.setEnterLogo(iamgeUrl+priseInfo.getLogoUrl());
//										  result.setData(JSONObject.fromObject(new ObjectDao().writeValueAsString(flowVoucher)));  
//									  }
//									  return result;
//						   		  }else{
//						   			  flowCardUtilService.chearCount(userPhone);
//						   			  result.setCode("08");
//									  result.setMsg("流量下发失败，未知错误");
//									  return result;
//						   		  }
//						   	}else if (exchangeResult == 1){
//						   		  result.setCode("10");
//						   		  result.setMsg("流量暂时无法充入该手机，请稍后再试");
//						   	}else {
//						   		  result.setCode(exchangeResult+"");
//						   		  result.setMsg("抱歉：发生未知的错误，无法兑换流量，请稍后再试");
//						   	}
//						   	flowCardUtilService.chearCount(userPhone);
//						   	flowCard.setCardState(2);
//							int c = this.flowCardDao.updateByPrimaryKeySelective(flowCard);
//							logger.debug("下发失败兑换卡状态修改返回---------------"+c);
//							return result;
//				  		}else{
//				  			throw new BussinessException("99", "获取APPID失败");
//				  		}
//				  	}else{
//				  		throw new BussinessException("99", "未知错误");
//				  	}
//			}else{
//				result.setCode("09");
//		   		result.setMsg("该手机号码本月兑换已达最大次数");
//				return result;
//			}
//		} catch (Exception e) {
//			logger.error("兑换异常："+e.getMessage(),e);
//			flowCard.setCardState(2);
//			flowCardDao.updateByPrimaryKeySelective(flowCard);
//			result.setCode("99");
//	   		result.setMsg("兑换失败！");
//			return result;
//		}
//	}
	
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.portal.service.impl.IExchangeFlowCardService#getCustomerInfoByUUID(java.lang.String)
	 */
	public CustomerInfo getCustomerInfoByUUID(String UUID) throws BussinessException {
		if(StringUtils.isEmpty(UUID)){
			throw new BussinessException(BizExceptionEnum.CUSTOMER_FORMAT_ERROR, "参数错误");
		}else{
			List<CustomerInfo> customerList = customerInfoDao.getCustomerInfoByUUID(UUID);
			if(customerList == null || customerList.isEmpty()){
				throw new BussinessException(BizExceptionEnum.CUSTOMER_FORMAT_ERROR, "客户信息错误");
			}else{
				return customerList.get(0);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.portal.service.impl.IExchangeFlowCardService#getFlowProductExchangeRecByFlowCard(com.yzx.flow.common.persistence.model.FlowProductExchangeRec)
	 */
	public FlowProductExchangeRec getFlowProductExchangeRecByFlowCard(FlowProductExchangeRec record) throws BussinessException{
		if(record == null){
			throw new BussinessException(BizExceptionEnum.CUSTOMER_FORMAT_ERROR, "参数错误");
		}else{
			return FlowProductExchangeRecDao.getFlowProductExchangeRecByFlowCardInfo(record);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.portal.service.impl.IExchangeFlowCardService#getPartnerInfoByPartnerId(java.lang.Long)
	 */
	public PartnerInfo getPartnerInfoByPartnerId(Long partnerId) throws BussinessException{
		if(partnerId == null){
			throw new BussinessException(BizExceptionEnum.CUSTOMER_FORMAT_ERROR, "参数错误");
		}else{
			PartnerInfo partnerInfo = PartnerInfoDao.selectByPrimaryKey(partnerId);
			if(partnerInfo == null){
				throw new BussinessException(BizExceptionEnum.CUSTOMER_FORMAT_ERROR, "合作伙伴信息错误");
			}else{
				return partnerInfo;
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.portal.service.impl.IExchangeFlowCardService#getCustomerInfoByCustomerId(java.lang.Long)
	 */
	public CustomerInfo getCustomerInfoByCustomerId(Long customerId) throws BussinessException{
		if(customerId == null){
			throw new BussinessException(BizExceptionEnum.CUSTOMER_FORMAT_ERROR, "参数错误");
		}else{
			CustomerInfo customerInfo = customerInfoDao.getCustomerInfoByCustomerId(customerId);
			if(customerInfo == null){
				throw new BussinessException(BizExceptionEnum.CUSTOMER_FORMAT_ERROR, "合作伙伴信息错误");
			}else{
				return customerInfo;
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.portal.service.impl.IExchangeFlowCardService#getFlowCardBatchInfoById(java.lang.Long)
	 */
	public FlowCardBatchInfo getFlowCardBatchInfoById(Long batchId) throws BussinessException{
		if(batchId == null){
			throw new BussinessException(BizExceptionEnum.CUSTOMER_FORMAT_ERROR, "参数错误");
		}else{
			FlowCardBatchInfo flowCardBatchInfo = flowCardBatchDao.selectByPrimaryKey(batchId);
			if(flowCardBatchInfo == null){
				throw new BussinessException(BizExceptionEnum.CUSTOMER_FORMAT_ERROR, "批次信息错误");
			}else{
				return flowCardBatchInfo;
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.portal.service.impl.IExchangeFlowCardService#getOrderInfoByBatchId(java.lang.Long)
	 */
	public OrderInfo getOrderInfoByBatchId(Long batchId) throws BussinessException{
		if(batchId == null){
			throw new BussinessException(BizExceptionEnum.CUSTOMER_FORMAT_ERROR, "参数错误");
		}else{
			OrderInfo orderInfo = orderInfoDao.getOrderInfoByBatchId(batchId);
			if(orderInfo == null){
				throw new BussinessException(BizExceptionEnum.CUSTOMER_FORMAT_ERROR, "批次信息错误");
			}else{
				return orderInfo;
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.portal.service.impl.IExchangeFlowCardService#deleteExchangeLogById(java.lang.Long)
	 */
	public void deleteExchangeLogById(Long exchangeId) throws BussinessException{
		if(exchangeId == null){
			throw new BussinessException(BizExceptionEnum.CUSTOMER_FORMAT_ERROR, "参数错误");
		}else{
			floProductExchangeDao.deleteByPrimaryKey(exchangeId);
		}
	}
	
//	/**
//	 * 流量卡定向兑换修改
//	 * @param uuid
//	 * @param mobile
//	 	//兑换范围：【全部、移动、电信、联通】
//		//兑换区域：【全国、广东省、XX省、xxxx】 
//	 * @return
//	 * @throws BussinessException 
//	 */
//	public WebResponseMsgJSON checkMobile(String uuid,String mobile) throws BussinessException{
//		WebResponseMsgJSON result = new WebResponseMsgJSON();
//		try {
//			if(StringUtils.isNotEmpty(uuid) && StringUtils.isNotEmpty(mobile)){
//				CExchangeurlInfo  cExchangeurlInfo = cExchnageDao.getCExchangeurlInfoByUUID(uuid);
//				if(null != cExchangeurlInfo){
//					result = checkExchangeInfoByMobile(cExchangeurlInfo,mobile);
//				}else{
//					logger.error("C类卡兑换 参数错误----uuid："+uuid+" 未找到对应配置信息");
//					throw new BussinessException("002", "兑换错误，无效兑换");
//				}
//			}else{
//				logger.error("C类卡兑换 参数异常----uuid："+uuid+"\t 手机号码："+mobile);
//				throw new BussinessException("001", "参数错误");
//			}
//		} catch (BussinessException e) {
//			result.setCode(e.getCode());
//			result.setMsg(e.getMsg());
//		}
//		return result;
//	}
//	
//	public WebResponseMsgJSON checkExchangeInfoByMobile(CExchangeurlInfo cExchangeurlInfo,String mobile){
//		WebResponseMsgJSON result = new WebResponseMsgJSON();
//		try {
//			if(StringUtils.isEmpty(cExchangeurlInfo.getOperatorCode()) || StringUtils.isEmpty(cExchangeurlInfo.getZone())){
//				logger.error("C类卡兑换错误，当前兑换配置信息  数据有误(operatorCode  is null   or zone is null)");
//				result.setCode("003");
//				result.setMsg("兑换失败！当前兑换配置信息异常");
//				return result;
//			}
//			//验证   配置  运营商编号
//			if("ALL".equals(cExchangeurlInfo.getOperatorCode()) && "00".equals(cExchangeurlInfo.getZone())){
//				return result;
//			}
//			//后台获取手机号码对应的归属信息
//			MobileHomeInfo mobileInfo = MobileHomeContext.getContext().getByMobile(mobile);
//			String _oper = CommonUtil.getMobileOpr(mobile);
//			if(null == mobileInfo || _oper == null || "NA".equalsIgnoreCase(_oper)){
//				logger.error("C类卡兑换--------"+mobile+"在数据库中无对应号码信息");
//				return result;
//			}
//			mobileInfo.setOperatorCode(_oper);// 之所以重新设置，因为数据库里面的数据包含除YD/DX/LT以外的数据
//			//运营商id
//			if(!"ALL".equals(cExchangeurlInfo.getOperatorCode()) && !cExchangeurlInfo.getOperatorCode().equals(mobileInfo.getOperatorCode())){
//				result.setCode("003");
//				String str = cExchangeurlInfo.getOperatorCode();
//				String oper = "";
//				switch (str) {
//					case "DX":
//						oper = "中国电信";
//						break;
//					case "LT":
//						oper = "中国联通";
//						break;
//					case "YD":
//						oper = "中国移动";
//						break;
//					default:
//						break;
//				}
//				result.setMsg("不是[ "+oper+" ]手机号码，无法领取！");
//				return result;
//			}
//			if (!"00".equals(cExchangeurlInfo.getZone()) && !cExchangeurlInfo.getZone().equals(mobileInfo.getAreaCode())) {
//				result.setCode("003");
//				//获取地区
//				String zone =returnZone(cExchangeurlInfo.getZone());
//				result.setMsg("不是[ "+zone+" ]手机号码，无法领取！");
//				return result;
//			}
//		} catch (BussinessException e) {
//			result.setCode(e.getCode());
//			result.setMsg(e.getMsg());
//		}catch (Exception e) {
//			logger.error("C类卡兑换  异常："+e.getMessage(),e);
//			result.setCode("99");
//			result.setMsg("流量兑换失败！");
//		}
//		return result;
//	}
//	
//	/**
//	 * 根据C卡兑换配置信息获取对应的地区信息列表
//	 * @param cExchangeZone
//	 * @return	对应的地区信息
//	 * @throws BussinessException
//	 */
//	public String returnZone(String cExchangeZone) throws BussinessException{
//		String zone = "";
//		List<AreaCode> areaCodeList = areaCodeDao.getAreaCodeAll();
//		if(areaCodeList.isEmpty()){
//			logger.debug("获取地区信息错误");
//			throw new BussinessException("99","获取地区信息错误");
//		}
//		for (AreaCode areaCode : areaCodeList) {
//			if(areaCode.getAreaCode().equals(cExchangeZone)){
//				zone = areaCode.getAreaName();
//			}
//		}
//		return zone;
//	}
	
}
