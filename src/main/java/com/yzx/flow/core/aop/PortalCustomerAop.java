package com.yzx.flow.core.aop;

import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yzx.flow.common.annotion.PortalCustomer;
import com.yzx.flow.common.constant.tips.ErrorTip;
import com.yzx.flow.common.exception.BizExceptionEnum;
import com.yzx.flow.common.exception.BussinessException;
import com.yzx.flow.core.shiro.ShiroKit;

/**
 * AOP 客户类型管理员检测
 * 
 * @author Administrator
 */
@Aspect
@Component
public class PortalCustomerAop {
	
	@Pointcut(value = "@annotation(com.yzx.flow.common.annotion.PortalCustomer)")
    private void cutPortalCustomer() {
    }
	
	
	@Around("cutPortalCustomer()")
	public Object validePortalCustomer(ProceedingJoinPoint point) throws Throwable {
		
		MethodSignature ms = (MethodSignature) point.getSignature();
		PortalCustomer portalAnno =  ms.getMethod().getAnnotation(PortalCustomer.class);
	    
		if ( ShiroKit.isCustomerTypeOfUser() ) {
			// 判断是否需要将当前登陆客户的ID放入到方法参数的Model中
			if ( !portalAnno.autoSetId2Model() || StringUtils.isBlank(portalAnno.attributeName()) ) {
				return point.proceed();
			}
			
			Object[] params = point.getArgs();
			for ( Object param : params ) {
				if ( Model.class.isInstance(param) ) {
					((Model)param).addAttribute(portalAnno.attributeName(), ShiroKit.getUser().getTargetId());
					break;
				}
			}
			return point.proceed(params);
		}
		
	    ResponseBody anno = ms.getMethod().getAnnotation(ResponseBody.class);
	    if ( anno != null ) {
	    	return ErrorTip.buildErrorTip(BizExceptionEnum.OPERATION_ILLEGALE);
	    } else if ( ms.getReturnType().equals(String.class) ) {
	    	return "/404.html";
	    } 
	    throw new BussinessException(BizExceptionEnum.OPERATION_ILLEGALE);
	}

}
