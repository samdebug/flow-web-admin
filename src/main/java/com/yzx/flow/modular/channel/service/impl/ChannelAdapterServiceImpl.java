package com.yzx.flow.modular.channel.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.page.PageInfoBT;
import com.yzx.flow.common.persistence.model.AccessChannelInfo;
import com.yzx.flow.common.persistence.model.ChannelAdapter;
import com.yzx.flow.common.persistence.model.Staff;
import com.yzx.flow.core.shiro.ShiroKit;
import com.yzx.flow.core.shiro.ShiroUser;
import com.yzx.flow.modular.channel.service.IChannelAdapterService;
import com.yzx.flow.modular.system.dao.ChannelAdapterDao;

/**
 * 
 * <b>Title：</b>ChannelAdapterService.java<br/>
 * <b>Description：</b> <br/>
 * <b>@author： </b>v5480<br/>
 * <b>@date：</b>2016-05-06 15:41:55<br/>
 * <b>Copyright (c) 2016 szwisdom Tech.</b>
 * 
 */
@Service("channelAdapterService")
public class ChannelAdapterServiceImpl implements IChannelAdapterService {
	@Autowired
	private ChannelAdapterDao channelAdapterDao;

	@Override
	public  PageInfoBT<ChannelAdapter> pageQuery(Page<ChannelAdapter> page) {
			List<ChannelAdapter> list = channelAdapterDao.pageQuery(page);
			PageInfoBT<ChannelAdapter> resPage=new PageInfoBT<ChannelAdapter>(list, page.getTotal());
	        return resPage;
	}

	@Override
	public void insert(ChannelAdapter data) {
		channelAdapterDao.insert(data);
	}

	@Override
	public ChannelAdapter get(Long adapterId) {
		return channelAdapterDao.selectByPrimaryKey(adapterId);
	}

	@Override
	public List<ChannelAdapter> find(ChannelAdapter data) {
		return channelAdapterDao.find(data);
	}

	@Override
	public void saveAndUpdate(ChannelAdapter data) throws Exception {
		  ShiroUser user =  ShiroKit.getUser();
		  Staff staff = new Staff();
		  staff.setLoginName(user.getName());
		if (null != data.getAdapterId()) {// 判断有没有传主键，如果传了为更新，否则为新增
			data.setUpdateTime(new Date());
			data.setUpdator(staff.getLoginName());
			this.update(data);
		} else {
			data.setCreateTime(new Date());
			data.setCreator(staff.getLoginName());
			this.insert(data);
		}
	}

	@Override
	public void update(ChannelAdapter data) {
		channelAdapterDao.updateByPrimaryKeySelective(data);
	}

	@Override
	public int delete(Long adapterId) {
		return channelAdapterDao.deleteByPrimaryKey(adapterId);
	}
}