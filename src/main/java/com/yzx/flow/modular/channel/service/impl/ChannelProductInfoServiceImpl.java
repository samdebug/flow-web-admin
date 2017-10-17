package com.yzx.flow.modular.channel.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yzx.flow.common.exception.MyException;
import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.persistence.model.ChannelProductInfo;
import com.yzx.flow.common.util.RedisHttpUtil;
import com.yzx.flow.common.util.URLConstants;
import com.yzx.flow.modular.channel.service.IChannelProductInfoService;
import com.yzx.flow.modular.system.dao.ChannelProductInfoDao;

/**
 * <b>Title：</b>ChannelProductInfoService.java<br/> <b>Description：</b> <br/> <b>@author： </b>v5480<br/>
 * <b>@date：</b>2015-10-28 14:43:40<br/> <b>Copyright (c) 2015 szwisdom Tech.</b>
 */
@Service("channelProductInfoService")
public class ChannelProductInfoServiceImpl implements IChannelProductInfoService {
    @Autowired
    private ChannelProductInfoDao channelProductInfoDao;

    /* (non-Javadoc)
	 * @see com.yzx.flow.modular.channel.service.impl.IChannelProductInfoService#pageQuery(com.yzx.flow.common.page.Page)
	 */
    @Override
	public List<ChannelProductInfo> pageQuery(Page<ChannelProductInfo> page) {
        List<ChannelProductInfo> list = channelProductInfoDao.pageQuery(page);
        return list;
    }

    /* (non-Javadoc)
	 * @see com.yzx.flow.modular.channel.service.impl.IChannelProductInfoService#insert(com.yzx.flow.common.persistence.model.ChannelProductInfo)
	 */
    @Override
	public void insert(ChannelProductInfo data) {
        channelProductInfoDao.insert(data);
        String result = RedisHttpUtil.sendGet(URLConstants.DEL_REDIS_URL,
				URLConstants.T_ACCESS_CHANNEL_PRODUCTS,data.getChannelSeqId()+"");
		if (!"OK".equals(result)) {
			throw new MyException("删除Redis中信息出错,其请求URL参数为:" + URLConstants.T_ACCESS_CHANNEL_PRODUCTS+"\t"+data.getChannelSeqId());
		}
    }

    /* (non-Javadoc)
	 * @see com.yzx.flow.modular.channel.service.impl.IChannelProductInfoService#get(java.lang.Long)
	 */
    @Override
	public ChannelProductInfo get(Long channelProductSeqId) {
        return channelProductInfoDao.selectByPrimaryKey(channelProductSeqId);
    }

    /* (non-Javadoc)
	 * @see com.yzx.flow.modular.channel.service.impl.IChannelProductInfoService#saveAndUpdate(com.yzx.flow.common.persistence.model.ChannelProductInfo)
	 */
    @Override
	@Transactional(rollbackFor = Exception.class)
    public void saveAndUpdate(ChannelProductInfo data) {
        if (null != data.getChannelProductSeqId()) {// 判断有没有传主键，如果传了为更新，否则为新增
            this.update(data);
        } else {
            this.insert(data);
        }
    }

    /* (non-Javadoc)
	 * @see com.yzx.flow.modular.channel.service.impl.IChannelProductInfoService#update(com.yzx.flow.common.persistence.model.ChannelProductInfo)
	 */
    @Override
	public void update(ChannelProductInfo data) {
        channelProductInfoDao.updateByPrimaryKey(data);
        String key=channelProductInfoDao.selectByPrimaryKey(data.getChannelProductSeqId()).getChannelSeqId()+"";
		String result = RedisHttpUtil.sendGet(URLConstants.DEL_REDIS_URL,
				URLConstants.T_ACCESS_CHANNEL_PRODUCTS,key);
		if (!"OK".equals(result)) {
			throw new MyException("删除Redis中信息出错,其请求URL参数为:" + URLConstants.T_ACCESS_CHANNEL_PRODUCTS+"\t"+key);
		}
    }

    /* (non-Javadoc)
	 * @see com.yzx.flow.modular.channel.service.impl.IChannelProductInfoService#delete(java.lang.Long)
	 */
    @Override
	@Transactional(rollbackFor = Exception.class)
    public int delete(Long channelProductSeqId) {
    	String key=channelProductInfoDao.selectByPrimaryKey(channelProductSeqId).getChannelSeqId()+"";
    	int i = channelProductInfoDao.deleteByPrimaryKey(channelProductSeqId);
		String result = RedisHttpUtil.sendGet(URLConstants.DEL_REDIS_URL,
				URLConstants.T_ACCESS_CHANNEL_PRODUCTS,key);
		if (!"OK".equals(result)) {
			throw new MyException("删除Redis中信息出错,其请求URL参数为:" + URLConstants.T_ACCESS_CHANNEL_PRODUCTS+"\t"+key);
		}
        return i;
    }
    
    /* (non-Javadoc)
	 * @see com.yzx.flow.modular.channel.service.impl.IChannelProductInfoService#selectByChannelSeqId(java.lang.Long)
	 */
    @Override
	public List<ChannelProductInfo> selectByChannelSeqId(Long channelSeqId) {
        ChannelProductInfo info = new ChannelProductInfo();
        info.setChannelSeqId(channelSeqId);
        return channelProductInfoDao.selectByInfo(info);
    }
    
    /* (non-Javadoc)
	 * @see com.yzx.flow.modular.channel.service.impl.IChannelProductInfoService#changeChannelProductStatus(java.util.Map)
	 */
    @Override
	@Transactional(rollbackFor = Exception.class)
    public int changeChannelProductStatus(Map<String, Object> params) {
        return channelProductInfoDao.changeChannelProductStatus(params);
    }
}
