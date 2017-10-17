package com.yzx.flow.config.converter;

import java.util.Date;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * 调用入口
 * <pre>
 * String convert to Date.
 * 避免前段字符串为空字符串，后端使用实中使用Date接收时 会解析异常的情况。
 * 有需要支持的时间格式请参考{@link String2DateConverterComposite}
 * </pre>
 * @author Liulei
 *
 */
@Component
public class String2DateConverterManager implements Converter<String, Date> {

	@Override
	public Date convert(String source) {
		
		if ( source == null || (source = source.trim()).isEmpty() )
			return null;
		
		// 是否支持
		if ( !String2DateConverterComposite.isSupportDate(source) ) {
			throw new UnsupportedOperationException(String.format("Can't convert str[%s] to Date, There is no corresponding format", source));
		} 
		// call convert
		return String2DateConverterComposite.convert2Date(source);
	}
	
	

}
