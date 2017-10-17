package com.yzx.flow.core.template.engine.base;

import com.yzx.flow.core.util.ToolUtil;

import org.apache.tomcat.jni.OS;
import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.resource.ClasspathResourceLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

/**
 * 项目模板生成 引擎
 *
 * @author liuyufeng
 * @date 2017-05-07 22:15
 */
public abstract class TemplateEngine extends AbstractTemplateEngine {

    protected GroupTemplate groupTemplate;

    public TemplateEngine() {
        initBeetlEngine();
    }

    public void initBeetlEngine() {
        Properties properties = new Properties();
        properties.put("RESOURCE.root", "");
        properties.put("DELIMITER_STATEMENT_START", "<%");
        properties.put("DELIMITER_STATEMENT_END", "%>");
        properties.put("HTML_TAG_FLAG", "##");
        Configuration cfg = null;
        try {
            cfg = new Configuration(properties);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ClasspathResourceLoader resourceLoader = new ClasspathResourceLoader();
        groupTemplate = new GroupTemplate(resourceLoader, cfg);
        groupTemplate.registerFunctionPackage("tool", new ToolUtil());
    }

    public void configTemplate(Template template){
        template.binding("controller", super.getControllerConfig());
        template.binding("context", super.getContextConfig());
    }

    public void generateFile(String template,String filePath){
        Template pageTemplate = groupTemplate.getTemplate(template);
        configTemplate(pageTemplate);
        File file = new File(filePath);
        File parentFile = file.getParentFile();
        if(!parentFile.exists()){
            parentFile.mkdirs();
        }
        OutputStream os = null; 
        try {
        	os = new FileOutputStream(file);
            pageTemplate.renderTo(os);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally{
        	try {
				os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
    }

    public void start() {
        generateController();
        generatePageHtml();
        generatePageAddHtml();
        generatePageEditHtml();
        generatePageJs();
        generatePageInfoJs();
    }

    protected abstract void generatePageEditHtml();

    protected abstract void generatePageAddHtml();

    protected abstract void generatePageInfoJs();

    protected abstract void generatePageJs();

    protected abstract void generatePageHtml();

    protected abstract void generateController();

}
