package com.yzx.flow.modular.open.service.impl;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yzx.flow.common.persistence.model.RespSystemVersion;
import com.yzx.flow.common.persistence.model.SystemVersion;
import com.yzx.flow.modular.open.rest.UpgradeRestContorller;
import com.yzx.flow.modular.open.service.IOpenUpgradeService;
import com.yzx.flow.modular.system.dao.SystemVersionDao;
/**
 * 
 * 请求升级处理结果接收serviceImpl
 * @author wxl
 * @date 1017/08/31 15:04
 *
 */
@Service("openUpgradeService")
public class OpenUpgradeServiceImpl implements IOpenUpgradeService{
	
	//日志记录
	protected final static Logger logger = LoggerFactory.getLogger(IOpenUpgradeService.class);
	
	@Autowired
	private SystemVersionDao systemVersionDao;
	
	/**
	 * 升级请求处理结果处理
	 */
	@Override
	@Transactional(rollbackFor={Exception.class})
	public Integer UpgrdeByIdAndStatus(Integer id, Integer status) {
		systemVersionDao.upgradeById(id, status);
		logger.info("更新组件状态：组件id="+id+"	升级状态(2成功，3失败):"+status);
		//如果全部更新完，切换版本号
		Integer updateAll = checkSuccess(id);
		if(updateAll==0){
			//更改之前的版本号为1
			SystemVersion curVersion = systemVersionDao.selectCurVersion();
			curVersion.setIsCurVersion(1);
			systemVersionDao.update(curVersion);
			logger.info("版本："+curVersion.getVersion()+"更改为历史版本");
			//更改升级完成的版本号为当前版本0
			SystemVersion systemVersion = systemVersionDao.selectParentByVersion(systemVersionDao.selectById(id).getVersion());
			systemVersion.setIsCurVersion(0);
			systemVersion.setStatus(2);
			systemVersionDao.update(systemVersion);
			logger.info("版本："+systemVersion.getVersion()+"升级成功");
		} else if(updateAll == 2) {
			SystemVersion systemVersion = systemVersionDao.selectParentByVersion(systemVersionDao.selectById(id).getVersion());
			systemVersion.setStatus(3);
			systemVersionDao.update(systemVersion);
		}
		return updateAll;
	}

	@Override
	public SystemVersion selectById(Integer id) {
		return systemVersionDao.selectById(id);
	}
	
	/**
	 * 判断是否全部更新完成
	 * 0:更新完成，1更新未完成，2更新失败
	 * @return
	 */
	@Override
	public Integer checkSuccess(Integer id){
		List<SystemVersion> versions = systemVersionDao.selectSystemVersionByVersion(systemVersionDao.selectById(id).getVersion());
		for(SystemVersion systemVersion : versions) {
			Integer status = systemVersion.getStatus();
			//不是父类
			if(null != status && systemVersion.getParentId() == 1) {
				if(status ==1){
					return 1;
				} else if(status == 3) {
					//设置此版本更新失败
					logger.info("版本："+systemVersion.getVersion()+"升级失败");
					return 2;
				}
			}
		}
		//设置次版本更新完成
		return 0;
	}
	
}
