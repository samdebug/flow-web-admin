package com.yzx.flow.common.excel;

/**
 * 包装文件名
 * @author Administrator
 *
 */
public class NameFileExcelFileTemplate implements ITemplateExcel {
	
	
	private String name;
	private ITemplateExcel template;
	
	/**
	 * 
	 * @param name
	 * @param template
	 */
	public NameFileExcelFileTemplate(String name, ITemplateExcel template) {
		this.name = name;
		this.template = template;
	}

	@Override
	public String getDisplayName() {
		return String.format("【%s】%s", this.name, template.getDisplayName());
	}

	@Override
	public String getTemplateName() {
		return template.getTemplateName();
	}

}
