package com.yzx.flow.modular.open.utils;

import java.util.List;

import com.yzx.flow.common.persistence.model.CompInfo;
import com.yzx.flow.common.persistence.model.CustInfo;

public class ResultData {
	//
	private String userName;
	//
	private String version;
	//
	private String start;
	//
	private String end;
	//
	private List<CompInfo> compInfos;
	//
	private List<CustInfo> custInfos;
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getStart() {
		return start;
	}
	public void setStart(String start) {
		this.start = start;
	}
	public String getEnd() {
		return end;
	}
	public void setEnd(String end) {
		this.end = end;
	}
	public List<CompInfo> getCompInfos() {
		return compInfos;
	}
	public void setCompInfos(List<CompInfo> compInfos) {
		this.compInfos = compInfos;
	}
	public List<CustInfo> getCustInfos() {
		return custInfos;
	}
	public void setCustInfos(List<CustInfo> custInfos) {
		this.custInfos = custInfos;
	}
	
	
}
