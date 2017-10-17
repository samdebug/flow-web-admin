package com.yzx.flow.common.excel;

public interface ITemplateExcel {
	
	
	/**
	 * 下载文件显示名称
	 * @return
	 */
	String getDisplayName();
	
	
	/**
	 * 模板文件名称
	 * @return
	 */
	String getTemplateName();
	
	
	
	static interface INameBuilder {
		
		/**
		 * 获取名称
		 * @return
		 */
		String getName();
		
	}

}
