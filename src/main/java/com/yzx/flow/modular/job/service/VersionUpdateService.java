package com.yzx.flow.modular.job.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yzx.flow.common.persistence.model.SystemVersion;
import com.yzx.flow.modular.system.dao.SystemVersionDao;


@Service
public class VersionUpdateService {
	@Autowired
	private SystemVersionDao systemVersionDao;
	/**
	 * 获取待更新的版本信息
	 * @param component
	 * @return
	 */
	public SystemVersion getSystemUpdateingVersionInfo(String component){
		return systemVersionDao.getSystemUpdateingVersionInfo(component);
	}
	
	/**
	 * 获取更新清单
	 * @return
	 */
	public List<SystemVersion> getSystemUpdateingVersionInfoList(){
		return systemVersionDao.getSystemUpdateingVersionInfoList();
	}
	
	/**
	 * 获取待回滚的版本信息
	 * @param component
	 * @return
	 */
	public SystemVersion getSystemRollBackingVersionInfo(String component) {
		return systemVersionDao.getSystemRollBackingVersionInfo(component);
	}

}
