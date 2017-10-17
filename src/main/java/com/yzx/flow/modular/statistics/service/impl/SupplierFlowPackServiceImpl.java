package com.yzx.flow.modular.statistics.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.page.PageInfoBT;
import com.yzx.flow.common.persistence.model.SupplierFlowPack;
import com.yzx.flow.common.util.DateUtil;
import com.yzx.flow.core.aop.dbrouting.DataSource;
import com.yzx.flow.core.aop.dbrouting.DataSourceType;
import com.yzx.flow.modular.statistics.service.ISupplierFlowPackService;
import com.yzx.flow.modular.system.dao.SupplierFlowPackDao;

@Service("supplierFlowPackService")
public class SupplierFlowPackServiceImpl implements ISupplierFlowPackService{
	
	@Autowired
	private SupplierFlowPackDao supplierFlowPackDao;
	
	
    @DataSource(DataSourceType.READ)
//	@Transactional(readOnly=true)
    public PageInfoBT<SupplierFlowPack> pageQuery(Page<SupplierFlowPack> page) throws Exception {
    	//开始时间
        Object beginCheckTime = page.getParams().get("beginCheckTime");
    	if (beginCheckTime==null || "".equals(beginCheckTime)){
    		beginCheckTime = DateUtil.dateToDateString(new Date(),DateUtil.YYYY_MM_EN );
    	}
    	beginCheckTime = beginCheckTime + "-01";
    	page.getParams().put("beginCheckTime",beginCheckTime);
    	//结束时间
    	Object endCheckTime = page.getParams().get("endCheckTime");
    	if (endCheckTime==null || "".equals(endCheckTime)){
    		endCheckTime = DateUtil.dateToDateString(new Date(),DateUtil.YYYY_MM_EN );
    	}
    	endCheckTime = endCheckTime + "-31";
    	page.getParams().put("endCheckTime",endCheckTime);

//    	int total = supplierFlowPackDao.countForPage(page.getParams());
    	List<SupplierFlowPack> list = supplierFlowPackDao.pageQuery(page);
    	PageInfoBT<SupplierFlowPack> resultPage = new PageInfoBT<>(list, page.getTotal());
        return resultPage;
    }
    
    @DataSource(DataSourceType.READ)
	@Transactional(readOnly=true)
    public Map<String, Object> queryTotal(Page<SupplierFlowPack> page) throws Exception {
    	//开始时间
        Object beginCheckTime = page.getParams().get("beginCheckTime");
    	if (beginCheckTime==null){
    		beginCheckTime = DateUtil.dateToDateString(new Date(),DateUtil.YYYY_MM_EN );
    	}
    	beginCheckTime = beginCheckTime + "-01";
    	page.getParams().put("beginCheckTime",beginCheckTime);
    	//结束时间
    	Object endCheckTime = page.getParams().get("endCheckTime");
    	if (endCheckTime==null){
    		endCheckTime = DateUtil.dateToDateString(new Date(),DateUtil.YYYY_MM_EN );
    	}
    	endCheckTime = endCheckTime + "-31";
    	page.getParams().put("endCheckTime",endCheckTime);
        page.setAutoCountTotal(false);
        
        Map<String, Object> map = supplierFlowPackDao.totalSum(page);
        map = map ==null ? new HashMap<String, Object>():map;
        return map;
    }

    @DataSource(DataSourceType.READ)
	@Transactional(readOnly=true)
    public Page<SupplierFlowPack> export(Page<SupplierFlowPack> page) throws Exception {
    	//开始时间
        Object beginCheckTime = page.getParams().get("beginCheckTime");
    	if (beginCheckTime==null){
    		beginCheckTime = DateUtil.dateToDateString(new Date(),DateUtil.YYYY_MM_EN );
    	}
    	beginCheckTime = beginCheckTime + "-01";
    	page.getParams().put("beginCheckTime",beginCheckTime);
    	//结束时间
    	Object endCheckTime = page.getParams().get("endCheckTime");
    	if (endCheckTime==null){
    		endCheckTime = DateUtil.dateToDateString(new Date(),DateUtil.YYYY_MM_EN );
    	}
    	endCheckTime = endCheckTime + "-31";
    	page.getParams().put("endCheckTime",endCheckTime);
        page.setAutoCountTotal(false);
        page.setTotal(supplierFlowPackDao.countForPage(page.getParams()));
        List<SupplierFlowPack> list = supplierFlowPackDao.pageQuery(page);
        int pageNo = page.getPage();
        page.setPage(pageNo);
        page.setDatas(list);
        return page;
    }
	
}
