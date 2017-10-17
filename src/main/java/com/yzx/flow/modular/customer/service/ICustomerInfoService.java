package com.yzx.flow.modular.customer.service;

import java.util.List;

import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.persistence.model.AreaCode;
import com.yzx.flow.common.persistence.model.CustomerInfo;

public interface ICustomerInfoService {

	List<CustomerInfo> pageQuery(Page<CustomerInfo> page);

	void insert(CustomerInfo data);

	CustomerInfo get(Long customerId);

	/**
	 * @param data
	 */
	void saveAndUpdate(CustomerInfo data);

	void update(CustomerInfo data);

	void updateDeletedFlag(CustomerInfo data);

	int delete(Long customerId);

	/**
	 * 判断根据账号查询客户对象
	 * 
	 * @param account
	 * @return
	 */
	List<CustomerInfo> getAccount(String account);

	/**
	 * 判断根据手机号查询客户对象
	 * 
	 * @param mobile
	 * @return
	 */
	List<CustomerInfo> getMobile(String mobile);

	CustomerInfo getCustomerInfoByDetailId(Long orderDetailId);

	List<CustomerInfo> getCustomerByCustomerInfo(CustomerInfo customerInfo);

	List<CustomerInfo> getByPartnerId(Long partnerId);

	/**
	 * 获取所有的地区信息
	 * @return
	 */
	List<AreaCode> selectALL();

}