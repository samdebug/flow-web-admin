package com.yzx.flow.modular.customer.transformer;

import org.springframework.stereotype.Component;

import com.yzx.flow.common.persistence.model.PartnerInfo;
import com.yzx.flow.common.transformers.ITransformer;
import com.yzx.flow.modular.customer.vo.PartnerNameValuePair;

@Component("ToPartnerNameValuePair_Transformer")
public class PartnerNameValuePairTransformer implements ITransformer<PartnerNameValuePair, PartnerInfo> {
	
	public static final String TRANSFORMER_NAME = "ToPartnerNameValuePair";

	/* (non-Javadoc)
	 * @see com.yzx.flow.common.transformers.ITransformer#transform(java.lang.Object, java.lang.Object[])
	 */
	@Override
	public PartnerNameValuePair transform(PartnerInfo p, Object... exts) {
		return new PartnerNameValuePair(p.getPartnerName(), String.valueOf(p.getPartnerId()), p.getPartnerType());
	}
	
	
	

}
