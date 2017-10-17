package com.yzx.flow.modular.channel.service.impl;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.page.PageInfoBT;
import com.yzx.flow.common.persistence.model.ChannelSupplier;
import com.yzx.flow.common.persistence.model.ChannelSupplierRecharge;
import com.yzx.flow.common.persistence.model.Staff;
import com.yzx.flow.modular.channel.service.IChannelSupplierRechargeService;
import com.yzx.flow.modular.system.dao.ChannelSupplierDao;
import com.yzx.flow.modular.system.dao.ChannelSupplierRechargeDao;

/**
 * 充值记录
 * @author hc4gw02
 *
 */
@Service("channelSupplierRechargeService")
public class ChannelSupplierRechargeServiceImpl implements IChannelSupplierRechargeService {
	private static final Logger LOGGER = LoggerFactory.getLogger(ChannelSupplierRechargeServiceImpl.class);
	
	@Autowired
	private ChannelSupplierRechargeDao channelSupplierRechargeDao;
	
	@Autowired
	private ChannelSupplierDao channelSupplierDao;
	
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.channel.service.impl.IChannelSupplierRechargeService#pageQuery(com.yzx.flow.common.page.Page)
	 */
	@Override
	public PageInfoBT<ChannelSupplierRecharge> pageQuery(Page<ChannelSupplierRecharge> page) {
		LOGGER.info("分页查询充值记录,params={}",page.getParams());
		List<ChannelSupplierRecharge> list = channelSupplierRechargeDao.pageQuery(page);
		PageInfoBT<ChannelSupplierRecharge> resPage = new PageInfoBT<ChannelSupplierRecharge>(list, page.getTotal());
		return resPage;
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.channel.service.impl.IChannelSupplierRechargeService#insert(com.yzx.flow.common.persistence.model.ChannelSupplierRecharge)
	 */
	@Override
	public void insert(ChannelSupplierRecharge data) {
		channelSupplierRechargeDao.insert(data);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.channel.service.impl.IChannelSupplierRechargeService#get(java.lang.Long)
	 */
	@Override
	public ChannelSupplierRecharge get(Long supplierRechargeId) {
		return channelSupplierRechargeDao.selectByPrimaryKey(supplierRechargeId);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.channel.service.impl.IChannelSupplierRechargeService#saveAndUpdate(com.yzx.flow.common.persistence.model.ChannelSupplierRecharge)
	 */
	@Override
	public void saveAndUpdate(ChannelSupplierRecharge data) {
		if (null != data.getSupplierRechargeId()) {// 判断有没有传主键，如果传了为更新，否则为新增
			this.update(data);
		} else {
			this.insert(data);
		}
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.channel.service.impl.IChannelSupplierRechargeService#update(com.yzx.flow.common.persistence.model.ChannelSupplierRecharge)
	 */
	@Override
	public void update(ChannelSupplierRecharge data) {
		channelSupplierRechargeDao.updateByPrimaryKey(data);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.channel.service.impl.IChannelSupplierRechargeService#delete(java.lang.Long)
	 */
	@Override
	public int delete(Long supplierRechargeId) {
		return channelSupplierRechargeDao.deleteByPrimaryKey(supplierRechargeId);
	}
	
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.channel.service.impl.IChannelSupplierRechargeService#saveSupplierRecharge(com.yzx.flow.common.persistence.model.ChannelSupplierRecharge, com.yzx.flow.common.persistence.model.Staff, java.lang.String)
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveSupplierRecharge(ChannelSupplierRecharge data, Staff staff,String operateIp) {
        // 更新供应商累计充值
        ChannelSupplier channelSupplier = channelSupplierDao.selectByPrimaryKey(data.getSupplierCode());
        if ("0".equals(data.getType())) {//充值
        	channelSupplier.setRechargeAmount(channelSupplier.getRechargeAmount().add(data.getMoney()));
        } 
        channelSupplier.setUpdator(staff.getLoginName());
        channelSupplier.setUpdateTime(new Date());
        channelSupplierDao.updateByPrimaryKey(channelSupplier);
        
        // 新增充值记录
        data.setInputTime(new Date());
        data.setLoginName(staff.getLoginName());
        data.setOperatorName(staff.getLoginName());
        data.setOperateIp(operateIp);
        channelSupplierRechargeDao.insert(data);
    }
}
