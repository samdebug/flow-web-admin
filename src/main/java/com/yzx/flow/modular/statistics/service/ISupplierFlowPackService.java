package com.yzx.flow.modular.statistics.service;

import java.util.Map;

import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.page.PageInfoBT;
import com.yzx.flow.common.persistence.model.SupplierFlowPack;

public interface ISupplierFlowPackService {
	
    public  PageInfoBT<SupplierFlowPack> pageQuery(Page<SupplierFlowPack> page) throws Exception;
    
    public Map<String, Object> queryTotal(Page<SupplierFlowPack> page) throws Exception;

    public Page<SupplierFlowPack> export(Page<SupplierFlowPack> page) throws Exception ;
	
}
