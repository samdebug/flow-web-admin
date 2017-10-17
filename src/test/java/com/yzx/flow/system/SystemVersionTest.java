package com.yzx.flow.system;

import java.util.Date;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.yzx.flow.base.BaseJunit;
import com.yzx.flow.common.persistence.model.SystemVersion;
import com.yzx.flow.modular.system.dao.SystemVersionDao;

/**
 * 系统自动更新
 *
 * @author wxl
 * @date 2017-09-02 9:23
 */
public class SystemVersionTest extends BaseJunit{
	
	@Autowired
	SystemVersionDao systemVersionDao;
	
	//@Test
	public void insertTest() {
		SystemVersion systemVersion = new SystemVersion();
		systemVersion.setComponent("测试3");
		systemVersion.setVersion("1.0.5");
		systemVersion.setComponentUrl("xxxx");
		systemVersion.setCreateTime(new Date());
		systemVersion.setIsCurVersion(1);
		systemVersion.setScriptUrl("xxxxxxx");
		systemVersion.setSqlUrl("xxxxxxx");
		systemVersion.setStatus(0);
		systemVersion.setVersionNotes("测试版本");
		systemVersionDao.insert(systemVersion);
	}
}
