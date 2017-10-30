package com.yzx.flow.modular.customer.vo;

import java.math.BigDecimal;
import java.util.Date;

public class CustomerVo {
	
	
	private long customerId;
	
	private String shorterName;
	
	private String customerName;
	
	private String adviserTypeDesc;
	
	private Integer customerLevel;
	
	private Integer status;
	
	private BigDecimal rechargeAmount;
	
	private BigDecimal balance;
	
	private BigDecimal currentAmount;
	
	private BigDecimal creditAmount;
	
	private BigDecimal availableCredit;
	
	private BigDecimal orderRiskSetting;
	
	private String partnerName = "";
	
	private String partnerType = "";
	
	private String linkmanEmail = "";// 联系人邮箱
	
	private String linkmanMobile = "";// 联系人电话
	
	private String account;
	private Date createTime;

	/**
	 * @return the customerId
	 */
	public long getCustomerId() {
		return customerId;
	}

	/**
	 * @param customerId the customerId to set
	 */
	public void setCustomerId(long customerId) {
		this.customerId = customerId;
	}

	/**
	 * @return the shorterName
	 */
	public String getShorterName() {
		return shorterName;
	}

	/**
	 * @param shorterName the shorterName to set
	 */
	public void setShorterName(String shorterName) {
		this.shorterName = shorterName;
	}

	/**
	 * @return the customerName
	 */
	public String getCustomerName() {
		return customerName;
	}

	/**
	 * @param customerName the customerName to set
	 */
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	/**
	 * @return the adviserTypeDesc
	 */
	public String getAdviserTypeDesc() {
		return adviserTypeDesc;
	}

	/**
	 * @param adviserTypeDesc the adviserTypeDesc to set
	 */
	public void setAdviserTypeDesc(String adviserTypeDesc) {
		this.adviserTypeDesc = adviserTypeDesc;
	}

	/**
	 * @return the customerLevel
	 */
	public Integer getCustomerLevel() {
		return customerLevel;
	}

	/**
	 * @param customerLevel the customerLevel to set
	 */
	public void setCustomerLevel(Integer customerLevel) {
		this.customerLevel = customerLevel;
	}

	/**
	 * @return the status
	 */
	public Integer getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(Integer status) {
		this.status = status;
	}

	/**
	 * @return the rechargeAmount
	 */
	public BigDecimal getRechargeAmount() {
		return rechargeAmount;
	}

	/**
	 * @param rechargeAmount the rechargeAmount to set
	 */
	public void setRechargeAmount(BigDecimal rechargeAmount) {
		this.rechargeAmount = rechargeAmount;
	}

	/**
	 * @return the balance
	 */
	public BigDecimal getBalance() {
		return balance;
	}

	/**
	 * @param balance the balance to set
	 */
	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	/**
	 * @return the currentAmount
	 */
	public BigDecimal getCurrentAmount() {
		return currentAmount;
	}

	/**
	 * @param currentAmount the currentAmount to set
	 */
	public void setCurrentAmount(BigDecimal currentAmount) {
		this.currentAmount = currentAmount;
	}

	/**
	 * @return the creditAmount
	 */
	public BigDecimal getCreditAmount() {
		return creditAmount;
	}

	/**
	 * @param creditAmount the creditAmount to set
	 */
	public void setCreditAmount(BigDecimal creditAmount) {
		this.creditAmount = creditAmount;
	}

	/**
	 * @return the availableCredit
	 */
	public BigDecimal getAvailableCredit() {
		return availableCredit;
	}

	/**
	 * @param availableCredit the availableCredit to set
	 */
	public void setAvailableCredit(BigDecimal availableCredit) {
		this.availableCredit = availableCredit;
	}

	/**
	 * @return the orderRiskSetting
	 */
	public BigDecimal getOrderRiskSetting() {
		return orderRiskSetting;
	}

	/**
	 * @param orderRiskSetting the orderRiskSetting to set
	 */
	public void setOrderRiskSetting(BigDecimal orderRiskSetting) {
		this.orderRiskSetting = orderRiskSetting;
	}

	/**
	 * @return the partnerName
	 */
	public String getPartnerName() {
		return partnerName == null ? "" : partnerName;
	}

	/**
	 * @param partnerName the partnerName to set
	 */
	public void setPartnerName(String partnerName) {
		this.partnerName = partnerName;
	}

	public String getPartnerType() {
		return partnerType;
	}

	public void setPartnerType(String partnerType) {
		this.partnerType = partnerType;
	}

	public String getLinkmanEmail() {
		return linkmanEmail;
	}

	public void setLinkmanEmail(String linkmanEmail) {
		this.linkmanEmail = linkmanEmail;
	}

	public String getLinkmanMobile() {
		return linkmanMobile;
	}

	public void setLinkmanMobile(String linkmanMobile) {
		this.linkmanMobile = linkmanMobile;
	}

	public String getAccount() {
		return account == null ? "" : account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

}
