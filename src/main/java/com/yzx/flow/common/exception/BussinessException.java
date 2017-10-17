package com.yzx.flow.common.exception;

/**
 * @Description 业务异常的封装
 * @author liuyufeng
 * @date 2016年11月12日 下午5:05:10
 */
@SuppressWarnings("serial")
public class BussinessException extends RuntimeException{

	//友好提示的code码
	private int friendlyCode;
	
	//友好提示
	private String friendlyMsg;
	
	//业务异常跳转的页面
	private String urlPath;
	
	
	public BussinessException(BizExceptionEnum bizExceptionEnum){
		this.friendlyCode = bizExceptionEnum.getCode();
		this.friendlyMsg = bizExceptionEnum.getMessage();
		this.urlPath = bizExceptionEnum.getUrlPath();
	}
	
	
	/**
	 * 自定义格式化的异常
	 * @param bizExceptionEnum message信息必须是可格式化的字符串（String.format(String, ...)）
	 * @param params
	 */
	public BussinessException(BizExceptionEnum bizExceptionEnum, Object...params) {
		this.friendlyCode = bizExceptionEnum.getCode();
		this.urlPath = bizExceptionEnum.getUrlPath();
		this.friendlyMsg = String.format(bizExceptionEnum.getMessage(), params);
	}
	
	
	public BussinessException(IError error){
		this.friendlyCode = error.getCode();
		this.friendlyMsg = error.getMessage();
	}
	
	
	/**
	 * 
	 * @param friendlyCode 友好提示的code码
	 * @param friendlyMsg 友好提示
	 * @param urlPath 业务异常跳转的页面
	 */
	public BussinessException(int friendlyCode, String friendlyMsg, String urlPath) {
		super();
		this.friendlyCode = friendlyCode;
		this.friendlyMsg = friendlyMsg;
		this.urlPath = urlPath;
	}



	public int getCode() {
		return friendlyCode;
	}

	public void setCode(int code) {
		this.friendlyCode = code;
	}

	public String getMessage() {
		return friendlyMsg;
	}

	public void setMessage(String message) {
		this.friendlyMsg = message;
	}

	public String getUrlPath() {
		return urlPath;
	}

	public void setUrlPath(String urlPath) {
		this.urlPath = urlPath;
	}
	
}
