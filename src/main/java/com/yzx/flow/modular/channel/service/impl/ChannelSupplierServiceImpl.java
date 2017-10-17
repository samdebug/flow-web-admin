package com.yzx.flow.modular.channel.service.impl;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;













import com.yzx.flow.common.exception.MyException;
import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.page.PageInfoBT;
import com.yzx.flow.common.persistence.model.AccessChannelInfo;
import com.yzx.flow.common.persistence.model.ChannelAdapter;
import com.yzx.flow.common.persistence.model.ChannelCompany;
import com.yzx.flow.common.persistence.model.ChannelSupplier;
import com.yzx.flow.common.persistence.model.Staff;
import com.yzx.flow.common.util.RedisHttpUtil;
import com.yzx.flow.common.util.URLConstants;
import com.yzx.flow.modular.channel.service.IChannelSupplierService;
import com.yzx.flow.modular.system.dao.AccessChannelInfoDao;
import com.yzx.flow.modular.system.dao.ChannelAdapterDao;
import com.yzx.flow.modular.system.dao.ChannelCompanyDao;
import com.yzx.flow.modular.system.dao.ChannelSupplierDao;

/**
 * <b>Title：</b>ChannelSupplierService.java<br/>
 * <b>Description：</b> <br/>
 * <b>@author： </b>v5480<br/>
 * <b>@date：</b>2015-10-28 14:43:40<br/>
 * <b>Copyright (c) 2015 szwisdom Tech.</b>
 */
@Service("channelSupplierService")
public class ChannelSupplierServiceImpl implements IChannelSupplierService {
	@Autowired
	private ChannelSupplierDao channelSupplierDao;
	@Autowired
	private ChannelCompanyDao channelCompanyDao;

	@Autowired
	private AccessChannelInfoDao accessChannelInfoDao;
	@Autowired
	private ChannelAdapterDao channelAdapterDao;

	private static final Logger logger = LoggerFactory.getLogger(ChannelSupplierServiceImpl.class);
	
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.channel.service.impl.IChannelSupplierService#pageQuery(com.yzx.flow.common.page.Page)
	 */
	@Override
	public PageInfoBT<ChannelSupplier> pageQuery(Page<ChannelSupplier> page) {
		List<ChannelSupplier> list = channelSupplierDao.pageQuery(page);
		PageInfoBT<ChannelSupplier> resPage=new PageInfoBT<ChannelSupplier>(list, page.getTotal());
	   return resPage;
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.channel.service.impl.IChannelSupplierService#insert(com.yzx.flow.common.persistence.model.ChannelSupplier)
	 */
	@Override
	public void insert(ChannelSupplier data) {
		channelSupplierDao.insert(data);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.channel.service.impl.IChannelSupplierService#get(java.lang.String)
	 */
	@Override
	public ChannelSupplier get(String supplierCode) {
		ChannelSupplier channelSupplier = channelSupplierDao.selectByPrimaryKey(supplierCode);
		if (channelSupplier != null) {
			if (StringUtils.isNotEmpty(channelSupplier.getCompanyId())) {
				ChannelCompany selectByPrimaryKey = channelCompanyDao.selectByPrimaryKey(Long
						.valueOf(channelSupplier.getCompanyId()));
				channelSupplier.setCompanyName(selectByPrimaryKey.getCompanyName());
			}

		}

		return channelSupplier;
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.channel.service.impl.IChannelSupplierService#saveAndUpdate(com.yzx.flow.common.persistence.model.ChannelSupplier, com.yzx.flow.common.persistence.model.Staff, java.lang.String)
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveAndUpdate(ChannelSupplier data, Staff staff, String updaId){
		if (null == data.getAdapterId()) {
			throw new MyException("请选择接入通道适配器");
		}
		ChannelAdapter channelAdapter = channelAdapterDao
				.selectByPrimaryKey(data.getAdapterId());
		if (null == channelAdapter) {
			throw new MyException("根据adapterId=" + data.getAdapterId()
					+ "找不到对应的接入通道适配器");
		}
		data.setAdapterName(channelAdapter.getClazzName());
		if (StringUtils.isNotEmpty(updaId)) {// 判断有没有传主键，如果传了为更新，否则为新增
			data.setUpdateTime(new Date());
			data.setUpdator(staff.getLoginName());
			this.update(data);
		} else {
			boolean isFlag = getSupplierCode(data.getSupplierCode());
			if (!isFlag) {
				throw new MyException("当前供应商编码重复");//new FOSSException("01", "当前供应商编码重复")
			}
			data.setCreator(staff.getLoginName());
			data.setCreateTime(new Date());
			data.setBalance(BigDecimal.valueOf(0));
			data.setConsumeAmount(BigDecimal.valueOf(0));
			data.setRechargeAmount(BigDecimal.valueOf(0));
			data.setSupplierSign(data.getSupplierCode());//sign暂定等于SupplierCode
			this.insert(data);
		}
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.channel.service.impl.IChannelSupplierService#getSupplierCode(java.lang.String)
	 */
	@Override
	public boolean getSupplierCode(String supplierCode) {
		ChannelSupplier channelSupplier = channelSupplierDao
				.getSupplierCode(supplierCode);
		if (null == channelSupplier) {
			return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.channel.service.impl.IChannelSupplierService#update(com.yzx.flow.common.persistence.model.ChannelSupplier)
	 */
	@Override
	public void update(ChannelSupplier data) {
		channelSupplierDao.updateByPrimaryKeySelective(data);
		//删除redis操作
		String result = RedisHttpUtil.sendGet(URLConstants.DEL_REDIS_URL,
				URLConstants.T_CHANNEL_SUPPLIER, data.getSupplierCode());
		if (!"OK".equals(result)) {
			throw new MyException("删除Redis中信息出错,其请求URL参数为:" + URLConstants.T_CHANNEL_SUPPLIER+"\t"+data.getSupplierCode());
		}
		//if (2 == data.getIsValid().intValue()) {//供应商无效删除通道组redis
			List<Map<String, Object>> channelGroupList = channelSupplierDao.findChannelGroups(data.getSupplierCode());
			if (null != channelGroupList && !channelGroupList.isEmpty()) {
				for (Map<String, Object> map : channelGroupList) {
					String str = RedisHttpUtil.sendGet(URLConstants.DEL_REDIS_URL,
							map.get("dispatchChannel").toString(),URLConstants.DELALL);
					if (!"OK".equals(str)) {
						throw new MyException("删除Redis中信息出错,其请求URL参数为:" + map.get("dispatchChannel").toString()+"\t"+URLConstants.DELALL);
					}
				}
			}
		//}
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.channel.service.impl.IChannelSupplierService#delete(java.lang.String)
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public int delete(String supplierCode) {
		int i = channelSupplierDao.deleteByPrimaryKey(supplierCode);
		if(i > 0){//删除数据成功后删除redis
			String URL = MessageFormat.format(URLConstants.DEL_REDIS_URL,
					URLConstants.T_CHANNEL_SUPPLIER, supplierCode);
			logger.info("删除redis操作,url={}",URL);
			String result = RedisHttpUtil.sendGet(URLConstants.DEL_REDIS_URL,
					URLConstants.T_CHANNEL_SUPPLIER, supplierCode);
			if (!"OK".equals(result)) {
				throw new MyException("删除Redis中信息出错,其请求URL参数为:" + URLConstants.T_CHANNEL_SUPPLIER+"\t"+supplierCode);
			}
		}
		return i;
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.channel.service.impl.IChannelSupplierService#selectChannelSupplierByName(java.lang.String)
	 */
	@Override
	public List<ChannelSupplier> selectChannelSupplierByName(String supplierCode) {
		return channelSupplierDao.selectChannelSupplierByName(supplierCode);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.channel.service.impl.IChannelSupplierService#selectBySupplierName(java.lang.String)
	 */
	@Override
	public List<ChannelSupplier> selectBySupplierName(String supplierName) {
		return channelSupplierDao.selectBySupplierName(supplierName);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.channel.service.impl.IChannelSupplierService#selectByInfo(java.lang.String)
	 */
	@Override
	public boolean selectByInfo(String supplierCode) {
		AccessChannelInfo accessChannelInfo = new AccessChannelInfo();
		accessChannelInfo.setSupplierCode(supplierCode);
		List<AccessChannelInfo> accessChannelInfoList = accessChannelInfoDao
				.selectByInfo(accessChannelInfo);
		if (accessChannelInfoList.isEmpty()) {
			return false;
		}
		return true;
	}
	
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.channel.service.impl.IChannelSupplierService#batchUpdate(java.util.List)
	 */
	@Override
	public int batchUpdate(List<ChannelSupplier> list){
		return channelSupplierDao.batchUpdate(list);
	}
}
