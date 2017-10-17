package com.yzx.flow.modular.customerSupplier.service;

import java.util.Map;

import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.persistence.model.CustomerSupplierInfo;

public interface ICustomerSupplierInfoService {

	/**
	 * 查询客户充值明细
	 * @param page
	 * @return
	 */
	Page<CustomerSupplierInfo> customerPageQuery(Page<CustomerSupplierInfo> page);

	/**
	 * 通道供应商充值明细
	 * @param page
	 * @return
	 */
	Page<CustomerSupplierInfo> supplierPageQuery(Page<CustomerSupplierInfo> page);

	/**
	 * 客户充值金额汇总
	 * @param page
	 * @return
	 */
	Map<String, Object> chargeDiff(Page<CustomerSupplierInfo> page);

}