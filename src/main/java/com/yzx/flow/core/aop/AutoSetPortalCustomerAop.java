package com.yzx.flow.core.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yzx.flow.common.constant.state.UserType;
import com.yzx.flow.common.constant.tips.ErrorTip;
import com.yzx.flow.common.exception.BizExceptionEnum;
import com.yzx.flow.common.exception.BussinessException;
import com.yzx.flow.core.portal.annotation.AutoSetPortalCustomer;
import com.yzx.flow.core.portal.annotation.PortalParamMeta;
import com.yzx.flow.core.shiro.ShiroKit;

/**
 * AOP - portal客户登陆。 针对一些查询访问自动设置客户id打到参数中
 * 
 * @author Administrator
 */
@Aspect
@Component
public class AutoSetPortalCustomerAop {
	
	@Pointcut(value = "@annotation(com.yzx.flow.core.portal.annotation.AutoSetPortalCustomer)")
    private void setPortalCustomer() {
    }
	
	
	@Around("setPortalCustomer()")
	public Object setPortalCustomerAttribute(ProceedingJoinPoint point) throws Throwable {
		
		MethodSignature ms = (MethodSignature) point.getSignature();
		// 当前登陆用户为客户类型 但是 targetId为null或小于0  则视为不合法访问
		if ( UserType.CUSTOMER.getCode().equals(ShiroKit.getUser().getType()) 
				&& ( ShiroKit.getUser().getTargetId() == null || ShiroKit.getUser().getTargetId().compareTo(0L) <=0 )) {
			
			ResponseBody anno = ms.getMethod().getAnnotation(ResponseBody.class);
		    if ( anno != null ) {
		    	return ErrorTip.buildErrorTip(BizExceptionEnum.OPERATION_ILLEGALE);
		    
		    } else if ( ms.getReturnType().equals(String.class) ) {
		    	
		    	return "/404.html";
		    } 
		    throw new BussinessException(BizExceptionEnum.OPERATION_ILLEGALE);
		}
		
		if ( !ShiroKit.isCustomerTypeOfUser() )// 不是portal客户  则忽略
			return point.proceed();
		
		Object[] params = point.getArgs();
		if ( params == null || params.length <= 0 )  // 没有参数 则忽略
			return point.proceed();
		
		PortalParamMeta[] annos =  ms.getMethod().getAnnotation(AutoSetPortalCustomer.class).value();
	    
		if ( annos == null || annos.length == 0 || annos[0] == null )
			return point.proceed();
		
		for ( PortalParamMeta meta : annos ) {
			if ( meta.setter() == null )
				continue;
			
			int index = meta.paramIndex();
			if ( index >= params.length )// ignore
				throw new BussinessException(BizExceptionEnum.CUSTOMER_FORMAT_ERROR, "Invalid PortalParamMeta.paramIndex()");
			
			// 添加或覆盖参数中的 值 - 设置为当前登陆客户的ID
			params[index] = meta.setter().set(params[index], meta.portalParamName());
		}
		
		return point.proceed(params);
	}

}
