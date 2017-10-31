package com.yzx.flow.modular.partner.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yzx.flow.common.exception.MyException;
import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.persistence.model.CustomerWallet;
import com.yzx.flow.common.persistence.model.PartnerInfo;
import com.yzx.flow.common.persistence.model.PartnerTradeFlow;
import com.yzx.flow.common.util.Constant;
import com.yzx.flow.common.util.DateUtil;
import com.yzx.flow.common.util.OrderIdUtils;
import com.yzx.flow.common.util.RedisHttpUtil;
import com.yzx.flow.common.util.URLConstants;
import com.yzx.flow.core.shiro.ShiroUser;
import com.yzx.flow.modular.partner.service.IPartnerTradeFlowService;
import com.yzx.flow.modular.system.dao.PartnerInfoDao;
import com.yzx.flow.modular.system.dao.PartnerTradeFlowDao;

/**
 * 
 * <b>Title：</b>PartnerTradeFlowService.java<br/>
 * <b>Description：</b> <br/>
 * <b>@author： </b>v5480<br/>
 * <b>@date：</b>2015-09-02 10:17:41<br/>
 * <b>Copyright (c) 2015 szwisdom Tech.</b>
 * 
 */
@Service("partnerTradeFlowService")
public class PartnerTradeFlowServiceImpl implements IPartnerTradeFlowService {
	@Autowired
	private PartnerTradeFlowDao partnerTradeFlowDao;

	@Autowired
	private PartnerInfoDao partnerInfoDao;

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.partner.service.impl.IPartnerTradeFlowService#pageQuery(com.yzx.flow.common.page.Page)
	 */
	@Override
	public Page<PartnerTradeFlow> pageQuery(Page<PartnerTradeFlow> page) {
		List<PartnerTradeFlow> list = partnerTradeFlowDao.pageQuery(page);
		page.setDatas(list);
		return page;
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.partner.service.impl.IPartnerTradeFlowService#insert(com.yzx.flow.common.persistence.model.PartnerTradeFlow)
	 */
	@Override
	public void insert(PartnerTradeFlow data) {
		partnerTradeFlowDao.insert(data);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.partner.service.impl.IPartnerTradeFlowService#get(java.lang.Long)
	 */
	@Override
	public PartnerTradeFlow get(Long tradeFlowId) {
		return partnerTradeFlowDao.selectByPrimaryKey(tradeFlowId);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.partner.service.impl.IPartnerTradeFlowService#saveAndUpdate(com.yzx.flow.common.persistence.model.PartnerTradeFlow)
	 */
	@Override
	public void saveAndUpdate(PartnerTradeFlow data) {
		if (null != data.getTradeFlowId()) {// 判断有没有传主键，如果传了为更新，否则为新增
			this.update(data);
		} else {
			this.insert(data);
		}
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.partner.service.impl.IPartnerTradeFlowService#update(com.yzx.flow.common.persistence.model.PartnerTradeFlow)
	 */
	@Override
	public void update(PartnerTradeFlow data) {
		partnerTradeFlowDao.updateByPrimaryKey(data);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.partner.service.impl.IPartnerTradeFlowService#delete(java.lang.Long)
	 */
	@Override
	public int delete(Long tradeFlowId) {
		return partnerTradeFlowDao.deleteByPrimaryKey(tradeFlowId);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.partner.service.impl.IPartnerTradeFlowService#correctionPartnerTradeFlow(java.lang.Long)
	 */
	@Override
	public void correctionPartnerTradeFlow(Long partnerId) {
		// 取出DB中原始数据
		PartnerTradeFlow partnerTradeFlow = new PartnerTradeFlow();
		partnerTradeFlow.setPartnerId(partnerId);
		List<PartnerTradeFlow> partnerTradeFlows = partnerTradeFlowDao
				.selectByInfo(partnerTradeFlow);
		if (!partnerTradeFlows.isEmpty()) {
			PartnerTradeFlow info = partnerTradeFlows.get(0);
			if (Constant.TRADE_TYPE_SETTLEMENT.equals(info.getTradeType())) {
				// 0 + 操作金额
				info.setBalance(info.getTradeAmount());
				partnerTradeFlowDao.updateByPrimaryKey(info);
				partnerTradeFlows = partnerTradeFlowDao
						.selectByInfo(partnerTradeFlow);
			}
		} else {
			return;
		}
		// 批量纠正每条流水的账户余额
		for (int i = 1; i < partnerTradeFlows.size(); i++) {
			// 根据上一条的【账户余额】纠正当前流水的账户余额
			// 上一条交易流水记录
			PartnerTradeFlow tradeFlowBefore = partnerTradeFlows.get(i - 1);
			// 本条流水记录
			PartnerTradeFlow tradeFlow = partnerTradeFlows.get(i);
			// 本条不是授信的话，设置授信为上一条的授信
			if (!Constant.TRADE_TYPE_CREDIT.equals(tradeFlow.getTradeType())) {
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
			this.update(tradeFlow);
		}

		// 更新partner_info表中的账户余额
		PartnerInfo partnerInfo = partnerInfoDao
				.selectByPrimaryKey(partnerId);
		partnerInfo.setBalance(partnerTradeFlows.get(
				partnerTradeFlows.size() - 1).getBalance());
		String result = RedisHttpUtil.sendGet(URLConstants.DEL_REDIS_URL,
				URLConstants.T_PARTNER_INFO, partnerInfo.getPartnerId()+"");
		if (!"OK".equals(result)) {
			throw new MyException("删除Redis中信息出错,其请求URL参数为:" + URLConstants.T_PARTNER_INFO+"\t"+partnerInfo.getPartnerId());
		}
		partnerInfoDao.updateByPrimaryKey(partnerInfo);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.partner.service.impl.IPartnerTradeFlowService#savePartnerTradeFlow(com.yzx.flow.common.persistence.model.PartnerTradeFlow, com.yzx.flow.common.persistence.model.Staff)
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void savePartnerTradeFlow(PartnerTradeFlow data, ShiroUser staff) {
		_savePartnerTradeFlow(data, staff);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.partner.service.impl.IPartnerTradeFlowService#_savePartnerTradeFlow(com.yzx.flow.common.persistence.model.PartnerTradeFlow, com.yzx.flow.common.persistence.model.Staff)
	 */
	@Override
	public void _savePartnerTradeFlow(PartnerTradeFlow data, ShiroUser staff) {
		// 更新partner_info表
		PartnerInfo partnerInfo = partnerInfoDao.selectByPrimaryKey(data
				.getPartnerId());
		if (data.getTradeType() == TRADETYPE_RECHARGE) {
			partnerInfo.setBalance(partnerInfo.getBalance().add(
					data.getTradeAmount()));
		} else if (data.getTradeType() == TRADETYPE_CREDIT) {
			partnerInfo.setCreditAmount(partnerInfo.getCreditAmount().add(
					data.getTradeAmount()));
		} else if (data.getTradeType() == Constant.TRADE_TYPE_SETTLEMENT) {
			partnerInfo.setBalance(partnerInfo.getBalance().add(
					data.getTradeAmount()));
		}
		partnerInfo.setUpdator(staff.getAccount());
		partnerInfo.setUpdateTime(new Date());
		String result = RedisHttpUtil.sendGet(URLConstants.DEL_REDIS_URL,
				URLConstants.T_PARTNER_INFO, partnerInfo.getPartnerId()+"");
		if (!"OK".equals(result)) {
			throw new MyException("删除Redis中信息出错,其请求URL参数为:" + URLConstants.T_PARTNER_INFO+"\t"+partnerInfo.getPartnerId());
		}
		partnerInfoDao.updateByPrimaryKey(partnerInfo);

		// 新增交易流水记录
		PartnerInfo pi = partnerInfoDao.selectByPrimaryKey(data
				.getPartnerId());
		data.setTradeTime(new Date());
		data.setBalance(pi.getBalance());
		data.setCreditAmount(pi.getCreditAmount());
		data.setLoginName(staff.getAccount());
		data.setOperatorName(staff.getName());
		data.setInputTime(new Date());
		partnerTradeFlowDao.insert(data);
		// 插入资金流水明细
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("tradeNo", OrderIdUtils.getTimeStampSequence());
		map.put("customerId", data.getPartnerId());
		map.put("partnerId", null);
		map.put("tradeType", "2");
		map.put("price", data.getTradeAmount());
		map.put("balance", pi.getBalance());
		// 备注
		String remark = "账户充值" + data.getTradeAmount() + "元";// 备注
		map.put("remark", remark);
		map.put("applyDate", DateUtil.dateToDateString(new Date()));
		String tableName="customer_wallet"+DateUtil.dateToDateString(new Date(), "yyyyMM");
		CustomerWallet customerWallet=new CustomerWallet();
	    customerWallet.setTableName(tableName);
	    map.put("tableName", tableName);
	    map.put("isPartner", 1);
	    partnerTradeFlowDao.insertAccountDetail(map);
	}
}