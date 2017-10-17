package com.yzx.flow.common.transformers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.yzx.flow.core.util.SpringContextHolder;

public class TransformerManager {
	
	
	/**
	 * 变形器名称后缀
	 */
	public static final String TRANSFORMER_NAME_SUFFIX = "_Transformer";
	
	
	/**
	 * 
	 * @param transformerName
	 * @param p
	 * @param exts
	 * @return
	 */
	public static <R, P> R transform(String transformerName, P p, Object...exts) {
		
		if (p == null) 
			return null;
		
		ITransformer<R, P> tf = getTransformer(transformerName);
		return tf.transform(p, exts);
	}
	
	/**
	 * 
	 * @param transformerName
	 * @param pList
	 * @param exts
	 * @return
	 */
	public static <R, P> List<R> transforms(String transformerName, List<P> pList, Object...exts) {
		
		if ( pList == null || pList.isEmpty() )
			return Collections.emptyList();
		
		ITransformer<R, P> tf = getTransformer(transformerName);
		
		ArrayList<R> res = new ArrayList<>(pList.size());
		for(P p : pList) {
			res.add(tf.transform(p, exts));
		}
		return res;
	}
	
	
	/**
	 * 获取指定名称的变形器
	 * @param transformerName
	 * @return
	 */
	public static <R, P> ITransformer<R, P> getTransformer(String transformerName) {
		if ( transformerName == null || transformerName.isEmpty() )
			throw new IllegalArgumentException("transformName must not null");
	
		ITransformer<R, P> tf = SpringContextHolder.getBean(String.format("%s%s", transformerName, TransformerManager.TRANSFORMER_NAME_SUFFIX));
		if (tf == null)
			throw new RuntimeException(String.format("not found：%s%s", transformerName, TransformerManager.TRANSFORMER_NAME_SUFFIX));
		
		return tf;
		
	}
	
	
	

}
