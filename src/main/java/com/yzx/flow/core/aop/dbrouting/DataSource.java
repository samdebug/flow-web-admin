package com.yzx.flow.core.aop.dbrouting;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DataSource {
	/**
	 * 
	 * 方法用途: 只能是这两个值"write"和 "read"<br>
	 * 实现步骤: 已有静态值在{@link com.szwisdom.fmp.common.dbrouting.DataSourceType}<br>
	 * @return
	 */
    String value(); 
}
