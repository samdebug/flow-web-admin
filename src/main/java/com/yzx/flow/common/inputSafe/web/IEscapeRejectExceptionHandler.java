package com.yzx.flow.common.inputSafe.web;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.alibaba.fastjson.JSONObject;
import com.yzx.flow.common.constant.tips.ErrorTip;
import com.yzx.flow.common.exception.BizExceptionEnum;
import com.yzx.flow.common.inputSafe.strategy.EscapeRejectException;
import com.yzx.flow.common.inputSafe.strategy.EscapeRejectStrategy;
import com.yzx.flow.common.inputSafe.web.context.ContextHandlerMappingsHolder;

public interface IEscapeRejectExceptionHandler {
	
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @param ex
	 */
	void handler(HttpServletRequest request, HttpServletResponse response, EscapeRejectException ex);
	
	
	
	/**
	 * 若使用的是{@link EscapeRejectStrategy}拒绝策略，则对拒绝抛出的异常的处理机制
	 * @author Liulei
	 *
	 */
	public static class SimpleEscapeRejectExceptionHandler implements IEscapeRejectExceptionHandler {
		

		@Override
		public void handler(HttpServletRequest request, HttpServletResponse response, EscapeRejectException ex) {
			
			try {
				if (getHandlerMappingsHolder(request, response).isResponseBody(request, response)) {
					// ResponseBody
					response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
					String out = JSONObject.toJSONString(new ErrorTip(BizExceptionEnum.ESCAPE_REJECT.getCode(), ex.getMessage()), true);
					response.getWriter().write(out);
					response.getWriter().flush();
					return;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				response.setStatus(HttpStatus.BAD_REQUEST.value());// 400
				request.getRequestDispatcher("/global/400").forward(request, response);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		
		private ContextHandlerMappingsHolder getHandlerMappingsHolder(HttpServletRequest request, HttpServletResponse response) {
			
			return ContextHandlerMappingsHolder.getInstance(WebApplicationContextUtils.getWebApplicationContext(request.getServletContext()));
		}
		
	}
	

}
