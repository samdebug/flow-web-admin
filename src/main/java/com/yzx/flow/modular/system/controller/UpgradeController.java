package com.yzx.flow.modular.system.controller;

import ch.qos.logback.classic.Logger;
import com.yzx.flow.common.annotion.Permission;
import com.yzx.flow.common.annotion.log.BussinessLog;
import com.yzx.flow.common.constant.Const;
import com.yzx.flow.common.constant.Dict;
import com.yzx.flow.common.constant.tips.Tip;
import com.yzx.flow.common.controller.BaseController;
import com.yzx.flow.common.exception.BizExceptionEnum;
import com.yzx.flow.common.exception.BussinessException;
import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.page.PageInfoBT;
import com.yzx.flow.common.persistence.model.SystemVersion;
import com.yzx.flow.core.shiro.ShiroKit;
import com.yzx.flow.modular.open.myTask.HeartData;
import com.yzx.flow.modular.system.service.IUpgradeService;

import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.ui.Model;

/**
 * 客户端自动升级控制器
 *
 * @author wxl
 * @Date 2017-08-30 19:27:49
 */
@Controller
@RequestMapping("/upgrade")
public class UpgradeController extends BaseController {
	
	//日志记录
	protected final static org.slf4j.Logger logger = LoggerFactory.getLogger(UpgradeController.class);
	
    private String PREFIX = "/system/upgrade/";
    
    @Autowired
    private IUpgradeService upgradeService;

    /**
     * 跳转到客户端自动升级首页
     */
    @RequestMapping("")
    public String index(Model model) {
    	SystemVersion curVersion = this.upgradeService.selectCurVersion();
    	model.addAttribute("curVersion", curVersion);
    	
    	SystemVersion systemVersion = this.upgradeService.selectIsNewVersion();
    	boolean flag = false;
    	if(null != systemVersion) {
    		flag = true;
    		model.addAttribute("newVersion", systemVersion);
    	}
    	model.addAttribute("flag", flag);
        return PREFIX + "upgrade.html";
    }

    /**
     * 获取客户端自动升级列表
     */
    @RequestMapping(value = "/list")
    @ResponseBody
    public Object list(Page<SystemVersion> page) {
    	Map<String, Object> params = new HashMap<String, Object>();
    	params.put("parentId", 0);
    	//设置默认排序为id ，逆序desc
		if(null == page.getSort()){
			page.setSort("version");
		}
		page.setParams(params);
    	Page<SystemVersion> pageQuery = this.upgradeService.pageQuery(page);
        return new PageInfoBT<SystemVersion>(pageQuery.getDatas(), page.getTotal());
    }
    
    /**
     * 升级请求
     */
    @RequestMapping("/autoUpgrade")
    @BussinessLog(value = "升级请求", key = "account", dict = Dict.UserDict)
    @Permission(Const.ADMIN_NAME)
    @ResponseBody
    public Tip autoUpgrade(String version, String reserveTime) {
    	if(version==null){
    		 throw new BussinessException(BizExceptionEnum.REQUEST_NULL);
    	}
    	//判断是否可以请求
    	this.upgradeService.autoUpgrade(version, reserveTime);
    	logger.info("用户："+ShiroKit.getLoginName()+"请求升级版本"+version+",预定升级时间为:"+reserveTime);
    	return SUCCESS_TIP;
    }
    
    /**
     * 客户端自动升级详情
     */
    @RequestMapping(value = "/detail/{version}/")
    public String detail(@PathVariable("version") String version ,Model model) {
    	SystemVersion systemVersion = this.upgradeService.selectParentByVersion(version);
    	if(null == systemVersion) {
    		throw new BussinessException(BizExceptionEnum.REQUEST_INVALIDATE);
    	} else {
    		model.addAttribute("systemVersion", systemVersion);
    		return PREFIX + "upgrade_detail.html";
    	}
    	
    }
}
