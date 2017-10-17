package com.yzx.flow.modular.portal.service.impl;

import java.io.IOException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yzx.flow.common.exception.BizExceptionEnum;
import com.yzx.flow.common.exception.BussinessException;
import com.yzx.flow.common.persistence.model.CustomerInfo;
import com.yzx.flow.common.persistence.model.FlowBatchRecord;
import com.yzx.flow.common.persistence.model.FlowCardInfo;
import com.yzx.flow.common.persistence.model.FlowCardRisk;
import com.yzx.flow.common.persistence.model.FlowExchangeLog;
import com.yzx.flow.common.persistence.model.FlowMakeOrderReqMesg;
import com.yzx.flow.common.persistence.model.FlowProductExchangeRec;
import com.yzx.flow.common.persistence.model.FlowReqMesg;
import com.yzx.flow.common.persistence.model.FossFlowMakeBack;
import com.yzx.flow.common.persistence.model.RespMsgBody;
import com.yzx.flow.common.persistence.model.TraderInfo;
import com.yzx.flow.common.persistence.model.exchange.FlowResMsg;
import com.yzx.flow.common.util.Constant;
import com.yzx.flow.common.util.DateUtil;
import com.yzx.flow.core.support.HttpUtil;
import com.yzx.flow.core.util.CheckPhone;
import com.yzx.flow.modular.portal.service.IExchangeFlowCardService;
import com.yzx.flow.modular.portal.service.IFlowCardUtilService;
import com.yzx.flow.modular.system.dao.CampaignRewardInfoDao;
import com.yzx.flow.modular.system.dao.CustomerInfoDao;
import com.yzx.flow.modular.system.dao.FlowAppInfoDao;
import com.yzx.flow.modular.system.dao.FlowBatchRecordDao;
import com.yzx.flow.modular.system.dao.FlowCardBatchInfoDao;
import com.yzx.flow.modular.system.dao.FlowCardInfoDao;
import com.yzx.flow.modular.system.dao.FlowCardRiskDao;
import com.yzx.flow.modular.system.dao.FlowExchangeLogDao;
import com.yzx.flow.modular.system.dao.FlowProductExchangeRecDao;
import com.yzx.flow.modular.system.dao.FlowProductInfoDao;
import com.yzx.flow.modular.system.dao.OrderDetailDao;
import com.yzx.flow.modular.system.dao.SignPointsRecordDao;
import com.yzx.flow.modular.system.dao.TraderInfoDao;

import com0oky.httpkit.http.HttpKit;

/**
 * 流量卡兑换帮助类
 * @author wml
 */
@Service
public class FlowCardUtilServiceImpl implements IFlowCardUtilService {
	
	/**
	 * 收单组件地址
	 */
	private static String FLOW_DISPATCHER_URL;
	
	@Autowired
	private FlowCardBatchInfoDao flowCardBatchDao;
	
	@Autowired
	private FlowCardRiskDao flowCardRiskDaos;
	
	@Autowired
	private FlowExchangeLogDao flowEchangeLogDao;
	
	@Autowired
	private FlowCardInfoDao flowCardDao;
	
	@Autowired
	private OrderDetailDao orderDetailDao;
	
	@Autowired
	private IExchangeFlowCardService cardService;
	
	@Autowired
	private FlowProductInfoDao flowProductDao;
	
	@Autowired
	private FlowExchangeLogDao flowExchangeLogDao;
	
	@Autowired
	private FlowProductExchangeRecDao flowProductExchangeLogDao;
	
	@Autowired
	private TraderInfoDao traderInfoDao;
	
	@Autowired
	private CustomerInfoDao customerDao;
	
	@Autowired
	private FlowAppInfoDao flowAppInfoDao;
	
	@Autowired
	private CampaignRewardInfoDao campaignRewardDao;
	
	@Autowired
	private FlowBatchRecordDao flowBatchRecordDao;
	
//	@Autowired
//	@Qualifier("exchagecampaignRewardInfoService")
//	private CampaignRewardInfoService campaignRewardInfoService;
	
//	@Autowired
//	private WXPayOrderInfoDao wxPayOrderInfoDao;
	
//	@Autowired
//	private TaobaoOrderInfoDao taobaoOrderInfoDao;
	
	@Autowired
	private SignPointsRecordDao signPointsRecordDao;
	
	//打印日志
	private static final Logger logger = LoggerFactory.getLogger(FlowCardUtilServiceImpl.class);
	
	public static String create_randomId(int len) {
    	String ret = UUID.randomUUID().toString().replace("-","");
    	if (ret.length() > len){
    		ret = ret.substring(0, len);
    	}
        return ret;
    }
	
//	//通过下发渠道下发
//	public int exchangFlowViaOperator(String userPhone,FlowCardInfo flowCard,String appId,String appKey,String type,String orderType){
//		boolean isExchange = true;
//		final FlowMakeOrderReqMesg order = new FlowMakeOrderReqMesg();
//		//流量下发记录
//		FlowExchangeLog flowLog = new FlowExchangeLog();
//		try {
//			//批次
//			FlowCardBatchInfo flowCardBatch = flowCardBatchDao.selectByPrimaryKey(flowCard.getBatchId());
//			//产品
//			FlowProductInfo flowProductInfo = flowProductDao.getFlowProductInfoByBatchId(flowCard.getBatchId());
//			flowLog.setFlowVoucherId(create_randomId(32));
//			flowLog.setMobile(userPhone);
//			flowLog.setProductId(flowProductInfo.getProductId());
//			flowLog.setOrderDetailId(flowCardBatch.getOrderDetailId());
//			flowLog.setItemCount(new Integer(1));
//			flowLog.setCreateTime(new Date());
//			flowLog.setOrgActiveId(0L);
//			flowLog.setMobileOperator(checkMobileOpr(userPhone));
//			//手机号码归属地
//			flowLog.setMobileHome("无");
//			flowLog.setSourceId(flowCard.getCardId().toString());
//			if("1".equals(type)){
//				flowLog.setSourceType("00");
//			}else{
//				flowLog.setSourceType("01");
//			}
//			flowLog.setExchangeOrderId("");
//			flowLog.setFlag("1");	//0 失败 1成功
//			//下发记录保存
//			int rows = flowEchangeLogDao.insert(flowLog);
//			if(rows != 1){
//				logger.error("兑换下发 出现异常 插入兑换日志失败.... number：157");
//				return 1;
//			}
//			logger.debug("兑换日志记录新增返回主键："+flowLog.getLogId());
//			//获取产品Id
//		    order.setEXTORDER(flowLog.getFlowVoucherId());
//			order.setPACKAGEID(flowProductInfo.getPackageId());	//流量包ID
//			order.setUSER(userPhone);
//			if (StringUtils.isBlank(orderType)){
//				orderType = "1";
//			}
//			order.setORDERTYPE(orderType);
//			order.setNOTE("");
//			
//			//根据订单客户Id查询对应客户的appId和appKey，作为参数下发流量
//			//String rewardSeqNo = dispatchFlowByCard(order,appId,appKey);
//			FossFlowMakeBack flowMackBack = dispatchFlowByCard(order,appId,appKey);
//			// logger.debug("---------------下发之后--------------"+rewardSeqNo);
//			if (flowMackBack != null && "0".equals(flowMackBack.getCode())){
//				flowLog.setExchangeOrderId(flowMackBack.getOrderId());
//			  	flowLog.setFlag("0");
//			  	int count = flowEchangeLogDao.updateByPrimaryKey(flowLog);
//			  	logger.debug("兑换记录日志 更新影响行数："+count);
//			}else{
//				isExchange = false;
//			}
//		} catch (Exception e) {
//			  logger.error("流量下发异常错误**************："+e.getMessage(),e);
//			  isExchange = false;
//		}
//		if(isExchange){
//			logger.debug("流量成功充入该手机："+userPhone);
//	        return 0;//已充入该手机
//		}else{
//			logger.debug("流量暂时无法充入该手机："+userPhone);
//			return 1;//暂时无法充入该手机
//		}
//	}
	
	//流量+ 流量卡流量下发
	public final static FossFlowMakeBack dispatchFlowByCard(FlowMakeOrderReqMesg s,String appId,String appKey) throws Exception{
		return dispatchFlow(s, appId,appKey, FLOW_DISPATCHER_URL);
	}
	
	
	public static void main(String[] args) throws IOException {
//		http://host.com/api.aspx?v=1.1&action=charge&account=帐号&mobile=手机号&package=100&sign=MD5
		String dispatch_url="http://127.0.0.1:8080/flow-rec/api.aspx";
		String param="v=1.1&action=charge&account=亿美&mobile=13400002924&package=YD10&OutTradeNo=123&sign=";
		String sign=DigestUtils.md5Hex("account=亿美&mobile=13400002924&package=YD10&key=9d5dcab21caf454383ae63c056e9c8c4");
		FlowMakeOrderReqMesg s=new FlowMakeOrderReqMesg();
		s.setUSER("13400002929");
		s.setPACKAGEID("YD10");
		String realurl=dispatch_url+"?v=1.1&action=charge";
		String baseparamMD5="account="+"亿美"+"&mobile="+s.getUSER()+"&package="+s.getPACKAGEID();
		String baseparam="account="+URLEncoder.encode("亿美","UTF-8")+"&mobile="+URLEncoder.encode(s.getUSER(),"UTF-8")+"&package="+URLEncoder.encode(s.getPACKAGEID(),"UTF-8");
		realurl=realurl+"&"+baseparam;
		realurl=realurl+"&sign="+DigestUtils.md5Hex(baseparamMD5+"&key=9d5dcab21caf454383ae63c056e9c8c4");
//		String str=HttpClientUtil.sendDataGetRequest(realurl);
//		System.out.println(str);
		String str="{\"Message\":\"充值提交成功\",\"Code\":\"0\"}";
		ObjectMapper objectDao = new ObjectMapper();
		FlowResMsg flowResMsg=objectDao.readValue(str, FlowResMsg.class);
		System.out.println(flowResMsg.getMessage());
		System.out.println(flowResMsg.getCode());
		System.out.println(flowResMsg.getTaskID());
	}
	
	public final static FossFlowMakeBack dispatchFlow(FlowMakeOrderReqMesg s,
			String flowRequestAppId, String flowRequestAppSecret,
			String dispatch_url) throws Exception {
		FossFlowMakeBack flowMackBack = new FossFlowMakeBack();
		FlowReqMesg reqMsg = new FlowReqMesg();
		String realurl=dispatch_url+"?v=1.1&action=charge";
		String baseparamMD5="account="+flowRequestAppId+"&mobile="+s.getUSER()+"&package="+s.getPACKAGEID();
		String baseparam="account="+URLEncoder.encode(flowRequestAppId,"UTF-8")+"&mobile="+URLEncoder.encode(s.getUSER(),"UTF-8")+"&package="+URLEncoder.encode(s.getPACKAGEID(),"UTF-8")+"&outTradeNo="+URLEncoder.encode(s.getEXTORDER(),"UTF-8");
		realurl=realurl+"&"+baseparam;
		realurl=realurl+"&sign="+DigestUtils.md5Hex(baseparamMD5+"&key="+flowRequestAppSecret);
//		String respJson=HttpUtil.sendGet(realurl, new HashMap<String, String>());
		String respJson = HttpKit.get(realurl).execute().getString();
		logger.debug("****respJson---------------------:"+respJson);
		if (respJson != null && !"".equals(respJson)) {
			FlowResMsg respMesg = JSONObject.parseObject(respJson, FlowResMsg.class);
			if ("0".equals(respMesg.getCode())) {
				flowMackBack.setCode(respMesg.getCode());
				flowMackBack.setOrderId(respMesg.getTaskID());
				flowMackBack.setMsg(respMesg.getMessage());
				return flowMackBack;
			}else{
				logger.info("充值失败,请求的URL:"+realurl);
				logger.error("兑换失败，网关返回："+respMesg.getMessage());
				flowMackBack.setCode(respMesg.getCode());
				flowMackBack.setOrderId(respMesg.getTaskID());
				flowMackBack.setMsg(respMesg.getMessage());
				return flowMackBack;
			}
		}
		return flowMackBack;
	}
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.portal.service.impl.IFlowCardUtilService#isRegist(java.lang.String)
	 */
	public boolean isRegist(String userPhone) throws ParseException{
		boolean a = getFlowCardInfo(userPhone);
		if(a){
			return true;
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.portal.service.impl.IFlowCardUtilService#getFlowCardInfo(java.lang.String)
	 */
	@Transactional
	public boolean getFlowCardInfo(String phone_no) throws ParseException{
		/**
		 * 根据手机号码查询对应的数据，为空则新增，已当前日期的起始时间
		 * 否则就判断兑换次数，大于3则提示已达到兑换数，不能兑换，否则继续兑换
		 */
		boolean isRigst = false;
		FlowCardRisk flowCard = flowCardRiskDaos.selectFlowCardRiskByPhone(phone_no);
		if (flowCard == null){
			boolean a = createWYFlowCardRisk(phone_no);
			if(a){
				isRigst = true;
			}
		}else{
			Date dataDate = new SimpleDateFormat(DateUtil.YYYYMMDD_EN).parse(flowCard.getReceiveDate());;	//兑换时间
			Date currDate = new Date();		//当前时间
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(dataDate);
			calendar.add(Calendar.DATE, 30); //加6表示一周
			dataDate = calendar.getTime();
			/**
			 * 兑换时间+30天与当前时间比较
			 * 大于当前时间则表示为距离上次兑换未超过一个月
			 * 小于则表示已超出一个月，也允许开始当月兑换
			 */
			if(dataDate.getTime() > currDate.getTime()){
				if(flowCard.getRiskCount() < 3){
					//修改兑换次数+1
					flowCard.setRiskCount(flowCard.getRiskCount()+1);
					int count = flowCardRiskDaos.updateByPrimaryKeySelective(flowCard);
					if (count == 1){
						isRigst = true;
					}else{
						isRigst = false;
					}
				}
			}else{
				flowCard.setRiskCount(1);
				int count = flowCardRiskDaos.updateByPrimaryKeySelective(flowCard);
				if (count == 1){
					isRigst = true;
				}else{
					isRigst = false;
				}
			}
		}
		return isRigst;
	}
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.portal.service.impl.IFlowCardUtilService#getFlowCardInfoAddCount(java.lang.String)
	 */
	public boolean getFlowCardInfoAddCount(String phone_no) throws ParseException{
		boolean isRigst = false;
		FlowCardRisk flowCard = flowCardRiskDaos.selectFlowCardRiskByPhone(phone_no);
		boolean a = checkDate(flowCard);
		if (a) {
			isRigst = true;
		}
		return isRigst;
	}
	
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.portal.service.impl.IFlowCardUtilService#chearCount(java.lang.String)
	 */
	public void chearCount(String userPhone){
		FlowCardRisk flowCard = flowCardRiskDaos.selectFlowCardRiskByPhone(userPhone);
		if(null == flowCard){
			logger.debug("卡劵，未加入风控----------");
		}else{
			flowCard.setRiskCount(flowCard.getRiskCount()-1);
			int a = flowCardRiskDaos.updateByPrimaryKeySelective(flowCard);
			logger.debug("流量+卡风控修改：----------"+a);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.portal.service.impl.IFlowCardUtilService#checkDate(com.yzx.flow.common.persistence.model.FlowCardRisk)
	 */
	@Transactional
	public boolean checkDate(FlowCardRisk flowCard) throws ParseException{
		Date dataDate = new SimpleDateFormat(DateUtil.YYYYMMDD_EN).parse(flowCard.getReceiveDate());;	//兑换时间
		Date currDate = new Date();		//当前时间
		boolean isRigst = false;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dataDate);
		calendar.add(Calendar.DATE, 30); //加6表示一周
		dataDate = calendar.getTime();
		/**
		 * 获取兑换日期与当前兑换时间比较
		 * 如果当前时间小于兑换日期+30天，则表示在一个月之内，允许兑换
		 * 如果大于+30天，则重新以当前时间为开始时间
		 */
		if(dataDate.getTime() > currDate.getTime()){
			int con = flowCard.getRiskCount();
			flowCard.setRiskCount(con);
			int count = flowCardRiskDaos.updateByPrimaryKeySelective(flowCard);
			if (count == 1){
				isRigst = true;
			}
		}else{
			String receiveDate = new SimpleDateFormat(DateUtil.YYYYMMDD_EN).format(new Date());
			flowCard.setReceiveDate(receiveDate);
			flowCard.setRiskCount(1);
			int count = flowCardRiskDaos.updateByPrimaryKeySelective(flowCard);
			if (count == 1){
				isRigst = true;
			}
		}
		return isRigst;
	}
	
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.portal.service.impl.IFlowCardUtilService#createWYFlowCardRisk(java.lang.String)
	 */
	@Transactional
	public boolean createWYFlowCardRisk(String phone_no){
		FlowCardRisk flowCard = new FlowCardRisk();
		flowCard.setMobileNo(phone_no);
		String receiveDate = new SimpleDateFormat(DateUtil.YYYYMMDD_EN).format(new Date());
		flowCard.setReceiveDate(receiveDate);
		flowCard.setRiskCount(1);
		int count = flowCardRiskDaos.insert(flowCard);
		return count == 1 ? true : false;
	}
	
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.portal.service.impl.IFlowCardUtilService#checkMobileOpr(java.lang.String)
	 */
	public String checkMobileOpr(String mobiles){
		String opr = "";
		String str = CheckPhone.getMobileOpr(mobiles);
		if("DX".equals(str)){
			opr = "中国电信";
		}else if("LT".equals(str)){
			opr = "中国联通";
		}else if("YD".equals(str)){
			opr = "中国移动";
		}else{
			opr = "获取运营商错误";
		}
		return opr;
	}
	
//	/**
//	 * //三层判断！客户状态、合作伙伴状态、订单状态，批次
//	 * @param flowCard
//	 * @return
//	 * @throws FOSSException
//	 * @throws IOException 
//	 * @throws JsonMappingException 
//	 * @throws JsonGenerationException 
//	 */
//	public WebResponseMsgJSON checkFlowCardInfo(FlowCardInfo flowCard) throws FOSSException, JsonGenerationException, JsonMappingException, IOException{
//		WebResponseMsgJSON result = new WebResponseMsgJSON();
//		//客户
//		CustomerInfo customerInfo = customerDao.getCustomerInfoByCustomerId(flowCard.getCustomId());
//		if(customerInfo.getStatus() == 1){
//			//合作伙伴
//			PartnerInfo partner = cardService.getPartnerInfoByPartnerId(customerInfo.getPartnerId());
//			if("1".equals(partner.getStatus())){
//				//批次
//				FlowCardBatchInfo flowCardBatch = cardService.getFlowCardBatchInfoById(flowCard.getBatchId());
//				//订单
//				OrderInfo orderInfo = cardService.getOrderInfoByBatchId(flowCardBatch.getBatchId());
//				if(orderInfo.getStatus() == 2){
//					result.setData(JSONObject.fromObject(new ObjectDao().writeValueAsString(orderInfo)));
//					return result;
//				}else{
//					result.setCode("005");
//					result.setMsg("订单未生效");
//				}
//			}else{
//				result.setCode("004");
//				result.setMsg("合作伙伴已停用");
//			}
//		}else{
//			result.setCode("003");
//			result.setMsg("客户已停用");
//		}
//		return result;
//	}
	
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.portal.service.impl.IFlowCardUtilService#getFlowExchangeLogByOrderId(java.lang.String)
	 */
	public FlowExchangeLog getFlowExchangeLogByOrderId(String orderId) throws BussinessException{
		if(orderId == null){
			throw new BussinessException(BizExceptionEnum.CUSTOMER_FORMAT_ERROR, "参数有误");
		}else{
			logger.debug("回调  外部订单ID--------------:"+orderId);
			FlowExchangeLog flowExchangeLog = flowExchangeLogDao.getFlowExchangeLogByFlowVoucherId(orderId);
			if(flowExchangeLog == null){
				throw new BussinessException(BizExceptionEnum.CUSTOMER_FORMAT_ERROR, "下发记录有误");
			}
			return flowExchangeLog;
		}
	}
	
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.portal.service.impl.IFlowCardUtilService#updateFlowExchangeLog(com.yzx.flow.common.persistence.model.FlowExchangeLog)
	 */
	@Transactional
	public int updateFlowExchangeLog(FlowExchangeLog flowExchangeLog){
		return flowExchangeLogDao.updateFlowExchangeStatus(flowExchangeLog);
	}
	
//	/**
//	 * 回调
//	 * @param request
//	 * @throws IOException
//	 */
//	@SuppressWarnings("static-access")
//	@Transactional
//	public FlowRespMesg callBack(HttpServletRequest request) throws IOException{
//		FlowRespMesg respMsg = new FlowRespMesg();
//		FlowRespMesgBody msgBody = new FlowRespMesgBody();
//		RespMsgBody msgResp = new RespMsgBody();
//
//		//输出流获取request请求json参数
//		BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
//        String line = null;
//        StringBuffer sb = new StringBuffer();
//        while ((line = br.readLine()) != null) {
//            sb.append(line);
//        }
//        br.close();
//        String appMsg=sb.toString();
//		logger.debug("exchange flow callback msg :"+appMsg);
//		FlowRespMesg callback = new com.alibaba.fastjson.JSONObject().parseObject(appMsg,FlowRespMesg.class);
//		
//		//判断Ip
//		String req_Ip = CommonUtil.getIp(request);
//		try {
//			boolean isFlag = false;
//			String CALLBACKIP = SystemConfigs.getInstance().getString("flow-card.callBackIp");
//			String[] strIp = CALLBACKIP.split(",");
//			for (String string : strIp) {
//				if(req_Ip.equals(string)){
//					isFlag = true;
//				}
//			}
//			if(isFlag){
//				//根据
//				JSONObject content = callback.getMsgbody().getCONTENT();
//				String orderId = content.getString("EXTORDER");
//				logger.debug("外部订单ID--------------:"+orderId);
//				
//				String exchangeOrderId = content.getString("ORDERID");
//				logger.debug("订单Id:"+exchangeOrderId);
//				
//				//查询请求回调的下发记录
//				FlowExchangeLog flowExchangeLog = getFlowExchangeLogByOrderId(orderId);
//				if(flowExchangeLog == null){
//					logger.error("网关回调通过orderId={}在FlowExchangeLog查找不到记录",orderId);
//					msgResp.setRCODE("99");
//					msgResp.setRMSG("请求回调对应下发记录无效");
//				}else{
//					String code = content.getString("CODE");
//					String msg = content.getString("STATUS");
//					logger.debug("*****回调请求Code:"+code+"\t msg："+msg);
//					if(!"00".equals(code)){
//						logger.debug("回调数据source_type:{}",flowExchangeLog.getSourceType());
//						//回调操作
//						//下发记录状态为0：网关收单成功
//						if("0".equals(flowExchangeLog.getFlag())){
//							if("00".equals(flowExchangeLog.getSourceType())){
//								/**
//								 * 流量+
//								 * 1、根据订单id修改下发记录日志的状态	成功为00，失败则对应请求回调的错误code
//								 * 2、修改兑换失败卡号的状态(允许再次兑换)
//								 * 3、流量卡兑换记录对应手机号码风控-1
//								 * 4、只允许一次请求回调有效
//								 */
//								msgResp = callBackFlowCard(flowExchangeLog,code);
//							}else if("01".equals(flowExchangeLog.getSourceType())){	
//								/**
//								 * 卡劵
//								 * 修改兑换失败卡劵的状态(允许再次兑换)
//								 * 流量卡兑换记录对应手机号码风控-1
//								 */
//								msgResp = callBackCardCoupons(flowExchangeLog,code);
//								//CP回调
//								CPCallBack(flowExchangeLog,code,msg);
//							}else if("02".equals(flowExchangeLog.getSourceType())){		
//								/**
//								 * 直充
//								 * 回滚账户余额
//								 * 直充兑换记录对应手机号码风控-1
//								 */
//								msgResp = callBackRechange(flowExchangeLog,code);
//							}else if("03".equals(flowExchangeLog.getSourceType())){
//								//流量江湖
//								msgResp = flowJiangHu(flowExchangeLog,code,msg,callback);
//							}else if("05".equals(flowExchangeLog.getSourceType())){
//								//foss 流量加
//								msgResp = flowJiangHu(flowExchangeLog,code,msg,callback);
//							}else if("06".equals(flowExchangeLog.getSourceType())){
//								//ACT活动
//								msgResp = eventCallBack(flowExchangeLog,code);
//							}else if("07".equals(flowExchangeLog.getSourceType())){
//								//深圳电信
//								msgResp = shenZhenCallBack(flowExchangeLog,code,msg,callback);
//							}else if("08".equals(flowExchangeLog.getSourceType())){
//								//批量充值
//								msgResp = rechargeBatchIssuedCallBack(flowExchangeLog,code);
//							}else if("09".equals(flowExchangeLog.getSourceType())){
//								//页面支付宝充值回调修改状态
//								msgResp = callBackZFB(flowExchangeLog, code);
//							}else if("10".equals(flowExchangeLog.getSourceType())){
//								msgResp = callBackTaoBao(flowExchangeLog, code);
//							}
//						}else{
//							msgResp.setRCODE("99");
//							msgResp.setRMSG("已经回调过了");
//						}
//					}else{
//						if("0".equals(flowExchangeLog.getFlag())){
//							if("01".equals(flowExchangeLog.getSourceType())){	
//								//CP回调
//								CPCallBack(flowExchangeLog,code,msg);
//							}
//							if("03".equals(flowExchangeLog.getSourceType())){	
//								//CP回调
//								flowJiangHu(flowExchangeLog,code,msg,callback);
//							}
//							if("06".equals(flowExchangeLog.getSourceType())){
//								//ACT活动
//								eventCallBack(flowExchangeLog,code);
//							}
//							if("07".equals(flowExchangeLog.getSourceType())){
//								//深圳电信
//								shenZhenCallBack(flowExchangeLog,code,msg,callback);
//							}
//							if("08".equals(flowExchangeLog.getSourceType())){
//								rechargeBatchIssuedCallBack(flowExchangeLog,code);
//							}
//							if("09".equals(flowExchangeLog.getSourceType())){
//								msgResp = callBackZFB(flowExchangeLog, code);
//							}
//							if("10".equals(flowExchangeLog.getSourceType())){
//								msgResp = callBackTaoBao(flowExchangeLog, code);
//							}
//							//修改下发记录状态
//							flowExchangeLog.setFlag(code);
//							updateFlowExchangeLog(flowExchangeLog);
//							//APP充值成功修改交易订单的状态为0
//							if("02".equals(flowExchangeLog.getSourceType())){
//								TraderInfo traderInfo = traderInfoDao.getTraderInfoByTraderId(flowExchangeLog.getSourceId());
//								if(traderInfo != null){
//									traderInfo.setStatus("0");		//成功
//									int rows = traderInfoDao.update(traderInfo);
//									logger.debug("APP直充成功状态修改为0影响行数："+rows);
//								}
//							}
//							//OK
//							msgResp.setRCODE("00");
//							msgResp.setRMSG("OK");
//						}else{
//							msgResp.setRCODE("99");
//							msgResp.setRMSG("已经回调过了");
//						}
//					}
//				}
//			}else{
//				//非法IP
//				logger.error("回调出错非法的IP["+req_Ip+"]请求。");
//				msgResp.setRCODE("999");
//				msgResp.setRMSG("非法请求："+req_Ip);
//			}
//		} catch (FOSSException e) {
//			logger.error("--portal--FOSSException--回调错误异常：："+e.getMsg(),e);
//			msgResp.setRCODE(e.getCode());
//			msgResp.setRMSG(e.getMsg());
//		}catch (Exception e){
//			logger.error("------回调错误异常：："+e.getMessage(),e);
//			msgResp.setRCODE("999");
//			msgResp.setRMSG("回调异常");
//		}
//		respMsg.setHeader(callback.getHeader());
//		msgBody.setRESP(msgResp);
//		respMsg.setMsgbody(msgBody);
//		logger.debug("*****callBack response---:"+JSONObject.fromObject(new ObjectDao().writeValueAsString(respMsg)));
//		return respMsg;
//	}
	
//	public RespMsgBody callBackTaoBao(FlowExchangeLog flowExchangeLog,String code) throws FOSSException{
//		RespMsgBody msgResp = new RespMsgBody();
//		flowExchangeLog.setFlag(code);
//		updateFlowExchangeLog(flowExchangeLog);
//		//根据sourceId获取对应的淘宝兑换订单
//		TaobaoOrderInfo taoiBaoOrderInfo = taobaoOrderInfoDao.getTaobaoOrderInfoByFlowVoucherId(flowExchangeLog.getSourceId());
//		if(null == taoiBaoOrderInfo){
//			logger.error("根据{}为找到对应的淘宝订单",flowExchangeLog.getSourceId());
//			throw new FOSSException("99", "数据异常");
//		}
//		if(taoiBaoOrderInfo.getStatus() != 2){
//			logger.error("{}对应淘宝订单状态不支持回调处理。状态为{}",taoiBaoOrderInfo.getFlowVoucherId(),taoiBaoOrderInfo.getStatus());
//			msgResp.setRCODE("01");
//			msgResp.setRMSG(taoiBaoOrderInfo.getFlowVoucherId()+"对应淘宝订单状态不支持回调处理。状态为"+taoiBaoOrderInfo.getStatus());
//			return msgResp;
//		}
//		if("00".equals(code)){
//			taoiBaoOrderInfo.setStatus(1);	//成功
//		}else{
//			taoiBaoOrderInfo.setStatus(3);	//成功
//		}
//		taoiBaoOrderInfo.setUpdateTime(new Date());
//		int rows = taobaoOrderInfoDao.updateByPrimaryKey(taoiBaoOrderInfo);
//		logger.debug("淘宝订单回调修改返回："+rows);
//		msgResp.setRCODE("00");
//		msgResp.setRMSG("OK");
//		return msgResp;
//	}
	
//	/**
//	 * 支付宝流量下发回调处理
//	 * @param flowExchangeLog
//	 * @param code
//	 * @return
//	 * @throws FOSSException
//	 */
//	public RespMsgBody callBackZFB(FlowExchangeLog flowExchangeLog,String code) throws FOSSException{
//		RespMsgBody msgResp = new RespMsgBody();
//		flowExchangeLog.setFlag(code);
//		updateFlowExchangeLog(flowExchangeLog);
//		
//		List<WXPayOrderInfo> wxPayOrderInfo = wxPayOrderInfoDao.selectInfoByFmpOrderId(flowExchangeLog.getSourceId());
//		if(wxPayOrderInfo.isEmpty()){
//			logger.error("兑换失败，数据异常。{}未找到对应的支付订单",flowExchangeLog.getSourceId());
//			throw new FOSSException("99", "兑换记录失效."+flowExchangeLog.getSourceId()+"未找到对应的支付订单");
//		}
//		if(wxPayOrderInfo.size() != 1){
//			throw new FOSSException("99", "兑换记录失效."+flowExchangeLog.getSourceId()+"找到多条对应的支付订单");
//		}
//		WXPayOrderInfo orderInfo = wxPayOrderInfo.get(0);
//		TraderInfo traderInfo = traderInfoDao.getTraderInfoByTraderId(orderInfo.getFmp_order_id());
//		if(null == traderInfo){
//			logger.error("根据订单Id{}获取TraderInfo信息错误...",orderInfo.getFmp_order_id());
//			throw new FOSSException("99","根据订单Id获取TraderInfo信息错误..."+orderInfo.getFmp_order_id());
//		}
//		//修改tradreInfo的状态
//		if("00".equals(code)){
//			traderInfo.setStatus("0");
//			orderInfo.setNeed_refund(0);	//0：不需要。1：需要
//			orderInfo.setSend_status(1);	//发送状态 0:未下发 1:下发成功 2:下发中 3:下发失败
//		}else{
//			traderInfo.setStatus(code);
//			orderInfo.setNeed_refund(1);	//0：不需要。1：需要
//			orderInfo.setIs_refund(0);		//退款标识 0:未退款 1:已退款
//			orderInfo.setSend_status(3);
//		}
//		traderInfoDao.update(traderInfo);
//		wxPayOrderInfoDao.update(orderInfo);
//		
//		msgResp.setRCODE("00");
//		msgResp.setRMSG("OK");
//		return msgResp;
//	}
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.portal.service.impl.IFlowCardUtilService#callBackFlowCard(com.yzx.flow.common.persistence.model.FlowExchangeLog, java.lang.String)
	 */
	@Transactional
	public RespMsgBody callBackFlowCard(FlowExchangeLog flowExchangeLog,String code) throws BussinessException{
		RespMsgBody msgResp = new RespMsgBody();
		//根据流量下发记录表流量卡Id查询对应流量卡信息
		FlowCardInfo flowCard = flowCardDao.selectByPrimaryKey(new Long(flowExchangeLog.getSourceId()));
		if(flowCard == null){
			logger.debug("***************根据下发记录的流量卡Id查询对应流量卡信息失败");
			throw new BussinessException(BizExceptionEnum.CUSTOMER_FORMAT_ERROR, "兑换记录失效");
		}else{
			//流量卡状态回滚
			flowCard.setCardState(2);
			int count = flowCardDao.updateByPrimaryKeySelective(flowCard);
			if(count != 1){
				logger.debug("***************流量卡状态回滚失败");
				throw new BussinessException(BizExceptionEnum.CUSTOMER_FORMAT_ERROR, "流量卡劵状态回滚失败");
			}else{
				//风控
				FlowProductExchangeRec flowProductExchangeRecInfo = new FlowProductExchangeRec();
				flowProductExchangeRecInfo.setCardNo(flowCard.getCardNo());
				flowProductExchangeRecInfo.setCardPass(flowCard.getCardPass());
				flowProductExchangeRecInfo = flowProductExchangeLogDao.getFlowProductExchangeRecByFlowCardInfo(flowProductExchangeRecInfo);
				if(flowProductExchangeRecInfo.getExchangeRecId() != null){
					this.chearCount(flowProductExchangeRecInfo.getMobileNo());
					flowExchangeLog.setFlag(code);
					updateFlowExchangeLog(flowExchangeLog);
					FlowProductExchangeRec flowCardLog = new FlowProductExchangeRec();
					flowCardLog.setCardNo(flowCard.getCardNo());
					flowCardLog.setCardPass(flowCard.getCardPass());
					flowCardLog = cardService.getFlowProductExchangeRecByFlowCard(flowCardLog);
					cardService.deleteExchangeLogById(flowCardLog.getExchangeRecId());
					msgResp.setRCODE("00");
					msgResp.setRMSG("OK");
				}else{
					logger.debug("***************根据流量卡劵信息查询对应的兑换记录ID失败："+flowProductExchangeRecInfo.getExchangeRecId());
				}
			}
		}
		return msgResp;
	}
	
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.portal.service.impl.IFlowCardUtilService#callBackCardCoupons(com.yzx.flow.common.persistence.model.FlowExchangeLog, java.lang.String)
	 */
	@Transactional
	public RespMsgBody callBackCardCoupons(FlowExchangeLog flowExchangeLog,String code) throws BussinessException{
		/**
		 * 1、本地处理网关回调，回滚兑换券状态
		 * 2、通知CP回调
		 */
		return callBackFlowCard(flowExchangeLog,code);
	}
	
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.portal.service.impl.IFlowCardUtilService#callBackRechange(com.yzx.flow.common.persistence.model.FlowExchangeLog, java.lang.String)
	 */
	public RespMsgBody callBackRechange(FlowExchangeLog flowExchangeLog,String code) throws BussinessException{
		RespMsgBody respMsg = new RespMsgBody();
		/**
		 * 根据下发记录信息查询APP直充交易记录
		 * 下发记录表本地流水号关联APP订单交易表中的orderId
		 */
		TraderInfo traderInfo = traderInfoDao.getTraderInfoByTraderId(flowExchangeLog.getSourceId());
		if(traderInfo == null){
			throw new BussinessException(BizExceptionEnum.CUSTOMER_FORMAT_ERROR, "充值订单无效");
		}else{
			//根据充值对应的客户ID进行账户回滚
			CustomerInfo customerInfo = new CustomerInfo();
			customerInfo = customerDao.getCustomerInfoByCustomerId(traderInfo.getUser_id());
			if(customerInfo.getCustomerId() == null){
				throw new BussinessException(BizExceptionEnum.CUSTOMER_FORMAT_ERROR, "客户信息有误");
			}else{
				flowExchangeLog.setFlag(code);
				updateFlowExchangeLog(flowExchangeLog);
				//修改交易订单状态为错误code
				if(traderInfo != null){
					traderInfo.setStatus(code);
					int rows = traderInfoDao.update(traderInfo);
					logger.debug("APP直充成功状态修改为错误code影响行数："+rows);
				}
				respMsg.setRCODE("00");
				respMsg.setRMSG("OK");
			}
		}
		//traderInfo交易记录表数据修改成功后判断是否是批量充值
		if("00".equals(respMsg.getRCODE()) && "08".equals(flowExchangeLog.getSourceType())){
			FlowBatchRecord  flowBatchRecord  = flowBatchRecordDao.getFlowBatchRecordByOrderId(traderInfo.getOrderid());
			if(flowBatchRecord == null){
				logger.error("批量充值  回调失败      -- FlowBatchRecord为找到对应数据");
				respMsg.setRCODE("002");
				respMsg.setRMSG("批量充值  回调失败      -- FlowBatchRecord为找到对应数据");
			}else{
				if("00".equals(code)){
					flowBatchRecord.setStatus(Constant.FLOW_ISSUED_STATUS_SUCCESS);	
				}else{
					flowBatchRecord.setStatus(Constant.FLOW_ISSUED_STATUS_FAILED);	
				}
				int count = flowBatchRecordDao.updateByPrimaryKey(flowBatchRecord);
				logger.debug("批量充值回调返回影响行数："  +count);
				respMsg.setRCODE("00");
				respMsg.setRMSG("OK");
			}
		}
		return respMsg;
	}
	
//	@SuppressWarnings("static-access")
//	public WebResponseMsgJSON getMobileInfo(String mobile){
//		//手机号码归属地
//		String GAINOPERATOR = SystemConfigs.getInstance().getString("flow-card.gainOperator");
//		WebResponseMsgJSON result = new WebResponseMsgJSON();
//		String dispatch_url = GAINOPERATOR+mobile;		
//		String str = HttpClientUtil.sendDataHttpsViaGet(dispatch_url);
//		result = new com.alibaba.fastjson.JSONObject().parseObject(str,WebResponseMsgJSON.class);
//		return result;
//	}
	
//	/**
//	 * 卡片信息校验
//	 * @throws FOSSException 
//	 * @throws IOException 
//	 * @throws JsonMappingException 
//	 * @throws JsonGenerationException 
//	 */
//	public WebResponseMsgJSON getFlowCard(FlowCardInfo flowCard) throws FOSSException, JsonGenerationException, JsonMappingException, IOException{
//		WebResponseMsgJSON result = new WebResponseMsgJSON();
//		try{
//			//验证卡有效性
//			result = checkFlowCardInfo(flowCard);
//			if("00".equals(result.getCode())){
//				result.setData(null);
//				FlowProductExchangeRec flowCardLog = new FlowProductExchangeRec();
//				flowCardLog.setCardNo(flowCard.getCardNo());
//				flowCardLog.setCardPass(flowCard.getCardPass());
//				flowCardLog = cardService.getFlowProductExchangeRecByFlowCard(flowCardLog);
//				if(flowCardLog != null){
//					///修改当前兑换卡的状态为已兑换 
//					flowCard.setCardState(3);
//					this.flowCardDao.updateByPrimaryKeySelective(flowCard);
//					result.setCode("03");
//					result.setMsg("当前卡已被兑换");
//					return result;
//				}else{
//					if(flowCard.getCardState() == 1){	
//						result.setCode("05");
//						result.setMsg("卡片未激活");
//						return result;
//					}else if(flowCard.getCardState() == 3){
//						result.setCode("03");
//						result.setMsg("卡片已兑换，不能重复兑换");
//						return result;
//					}else if(flowCard.getCardState() == 4){
//						result.setCode("06");
//						result.setMsg("卡片已锁定，不能兑换");
//						return result;
//					}else if(flowCard.getCardState() == 5){
//						result.setCode("07");
//						result.setMsg("卡片已过期，不能兑换");
//						return result;
//					}else if(flowCard.getCardState() == 6){
//						result.setCode("08");
//						result.setMsg("卡片已作废，不能兑换");
//						return result;
//					}else if(flowCard.getCardExp() != null){
//						if(flowCard.getCardExp().getTime() < new Date().getTime()){
//							result.setCode("07");
//							result.setMsg("卡片已过期");
//							return result;
//						}
//					}
//				}
//			}
//		}catch (FOSSException e){
//			result.setCode(e.getCode());
//			result.setMsg(e.getMsg());
//		}
//		return result;
//	}
	
//	public void CPCallBack(FlowExchangeLog flowExchangeLog,String code,String msg) throws JsonGenerationException, JsonMappingException, IOException{
//		FlowRespMesg respMsg = new FlowRespMesg();
//		FlowMesgHeader msgHeader = new FlowMesgHeader();
//		FlowRespMesgBody resp = new FlowRespMesgBody();
//		RespMsgBody rMsg = new RespMsgBody();
//		try {
//			//来源找流量卡劵
//			FlowCardInfo flowCard = flowCardDao.selectByPrimaryKey(new Long(flowExchangeLog.getSourceId()));
//			logger.debug("*******流量卡信息："+flowCard.getCardPass());
//			//根据卡号找到订单明细中FMP本地流水号及CP流水号
//			OrderDetail orderDetail = orderDetailDao.selectByPrimaryKey(flowExchangeLog.getOrderDetailId());
//			logger.debug("*******订单明细："+orderDetail.getOrderDetailId());
//			//APP接入信息
//			FlowAppInfo flowApp = new FlowAppInfo();
//			flowApp.setCustomerId(flowCard.getCustomId());
//			flowApp.setOrderId(orderDetail.getOrderId());
//			List<FlowAppInfo> flowAppList = flowAppInfoDao.getFlowAppInfoByOrderIdAndCustomerId(flowApp);
//			if(flowAppList == null || flowAppList.isEmpty()){
//				return ;
//			}
//			logger.debug("------appID:"+flowAppList.get(0).getAppId());
//			//判断CP是否有回调
//			flowApp = flowAppList.get(0);
//			if(flowApp.getExchangeCbUrl() != null){
//				logger.debug("**********回调地址不能为空："+flowApp.getExchangeCbUrl());
//				msgHeader.setAPPID(flowApp.getAppId());
//				String date = new SimpleDateFormat(DateUtil.YYYYMMDDHHMMSS_EN).format(new Date());
//				msgHeader.setTIMESTAMP(date);
//				msgHeader.setVERSION("V1.0");
//				msgHeader.setSEQNO(UUID.randomUUID().toString().replace("-","")+"");
//				String md5 = com.szwisdom.common.encoder.MD5Util.MD5(msgHeader.getTIMESTAMP()+msgHeader.getSEQNO()+msgHeader.getAPPID()+flowAppList.get(0).getAppKey());
//				msgHeader.setSECERTKEY(md5);
//				respMsg.setHeader(msgHeader); 	//拼接頭部
//				//根据批次找CP本地流水
//				FlowCardBatchInfo cardBatch = flowCardBatchDao.selectByPrimaryKey(flowCard.getBatchId());
//				Map<String, Object> map = new HashMap<String, Object>();
//				map.put("ORDERID", orderDetail.getOrderId());
//				map.put("EXTORDER", cardBatch.getExtOrder());
//				map.put("CARDPASS", flowCard.getCardPass());
//				map.put("CARDSTATUS", code);
//				String sign = com.szwisdom.common.encoder.MD5Util.MD5(flowApp.getAppKey()+orderDetail.getOrderId()+cardBatch.getExtOrder()+flowCard.getCardPass()+code);
//				map.put("SIGN", sign);
//				resp.setCONTENT(JSONObject.fromObject(new ObjectDao().writeValueAsString(map)));
//				//判断code
//				if("00".equals(code)){
//					//OK
//					rMsg.setRCODE("00");
//					rMsg.setRMSG("OK");
//				}else{
//					//失败
//					rMsg.setRCODE(code);
//					rMsg.setRMSG(msg);
//				}
//				resp.setRESP(rMsg);
//				respMsg.setMsgbody(resp);
//				//回調CP
//				JSONObject json = JSONObject.fromObject(new ObjectDao().writeValueAsString(respMsg));
//				
//				logger.debug("*******CP回调Json："+json);
//				logger.debug("*******CP回调URL："+flowApp.getExchangeCbUrl());
//				String back = HttpClientUtil.sendData(json.toString(), flowApp.getExchangeCbUrl());
//				logger.debug("------CP回调返回："+back);
//			}else{
//				return ;
//			}
//		} catch (Exception e) {
//			logger.error("CP回调失败。。。。"+e.getMessage(),e);
//		}
//	}
//	
//	/**
//	 * 江湖回调
//	 * @throws IOException 
//	 * @throws JsonMappingException 
//	 * @throws JsonGenerationException 
//	 */
//	public RespMsgBody flowJiangHu(FlowExchangeLog flowExchangeLog,String code,String msg,FlowRespMesg callback) throws JsonGenerationException, JsonMappingException, IOException{
//		RespMsgBody respMsg = new RespMsgBody();
//		try {
//			//修改下发记录的状态
//			flowExchangeLog.setFlag(code);
//			int rows = updateFlowExchangeLog(flowExchangeLog);
//			if(rows == 1){
//				Map<String, Object> map = new HashMap<String,Object>();
//				map.put("ORDERID", flowExchangeLog.getExchangeOrderId());
//				map.put("EXTORDER", flowExchangeLog.getSourceId());
//				map.put("STATUS", msg);
//				map.put("CODE", code);
//				FlowRespMesgBody body = new FlowRespMesgBody();
//				body.setCONTENT(JSONObject.fromObject(new ObjectDao().writeValueAsString(map)));
//				callback.setMsgbody(body);
//				String sendJson = JSONObject.fromObject(new ObjectDao().writeValueAsString(callback)).toString();
//				logger.debug("FOSS 回调 数据："+sendJson);
//				String callBackUrl = SystemConfigs.getInstance().getString("flow-card.fossCallBack");
//				HttpClientUtil.sendData(sendJson, callBackUrl);
//				logger.debug("------   回调FOSS------");
//				respMsg.setRCODE("00");
//				respMsg.setRMSG("OK");
//			}else{
//				logger.error("------流量江湖兑换下发记录状态修改失败");
//				respMsg.setRCODE("99");
//				respMsg.setRMSG("FOSS 兑换下发记录状态修改失败");
//			}
//		} catch (Exception e) {
//			logger.error("FOSS 系统回调错误："+e.getMessage(),e);
//			respMsg.setRCODE("99");
//			respMsg.setRMSG("FOSS 系统回调错误");
//		}
//		return respMsg;
//	}
//	
//	/**
//	 * 大好大活动回调，修改奖金池
//	 * @param flowExchangeLog
//	 * @param code
//	 */
//	public RespMsgBody eventCallBack(FlowExchangeLog flowExchangeLog,String code){
//		RespMsgBody respMsg = new RespMsgBody();
//		try {
//			logger.debug("流量网关回调活动FlowVoucherId[" + flowExchangeLog.getSourceId() + "],code=" + code);
//			campaignRewardInfoService.myCardexchangeNum(flowExchangeLog.getSourceId(), code);
//			flowExchangeLog.setFlag(code);
//			int rows = updateFlowExchangeLog(flowExchangeLog);
//			logger.debug("act 活动回调 兑换状态修改："+rows);
//			respMsg.setRCODE("00");
//			respMsg.setRMSG("OK");
//			if (!"00".equals(code)){
//				CampaignRewardInfo  campaignRewardInfo = campaignRewardDao.getflowVoucherIdRewObj(flowExchangeLog.getSourceId());
//				if (null != campaignRewardInfo){
//					int result = signPointsRecordDao.deleteByRewardId(campaignRewardInfo.getRewardId());
//					logger.info("签到流量下发失败code" + code + "，作回滚,rewardId=" + campaignRewardInfo.getRewardId() + "回滚签到记录表记录数=" + result);
//				}
//			}
//			
//			/*CampaignRewardInfo record = new CampaignRewardInfo();
//			record.setFlowVoucherId(flowExchangeLog.getSourceId());
//			if ("00".equals(code)){
//				record.setStatus(CampaignRewardInfo.STATUS_SEND_SUCC);
//			}else{
//				record.setStatus(CampaignRewardInfo.STATUS_SEND_FAIL);
//				String actUrl = SystemConfigs.getInstance().getActCallBackUrl();
//				logger.debug("URL:"+actUrl+flowExchangeLog.getSourceId());
//				String number = HttpClientUtil.sendDataHttpsViaGet(actUrl+flowExchangeLog.getSourceId());
//				logger.debug("请求 act 修改次数 return :"+number);
//			}
//			campaignRewardDao.updateCampaignStatus(record);*/
//		} catch (Exception e) {
//			logger.error("活动流量下发失败， 活动奖金池UPDATE发生异常："+e.getMessage(),e);
//			respMsg.setRCODE("99");
//			respMsg.setRMSG("活动流量下发失败， 活动奖金池UPDATE发生异常");
//		}
//		return respMsg;
//	}
//	
//	/**
//	 * 深圳电信回调
//	 *  修改兑换日志状态为网关回调对应状态码
//	 *  回调深圳电信url
//	 * @param flowExchangeLog
//	 * @param code
//	 * @return
//	 * @throws IOException 
//	 * @throws JsonMappingException 
//	 * @throws JsonGenerationException 
//	 */
//	public RespMsgBody shenZhenCallBack(FlowExchangeLog flowExchangeLog,String code,String msg,FlowRespMesg callback) throws JsonGenerationException, JsonMappingException, IOException{
//		RespMsgBody respMsg = new RespMsgBody();
//		try {
//			flowExchangeLog.setFlag(code);
//			int rows = updateFlowExchangeLog(flowExchangeLog);
//			if(rows == 1){
//				Map<String, Object> map = new HashMap<String,Object>();
//				map.put("ORDERID", flowExchangeLog.getExchangeOrderId());
//				map.put("EXTORDER", flowExchangeLog.getSourceId());
//				map.put("STATUS", msg);
//				map.put("CODE", code);
//				FlowRespMesgBody body = new FlowRespMesgBody();
//				body.setCONTENT(JSONObject.fromObject(new ObjectDao().writeValueAsString(map)));
//				callback.setMsgbody(body);
//				String sendJson = JSONObject.fromObject(new ObjectDao().writeValueAsString(callback)).toString();
//				logger.debug("深圳电信 回调 数据："+sendJson);
//				String callBackUrl = SystemConfigs.getInstance().getString("flow-card.sztelCallBack");
//				logger.debug("深圳电信回调URL："+callBackUrl);
//				HttpClientUtil.sendData(sendJson, callBackUrl);
//				logger.debug("------   回调FOSS [深圳电信回调]------");
//				respMsg.setRCODE("00");
//				respMsg.setRMSG("OK");
//			}else{
//				logger.debug("------深圳电信兑换下发记录状态修改失败");
//				respMsg.setRCODE("99");
//				respMsg.setRMSG("FOSS 深圳电信 兑换下发记录状态修改失败");
//			}
//		} catch (Exception e) {
//			logger.error("------深圳电信兑换下发回调失败:"+e.getMessage(),e);
//			respMsg.setRCODE("99");
//			respMsg.setRMSG("FOSS 深圳电信兑换下发回调失败");
//		}
//		return respMsg;
//	}
//	
//	/**
//	 * 批量充值回调处理
//	 * 流量批量下发记录  的状态
//	 * 流量兑换日志表 flag
//	 */
//	public RespMsgBody rechargeBatchIssuedCallBack(FlowExchangeLog flowExchangeLog,String code){
//		RespMsgBody respMsg = new RespMsgBody();
//		try {
//			respMsg = callBackRechange(flowExchangeLog,code);
//		} catch (FOSSException e) {
//			respMsg.setRCODE(e.getCode());
//			respMsg.setRMSG(e.getMsg());
//		}
//		return respMsg;
//	}
	
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.portal.service.impl.IFlowCardUtilService#setFlowDispatcherUrl(java.lang.String)
	 */
	@Value("${flow.dispatcher.url}")
	public void setFlowDispatcherUrl(String flowDispatcherUrl) {
		FLOW_DISPATCHER_URL = flowDispatcherUrl;
	}
}

