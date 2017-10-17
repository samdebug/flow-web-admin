package com.yzx.flow.common.inputSafe.escape;

import com.yzx.flow.common.inputSafe.IEscape;
import com.yzx.flow.common.inputSafe.IEscapeStrategy;

/**
 * 模板方法
 * @author Liulei
 *
 */
public abstract class AbstractEscape implements IEscape {
	
	protected IEscapeStrategy strategy;
	
	protected String[] keys;
	

	@Override
	public String checkAndEscape(String value) {
		if ( value == null  || value.trim().isEmpty() || keys == null || keys.length < 1 ) 
			return value;
		
		// 检测输入值是否包含指定的关键词
		String temp = value.toLowerCase();
		for( String key : keys ) {
			if ( key == null || key.isEmpty() )
				continue;
			
			if ( temp.contains(key) ) {
				value = getEscapeStrategy().escape(value, key);
				temp = value.toLowerCase();// 避免转义之后又出现新的敏感字符
			}
		}
		return value;
	}

	@Override
	public IEscapeStrategy getEscapeStrategy() {
		return strategy;
	}
	
	
	

}
