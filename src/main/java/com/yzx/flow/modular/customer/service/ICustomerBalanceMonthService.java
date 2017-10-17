package com.yzx.flow.modular.customer.service;

import java.text.ParseException;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.persistence.model.CustomerBalanceMonth;
import com.yzx.flow.common.persistence.model.Staff;
import com.yzx.flow.core.shiro.ShiroUser;

public interface ICustomerBalanceMonthService {

	/**
	 * 交易类型 1：结算 2：充值 3：授信
	 */
	Integer TRADETYPE_SETTLEMENT = 1;

	Page<CustomerBalanceMonth> pageQuery(Page<CustomerBalanceMonth> page);

	void insert(CustomerBalanceMonth data);

	CustomerBalanceMonth get(Long balanceMonthId);

	void saveAndUpdate(CustomerBalanceMonth data);

	void update(CustomerBalanceMonth data);

	int delete(Long balanceMonthId);

	int getCustomerBalanceMonth();

	/**
	 * 结算价格调整
	 */
	void saveCustomerBalanceMonth(CustomerBalanceMonth data, ShiroUser staff);

	int updateCustomerInfoBalanceStatus() throws ParseException;

	/**
	 * 指定时间月结算
	 */
	void BillMonthByTime(Map<String, Object> map);

}