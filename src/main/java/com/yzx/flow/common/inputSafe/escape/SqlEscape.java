package com.yzx.flow.common.inputSafe.escape;

import com.yzx.flow.common.inputSafe.IEscapeStrategy;

/**
 * sql 关键词检测及处理
 * @author Liulei
 *
 */
public class SqlEscape extends AbstractEscape {
	
	
	
	@Override
	public String checkAndEscape(String value) {
		if ( value == null  || value.trim().isEmpty() || keys == null || keys.length < 1 ) 
			return value;
		
		// 检测输入值是否包含指定的关键词
		String temp = value.toLowerCase();
		for( String key : keys ) {
			if ( key == null || key.isEmpty() )
				continue;
			
			if ( temp.contains(key + " ") ) {// sql 校验 通过 关键词+空格匹配
				value = getEscapeStrategy().escape(value, key);
				temp = value.toLowerCase();// 避免转义之后又出现新的敏感字符
			}
		}
		return value;
	}
	
	
	/**
	 * 关键词列表
	 * @param sqlKeys
	 * @param strategy
	 */
	public SqlEscape(String sqlKeys, IEscapeStrategy strategy) {
		if ( sqlKeys != null && !(sqlKeys = sqlKeys.trim()).isEmpty() ) {
			this.keys = sqlKeys.split(" ");
		}
		this.strategy = strategy;
	}

	
	
	
	

}
