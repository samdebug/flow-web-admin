package com.yzx.flow.modular.partner.service.impl;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yzx.flow.common.exception.MyException;
import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.persistence.model.PartnerBalanceMonth;
import com.yzx.flow.common.persistence.model.PartnerInfo;
import com.yzx.flow.common.persistence.model.PartnerTradeFlow;
import com.yzx.flow.common.persistence.model.Staff;
import com.yzx.flow.common.util.DateUtil;
import com.yzx.flow.common.util.RedisHttpUtil;
import com.yzx.flow.common.util.URLConstants;
import com.yzx.flow.core.shiro.ShiroUser;
import com.yzx.flow.modular.partner.service.IPartnerBalanceMonthService;
import com.yzx.flow.modular.system.dao.PartnerBalanceMonthDao;
import com.yzx.flow.modular.system.dao.PartnerInfoDao;
import com.yzx.flow.modular.system.dao.PartnerTradeFlowDao;

/**
 * 
 * <b>Title：</b>PartnerBalanceMonthService.java<br/>
 * <b>Description：</b> <br/>
 * <b>@author： </b>v5480<br/>
 * <b>@date：</b>2015-09-02 10:17:41<br/>
 * <b>Copyright (c) 2015 szwisdom Tech.</b>
 * 
 */
@Service("partnerBalanceMonthService")
public class PartnerBalanceMonthServiceImpl implements IPartnerBalanceMonthService {
	
	private static final Logger LOG = LoggerFactory.getLogger(PartnerBalanceMonthServiceImpl.class);
	
	
	@Autowired
	private PartnerBalanceMonthDao partnerBalanceMonthDao;
	

	@Autowired
	private PartnerInfoDao partnerInfoDao;
	

	@Autowired
	private PartnerTradeFlowDao partnerTradeFlowDao;
	
	
	
	
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.partner.service.impl.IPartnerBalanceMonthService#pageQuery(com.yzx.flow.common.page.Page)
	 */
	@Override
	public Page<PartnerBalanceMonth> pageQuery(Page<PartnerBalanceMonth> page) {
		List<PartnerBalanceMonth> list = partnerBalanceMonthDao.pageQuery(page);
		page.setDatas(list);
		return page;
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.partner.service.impl.IPartnerBalanceMonthService#insert(com.yzx.flow.common.persistence.model.PartnerBalanceMonth)
	 */
	@Override
	public void insert(PartnerBalanceMonth data) {
		partnerBalanceMonthDao.insert(data);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.partner.service.impl.IPartnerBalanceMonthService#get(java.lang.Long)
	 */
	@Override
	public PartnerBalanceMonth get(Long balanceMonthId) {
		return partnerBalanceMonthDao.selectByPrimaryKey(balanceMonthId);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.partner.service.impl.IPartnerBalanceMonthService#saveAndUpdate(com.yzx.flow.common.persistence.model.PartnerBalanceMonth)
	 */
	@Override
	public void saveAndUpdate(PartnerBalanceMonth data) {
		if (null != data.getBalanceMonthId()) {// 判断有没有传主键，如果传了为更新，否则为新增
			this.update(data);
		} else {
			this.insert(data);
		}
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.partner.service.impl.IPartnerBalanceMonthService#update(com.yzx.flow.common.persistence.model.PartnerBalanceMonth)
	 */
	@Override
	public void update(PartnerBalanceMonth data) {
		partnerBalanceMonthDao.updateByPrimaryKey(data);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.partner.service.impl.IPartnerBalanceMonthService#delete(java.lang.Long)
	 */
	@Override
	public int delete(Long balanceMonthId) {
		return partnerBalanceMonthDao.deleteByPrimaryKey(balanceMonthId);
	}
	
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.partner.service.impl.IPartnerBalanceMonthService#insertPartnerBalanceMonth()
	 */
	@Override
	public int insertPartnerBalanceMonth(){
		return partnerBalanceMonthDao.insertPartnerBalanceMonth();
	}
	
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.partner.service.impl.IPartnerBalanceMonthService#savePartnerBalanceMonth(com.yzx.flow.common.persistence.model.PartnerBalanceMonth, com.yzx.flow.common.persistence.model.Staff)
	 */
    @Override
	@Transactional(rollbackFor = Exception.class)
    public void savePartnerBalanceMonth(PartnerBalanceMonth data, ShiroUser current) {
        // 更新月结算明细表
        saveAndUpdate(data);
        
        // 更新partner_info表
        PartnerInfo partnerInfo = partnerInfoDao.selectByPrimaryKey(data.getPartnerId());
        partnerInfo.setBalance(partnerInfo.getBalance().subtract(data.getAdjustMoney()));
        partnerInfo.setUpdator(current.getAccount());
        partnerInfo.setUpdateTime(new Date());
		String result = RedisHttpUtil.sendGet(URLConstants.DEL_REDIS_URL,
				URLConstants.T_PARTNER_INFO, partnerInfo.getPartnerId()+"");
		if (!"OK".equals(result)) {
			throw new MyException("删除Redis中信息出错,其请求URL参数为:" + URLConstants.T_PARTNER_INFO+"\t"+partnerInfo.getPartnerId());
		}
        partnerInfoDao.updateByPrimaryKey(partnerInfo);
        
        // 新增交易流水记录
        PartnerTradeFlow ptf = new PartnerTradeFlow();
        PartnerInfo pi = partnerInfoDao.selectByPrimaryKey(data.getPartnerId());
        ptf.setPartnerId(pi.getPartnerId());
        ptf.setTradeTime(new Date());
        ptf.setTradeType(TRADETYPE_SETTLEMENT);
        ptf.setBalance(pi.getBalance());
        ptf.setCreditAmount(pi.getCreditAmount());
        ptf.setTradeAmount(new BigDecimal(0).subtract(data.getAdjustMoney()));
        ptf.setLoginName(current.getAccount());
        ptf.setOperatorName(current.getName());
        ptf.setInputTime(new Date());
        ptf.setRemark(data.getRemark());
        partnerTradeFlowDao.insert(ptf);
    }
    
    /* (non-Javadoc)
	 * @see com.yzx.flow.modular.partner.service.impl.IPartnerBalanceMonthService#updatePartnerInfoBalanceStatus()
	 */
    @Override
	public int updatePartnerInfoBalanceStatus() throws ParseException{
    	List<PartnerBalanceMonth> partnerList = partnerBalanceMonthDao.selectPartnerBalanceByAutomacticBill();
    	if(partnerList.isEmpty()){
    		LOG.debug("合作伙伴 无自动结算 数据！返回 0 。");
			return 0;
    	}else{
    		LOG.debug("待自动结算 数据集合长度："+partnerList.size());
    		int count = 0;
    		for (PartnerBalanceMonth partnerBalanceMonth : partnerList) {
    			if(partnerBalanceMonth.getAdjustMoney().compareTo(BigDecimal.valueOf(0.00)) != 0
    					|| partnerBalanceMonth.getAdjustMoney().compareTo(BigDecimal.valueOf(0)) != 0){
    				PartnerInfo partnerInfo = partnerInfoDao.selectByPrimaryKey(partnerBalanceMonth.getPartnerId());
    				partnerInfo.setBalance(partnerInfo.getBalance().subtract(partnerBalanceMonth.getAdjustMoney()));
    				String result = RedisHttpUtil.sendGet(URLConstants.DEL_REDIS_URL,
    						URLConstants.T_PARTNER_INFO, partnerInfo.getPartnerId()+"");
    				if (!"OK".equals(result)) {
    					throw new MyException("删除Redis中信息出错,其请求URL参数为:" + URLConstants.T_PARTNER_INFO+"\t"+partnerInfo.getPartnerId());
    				}
    				int row = partnerInfoDao.updateByPrimaryKey(partnerInfo);
    				if(row == 1){
    					PartnerTradeFlow data = new PartnerTradeFlow();
    			        data.setTradeTime(new Date());
    			        data.setBalance(partnerInfo.getBalance());
    			        data.setCreditAmount(partnerInfo.getCreditAmount());
    			        data.setLoginName("admin");
    			        data.setPartnerId(partnerInfo.getPartnerId());
    			        data.setTradeType(PartnerTradeFlow.TRADETYPE_SETTLEMENT);
    			        data.setTradeAmount(BigDecimal.valueOf(0).subtract(partnerBalanceMonth.getAdjustMoney()));
    			        data.setOperatorName("调整"+data.getTradeAmount());
    			        data.setInputTime(new Date());
    			        Date time = new SimpleDateFormat("yyyyMM").parse(partnerBalanceMonth.getMonth());
    			        int month = DateUtil.getMonth(time);
    			        data.setRemark(month+"月系统自动确认， 调整金额："+data.getTradeAmount());
    			        int rows = partnerTradeFlowDao.insert(data);
    			        LOG.debug("合作伙伴账户流水新增："+rows);
    				}
    			}
    			partnerBalanceMonth.setBalanceStatus(1);
    			partnerBalanceMonth.setUpdateTime(new Date());
    			partnerBalanceMonth.setUpdator("admin");
    			partnerBalanceMonth.setRemark(DateUtil.getMonth(new Date())+"月，系统自动月结算");
    			count = partnerBalanceMonthDao.updateByPrimaryKey(partnerBalanceMonth);
    		}
    		return count;
    	}
    }
}