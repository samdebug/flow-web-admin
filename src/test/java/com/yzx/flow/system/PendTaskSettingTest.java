package com.yzx.flow.system;

import com.yzx.flow.base.BaseJunit;
import com.yzx.flow.common.persistence.dao.PendTaskSettingMapper;
import com.yzx.flow.common.persistence.model.PendTaskSetting;
import com.yzx.flow.common.persistence.model.SystemVersion;
import com.yzx.flow.modular.system.dao.SystemVersionDao;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import javax.annotation.Resource;

/**
 * 个性化设置参数测试
 *
 * @author wxl
 * @date 2017-08-23 9:23
 */
public class PendTaskSettingTest extends BaseJunit {

    @Resource
    PendTaskSettingMapper pendTaskSettingMapper;
    
    @Autowired
    SystemVersionDao systemVersionDao;

    //@Test
    public void getPendTaskSettingTest() {
    	
    	PendTaskSetting pendTaskSetting = pendTaskSettingMapper.selectByPrimaryKey("105");
    	System.out.println(pendTaskSetting.getStaffid());
    	System.out.println(pendTaskSetting.getSendemail());
    	System.out.println(pendTaskSetting.getEmailsendtime());
    }
    
}
