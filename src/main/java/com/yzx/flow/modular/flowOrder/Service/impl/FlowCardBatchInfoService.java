package com.yzx.flow.modular.flowOrder.Service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.persistence.model.FlowCardBatchInfo;
import com.yzx.flow.modular.flowOrder.Service.IFlowCardBatchInfoService;
import com.yzx.flow.modular.system.dao.FlowCardBatchInfoDao;


/**
 * 
 * <b>Title：</b>FlowCardBatchInfoService.java<br/>
 * <b>Description：</b> <br/>
 * <b>@author： </b>v5480<br/>
 * <b>@date：</b>2015-07-29 11:02:41<br/>
 * <b>Copyright (c) 2015 szwisdom Tech.</b>
 * 
 */
@Service("flowCardBatchInfoService")
public class FlowCardBatchInfoService implements IFlowCardBatchInfoService{
    @Autowired
    private FlowCardBatchInfoDao flowCardBatchInfoDao;
    
    /* (non-Javadoc)
	 * @see com.yzx.flow.modular.flowOrder.Service.impl.IFlowCardBatchInfoService#pageQuery(com.yzx.flow.common.page.Page)
	 */
    public Page<FlowCardBatchInfo> pageQuery(Page<FlowCardBatchInfo> page) {
    	List<FlowCardBatchInfo> list = flowCardBatchInfoDao.pageQuery(page);
    	for (FlowCardBatchInfo flowCardBatchInfo:list) {
    		if(flowCardBatchInfo.getValidTime().getTime()<new Date().getTime()){
    			flowCardBatchInfo.setCardState(4);
    		}
		}
		page.setDatas(list);
        return page;
    }
    /* (non-Javadoc)
	 * @see com.yzx.flow.modular.flowOrder.Service.impl.IFlowCardBatchInfoService#insert(com.yzx.flow.common.persistence.model.FlowCardBatchInfo)
	 */
    public void insert(FlowCardBatchInfo data) {
        flowCardBatchInfoDao.insert(data);
    }
    /* (non-Javadoc)
	 * @see com.yzx.flow.modular.flowOrder.Service.impl.IFlowCardBatchInfoService#get(java.lang.Long)
	 */
    public FlowCardBatchInfo get(Long batchId) {
        return flowCardBatchInfoDao.selectByPrimaryKey(batchId);
    }
    /* (non-Javadoc)
	 * @see com.yzx.flow.modular.flowOrder.Service.impl.IFlowCardBatchInfoService#saveAndUpdate(com.yzx.flow.common.persistence.model.FlowCardBatchInfo)
	 */
    public void saveAndUpdate(FlowCardBatchInfo data){
    			if (null != data.getBatchId()){//判断有没有传主键，如果传了为更新，否则为新增
					this.update(data);
		}else{
			this.insert(data);
		}
    }
    /* (non-Javadoc)
	 * @see com.yzx.flow.modular.flowOrder.Service.impl.IFlowCardBatchInfoService#update(com.yzx.flow.common.persistence.model.FlowCardBatchInfo)
	 */
    public void update(FlowCardBatchInfo data) {
        flowCardBatchInfoDao.updateByPrimaryKey(data);
    }
    /* (non-Javadoc)
	 * @see com.yzx.flow.modular.flowOrder.Service.impl.IFlowCardBatchInfoService#delete(java.lang.Long)
	 */
    public int delete(Long batchId) {
        return flowCardBatchInfoDao.deleteByPrimaryKey(batchId);
    }
    }