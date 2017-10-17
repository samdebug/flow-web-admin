package com.yzx.flow.modular.system.service;

import com.yzx.flow.common.persistence.model.PendTaskSetting;
/**
 * 个性化设置服务
 * @author wxl
 * @data 2017-08-24 09:48
 *
 */
public interface IPendTaskSettingService {
	/**
	 * 获取个性化设置对象
	 * @param staffid
	 * @return
	 */
	 PendTaskSetting get(String staffid);
	
	 /**
	  * 个性化设置修改
	  * @param data
	  * @throws Exception
	  */
	 void saveAndUpdate(PendTaskSetting data) throws Exception;
}
