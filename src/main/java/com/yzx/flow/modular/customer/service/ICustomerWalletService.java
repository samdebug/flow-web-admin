package com.yzx.flow.modular.customer.service;

import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.page.PageInfoBT;
import com.yzx.flow.common.persistence.model.CustomerWallet;

public interface ICustomerWalletService {
	
	
	/**分页查找
	 * @param page
	 * @return
	 */
	public  PageInfoBT<CustomerWallet> pageQuery(Page<CustomerWallet> page);
	
}
