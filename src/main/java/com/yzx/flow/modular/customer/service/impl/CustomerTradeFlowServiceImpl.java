package com.yzx.flow.modular.customer.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yzx.flow.common.exception.MyException;
import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.page.PageInfoBT;
import com.yzx.flow.common.persistence.model.CustomerInfo;
import com.yzx.flow.common.persistence.model.CustomerTradeFlow;
import com.yzx.flow.common.persistence.model.Staff;
import com.yzx.flow.common.util.Constant;
import com.yzx.flow.common.util.RedisHttpUtil;
import com.yzx.flow.common.util.URLConstants;
import com.yzx.flow.modular.customer.service.ICustomerTradeFlowService;
import com.yzx.flow.modular.system.dao.CustomerInfoDao;
import com.yzx.flow.modular.system.dao.CustomerTradeFlowDao;

/**
 * 
 * <b>Title：</b>CustomerTradeFlowService.java<br/>
 * <b>Description：</b> <br/>
 * <b>@author： </b>v5480<br/>
 * <b>@date：</b>2015-09-02 10:17:41<br/>
 * <b>Copyright (c) 2015 szwisdom Tech.</b>
 * 
 */
@Service("customerTradeFlowService")
public class CustomerTradeFlowServiceImpl implements ICustomerTradeFlowService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerTradeFlowServiceImpl.class);
    
	@Autowired
	private CustomerTradeFlowDao customerTradeFlowDao;

	@Autowired
	private CustomerInfoDao customerInfoDao;
	
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.customer.service.impl.ICustomerTradeFlowService#pageQuery(com.yzx.flow.common.page.Page)
	 */
	@Override
	public PageInfoBT<CustomerTradeFlow> pageQuery(Page<CustomerTradeFlow> page) {
		page.setAutoCountTotal(false);
		List<CustomerTradeFlow> list = customerTradeFlowDao.pageQuery(page);
    	
		return new PageInfoBT<CustomerTradeFlow>(list, page.getTotal());
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.customer.service.impl.ICustomerTradeFlowService#insert(com.yzx.flow.common.persistence.model.CustomerTradeFlow)
	 */
	@Override
	public void insert(CustomerTradeFlow data) {
		customerTradeFlowDao.insert(data);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.customer.service.impl.ICustomerTradeFlowService#get(java.lang.Long)
	 */
	@Override
	public CustomerTradeFlow get(Long tradeFlowId) {
		return customerTradeFlowDao.selectByPrimaryKey(tradeFlowId);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.customer.service.impl.ICustomerTradeFlowService#saveAndUpdate(com.yzx.flow.common.persistence.model.CustomerTradeFlow)
	 */
	@Override
	public void saveAndUpdate(CustomerTradeFlow data) {
		if (null != data.getTradeFlowId()) {// 判断有没有传主键，如果传了为更新，否则为新增
			this.update(data);
		} else {
			this.insert(data);
		}
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.customer.service.impl.ICustomerTradeFlowService#update(com.yzx.flow.common.persistence.model.CustomerTradeFlow)
	 */
	@Override
	public void update(CustomerTradeFlow data) {
		customerTradeFlowDao.updateByPrimaryKey(data);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.customer.service.impl.ICustomerTradeFlowService#delete(java.lang.Long)
	 */
	@Override
	public int delete(Long tradeFlowId) {
		return customerTradeFlowDao.deleteByPrimaryKey(tradeFlowId);
	}
	
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.customer.service.impl.ICustomerTradeFlowService#correctionCustomerTradeFlow(java.lang.Long)
	 */
	@Override
	public void correctionCustomerTradeFlow(Long customerId) {
	    LOGGER.debug("-------纠错方法-----开始执行-------");
	    // 取出DB中原始数据
	    CustomerTradeFlow customerTradeFlow = new CustomerTradeFlow();
	    customerTradeFlow.setCustomerId(customerId);
	    List<CustomerTradeFlow> customerTradeFlows = customerTradeFlowDao.selectByInfo(customerTradeFlow);
	    if (!customerTradeFlows.isEmpty()) {
	        CustomerTradeFlow info = customerTradeFlows.get(0);
	        if (Constant.TRADE_TYPE_SETTLEMENT.equals(info.getTradeType())) {
	            LOGGER.debug("-------------纠错方法-----第一条是【结算】类型-------");
	            // 0 + 操作金额
	            info.setBalance(info.getTradeAmount());
	            info.setCreditAmount(new BigDecimal(0));
	            customerTradeFlowDao.updateByPrimaryKey(info);
	            LOGGER.debug("-------------纠错方法-----重新查询-------");
	            customerTradeFlows = customerTradeFlowDao.selectByInfo(customerTradeFlow);
	        }
	    } else {
	        return;
	    }
	    // 批量纠正每条流水的账户余额
	    for (int i = 1; i < customerTradeFlows.size(); i++) {
	        // 根据上一条的【账户余额】纠正当前流水的账户余额
	        // 上一条交易流水记录
	        CustomerTradeFlow tradeFlowBefore = customerTradeFlows.get(i-1);
	        // 本条流水记录
	        CustomerTradeFlow tradeFlow = customerTradeFlows.get(i);
	        // 本条不是授信的话，设置授信为上一条的授信
	        if (!Constant.TRADE_TYPE_CREDIT.equals(tradeFlow.getTradeType())) {
	            LOGGER.debug("-------------纠错方法-----本条不是【授信】，设置授信为上一条的授信-------");
	            tradeFlow.setCreditAmount(tradeFlowBefore.getCreditAmount());
	        }
	        
	        // 校验：上一条的账户余额 + 本条的操作金额 = 本条的账户余额
	        // 上一条的账户余额 + 本条的操作金额
	        BigDecimal balanceBefore = tradeFlowBefore.getBalance();
	        BigDecimal balanceFinal = new BigDecimal(0);
            // 交易类型为【授信】的情况下，取0作为本条流水的操作金额
            if (Constant.TRADE_TYPE_CREDIT == tradeFlow.getTradeType()) {
                balanceFinal = balanceBefore;
            } else {
                balanceFinal = balanceBefore.add(tradeFlow.getTradeAmount());
            }
	        
	        // 校验不通过，手动纠正
            tradeFlow.setBalance(balanceFinal);
            // update当前流水记录
            LOGGER.debug("-------------纠错方法-----for循环里update当前流水记录-------");
            this.update(tradeFlow);
	    }
	    
	    // 更新customer_info表中的账户余额
	    CustomerInfo customerInfo = customerInfoDao.selectByPrimaryKey(customerId);
	    customerInfo.setBalance(customerTradeFlows.get(customerTradeFlows.size()-1).getBalance());
	    LOGGER.debug("-------------纠错方法-----最后更新客户余额-------");
		String result = RedisHttpUtil.sendGet(URLConstants.DEL_REDIS_URL,
				URLConstants.T_CUSTOMER_INFO, customerInfo.getCustomerId()+"");
		if (!"OK".equals(result)) {
			throw new MyException("删除Redis中信息出错,其请求URL参数为:" + URLConstants.T_CUSTOMER_INFO+"\t"+customerInfo.getCustomerId());
		}
	    customerInfoDao.updateByPrimaryKey(customerInfo);
	    LOGGER.debug("-------纠错方法-----执行结束-------");
	}
	
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.customer.service.impl.ICustomerTradeFlowService#saveCustomerTradeFlow(com.yzx.flow.common.persistence.model.CustomerTradeFlow, com.yzx.flow.common.persistence.model.Staff)
	 */
    @Override
	@Transactional(rollbackFor = Exception.class)
    public void saveCustomerTradeFlow(CustomerTradeFlow data, Staff staff) {
    	_saveCustomerTradeFlow(data,staff);
    }
    /* (non-Javadoc)
	 * @see com.yzx.flow.modular.customer.service.impl.ICustomerTradeFlowService#_saveCustomerTradeFlow(com.yzx.flow.common.persistence.model.CustomerTradeFlow, com.yzx.flow.common.persistence.model.Staff)
	 */
    @Override
	public void _saveCustomerTradeFlow(CustomerTradeFlow data, Staff staff) {
        // 更新customer_info表
        CustomerInfo customerInfo = customerInfoDao.selectByPrimaryKey(data.getCustomerId());
        if (data.getTradeType() == TRADETYPE_RECHARGE) {
            customerInfo.setBalance(customerInfo.getBalance().add(data.getTradeAmount()));
        } else if (data.getTradeType() == TRADETYPE_CREDIT) {
            customerInfo.setCreditAmount(customerInfo.getCreditAmount().add(data.getTradeAmount()));
        }else if (data.getTradeType() == Constant.TRADE_TYPE_SETTLEMENT) {
            customerInfo.setBalance(customerInfo.getBalance().add(data.getTradeAmount()));
        }
        customerInfo.setUpdator(staff.getLoginName());
        customerInfo.setUpdateTime(new Date());
		String result = RedisHttpUtil.sendGet(URLConstants.DEL_REDIS_URL,
				URLConstants.T_CUSTOMER_INFO, customerInfo.getCustomerId()+"");
		if (!"OK".equals(result)) {
			throw new MyException("删除Redis中信息出错,其请求URL参数为:" + URLConstants.T_CUSTOMER_INFO+"\t"+customerInfo.getCustomerId());
		}
        customerInfoDao.updateByPrimaryKey(customerInfo);
        
        // 新增交易流水记录
        CustomerInfo ci = customerInfoDao.selectByPrimaryKey(data.getCustomerId());
//        TODO 合作伙伴在授信时写入了操作金额，所以注释掉这个代码
//        if (data.getTradeType() == TRADETYPE_CREDIT) {
//            data.setTradeAmount(new BigDecimal(0));
//        }
        data.setTradeTime(new Date());
        data.setBalance(ci.getBalance());
        data.setCreditAmount(ci.getCreditAmount());
        data.setLoginName(staff.getLoginName());
        data.setOperatorName(staff.getLoginName());
        data.setInputTime(new Date());
        customerTradeFlowDao.insert(data);
    }
}