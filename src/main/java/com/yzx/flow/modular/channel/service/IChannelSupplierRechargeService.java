package com.yzx.flow.modular.channel.service;

import org.springframework.transaction.annotation.Transactional;

import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.page.PageInfoBT;
import com.yzx.flow.common.persistence.model.ChannelSupplierRecharge;
import com.yzx.flow.common.persistence.model.Staff;

public interface IChannelSupplierRechargeService {

	/**
	 * 分页查询充值记录
	 * @param page
	 * @return
	 */
	public abstract PageInfoBT<ChannelSupplierRecharge> pageQuery(
			Page<ChannelSupplierRecharge> page);

	public abstract void insert(ChannelSupplierRecharge data);

	public abstract ChannelSupplierRecharge get(Long supplierRechargeId);

	public abstract void saveAndUpdate(ChannelSupplierRecharge data);

	public abstract void update(ChannelSupplierRecharge data);

	public abstract int delete(Long supplierRechargeId);

	/**
	 * 保存充值记录
	 * @param data
	 * @param staff
	 */
	public abstract void saveSupplierRecharge(ChannelSupplierRecharge data,
			Staff staff, String operateIp);

}