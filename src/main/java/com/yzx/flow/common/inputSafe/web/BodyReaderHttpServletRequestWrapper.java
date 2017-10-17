package com.yzx.flow.common.inputSafe.web;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StreamUtils;

/**
 * 
 * @author Liulei
 *
 */
public class BodyReaderHttpServletRequestWrapper extends HttpServletRequestWrapper {
	
	private static final Logger logger = LoggerFactory.getLogger(XssFilter.class);

	private byte[] body;
	private String bodyStr;

	public BodyReaderHttpServletRequestWrapper(final HttpServletRequest request) throws Exception {
		super(request);
		// 判断有点不严谨，但本系统可以使用 - 还应该对有表单文件提交的方式处理
		if ( getRequest().getInputStream().available() > 0 && !isFileFormData() ) {
			body = StreamUtils.copyToByteArray(getRequest().getInputStream());
		} else {
			body = new byte[0];
		}
	}
	
	
	/**
	 * 是否是form文件提交方式
	 * @return
	 */
	public boolean isFileFormData() {
		// multipart/form-data
		return getRequest().getContentType().contains("multipart/form-data");
	}
	
	
	
	/**
	 * 获取 body中的字符内容
	 * @return
	 * @throws Exception
	 */
	public String getBodyContentStr() throws Exception {
		
		if ( bodyStr != null ) return bodyStr;
		
		// body中有数据，且不是以multipart/form-data形式提交数据的（二进制数据传输方式暂时忽略）
		if ( body != null && body.length > 0 ) {
			bodyStr = new String(body, getCharacterEncoding());	
		} else {
			bodyStr = "";
		}
		return bodyStr;
	}

	
	@Override
	public BufferedReader getReader() throws IOException {
		return new BufferedReader(new InputStreamReader(getInputStream(), getCharacterEncoding()));
	}
	
	/**
	 * 清理数据
	 */
	public void clear() {
		this.body = null;
		this.bodyStr = null;
	}

	
	@Override
	public ServletInputStream getInputStream() throws IOException {
		final ByteArrayInputStream bais = new ByteArrayInputStream(body);
		return new ServletInputStream() {

			boolean finished = false;
			@Override
			public boolean isFinished() {
				return finished;
			}

			@Override
			public boolean isReady() {
				return !finished;
			}

			@Override
			public void setReadListener(ReadListener listener) {
				logger.debug("Call ServletInputStream#setReadListener(ReadListener listener)");
			}

			@Override
			public int read() throws IOException {
				int read = bais.read();
				if ( read == -1 )
					finished = true;
				return read;
			}
			
			@Override
			public void close() throws IOException {
				super.close();
				bais.close();
				clear();
			}
		};
	}
	
	
	
   
}
