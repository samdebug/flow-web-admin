package com.yzx.flow.common.inputSafe.web.context;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerMapping;

/**
 * 容器的HandlerMapping holder
 * @author Liulei
 *
 */
public class ContextHandlerMappingsHolder {
	
	
	private volatile static ContextHandlerMappingsHolder instance;
	
	/**
	 * 必须在容器初始化完成之后调用
	 * @param context
	 * @return
	 */
	public static ContextHandlerMappingsHolder getInstance(ApplicationContext context) {
		
		if ( ContextHandlerMappingsHolder.instance == null ) {
			synchronized (ContextHandlerMappingsHolder.class) {
				
				if (ContextHandlerMappingsHolder.instance != null)
					return ContextHandlerMappingsHolder.instance;
				
				ContextHandlerMappingsHolder.instance = new ContextHandlerMappingsHolder(context);
				ContextHandlerMappingsHolder.instance.init(context);
			}
		}
		return ContextHandlerMappingsHolder.instance;
	}
	
	
	public ContextHandlerMappingsHolder(ApplicationContext context) {
		init(context);
	}
	
	/**
	 * 获取容器中对应请求的HandlerExecutionChain
	 * @param request
	 * @return
	 */
	public HandlerExecutionChain getHandler(HttpServletRequest request) {
		try {
			for (HandlerMapping hm : this.handlerMappings) {
				HandlerExecutionChain handler = hm.getHandler(request);
				if (handler != null) {
					return handler;
				}
			}
		} catch (Exception e) {
			
		}
		return null;
	}
	
	/**
	 * 请求访问的目标方法是否是一个 输出到ResponseBody的方法
	 * @param request
	 * @param response
	 * @return
	 */
	public boolean isResponseBody(HttpServletRequest request, HttpServletResponse response) {

		HandlerExecutionChain chain = getHandler(request);
		if ( chain == null ) return false;
		
		Object handler = chain.getHandler();
		
		if ( handler != null && HandlerMethod.class.isInstance(handler) ) {
			Method method = ((HandlerMethod)handler).getMethod();
			ResponseBody anno = method.getAnnotation(ResponseBody.class);
			if ( anno != null ) return true;
		}
		return false;
	}
	
	private List<HandlerMapping> handlerMappings;
	
	/**
	 * 初始化
	 * @param context
	 */
	private void init (ApplicationContext context) {
		Map<String, HandlerMapping> matchingBeans =
				BeanFactoryUtils.beansOfTypeIncludingAncestors(context, HandlerMapping.class, true, false);
		if (!matchingBeans.isEmpty()) {
			this.handlerMappings = new ArrayList<HandlerMapping>(matchingBeans.values());
			// We keep HandlerMappings in sorted order.
			AnnotationAwareOrderComparator.sort(this.handlerMappings);
		}
	}
	

}
