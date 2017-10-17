package com.yzx.flow.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * xss/sql关键词配置
 *
 * @author Liulei
 */
@Component
@ConfigurationProperties(prefix = EscapeProperties.PREFIX)
public class EscapeProperties {

    public static final String PREFIX = "escpae.inject";
	
    /**
     * sql关键词
     */
    private String sqlKey;
    
    /**
     * 一些受限关键字符
     */
    private String restrictKey;
    

	public String getSqlKey() {
		return sqlKey == null ? "" : sqlKey;
	}

	public void setSqlKey(String sqlKey) {
		this.sqlKey = sqlKey;
	}

	public String getRestrictKey() {
		return restrictKey == null ? "" : restrictKey;
	}

	public void setRestrictKey(String restrictKey) {
		this.restrictKey = restrictKey;
	}
    
    

}
