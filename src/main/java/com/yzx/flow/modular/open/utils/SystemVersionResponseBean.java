package com.yzx.flow.modular.open.utils;

public class SystemVersionResponseBean {
	
	/**
	 * 版本号
	 */
	private String version;
	/**
	 * 组件名称
	 */
	private String componentName;
	/**
	 * 组件包大小
	 */
	private Double componentSize;
	/**
	 * 组件下载地址
	 */
	private String componentUrl;
	
	/**
	 * 升级脚本下载地址
	 */
     private String scriptUrl;
     /**
      * sql脚本下载地址
      */
     private String sqlUrl;
     /**
      *  版本更新说明
      */
     private String versionNotes;
     /**
      * 
      */
     private Integer isNeedUpgrade;
     
	public SystemVersionResponseBean() {
		
	}

	public SystemVersionResponseBean(String version, String componentName,
			String componentUrl, String scriptUrl, String sqlUrl,
			String versionNotes, Integer isNeedUpgrade, Double componentSize) {
		super();
		this.version = version;
		this.componentName = componentName;
		this.componentUrl = componentUrl;
		this.scriptUrl = scriptUrl;
		this.sqlUrl = sqlUrl;
		this.versionNotes = versionNotes;
		this.isNeedUpgrade = isNeedUpgrade;
		this.componentSize = componentSize;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getComponentName() {
		return componentName;
	}

	public void setComponentName(String componentName) {
		this.componentName = componentName;
	}

	public String getComponentUrl() {
		return componentUrl;
	}

	public void setComponentUrl(String componentUrl) {
		this.componentUrl = componentUrl;
	}

	public String getScriptUrl() {
		return scriptUrl;
	}

	public void setScriptUrl(String scriptUrl) {
		this.scriptUrl = scriptUrl;
	}

	public String getSqlUrl() {
		return sqlUrl;
	}

	public void setSqlUrl(String sqlUrl) {
		this.sqlUrl = sqlUrl;
	}

	public String getVersionNotes() {
		return versionNotes;
	}

	public void setVersionNotes(String versionNotes) {
		this.versionNotes = versionNotes;
	}

	public Integer getIsNeedUpgrade() {
		return isNeedUpgrade;
	}

	public void setIsNeedUpgrade(Integer isNeedUpgrade) {
		this.isNeedUpgrade = isNeedUpgrade;
	}

	public Double getComponentSize() {
		return componentSize;
	}

	public void setComponentSize(Double componentSize) {
		this.componentSize = componentSize;
	}

}
