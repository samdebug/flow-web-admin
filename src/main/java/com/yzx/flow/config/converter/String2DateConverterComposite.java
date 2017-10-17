package com.yzx.flow.config.converter;

import java.util.Date;
import java.util.regex.Pattern;

import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * String 转 Date的所有转换器复合类
 * @author Liulei
 *
 */
public enum String2DateConverterComposite implements IString2DateConverter {
	
	
	yyyy_MM_dd("yyyy-MM-dd", "\\d{4}-\\d{2}-\\d{2}"),
	yyyy_MM_dd_HH_mm("yyyy-MM-dd HH:mm", "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}"),
	yyyy_MM_dd_HH_mm_ss("yyyy-MM-dd HH:mm:ss", "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}"),
	
	;
	
	private static final Logger LOG = LoggerFactory.getLogger(String2DateConverterComposite.class);
	
	private Pattern datePattern;// 对一个日期格式的简单正则
	private String dateFormat;// 日期格式化
	
	
	private String2DateConverterComposite(String dateFormat, String datePatternStr) {
		this.dateFormat = dateFormat;
		this.datePattern = Pattern.compile(datePatternStr);
	}
	
	
	/**
	 * 当前转换器支持的日期格式
	 * @return
	 */
	public String getDateFormat() {
		return dateFormat;
	}
	
	
	@Override
	public boolean isSupport(String source) {
		if ( source == null || source.isEmpty() )
			return false;
		// 判断 和当前 转换器支持的格式是否匹配
		return this.datePattern.matcher(source).matches();
	}

	@Override
	public Date convert(String source) {
		if ( !isSupport(source) ) // 避免直接调用 
			return null;
		
		try {
			return DateUtils.parseDate(source, new String[] {this.dateFormat});
		} catch (Exception e) {
			String msg = String.format("String[%s] convert to Date[%s] has exception !", source, this.dateFormat);
			LOG.error(msg, e);
			throw new RuntimeException(msg);
		}
	}

	/**
	 * 查看当前是否有支持的转换器
	 * @param source
	 * @return
	 */
	public static boolean isSupportDate(String source) {
		String2DateConverterComposite[] composite = String2DateConverterComposite.values();
		for ( String2DateConverterComposite convert : composite ) {
			if ( convert.isSupport(source) ) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 将输入的字符串 格式化为对应的日期
	 * @param source
	 * @return
	 */
	public static Date convert2Date(String source) {
		String2DateConverterComposite[] composite = String2DateConverterComposite.values();
		for ( String2DateConverterComposite convert : composite ) {
			if ( convert.isSupport(source) ) {
				return convert.convert(source);
			}
		}
		return null;
	}
	

}
