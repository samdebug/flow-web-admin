package com.yzx.flow.common.inputSafe.strategy;

import com.yzx.flow.common.exception.BizExceptionEnum;

/**
 * 非法字符异常类
 * @author Liulei
 *
 */
public class EscapeRejectException extends RuntimeException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8093518965170508340L;

	/**
	 * 
	 * @param escapeKey 关键字符
	 */
	public EscapeRejectException(String escapeKey) {
		super(String.format(BizExceptionEnum.ESCAPE_REJECT.getMessage(), escapeKey));
	}
	

}
