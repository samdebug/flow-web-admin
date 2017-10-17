package com.yzx.flow.modular.open.utils;

import java.io.Serializable;
import java.util.List;

public class  ResponseBean implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6203733346859560924L;

	private String resCode;
	
	private String resMsg;
	
	private String sign;
	
	private List<SystemVersionResponseBean> resData;

	public ResponseBean() {
	}
	
	public ResponseBean(String resCode, String resMsg, List<SystemVersionResponseBean> resData) {
		super();
		this.resCode = resCode;
		this.resMsg = resMsg;
		this.resData = resData;
	}

	public String getResCode() {
		return resCode;
	}

	public void setResCode(String resCode) {
		this.resCode = resCode;
	}

	public String getResMsg() {
		return resMsg;
	}

	public void setResMsg(String resMsg) {
		this.resMsg = resMsg;
	}

	public List<SystemVersionResponseBean> getResData() {
		return resData;
	}

	public void setResData(List<SystemVersionResponseBean> resData) {
		this.resData = resData;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	@Override
	public String toString() {
		return "ResponseBean [resCode=" + resCode + ", resMsg=" + resMsg + ", sign=" + sign + ", resData=" + resData
				+ "]";
	}

}
