package com.yzx.flow.modular.flowOrder.Service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.page.PageInfoBT;
import com.yzx.flow.common.persistence.model.FlowAppInfo;
import com.yzx.flow.common.persistence.model.FlowOrderInfo;
import com.yzx.flow.common.util.DateUtil;
import com.yzx.flow.core.aop.dbrouting.DataSource;
import com.yzx.flow.core.aop.dbrouting.DataSourceType;
import com.yzx.flow.modular.flowOrder.Service.IFlowOrderInfoExceptionService;
import com.yzx.flow.modular.flowOrder.Service.RabbitMQService;
import com.yzx.flow.modular.system.dao.CallBackOrderInfoDao;
import com.yzx.flow.modular.system.dao.FlowAppInfoDao;
import com.yzx.flow.modular.system.dao.FlowOrderInfoDao;
import com.yzx.flow.modular.system.dao.FlowOrderInfoExceptionDao;

@Service
public class FlowOrderInfoExceptionServiceImpl implements IFlowOrderInfoExceptionService {
	private static Logger logger = LoggerFactory.getLogger(FlowOrderInfoExceptionServiceImpl.class);
	@Autowired
	private FlowOrderInfoExceptionDao flowOrderInfoExceptionDao;
	@Autowired
	private FlowOrderInfoDao flowOrderInfoDao;
	@Autowired
	private FlowAppInfoDao flowAppInfoDao;
	@Autowired
    private CallBackOrderInfoDao callBackOrderInfoDao;
    @Autowired
    private RabbitMQService rabbitMQService;
	
	@DataSource(DataSourceType.READ)
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.flowOrder.Service.impl.IFlowOrderInfoExceptionService#pageQuery(com.yzx.flow.common.page.Page)
	 */
	@Override
	@Transactional(readOnly=true)
	public PageInfoBT<FlowOrderInfo> pageQuery(Page<FlowOrderInfo> page) {
		
        /*if (!StaffUtil.isAdmin()) { 未做处理 后面统一改
            page.getParams().put("loginName", StaffUtil.getLoginName());
        }*/
		page.setAutoCountTotal(false);
		page.setTotal(flowOrderInfoExceptionDao.countForPage(page.getParams()));
        List<FlowOrderInfo> list = flowOrderInfoExceptionDao.pageQuery(page);
        return new PageInfoBT<FlowOrderInfo>(list, page.getTotal());
	}
	
	@DataSource(DataSourceType.READ)
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.flowOrder.Service.impl.IFlowOrderInfoExceptionService#get(java.lang.Long)
	 */
	@Override
	@Transactional(readOnly=true)
    public FlowOrderInfo get(Long orderId) {
        return flowOrderInfoExceptionDao.selectByPrimaryKey(orderId);
    }
    
	
    
    /* (non-Javadoc)
	 * @see com.yzx.flow.modular.flowOrder.Service.impl.IFlowOrderInfoExceptionService#reFailBack(java.lang.String)
	 */
    @Override
	public boolean reFailBack(String orderIds) {
    	//批量置失败删除异常订单表数据，订单表状态置失败
    	List<Long> orderIdList = new ArrayList<Long>();
    	String[] orderidItems=orderIds.split(",");
    	for(String id:orderidItems){
    		if(!StringUtils.isEmpty(id)){
    			orderIdList.add(Long.valueOf(id));
    		}
    	}
    	if (orderIdList.size() > 0) {
    		//更新原表
    		flowOrderInfoDao.reFailBackList(getFlowOrderOldParams(orderIdList));
    		//更新分表后的表.
    		//根据订单id获取订单入库时间戳
			String orderTime = orderIdList.get(0).toString().substring(0,13);
    		flowOrderInfoDao.reFailBackList(getFlowOrderNewParams(orderTime, orderIdList ));
    		//修改异常订单
    		flowOrderInfoExceptionDao.reFailBack(orderIdList);
    		
    		//异常订单置失败需要写入MQ或者call_back_info回调
    		List<FlowOrderInfo> foiList = flowOrderInfoDao.queryByOrderIds(getFlowOrderOldParams(orderIdList));
    		List<FlowOrderInfo> foiList_new = flowOrderInfoDao.queryByOrderIds_new(getFlowOrderNewParams(orderTime, orderIdList ));
			for (FlowOrderInfo flowOrderInfo : foiList) {
				String flowAppId = String.valueOf(flowOrderInfo.getFlowAppId());
				String flag = "false";
				String taskId = flowOrderInfo.getOrderIdStr();
				String mobile = flowOrderInfo.getUsedMobile();
				String status = "5"; // 回调给下游4是成功,5是失败
				String reportTime = flowOrderInfo.getCheckTime() == null ? "": DateUtil.dateToDateString(flowOrderInfo.getCheckTime());
				String reportCode = flowOrderInfo.getGwStatus();
				String outTradeNo = flowOrderInfo.getExtorderId();
				try {
					FlowAppInfo flowAppInfo = flowAppInfoDao.selectByPrimaryKey(flowOrderInfo.getFlowAppId());
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
    		return true;
		}else{
			return false;
		}
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
