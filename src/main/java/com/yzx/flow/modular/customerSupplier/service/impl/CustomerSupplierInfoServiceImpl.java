package com.yzx.flow.modular.customerSupplier.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.persistence.model.CustomerSupplierInfo;
import com.yzx.flow.modular.customerSupplier.service.ICustomerSupplierInfoService;
import com.yzx.flow.modular.system.dao.CustomerSupplierInfoDao;

/**
 * @author yangmiao
 * @date 2017年3月21日下午5:49:12
 */
@Service("customerSupplierInfoService")
public class CustomerSupplierInfoServiceImpl implements ICustomerSupplierInfoService {
	
	private static final Logger LOG = LoggerFactory.getLogger(CustomerSupplierInfoServiceImpl.class);
	
	@Autowired
	private CustomerSupplierInfoDao customerSupplierInfoDao;
	
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.customerSupplier.service.ICustomerSupplierInfoService#customerPageQuery(com.yzx.flow.common.page.Page)
	 */
	@Override
	public Page<CustomerSupplierInfo> customerPageQuery(Page<CustomerSupplierInfo> page) {
		List<CustomerSupplierInfo> list = customerSupplierInfoDao.customerPageQuery(page);
		page.setDatas(list);
		return page;
	}
	
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.customerSupplier.service.ICustomerSupplierInfoService#supplierPageQuery(com.yzx.flow.common.page.Page)
	 */
	@Override
	public Page<CustomerSupplierInfo> supplierPageQuery(Page<CustomerSupplierInfo> page) {
		List<CustomerSupplierInfo> list = customerSupplierInfoDao.supplierPageQuery(page);
		page.setDatas(list);
		return page;
	}
	
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.customerSupplier.service.ICustomerSupplierInfoService#chargeDiff(com.yzx.flow.common.page.Page)
	 */
	@Override
	public Map<String, Object> chargeDiff(Page<CustomerSupplierInfo> page) {
		Map<String, Object> map= new HashMap<String, Object>();
		Map<String, Object> map_cus = customerSupplierInfoDao.customerTradeTotal(page);
        Map<String, Object> map_sup = customerSupplierInfoDao.supplierTradeTotal(page);
        BigDecimal sum_tradeAmount = new BigDecimal(0);
        BigDecimal sum_money = new BigDecimal(0);
        BigDecimal diff;
        if(map_cus != null ){
        	sum_tradeAmount = (BigDecimal) map_cus.get("sum_tradeAmount");
        }
        if(map_sup != null){
        	sum_money = (BigDecimal) map_sup.get("sum_money");
        }
        map.put("sum_tradeAmount", sum_tradeAmount.setScale(2, BigDecimal.ROUND_HALF_UP));
        map.put("sum_money", sum_money.setScale(2, BigDecimal.ROUND_HALF_UP));
        diff = sum_tradeAmount.subtract(sum_money).setScale(2, BigDecimal.ROUND_HALF_UP);
        map.put("charge_diff", diff);
        map.put("success", true);
		return map;
	}
}
