package com.yzx.flow.modular.channel.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.page.PageInfoBT;
import com.yzx.flow.common.persistence.model.ChannelCompany;
import com.yzx.flow.common.persistence.model.Staff;

public interface IChannelCompanyService {
	/**
	 * 分页查找
	 * 
	 * @param page
	 * @return
	 */
	public PageInfoBT<ChannelCompany> pageQuery(Page<ChannelCompany> page);

	/**
	 * 插入数据
	 * 
	 * @param data
	 */
	public void insert(ChannelCompany data);

	/**
	 * 主键查找
	 * 
	 * @param companyId
	 * @return
	 */
	public ChannelCompany get(long companyId);

	/**
	 * 修改数据
	 * 
	 * @param data
	 */
	public void update(ChannelCompany data);

	public void saveAndUpdate(ChannelCompany data, Staff staff, String updaId);
	/**
	 * 删除数据
	 * 
	 * @param supplierCode
	 * @return
	 */
	public int delete(long supplierCode);

	public int getChannelSuppliersById(long supplierCode);

	public List<ChannelCompany> selectChannelCompanyByName(String companyName);
	
	public List<ChannelCompany> checkCompanyByCode(String companyCode);

	public List<ChannelCompany> checkCompanyByName(String companyName);
	
	public ChannelCompany getChannelCompany(String companyCode,String companyName);

	public BigDecimal getPriceByCpId(Long companyId);
	
	public BigDecimal checkAllowPrice(Long companyId);
	
	public Map<String,BigDecimal> getAllowPrice(Map<String,BigDecimal> map);
}
