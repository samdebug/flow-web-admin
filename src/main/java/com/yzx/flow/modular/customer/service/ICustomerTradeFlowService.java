package com.yzx.flow.modular.customer.service;

import org.springframework.transaction.annotation.Transactional;

import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.page.PageInfoBT;
import com.yzx.flow.common.persistence.model.CustomerTradeFlow;
import com.yzx.flow.common.persistence.model.Staff;

public interface ICustomerTradeFlowService {

	/**
	 * 交易类型  1：结算  2：充值  3：授信
	 */
	Integer TRADETYPE_RECHARGE = 2;
	/**
	 * 交易类型  1：结算  2：充值  3：授信
	 */
	Integer TRADETYPE_CREDIT = 3;

	PageInfoBT<CustomerTradeFlow> pageQuery(Page<CustomerTradeFlow> page);

	void insert(CustomerTradeFlow data);

	CustomerTradeFlow get(Long tradeFlowId);

	void saveAndUpdate(CustomerTradeFlow data);

	void update(CustomerTradeFlow data);

	int delete(Long tradeFlowId);

	/**
	 * 基于流水金额调整后客户余额显示不对的纠错方法
	 */
	void correctionCustomerTradeFlow(Long customerId);

	/**
	 * 充值/信用额度调整
	 */
	void saveCustomerTradeFlow(CustomerTradeFlow data, Staff staff);

	void _saveCustomerTradeFlow(CustomerTradeFlow data, Staff staff);

}