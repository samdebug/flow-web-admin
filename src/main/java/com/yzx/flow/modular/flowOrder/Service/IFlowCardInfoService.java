package com.yzx.flow.modular.flowOrder.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import com.yzx.flow.common.exception.BussinessException;
import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.persistence.model.CExchangeurlInfo;
import com.yzx.flow.common.persistence.model.CustomerInfo;
import com.yzx.flow.common.persistence.model.FlowCardBatchInfo;
import com.yzx.flow.common.persistence.model.FlowCardInfo;
import com.yzx.flow.common.persistence.model.FlowCardSettlement;
import com.yzx.flow.common.persistence.model.OrderInfo;
import com.yzx.flow.common.persistence.model.RecordFlowCardInfo;
import com.yzx.flow.common.persistence.model.Staff;

public interface IFlowCardInfoService {

	Page<FlowCardInfo> pageQuery(Page<FlowCardInfo> page);

	void insert(FlowCardInfo data);

	FlowCardInfo get(Long cardId);

	void saveAndUpdate(FlowCardInfo data);

	void update(FlowCardInfo data);

	int delete(Long cardId);

	String selectMaxCardNo(String prefix);

	Long createCard(Map<String, Integer> countMap, Long customId, Long orderId, FlowCardBatchInfo flowCardBatchInfo,
			Staff staff);

	Long createCard(int count, OrderInfo orderinfo, Long productId, FlowCardBatchInfo flowCardBatchInfo, Staff staff);

	/**
	 * @param data
	 * @param orderDetilId
	 * @param staff
	 * @param customerInfoController
	 * @return
	 * @throws BussinessException 
	 */
	boolean updateCustomerInfoActiveCard(Long batchId) throws BussinessException;

	int inserAll(List<FlowCardInfo> flowCardInfoList);

	void updateCardState(FlowCardInfo flowCardInfo);

	/**
	 * @param batchId
	 * @param cause
	 * @return
	 * @throws BussinessException 
	 */
	boolean updateCauseAll(Long batchId, String cause) throws BussinessException;

	boolean updateCause(String[] ids, String cause);

	boolean updateCustomerInfo(CustomerInfo data, CExchangeurlInfo cExchangeurlInfo, Staff staff, boolean bFlag,
			boolean cFlag);

	List<FlowCardInfo> excelQuery(Map<String, Object> params);

	List<Map<String, Object>> convertListMap(List<FlowCardInfo> list);

	List<FlowCardInfo> excelQueryByBatchId(Long batchId);

	/**
	 * 流量+ 卡激活结算
	 * 根据批次获取对应的流量卡信息
	 * 
	 * @param batchId 批次ID
	 * @throws BussinessException 
	 */
	void flowCardInfoActivatedBalance(Long batchId) throws BussinessException;

	/**
	 * 客户结算，扣减余额，生成出账流水
	 * @param flowCardBalance
	 * @param logName
	 * @param bool		区分客户余额的增减     bool == true ? - : +
	 */
	void customerSettlement(FlowCardSettlement flowCardBalance, String logName, boolean bool);

	/**
	 * 合作伙伴结算   扣减余额，生成出账流水
	 * @param flowCardBalance
	 * @param logName	操作用户
	 * @param bool		区分客户余额的增减     bool == true ? - : +
	 */
	void partnerSettlement(FlowCardSettlement flowCardBalance, String logName, boolean bool);

	/**
	 * 根据批次获取需回滚的客户余额
	 * @param batchId
	 * @return
	 * @throws BussinessException 
	 */
	FlowCardSettlement getFlowCardSettlementByCancel(Long batchId) throws BussinessException;

	/**
	 * 流量卡单个或多个作废余额回滚
	 * @param ids
	 * @param logName
	 */
	void cancelFlowCard(String[] ids, String logName);

	boolean getIsFlag(Long batchId) throws BussinessException;

	/**
	 * 获取对应作废卡的余额存入集合
	 * @param RecordFlowCardInfoList
	 * @param flowCardSettlement
	 * @return
	 */
	List<RecordFlowCardInfo> updateRecordFlowCardInfo(List<RecordFlowCardInfo> RecordFlowCardInfoList,
			FlowCardSettlement flowCardSettlement);

	/**
	 * 插入
	 * @param flowCardBalance	结算对应信息
	 * @param customerId		客户ID
	 * @param partnerId			合作伙伴ID
	 * @param logName			操作用户	
	 * @param bool				true : 余额+ ？ 余额-
	 */
	void insertCustomerBalanceDay(FlowCardSettlement flowCardBalance, Long customerId, Long partnerId, String logName,
			boolean bool);

	List<FlowCardInfo> selectActiveAndExchangeCardInfoList(Long orderDetailId);

	List<FlowCardInfo> selectByCardNo(String startCardNo, String endCardNo);

	void updateByCardNo(String startCardNo, String endCardNo, Date cardExp);

}