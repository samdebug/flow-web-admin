package com.yzx.flow.modular.customer.vo;

import com.yzx.flow.common.box.IDisplayNameValue;

/**
 * 名-值 对
 * @author Administrator
 *
 */
public class NameValuePair implements IDisplayNameValue<String, String> {
	
	private String displayName;
	
	private String value;
	
	public NameValuePair() {
	}

	public NameValuePair(String displayName, String value) {
		this();
		this.displayName = displayName;
		this.value = value;
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.common.box.IDisplayNameValue#getDisplayName()
	 */
	@Override
	public String getDisplayName() {
		return displayName;
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.common.box.IDisplayNameValue#getValue()
	 */
	@Override
	public String getValue() {
		return value;
	}

	/**
	 * @param displayName the displayName to set
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
	
	
	

}
