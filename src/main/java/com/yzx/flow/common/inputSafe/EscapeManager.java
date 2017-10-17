package com.yzx.flow.common.inputSafe;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.yzx.flow.common.inputSafe.escape.EscapeComposite;
import com.yzx.flow.common.inputSafe.escape.RestrictEscape;
import com.yzx.flow.common.inputSafe.escape.SqlEscape;
import com.yzx.flow.common.inputSafe.strategy.EscapeRejectStrategy;
import com.yzx.flow.config.properties.EscapeProperties;

/**
 * 
 * @author Liulei
 *
 */
@Component(EscapeManager.BEAN_NAME)
@DependsOn("springContextHolder")
public class EscapeManager implements InitializingBean, DisposableBean  {
	
	public static final String BEAN_NAME= "Escape-Manager";
	
	private EscapeComposite escapeComposit = new EscapeComposite();
	
	@Autowired
	private EscapeProperties escapeProperties;
	
	
	@Override
	public void afterPropertiesSet() throws Exception {
		// init  默认配置，可以考虑将这部分代码使用配置的方式填充escapeComposit
		if ( escapeProperties.getSqlKey() != null )
			addEscape(new SqlEscape(escapeProperties.getSqlKey(), new EscapeRejectStrategy()));
		
		if ( escapeProperties.getRestrictKey() != null )
			addEscape(new RestrictEscape(escapeProperties.getRestrictKey(), new EscapeRejectStrategy()));
		
		addEscape(new SqlEscape(escapeProperties.getSqlKey(), new EscapeRejectStrategy()));
	}


	/**
	 * chacke
	 * @param value
	 * @return
	 */
	public String checkAndEscape(String value) {
		return escapeComposit.checkAndEscape(value);
	}
	
	
	/**
	 * 
	 * @param escape
	 */
	public void addEscape(IEscape escape) {
		this.escapeComposit.addEscape(escape);
	}


	@Override
	public void destroy() throws Exception {
		escapeComposit.destroy();
	}

}
