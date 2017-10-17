package com.yzx.flow.modular.partner.service;

import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.persistence.model.PartnerTradeFlow;
import com.yzx.flow.common.persistence.model.Staff;
import com.yzx.flow.core.shiro.ShiroUser;

public interface IPartnerTradeFlowService {

	/**
	 * 交易类型 1：结算 2：充值 3：授信
	 */
	Integer TRADETYPE_RECHARGE = 2;
	/**
	 * 交易类型 1：结算 2：充值 3：授信
	 */
	Integer TRADETYPE_CREDIT = 3;

	Page<PartnerTradeFlow> pageQuery(Page<PartnerTradeFlow> page);

	void insert(PartnerTradeFlow data);

	PartnerTradeFlow get(Long tradeFlowId);

	void saveAndUpdate(PartnerTradeFlow data);

	void update(PartnerTradeFlow data);

	int delete(Long tradeFlowId);

	/**
	 * 基于流水金额调整后合作伙伴余额显示不对的纠错方法
	 */
	void correctionPartnerTradeFlow(Long partnerId);

	/**
	 * 充值/信用额度调整
	 */
	void savePartnerTradeFlow(PartnerTradeFlow data, ShiroUser staff);

	void _savePartnerTradeFlow(PartnerTradeFlow data, ShiroUser staff);

}