package com.yzx.flow.modular.flowOrder.Service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yzx.flow.common.exception.MyException;
import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.page.PageInfoBT;
import com.yzx.flow.common.persistence.model.AccessChannelGroup;
import com.yzx.flow.common.persistence.model.ExportAppInfo;
import com.yzx.flow.common.persistence.model.FlowAppInfo;
import com.yzx.flow.common.persistence.model.FlowOrderInfo;
import com.yzx.flow.common.persistence.model.OrderInfo;
import com.yzx.flow.common.util.RedisHttpUtil;
import com.yzx.flow.common.util.URLConstants;
import com.yzx.flow.core.aop.dbrouting.DataSource;
import com.yzx.flow.core.aop.dbrouting.DataSourceType;
import com.yzx.flow.modular.flowOrder.Service.IFlowAppInfoService;
import com.yzx.flow.modular.system.dao.AccessChannelGroupDao;
import com.yzx.flow.modular.system.dao.EXportAppInfoDao;
import com.yzx.flow.modular.system.dao.FlowAppInfoDao;
import com.yzx.flow.modular.system.dao.FlowOrderInfoDao;
import com.yzx.flow.modular.system.dao.FlowProductInfoDao;
import com.yzx.flow.modular.system.dao.OrderInfoDao;


/**
 * <b>Title：</b>FlowAppInfoService.java<br/>
 * <b>Description：</b> <br/>
 * <b>@author： </b>v5480<br/>
 * <b>@date：</b>2015-07-29 11:02:41<br/>
 * <b>Copyright (c) 2015 szwisdom Tech.</b>
 */
@Service("flowAppInfoService")
public class FlowAppInfoServiceImpl implements IFlowAppInfoService {
    @Autowired
    private FlowAppInfoDao flowAppInfoDao;

    @Autowired
    private OrderInfoDao orderInfoDao;

    @Autowired
    private FlowOrderInfoDao flowOrderInfoDao;

    @Autowired
    private FlowProductInfoDao productDao;

    @Autowired
    private EXportAppInfoDao exportAppInfoDao;

    @Autowired
    private AccessChannelGroupDao accessChannelGroupDao;

    /* (non-Javadoc)
	 * @see com.yzx.flow.modular.flowOrder.Service.impl.IFlowAppInfoService#pageQuery(com.yzx.flow.common.page.Page)
	 */
    @Override
	public PageInfoBT<FlowAppInfo> pageQuery(Page<FlowAppInfo> page) {
        List<FlowAppInfo> list = flowAppInfoDao.pageQuery(page);
        return new PageInfoBT<FlowAppInfo>(list, page.getTotal());
    }

    /* (non-Javadoc)
	 * @see com.yzx.flow.modular.flowOrder.Service.impl.IFlowAppInfoService#insert(com.yzx.flow.common.persistence.model.FlowAppInfo)
	 */
    @Override
	public void insert(FlowAppInfo data) {
        flowAppInfoDao.insert(data);
    }

    /* (non-Javadoc)
	 * @see com.yzx.flow.modular.flowOrder.Service.impl.IFlowAppInfoService#get(java.lang.Long)
	 */
    @Override
	public FlowAppInfo get(Long flowAppId) {
        return flowAppInfoDao.selectByPrimaryKey(flowAppId);
    }

    /* (non-Javadoc)
	 * @see com.yzx.flow.modular.flowOrder.Service.impl.IFlowAppInfoService#saveAndUpdate(com.yzx.flow.common.persistence.model.FlowAppInfo)
	 */
    @Override
	@Transactional(rollbackFor = Exception.class)
    public void saveAndUpdate(FlowAppInfo data) {
    	if(StringUtils.isEmpty(data.getOrderIdStr())){
    		throw new MyException("订单ID不能为空！");
		}
        Long orderId = Long.valueOf(data.getOrderIdStr());
        data.setOrderId(orderId);
        OrderInfo orderInfo = orderInfoDao.selectByPrimaryKey(orderId);
        if (null == orderInfo) {
            throw new MyException("订单ID[" + orderId + "]对应的订单记录不存在!");
        }
        if (null != data.getFlowAppId()) {// 判断有没有传主键，如果传了为更新，否则为新增
            this.update(data);
        } else {
            if (null != flowAppInfoDao.getFlowAppInfoByAppId(data.getAppId())) {
                throw new MyException("应用ID[" + data.getAppId() + "]不能重复!");
            }
            data.setCustomerId(orderInfo.getCustomerId());
            data.setAppKey(genAppkey());
            this.insert(data);
        }
    }

    private String genAppkey() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /* (non-Javadoc)
	 * @see com.yzx.flow.modular.flowOrder.Service.impl.IFlowAppInfoService#update(com.yzx.flow.common.persistence.model.FlowAppInfo)
	 */
    @Override
	public void update(FlowAppInfo data) {
    	flowAppInfoDao.updateByPrimaryKey(data);
		String result = RedisHttpUtil.sendGet(URLConstants.DEL_REDIS_URL,
				URLConstants.T_FLOW_APP_INFO,data.getFlowAppId()+"");
		if (!"OK".equals(result)) {
			throw new MyException("删除Redis中信息出错,其请求URL参数为:" + URLConstants.T_FLOW_APP_INFO+"\t"+data.getFlowAppId()+"");
		}
    }

    /* (non-Javadoc)
	 * @see com.yzx.flow.modular.flowOrder.Service.impl.IFlowAppInfoService#delete(java.lang.Long)
	 */
    @Override
	@Transactional
    public int delete(Long flowAppId) {
        FlowOrderInfo flowOrderInfo = new FlowOrderInfo();
        flowOrderInfo.setFlowAppId(flowAppId);
        if (flowOrderInfoDao.count(flowOrderInfo) > 0) {
            throw new MyException("FlowAppId[" + flowAppId + "]存在关联的流量分发网关数据不允许删除。");
        }
        int i = flowAppInfoDao.deleteByPrimaryKey(flowAppId);
		String result = RedisHttpUtil.sendGet(URLConstants.DEL_REDIS_URL,
				URLConstants.T_FLOW_APP_INFO,flowAppId+"");
		if (!"OK".equals(result)) {
			throw new MyException("删除Redis中信息出错,其请求URL参数为:" + URLConstants.T_FLOW_APP_INFO+"\t"+flowAppId+"");
		}
        return i;
    }

    /* (non-Javadoc)
	 * @see com.yzx.flow.modular.flowOrder.Service.impl.IFlowAppInfoService#getFlowAppInfoByAppId(java.lang.String)
	 */
    @Override
	public FlowAppInfo getFlowAppInfoByAppId(String appId) {
        FlowAppInfo appInfo = flowAppInfoDao.getFlowAppInfoByAppId(appId);
        if (appInfo == null) {
            throw new MyException("接入信息错误，无此APPID");
        }
        return appInfo;
    }

    /* (non-Javadoc)
	 * @see com.yzx.flow.modular.flowOrder.Service.impl.IFlowAppInfoService#getProductListByOrderId(java.lang.Long, java.lang.String)
	 */
    @Override
	public List<ExportAppInfo> getProductListByOrderId(Long orderId, String appId) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("orderId", orderId);
        map.put("appId", appId);
        List<ExportAppInfo> product = exportAppInfoDao.getProductListByOrderId(map);
        if (product.size() <= 0) {
            throw new MyException("产品错误");
        }
        return product;
    }

    /* (non-Javadoc)
	 * @see com.yzx.flow.modular.flowOrder.Service.impl.IFlowAppInfoService#getFlowAppInfo(com.yzx.flow.common.persistence.model.FlowAppInfo)
	 */
    @Override
	public List<FlowAppInfo> getFlowAppInfo(FlowAppInfo flowAppInfo) {
        return flowAppInfoDao.getFlowAppInfo(flowAppInfo);
    }

    /* (non-Javadoc)
	 * @see com.yzx.flow.modular.flowOrder.Service.impl.IFlowAppInfoService#getAppInfoByOrderId(java.lang.Long)
	 */
    @Override
	public FlowAppInfo getAppInfoByOrderId(Long orderId) {
        return flowAppInfoDao.selectAppInfoByOrderId(orderId);
    }

    /* (non-Javadoc)
	 * @see com.yzx.flow.modular.flowOrder.Service.impl.IFlowAppInfoService#getAppInfoListByOrderId(java.lang.Long)
	 */
    @Override
	public List<FlowAppInfo> getAppInfoListByOrderId(Long orderId) {
        return flowAppInfoDao.selectAppInfoListByOrderId(orderId);
    }
    
    /* (non-Javadoc)
	 * @see com.yzx.flow.modular.flowOrder.Service.impl.IFlowAppInfoService#getDispatchChannelList(java.lang.String)
	 */
    @Override
	public List<AccessChannelGroup> getDispatchChannelList(String dispatchChannel) {
        return accessChannelGroupDao.selectByDispatchChannel(dispatchChannel);
    }

    @DataSource(DataSourceType.READ)
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.flowOrder.Service.impl.IFlowAppInfoService#selectInfoByIdOrName(java.lang.String)
	 */
	@Override
	@Transactional(readOnly=true)
    public List<FlowAppInfo> selectInfoByIdOrName(String idOrName) {
        return flowAppInfoDao.selectInfoByIdOrName(idOrName);
    }
}