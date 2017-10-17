package com.yzx.flow.modular.customer.transformer;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.stereotype.Component;

import com.yzx.flow.common.persistence.model.CustomerInfo;
import com.yzx.flow.common.transformers.ITransformer;
import com.yzx.flow.modular.customer.vo.CustomerVo;

/**
 * CustomerVo变形器
 * @author Administrator
 *
 */
@Component("ToCustomerVo_Transformer")
public class CustomerVoTransformer implements ITransformer<CustomerVo, CustomerInfo> {
	
	
	public static final String TRANSFORMER_NAME = "ToCustomerVo";
	
	
	/* (non-Javadoc)
	 * @see com.yzx.flow.common.transformers.ITransformer#transform(java.lang.Object, java.lang.Object[])
	 */
	@Override
	public CustomerVo transform(CustomerInfo p, Object... exts) {
		CustomerVo vo = new CustomerVo();
		
		try {
			BeanUtils.copyProperties(vo, p);
			
			if ( p.getPartnerInfo() != null ) 
				vo.setPartnerName(p.getPartnerInfo().getPartnerName());
			
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return vo;
	}
	

}
