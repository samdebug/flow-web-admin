package com.yzx.flow.modular.flowOrder.Service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.yzx.flow.common.exception.MyException;
import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.page.PageInfoBT;
import com.yzx.flow.common.persistence.model.CustomerInfo;
import com.yzx.flow.common.persistence.model.CustomerTradeFlow;
import com.yzx.flow.common.persistence.model.FlowAppInfo;
import com.yzx.flow.common.persistence.model.FlowOrderInfo;
import com.yzx.flow.common.persistence.model.FlowOrderRefundRecord;
import com.yzx.flow.common.persistence.model.PartnerTradeFlow;
import com.yzx.flow.common.util.Constant;
import com.yzx.flow.common.util.DateUtil;
import com.yzx.flow.core.aop.dbrouting.DataSource;
import com.yzx.flow.core.aop.dbrouting.DataSourceType;
import com.yzx.flow.core.shiro.ShiroKit;
import com.yzx.flow.core.util.ToolUtil;
import com.yzx.flow.modular.customer.service.ICustomerInfoService;
import com.yzx.flow.modular.customer.service.ICustomerTradeFlowService;
import com.yzx.flow.modular.flowOrder.Service.IFlowAppInfoService;
import com.yzx.flow.modular.flowOrder.Service.IFlowOrderInfoService;
import com.yzx.flow.modular.flowOrder.Service.RabbitMQService;
import com.yzx.flow.modular.partner.service.IPartnerTradeFlowService;
import com.yzx.flow.modular.system.dao.CallBackOrderInfoDao;
import com.yzx.flow.modular.system.dao.FlowOrderInfoDao;
import com.yzx.flow.modular.system.dao.FlowOrderInfoExceptionDao;
import com.yzx.flow.modular.system.dao.FlowOrderRefundRecordDao;
import com.yzx.flow.modular.system.dao.MobileFlowDispatchRecDao;





/**
 * <b>Title：</b>FlowOrderInfoService.java<br/> <b>Description：</b> <br/> <b>@author： </b>v5480<br/>
 * <b>@date：</b>2015-08-01 11:38:16<br/> <b>Copyright (c) 2015 szwisdom Tech.</b>
 */
@Service("flowOrderInfoService")
public class FlowOrderInfoServiceImpl implements IFlowOrderInfoService {
	private static Logger logger = LoggerFactory.getLogger(FlowOrderInfoServiceImpl.class);
    @Autowired
    private FlowOrderInfoDao flowOrderInfoDao;
    @Autowired
    private FlowOrderRefundRecordDao flowOrderRefundRecordDao;
    @Autowired
    @Qualifier("customerTradeFlowService")
    private ICustomerTradeFlowService customerTradeFlowService;
    @Autowired
    @Qualifier("partnerTradeFlowService")
    private IPartnerTradeFlowService partnerTradeFlowService;
    @Autowired
    @Qualifier("customerInfoService")
    private ICustomerInfoService  customerInfoService;
    @Autowired
    private MobileFlowDispatchRecDao mobileFlowDispatchRecDao;
    @Autowired
    private FlowOrderInfoExceptionDao flowOrderInfoExceptionDao;
    @Autowired
    private CallBackOrderInfoDao callBackOrderInfoDao;
    @Autowired
    private RabbitMQService rabbitMQService;
    @Autowired
    private IFlowAppInfoService flowAppInfoService;
    
    @Value("${flow_ordertable_startFlage}")
    private String flow_ordertable_startFlage; //分表后流量订单表标志起始位
    
    @DataSource(DataSourceType.READ)
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.flowOrder.Service.impl.IFlowOrderInfoService#pageQuery(com.yzx.flow.common.page.Page)
	 */
	@Override
	@Transactional(readOnly=true)
    public PageInfoBT<FlowOrderInfo> pageQuery(Page<FlowOrderInfo> page){
    	//根据查询的起始时间判定
        Object startTime = page.getParams().get("createStartTime");
        Object endTime = page.getParams().get("createEndTime");
    	if (null == startTime || StringUtils.isEmpty(startTime.toString())){
    		page.getParams().put("createStartTime",DateUtil.dateToDateString(new Date(), "yyyy-MM-dd 00:00:00"));//开始时间默认取今天0点
    	}
    	if (null == endTime || StringUtils.isEmpty(endTime.toString())){
    		page.getParams().put("createStartTime",DateUtil.dateToDateString(new Date(), "yyyy-MM-dd 59:59:59"));//结束默认取今天最后结束时间
    	}
    	String startTemp = DateUtil.stringToDateString(startTime.toString(), "yyyy-MM-dd hh:mm:ss", "yyyyMM");
    	String endTemp = DateUtil.stringToDateString(endTime.toString(), "yyyy-MM-dd hh:mm:ss", "yyyyMM");
    	if (!startTemp.equals(endTemp)) {
			throw new MyException("跨月查询暂不支持");
		}
    	//判断查询的表是否存在
    	if(Integer.parseInt(flow_ordertable_startFlage)>Integer.parseInt(startTemp)){
    		throw new MyException("查询的表不存在!");
    	}
        page.setAutoCountTotal(false);
    	//设置查询的表名
    	page.getParams().put("tableName", "flow_order_info_"+startTemp);
        page.setTotal(flowOrderInfoDao.countForPageNew(page.getParams()));
        List<FlowOrderInfo> list = flowOrderInfoDao.pageQueryNew(page);
       /* //合计
        int pageNo = page.getPage();
        if(!list.isEmpty()){
        	page.setPage(1);
        	FlowOrderInfo info = flowOrderInfoDao.pageQueryByPrice(page);
        	for (FlowOrderInfo flowOrderInfo : list) {
        		flowOrderInfo.setSumPrice(info.getSumPrice());
        		flowOrderInfo.setPartnerPrice(info.getPartnerPrice());
        		flowOrderInfo.setOperatorPrice(info.getOperatorPrice());
			}
        }
        page.setPage(pageNo);*/
        return new PageInfoBT<FlowOrderInfo>(list, page.getTotal());
    }

	@DataSource(DataSourceType.READ)
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.flowOrder.Service.impl.IFlowOrderInfoService#excelRefund(com.yzx.flow.common.page.Page)
	 */
	@Override
	@Transactional(readOnly=true)
    public List<FlowOrderRefundRecord> excelRefund(Page<FlowOrderRefundRecord> page) throws Exception {
        /*if (!StaffUtil.isAdmin()) {
            page.getParams().put("loginName", StaffUtil.getLoginName());
        }*/
        List<FlowOrderRefundRecord> list = flowOrderInfoDao.excelRefundQuery(page.getParams());
        return list;
    }
    
	@DataSource(DataSourceType.READ)
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.flowOrder.Service.impl.IFlowOrderInfoService#pageQueryForRefund(com.yzx.flow.common.page.Page)
	 */
	@Override
	@Transactional(readOnly=true)
    public PageInfoBT<FlowOrderInfo> pageQueryForRefund(Page<FlowOrderInfo> page) throws Exception {
       /* if (!StaffUtil.isAdmin()) {
            page.getParams().put("loginName", StaffUtil.getLoginName());
        }*/
        page.setAutoCountTotal(false);
        page.setTotal(flowOrderInfoDao.countForPage(page.getParams()));
        List<FlowOrderInfo> list = flowOrderInfoDao.pageQueryForRefund(page);
        return new PageInfoBT<FlowOrderInfo>(list,page.getTotal());
    }

	@DataSource(DataSourceType.READ)
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.flowOrder.Service.impl.IFlowOrderInfoService#pageQueryByPrice(com.yzx.flow.common.page.Page)
	 */
	@Override
	@Transactional(readOnly=true)
    public FlowOrderInfo pageQueryByPrice(Page<FlowOrderInfo> page){
    	return flowOrderInfoDao.pageQueryByPrice(page.getParams());
    }
    /* (non-Javadoc)
	 * @see com.yzx.flow.modular.flowOrder.Service.impl.IFlowOrderInfoService#insert(com.yzx.flow.common.persistence.model.FlowOrderInfo)
	 */
    @Override
	public void insert(FlowOrderInfo data) {
        flowOrderInfoDao.insert(data);
    }

	@DataSource(DataSourceType.READ)
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.flowOrder.Service.impl.IFlowOrderInfoService#get(java.lang.Long)
	 */
	@Override
	@Transactional(readOnly=true)
    public FlowOrderInfo get(Long orderId) {
		//获取订单入库时间戳
		String recordTime = orderId.toString().substring(0, 13);
		//入库时间
		String recordDate = DateUtil.getDateStr(new Long(recordTime), "yyyyMM");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("tableName", "flow_order_info_"+recordDate);
		params.put("orderId", orderId);
		FlowOrderInfo flowOrderInfo =  flowOrderInfoDao.selectByPrimaryKey(params);
		if(flowOrderInfo==null)
			flowOrderInfo = flowOrderInfoDao.selectByPrimaryKeyInHis(orderId);
		return flowOrderInfo;
    }

    /* (non-Javadoc)
	 * @see com.yzx.flow.modular.flowOrder.Service.impl.IFlowOrderInfoService#saveAndUpdate(com.yzx.flow.common.persistence.model.FlowOrderInfo)
	 */
    @Override
	public void saveAndUpdate(FlowOrderInfo data) {
        if (null != data.getOrderId()) {// 判断有没有传主键，如果传了为更新，否则为新增
            this.update(data);
        } else {
            this.insert(data);
        }
    }
    /* (non-Javadoc)
	 * @see com.yzx.flow.modular.flowOrder.Service.impl.IFlowOrderInfoService#refund(java.lang.String, java.lang.String)
	 */
    @Override
	@Transactional(rollbackFor = Exception.class)
    public void refund(String orderId,String remark) throws Exception{
    	if (ToolUtil.isEmpty(orderId)){
    		throw new MyException("orderId不能为空！");
    	}
    	FlowOrderInfo flowOrderInfo = get(Long.valueOf(orderId));
    	if (null == flowOrderInfo){
    		throw new MyException("根据orderId["+orderId+"]查找不到分发记录！");
    	}
    	FlowOrderRefundRecord flowOrderRefundRecord = flowOrderRefundRecordDao.selectByOrderId(orderId);
    	if (null != flowOrderRefundRecord){
    		throw new MyException("该记录已经退款过不能重复退款！");
    	}
    	CustomerInfo customerInfo = customerInfoService.get(flowOrderInfo.getEnterpriseId());
    	if (null == customerInfo){
    		throw new MyException("找不到对应的客户信息");
    	}
    	FlowOrderRefundRecord data = new FlowOrderRefundRecord();
    	data.setOrderId(Long.valueOf(orderId));
    	data.setRefundTime(new Date());
    	data.setRemark(remark);
    	data.setInputBy(ShiroKit.getUser().getAccount());
    	flowOrderRefundRecordDao.insert(data);
    	CustomerTradeFlow customerTradeFlow = new CustomerTradeFlow();
    	customerTradeFlow.setTradeType(Constant.TRADE_TYPE_SETTLEMENT);
    	customerTradeFlow.setTradeAmount(flowOrderInfo.getPrice());
    	customerTradeFlow.setCustomerId(flowOrderInfo.getEnterpriseId());
    	customerTradeFlow.setRemark("退款," + (ToolUtil.isNotEmpty(remark)?remark:""));
    	customerTradeFlowService._saveCustomerTradeFlow(customerTradeFlow,ShiroKit.getStaff() );
    	PartnerTradeFlow partnerTradeFlow = new PartnerTradeFlow();
    	partnerTradeFlow.setTradeType(Constant.TRADE_TYPE_SETTLEMENT);
    	partnerTradeFlow.setTradeAmount(flowOrderInfo.getPartnerBalancePrice());
    	partnerTradeFlow.setPartnerId(customerInfo.getPartnerId());
    	partnerTradeFlow.setRemark("退款," + (ToolUtil.isNotEmpty(remark)?remark:""));
    	partnerTradeFlowService._savePartnerTradeFlow(partnerTradeFlow, ShiroKit.getUser());
    }
    /* (non-Javadoc)
	 * @see com.yzx.flow.modular.flowOrder.Service.impl.IFlowOrderInfoService#update(com.yzx.flow.common.persistence.model.FlowOrderInfo)
	 */
    @Override
	public void update(FlowOrderInfo data) {
        int row = flowOrderInfoDao.updateByPrimaryKey(data);
        if(row==0)
        	flowOrderInfoDao.updateByPrimaryKeyInHis(data);
    }

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.flowOrder.Service.impl.IFlowOrderInfoService#reCallBack(java.lang.String)
	 */
	@Override
	public void reCallBack(String orderIds) {
		if(orderIds==null&&"".equals(orderIds))
			return;
		String[] ids = orderIds.split(",");
		List<Long> orderIdList = new ArrayList<>(); 
		for(String id:ids){
			if (StringUtils.isEmpty(id))
				continue;
			Long orderId = Long.valueOf(id);
			orderIdList.add(orderId);
		}
		//根据订单id获取订单入库时间戳
		String orderTime = orderIdList.get(0).toString().substring(0,13);
		//更新or 查询原始表参数
		Map<String, Object> paramsOld = getFlowOrderOldParams(orderIdList);
		//更新or 查询分表参数
		Map<String, Object> paramsNew =  getFlowOrderNewParams(orderTime,orderIdList);;
		
		//更新原表
		flowOrderInfoDao.reCallBack(paramsOld);
		//TODO 分表后订单修改
		flowOrderInfoDao.reCallBack(paramsNew);
		//查询原始表
		List<FlowOrderInfo> foiList  = flowOrderInfoDao.queryByOrderIds(paramsNew);
		//分表后数据查询
		List<FlowOrderInfo> foiList_new  = flowOrderInfoDao.queryByOrderIds_new(paramsNew);
		for(FlowOrderInfo flowOrderInfo:foiList){
			String flowAppId = String.valueOf(flowOrderInfo.getFlowAppId());
			String flag = "6".equals(flowOrderInfo.getStatus())? "true":"false";
			String taskId = flowOrderInfo.getOrderIdStr();
			String mobile = flowOrderInfo.getUsedMobile();
			String status = "6".equals(flowOrderInfo.getStatus())?"4":"5"; //回调给下游4是成功,5是失败
			String reportTime =  flowOrderInfo.getCheckTime()==null?"":DateUtil.dateToDateString(flowOrderInfo.getCheckTime());
			String reportCode = flowOrderInfo.getGwStatus();
			String outTradeNo = flowOrderInfo.getExtorderId();
			try {
				FlowAppInfo flowAppInfo = flowAppInfoService.get(flowOrderInfo.getFlowAppId());
				if (StringUtils.isEmpty(flowAppInfo.getCallbackUrl())) {
					flowOrderInfo.setFlag(flag);
					flowOrderInfo.setCpStatus(status);
					callBackOrderInfoDao.insert(flowOrderInfo);
				} else {
					rabbitMQService.sentToCTGW(flowAppId, flag, taskId, mobile, status, reportTime, reportCode,
							outTradeNo);
				}
			} catch (Exception e) {
				Map<String, Object> params =new HashMap<String, Object>();
				//更新原始表
				params.put("tableName", "flow_order_info");
				params.put("orderId", flowOrderInfo.getOrderId());
				flowOrderInfoDao.unReCallBack(params);
				//更新分表后的
				String  recordTime= flowOrderInfo.getOrderId().toString().substring(0,13);
				params.put("tableName", "flow_order_info_"+DateUtil.getDateStr(new Long(recordTime), "yyyyMM"));
				flowOrderInfoDao.unReCallBack(params);
				logger.error("发送OrderId="+flowOrderInfo.getOrderId()+"的回调信息到MQ失败", e);
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.flowOrder.Service.impl.IFlowOrderInfoService#reFailBack(java.lang.String)
	 */
	@Override
	public void reFailBack(String orderIds) {
		
		if(orderIds==null&&"".equals(orderIds))
			return;
		List<Long> orderIdList = new ArrayList<Long>();
		String[] orderidItems = orderIds.split(",");
		for (String id : orderidItems) {
			if(!StringUtils.isEmpty(id))
				orderIdList.add(Long.valueOf(id));
		}
		int rows = 0;
		//根据订单id获取订单入库时间戳
		String orderTime = orderIdList.get(0).toString().substring(0,13);
		//更新or 查询原始表参数
		Map<String, Object> paramsOld = getFlowOrderOldParams(orderIdList);
		//更新or 查询分表参数
		Map<String, Object> paramsNew = getFlowOrderNewParams(orderTime,orderIdList);
		if (orderIdList.size() > 0) {
			//更新原始表
			rows = flowOrderInfoDao.reFailBackList(paramsOld);
			//更新分表后的数据
			flowOrderInfoDao.reFailBackList(paramsNew);
			//更新异常订单表
			flowOrderInfoExceptionDao.reFailBack(orderIdList);
		}
		if (rows > 0) {
			//原表查询
			List<FlowOrderInfo> foiList = flowOrderInfoDao.queryByOrderIds(paramsOld);
			//分表后查询
			List<FlowOrderInfo> foiList_new = flowOrderInfoDao.queryByOrderIds_new(paramsNew);
			for (FlowOrderInfo flowOrderInfo : foiList) {
				String flowAppId = String.valueOf(flowOrderInfo.getFlowAppId());
				String flag = "false";
				String taskId = flowOrderInfo.getOrderIdStr();
				String mobile = flowOrderInfo.getUsedMobile();
				String status = "5"; // 回调给下游4是成功,5是失败
				String reportTime = flowOrderInfo.getCheckTime() == null ? ""
						: DateUtil.dateToDateString(flowOrderInfo.getCheckTime());
				String reportCode = flowOrderInfo.getGwStatus();
				String outTradeNo = flowOrderInfo.getExtorderId();
				try {
					FlowAppInfo flowAppInfo = flowAppInfoService.get(flowOrderInfo.getFlowAppId());
					if (StringUtils.isEmpty(flowAppInfo.getCallbackUrl())) {
						flowOrderInfo.setFlag(flag);
						flowOrderInfo.setCpStatus(status);
						callBackOrderInfoDao.insert(flowOrderInfo);
					} else {
						rabbitMQService.sentToCTGW(flowAppId, flag, taskId, mobile, status, reportTime, reportCode,
								outTradeNo);
					}
				} catch (Exception e) {
					logger.error("发送OrderId=" + flowOrderInfo.getOrderId() + "的回调信息到MQ失败", e);
				}
			}
		}
	}

	 

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.flowOrder.Service.impl.IFlowOrderInfoService#reSuccessBack(java.lang.String)
	 */
	@Override
	public void reSuccessBack(String orderIds) {
		if(orderIds==null&&"".equals(orderIds))
			return;
		List<Long> orderIdList  = new ArrayList<>();
		String[] orderidItems = orderIds.split(",");
		for (String id : orderidItems) {
			if(!StringUtils.isEmpty(id))
				orderIdList.add(Long.valueOf(id));
		}
		int rows = 0;
		//根据订单id获取订单入库时间戳
		String orderTime = orderIdList.get(0).toString().substring(0,13);
		//更新or 查询原始表参数
		Map<String, Object> paramsOld = getFlowOrderOldParams(orderIdList);
		//更新or 查询分表参数
		Map<String, Object> paramsNew = getFlowOrderNewParams(orderTime,orderIdList);
		if (orderIdList.size() > 0) {
			//更新订单原表数据
			rows = flowOrderInfoDao.reSuccessBack(paramsOld);
			//更新分表后订单数据
			rows = flowOrderInfoDao.reSuccessBack(paramsNew);
			flowOrderInfoExceptionDao.reFailBack(orderIdList);
		}
		if (rows > 0) {
			//原表数据查询
			List<FlowOrderInfo> foiList  = flowOrderInfoDao.queryByOrderIds(paramsOld);
			//分表后数据查询
			List<FlowOrderInfo> foiList_new  = flowOrderInfoDao.queryByOrderIds(paramsOld);
			for(FlowOrderInfo flowOrderInfo:foiList){
				String flowAppId = String.valueOf(flowOrderInfo.getFlowAppId());
				String flag = "true";
				String taskId = flowOrderInfo.getOrderIdStr();
				String mobile = flowOrderInfo.getUsedMobile();
				String status = "4"; //回调给下游4是成功,5是失败
				String reportTime =  flowOrderInfo.getCheckTime()==null?"":DateUtil.dateToDateString(flowOrderInfo.getCheckTime());
				String reportCode = flowOrderInfo.getGwStatus();
				String outTradeNo = flowOrderInfo.getExtorderId();
				try {
					FlowAppInfo flowAppInfo = flowAppInfoService.get(flowOrderInfo.getFlowAppId());
					if(StringUtils.isEmpty(flowAppInfo.getCallbackUrl())){
						flowOrderInfo.setFlag(flag);
						flowOrderInfo.setCpStatus(status);
						callBackOrderInfoDao.insert(flowOrderInfo);
					}else{
						rabbitMQService.sentToCTGW(flowAppId, flag, taskId, mobile, status, reportTime, reportCode, outTradeNo);
					}
				} catch (Exception e) {
					logger.error("发送OrderId="+flowOrderInfo.getOrderId()+"的回调信息到MQ失败", e);
				}
			}
		}
	}
    /* (non-Javadoc)
	 * @see com.yzx.flow.modular.flowOrder.Service.impl.IFlowOrderInfoService#reSend(java.lang.String)
	 */
    @Override
    @Transactional
    public void reSend(String orderIds) {
    	Map<String, Object> params = new HashMap<String,Object>();
		String[] orderidItems = orderIds.split(",");
		for (String orderId : orderidItems) {
			if(!StringUtils.isEmpty(orderId)){
				params.put("tableName", "flow_order_info");
				params.put("orderIds", orderId);
				//更新原表
				int i = flowOrderInfoDao.reSend(params);
				//更新分表后的数据
				params.put("tableName", "flow_order_info_"+ DateUtil.getDateStr(new Long(orderId.substring(0, 13)), "yyyyMM"));
				flowOrderInfoDao.reSend(params);
				if (i > 0) {//可重发
					//添加异常订单可重发
					FlowOrderInfo flowOrderInfo = get(Long.valueOf(orderId));
					flowOrderInfo.setGwErrorCode("");
					flowOrderInfo.setGwStatus("风控订单待重发");
					flowOrderInfo.setActiveDate(new Date());
					flowOrderInfoExceptionDao.insert(flowOrderInfo);
					params.clear();
					//更新下发记录
			    	params.put("sendCount", 1);
			    	params.put("recId", orderId + "_1");
			    	params.put("orderIds", orderId);
			    	mobileFlowDispatchRecDao.updateSendCountByOrderId(params);
				}
			}
		}
    }
   
    /* (non-Javadoc)
	 * @see com.yzx.flow.modular.flowOrder.Service.impl.IFlowOrderInfoService#selectByFlowAppId(java.lang.Long)
	 */
    @Override
	public List<FlowOrderInfo> selectByFlowAppId(Long flowAppId) {
        return flowOrderInfoDao.selectByFlowAppId(flowAppId);
    }
    
    
    @DataSource(DataSourceType.READ)
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.flowOrder.Service.impl.IFlowOrderInfoService#export(com.yzx.flow.common.page.Page)
	 */
	@Override
	@Transactional(readOnly=true)
	public Page<FlowOrderInfo> export(Page<FlowOrderInfo> page) throws Exception {
		/*if (!ShiroKit.isAdmin()) {
			page.getParams().put("loginName", StaffUtil.getLoginName());
		}*/
		return exportWithoutAuth(page);
	}
    
    /* (non-Javadoc)
	 * @see com.yzx.flow.modular.flowOrder.Service.impl.IFlowOrderInfoService#exportWithoutAuth(com.yzx.flow.common.page.Page)
	 */
    @Override
	public Page<FlowOrderInfo> exportWithoutAuth(Page<FlowOrderInfo> page) throws Exception {
		String dataType = (String) page.getParams().get("dataType");
		String tableName = "flow_order_info";
		if (dataType != null && "on".equalsIgnoreCase(dataType))
			tableName = "flow_order_info_his";
		page.getParams().put("tableName", tableName);

		page.setAutoCountTotal(false);
        page.setTotal(flowOrderInfoDao.countForPageNew(page.getParams()));
		List<FlowOrderInfo> list = flowOrderInfoDao.export(page);
		page.setDatas(list);
		return page;
    }
    
    @DataSource(DataSourceType.READ)
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.flowOrder.Service.impl.IFlowOrderInfoService#queryStuckChannelIds(int, int, java.util.List)
	 */
	@Override
	public List<String> queryStuckChannelIds(int inHours,int stuckRows,List<Long> channelIdsInEnable) {
    	Map<String,Object> parmas = new HashMap<>();
    	parmas.put("inHours", inHours);
    	parmas.put("stuckRows", stuckRows);
    	parmas.put("channelIdsInEnable", channelIdsInEnable);
		return flowOrderInfoDao.selectStuckChannelIds(parmas);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.flowOrder.Service.impl.IFlowOrderInfoService#queryCustomerIds(com.yzx.flow.common.page.Page)
	 */
	@Override
	public List<Long> queryCustomerIds(Page<FlowOrderInfo> page) {
		page.setAutoCountTotal(false);
        String dataType = (String) page.getParams().get("dataType");
        String tableName = "flow_order_info";
        if(dataType!=null&&"on".equalsIgnoreCase(dataType))
        	tableName = "flow_order_info_his";
        page.getParams().put("tableName", tableName);
        page.setRows(5000);
		return flowOrderInfoDao.queryCustomerIds(page);
	}
 
	/**
	 * 获取分表后更新数据参数
	 * @param recordTime 订单入库时间戳
	 * @param list 需要变更状态的订单id集合
	 * @return
	 */
	private Map<String, Object> getFlowOrderNewParams(String recordTime,List list){
	  Map<String, Object> paramsNew=new	HashMap<String,Object>();
		//将时间戳转换为时间
		String recordDate = DateUtil.getDateStr(new Long(recordTime), "yyyyMM");
		paramsNew.put("tableName", "flow_order_info_"+recordDate);
		paramsNew.put("list", list);
		return paramsNew;
	}
	
	/**
	 * 获取订单原始表更新参数
	 * @param list 需要变更状态的订单id集合
	 * @return
	 */
	private Map<String, Object> getFlowOrderOldParams(List list){
		 Map<String, Object> paramsOld=new	HashMap<String,Object>();
		paramsOld.put("tableName", "flow_order_info");
		paramsOld.put("list", list);
		return paramsOld;
	}
}
