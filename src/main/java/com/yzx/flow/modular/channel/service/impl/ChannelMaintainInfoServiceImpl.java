package com.yzx.flow.modular.channel.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;











import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.page.PageInfoBT;
import com.yzx.flow.common.persistence.dao.AreaCodeMapper;
import com.yzx.flow.common.persistence.model.AreaCode;
import com.yzx.flow.common.persistence.model.ChannelMaintainInfo;
import com.yzx.flow.common.persistence.model.Staff;
import com.yzx.flow.common.util.CommonUtil;
import com.yzx.flow.modular.channel.service.IChannelMaintainInfoService;
import com.yzx.flow.modular.system.dao.ChannelMaintainInfoDao;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;


/**
 * 
 * <b>Title：</b>ChannelMaintainInfoService.java<br/>
 * <b>Description：</b> <br/>
 * <b>@author： </b>v5480<br/>
 * <b>@date：</b>2015-10-30 16:58:29<br/>
 * <b>Copyright (c) 2015 szwisdom Tech.</b>
 * 
 */
@Service("channelMaintainInfoService")
public class ChannelMaintainInfoServiceImpl implements IChannelMaintainInfoService {
	@Autowired
	private ChannelMaintainInfoDao channelMaintainInfoDao;
	
	@Autowired
	private AreaCodeMapper areaCodeDao;

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.channel.service.impl.IChannelMaintainInfoService#pageQuery(com.yzx.flow.common.page.Page)
	 */
	@Override
	public PageInfoBT<ChannelMaintainInfo> pageQuery(Page<ChannelMaintainInfo> page) {
		List<ChannelMaintainInfo> list = channelMaintainInfoDao.pageQuery(page);
		return new PageInfoBT<ChannelMaintainInfo>(list, page.getTotal());
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.channel.service.impl.IChannelMaintainInfoService#insert(com.yzx.flow.common.persistence.model.ChannelMaintainInfo)
	 */
	@Override
	public void insert(ChannelMaintainInfo data) {
		channelMaintainInfoDao.insert(data);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.channel.service.impl.IChannelMaintainInfoService#get(java.lang.Long)
	 */
	@Override
	public ChannelMaintainInfo get(Long maintainSeqId) {
		return channelMaintainInfoDao.selectByPrimaryKey(maintainSeqId);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.channel.service.impl.IChannelMaintainInfoService#saveAndUpdate(com.yzx.flow.common.persistence.model.ChannelMaintainInfo, com.yzx.flow.common.persistence.model.Staff, javax.servlet.http.HttpServletRequest)
	 */
	@Override
	public void saveAndUpdate(ChannelMaintainInfo data, Staff staff,HttpServletRequest request) {
		if (null != data.getMaintainSeqId()) {// 判断有没有传主键，如果传了为更新，否则为新增
			data.setUpdateTime(new Date());
			data.setUpdator(staff.getLoginName());
			this.update(data);
		} else {
			data.setIp(CommonUtil.getIp(request));
			data.setCreateTime(new Date());
			data.setCreator(staff.getLoginName());
			this.insert(data);
		}
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.channel.service.impl.IChannelMaintainInfoService#update(com.yzx.flow.common.persistence.model.ChannelMaintainInfo)
	 */
	@Override
	public void update(ChannelMaintainInfo data) {
		channelMaintainInfoDao.updateByPrimaryKeySelective(data);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.channel.service.impl.IChannelMaintainInfoService#delete(java.lang.Long)
	 */
	@Override
	public int delete(Long maintainSeqId) {
		return channelMaintainInfoDao.deleteByPrimaryKey(maintainSeqId);
	}
	
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.channel.service.impl.IChannelMaintainInfoService#selectAreaCodeAll()
	 */
	@Override
	public List<AreaCode> selectAreaCodeAll(){
		return areaCodeDao.getAreaCodeAll();
	}
}