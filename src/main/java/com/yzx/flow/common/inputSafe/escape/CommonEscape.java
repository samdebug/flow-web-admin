package com.yzx.flow.common.inputSafe.escape;

import org.apache.commons.lang.StringEscapeUtils;

import com.yzx.flow.common.inputSafe.IEscape;
import com.yzx.flow.common.inputSafe.IEscapeStrategy;

/**
 * 统一处理所有的关键字符  - 转义
 * <pre>
 * 将忽略配置的关键词列表。
 * 
 * 建议 单独使用 或者 放在 {@link EscapeComposite}的最后；
 * 
 * 若 {@link EscapeComposite}中的检测已经够强可以忽略此校验）
 * </pre>
 * @author Liulei
 *
 */
public class CommonEscape implements IEscape, IEscapeStrategy {

	
	@Override
	public String escape(String value, String key) {
		return StringEscapeUtils.escapeSql(
				StringEscapeUtils.escapeJavaScript(
						StringEscapeUtils.escapeHtml(value)
					)
			);
	}
	
	@Override
	public String checkAndEscape(String value) {
		return this.escape(value, null);
	}
	

	@Override
	public IEscapeStrategy getEscapeStrategy() {
		return this;
	}

	
	
}
