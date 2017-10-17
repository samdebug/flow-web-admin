package com.yzx.flow.common.exception;

/**
 * @Description 所有业务异常的枚举
 * @author liuyufeng
 * @date 2016年11月12日 下午5:04:51
 */
public enum BizExceptionEnum implements IError {
	
	
	OPERATION_ILLEGALE(400, "操作不合法"), 

	/**
	 * 字典
	 */
	DICT_EXISTED(400,"字典已经存在"),
	ERROR_CREATE_DICT(500,"创建字典失败"),
	ERROR_WRAPPER_FIELD(500,"包装字典属性失败"),


	/**
	 * 文件上传
	 */
	FILE_READING_ERROR(400,"FILE_READING_ERROR!"),
	FILE_NOT_FOUND(400,"FILE_NOT_FOUND!"),
	UPLOAD_ERROR(500,"上传图片出错"),

	/**
	 * 权限和数据问题
	 */
	DB_RESOURCE_NULL(400,"数据库中没有该资源"),
	NO_PERMITION(405, "权限异常"),
	REQUEST_INVALIDATE(400,"请求数据格式不正确"),
	INVALID_KAPTCHA(400,"验证码不正确"),

	/**
	 * 账户问题
	 */
	USER_ALREADY_REG(401,"该用户已经注册"),
	NO_THIS_USER(400,"没有此用户"),
	USER_NOT_EXISTED(400, "没有此用户"),
	ACCOUNT_FREEZED(401, "账号被冻结"),
	OLD_PWD_NOT_RIGHT(402, "原密码不正确"),
	TWO_PWD_NOT_MATCH(405, "两次输入密码不一致"),
	ACCOUNT_EXIST(406, "账户已经被使用"),
	
	

	/**
	 * 错误的请求
	 */
	REQUEST_NULL(400, "请求有错误"),
	SERVER_ERROR(500, "server error"),

	/**
	 * 通道
	 */
	CHANNEL_COMPANY_BUILD(600,"上游通道绑定了通道供应商"),
	
	
	
	/**
	 * 合作伙伴
	 */
	PARTNER_NOT_EXIST(700, "合作伙伴信息不存在"),
	PARTNER_NOT_USE(701, "合作伙伴暂未启用"),
	
	
	
	/**
	 * 客户
	 */
	CUSTOMER_NOT_EXIST(801, "客户信息不存在"),
	
	
	/**
	 * 自定义格式化异常
	 */
	CUSTOMER_FORMAT_ERROR(901, "%s"),
	
	
	
	ESCAPE_REJECT(401, "提交内容包含非法字符[%s]"),
	ESCAPE_REJECT_2(402, "提交内容包含非法字符"),
	;
	
	
	BizExceptionEnum(int code, String message) {
		this.friendlyCode = code;
		this.friendlyMsg = message;
	}
	
	BizExceptionEnum(int code, String message,String urlPath) {
		this.friendlyCode = code;
		this.friendlyMsg = message;
		this.urlPath = urlPath;
	}

	private int friendlyCode;

	private String friendlyMsg;
	
	private String urlPath;
	
	private static String NS = "";

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

	@Override
	public String getNamespace() {
		return NS;
	}

}
