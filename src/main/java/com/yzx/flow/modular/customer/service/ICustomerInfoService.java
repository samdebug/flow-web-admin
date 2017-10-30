package com.yzx.flow.modular.customer.service;

import java.util.List;

import com.yzx.flow.common.exception.BussinessException;
import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.persistence.model.AreaCode;
import com.yzx.flow.common.persistence.model.CustomerInfo;
import com.yzx.flow.common.persistence.model.OrderInfo;

public interface ICustomerInfoService {

	List<CustomerInfo> pageQuery(Page<CustomerInfo> page);

	void insert(CustomerInfo data);

	CustomerInfo get(Long customerId);

	/**
	 * 保存客户及客户产品配置信息
	 * @param data
	 */
	void saveAndUpdate(CustomerInfo data, OrderInfo updateOrder);

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
	
	/**
	 * 重置密码 - 
	 * @param data
	 */
	void resetPasswd(Long customerId) throws BussinessException;

}