package com.yzx.flow.common.inputSafe;

/**
 * 具体处理
 * @author Liulei
 *
 */
public interface IEscapeStrategy {
	
	
	/**
	 * 
	 * @param value 输入值
	 * @param key 关键词
	 * @return
	 */
	String escape(String value, String key);
	

}
