package com.yzx.flow.common.inputSafe.escape;

import com.yzx.flow.common.inputSafe.IEscapeStrategy;

public class RestrictEscape extends AbstractEscape {
	
	
	/**
	 * 关键词列表
	 * @param sqlKeys
	 * @param strategy
	 */
	public RestrictEscape(String restrictKeys, IEscapeStrategy strategy) {
		if ( restrictKeys != null && !(restrictKeys = restrictKeys.trim()).isEmpty() ) {
			this.keys = restrictKeys.split(" ");
		}
		this.strategy = strategy;
	}

}
