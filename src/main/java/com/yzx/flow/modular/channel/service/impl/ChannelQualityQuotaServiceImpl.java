package com.yzx.flow.modular.channel.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yzx.flow.common.exception.MyException;
import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.page.PageInfoBT;
import com.yzx.flow.common.persistence.model.ChannelQualityQuota;
import com.yzx.flow.common.persistence.model.Staff;
import com.yzx.flow.common.util.RedisHttpUtil;
import com.yzx.flow.common.util.URLConstants;
import com.yzx.flow.modular.channel.service.IChannelQualityQuotaService;
import com.yzx.flow.modular.system.dao.ChannelQualityQuotaDao;

/**
 * 
 * <b>Title：</b>ChannelQualityQuotaService.java<br/>
 * <b>Description：</b> <br/>
 * <b>@author： </b>v5480<br/>
 * <b>@date：</b>2015-12-26 15:26:01<br/>
 * <b>Copyright (c) 2015 szwisdom Tech.</b>
 * 
 */
@Service("channelQualityQuotaService")
public class ChannelQualityQuotaServiceImpl implements IChannelQualityQuotaService {
	@Autowired
	private ChannelQualityQuotaDao channelQualityQuotaMapper;

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.channel.service.impl.IChannelQualityQuotaService#pageQuery(com.yzx.flow.common.page.Page)
	 */
	@Override
	public PageInfoBT<ChannelQualityQuota> pageQuery(Page<ChannelQualityQuota> page) {
		List<ChannelQualityQuota> list = channelQualityQuotaMapper.pageQuery(page);
		return new PageInfoBT<ChannelQualityQuota>(list, page.getTotal());
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.channel.service.impl.IChannelQualityQuotaService#insert(com.yzx.flow.common.persistence.model.ChannelQualityQuota)
	 */
	@Override
	public void insert(ChannelQualityQuota data) {
		channelQualityQuotaMapper.insert(data);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.channel.service.impl.IChannelQualityQuotaService#get(java.lang.Long)
	 */
	@Override
	public ChannelQualityQuota get(Long quotaId) {
		return channelQualityQuotaMapper.selectByPrimaryKey(quotaId);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.channel.service.impl.IChannelQualityQuotaService#saveAndUpdate(com.yzx.flow.common.persistence.model.ChannelQualityQuota, com.yzx.flow.common.persistence.model.Staff)
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveAndUpdate(ChannelQualityQuota data, Staff staff) {
		if (null != data.getQuotaId()) {// 判断有没有传主键，如果传了为更新，否则为新增
			data.setUpdateTime(new Date());
			data.setUpdator(staff.getLoginName());
			this.update(data);
		} else {
			data.setCreateTime(new Date());
			data.setCreator(staff.getLoginName());
			this.insert(data);
		}
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.channel.service.impl.IChannelQualityQuotaService#update(com.yzx.flow.common.persistence.model.ChannelQualityQuota)
	 */
	@Override
	public void update(ChannelQualityQuota data) {
		channelQualityQuotaMapper.updateByPrimaryKeySelective(data);
		String result = RedisHttpUtil.sendGet(URLConstants.DEL_REDIS_URL,
				URLConstants.T_CHANNEL_QUALITY_QUOTA, data.getQuotaId()+"");
		if (!"OK".equals(result)) {
			throw new MyException("删除Redis中信息出错,其请求URL参数为:" + URLConstants.T_CHANNEL_QUALITY_QUOTA+"\t"+data.getQuotaId());
		}
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.channel.service.impl.IChannelQualityQuotaService#delete(java.lang.Long)
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public int delete(Long quotaId) {
		int i = channelQualityQuotaMapper.deleteByPrimaryKey(quotaId);
		String result = RedisHttpUtil.sendGet(URLConstants.DEL_REDIS_URL,
				URLConstants.T_CHANNEL_QUALITY_QUOTA, quotaId+"");
		if (!"OK".equals(result)) {
			throw new  MyException("删除Redis中信息出错,其请求URL参数为:" + URLConstants.T_CHANNEL_QUALITY_QUOTA+"\t"+quotaId);
		}
		return i;
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.channel.service.impl.IChannelQualityQuotaService#selectQuotaInfo(java.lang.String)
	 */
	@Override
	public List<ChannelQualityQuota> selectQuotaInfo(String quotaName) {
		ChannelQualityQuota c = new ChannelQualityQuota();
		c.setQuotaName(quotaName);
		return channelQualityQuotaMapper.selectQuotaInfo(c);
	}
}