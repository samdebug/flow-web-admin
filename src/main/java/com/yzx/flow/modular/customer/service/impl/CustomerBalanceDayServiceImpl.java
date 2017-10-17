package com.yzx.flow.modular.customer.service.impl;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yzx.flow.common.exception.MyException;
import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.persistence.model.CustomerBalanceDay;
import com.yzx.flow.common.persistence.model.CustomerInfo;
import com.yzx.flow.common.persistence.model.CustomerTradeFlow;
import com.yzx.flow.common.persistence.model.FlowCardBatchInfo;
import com.yzx.flow.common.persistence.model.FlowCardSettlement;
import com.yzx.flow.common.persistence.model.PartnerInfo;
import com.yzx.flow.common.persistence.model.PartnerTradeFlow;
import com.yzx.flow.common.util.DateUtil;
import com.yzx.flow.common.util.RedisHttpUtil;
import com.yzx.flow.common.util.URLConstants;
import com.yzx.flow.modular.customer.service.ICustomerBalanceDayService;
import com.yzx.flow.modular.customer.service.ICustomerTradeFlowService;
import com.yzx.flow.modular.partner.service.IPartnerTradeFlowService;
import com.yzx.flow.modular.system.dao.CustomerBalanceDayDao;
import com.yzx.flow.modular.system.dao.CustomerInfoDao;
import com.yzx.flow.modular.system.dao.CustomerTradeFlowDao;
import com.yzx.flow.modular.system.dao.FlowCardBatchInfoDao;
import com.yzx.flow.modular.system.dao.FlowCardSettlementDao;
import com.yzx.flow.modular.system.dao.FlowOrderInfoExtDao;
import com.yzx.flow.modular.system.dao.PartnerInfoDao;
import com.yzx.flow.modular.system.dao.PartnerTradeFlowDao;


/**
 * 
 * <b>Title：</b>CustomerBalanceDayService.java<br/>
 * <b>Description：</b> <br/>
 * <b>@author： </b>v5480<br/>
 * <b>@date：</b>2015-09-02 10:17:41<br/>
 * <b>Copyright (c) 2015 szwisdom Tech.</b>
 * 
 */
@Service("customerBalanceDayService")
public class CustomerBalanceDayServiceImpl implements ICustomerBalanceDayService {
	@Autowired
	private CustomerBalanceDayDao customerBalanceDayDao;
	
	@Autowired
	private CustomerTradeFlowDao customerTraderFlowDao;
	
	@Autowired
	private PartnerTradeFlowDao partnerTraderFlowDao;
	
	@Autowired
	private CustomerInfoDao customerDao;
	
	@Autowired
	private PartnerInfoDao partnerDao;
	
	@Autowired
	private CustomerTradeFlowDao customerTradeFlowDao;
	
	@Autowired
	private PartnerTradeFlowDao partnerTradeFlowDao;
	
	@Autowired
	private FlowOrderInfoExtDao flowOrderInfoExtDao;
	
	@Autowired
	private FlowCardSettlementDao flowCardSettlementDao; 
	
	@Autowired
	private FlowCardBatchInfoDao flowCardBatchInfoDao;
	
	@Autowired
	private ICustomerTradeFlowService customerTraderFlowService;
	
	@Autowired
	private IPartnerTradeFlowService partnerTraderService;
	
	private static final Logger LOG = LoggerFactory.getLogger(CustomerBalanceDayServiceImpl.class);
	
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.customer.service.impl.ICustomerBalanceDayService#pageQueryPartner(com.yzx.flow.common.page.Page)
	 */
	@Override
	public Page<CustomerBalanceDay> pageQueryPartner(Page<CustomerBalanceDay> page) {
	    List<CustomerBalanceDay> list = customerBalanceDayDao.getPartnerBalanceDay(page);
	    page.setDatas(list);
	    return page;
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.customer.service.impl.ICustomerBalanceDayService#pageQueryPartnerMonth(com.yzx.flow.common.page.Page)
	 */
	@Override
	public Page<CustomerBalanceDay> pageQueryPartnerMonth(Page<CustomerBalanceDay> page) {
	    List<CustomerBalanceDay> list = customerBalanceDayDao.getPartnerBalanceDayMonth(page);
	    page.setDatas(list);
	    return page;
	}
	
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.customer.service.impl.ICustomerBalanceDayService#pageQueryPartnerMonthByPartner(com.yzx.flow.common.page.Page)
	 */
	@Override
	public Page<CustomerBalanceDay> pageQueryPartnerMonthByPartner(Page<CustomerBalanceDay> page) {
	    List<CustomerBalanceDay> list = customerBalanceDayDao.getPartnerBalanceDayMonthPartner(page);
	    page.setDatas(list);
	    return page;
	}
	
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.customer.service.impl.ICustomerBalanceDayService#pageQuery(com.yzx.flow.common.page.Page)
	 */
	@Override
	public Page<CustomerBalanceDay> pageQuery(Page<CustomerBalanceDay> page) {
		List<CustomerBalanceDay> list = customerBalanceDayDao.pageQuery(page);
		page.setDatas(list);
		return page;
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.customer.service.impl.ICustomerBalanceDayService#insert(com.yzx.flow.common.persistence.model.CustomerBalanceDay)
	 */
	@Override
	public void insert(CustomerBalanceDay data) {
		customerBalanceDayDao.insert(data);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.customer.service.impl.ICustomerBalanceDayService#get(java.lang.Long)
	 */
	@Override
	public CustomerBalanceDay get(Long balanceDayId) {
		return customerBalanceDayDao.selectByPrimaryKey(balanceDayId);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.customer.service.impl.ICustomerBalanceDayService#saveAndUpdate(com.yzx.flow.common.persistence.model.CustomerBalanceDay)
	 */
	@Override
	public void saveAndUpdate(CustomerBalanceDay data) {
		if (null != data.getBalanceDayId()) {// 判断有没有传主键，如果传了为更新，否则为新增
			this.update(data);
		} else {
			this.insert(data);
		}
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.customer.service.impl.ICustomerBalanceDayService#update(com.yzx.flow.common.persistence.model.CustomerBalanceDay)
	 */
	@Override
	public void update(CustomerBalanceDay data) {
		customerBalanceDayDao.updateByPrimaryKey(data);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.customer.service.impl.ICustomerBalanceDayService#delete(java.lang.Long)
	 */
	@Override
	public int delete(Long balanceDayId) {
		return customerBalanceDayDao.deleteByPrimaryKey(balanceDayId);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.customer.service.impl.ICustomerBalanceDayService#getCustomerBalanceDay()
	 */
	@Override
	public int getCustomerBalanceDay() {
		return customerBalanceDayDao.insertCustomerBalanceDay();
//		return customerBalanceDayDao.insertCustomerBalanceDaily();
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.customer.service.impl.ICustomerBalanceDayService#getPartnerCBDUsedByStatistics(com.yzx.flow.common.persistence.model.CustomerBalanceDay)
	 */
	@Override
	public List<CustomerBalanceDay> getPartnerCBDUsedByStatistics(CustomerBalanceDay info) {
	    return customerBalanceDayDao.getPartnerCBDUsedByStatistics(info);
	}
	
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.customer.service.impl.ICustomerBalanceDayService#getCBDUsedByStatistics(com.yzx.flow.common.persistence.model.CustomerBalanceDay)
	 */
	@Override
	public List<CustomerBalanceDay> getCBDUsedByStatistics(CustomerBalanceDay info) {
        return customerBalanceDayDao.getCBDUsedByStatistics(info);
    }
	
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.customer.service.impl.ICustomerBalanceDayService#selectPartnerCBD()
	 */
    @Override
	public List<CustomerBalanceDay> selectPartnerCBD() {
        return customerBalanceDayDao.selectPartnerCBD();
    }

    /* (non-Javadoc)
	 * @see com.yzx.flow.modular.customer.service.impl.ICustomerBalanceDayService#selectCustomerCBD()
	 */
    @Override
	public List<CustomerBalanceDay> selectCustomerCBD() {
        return customerBalanceDayDao.selectCustomerCBD();
    }

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.customer.service.impl.ICustomerBalanceDayService#insertCustomerTradeFlowInfo()
	 */
	@Override
	@Transactional
	public int insertCustomerTradeFlowInfo(){
		return customerTraderFlowDao.insertCustomerTradeFlowInfo();
	}
	
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.customer.service.impl.ICustomerBalanceDayService#insertPartnerTraderFlowInfo()
	 */
	@Override
	@Transactional
	public int insertPartnerTraderFlowInfo(){
		return partnerTraderFlowDao.insertPartnerTraderFlowInfo();
	}
	
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.customer.service.impl.ICustomerBalanceDayService#accountBalance()
	 */
	@Override
	public void accountBalance(){
		try {
			int rows = getCustomerBalanceDay();
			LOG.debug("*************客户日账单结算结束*********日账单明细新增记录条数："+rows);
			List<CustomerBalanceDay> customerBalanceList = customerBalanceDayDao.selectCustomerTraderPrice();
			for (CustomerBalanceDay customerBalanceDay : customerBalanceList) {
				CustomerInfo customerInfo = customerDao.getCustomerInfoByCustomerId(customerBalanceDay.getCustomerId());
				LOG.debug("[ "+customerInfo.getCustomerName()+" ]待扣减余额："+customerBalanceDay.getCustomerAmount());
				customerInfo.setBalance(customerInfo.getBalance().subtract(customerBalanceDay.getCustomerAmount()).setScale(2,BigDecimal.ROUND_HALF_UP));
				LOG.debug("[ "+customerInfo.getCustomerName()+" ]修改账户余额为："+customerInfo.getBalance());
				customerInfo.setCurrentAmount(customerInfo.getCurrentAmount().subtract(customerBalanceDay.getCustomerAmount()).setScale(2,BigDecimal.ROUND_HALF_UP));
				LOG.debug("[ "+customerInfo.getCustomerName()+" ]修改未确认金额为："+customerInfo.getCurrentAmount());
				String result = RedisHttpUtil.sendGet(URLConstants.DEL_REDIS_URL,
						URLConstants.T_CUSTOMER_INFO,customerInfo.getCustomerId()+"");
				if (!"OK".equals(result)) {
					throw new MyException("删除Redis中信息出错,其请求URL参数为:" + URLConstants.T_CUSTOMER_INFO+"\t"+customerInfo.getCustomerId());
				}	
				int con = customerDao.updateByPrimaryKeyByBill(customerInfo);
				LOG.debug("[ "+customerInfo.getCustomerName()+" ]账户余额修改影响行数为："+con);
				if(con == 1){
					CustomerTradeFlow data = new CustomerTradeFlow();
					CustomerInfo ci = customerDao.selectByPrimaryKey(customerBalanceDay.getCustomerId());
			        data.setTradeTime(new Date());
			        data.setBalance(customerInfo.getBalance());
			        data.setCreditAmount(ci.getCreditAmount());
			        data.setLoginName("admin");
			        data.setCustomerId(customerBalanceDay.getCustomerId());
			        data.setTradeType(1);
			        data.setTradeAmount(BigDecimal.valueOf(0).subtract(customerBalanceDay.getCustomerAmount()));
			        data.setOperatorName("结算");
			        data.setInputTime(new Date());
			        Date time = new SimpleDateFormat("yyyyMMdd").parse(customerBalanceDay.getBalanceDay());
			        String date = DateUtil.dateToDateString(time, "yyyy-MM-dd");
			        data.setRemark(customerBalanceDay.getBalanceDay() +" 客户账户流水"+BigDecimal.valueOf(0).subtract(customerBalanceDay.getCustomerAmount()));
			        int row = customerTradeFlowDao.insert(data);
			        LOG.debug("客户账户流水新增："+row);
			        
			        customerTraderFlowService.correctionCustomerTradeFlow(customerInfo.getCustomerId());
				}
			}
			List<CustomerBalanceDay> partnerBalanceList = customerBalanceDayDao.selectPartnerTraderPrices();
			for (CustomerBalanceDay customerBalanceDay : partnerBalanceList) {
				PartnerInfo partnerInfo = partnerDao.selectByPrimaryKey(customerBalanceDay.getPartnerId());
				LOG.debug("[ "+partnerInfo.getPartnerName()+" ]待扣减余额："+customerBalanceDay.getPartnerAmount());
				partnerInfo.setBalance(partnerInfo.getBalance().subtract(customerBalanceDay.getPartnerAmount()).setScale(2,BigDecimal.ROUND_HALF_UP));
				LOG.debug("[ "+partnerInfo.getPartnerName()+" ]修改账户余额为："+partnerInfo.getBalance());
				partnerInfo.setCurrentAmount(partnerInfo.getCurrentAmount().subtract(customerBalanceDay.getPartnerAmount()).setScale(2,BigDecimal.ROUND_HALF_UP));
				LOG.debug("[ "+partnerInfo.getPartnerName()+" ]修改账户余额为："+partnerInfo.getCurrentAmount());
				String result = RedisHttpUtil.sendGet(URLConstants.DEL_REDIS_URL,
						URLConstants.T_PARTNER_INFO, partnerInfo.getPartnerId()+"");
				if (!"OK".equals(result)) {
					throw new MyException("删除Redis中信息出错,其请求URL参数为:" + URLConstants.T_PARTNER_INFO+"\t"+partnerInfo.getPartnerId());
				}
				int con = partnerDao.updateByPrimaryKeyBill(partnerInfo);
				LOG.debug("[ "+partnerInfo.getPartnerName()+" ]账户余额修改影响行数为："+con);
				if(con == 1){
					PartnerTradeFlow data = new PartnerTradeFlow();
					// 新增交易流水记录
			        PartnerInfo pi = partnerDao.selectByPrimaryKey(customerBalanceDay.getPartnerId());
			        data.setTradeTime(new Date());
			        data.setBalance(partnerInfo.getBalance());
			        data.setCreditAmount(pi.getCreditAmount());
			        data.setPartnerId(customerBalanceDay.getPartnerId());
			        data.setTradeType(1);
			        data.setTradeAmount(BigDecimal.valueOf(0).subtract(customerBalanceDay.getPartnerAmount()));
			        data.setLoginName("admin");
			        data.setOperatorName("结算");
			        data.setInputTime(new Date());
			        Date time = new SimpleDateFormat("yyyyMMdd").parse(customerBalanceDay.getBalanceDay());
			        String date = DateUtil.dateToDateString(time, "yyyy-MM-dd");
			        data.setRemark(customerBalanceDay.getBalanceDay()+" 合作伙伴账户流水"+BigDecimal.valueOf(0).subtract(customerBalanceDay.getPartnerAmount()));
			        int row = partnerTradeFlowDao.insert(data);
			        LOG.debug("客户账户流水新增："+row);
			        
			        partnerTraderService.correctionPartnerTradeFlow(partnerInfo.getPartnerId());
				}
			}
			//更新结算客户及合作伙伴的未确认金额
			updateCurrentAmount();
		} catch (Exception e) {
			LOG.error("客户日结算异常:"+e.getMessage(),e);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.customer.service.impl.ICustomerBalanceDayService#selectSUMTraderAmount(java.lang.Long, java.lang.String, java.lang.String, java.lang.Integer)
	 */
	@Override
	public double selectSUMTraderAmount(Long partnerId, String tradeStartTime, String tradeEndTime, Integer tradeType) {
	    PartnerTradeFlow ptf = new PartnerTradeFlow();
        ptf.setPartnerId(partnerId);
        ptf.setTradeStartTime(tradeStartTime);
        ptf.setTradeEndTime(tradeEndTime);
        ptf.setTradeType(tradeType);
	    return partnerTraderFlowDao.selectSUMTraderAmount(ptf);
	}
	
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.customer.service.impl.ICustomerBalanceDayService#genCBD(java.lang.Long, java.lang.String, java.lang.String)
	 */
	@Override
	public CustomerBalanceDay genCBD(Long partnerId, String tradeStartTime, String tradeEndTime) {
	    CustomerBalanceDay cbd = new CustomerBalanceDay();
        cbd.setPartnerId(partnerId);
        cbd.setInputStartTime(tradeStartTime);
        cbd.setInputEndTime(tradeEndTime);
        return cbd;
	}
	
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.customer.service.impl.ICustomerBalanceDayService#selectCustomerSUMTraderAmount(java.lang.Long, java.lang.String, java.lang.String, java.lang.Integer)
	 */
    @Override
	public double selectCustomerSUMTraderAmount(Long customerId, String tradeStartTime, String tradeEndTime, Integer tradeType) {
        CustomerTradeFlow ctf = new CustomerTradeFlow();
        ctf.setCustomerId(customerId);
        ctf.setTradeStartTime(tradeStartTime);
        ctf.setTradeEndTime(tradeEndTime);
        ctf.setTradeType(tradeType);
        return customerTraderFlowDao.selectSUMTraderAmount(ctf);
    }
    
    /* (non-Javadoc)
	 * @see com.yzx.flow.modular.customer.service.impl.ICustomerBalanceDayService#genCustomerCBD(java.lang.Long, java.lang.String, java.lang.String)
	 */
    @Override
	public CustomerBalanceDay genCustomerCBD(Long customerId, String tradeStartTime, String tradeEndTime) {
        CustomerBalanceDay cbd = new CustomerBalanceDay();
        cbd.setCustomerId(customerId);
        cbd.setInputStartTime(tradeStartTime);
        cbd.setInputEndTime(tradeEndTime);
        return cbd;
    }
    /* (non-Javadoc)
	 * @see com.yzx.flow.modular.customer.service.impl.ICustomerBalanceDayService#getCustomerBalanceDaySept(java.util.Map)
	 */
    @Override
	public int getCustomerBalanceDaySept(Map<String,Object> map) {
		return customerBalanceDayDao.insertCustomerBalanceDaySept(map);
	}
    
    /* (non-Javadoc)
	 * @see com.yzx.flow.modular.customer.service.impl.ICustomerBalanceDayService#selectSUMCustomerAmount(java.lang.Long)
	 */
    @Override
	public double selectSUMCustomerAmount(Long customerId) {
        return customerTraderFlowDao.selectSUMTraderAmountByCustomerId(customerId);
    } 

    /* (non-Javadoc)
	 * @see com.yzx.flow.modular.customer.service.impl.ICustomerBalanceDayService#selectSUMPartnerAmount(java.lang.Long)
	 */
    @Override
	public double selectSUMPartnerAmount(Long partnerId) {
        return partnerTraderFlowDao.selectSUMTraderAmountByPartnerId(partnerId);
    } 
    

    /* (non-Javadoc)
	 * @see com.yzx.flow.modular.customer.service.impl.ICustomerBalanceDayService#accountBalanceSept(java.util.Map)
	 */
	@Override
	public void accountBalanceSept(Map<String,Object> map){
    	try {
    		String isFlagB = (String) map.get("isFlag_b");
    		if("y".equalsIgnoreCase(isFlagB)){
    			customerAndPartnerBill(map);
    		}
    		String isFlagK = (String) map.get("isFlag_k");
    		if(!"n".equalsIgnoreCase(isFlagK)){
    			customerFlowJiaBill(map);
    		}
		} catch (Exception e) {
			LOG.error("时间区间内   客户日结算异常"+e.getMessage(),e);
		}
    }
    
    /* (non-Javadoc)
	 * @see com.yzx.flow.modular.customer.service.impl.ICustomerBalanceDayService#customerFlowJiaBill(java.util.Map)
	 */
    @Override
	public void customerFlowJiaBill(Map<String, Object> map){
    	try {
        	map.put("status", "1");
    		//原 结算价格
    		List<CustomerBalanceDay> customerList = customerBalanceDayDao.getCustomerBalanceDayByTime(map);
    		List<CustomerBalanceDay> partnerList = customerBalanceDayDao.getPartnerBalanceDayByTime(map);
    		//删除指定时间之内的数据
    		customerBalanceDayDao.deleteCustomerBalanceDay(map);
    		
    		//重新插入对应的流量包结算信息
    		List<FlowCardBatchInfo> flowCardBatchList = flowCardBatchInfoDao.getFlowCardBatchListByTime(map);
    		if(flowCardBatchList.isEmpty()){
    			LOG.debug("当前时间段内 无对应的流量加卡激活数据，无结算");
    			return ;
    		}
    		String batchId = "";
    		for (int i = 0; i < flowCardBatchList.size(); i++) {
				if(i <= flowCardBatchList.size()-2){
					batchId+=flowCardBatchList.get(i).getBatchId()+",";
				}else{
					batchId+=flowCardBatchList.get(i).getBatchId();
				}
			}
    		LOG.debug("batchId:"+batchId);
    		List<CustomerBalanceDay> customerBalanceDayList = new ArrayList<CustomerBalanceDay>();
    		List<FlowCardSettlement> flowCardSettlementList = flowCardSettlementDao.getFlowCardRepeatSettlement(map);
    		for (FlowCardSettlement flowCardSettlement : flowCardSettlementList) {
    			customerBalanceDayList.add(insertCustomerBalanceDayInfo(flowCardSettlement));
			}
    		customerBalanceDayDao.insertCustomerDayBalance(customerBalanceDayList);
    		
    		//指定时间区间内 客户结算
    		customerListBill(map,customerList,"2");
    		
    		//指定区间 合作伙伴结算操作
    		partnerListBill(map,partnerList,"2");
    		
    		//清除集合，释放内存
    		customerList.clear();
    		partnerList.clear();
		} catch (Exception e) {
			LOG.error("重复结算:"+e.getMessage(),e);
		}
    }
    
    /* (non-Javadoc)
	 * @see com.yzx.flow.modular.customer.service.impl.ICustomerBalanceDayService#insertCustomerBalanceDayInfo(com.yzx.flow.common.persistence.model.FlowCardSettlement)
	 */
    @Override
	public CustomerBalanceDay insertCustomerBalanceDayInfo(FlowCardSettlement flowCardSettlement){
    	CustomerBalanceDay customerBalanceDay = new CustomerBalanceDay();
		customerBalanceDay = new CustomerBalanceDay();
		customerBalanceDay.setCreateTime(new Date());
		customerBalanceDay.setCreator("系统");
		customerBalanceDay.setCustomerId(flowCardSettlement.getCustomerId());
		customerBalanceDay.setPartnerId(flowCardSettlement.getPartnerId());
		customerBalanceDay.setBalanceDay(flowCardSettlement.getBalanceDate());
		customerBalanceDay.setPackageId(flowCardSettlement.getPackageId());
		customerBalanceDay.setProductId(flowCardSettlement.getProductId());
		customerBalanceDay.setProductName(flowCardSettlement.getProductName());
		customerBalanceDay.setMobileOperator("WY".equals(flowCardSettlement.getOperatorCode()) ? "YD/LT/DX" : flowCardSettlement.getOperatorCode());
		customerBalanceDay.setRemark("流量加 重复结算 生成账单");
		customerBalanceDay.setStatus(1);
		customerBalanceDay.setZone(flowCardSettlement.getZone());
		customerBalanceDay.setFlowAmount(flowCardSettlement.getCardFlow());
		customerBalanceDay.setCustomerBalancePrice(flowCardSettlement.getPrice());
		customerBalanceDay.setPartnerBalancePrice(flowCardSettlement.getSettPrice());
		customerBalanceDay.setOperatorBalancePrice(flowCardSettlement.getOperCostPrice());
		customerBalanceDay.setSendNum(flowCardSettlement.getCount());
		customerBalanceDay.setInputTime(new Date());
		customerBalanceDay.setCustomerAmount(flowCardSettlement.getCustomerPrice());
		customerBalanceDay.setPartnerAmount(flowCardSettlement.getPartnerPrice());
		return customerBalanceDay;
    }
    
    /* (non-Javadoc)
	 * @see com.yzx.flow.modular.customer.service.impl.ICustomerBalanceDayService#insertFlowCardSettlement(com.yzx.flow.common.persistence.model.FlowCardSettlement)
	 */
    @Override
	public FlowCardSettlement insertFlowCardSettlement(FlowCardSettlement flowCardSettlement){
    	FlowCardSettlement FlowCardSettlementInfo = new FlowCardSettlement();
    	FlowCardSettlementInfo.setBatchId(flowCardSettlement.getBatchId());
    	FlowCardSettlementInfo.setCardFlow(flowCardSettlement.getCardFlow());
    	FlowCardSettlementInfo.setCostPrice(flowCardSettlement.getCostPrice());
    	FlowCardSettlementInfo.setCount(flowCardSettlement.getCount());
    	FlowCardSettlementInfo.setCustomerId(flowCardSettlement.getCustomerId());
    	FlowCardSettlementInfo.setCustomerPrice(flowCardSettlement.getCustomerPrice());
    	FlowCardSettlementInfo.setOperCostPrice(flowCardSettlement.getCostPrice());
    	FlowCardSettlementInfo.setOrderId(flowCardSettlement.getOrderId());
    	FlowCardSettlementInfo.setPackageId(flowCardSettlement.getPackageId());
    	FlowCardSettlementInfo.setPartnerId(flowCardSettlement.getPartnerId());
    	FlowCardSettlementInfo.setPartnerPrice(flowCardSettlement.getPartnerPrice());
    	FlowCardSettlementInfo.setPrice(flowCardSettlement.getPartnerPrice());
    	FlowCardSettlementInfo.setProductId(flowCardSettlement.getPartnerId());
    	FlowCardSettlementInfo.setProductName(flowCardSettlement.getProductName());
    	FlowCardSettlementInfo.setSettPrice(flowCardSettlement.getSettPrice());
    	FlowCardSettlementInfo.setZone(flowCardSettlement.getZone());
    	return FlowCardSettlementInfo;
    }
    
    /* (non-Javadoc)
	 * @see com.yzx.flow.modular.customer.service.impl.ICustomerBalanceDayService#customerAndPartnerBill(java.util.Map)
	 */
    @Override
	public void customerAndPartnerBill(Map<String, Object> map){
    	try {
    		Map<String, Object> maps = new HashMap<String, Object>();
        	map.put("status", "0");
    		maps = map;
        	//清理扩展表数据
    		int c = flowOrderInfoExtDao.deleteFlowOrderInfoExt(map);
    		LOG.debug("流量分发扩展表数据删除返回："+c);
    		//插入扩展表数据
    		int cc = flowOrderInfoExtDao.insertFlowOrderExtInfoByOrderInfoSept(map);
    		LOG.debug("重新导入流量分发记录数据返回："+cc);
    		//原 结算价格
    		List<CustomerBalanceDay> customerList = customerBalanceDayDao.getCustomerBalanceDayByTime(map);
    		if(customerList.isEmpty()){
    			LOG.debug("统计区间 客户日结算无数据...."+map.get("startTime").toString()+"\t "+map.get("endTime").toString());
    		}
    		List<CustomerBalanceDay> partnerList = customerBalanceDayDao.getPartnerBalanceDayByTime(map);
    		if(partnerList.isEmpty()){
    			LOG.debug("统计区间 合作伙伴 日结算无数据...."+map.get("startTime").toString()+"\t "+map.get("endTime").toString());
    		}
    		int count = customerBalanceDayDao.deleteCustomerBalanceDay(map);
    		LOG.debug("删除指定区间内的日结算数据："+count);
    		int rows = getCustomerBalanceDaySept(maps);
    		LOG.debug("*************【九月】   客户日账单结算结束*********日账单明细新增记录条数："+rows);
    		LOG.debug("集合长度："+customerList.size()+"\t -- "+partnerList.size());
    		
    		//指定时间区间内 客户结算
    		customerListBill(map,customerList,"1");
    		
    		//指定区间 合作伙伴结算操作
    		partnerListBill(map,partnerList,"1");
    		
    		//清除集合，释放内存
    		customerList.clear();
    		partnerList.clear();
		} catch (Exception e) {
			LOG.error("重复结算:"+e.getMessage(),e);
		}
    }
	
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.customer.service.impl.ICustomerBalanceDayService#customerListBill(java.util.Map, java.util.List, java.lang.String)
	 */
	@Override
	@SuppressWarnings("deprecation")
	public void customerListBill(Map<String, Object> map,List<CustomerBalanceDay> customerList,String str) throws ParseException{
		//新结算价格
		List<CustomerBalanceDay> customerListByTime = customerBalanceDayDao.selectCustomerTraderPriceByTime(map);
		if(customerListByTime.isEmpty()){
			LOG.debug("客户新日账单为空...	直接退出。 ");
			return ;
		}
		for (CustomerBalanceDay customerBalanceDay : customerListByTime) {
			LOG.debug("待扣减的总额为："+customerBalanceDay.getCustomerAmount());
			CustomerInfo customerInfo = customerDao.getCustomerInfoByCustomerId(customerBalanceDay.getCustomerId());
			LOG.debug("[ "+customerInfo.getCustomerName()+" ]账户余额："+customerInfo.getBalance()+"\t 未确认金额："+customerInfo.getCurrentAmount());
			if(customerList.isEmpty()){
				LOG.debug("客户历史月账单为空... 直接插入数据");
				customerInfo.setBalance(customerInfo.getBalance().subtract(customerBalanceDay.getCustomerAmount()).setScale(2,BigDecimal.ROUND_HALF_UP));
				LOG.debug("[ "+customerInfo.getCustomerName()+" ]修改账户余额为："+customerInfo.getBalance());
				customerInfo.setCurrentAmount(customerInfo.getCurrentAmount().subtract(customerBalanceDay.getCustomerAmount()).setScale(2,BigDecimal.ROUND_HALF_UP));
				LOG.debug("[ "+customerInfo.getCustomerName()+" ]修改未确认金额为："+customerInfo.getCurrentAmount());
				String result = RedisHttpUtil.sendGet(URLConstants.DEL_REDIS_URL,
						URLConstants.T_CUSTOMER_INFO,customerInfo.getCustomerId()+"");
				if (!"OK".equals(result)) {
					throw new MyException("删除Redis中信息出错,其请求URL为:" + URLConstants.T_CUSTOMER_INFO+"\t"+customerInfo.getCustomerId());
				}	
				int con = customerDao.updateByPrimaryKeyByBill(customerInfo);
				LOG.debug("[ "+customerInfo.getCustomerName()+" ]账户余额修改影响行数为："+con);
				if(con == 1){
					CustomerTradeFlow data = new CustomerTradeFlow();
					CustomerInfo ci = customerDao.selectByPrimaryKey(customerBalanceDay.getCustomerId());
					Date times = DateUtil.transferDate(customerBalanceDay.getBalanceDay());
			        times.setDate(times.getDate()+2);
			        times.setHours(1);
			        data.setTradeTime(times);
			        data.setBalance(customerInfo.getBalance());
			        data.setTradeAmount(BigDecimal.valueOf(0).subtract(customerBalanceDay.getCustomerAmount()));
			        data.setCreditAmount(ci.getCreditAmount());
			        data.setCustomerId(customerBalanceDay.getCustomerId());
			        data.setLoginName("admin");
			        data.setOperatorName("结算");
			        data.setInputTime(new Date());
			        Date time = new SimpleDateFormat("yyyyMMdd").parse(customerBalanceDay.getBalanceDay());
			        int month = DateUtil.getMonth(time);
			        LOG.debug("时间："+customerBalanceDay.getBalanceDay()+"\t 月份："+month);
			        if("1".equals(str)){
			        	data.setTradeType(1);
			        	data.setRemark(customerBalanceDay.getBalanceDay()+" 日结算总额："+customerBalanceDay.getCustomerAmount());	
			        }else{
			        	data.setTradeType(4);
			        	data.setRemark(customerBalanceDay.getBalanceDay()+" 流量加结算："+customerBalanceDay.getCustomerAmount());
			        }
			        int row = customerTradeFlowDao.insert(data);
			        LOG.debug("[ "+customerInfo.getCustomerName()+" ]客户流水新增 ："+row);
			        
//			        customerTraderFlowService.correctionCustomerTradeFlow(customerInfo.getCustomerId());
				}
			}else{
				LOG.debug("客户历史日账单不为空... 新旧数据总额比较插入数据");
				for (CustomerBalanceDay customerBalanceDayList : customerList) {
					if(customerBalanceDay.getCustomerId().equals(customerBalanceDayList.getCustomerId()) && customerBalanceDay.getBalanceDay().equals(customerBalanceDayList.getBalanceDay())){
						LOG.debug("客户新结算金额为："+customerBalanceDay.getCustomerAmount());
						//新结算总额与原结算总额做比较  -1 小于 0等于  1 大于
						int num = customerBalanceDay.getCustomerAmount().compareTo(customerBalanceDayList.getCustomerAmount());
						LOG.debug("-----：新旧账单比较："+num);
						LOG.debug("客户账户余额："+customerInfo.getBalance() +"\t 历史总额："+customerBalanceDayList.getCustomerAmount() +"\t 新总额："+customerBalanceDay.getCustomerAmount());
						Date time = DateUtil.transferDate(customerBalanceDay.getBalanceDay());
						if(num == -1){	//新结算总额 【小于】原结算总额 客户账户+
							BigDecimal money = customerBalanceDayList.getCustomerAmount().subtract(customerBalanceDay.getCustomerAmount());
							LOG.debug(customerInfo.getCustomerName() + " 待操作余额："+money);
							customerInfo.setBalance(customerInfo.getBalance().add(money).setScale(2,BigDecimal.ROUND_HALF_UP));
							LOG.debug("[ "+customerInfo.getCustomerName()+" ]修改账户余额为："+customerInfo.getBalance().setScale(2,BigDecimal.ROUND_HALF_UP));
							customerInfo.setCurrentAmount(customerInfo.getCurrentAmount().add(money).setScale(2,BigDecimal.ROUND_HALF_UP));
							LOG.debug("[ "+customerInfo.getCustomerName()+" ]修改未确认金额为："+customerInfo.getCurrentAmount());
							int con = customerDao.updateByPrimaryKeyByBill(customerInfo);
							LOG.debug("[ "+customerInfo.getCustomerName()+" ]账户余额修改影响行数为："+con);
							
							Map<String, String> mapDate = getDate(customerBalanceDay.getBalanceDay());
							customerTradeLog(mapDate, time, customerBalanceDay, customerInfo,str);
							
						}else if(num == 1){	//新结算总额 【大于】原结算总额  客户账户 -
							BigDecimal money = customerBalanceDay.getCustomerAmount().subtract(customerBalanceDayList.getCustomerAmount());
							LOG.debug(customerInfo.getCustomerName() + " 待操作余额："+money);
							customerInfo.setBalance(customerInfo.getBalance().subtract(money).setScale(2,BigDecimal.ROUND_HALF_UP));
							LOG.debug("[ "+customerInfo.getCustomerName()+" ]修改账户余额为："+customerInfo.getBalance().setScale(2,BigDecimal.ROUND_HALF_UP));
							customerInfo.setCurrentAmount(customerInfo.getCurrentAmount().subtract(money).setScale(2,BigDecimal.ROUND_HALF_UP));
							LOG.debug("[ "+customerInfo.getCustomerName()+" ]修改未确认金额为："+customerInfo.getCurrentAmount());
							int con = customerDao.updateByPrimaryKeyByBill(customerInfo);
							LOG.debug("[ "+customerInfo.getCustomerName()+" ]账户余额修改影响行数为："+con);
							
							Map<String, String> mapDate = getDate(customerBalanceDay.getBalanceDay());
							customerTradeLog(mapDate, time, customerBalanceDay, customerInfo,str);
							
						}
					}
				}
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.customer.service.impl.ICustomerBalanceDayService#partnerListBill(java.util.Map, java.util.List, java.lang.String)
	 */
	@Override
	@SuppressWarnings("deprecation")
	public void partnerListBill(Map<String, Object> map,List<CustomerBalanceDay> partnerList,String str) throws ParseException{
		List<CustomerBalanceDay> partnerListByTime = customerBalanceDayDao.selectPartnerTraderPricesByTime(map);
		if(partnerListByTime.isEmpty()){
			LOG.debug("合作伙伴  新日账单为空...");
			return ;
		}
		for (CustomerBalanceDay customerBalanceDay : partnerListByTime) {
			LOG.debug("待扣减的总额为："+customerBalanceDay.getPartnerAmount());
			PartnerInfo partnerInfo = partnerDao.selectByPrimaryKey(customerBalanceDay.getPartnerId());
			LOG.debug("[ "+partnerInfo.getPartnerName()+" ]账户余额："+partnerInfo.getBalance()+"\t 未确认金额："+partnerInfo.getCurrentAmount());
			if(partnerList.isEmpty()){
				LOG.debug("合作伙伴  历史日账单为空...");
				partnerInfo.setBalance(partnerInfo.getBalance().subtract(customerBalanceDay.getPartnerAmount()).setScale(2,BigDecimal.ROUND_HALF_UP));
				LOG.debug("[ "+partnerInfo.getPartnerName()+" ]修改账户余额为："+partnerInfo.getBalance().setScale(2,BigDecimal.ROUND_HALF_UP));
				partnerInfo.setCurrentAmount(partnerInfo.getCurrentAmount().subtract(customerBalanceDay.getPartnerAmount()).setScale(2,BigDecimal.ROUND_HALF_UP));
				LOG.debug("[ "+partnerInfo.getPartnerName()+" ]修改待确认金额为："+partnerInfo.getCurrentAmount());
				String result = RedisHttpUtil.sendGet(URLConstants.DEL_REDIS_URL,
						URLConstants.T_PARTNER_INFO, partnerInfo.getPartnerId()+"");
				if (!"OK".equals(result)) {
					throw new MyException("删除Redis中信息出错,其请求URL参数为:" + URLConstants.T_PARTNER_INFO+"\t"+partnerInfo.getPartnerId());
				}
				int con = partnerDao.updateByPrimaryKeyBill(partnerInfo);
				LOG.debug("[ "+partnerInfo.getPartnerName()+" ]账户余额修改影响行数为："+con);
				if(con == 1){
					PartnerTradeFlow data = new PartnerTradeFlow();
					// 新增交易流水记录
			        PartnerInfo pi = partnerDao.selectByPrimaryKey(customerBalanceDay.getPartnerId());
			        Date times = DateUtil.transferDate(customerBalanceDay.getBalanceDay());
			        times.setDate(times.getDate()+2);
			        times.setHours(1);
			        data.setTradeTime(times);
			        data.setBalance(partnerInfo.getBalance());
			        data.setCreditAmount(pi.getCreditAmount());
			        data.setTradeAmount(BigDecimal.valueOf(0).subtract(customerBalanceDay.getPartnerAmount()));
			        data.setPartnerId(customerBalanceDay.getPartnerId());
			        data.setLoginName("admin");
			        data.setOperatorName("结算");
			        data.setInputTime(new Date());
//			        Date time = new SimpleDateFormat("yyyyMMdd").parse(customerBalanceDay.getBalanceDay());
//			        int month = DateUtil.getMonth(time);
			        if("1".equals(str)){
			        	data.setTradeType(1);
			        	data.setRemark(customerBalanceDay.getBalanceDay()+" 日结算总额："+customerBalanceDay.getPartnerAmount());
			        }else{
			        	data.setTradeType(4);
			        	data.setRemark(customerBalanceDay.getBalanceDay()+" 流量加结算总额："+customerBalanceDay.getPartnerAmount());
			        }
			        int row = partnerTradeFlowDao.insert(data);
			        LOG.debug("[ "+partnerInfo.getPartnerName()+" ]合作伙伴流水新增 ："+row);
			        
//			        partnerTraderService.correctionPartnerTradeFlow(partnerInfo.getPartnerId());
				}
			}else{
				for (CustomerBalanceDay customerBalanceDayList : partnerList) {
					if(customerBalanceDay.getPartnerId().equals(customerBalanceDayList.getPartnerId()) && customerBalanceDay.getBalanceDay().equals(customerBalanceDayList.getBalanceDay())){
						LOG.debug("合作伙伴新结算金额为："+customerBalanceDay.getPartnerAmount());
						int num = customerBalanceDay.getPartnerAmount().compareTo(customerBalanceDayList.getPartnerAmount());
						LOG.debug("-----：新旧账单比较："+num);
						LOG.debug("合作伙伴账户余额："+partnerInfo.getBalance() +"\t 新总额："+customerBalanceDay.getPartnerAmount() +"\t 历史总额："+customerBalanceDayList.getPartnerAmount());
						Date time = DateUtil.transferDate(customerBalanceDay.getBalanceDay());

						if(num == -1){		//新结算总额【小于】历史结算总额	-1表示小于,0是等于,1是大于.
							BigDecimal money = customerBalanceDayList.getPartnerAmount().subtract(customerBalanceDay.getPartnerAmount());
							LOG.debug(partnerInfo.getPartnerName() + " 待操作余额："+money);
							partnerInfo.setBalance(partnerInfo.getBalance().add(money).setScale(2,BigDecimal.ROUND_HALF_UP));
							LOG.debug("[ "+partnerInfo.getPartnerName()+" ]修改账户余额为："+partnerInfo.getBalance().setScale(2,BigDecimal.ROUND_HALF_UP));
							partnerInfo.setCurrentAmount(partnerInfo.getCurrentAmount().add(money).setScale(2,BigDecimal.ROUND_HALF_UP));
							LOG.debug("[ "+partnerInfo.getPartnerName()+" ]修改待确认金额为："+partnerInfo.getCurrentAmount());
							int con = partnerDao.updateByPrimaryKeyBill(partnerInfo);
							LOG.debug("[ "+partnerInfo.getPartnerName()+" ]账户余额修改影响行数为："+con);
							
							Map<String, String> mapDate = getDate(customerBalanceDay.getBalanceDay());
							partnerTradeLog(mapDate, time, customerBalanceDay, partnerInfo,str);

						}else if(num == 1){	//新结算总额【大于】历史结算总额	-1表示小于,0是等于,1是大于.
							BigDecimal money = customerBalanceDay.getPartnerAmount().subtract(customerBalanceDayList.getPartnerAmount());
							LOG.debug(partnerInfo.getPartnerName() + " 待操作余额："+money);
							partnerInfo.setBalance(partnerInfo.getBalance().subtract(money).setScale(2,BigDecimal.ROUND_HALF_UP));
							LOG.debug("[ "+partnerInfo.getPartnerName()+" ]修改账户余额为："+partnerInfo.getBalance().setScale(2,BigDecimal.ROUND_HALF_UP));
							partnerInfo.setCurrentAmount(partnerInfo.getCurrentAmount().subtract(money).setScale(2,BigDecimal.ROUND_HALF_UP));
							LOG.debug("[ "+partnerInfo.getPartnerName()+" ]修改待确认金额为："+partnerInfo.getCurrentAmount());
							int con = partnerDao.updateByPrimaryKeyBill(partnerInfo);
							LOG.debug("[ "+partnerInfo.getPartnerName()+" ]账户余额修改影响行数为："+con);
							
							Map<String, String> mapDate = getDate(customerBalanceDay.getBalanceDay());
							partnerTradeLog(mapDate, time, customerBalanceDay, partnerInfo,str);

						}
					}
				}
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.customer.service.impl.ICustomerBalanceDayService#updateCurrentAmount()
	 */
	@Override
	public void updateCurrentAmount(){
		//获取更新未确认金额开始结束时间
		String startTime = DateUtil.getYestDayBeginDate();
		String endTime = DateUtil.getCurDateTime();
		LOG.debug("startTime:"+startTime+"\t endTime:"+endTime);
		
		//重置所有客户及合作伙伴未确认金额为0
		List<Integer> del=customerDao.selectAllForRedisDel();
		for(Integer i:del){
			String result = RedisHttpUtil.sendGet(URLConstants.DEL_REDIS_URL,
					URLConstants.T_CUSTOMER_INFO, i+"");
			if (!"OK".equals(result)) {
				throw new MyException("删除Redis中信息出错,其请求URL参数为:" + URLConstants.T_CUSTOMER_INFO+"\t"+i);
			}	
		}
		customerDao.updateCurrentAmountForZero();
		List<Integer> dellist=partnerDao.selectAllForDelRedis();
		for(Integer i:dellist){
			String result = RedisHttpUtil.sendGet(URLConstants.DEL_REDIS_URL,
					URLConstants.T_PARTNER_INFO,i+"");
			if (!"OK".equals(result)) {
				throw new MyException("删除Redis中信息出错,其请求URL参数为:" + URLConstants.T_PARTNER_INFO+"\t"+i);
			}
		}
		partnerDao.updateCurrentAmountForZero();
		
		Map<String, String> map =new HashMap<String,String>();
		map.put("startTime", startTime);
		map.put("endTime", endTime);
		List<Integer> del1=customerDao.selectCurrentAmountForRedisDel(map);
		for(Integer i:del1){
			String result = RedisHttpUtil.sendGet(URLConstants.DEL_REDIS_URL,
					URLConstants.T_CUSTOMER_INFO, i+"");
			if (!"OK".equals(result)) {
				throw new MyException("删除Redis中信息出错,其请求URL参数为:" + URLConstants.T_CUSTOMER_INFO+"\t"+i);
			}			
		}
		int row1 = customerDao.updateCurrentAmount(map);
		List<Integer> partnerIdlist=partnerDao.selectCurrentAmountForDelRedis(map);
		for(Integer i:partnerIdlist){
			String result = RedisHttpUtil.sendGet(URLConstants.DEL_REDIS_URL,
					URLConstants.T_PARTNER_INFO,i+"");
			if (!"OK".equals(result)) {
				throw new MyException("删除Redis中信息出错,其请求URL参数为:" + URLConstants.T_PARTNER_INFO+"\t"+i);
			}
		}
		int row2 = partnerDao.updateCurrentAmount(map);
		LOG.debug("结算 更新客户、合作伙伴的未确认金额返回 --- 客户："+row1+"\t--合作伙伴："+row2);
	}
	
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.customer.service.impl.ICustomerBalanceDayService#isSameDay(java.util.Date, java.util.Date)
	 */
	@Override
	public boolean isSameDay(Date day1, Date day2) {
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	    String ds1 = sdf.format(day1);
	    String ds2 = sdf.format(day2);
	    if (ds1.equals(ds2)) {
	        return true;
	    } else {
	        return false;
	    }
	}
	
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.customer.service.impl.ICustomerBalanceDayService#getDate(java.lang.String)
	 */
	@Override
	@SuppressWarnings("deprecation")
	public Map<String, String> getDate(String balanceDay){
		
		Date time = DateUtil.transferDate(balanceDay);
		time.setDate(time.getDate()+2);
		String date = new SimpleDateFormat(DateUtil.YYYYMMDD_EN).format(time);
		
		Map<String, String> mapDate = new HashMap<String,String>();
		String a = date.substring(0, 4);
		String b = date.substring(4, 6);
		String c = date.substring(6, 8);
		mapDate.put("startTime", a+"-"+b+"-"+c+" 00:00:00");
		mapDate.put("endTime", a+"-"+b+"-"+c+" 23:59:59");
		return mapDate;
	}
	
	
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.customer.service.impl.ICustomerBalanceDayService#customerTradeLog(java.util.Map, java.util.Date, com.yzx.flow.common.persistence.model.CustomerBalanceDay, com.yzx.flow.common.persistence.model.CustomerInfo, java.lang.String)
	 */
	@Override
	@SuppressWarnings("deprecation")
	public void customerTradeLog(Map<String, String> mapDate, Date time,CustomerBalanceDay customerBalanceDay,CustomerInfo customerInfo,String str){
		mapDate.put("userId", customerInfo.getCustomerId()+"");
		CustomerTradeFlow data = customerTradeFlowDao.getCustomerTradeFlowByDate(mapDate);
		if(null != data){
			data.setBalance(customerInfo.getBalance());
	        data.setTradeAmount(BigDecimal.valueOf(0).subtract(customerBalanceDay.getCustomerAmount()));
	        data.setCreditAmount(customerInfo.getCreditAmount());
			int row = customerTradeFlowDao.updateByPrimaryKey(data);
	        LOG.debug("[ "+customerInfo.getCustomerName()+" ] 流水修改 ："+row);
		}else{
			data = new CustomerTradeFlow();
			
			time.setDate(time.getDate()+2);
			time.setHours(1);
			data.setTradeTime(time);
	        data.setBalance(customerInfo.getBalance());
	        data.setTradeAmount(BigDecimal.valueOf(0).subtract(customerBalanceDay.getCustomerAmount()));
	        data.setCreditAmount(customerInfo.getCreditAmount());
	        data.setCustomerId(customerBalanceDay.getCustomerId());
	        data.setLoginName("admin");
	        data.setOperatorName("结算");
	        data.setInputTime(new Date());
	        if("1".equals(str)){
	        	data.setTradeType(1);
	        	data.setRemark(customerBalanceDay.getBalanceDay()+" 账户余额结算："+BigDecimal.valueOf(0).subtract(customerBalanceDay.getCustomerAmount()));
	        }else{
	        	data.setTradeType(4);
	        	data.setRemark(customerBalanceDay.getBalanceDay()+" 流量加账户余额结算："+BigDecimal.valueOf(0).subtract(customerBalanceDay.getCustomerAmount()));
	        }
	        int row = customerTradeFlowDao.insert(data);
	        LOG.debug("[ "+customerInfo.getCustomerName()+" ] 流水新增 ："+row);
		}

		//基于流水金额调整后客户余额显示不对的纠错方法
//		customerTraderFlowService.correctionCustomerTradeFlow(customerInfo.getCustomerId());
	}
	
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.customer.service.impl.ICustomerBalanceDayService#partnerTradeLog(java.util.Map, java.util.Date, com.yzx.flow.common.persistence.model.CustomerBalanceDay, com.yzx.flow.common.persistence.model.PartnerInfo, java.lang.String)
	 */
	@Override
	@SuppressWarnings("deprecation")
	public void partnerTradeLog(Map<String, String> mapDate, Date time,CustomerBalanceDay customerBalanceDay,PartnerInfo partnerInfo,String str){
		mapDate.put("userId", partnerInfo.getPartnerId()+"");
		PartnerTradeFlow data = partnerTradeFlowDao.getPartnerTradeFlowByDate(mapDate);
		if(null != data){
	        data.setBalance(partnerInfo.getBalance());
	        data.setTradeAmount(BigDecimal.valueOf(0).subtract(customerBalanceDay.getPartnerAmount()));
	        data.setCreditAmount(partnerInfo.getCreditAmount());
	        data.setTradeType(1);
	        int row = partnerTradeFlowDao.updateByPrimaryKey(data);
	        LOG.debug("[ "+partnerInfo.getPartnerName()+" ] 流水修改 ："+row);
		}else{
			data = new PartnerTradeFlow();
			time.setDate(time.getDate()+2);
			time.setHours(1);
			data.setTradeTime(time);
	        data.setBalance(partnerInfo.getBalance());
	        data.setTradeAmount(BigDecimal.valueOf(0).subtract(customerBalanceDay.getPartnerAmount()));
	        data.setCreditAmount(partnerInfo.getCreditAmount());
	        data.setPartnerId(customerBalanceDay.getPartnerId());
	        data.setLoginName("admin");
	        data.setOperatorName("结算");
	        data.setInputTime(new Date());
	        if("1".equals(str)){
	        	data.setTradeType(1);
	        	data.setRemark(customerBalanceDay.getBalanceDay()+" 账户余额结算："+BigDecimal.valueOf(0).subtract(customerBalanceDay.getPartnerAmount()));
	        }else{
	        	data.setTradeType(4);
	        	data.setRemark(customerBalanceDay.getBalanceDay()+" 流量加账户余额结算："+BigDecimal.valueOf(0).subtract(customerBalanceDay.getPartnerAmount()));
	        }
	        int row = partnerTradeFlowDao.insert(data);
	        LOG.debug("[ "+partnerInfo.getPartnerName()+" ] 流水新增 ："+row);
		}
		
		//基于流水金额调整后合作伙伴余额显示不对的纠错方法
//		partnerTraderService.correctionPartnerTradeFlow(partnerInfo.getPartnerId());
	}
	
	@Autowired
	private CustomerInfoDao customerInfoDao;

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.customer.service.impl.ICustomerBalanceDayService#correctionCustomerTradeFlow()
	 */
	@Override
	public void correctionCustomerTradeFlow(){
		String[] lo = null;
		// TODO
		String customerId = "";
		// before
//		String customerId = SystemConfig.getInstance().getString("customer_id");
		if(StringUtils.isEmpty(customerId)){
			List<CustomerInfo> customerList = customerInfoDao.selectCustomerInfoList();
			if(!customerList.isEmpty()){
				lo = new String[customerList.size()];
				for (int i = 0; i < customerList.size(); i++) {
					lo[i] = customerList.get(i).getCustomerId()+"";
				}
			}
		}else{
			lo = customerId.split(",");
		}
		if(lo != null){
			for (String string : lo) {
				customerTraderFlowService.correctionCustomerTradeFlow(Long.parseLong(string));
			}
		}
	}
	
	@Autowired
	private PartnerInfoDao partnerInfoDao;
	
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.customer.service.impl.ICustomerBalanceDayService#correctionPartnerTradeFlow()
	 */
	@Override
	public void correctionPartnerTradeFlow(){
		
		List<Long> list = new ArrayList<Long>();
		// TODO
		String customerId = "";
		// before
//		String customerId = SystemConfig.getInstance().getString("customer_id");
		if(StringUtils.isEmpty(customerId)){
			List<CustomerInfo> customerList = customerInfoDao.selectCustomerInfoList();
			if(!customerList.isEmpty()){
				for (int i = 0; i < customerList.size(); i++) {
					if(!list.contains(customerList.get(i).getPartnerId())){
						list.add(customerList.get(i).getPartnerId());
					}
				}
			}
		}else{
			String[] lo = customerId.split(",");
			for (String string : lo) {
				CustomerInfo customerInfo = customerInfoDao.selectByPrimaryKey(Long.parseLong(string));
				if(null != customerInfo){
					if(!list.contains(customerInfo.getPartnerId())){
						list.add(customerInfo.getPartnerId());
					}
				}
			}
		}
		
		for (Long partnerId : list) {
//			customerTraderFlowService.correctionCustomerTradeFlow(partnerId);
			partnerTraderService.correctionPartnerTradeFlow(partnerId);
		}
		
	}
}
