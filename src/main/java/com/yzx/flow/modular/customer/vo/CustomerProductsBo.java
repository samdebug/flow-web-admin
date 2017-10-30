package com.yzx.flow.modular.customer.vo;

import java.util.ArrayList;
import java.util.List;

import com.yzx.flow.common.persistence.model.CustomerInfo;
import com.yzx.flow.common.persistence.model.FlowProductInfo;
import com.yzx.flow.common.persistence.model.OrderInfo;

/**
 * 客户信息及客户产品配置信息
 * @author Administrator
 *
 */
public class CustomerProductsBo {
	
	private Long customerId;
	/**
	 * 合作伙伴ID
	 */
	private Long partnerId;
	
	/**
	 * 客户名称
	 */
	private String customerName;
	
	/**
	 * 登录帐号
	 */
	private String account;
	
	/**
	 * 状态,0:待提交 1:商用２:暂停
	 */
	private Integer status;
	
	/**
	 * 客户等级
	 */
	private Integer customerLevel = 1;
	
	/**
	 * 客户余额不足是否收单：0 不收单；1 收单
	 */
	private Integer balanceLackConfigure;
	/**
	 * 联系人姓名
	 */
	private String linkmanName = "";

	/**
	 * 联系人电话
	 */
	private String linkmanMobile = "";

	/**
	 * 联系人邮箱
	 */
	private String linkmanEmail = "";
	/**
	 * 住址
	 */
	private String address = "";
	
	
	
	//////////////////////////////////////// 客户产品相关信息 ///////////////////////////////////////////
	/**
	 * 订单ID号
	 */
	private Long orderId;
	// 订单产品列表
	private List<FlowProductInfo> flowProductInfoList;
	
	public CustomerProductsBo() {}

	
	/**
	 * 获取前端客户数据
	 * @return
	 */
	public CustomerInfo buildCustomerInfo() {
		CustomerInfo res = new CustomerInfo();
		
		res.setCustomerId(this.customerId);
		res.setPartnerId(this.partnerId);
		
		res.setCustomerName(this.customerName);
		res.setAccount(this.account);
		
		res.setStatus(this.status);
		res.setCustomerLevel(this.customerLevel);
		res.setBalanceLackConfigure(this.balanceLackConfigure);
		
		res.setLinkmanEmail(this.linkmanEmail);
		res.setLinkmanMobile(this.linkmanMobile);
		res.setLinkmanName(this.linkmanName);
		res.setAddress(address);
		
		return res;
	}
	
	/**
	 * 获取前端 的客户产品配置信息
	 * @return
	 */
	public OrderInfo buildOrderInfo() {
		
		List<FlowProductInfo> temp = new ArrayList<FlowProductInfo>();
		if ( this.flowProductInfoList != null && !this.flowProductInfoList.isEmpty() ) {
			for ( FlowProductInfo p : flowProductInfoList ) {
				if ( p.getProductId() == null ) {
					temp.add(p);
				}
			}
			if ( !temp.isEmpty() )
				this.flowProductInfoList.removeAll(temp);
			temp.clear();
		}
		
		OrderInfo res = new OrderInfo();
		
		res.setOrderId(this.orderId);
		res.setFlowProductInfoList(this.flowProductInfoList);
		
		return res;
	}
	
	
	public Long getCustomerId() {
		return customerId;
	}
	
	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}
	
	public Long getPartnerId() {
		return partnerId;
	}
	
	public void setPartnerId(Long partnerId) {
		this.partnerId = partnerId;
	}
	
	public String getCustomerName() {
		return customerName;
	}
	
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	
	public String getAccount() {
		return account;
	}
	
	public void setAccount(String account) {
		this.account = account;
	}
	
	public Integer getStatus() {
		return status;
	}
	
	public void setStatus(Integer status) {
		this.status = status;
	}
	
	public Integer getCustomerLevel() {
		return customerLevel;
	}
	
	public void setCustomerLevel(Integer customerLevel) {
		this.customerLevel = customerLevel;
	}
	
	public Integer getBalanceLackConfigure() {
		return balanceLackConfigure;
	}
	
	public void setBalanceLackConfigure(Integer balanceLackConfigure) {
		this.balanceLackConfigure = balanceLackConfigure;
	}
	
	public String getLinkmanName() {
		return linkmanName;
	}
	
	public void setLinkmanName(String linkmanName) {
		this.linkmanName = linkmanName;
	}
	
	public String getLinkmanMobile() {
		return linkmanMobile;
	}
	
	public void setLinkmanMobile(String linkmanMobile) {
		this.linkmanMobile = linkmanMobile;
	}
	
	public String getLinkmanEmail() {
		return linkmanEmail;
	}
	
	public void setLinkmanEmail(String linkmanEmail) {
		this.linkmanEmail = linkmanEmail;
	}
	
	public String getAddress() {
		return address;
	}
	
	public void setAddress(String address) {
		this.address = address;
	}
	
	public Long getOrderId() {
		return orderId;
	}
	
	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}
	
	public List<FlowProductInfo> getFlowProductInfoList() {
		return flowProductInfoList;
	}
	
	public void setFlowProductInfoList(List<FlowProductInfo> flowProductInfoList) {
		this.flowProductInfoList = flowProductInfoList;
	}
}
