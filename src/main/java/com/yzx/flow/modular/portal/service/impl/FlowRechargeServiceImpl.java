package com.yzx.flow.modular.portal.service.impl;

import java.util.Collections;
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

import com.yzx.flow.common.exception.BizExceptionEnum;
import com.yzx.flow.common.exception.BussinessException;
import com.yzx.flow.common.persistence.model.CustomerInfo;
import com.yzx.flow.common.persistence.model.FlowAppInfo;
import com.yzx.flow.common.persistence.model.FlowExchangeLog;
import com.yzx.flow.common.persistence.model.FlowMakeOrderReqMesg;
import com.yzx.flow.common.persistence.model.FlowPackage;
import com.yzx.flow.common.persistence.model.FlowPackageInfo;
import com.yzx.flow.common.persistence.model.FlowProductInfo;
import com.yzx.flow.common.persistence.model.FossFlowMakeBack;
import com.yzx.flow.common.persistence.model.OrderInfo;
import com.yzx.flow.common.persistence.model.PartnerInfo;
import com.yzx.flow.common.persistence.model.TraderInfo;
import com.yzx.flow.common.util.CommonUtil;
import com.yzx.flow.common.util.Constant;
import com.yzx.flow.core.shiro.ShiroKit;
import com.yzx.flow.core.shiro.ShiroUser;
import com.yzx.flow.core.support.HttpUtil;
import com.yzx.flow.core.util.CheckPhone;
import com.yzx.flow.modular.customer.service.ICustomerInfoService;
import com.yzx.flow.modular.partner.service.IPartnerService;
import com.yzx.flow.modular.portal.service.IFlowCardUtilService;
import com.yzx.flow.modular.portal.service.IFlowRechargeService;
import com.yzx.flow.modular.portal.service.IProvinceChannelPoolService;
import com.yzx.flow.modular.system.dao.FlowAppInfoDao;
import com.yzx.flow.modular.system.dao.FlowExchangeLogDao;
import com.yzx.flow.modular.system.dao.FlowPackageDao;
import com.yzx.flow.modular.system.dao.FlowProductInfoDao;
import com.yzx.flow.modular.system.dao.OrderInfoDao;
import com.yzx.flow.modular.system.dao.TraderInfoDao;

@Service
public class FlowRechargeServiceImpl implements IFlowRechargeService {

	private static final Logger logger = LoggerFactory.getLogger(FlowRechargeServiceImpl.class);
	
	
	
	@Autowired
	private OrderInfoDao orderDao;
	
	@Autowired
	private ICustomerInfoService customerInfoService;
	
	@Autowired
	private IPartnerService partnerService;
	
	@Autowired
	private IProvinceChannelPoolService provinceChannelPoolService;
	
	@Autowired
	private FlowPackageDao flowPackageDao;
	
	@Autowired
	private FlowAppInfoDao flowAppdao;
	
	@Autowired
	private TraderInfoDao traderDao;
	
	@Autowired
	private FlowProductInfoDao flowProductDao;
	
	@Autowired
	private IFlowCardUtilService flowCardUtilService;
	
	@Autowired
	private FlowExchangeLogDao flowExchangeLogDao;
	

	
	
	
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.portal.service.impl.ss#getFlowPackageInfo(java.lang.Long, java.lang.String, java.lang.String, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public List<FlowPackage> getFlowPackageInfo(Long customerId, String userMobile, String code, String isCombo) throws BussinessException {
    	//获取客户信息
    	CustomerInfo customer = null;
    	if ( customerId == null || customerId.compareTo(0L) <= 0 || (customer = customerInfoService.get(customerId)) == null ) 
    		throw new BussinessException(BizExceptionEnum.CUSTOMER_FORMAT_ERROR, "非法客户");
    	
		if( !Constant.CUSTOMER_STATUS_ON.equals(customer.getStatus()) )		//客户为商用状态
			throw new BussinessException(BizExceptionEnum.CUSTOMER_FORMAT_ERROR, "客户状态不可用");
		
		if ( customer.getPartnerId() != null && customer.getPartnerId() > 0L ) {
			PartnerInfo partnerInfo = partnerService.get(customer.getPartnerId());
			if( partnerInfo == null || !PartnerInfo.STATUS_OK.equals(partnerInfo.getStatus()) )
	            throw new BussinessException(BizExceptionEnum.CUSTOMER_FORMAT_ERROR, "合作伙伴不存在或状态无效");
		}
		
		//根据合作伙伴ID查询产品列表
		List<FlowPackage> flowPackage = getFlowProductList(userMobile,customer.getCustomerId(), code, isCombo);
		if(flowPackage == null || flowPackage.isEmpty()){
			throw new BussinessException(BizExceptionEnum.CUSTOMER_FORMAT_ERROR, "当前无可选流量包");
		}
		return flowPackage;
	}
	
	
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.portal.service.impl.ss#getFlowProductList(java.lang.String, java.lang.Long, java.lang.String, java.lang.String)
	 */
	public List<FlowPackage> getFlowProductList(String phone, Long customerId, String code, String isCombo) throws BussinessException{
		String pos = CheckPhone.getMobileOpr(phone);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userId", customerId);
		map.put("pos", pos);
		map.put("code", code);
		//验证是否是月末两天
		boolean flag = provinceChannelPoolService.selectProvinceChannelPoolInfo(map);
		int i = 0;
		if(FlowPackageInfo.BASIC_PACKAGE.equals(isCombo)  && flag){
			i = 1;
			map.put("type", "2");
		}else{
			map.put("type", "1");
		}
		map.put("isCombo", isCombo);
		List<FlowPackage> flowPackeage = flowPackageDao.getFlowRechargeByCode(map);
		if(flowPackeage == null || flowPackeage.isEmpty()){
			if ( i == 0 ) {// 暂不清楚i的用处。此处的目的是 当查询制定区域的流量包没有时，不去查询全国区域的流量包
				throw new BussinessException(BizExceptionEnum.CUSTOMER_FORMAT_ERROR, "无对应的充值产品,请联系客服！");
			}
			flowPackeage = selectFlowPackeage(customerId, pos, code,i,isCombo);
			if(flowPackeage.isEmpty()){
				throw new BussinessException(BizExceptionEnum.CUSTOMER_FORMAT_ERROR, "无对应的充值产品,请联系客服！");
			}
			for (FlowPackage flowPackage : flowPackeage) {
				flowPackage.setCarrieroperator("");
				flowPackage.setProductId(0L);
			}
			return flowPackeage;
		}else{
			for (FlowPackage flowPackage : flowPackeage) {
				flowPackage.setCarrieroperator("");
				flowPackage.setProductId(0L);
			}
			return flowPackeage;
		}
	}
	
	
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.portal.service.impl.ss#selectFlowPackeage(java.lang.Long, java.lang.String, java.lang.String, int, java.lang.String)
	 */
	public List<FlowPackage> selectFlowPackeage(Long customerId,String pos,String areaCode,int i,String isCombo) {
		Map<String, Object> map = new HashMap<String,Object>();
		map.put("userId", customerId);
		map.put("pos", pos);
		if(i == 0){
			map.put("code", "00");
		}else{
			if(StringUtils.isEmpty(areaCode)){
				map.put("code", "00");
			}else{
				map.put("code", areaCode);
			}
		}
		map.put("type", "1");
		map.put("isCombo", isCombo);
		List<FlowPackage> flowPackeage = flowPackageDao.getFlowRechargeByCode(map);
		if(flowPackeage == null || flowPackeage.isEmpty()){
			map.put("code", "00");
			map.put("type", "1");
			flowPackeage = flowPackageDao.getFlowRechargeByCode(map);
//			flowPackeage = selectFlowPackeage(customerId, pos, null,i);
		}
		return flowPackeage;
	}
	
	
	
	public void flowRecharge(Long customerId, String mobile, String packageId, String isCombo) throws BussinessException{
		if ( !CheckPhone.isMobileNO(mobile) ) {
			throw new BussinessException(BizExceptionEnum.CUSTOMER_FORMAT_ERROR, "手机号码格式不正确");
		}
		CustomerInfo customer = null;
		if ( customerId == null || customerId.compareTo(0L) <= 0 
				|| ( customer = customerInfoService.get(customerId)) == null ) {
			throw new BussinessException(BizExceptionEnum.CUSTOMER_FORMAT_ERROR, "非法客户");
		}
		
    	//根据客户id查询对对应app接入getOrderInfoByOrderTypeAndCustomerId
    	FlowAppInfo appInfo = flowAppdao.getOrderInfoByOrderTypeAndCustomerId(customer.getCustomerId());
    	
    	if ( appInfo == null ) 
    		throw new BussinessException(BizExceptionEnum.CUSTOMER_FORMAT_ERROR, "接入网关错误");
    	
		Integer rechargeType = Constant.RECHARGE_TYPE_BASE_PACKAGE;
		if (FlowPackageInfo.HF_PACKAGE.equals(isCombo)){
			rechargeType = 4;
		}
		
		FlowPackage flowPack = getFlowRecharge(packageId, customer, rechargeType);
		double money = ((customer.getBalance().add(customer.getCreditAmount())).subtract(customer.getCurrentAmount())).doubleValue();
		
		logger.debug("账户余额："+customer.getBalance()+"\t 授信额度："+customer.getCreditAmount()+"\t 当前消费："+customer.getCurrentAmount());
		logger.debug("当前用户账户可用余额："+money);
		
		if((money <= 0) || (money - flowPack.getRealprice() < 0))
			throw new BussinessException(BizExceptionEnum.CUSTOMER_FORMAT_ERROR, "账户余额不足，请联系客服");
		
		TraderInfo traderinfo = new TraderInfo();
		traderinfo.setTime(new Date());
		traderinfo.setFlowamount(flowPack.getFlowamount());
		traderinfo.setTrader_ip(CommonUtil.getIp(HttpUtil.getRequest()));
		traderinfo.setOrderid(customer.getCustomerId()+new Date().getTime()+ "");
		traderinfo.setSaleprice(flowPack.getSaleprice());
		traderinfo.setProductname(flowPack.getProductname());
		traderinfo.setProduct_id(flowPack.getProductId());
		traderinfo.setRealprice(flowPack.getRealprice());
		traderinfo.setStatus("2");
		traderinfo.setUser_id(customer.getCustomerId());
		traderinfo.setUsermobile(mobile);
		traderinfo.setSingnature("页面充值");
		traderinfo.setOrder_detail_id(flowPack.getOrderDetailId());
		
		int rows = traderDao.insert(traderinfo);
		if(rows == 1){
			String orderType = "1";
			if (FlowPackageInfo.HF_PACKAGE.equals(isCombo)){
				orderType = "3";
			}
			int count = flowIssued(traderinfo, appInfo, "02", orderType);
			if(count != 0){
				throw new BussinessException(BizExceptionEnum.CUSTOMER_FORMAT_ERROR, "流量暂时无法充入该手机");
			}
		}
	}
	
	
	
	public int flowIssued(TraderInfo traderInfo, FlowAppInfo appInfo, String type, String orderType) {
		boolean isExchange = true;
		final FlowMakeOrderReqMesg order = new FlowMakeOrderReqMesg();
		//根据产品ID获取流量包Id
		FlowProductInfo flowProductInfo = flowProductDao.getFlowProductInfoByProductId(traderInfo.getProduct_id());
		if(flowProductInfo.getPackageId() == null){
			logger.debug("********APP流量下发对应流量包Id："+flowProductInfo.getPackageId());
			return 1;
		}
		
		//新增流量下发记录
		FlowExchangeLog flowLog = new FlowExchangeLog();
		//OK,下发日志记录
		String opr = CheckPhone.getMobileOpr(traderInfo.getUsermobile());
		flowLog.setMobileOperator(opr);
		flowLog.setFlowVoucherId(create_randomId(32));
		flowLog.setMobile(traderInfo.getUsermobile());
		flowLog.setItemCount(1);
		flowLog.setCreateTime(new Date());
		flowLog.setProductId(traderInfo.getProduct_id());
		flowLog.setOrderDetailId(traderInfo.getOrder_detail_id());
		flowLog.setOrgActiveId(0L);
		flowLog.setMobileHome("");
		flowLog.setSourceId(traderInfo.getOrderid());
		flowLog.setSourceType(type);
		
		try {
			order.setEXTORDER(flowLog.getFlowVoucherId());
			order.setPACKAGEID(flowProductInfo.getPackageId());//流量包Id
			order.setUSER(traderInfo.getUsermobile());
			if (StringUtils.isBlank(orderType)){
				orderType = "1";
			}
			order.setORDERTYPE(orderType);
			order.setNOTE("");
			
			FossFlowMakeBack rewardSeqNo = FlowCardUtilServiceImpl.dispatchFlowByCard(order, appInfo.getAppId(), appInfo.getAppKey());
			
			if ("0".equals(rewardSeqNo.getCode())){
				flowLog.setExchangeOrderId(rewardSeqNo.getOrderId());
				flowLog.setFlag("0");
			}else{
				isExchange = false;
				flowLog.setFlag("1");
				flowLog.setExchangeOrderId("");
				//订单修改状态失败
				traderInfo.setStatus("3");
				int row = traderDao.update(traderInfo);
				logger.debug("流量下发失败交易订单修改返回影响行数："+row);
			}
		} catch (Exception e) {
			logger.error("流量下发异常错误**************："+e.getMessage(),e);
			flowLog.setFlag("1");
			flowLog.setExchangeOrderId("");
			isExchange = false;
		}
		flowLog.setCreateTime(new Date());
		flowExchangeLogDao.insert(flowLog);	//新增下发记录
		if(isExchange){
	        return 0;//已充入该手机
		}else{
			return 1;//暂时无法充入该手机
		}
	}
	
	
	
	public FlowPackage getFlowRecharge(String packageId, CustomerInfo userInfo, Integer rechargeType) throws BussinessException{
		
		if(packageId == null || "".equals(packageId))
			throw new BussinessException(BizExceptionEnum.CUSTOMER_FORMAT_ERROR, "流量包Id为空");
		
		if(userInfo == null)
			throw new BussinessException(BizExceptionEnum.CUSTOMER_FORMAT_ERROR, "用户为空");
		
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("userId", userInfo.getCustomerId());
		map.put("packageId", packageId);
		map.put("rechargeType", rechargeType);
		List<FlowPackage> flowPack = flowPackageDao.getFlowRecharge(map);
		
		if(flowPack == null || flowPack.isEmpty()){
			throw new BussinessException(BizExceptionEnum.CUSTOMER_FORMAT_ERROR, "流量包对象为空");
		}else{
			return flowPack.get(0);
		}
	}
	
	
	public static String create_randomId(int len) {
    	String ret = UUID.randomUUID().toString().replace("-","");
    	if (ret.length() > len){
    		ret = ret.substring(0, len);
    	}
        return ret;
    }

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.portal.service.IFlowRechargeService#getFlowOrderInfoByCustomerId(java.lang.Long)
	 */
	@Override
	public void checkFlowOrderInfoByCustomerId(Long customerId) throws BussinessException {
		
		if ( customerId == null || customerId.compareTo(0L) <= 0 ) 
			throw new BussinessException(BizExceptionEnum.CUSTOMER_NOT_EXIST);
		
		//获取客户信息
		CustomerInfo customer = customerInfoService.get(customerId);
        
		if (customer == null || !Constant.CUSTOMER_STATUS_ON.equals(customer.getStatus())) 
            throw new BussinessException(BizExceptionEnum.CUSTOMER_NOT_EXIST);
        
    	//查询对应的流量包订单
		List<OrderInfo> orderInfoList = orderDao.getOrderInfoByCustomerId(customer.getCustomerId());
		if(orderInfoList.isEmpty())
            throw new BussinessException(BizExceptionEnum.CUSTOMER_FORMAT_ERROR, "当前未开通流量充值功能，请联系管理员！");
		
		if(Constant.ORDER_STATUS_EFFECTIVE.equals(orderInfoList.get(0).getStatus())){
			return;
		}else{
			throw new BussinessException(BizExceptionEnum.CUSTOMER_FORMAT_ERROR, "当前订单未生效");
		}
	}
	
	
	
	
	
	
	
	
	/**
	 * 查询运营商对应的流量包信息
	 * @param userMobile
	 * @param request
	 * @return
	 * @throws JsonProcessingException 
	 */
	public List<FlowPackage> getBatchFlowPackageInfo(String operator, Integer packageType) throws BussinessException {
	    
        ShiroUser staff = ShiroKit.getUser();
      //获取客户信息
        CustomerInfo customer = null;
        if ( (customer = customerInfoService.get(staff.getTargetId())) == null 
        		|| !Constant.CUSTOMER_STATUS_ON.equals(customer.getStatus()) ) {
            throw new BussinessException(BizExceptionEnum.CUSTOMER_FORMAT_ERROR, "客户不存在或状态不可用");
        }
        
        if ( customer.getPartnerId() != null && customer.getPartnerId() > 0L ) {
        	PartnerInfo partnerInfo = partnerService.get(customer.getPartnerId());
            if ( partnerInfo == null || !PartnerInfo.STATUS_OK.equals(partnerInfo.getStatus()) ) 
            	throw new BussinessException(BizExceptionEnum.CUSTOMER_FORMAT_ERROR, "合作伙伴不存在或状态不可用");
        }
        
        //根据合作伙伴ID查询产品列表
        List<FlowPackage> flowPackage = getBatchFlowProductList(operator, packageType, customer.getCustomerId());
	    return flowPackage;
	}
	
	/**
	 * 根据运营商查询对应的流量产品列表
	 * @return
	 * @throws FOSSException 
	 */
	public List<FlowPackage> getBatchFlowProductList(String operator, Integer packageType, Long customerId) {
	    Map<String, Object> map = new HashMap<String, Object>();
	    map.put("userId", customerId);
	    map.put("pos", operator);
	    map.put("packageType", packageType);
	    
	    List<FlowPackage> flowPackeage = flowPackageDao.getBatchFlowRechargeByCode(map);
	    if ( flowPackeage == null || flowPackeage.isEmpty() )
	    	return Collections.emptyList();
	    
        for (FlowPackage flowPackage : flowPackeage) {
            flowPackage.setCarrieroperator("");
            flowPackage.setPackagePrice(0d);
        }
        return flowPackeage;
	}
	
}
