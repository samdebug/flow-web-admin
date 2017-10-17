package com.yzx.flow.modular.customer.service.impl;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yzx.flow.common.exception.MyException;
import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.persistence.model.CustomerBalanceMonth;
import com.yzx.flow.common.persistence.model.CustomerInfo;
import com.yzx.flow.common.persistence.model.CustomerTradeFlow;
import com.yzx.flow.common.persistence.model.Staff;
import com.yzx.flow.common.util.DateUtil;
import com.yzx.flow.common.util.RedisHttpUtil;
import com.yzx.flow.common.util.URLConstants;
import com.yzx.flow.core.shiro.ShiroUser;
import com.yzx.flow.modular.customer.service.ICustomerBalanceMonthService;
import com.yzx.flow.modular.system.dao.CustomerBalanceMonthDao;
import com.yzx.flow.modular.system.dao.CustomerInfoDao;
import com.yzx.flow.modular.system.dao.CustomerTradeFlowDao;
import com.yzx.flow.modular.system.dao.PartnerBalanceMonthDao;


/**
 * 
 * <b>Title：</b>CustomerBalanceMonthService.java<br/>
 * <b>Description：</b> <br/>
 * <b>@author： </b>v5480<br/>
 * <b>@date：</b>2015-09-02 10:17:41<br/>
 * <b>Copyright (c) 2015 szwisdom Tech.</b>
 * 
 */
@Service("customerBalanceMonthService")
public class CustomerBalanceMonthService implements ICustomerBalanceMonthService {

	private static final Logger LOG = LoggerFactory
			.getLogger(CustomerBalanceMonthService.class);

	@Autowired
	private CustomerBalanceMonthDao customerBalanceMonthDao;

	@Autowired
	private CustomerInfoDao customerInfoDao;

	@Autowired
	private CustomerTradeFlowDao customerTradeFlowDao;
	
	@Autowired
	private PartnerBalanceMonthDao partnerBalanceMonthMapepr;

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.customer.service.impl.ICustomerBalanceMonthService#pageQuery(com.yzx.flow.common.page.Page)
	 */
	public Page<CustomerBalanceMonth> pageQuery(Page<CustomerBalanceMonth> page) {
		List<CustomerBalanceMonth> list = customerBalanceMonthDao
				.pageQuery(page);
		page.setDatas(list);
		return page;
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.customer.service.impl.ICustomerBalanceMonthService#insert(com.yzx.flow.common.persistence.model.CustomerBalanceMonth)
	 */
	public void insert(CustomerBalanceMonth data) {
		customerBalanceMonthDao.insert(data);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.customer.service.impl.ICustomerBalanceMonthService#get(java.lang.Long)
	 */
	public CustomerBalanceMonth get(Long balanceMonthId) {
		return customerBalanceMonthDao.selectByPrimaryKey(balanceMonthId);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.customer.service.impl.ICustomerBalanceMonthService#saveAndUpdate(com.yzx.flow.common.persistence.model.CustomerBalanceMonth)
	 */
	public void saveAndUpdate(CustomerBalanceMonth data) {
		if (null != data.getBalanceMonthId()) {// 判断有没有传主键，如果传了为更新，否则为新增
			this.update(data);
		} else {
			this.insert(data);
		}
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.customer.service.impl.ICustomerBalanceMonthService#update(com.yzx.flow.common.persistence.model.CustomerBalanceMonth)
	 */
	public void update(CustomerBalanceMonth data) {
		customerBalanceMonthDao.updateByPrimaryKey(data);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.customer.service.impl.ICustomerBalanceMonthService#delete(java.lang.Long)
	 */
	public int delete(Long balanceMonthId) {
		return customerBalanceMonthDao.deleteByPrimaryKey(balanceMonthId);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.customer.service.impl.ICustomerBalanceMonthService#getCustomerBalanceMonth()
	 */
	public int getCustomerBalanceMonth() {
		return customerBalanceMonthDao.insertCustomerBalanceMonth();
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.customer.service.impl.ICustomerBalanceMonthService#saveCustomerBalanceMonth(com.yzx.flow.common.persistence.model.CustomerBalanceMonth, com.yzx.flow.common.persistence.model.Staff)
	 */
	@Transactional(rollbackFor = Exception.class)
	public void saveCustomerBalanceMonth(CustomerBalanceMonth data, ShiroUser staff) {
		// 更新月结算明细表
		saveAndUpdate(data);
		
		// 更新customer_info表
        CustomerInfo customerInfo = customerInfoDao.selectByPrimaryKey(data.getCustomerId());
        customerInfo.setBalance(customerInfo.getBalance().subtract(data.getAdjustMoney()));
        customerInfo.setUpdator(staff.getAccount());
        customerInfo.setUpdateTime(new Date());
		String result = RedisHttpUtil.sendGet(URLConstants.DEL_REDIS_URL,
				URLConstants.T_CUSTOMER_INFO, customerInfo.getCustomerId()+"");
		if (!"OK".equals(result)) {
			throw new MyException("删除Redis中信息出错,其请求URL参数为:" + URLConstants.T_CUSTOMER_INFO+"\t"+customerInfo.getCustomerId());
		}
        customerInfoDao.updateByPrimaryKey(customerInfo);

		// 新增交易流水记录
		CustomerTradeFlow ctf = new CustomerTradeFlow();
		CustomerInfo ci = customerInfoDao.selectByPrimaryKey(data
				.getCustomerId());
		ctf.setCustomerId(ci.getCustomerId());
		ctf.setTradeTime(new Date());
		ctf.setTradeType(TRADETYPE_SETTLEMENT);
		ctf.setBalance(ci.getBalance());
		ctf.setCreditAmount(ci.getCreditAmount());
		ctf.setTradeAmount(new BigDecimal(0).subtract(data.getAdjustMoney()));
		ctf.setLoginName(staff.getAccount());
		ctf.setOperatorName(staff.getName());
		ctf.setInputTime(new Date());
		ctf.setRemark(data.getRemark());
		customerTradeFlowDao.insert(ctf);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.customer.service.impl.ICustomerBalanceMonthService#updateCustomerInfoBalanceStatus()
	 */
	public int updateCustomerInfoBalanceStatus() throws ParseException {
		List<CustomerBalanceMonth> customerList = customerBalanceMonthDao.selectCustomerBalanceByAutomacticBill();
		if(customerList.isEmpty()){
			LOG.debug("客户 无自动结算 数据！返回 0 。");
			return 0;
		}
		LOG.debug("待自动结算 数据集合长度："+customerList.size());
		int count = 0;
		for (CustomerBalanceMonth customerBalanceMonth : customerList) {
			if(customerBalanceMonth.getAdjustMoney().compareTo(BigDecimal.valueOf(0.00)) != 0
					|| customerBalanceMonth.getAdjustMoney().compareTo(BigDecimal.valueOf(0)) != 0){
				CustomerInfo customerInfo = customerInfoDao.selectByPrimaryKey(customerBalanceMonth.getCustomerId());
				customerInfo.setBalance(customerInfo.getBalance().subtract(customerBalanceMonth.getAdjustMoney()));
				String result = RedisHttpUtil.sendGet(URLConstants.DEL_REDIS_URL,
						URLConstants.T_CUSTOMER_INFO, customerInfo.getCustomerId()+"");
				if (!"OK".equals(result)) {
					throw new MyException("删除Redis中信息出错,其请求URL参数为:" + URLConstants.T_CUSTOMER_INFO+"\t"+customerInfo.getCustomerId());
				}
				int row = customerInfoDao.updateByPrimaryKey(customerInfo);
				if(row == 1){
					CustomerTradeFlow data = new CustomerTradeFlow();
			        data.setTradeTime(new Date());
			        data.setBalance(customerInfo.getBalance());
			        data.setCreditAmount(customerInfo.getCreditAmount());
			        data.setLoginName("admin");
			        data.setCustomerId(customerInfo.getCustomerId());
			        data.setTradeType(CustomerTradeFlow.TRADETYPE_SETTLEMENT);
			        data.setTradeAmount(BigDecimal.valueOf(0).subtract(customerBalanceMonth.getAdjustMoney()));
			        data.setOperatorName("调整"+BigDecimal.valueOf(0).subtract(customerBalanceMonth.getAdjustMoney()));
			        data.setInputTime(new Date());
			        Date time = new SimpleDateFormat("yyyyMM").parse(customerBalanceMonth.getMonth());
			        int month = DateUtil.getMonth(time);
			        data.setRemark(month+"月系统自动确认， 调整金额："+data.getTradeAmount());
			        int rows = customerTradeFlowDao.insert(data);
			        LOG.debug("客户账户流水新增："+rows);
				}
			}
			customerBalanceMonth.setBalanceStatus(1);
			customerBalanceMonth.setUpdateTime(new Date());
			customerBalanceMonth.setUpdator("admin");
			customerBalanceMonth.setRemark(DateUtil.getMonth(new Date())+"月，系统自动月结算");
			count = customerBalanceMonthDao.updateByPrimaryKey(customerBalanceMonth);
		}
		return count;
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.customer.service.impl.ICustomerBalanceMonthService#BillMonthByTime(java.util.Map)
	 */
	public void BillMonthByTime(final Map<String, Object> map) {
		try {
			//客户
			int count = customerBalanceMonthDao.deleteCustomerBalanceMonthByTime(map);
			LOG.debug("指定该区间 {客户}月账单数据 集合删除返回："+count);
			int rows = customerBalanceMonthDao.insertCustomerBalanceMonthByTime(map);
			LOG.debug("制定该区间  {客户}月账单 数据插入返回："+rows);	
			//合作伙伴
			int count1 = partnerBalanceMonthMapepr.deletePartnerBalanceMonthByTime(map);
			LOG.debug("指定该区间 {合作伙伴}月账单数据 集合删除返回："+count1);
			int rows1 = partnerBalanceMonthMapepr.insertPartnerBalanceMonthByTime(map);
			LOG.debug("制定该区间  {合作伙伴}月账单 数据插入返回："+rows1);	
		} catch (Exception e) {
			LOG.error("指定该区间  月账单数据统计 发生异常:"+e.getMessage(),e);
		}
	}
}