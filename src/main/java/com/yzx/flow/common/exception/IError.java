package com.yzx.flow.common.exception;

/**
 * 
 * @author Administrator
 *
 */
public interface IError {
	
	/**
	 * 友好的code
	 * @return
	 */
	int getCode();
	
	/**
	 * 信息
	 * @return
	 */
	String getMessage();
	
	
	/**
	 * 错误码命名控件
	 * @return
	 */
	String getNamespace();

}
