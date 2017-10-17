package com.yzx.flow.modular.channel.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.page.PageInfoBT;
import com.yzx.flow.common.persistence.dao.AreaCodeMapper;
import com.yzx.flow.common.persistence.model.ChannelCompany;
import com.yzx.flow.common.persistence.model.ChannelCompanyRecharge;
import com.yzx.flow.common.persistence.model.Staff;
import com.yzx.flow.modular.channel.service.IChannelCompanyService;
import com.yzx.flow.modular.system.dao.ChannelCompanyDao;

@Service
public class ChannelCompanyServiceImpl implements IChannelCompanyService{

	@Autowired
	private ChannelCompanyDao channelCompanyDao;
	

	/**
	 * 分页查找
	 * 
	 * @param page
	 * @return
	 */
	public PageInfoBT<ChannelCompany> pageQuery(Page<ChannelCompany> page) {
		List<ChannelCompany> list = null ;
		BigDecimal balance = null;
		BigDecimal money =null;
		BigDecimal unCheckPrice=null;
		BigDecimal successPrice = null;
		if(StringUtils.isNotBlank(String.valueOf(page.getParams().get("isValid")!=null?page.getParams().get("isValid"):""))){
			list = channelCompanyDao.validQuery(page);
		}else{
			list = channelCompanyDao.pageQuery(page);
		}
		for(ChannelCompany channelCompany : list){
			if(null == channelCompany.getBalance() || "".equals(channelCompany.getBalance())){
				balance = new BigDecimal(0);
			}else{
				balance = channelCompany.getBalance();
			}
			if(null == channelCompany.getMoney() || "".equals(channelCompany.getMoney())){
				money = new BigDecimal(0);
			}else{
				money = channelCompany.getMoney();
			}
			if(null == channelCompany.getUnCheckPrice() ||"".equals(channelCompany.getUnCheckPrice())){
				unCheckPrice = new BigDecimal(0.00);
			}else{
				unCheckPrice = channelCompany.getUnCheckPrice();
			}if(null == channelCompany.getSuccessPrice() || "".equals(channelCompany.getSuccessPrice())){
				successPrice = new BigDecimal(0);
			}else{
				successPrice = channelCompany.getSuccessPrice();
			}
			BigDecimal allowPrice = balance.add(money).subtract(unCheckPrice).subtract(successPrice);
			if(null == allowPrice || "".equals(allowPrice)){
				allowPrice = new BigDecimal(0);
			}
			channelCompany.setAllowBalance(allowPrice);
			if(channelCompany .getIsValid() >= 1){
				channelCompany.setIsValidDesc("有效");
			}else{
				channelCompany.setIsValidDesc("无效");
			}
		}
		PageInfoBT<ChannelCompany> resPage=new PageInfoBT<ChannelCompany>(list, page.getTotal());
        return resPage;
	}

	/**
	 * 插入数据
	 * 
	 * @param data
	 */
	public void insert(ChannelCompany data) {
		channelCompanyDao.insert(data);
	}

	/**
	 * 主键查找
	 * 
	 * @param companyId
	 * @return
	 */
	public ChannelCompany get(long companyId) {
		return channelCompanyDao.selectByPrimaryKey(companyId);
	}

	/**
	 * 修改数据
	 * 
	 * @param data
	 */
	public void update(ChannelCompany data) {
		channelCompanyDao.updateByPrimaryKey(data);
	}

	@Transactional(rollbackFor = Exception.class)
	public void saveAndUpdate(ChannelCompany data, Staff staff, String updaId) {

		if (StringUtils.isNotEmpty(updaId)) {// 判断有没有传主键，如果传了为更新，否则为新增
			data.setUpdator(staff.getLoginName());
			data.setCompanyId(Long.valueOf(updaId));
			this.update(data);
		} else {
			data.setCreator(staff.getLoginName());
		this.insert(data);
		}
	}
	/**
	 * 删除数据
	 * 
	 * @param supplierCode
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	public int delete(long supplierCode) {
		int i = channelCompanyDao.deleteByPrimaryKey(supplierCode);
		return i;
	}

	public int getChannelSuppliersById(long supplierCode) {
		int i = channelCompanyDao.getChannelSuppliersById(supplierCode);
		return i;
	}

	public List<ChannelCompany> selectChannelCompanyByName(String companyName) {
	
		List<ChannelCompany> list = channelCompanyDao.selectChannelCompanyByName(companyName);
		return list;
	}
	
	
	@Override
	public List<ChannelCompany> checkCompanyByCode(String companyCode) {
		List<ChannelCompany> list = channelCompanyDao.checkCompanyByCode(companyCode);
		return list;
	}
	
	@Override
	public List<ChannelCompany> checkCompanyByName(String companyName) {
		List<ChannelCompany> list = channelCompanyDao.checkCompanyByName(companyName);
		return list;
	}
	
	
	public ChannelCompany getChannelCompany(String companyCode,String companyName){
		return channelCompanyDao.getChannelCompany(companyCode,companyName);
	}

	public BigDecimal getPriceByCpId(Long companyId){
		return channelCompanyDao.getPriceByCpId(companyId);
	}
	
	public BigDecimal checkAllowPrice(Long companyId){
		return channelCompanyDao.getPriceByCpId(companyId);
	}
	
	public Map<String,BigDecimal> getAllowPrice(Map<String,BigDecimal> map){
		return channelCompanyDao.getAllowPrice(map);
	}
	
}
