package com.yzx.flow.common.inputSafe.web;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import com.yzx.flow.common.inputSafe.EscapeManager;
import com.yzx.flow.common.inputSafe.strategy.EscapeRejectException;

/**
 * 
 * @author Liulei
 *
 */
public class XssFilter implements Filter {
	
	private static final Logger logger = LoggerFactory.getLogger(XssFilter.class);
	
	
	private EscapeManager escapeManager;// 暂时统一使用的检测方式 - 后期客针对 url/parameter/body使用不同的检测方式
	
	private IEscapeRejectExceptionHandler escapeRejectExceptionHandler;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		HttpServletRequest req = null;
		HttpServletResponse res = (HttpServletResponse)response;
		
		try {
			if ( request.getInputStream().available() > 0 && !isFileFormData(request) ) {
				req = new BodyReaderHttpServletRequestWrapper((HttpServletRequest)request);
			} else {
				req = (HttpServletRequest)request;
			}
			
			/**
			 * url
			 */
			checkRequestUrl(req);
			/**
			 * 对参数进行校验
			 */
			checkParameters(req);
			/**
			 * 对body进行校验
			 */
			checkRequestBody(req);
			
			chain.doFilter(req, response);
		} catch (EscapeRejectException e) {// 使用拒绝策略时会抛出此异常，需要在handler中进行处理
			if ( escapeRejectExceptionHandler != null )
				escapeRejectExceptionHandler.handler(req, res, e);
			else 
				throw e;
		} catch (IOException | ServletException e) {
			throw e;
		} catch (Exception e) {
			logger.error("", e);
			res.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value());
			res.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
	}
	
	
	@Override
	public void destroy() {
		try {
			escapeManager.destroy();			
		} catch(Exception e) {
			logger.error("Filter destroy has an exception", e);
		}
	}
	
	/**
	 * 检查url
	 * @param request
	 * @throws Exception 
	 */
	protected void checkRequestUrl(HttpServletRequest request) throws Exception {
//		String[] u = URLDecoder.decode(request.getRequestURI(), request.getCharacterEncoding()).split("/");
//		if ( u == null || u.length < 1 )
//			return;
//		
//		for (String path : u) {
//			if ( path.isEmpty() )
//				continue;
//			
//			escapeManager.checkAndEscape(path);
//		}
		escapeManager.checkAndEscape(URLDecoder.decode(request.getRequestURI(), request.getCharacterEncoding()));
	}
	
	
	/**
	 * 检查body - 暂未提供回写到requestBody功能
	 * @param request
	 */
	protected void checkRequestBody(HttpServletRequest request) throws Exception {
		
		if ( !BodyReaderHttpServletRequestWrapper.class.isInstance(request) ) 
			return;
		
		String bodyContent = ((BodyReaderHttpServletRequestWrapper)request).getBodyContentStr();
		if ( bodyContent == null || bodyContent.isEmpty() )
			return;
		
		escapeManager.checkAndEscape(bodyContent);
	}
	
	
	/**
	 * 校验参数 - 暂未提供回写parameters功能
	 * @param request
	 */
	protected void checkParameters(HttpServletRequest request) {
		
		Map<String, String[]> params = request.getParameterMap();
		if ( params.isEmpty() ) return;
		
		for ( String name : params.keySet() ) {
			
			String[] values = params.get(name);
			if ( values == null || values.length == 0 )
				continue;
			
			for ( String v : values ) {
				escapeManager.checkAndEscape(v);
			}
		}
	}
	
	/**
	 * 是否是 文件表单域提交
	 * @param request
	 * @return
	 */
	public boolean isFileFormData(ServletRequest request) {
		// multipart/form-data
		return request.getContentType().contains("multipart/form-data");
	}

	/**
	 * 设置 非法字符管理器
	 * @param escapeManager
	 */
	public void setEscapeManager(EscapeManager escapeManager) {
		this.escapeManager = escapeManager;
	}
	
	
	/**
	 * 
	 * @param escapeRejectExceptionHandler
	 */
	public void setEscapeRejectExceptionHandler(IEscapeRejectExceptionHandler escapeRejectExceptionHandler) {
		this.escapeRejectExceptionHandler = escapeRejectExceptionHandler;
	}
	

}
