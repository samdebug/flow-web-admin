package com.yzx.flow.common.transformers;


/**
 * 数据变形器
 * @author Administrator
 *
 * @param <R> 结果
 * @param <P> 
 */
public interface ITransformer<R, P> {
	
	
	/**
	 * 将数据p变形为R类型
	 * @param p
	 * @param exts
	 * @return
	 */
	R transform(P p, Object...exts);
	

}
