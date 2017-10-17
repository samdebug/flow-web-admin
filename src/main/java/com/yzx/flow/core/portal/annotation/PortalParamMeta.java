package com.yzx.flow.core.portal.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.yzx.flow.core.portal.PortalParamSetter;

/**
 * 一些元数据配置 - 通过设置入参的索引增加当前登录对象的客户ID
 * @author Liulei
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PortalParamMeta {
	
	/**
	 * 需要设置的参数索引
	 * @return
	 */
	int paramIndex() default 0;
	
	String portalParamName() default "customerId";
	
	PortalParamSetter setter();

}
