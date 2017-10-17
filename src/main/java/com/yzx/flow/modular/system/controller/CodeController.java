package com.yzx.flow.modular.system.controller;

import com.yzx.flow.common.controller.BaseController;
import com.yzx.flow.common.exception.BizExceptionEnum;
import com.yzx.flow.common.exception.BussinessException;
import com.yzx.flow.core.template.config.ContextConfig;
import com.yzx.flow.core.template.engine.SimpleTemplateEngine;
import com.yzx.flow.core.template.engine.base.TemplateEngine;
import com.yzx.flow.core.util.ToolUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 代码生成控制器
 *
 * @author liuyufeng
 * @Date 2017-05-23 18:52:34
 */
@Controller
@RequestMapping("/code")
public class CodeController extends BaseController {

    private String PREFIX = "/system/code/";

    /**
     * 跳转到代码生成首页
     */
    @RequestMapping("")
    public String index() {
        return PREFIX + "code.html";
    }

    /**
     * 代码生成
     */
    @RequestMapping(value = "/generate")
    @ResponseBody
    public Object add(String bizChName, String bizEnName, String path) {
        if(ToolUtil.isOneEmpty(bizChName,bizEnName)){
            throw new BussinessException(BizExceptionEnum.REQUEST_NULL);
        }
        ContextConfig contextConfig = new ContextConfig();
        contextConfig.setBizChName(bizChName);
        contextConfig.setBizEnName(bizEnName);
        if(ToolUtil.isNotEmpty(path)){
            contextConfig.setProjectPath(path);
        }

        TemplateEngine TemplateEngine = new SimpleTemplateEngine();
        TemplateEngine.setContextConfig(contextConfig);
        TemplateEngine.getControllerConfig()
        	.setControllerPathTemplate(
    			String.format("\\src\\main\\java\\com\\yzx\\flow\\modular\\%s\\controller\\{}Controller.java", bizEnName)
        	);
        TemplateEngine.getControllerConfig().setPackageName(String.format("com.yzx.flow.modular.%s.controller", bizEnName));
        TemplateEngine.start();

        return super.SUCCESS_TIP;
    }
    
}
