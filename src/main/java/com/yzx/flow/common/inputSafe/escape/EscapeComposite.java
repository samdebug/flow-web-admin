package com.yzx.flow.common.inputSafe.escape;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.DisposableBean;

import com.yzx.flow.common.inputSafe.IEscape;
import com.yzx.flow.common.inputSafe.IEscapeStrategy;

/**
 * EscapeComposite
 * @author Liulei
 *
 */
public class EscapeComposite implements IEscape, DisposableBean  {

	
	private List<IEscape> escapeComposite = null;
	
	
	public EscapeComposite() {
		this.escapeComposite = new ArrayList<IEscape>();
	}
	
	
	/**
	 * add to Composite
	 * @param escape
	 */
	public void addEscape(IEscape escape) {
		if ( escape != null && !this.escapeComposite.contains(escape) )
			this.escapeComposite.add(escape);
	}
	
	/**
	 * remove
	 * @param escape
	 * @return
	 */
	public boolean removeEscape(IEscape escape) {
		if ( escape == null )
			return false;
		
		return this.escapeComposite.remove(escape);
	}
	

	@Override
	public String checkAndEscape(String value) {
		
		if ( this.escapeComposite == null || this.escapeComposite.isEmpty() )
			return value;
		
		for( IEscape escape : escapeComposite ) {
			value = escape.checkAndEscape(value);
		}
		
		return value;
	}

	/**
	 * unsupported
	 */
	@Override
	public IEscapeStrategy getEscapeStrategy() {
		throw new UnsupportedOperationException();// 
	}
	
	
	/**
	 * unmodifiableList
	 * @return
	 */
	public List<IEscape> getEscapeComposite() {
		return Collections.unmodifiableList(escapeComposite);
	}


	@Override
	public void destroy() throws Exception {
		if ( this.escapeComposite != null ) {
			this.escapeComposite.clear();
			this.escapeComposite = null;
		}
	}
	
}
