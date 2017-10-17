package com.yzx.flow.modular.system.service;

import java.util.List;

import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.persistence.model.SystemVersion;

public interface IUpgradeService {
	/**
	 * 展示页面查询
	 * @param page
	 * @return
	 */
	Page<SystemVersion> pageQuery(Page<SystemVersion> page);
	/**
	 * 通过版本号查询版本列表
	 * @param version
	 * @return
	 */
	List<SystemVersion> selectSystemVersionByVersion(String version);
	/**
	 * 通过版本号查询所属版本信息
	 * @param version
	 * @return
	 */
	SystemVersion selectParentByVersion(String version);
	/**
	 * 查询当前版本信息
	 * @return
	 */
	SystemVersion selectCurVersion();
	/**
	 * 插入或更新版本信息
	 * @param systemVersions
	 */
	void saveOrUpdate(List<SystemVersion> systemVersions);
	/**
	 * 升级请求
	 * @param version
	 * @param reserveTime
	 */
	void autoUpgrade(String version, String reserveTime);
	/**
	 * 查询可升级版本
	 * @return
	 */
	SystemVersion selectIsNewVersion();
}
