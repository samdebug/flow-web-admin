package com.yzx.flow.config.converter;

import java.util.Date;

import org.springframework.core.convert.converter.Converter;

/**
 * 扩展Converter
 * <pre>
 * 支持对输入的日期字符串的格式校验
 * </pre>
 * @author Liulei
 *
 */
public interface IString2DateConverter extends Converter<String, Date> {
	
	
	/**
	 * 是否支持当前输入的日期字符串格式
	 * @param source
	 * @return
	 */
	boolean isSupport(String source);
	

}
