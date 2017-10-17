package com.yzx.flow.common.annotion;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.ui.Model;

/**
 * 限制 只允许 客户类型的 管理员访问
 * @author Administrator
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PortalCustomer {

	/**
	 * 是否自动为{@link Model}类型的参数自动添加当前客户id
	 * @return
	 */
	boolean autoSetId2Model() default true;
	
	/**
	 * 默认的属性名称 - customerId
	 * @return
	 */
	String attributeName() default "customerId";
	
}
