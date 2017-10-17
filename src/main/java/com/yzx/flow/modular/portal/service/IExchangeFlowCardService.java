package com.yzx.flow.modular.portal.service;

import com.yzx.flow.common.exception.BussinessException;
import com.yzx.flow.common.persistence.model.CustomerInfo;
import com.yzx.flow.common.persistence.model.FlowCardBatchInfo;
import com.yzx.flow.common.persistence.model.FlowProductExchangeRec;
import com.yzx.flow.common.persistence.model.OrderInfo;
import com.yzx.flow.common.persistence.model.PartnerInfo;

public interface IExchangeFlowCardService {

	/**
	 * 根据UUID查询对应的客户信息
	 * @param UUID
	 * @return
	 * @throws BussinessException
	 */
	CustomerInfo getCustomerInfoByUUID(String UUID) throws BussinessException;

	/**
	 * 根据卡号和卡密查询兑换日志
	 * @param record
	 * @return
	 * @throws BussinessException
	 */
	FlowProductExchangeRec getFlowProductExchangeRecByFlowCard(FlowProductExchangeRec record) throws BussinessException;

	/**
	 * 客户查询合作伙伴信息
	 * @param partnerId
	 * @return
	 * @throws BussinessException
	 */
	PartnerInfo getPartnerInfoByPartnerId(Long partnerId) throws BussinessException;

	/**
	 * 根据Id查询客户信息
	 * @param customerId
	 * @return
	 * @throws BussinessException 
	 */
	CustomerInfo getCustomerInfoByCustomerId(Long customerId) throws BussinessException;

	/**
	 * 兑换卡批次Id查询对应的批次信息
	 * @param batchId
	 * @return
	 * @throws BussinessException 
	 */
	FlowCardBatchInfo getFlowCardBatchInfoById(Long batchId) throws BussinessException;

	/**
	 * 根据批次Id查询对应的订单信息
	 * @param batchId
	 * @return
	 * @throws BussinessException
	 */
	OrderInfo getOrderInfoByBatchId(Long batchId) throws BussinessException;

	/**
	 * 根据流量兑换日志Id删除对应信息
	 * @throws BussinessException
	 */
	void deleteExchangeLogById(Long exchangeId) throws BussinessException;

}