package com.yzx.flow.common.inputSafe.strategy;

import com.yzx.flow.common.inputSafe.IEscapeStrategy;

/**
 * 拒绝通过
 * @author Liulei
 *
 */
public final class EscapeRejectStrategy implements IEscapeStrategy {

	
	@Override
	public String escape(String value, String key) {
		throw new EscapeRejectException(key);
	}
	

}
