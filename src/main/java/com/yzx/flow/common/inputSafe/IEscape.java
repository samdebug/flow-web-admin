package com.yzx.flow.common.inputSafe;

/**
 * 对输入的值到检测和处理
 * @author Liulei
 *
 */
public interface IEscape {
	
	
//	/**
//	 * 允许通过的 正则列表 - 满足其一
//	 * @return
//	 */
//	Pattern[] getAllowPattern();
	
	
	/**
	 * value是否允许
	 * @param value
	 * @return - 返回处理后的值
	 */
	String checkAndEscape(String value);
	
	
	/**
	 * 获取处理策略
	 * @return
	 */
	IEscapeStrategy getEscapeStrategy();
	

}
