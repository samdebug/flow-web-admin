package com.yzx.flow.core.portal.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * 自动检测参数列表中是否需要增加（或覆盖）客户ID
 * <br/>
 * 若当前用户为 portal端客户登陆，是否需要自动将客户id增加到参数列表中 
 * @author Liulei
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AutoSetPortalCustomer {

	
	PortalParamMeta[] value();
}
