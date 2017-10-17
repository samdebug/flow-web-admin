package com.yzx.flow.core.aop.dbrouting;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.stereotype.Component;

//@Component
public class ChooseDataSource extends AbstractRoutingDataSource {
	Logger logger = LoggerFactory.getLogger(ChooseDataSource.class);

	
	@Override
	protected Object determineCurrentLookupKey() {
		String datasourceKey = HandleDataSource.getDataSource();
		if (datasourceKey == null)
			logger.debug("选择默认数据库");
		else
			logger.debug("选择key={}的数据库", datasourceKey);
		return datasourceKey;
	}
}
