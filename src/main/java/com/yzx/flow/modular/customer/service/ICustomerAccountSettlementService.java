package com.yzx.flow.modular.customer.service;

import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.page.PageInfoBT;
import com.yzx.flow.common.persistence.model.CustomerAccountSettlement;

public interface ICustomerAccountSettlementService {

	/**
	 * 分页查找
	 * @param page
	 * @return
	 */
	PageInfoBT<CustomerAccountSettlement> pageQuery(Page<CustomerAccountSettlement> page);

}