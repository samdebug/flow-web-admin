package com.yzx.flow.core.portal;

public interface IPortalParamSetter {
	
	/**
	 * 对入参设置当前登陆的客户ID
	 * @param paramValue  原入参
	 * @param attributeName 若需要设置则设置的属性名称 - 基本类型将忽略
	 * @return 
	 */
	Object setPortalCustomerId(Object paramValue, String attributeName);

}
