package com.yzx.flow.modular.channel.service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.page.PageInfoBT;
import com.yzx.flow.common.persistence.model.ChannelSupplier;
import com.yzx.flow.common.persistence.model.Staff;

public interface IChannelSupplierService {

	public abstract PageInfoBT<ChannelSupplier> pageQuery(Page<ChannelSupplier> page);

	public abstract void insert(ChannelSupplier data);

	public abstract ChannelSupplier get(String supplierCode);

	public abstract void saveAndUpdate(ChannelSupplier data, Staff staff,
			String updaId);

	public abstract boolean getSupplierCode(String supplierCode);

	public abstract void update(ChannelSupplier data);

	public abstract int delete(String supplierCode);

	public abstract List<ChannelSupplier> selectChannelSupplierByName(
			String supplierCode);

	public abstract List<ChannelSupplier> selectBySupplierName(
			String supplierName);

	public abstract boolean selectByInfo(String supplierCode);

	public abstract int batchUpdate(List<ChannelSupplier> list);

}