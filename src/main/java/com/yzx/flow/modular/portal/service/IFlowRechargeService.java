package com.yzx.flow.modular.portal.service;

import java.util.List;

import com.yzx.flow.common.exception.BussinessException;
import com.yzx.flow.common.persistence.model.FlowPackage;

public interface IFlowRechargeService {

	/**
	 * 判断用户是否存在可用流量包订单 - 请处理BussinessException
	 * @param request
	 * @return
	 */
	void checkFlowOrderInfoByCustomerId(Long customerId) throws BussinessException;
	
	
	
	/**
	 * 查询订单中手机号码对应的流量包信息
	 * @param userMobile
	 * @param request
	 * @return
	 * @throws BussinessException 
	 */
	List<FlowPackage> getFlowPackageInfo(Long customerId, String userMobile, String code, String isCombo)
			throws BussinessException;

	/**
	 * 根据手机号码的运营商查询对应的流量产品列表
	 * @return
	 * @throws BussinessException 
	 */
	List<FlowPackage> getFlowProductList(String phone, Long customerId, String code, String isCombo)
			throws BussinessException;

	List<FlowPackage> selectFlowPackeage(Long customerId, String pos, String areaCode, int i, String isCombo);
	
	/**
	 * 流量充值
	 * @param customerId
	 * @param mobile
	 * @param packageId
	 * @param isCombo
	 */
	void flowRecharge(Long customerId, String mobile, String packageId, String isCombo) throws BussinessException;
	
	
	/**
	 * 查询运营商对应的流量包信息
	 * @param userMobile
	 * @param request
	 * @return
	 * @throws BussinessException 
	 */
	List<FlowPackage> getBatchFlowPackageInfo(String operator, Integer packageType) throws BussinessException;
	
	/**
	 * 根据运营商查询对应的流量产品列表
	 * @return
	 * @throws  
	 */
	List<FlowPackage> getBatchFlowProductList(String operator, Integer packageType, Long customerId);

}