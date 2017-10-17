package com.yzx.flow.modular.partner.service;

import java.text.ParseException;

import org.springframework.transaction.annotation.Transactional;

import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.persistence.model.PartnerBalanceMonth;
import com.yzx.flow.common.persistence.model.Staff;
import com.yzx.flow.core.shiro.ShiroUser;

public interface IPartnerBalanceMonthService {

	/**
	 * 交易类型  1：结算  2：充值  3：授信
	 */
	Integer TRADETYPE_SETTLEMENT = 1;

	Page<PartnerBalanceMonth> pageQuery(Page<PartnerBalanceMonth> page);

	void insert(PartnerBalanceMonth data);

	PartnerBalanceMonth get(Long balanceMonthId);

	void saveAndUpdate(PartnerBalanceMonth data);

	void update(PartnerBalanceMonth data);

	int delete(Long balanceMonthId);

	int insertPartnerBalanceMonth();

	/**
	 * 结算价格调整
	 */
	void savePartnerBalanceMonth(PartnerBalanceMonth data, ShiroUser current);

	int updatePartnerInfoBalanceStatus() throws ParseException;

}