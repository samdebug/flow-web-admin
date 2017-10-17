package com.yzx.flow.modular.customer.vo;

public class PartnerNameValuePair extends NameValuePair {
	
	private Integer partnerType;
	
	

	public PartnerNameValuePair() {
	}

	public PartnerNameValuePair(String displayName, String value, Integer partnerType) {
		super(displayName, value);
		this.partnerType = partnerType;
	}

	/**
	 * @return the partnerType
	 */
	public Integer getPartnerType() {
		return partnerType;
	}

	/**
	 * @param partnerType the partnerType to set
	 */
	public void setPartnerType(Integer partnerType) {
		this.partnerType = partnerType;
	}
	
	

}
