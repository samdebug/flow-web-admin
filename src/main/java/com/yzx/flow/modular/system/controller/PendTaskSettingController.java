package com.yzx.flow.modular.system.controller;

import com.yzx.flow.common.constant.tips.Tip;
import com.yzx.flow.common.controller.BaseController;
import com.yzx.flow.common.exception.BizExceptionEnum;
import com.yzx.flow.common.exception.BussinessException;
import com.yzx.flow.common.persistence.dao.UserMapper;
import com.yzx.flow.common.persistence.model.PendTaskSetting;
import com.yzx.flow.common.persistence.model.User;
import com.yzx.flow.core.shiro.ShiroKit;
import com.yzx.flow.core.shiro.ShiroUser;
import com.yzx.flow.modular.system.dao.UserMgrDao;
import com.yzx.flow.modular.system.service.IPendTaskSettingService;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 设置个性化参数控制器
 *
 * @author liuyufeng
 * @Date 2017-08-17 09:27:28
 */
@Controller
@RequestMapping("/pendTaskSetting")
public class PendTaskSettingController extends BaseController {
	
	@Autowired
	private IPendTaskSettingService pendTaskSettingService;
	
	@Autowired
	private UserMgrDao userMgrDao;
	
    private String PREFIX = "/system/pendTaskSetting/";

    /**
     * 跳转到设置个性化参数首页
     */
    @RequestMapping("")
    public String index(Model model) {
        return PREFIX + "pendTaskSetting.html";
    }

    /**
     * 跳转到添加设置个性化参数
     */
    @RequestMapping("/pendTaskSetting_add")
    public String pendTaskSettingAdd() {
        return PREFIX + "pendTaskSetting_add.html";
    }

    /**
     * 跳转到修改设置个性化参数
     */
    @RequestMapping("/pendTaskSetting_update/{pendTaskSettingId}")
    public String pendTaskSettingUpdate(@PathVariable Integer pendTaskSettingId, Model model) {
        return PREFIX + "pendTaskSetting_edit.html";
    }

    /**
     * 获取设置个性化参数列表
     */
    @RequestMapping(value = "/list")
    @ResponseBody
    public Object list(String condition) {
        return null;
    }

    /**
     * 新增设置个性化参数
     */
    @RequestMapping(value = "/add")
    @ResponseBody
    public Object add() {
        return super.SUCCESS_TIP;
    }

    /**
     * 删除设置个性化参数
     */
    @RequestMapping(value = "/delete")
    @ResponseBody
    public Object delete() {
        return SUCCESS_TIP;
    }


    /**
     * 修改设置个性化参数
     */
    @RequestMapping(value = "/update")
    @ResponseBody
    public Object update() {
        return super.SUCCESS_TIP;
    }

    /**
     * 设置个性化参数详情
     */
    @RequestMapping(value = "/detail")
    @ResponseBody
    public Object detail() {
        return null;
    }
    
    /**
     * 异步加载数据
     */
    @RequestMapping(value = "/get.ajax")
    @ResponseBody
    public Map<String, Object> get() {
    	//获取用户账号
    	String account = ShiroKit.getUser().account;
    	//获取用户user对象
    	User user = this.userMgrDao.getByAccount(account);
    	//获取个性化设置对象
    	PendTaskSetting pendTaskSetting = this.pendTaskSettingService.get(user.getId().toString());
    	Map<String, Object> result = new HashMap();
    	result.put("pendTaskSetting", pendTaskSetting);
    	result.put("user", user);
    	return result;
    }
    
    @RequestMapping(value = "/update.ajax")
    @ResponseBody
    public Tip update(PendTaskSetting data){
    	if(data == null) {
    		 throw new BussinessException(BizExceptionEnum.REQUEST_NULL);
    	} else {
			try {
				pendTaskSettingService.saveAndUpdate(data);
			} catch (Exception e) {
				e.printStackTrace();
			}
    		return SUCCESS_TIP;
    	}
    }
}
