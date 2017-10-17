package com.yzx.flow.modular.sms.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.persistence.model.SmsChannelInfo;
import com.yzx.flow.modular.sms.service.ISmsChannelInfoService;
import com.yzx.flow.modular.system.dao.SmsChannelInfoDao;

/**
 * 
 * <b>Title：</b>SmsChannelInfoService.java<br/>
 * <b>Description：</b> <br/>
 * <b>@author： </b>v5480<br/>
 * <b>@date：</b>2015-08-01 11:38:16<br/>
 * <b>Copyright (c) 2015 szwisdom Tech.</b>
 * 
 */
@Service("smsChannelInfoService")
public class SmsChannelInfoServiceImpl implements ISmsChannelInfoService {
	@Autowired
	private SmsChannelInfoDao smsChannelInfoDao;

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.sms.service.ISmsChannelInfoService#pageQuery(com.yzx.flow.common.page.Page)
	 */
	public Page<SmsChannelInfo> pageQuery(Page<SmsChannelInfo> page) {
		List<SmsChannelInfo> list = smsChannelInfoDao.pageQuery(page);
		page.setDatas(list);
		return page;
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.sms.service.ISmsChannelInfoService#insert(com.yzx.flow.common.persistence.model.SmsChannelInfo)
	 */
	public void insert(SmsChannelInfo data) {
		smsChannelInfoDao.insert(data);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.sms.service.ISmsChannelInfoService#get(java.lang.Long)
	 */
	public SmsChannelInfo get(Long smsChannelId) {
		return smsChannelInfoDao.selectByPrimaryKey(smsChannelId);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.sms.service.ISmsChannelInfoService#saveAndUpdate(com.yzx.flow.common.persistence.model.SmsChannelInfo)
	 */
	public void saveAndUpdate(SmsChannelInfo data) {
		if (null != data.getSmsChannelId()) {// 判断有没有传主键，如果传了为更新，否则为新增
			data.setModifyDate(new Date());
			this.update(data);
		} else {
			data.setCreateDate(new Date());
			this.insert(data);
		}
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.sms.service.ISmsChannelInfoService#update(com.yzx.flow.common.persistence.model.SmsChannelInfo)
	 */
	public void update(SmsChannelInfo data) {
		smsChannelInfoDao.updateByPrimaryKey(data);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.sms.service.ISmsChannelInfoService#delete(java.lang.Long)
	 */
	public int delete(Long smsChannelId) {
		return smsChannelInfoDao.deleteByPrimaryKey(smsChannelId);
	}
}