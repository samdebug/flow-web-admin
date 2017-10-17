package com.yzx.flow.template;

import com.yzx.flow.core.template.config.ContextConfig;
import com.yzx.flow.core.template.engine.SimpleTemplateEngine;
import com.yzx.flow.core.template.engine.base.TemplateEngine;

import java.io.IOException;

/**
 * 测试模板引擎
 *
 * @author liuyufeng
 * @date 2017-05-09 20:27
 */
public class TemplateGenerator {

    public static void main(String[] args) throws IOException {
        ContextConfig contextConfig = new ContextConfig();
        contextConfig.setBizChName("代码生成");
        contextConfig.setBizEnName("code");

        TemplateEngine templateEngine = new SimpleTemplateEngine();
        templateEngine.setContextConfig(contextConfig);
        templateEngine.start();
    }

}
