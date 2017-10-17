package com.yzx.flow.modular.customer.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.page.PageInfoBT;
import com.yzx.flow.common.persistence.model.CustomerAccountSettlement;
import com.yzx.flow.modular.customer.service.ICustomerAccountSettlementService;
import com.yzx.flow.modular.system.dao.CustomerAccountSettlementDao;

/**
 * <b>Title：</b>CustomerInfoService.java<br/>
 * <b>Description：</b> <br/>
 * <b>@author： </b>v5480<br/>
 * <b>@date：</b>2015-07-08 18:05:18<br/>
 * <b>Copyright (c) 2015 szwisdom Tech.</b>
 */
@Service("customerAccountSettlementService")
public class CustomerAccountSettlementServiceImpl implements ICustomerAccountSettlementService {

    private static final Logger LOG = LoggerFactory.getLogger(CustomerAccountSettlementServiceImpl.class);
    @Autowired
    private CustomerAccountSettlementDao customerAccountSettlementDao;

    /* (non-Javadoc)
	 * @see com.yzx.flow.modular.customer.service.impl.ICustomerAccountSettlementService#pageQuery(com.yzx.flow.common.page.Page)
	 */
    public PageInfoBT<CustomerAccountSettlement> pageQuery(Page<CustomerAccountSettlement> page) {
    	LOG.debug("分页查找结算账单记录，params={}",page.getParams());
        List<CustomerAccountSettlement> list = customerAccountSettlementDao.pageQuery(page);
        return new PageInfoBT<CustomerAccountSettlement>(list, page.getTotal());
    }
}