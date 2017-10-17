package com.yzx.flow.modular.portal.service;

import java.text.ParseException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import com.yzx.flow.common.exception.BussinessException;
import com.yzx.flow.common.persistence.model.FlowCardRisk;
import com.yzx.flow.common.persistence.model.FlowExchangeLog;
import com.yzx.flow.common.persistence.model.RespMsgBody;

public interface IFlowCardUtilService {

	/**
	 * 验证风控
	 * @param userPhone
	 * @return
	 * @throws ParseException
	 */
	boolean isRegist(String userPhone) throws ParseException;

	/**
	 * 根据手机号码判断风控
	 * @param phone_no
	 * @return
	 * @throws ParseException
	 */
	boolean getFlowCardInfo(String phone_no) throws ParseException;

	/**
	 * 流量下发成功则风控手机号码兑换次数+1
	 * @param phone_no
	 * @return
	 * @throws ParseException
	 */
	boolean getFlowCardInfoAddCount(String phone_no) throws ParseException;

	/**
	 * 下发失败，风控-1
	 * @param userPhone
	 */
	void chearCount(String userPhone);

	/**
	 * 检查日期
	 * @param flowCard
	 * @return
	 * @throws ParseException
	 */
	boolean checkDate(FlowCardRisk flowCard) throws ParseException;

	/**
	 * 新增风控次数
	 * @param phone_no
	 * @return
	 */
	boolean createWYFlowCardRisk(String phone_no);

	/**
	 * 或手机号码运营商
	 * @param mobiles
	 * @return
	 */
	String checkMobileOpr(String mobiles);

	/**
	 * 根据外部订单查询对应的下发记录
	 * @param orderId
	 * @return
	 * @throws BussinessException
	 */
	FlowExchangeLog getFlowExchangeLogByOrderId(String orderId) throws BussinessException;

	/**
	 *  更新下发记录日志状态
	 * @param flowExchangeLog
	 * @return
	 */
	int updateFlowExchangeLog(FlowExchangeLog flowExchangeLog);

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
	/**
	 * 回调流量卡
	 * @throws BussinessException 
	 */
	RespMsgBody callBackFlowCard(FlowExchangeLog flowExchangeLog, String code) throws BussinessException;

	/**
	 * 回调卡劵
	 * @throws BussinessException 
	 */
	RespMsgBody callBackCardCoupons(FlowExchangeLog flowExchangeLog, String code) throws BussinessException;

	/**
	 * 回调APP直充
	 * @throws BussinessException 
	 */
	RespMsgBody callBackRechange(FlowExchangeLog flowExchangeLog, String code) throws BussinessException;

	/**
	 * 注入静态资源
	 * @param flowDispatcherUrl
	 */
	void setFlowDispatcherUrl(String flowDispatcherUrl);

}