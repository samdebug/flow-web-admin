package com.yzx.flow.modular.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.persistence.dao.PendTaskSettingMapper;
import com.yzx.flow.common.persistence.model.PendTaskSetting;
import com.yzx.flow.core.shiro.ShiroKit;
import com.yzx.flow.modular.system.service.IPendTaskSettingService;
/**
 *  菜单服务
 * @author wxl
 * @data 2017-08-24 09:48
 */
@Service("pendTaskSettingService")
public class PendTaskSettingServiceImpl implements IPendTaskSettingService {
  @Autowired
  private PendTaskSettingMapper pendTaskSettingMapper;
  
  //获取个性化设置对象
  public PendTaskSetting get(String staffid)
  {
    return this.pendTaskSettingMapper.selectByPrimaryKey(staffid);
  }
  
  //个性化设置插入
  @Transactional(rollbackFor={Exception.class})
  public void insert(PendTaskSetting data)
  {
    this.pendTaskSettingMapper.insert(data);
  }
  
  //个性化设置增加&修改
  @Transactional(rollbackFor={Exception.class})
  public void saveAndUpdate(PendTaskSetting data)
    throws Exception
  {
	//获取用户id
    //Staff staff = StaffUtil.getLoginStaff();
	Integer userId = ShiroKit.getUser().getId();
	//data.setStaffid(staff.getStaffId().toString());
	data.setStaffid(userId.toString());
    //PendTaskSetting pendTaskSetting = get(staff.getStaffId().toString());
	PendTaskSetting pendTaskSetting = get(userId.toString());
    if (null != pendTaskSetting) {
      update(data);
    } else {
      insert(data);
    }
  }
  
  //个性化设置分页查询
  public Page<PendTaskSetting> pageQuery(Page<PendTaskSetting> page)
  {
    List<PendTaskSetting> list = this.pendTaskSettingMapper.pageQuery(page);
    page.setDatas(list);
    return page;
  }
  
  //个性化设置修改
  @Transactional(rollbackFor={Exception.class})
  public void update(PendTaskSetting data)
  {
    this.pendTaskSettingMapper.updateByPrimaryKey(data);
  }
  
  @Transactional(rollbackFor={Exception.class})
  public int delete(String staffid)
  {
    return this.pendTaskSettingMapper.deleteByPrimaryKey(staffid);
  }
}
