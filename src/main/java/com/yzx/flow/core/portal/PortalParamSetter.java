package com.yzx.flow.core.portal;

import com.yzx.flow.common.page.Page;
import com.yzx.flow.core.shiro.ShiroKit;

public enum PortalParamSetter {
	
	/**
	 * Page类型参数
	 */
	PAGE(new IPortalParamSetter() {
		@Override
		public Object setPortalCustomerId(Object paramValue, String attributeName) {
			if ( paramValue != null ) {
				((Page<?>)paramValue).addParam(attributeName, ShiroKit.getUser().getTargetId());
			}
			return paramValue;
		}}),
	
	/**
	 * string
	 */
	CUSTOMER_ID_STRING(new IPortalParamSetter() {
		@Override
		public Object setPortalCustomerId(Object paramValue, String attributeName) {
			return String.valueOf(ShiroKit.getUser().getTargetId());
		}}),
	
	/**
	 * long
	 */
	CUSTOMER_ID_LONG(new IPortalParamSetter() {
		@Override
		public Object setPortalCustomerId(Object paramValue, String attributeName) {
			return ShiroKit.getUser().getTargetId();
		}}),
	
	;
	
	private IPortalParamSetter setter;
	
	PortalParamSetter(IPortalParamSetter setter) {
		this.setter = setter;
	}

	public IPortalParamSetter getSetter() {
		return setter;
	}
	
	
	public  Object set(Object paramValue, String attributeName) {
		return this.setter.setPortalCustomerId(paramValue, attributeName);
	}

}
