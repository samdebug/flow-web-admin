package com.yzx.flow.modular.system.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xiaoleilu.hutool.util.NetUtil;
import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.persistence.model.SystemVersion;
import com.yzx.flow.common.util.DateUtil;
import com.yzx.flow.modular.system.dao.SystemVersionDao;
import com.yzx.flow.modular.system.service.IUpgradeService;

@Service("upgradeService")
public class UpgradeServiceImpl implements IUpgradeService {
	
	@Resource
	private SystemVersionDao systemVersionDao;

	@Value("${tomcatPort}")
	private String port;
	/**
	 * 展示页面查询
	 * @param page
	 * @return
	 */
	@Override
	public Page<SystemVersion> pageQuery(Page<SystemVersion> page) {
		List<SystemVersion> datas = systemVersionDao.pageQuery(page);
		page.setDatas(datas);
		return page;
	}
	/**
	 * 插入或更新版本信息
	 * @param systemVersions
	 */
	@Override
	@Transactional(rollbackFor={Exception.class})
	public void saveOrUpdate(List<SystemVersion> systemVersions) {
		List<SystemVersion> versions = systemVersionDao.selectSystemVersionByVersion(systemVersions.get(0).getVersion());
		if(versions.isEmpty()) {
			double total = 0 ;
			StringBuffer sBuffer = new StringBuffer() ;
			for(SystemVersion sVersion : systemVersions) {
				//获取客户端部署的ip地址，格式为：http：//内网ip:port/项目访问名/升级成功访问的接口路径
				sVersion.setDeploymentUrl("http://"+NetUtil.getLocalhostStr()+":"+port+"/"+sVersion.getComponent()+"/openUpgrade/1.0.0/updateStatus");
				sVersion.setParentId(1);
				systemVersionDao.insert(sVersion);
				total += sVersion.getComponentSize();
				sBuffer.append(sVersion.getVersionNotes()).append(";");
			}
			//父类
			SystemVersion systemVersion = new SystemVersion();
			systemVersion.setVersion(systemVersions.get(0).getVersion());
			//发布时间
			systemVersion.setCreateTime(new Date());
			systemVersion.setIsCurVersion(1);
			//有新版本升级
			systemVersion.setStatus(0);
			systemVersion.setParentId(0);
			systemVersion.setComponentSize(total);
			systemVersion.setVersionNotes(sBuffer.toString());
			systemVersionDao.insert(systemVersion);
		} else {
			//预留修改窗口
		}
	}
	/**
	 * 查询系统版本
	 * @param string
	 * @return
	 */
	@Override
	public SystemVersion selectCurVersion() {
		return systemVersionDao.selectCurVersion();
	}
	/**
	 * 升级请求
	 * @param version
	 * @param reserveTime
	 */
	@Override
	public void autoUpgrade(String version, String reserveTime) {
		List<SystemVersion> versions = systemVersionDao.selectSystemVersionByVersion(version);
		for(SystemVersion systemVersion : versions) {
			//将可升级组件设置为待升级状态
			if(systemVersion.getParentId() == 1 && systemVersion.getIsNeedUpgrade() == 1) {
				//未申请过升级的设置为升级状态
				if(null == systemVersion.getStatus()) {
					systemVersion.setStatus(1);
				}
			}
			if(systemVersion.getParentId() == 0) {
				//版本升级请求
				systemVersion.setStatus(1);
			}
			if(null == reserveTime || ("").equals(reserveTime)) {
				systemVersion.setReserveTime(new Date());
			} else {
				reserveTime += ":01";
				systemVersion.setReserveTime(DateUtil.getDate(reserveTime, "yyyy-MM-dd HH:mm:ss"));
			}
			systemVersionDao.update(systemVersion);
		}
	}
	
	/**
	 * 通过版本号查询版本列表
	 * @param version
	 * @return
	 */
	@Override
	public List<SystemVersion> selectSystemVersionByVersion(String version){
		return systemVersionDao.selectSystemVersionByVersion(version);
	}
	
	/**
	 * 通过版本号查询所属版本信息
	 * @param version
	 * @return
	 */
	@Override
	public SystemVersion selectParentByVersion(String version) {
		return systemVersionDao.selectParentByVersion(version);
	}
	@Override
	public SystemVersion selectIsNewVersion() {
		return systemVersionDao.selectisNewVersion();
	}
	
}
