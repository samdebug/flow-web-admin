package com.yzx.flow.modular.open.utils;

import java.util.List;

import com.yzx.flow.common.persistence.model.SystemVersion;
/**
 * 接收数据转换
 * @author wxl
 *
 */
public class ResponseTranscation {
	
	/**
	 * 将resData转换为systemVersions
	 * @param resData
	 * @param systemVersions
	 */
	public void transTo(List<SystemVersionResponseBean> resData, List<SystemVersion> systemVersions) {
		for(SystemVersionResponseBean sBean : resData){
			SystemVersion systemVersion = new SystemVersion();
			systemVersion.setVersion(sBean.getVersion());
			systemVersion.setComponent(sBean.getComponentName());
			systemVersion.setComponentSize(sBean.getComponentSize());
			systemVersion.setComponentUrl(sBean.getComponentUrl());
			systemVersion.setScriptUrl(sBean.getScriptUrl());
			systemVersion.setSqlUrl(sBean.getSqlUrl());
			systemVersion.setVersionNotes(sBean.getVersionNotes());
			systemVersion.setIsNeedUpgrade(sBean.getIsNeedUpgrade());
			systemVersions.add(systemVersion);
		}
	}


}
