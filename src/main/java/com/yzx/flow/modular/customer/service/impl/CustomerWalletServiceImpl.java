package com.yzx.flow.modular.customer.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.page.PageInfoBT;
import com.yzx.flow.common.persistence.model.CustomerWallet;
import com.yzx.flow.modular.customer.service.ICustomerWalletService;
import com.yzx.flow.modular.system.dao.CustomerWalletDao;

@Service("customerWalletService")
public class CustomerWalletServiceImpl implements ICustomerWalletService {
	@Autowired
	private CustomerWalletDao customerWalletDao;
	
	
	/* 
	 * 分页查询
	 */
	@Override
	public PageInfoBT<CustomerWallet> pageQuery(Page<CustomerWallet> page) {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMM");  
	    String tableName = "customer_wallet" + format.format(new Date()); 
        page.getParams().put("tableName", tableName);
        List<CustomerWallet> list = customerWalletDao.pageQuery(page);
        return new PageInfoBT<CustomerWallet>(list, page.getTotal());
	}

}
