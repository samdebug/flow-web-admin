package com.yzx.flow.modular.customer.service;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.persistence.model.CustomerBalanceDay;
import com.yzx.flow.common.persistence.model.CustomerInfo;
import com.yzx.flow.common.persistence.model.FlowCardSettlement;
import com.yzx.flow.common.persistence.model.PartnerInfo;

public interface ICustomerBalanceDayService {

	/**
	 * 合作伙伴日账单查询
	 * @param page
	 * @return
	 */
	Page<CustomerBalanceDay> pageQueryPartner(Page<CustomerBalanceDay> page);

	/**
	 * 客户日账单导出
	 * @param page
	 * @return
	 */
	Page<CustomerBalanceDay> pageQueryPartnerMonth(Page<CustomerBalanceDay> page);

	/**
	 * 合作伙伴日账单导出
	 * @param page
	 * @return
	 */
	Page<CustomerBalanceDay> pageQueryPartnerMonthByPartner(Page<CustomerBalanceDay> page);

	Page<CustomerBalanceDay> pageQuery(Page<CustomerBalanceDay> page);

	void insert(CustomerBalanceDay data);

	CustomerBalanceDay get(Long balanceDayId);

	void saveAndUpdate(CustomerBalanceDay data);

	void update(CustomerBalanceDay data);

	int delete(Long balanceDayId);

	int getCustomerBalanceDay();

	List<CustomerBalanceDay> getPartnerCBDUsedByStatistics(CustomerBalanceDay info);

	List<CustomerBalanceDay> getCBDUsedByStatistics(CustomerBalanceDay info);

	/**
	 * 取得有日账单的合作伙伴
	 * @param info
	 * @return
	 */
	List<CustomerBalanceDay> selectPartnerCBD();

	/**
	 * 取得有日账单的客户
	 * @return
	 */
	List<CustomerBalanceDay> selectCustomerCBD();

	int insertCustomerTradeFlowInfo();

	int insertPartnerTraderFlowInfo();

	/**
	 * 账户结算
	 */
	void accountBalance();

	/**
	 * 取得交易总额
	 * @return
	 */
	double selectSUMTraderAmount(Long partnerId, String tradeStartTime, String tradeEndTime, Integer tradeType);

	CustomerBalanceDay genCBD(Long partnerId, String tradeStartTime, String tradeEndTime);

	/**
	 * 取得交易总额(客户)
	 * @return
	 */
	double selectCustomerSUMTraderAmount(Long customerId, String tradeStartTime, String tradeEndTime,
			Integer tradeType);

	CustomerBalanceDay genCustomerCBD(Long customerId, String tradeStartTime, String tradeEndTime);

	int getCustomerBalanceDaySept(Map<String, Object> map);

	/**
	 * 取得客户日账单之和
	 */
	double selectSUMCustomerAmount(Long customerId);

	/**
	 * 取得合作伙伴日账单之和
	 */
	double selectSUMPartnerAmount(Long partnerId);

	/**
	 * 统计九月份部署之前的日结算
	 * 统计结算 所扣客户金额，进行余额增减操作
	 */
	void accountBalanceSept(Map<String, Object> map);

	/**
	 * 日常流量加  订单结算
	 * @param map 
	 */
	void customerFlowJiaBill(Map<String, Object> map);

	CustomerBalanceDay insertCustomerBalanceDayInfo(FlowCardSettlement flowCardSettlement);

	FlowCardSettlement insertFlowCardSettlement(FlowCardSettlement flowCardSettlement);

	/**
	 * 日常流量包  订单结算
	 * @param map
	 */
	void customerAndPartnerBill(Map<String, Object> map);

	/**
	 * 指定时间区间内 客户结算
	 * @param map	时间区间
	 * @param customerList 旧数据
	 * @throws ParseException 
	 */
	void customerListBill(Map<String, Object> map, List<CustomerBalanceDay> customerList, String str)
			throws ParseException;

	/**
	 * 指定区间 合作伙伴结算操作
	 * @param map	时间区间
	 * @param partnerList	旧记录数据
	 * @throws ParseException 
	 */
	void partnerListBill(Map<String, Object> map, List<CustomerBalanceDay> partnerList, String str)
			throws ParseException;

	void updateCurrentAmount();

	boolean isSameDay(Date day1, Date day2);

	Map<String, String> getDate(String balanceDay);

	void customerTradeLog(Map<String, String> mapDate, Date time, CustomerBalanceDay customerBalanceDay,
			CustomerInfo customerInfo, String str);

	void partnerTradeLog(Map<String, String> mapDate, Date time, CustomerBalanceDay customerBalanceDay,
			PartnerInfo partnerInfo, String str);

	void correctionCustomerTradeFlow();

	void correctionPartnerTradeFlow();

}