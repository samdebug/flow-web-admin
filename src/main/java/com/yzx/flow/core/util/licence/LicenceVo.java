package com.yzx.flow.core.util.licence;

public class LicenceVo {
	//mac地址
	private String mac;
	
	//客户名称
	private String customerName;
	
	//
	private String password;
	
	
	//购买时间YYYY-MM-DD HH-mm-ss
	private String startTime;
	
	//到期时间YYYY-MM-DD HH-mm-ss
	private String endTime;
	
	//文件有效期
	private String validity;
	
	//版本
	private String version = "标准版";

	
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getValidity() {
		return validity;
	}

	public void setValidity(String validity) {
		this.validity = validity;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	@Override
	public String toString() {
		return "Licence [mac地址=" + mac + ", 用户名称=" + customerName
				+ ", 开始时间=" + startTime + ", 结束时间=" + endTime
				+ ", 有效期=" + validity + ", 版本号=" + version + "]";
	}
	
	
}
