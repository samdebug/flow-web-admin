package com.yzx.flow.common.excel;

import java.io.File;
import java.net.URL;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.yzx.flow.core.util.SpringContextHolder;
import com.yzx.flow.core.util.ToolUtil;
import com.yzx.flow.core.util.download.DownloadUtil;


@Component(TemplateExcelManager.BEAN_NAME)
@DependsOn("springContextHolder")
public class TemplateExcelManager implements InitializingBean {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(TemplateExcelManager.class);
	
	public static final String BEAN_NAME = "TemplateExcel-Manager";
	
	
	/**
	 * 模板文件地址存放目录名
	 */
	@Value("${template.excel.rootDir:excel}")
	private String templateRootDir = "excel";
	
	
	private String tempRootDirPath;// 临时存放路径
	private String templateRootDirPath;// 模板文件全路径
	
	
	public static TemplateExcelManager getInstance() {
		return SpringContextHolder.getBean(TemplateExcelManager.BEAN_NAME);
	}
	
	
	@Override
	public void afterPropertiesSet() throws Exception {
		
		this.templateRootDirPath = String.format("%s%s", TemplateExcelManager.class.getResource("/").getPath(), templateRootDir);
		this.tempRootDirPath = String.format("%s%s%s", this.templateRootDirPath, File.separator, "temp");
		
		File temp = new File(tempRootDirPath);
		if ( !temp.exists() || !temp.isDirectory() )
			temp.mkdirs();
		
		LOGGER.info("Init templateRootDirPath:{}\r\nInit tempRootDirPath:{}", this.templateRootDirPath, this.tempRootDirPath);
	}
	
	
	/**
	 * 根据模板文件创建对应的数据文件
	 * @param template
	 * @param model
	 * @return
	 */
	public File createExcelFile(ITemplateExcel template, Map<String, Object> model) {
		
		String tempPath = getTempPath(template);// 生成文件的临时存放路径
		ExcelUtil.createExcel(getTemplatePath(template), model, tempPath);
		
		return new File(tempPath);
	}
	
	/**
	 * 创建模板文件的对应的数据文件 并 提示客户端下载
	 * @param template
	 * @param model
	 * @throws Exception 
	 */
	public void createExcelFileAndDownload(ITemplateExcel template, Map<String, Object> model) throws Exception {
		
		File file = createExcelFile(template, model);
		DownloadUtil.downloadExcelFile(template.getDisplayName(), file);
	}
	
	
	/**
	 * 获取模板对应的path
	 * @param template
	 * @return
	 */
	public String getTemplatePath(ITemplateExcel template) {
		return String.format("%s%s%s", this.templateRootDirPath, File.separator, template.getTemplateName());
	}
	
	/**
	 * 获取指定模板对一个的数据文件临时存放路径
	 * @param template
	 * @return
	 */
	public String getTempPath(ITemplateExcel template) {
		return String.format("%s%s%s%s", this.tempRootDirPath, File.separator, ToolUtil.getRandomNum(), template.getDisplayName());
	}
	
	public String getTempRootDirPath() {
		return tempRootDirPath;
	}

	
	public String getTemplateRootDirPath() {
		return templateRootDirPath;
	}

}
