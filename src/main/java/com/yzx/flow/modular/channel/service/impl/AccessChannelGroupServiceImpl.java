package com.yzx.flow.modular.channel.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yzx.flow.common.exception.MyException;
import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.page.PageInfoBT;
import com.yzx.flow.common.persistence.model.AccessChannelGroup;
import com.yzx.flow.common.persistence.model.AccessChannelInfo;
import com.yzx.flow.common.persistence.model.ChannelGroupToGroup;
import com.yzx.flow.common.persistence.model.ChannelToGroup;
import com.yzx.flow.common.persistence.model.SuppilerTradeDay;
import com.yzx.flow.common.util.RedisHttpUtil;
import com.yzx.flow.common.util.URLConstants;
import com.yzx.flow.modular.channel.service.IAccessChannelGroupService;
import com.yzx.flow.modular.system.dao.AccessChannelGroupDao;
import com.yzx.flow.modular.system.dao.ChannelGroupToGroupDao;
import com.yzx.flow.modular.system.dao.ChannelToGroupDao;


/**
 * <b>Title：</b>AccessChannelGroupService.java<br/>
 * <b>Description：</b> <br/>
 * <b>@author： </b>v5480<br/>
 * <b>@date：</b>2015-10-28 14:43:40<br/>
 * <b>Copyright (c) 2015 szwisdom Tech.</b>
 */
@Service("accessChannelGroupService")
public class AccessChannelGroupServiceImpl implements IAccessChannelGroupService {
	@Autowired
	private AccessChannelGroupDao accessChannelGroupDao;

	@Autowired
	private ChannelToGroupDao channelToGroupDao;
	
	@Autowired
	private ChannelGroupToGroupDao channelGroupToGroupDao;

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.channel.service.impl.IAccessChannelGroupService#pageQuery(com.yzx.flow.common.page.Page)
	 */
	@Override
	public PageInfoBT<AccessChannelGroup> pageQuery(Page<AccessChannelGroup> page) {
		List<AccessChannelGroup> list = accessChannelGroupDao.pageQuery(page);
		PageInfoBT<AccessChannelGroup> resultPage = new PageInfoBT<>(list, page.getTotal());
		return resultPage;
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.channel.service.impl.IAccessChannelGroupService#insert(com.yzx.flow.common.persistence.model.AccessChannelGroup)
	 */
	@Override
	public void insert(AccessChannelGroup data) {
		accessChannelGroupDao.insert(data);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.channel.service.impl.IAccessChannelGroupService#get(java.lang.Long)
	 */
	@Override
	public AccessChannelGroup get(Long channelGroupId) {
		return accessChannelGroupDao.selectByPrimaryKey(channelGroupId);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.channel.service.impl.IAccessChannelGroupService#selectByInfo(com.yzx.flow.common.persistence.model.AccessChannelGroup)
	 */
	@Override
	public List<AccessChannelGroup> selectByInfo(AccessChannelGroup info) {
		return accessChannelGroupDao.selectByInfo(info);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.channel.service.impl.IAccessChannelGroupService#getChannelToGroupsByChannelGroupId(java.lang.Long)
	 */
	@Override
	public List<ChannelToGroup> getChannelToGroupsByChannelGroupId(
			Long channelGroupId) {
		return channelToGroupDao.selectByChannelGroupId(channelGroupId);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.channel.service.impl.IAccessChannelGroupService#saveAndUpdate(com.yzx.flow.common.persistence.model.AccessChannelGroup)
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveAndUpdate(AccessChannelGroup data) {
		// 判断有没有传主键，如果传了为更新，否则为新增
		if (data.getChannelGroupId() == null) {
			this.insert(data);
			// 通道组关联表
			for (AccessChannelInfo aci : data.getAccessChannelInfoList()) {
				if (null == aci.getChannelSeqId()) {
					continue;
				}
				ChannelToGroup channelToGroup = new ChannelToGroup();
				channelToGroup.setChannelGroupId(data.getChannelGroupId());
				channelToGroup.setChannelSeqId(aci.getChannelSeqId());
				channelToGroup.setWeight(aci.getWeight());
				channelToGroupDao.insert(channelToGroup);
			}
		} else {
			this.update(data);
			String result = RedisHttpUtil.sendGet(URLConstants.DEL_REDIS_URL,
					URLConstants.T_ACCESS_CHANNEL_GROUP,
					data.getDispatchChannel());
			if (!"OK".equals(result)) {
				throw new MyException("删除Redis中信息出错,其请求URL参数为:" +URLConstants.T_ACCESS_CHANNEL_GROUP+"\t"+
						data.getDispatchChannel());
			}
			// 通道组关联表
			// 表中原始数据
			List<ChannelToGroup> originChannelToGroupList = channelToGroupDao
					.selectByChannelGroupId(data.getChannelGroupId());

			Map<String, ChannelToGroup> originChannelToGroupMap = new HashMap<String, ChannelToGroup>();
			Map<String, AccessChannelInfo> finalChannelToGroupMap = new HashMap<String, AccessChannelInfo>();
			for (ChannelToGroup channelToGroup : originChannelToGroupList) {
				originChannelToGroupMap.put(channelToGroup.getChannelGroupId()
						+ "_" + channelToGroup.getChannelSeqId(),
						channelToGroup);
			}
			for (AccessChannelInfo aci : data.getAccessChannelInfoList()) {
				if (null == aci.getChannelSeqId()) {
					continue;
				}
				finalChannelToGroupMap.put(
						data.getChannelGroupId() + "_" + aci.getChannelSeqId(),
						aci);
			}

			for (ChannelToGroup channelToGroup : originChannelToGroupList) {
				String key = channelToGroup.getChannelGroupId() + "_"
						+ channelToGroup.getChannelSeqId();
				if (finalChannelToGroupMap.keySet().contains(key)) {
					AccessChannelInfo aci = finalChannelToGroupMap.get(key);
					// 更新
					ChannelToGroup info = new ChannelToGroup();
					info.setSeqId(channelToGroup.getSeqId());
					info.setChannelGroupId(data.getChannelGroupId());
					info.setChannelSeqId(aci.getChannelSeqId());
					info.setWeight(aci.getWeight());
					channelToGroupDao.updateByPrimaryKey(info);
				} else {
					// 删除
					channelToGroupDao.deleteByPrimaryKey(channelToGroup
							.getSeqId());
				}
			}
			for (AccessChannelInfo aci : data.getAccessChannelInfoList()) {
				if (null == aci.getChannelSeqId()) {
					continue;
				}
				String key = data.getChannelGroupId() + "_"
						+ aci.getChannelSeqId();
				if (!originChannelToGroupMap.keySet().contains(key)) {
					// 新增
					ChannelToGroup info = new ChannelToGroup();
					info.setChannelGroupId(data.getChannelGroupId());
					info.setChannelSeqId(aci.getChannelSeqId());
					info.setWeight(aci.getWeight());
					channelToGroupDao.insert(info);
				}
			}
			
			//修改被引用的通道组对应关系修改
			if (1 == data.getIsQuote()) {
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("quoteGroupId", data.getChannelGroupId());
				List<ChannelGroupToGroup> list = channelGroupToGroupDao.selectByChannelGroupId(params);
				if (null != list && !list.isEmpty()) {
					for (ChannelGroupToGroup channelGroupToGroup : list) {
						params = new HashMap<String, Object>();
						params.put("channelGroupId", channelGroupToGroup.getChannelGroupId());
						params.put("quoteGroupId", data.getChannelGroupId());
						List<ChannelToGroup> originList = channelToGroupDao.selectByQuoteGroupId(params);//被引用的通道
						finalChannelToGroupMap = new HashMap<String, AccessChannelInfo>();
						for (AccessChannelInfo aci : data.getAccessChannelInfoList()) {
							if (null == aci.getChannelSeqId()) {
								continue;
							}
							finalChannelToGroupMap.put(channelGroupToGroup.getChannelGroupId() + "_" + aci.getChannelSeqId(),aci);
						}
						for (ChannelToGroup channelToGroup : originList) {
							String key = channelToGroup.getChannelGroupId() + "_" + channelToGroup.getChannelSeqId();
							if (finalChannelToGroupMap.keySet().contains(key)) {
								AccessChannelInfo aci = finalChannelToGroupMap.get(key);
								// 更新
								ChannelToGroup info = new ChannelToGroup();
								info.setSeqId(channelToGroup.getSeqId());
								info.setChannelGroupId(channelGroupToGroup.getChannelGroupId());
								info.setChannelSeqId(aci.getChannelSeqId());
								info.setWeight(aci.getWeight());
								info.setBelongGroup(data.getChannelGroupId());
								channelToGroupDao.updateByPrimaryKey(info);
							} else {
								// 删除
								channelToGroupDao.deleteByPrimaryKey(channelToGroup.getSeqId());
							}
						}
						originChannelToGroupMap = new HashMap<String, ChannelToGroup>();
						for (ChannelToGroup channelToGroup : originList) {
							originChannelToGroupMap.put(channelToGroup.getChannelGroupId()+ "_" + channelToGroup.getChannelSeqId(),channelToGroup);
						}
						for (AccessChannelInfo aci : data.getAccessChannelInfoList()) {
							if (null == aci.getChannelSeqId()) {
								continue;
							}
							String key = channelGroupToGroup.getChannelGroupId() + "_" + aci.getChannelSeqId();
							if (!originChannelToGroupMap.keySet().contains(key)) {
								// 新增
								ChannelToGroup info = new ChannelToGroup();
								info.setChannelGroupId(channelGroupToGroup.getChannelGroupId());
								info.setChannelSeqId(aci.getChannelSeqId());
								info.setWeight(aci.getWeight());
								info.setBelongGroup(data.getChannelGroupId());
								channelToGroupDao.insert(info);
							}
						}
					}
				}
			}
		}
		
		//通道组关联通道组
		if (null != data.getChannelGroupList() && !data.getChannelGroupList().isEmpty()) {
			//删除之前的对应关系
			List<ChannelGroupToGroup> groupToGroupList = getGroupToGroupsByChannelGroupId(data.getChannelGroupId());
			if (null != groupToGroupList && !groupToGroupList.isEmpty()) {//对应关系删除
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("channelGroupId", data.getChannelGroupId());
				channelGroupToGroupDao.deleteByGrouplId(params);
				
				for (ChannelGroupToGroup channelGroupToGroup : groupToGroupList) {
					params = new HashMap<String, Object>();
					params.put("channelGroupId", data.getChannelGroupId());
					params.put("belongGroup", channelGroupToGroup.getQuoteGroupId());
					channelToGroupDao.deleteQuoteGroup(params);
				}
			}
			//添加对应关系
			for (AccessChannelGroup groupTmp : data.getChannelGroupList()) {
				if (null == groupTmp.getChannelGroupId() || null == groupTmp.getChannelGroupId()) {
					continue;
				}
				ChannelGroupToGroup channelGroupToGroup = new ChannelGroupToGroup();
				channelGroupToGroup.setChannelGroupId(data.getChannelGroupId());
				channelGroupToGroup.setQuoteGroupId(groupTmp.getChannelGroupId());
				channelGroupToGroupDao.insert(channelGroupToGroup);
				//插入通道
				List<ChannelToGroup> list = channelToGroupDao.selectByChannelGroupId(groupTmp.getChannelGroupId());
				for (ChannelToGroup channelToGroup : list) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("channelGroupId", data.getChannelGroupId());
					map.put("channelSeqId", channelToGroup.getChannelSeqId());
					map.put("belongGroup", groupTmp.getChannelGroupId());
					List<ChannelToGroup> isExistChannelList = channelToGroupDao.selectByGroupAndChannel(map);
					if(null == isExistChannelList || isExistChannelList.isEmpty()){//不存在
						ChannelToGroup tmp = new ChannelToGroup();
						tmp.setChannelGroupId(data.getChannelGroupId());
						tmp.setChannelSeqId(channelToGroup.getChannelSeqId());
						tmp.setWeight(channelToGroup.getWeight());
						tmp.setBelongGroup(groupTmp.getChannelGroupId());
						channelToGroupDao.insert(tmp);
					}
				}
			}
		}else {
			//删除之前的对应关系
			List<ChannelGroupToGroup> groupToGroupList = getGroupToGroupsByChannelGroupId(data.getChannelGroupId());
			if (null != groupToGroupList && !groupToGroupList.isEmpty()) {//对应关系删除
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("channelGroupId", data.getChannelGroupId());
				channelGroupToGroupDao.deleteByGrouplId(params);
				
				for (ChannelGroupToGroup channelGroupToGroup : groupToGroupList) {
					params = new HashMap<String, Object>();
					params.put("channelGroupId", data.getChannelGroupId());
					params.put("belongGroup", channelGroupToGroup.getQuoteGroupId());
					channelToGroupDao.deleteQuoteGroup(params);
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.channel.service.impl.IAccessChannelGroupService#update(com.yzx.flow.common.persistence.model.AccessChannelGroup)
	 */
	@Override
	public void update(AccessChannelGroup data) {
		accessChannelGroupDao.updateByPrimaryKey(data);
		String result = RedisHttpUtil.sendGet(URLConstants.DEL_REDIS_URL,
				URLConstants.T_ACCESS_CHANNEL_GROUP, data.getDispatchChannel());
		if (!"OK".equals(result)) {
			throw new MyException("删除Redis中信息出错,其请求URL参数为:" + URLConstants.T_ACCESS_CHANNEL_GROUP+"\t"+data.getDispatchChannel());
		}
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.channel.service.impl.IAccessChannelGroupService#delete(java.lang.Long)
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public int delete(Long channelGroupId) {
		AccessChannelGroup accessChannelGroup = accessChannelGroupDao.selectByPrimaryKey(channelGroupId);
		channelToGroupDao.deleteByChannelId(channelGroupId);
		//通道组关系删除
		Map<String, Object> paramsTmp = new HashMap<String, Object>();
		paramsTmp.put("channelGroupId", channelGroupId);
		channelGroupToGroupDao.deleteByGrouplId(paramsTmp);
		int i = accessChannelGroupDao.deleteByPrimaryKey(channelGroupId);
		
		if (1 == accessChannelGroup.getIsQuote()) {//如果是可引用通道，删除对应关系
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("quoteGroupId", accessChannelGroup.getChannelGroupId());
			List<ChannelGroupToGroup> list = channelGroupToGroupDao.selectByChannelGroupId(params);
			if (null != list && !list.isEmpty()) {
				for (ChannelGroupToGroup channelGroupToGroup : list) {
					params = new HashMap<String, Object>();
					params.put("channelGroupId", channelGroupToGroup.getChannelGroupId());
					params.put("belongGroup", accessChannelGroup.getChannelGroupId());
					channelToGroupDao.deleteQuoteGroup(params);
				}
				
				params = new HashMap<String, Object>();
				params.put("quoteGroupId", accessChannelGroup.getChannelGroupId());
				channelGroupToGroupDao.deleteByGrouplId(params);
				
				//删除引用通道组的redis缓存
				for (ChannelGroupToGroup channelGroupToGroup : list) {
					AccessChannelGroup tmp = get(channelGroupToGroup.getChannelGroupId());
					String result = RedisHttpUtil.sendGet(URLConstants.DEL_REDIS_URL,
							tmp.getDispatchChannel(),URLConstants.DELALL);
					if (!"OK".equals(result)) {
						throw new MyException("删除Redis中信息出错,其请求URL参数为:" + tmp.getDispatchChannel()+"\t"+URLConstants.DELALL);
					}
				}
			}
			
		}
		String key=accessChannelGroup.getDispatchChannel();
		String result = RedisHttpUtil.sendGet(URLConstants.DEL_REDIS_URL,URLConstants.T_ACCESS_CHANNEL_GROUP,key);
		if (!"OK".equals(result)) {
			throw new MyException("删除Redis中信息出错,其请求URL参数为:" + URLConstants.T_ACCESS_CHANNEL_GROUP+"\t"+key);
		}
		return i;
	}
	
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.channel.service.impl.IAccessChannelGroupService#selectQuoteChannelGroup(java.util.Map)
	 */
	@Override
	public List<AccessChannelGroup> selectQuoteChannelGroup(Map<String, Object> params) {
		return accessChannelGroupDao.selectQuoteChannelGroup(params);
	}
	
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.channel.service.impl.IAccessChannelGroupService#getGroupToGroupsByChannelGroupId(java.lang.Long)
	 */
	@Override
	public List<ChannelGroupToGroup> getGroupToGroupsByChannelGroupId(Long channelGroupId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("channelGroupId", channelGroupId);
		return channelGroupToGroupDao.selectByChannelGroupId(params);
	}
	
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.channel.service.impl.IAccessChannelGroupService#getGroupToGroupsByQuoteGroupId(java.lang.Long)
	 */
	@Override
	public List<ChannelGroupToGroup> getGroupToGroupsByQuoteGroupId(Long quoteGroupId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("quoteGroupId", quoteGroupId);
		return channelGroupToGroupDao.selectByChannelGroupId(params);
	}
	
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.channel.service.impl.IAccessChannelGroupService#groupAddToGroup(com.yzx.flow.common.persistence.model.AccessChannelGroup)
	 */
    @Override
	@Transactional(rollbackFor = Exception.class)
	public void groupAddToGroup(AccessChannelGroup data) {
    	//通道组关联通道组
    	if (null != data.getChannelGroupList() && !data.getChannelGroupList().isEmpty()) {
    		//删除之前的对应关系
    		List<ChannelGroupToGroup> groupToGroupList = getGroupToGroupsByQuoteGroupId(data.getChannelGroupId());
    		if (null != groupToGroupList && !groupToGroupList.isEmpty()) {//对应关系删除
    			Map<String, Object> params = null;
    			for (ChannelGroupToGroup channelGroupToGroup : groupToGroupList) {
    				//删除通道组对应通道组关系
    				params = new HashMap<String, Object>();
    				params.put("channelGroupId", channelGroupToGroup.getChannelGroupId());
        			channelGroupToGroupDao.deleteByGrouplId(params);
        			
        			//删除通道对应通道组关系
        			params = new HashMap<String, Object>();
					params.put("channelGroupId", channelGroupToGroup.getChannelGroupId());
					params.put("belongGroup", channelGroupToGroup.getQuoteGroupId());
					channelToGroupDao.deleteQuoteGroup(params);
					
					//删除通道组redis
					AccessChannelGroup accessChannelGroupTmp = accessChannelGroupDao.selectByPrimaryKey(channelGroupToGroup.getChannelGroupId());
					String result = RedisHttpUtil.sendGet(URLConstants.DEL_REDIS_URL,
							accessChannelGroupTmp.getDispatchChannel(),URLConstants.DELALL);
					if (!"OK".equals(result)) {
						throw new MyException("删除Redis中信息出错,其请求URL参数为:" +accessChannelGroupTmp.getDispatchChannel()+"\t"+
										accessChannelGroupTmp.getDispatchChannel());
					}
				}
    		}
    		//添加对应关系
    		for (AccessChannelGroup groupTmp : data.getChannelGroupList()) {
    			if (null == groupTmp.getChannelGroupId() || null == groupTmp.getChannelGroupId()) {
    				continue;
    			}
    			ChannelGroupToGroup channelGroupToGroup = new ChannelGroupToGroup();
    			channelGroupToGroup.setChannelGroupId(groupTmp.getChannelGroupId());
    			channelGroupToGroup.setQuoteGroupId(data.getChannelGroupId());
    			channelGroupToGroupDao.insert(channelGroupToGroup);
    			//插入通道
    			List<ChannelToGroup> list = channelToGroupDao.selectByChannelGroupId(data.getChannelGroupId());
    			for (ChannelToGroup channelToGroup : list) {
    				Map<String, Object> map = new HashMap<String, Object>();
    				map.put("channelGroupId", groupTmp.getChannelGroupId());
    				map.put("channelSeqId", channelToGroup.getChannelSeqId());
    				map.put("belongGroup", data.getChannelGroupId());
    				List<ChannelToGroup> isExistChannelList = channelToGroupDao.selectByGroupAndChannel(map);
    				if(null == isExistChannelList || isExistChannelList.isEmpty()){//不存在
    					ChannelToGroup tmp = new ChannelToGroup();
    					tmp.setChannelGroupId(groupTmp.getChannelGroupId());
    					tmp.setChannelSeqId(channelToGroup.getChannelSeqId());
    					tmp.setWeight(channelToGroup.getWeight());
    					tmp.setBelongGroup(data.getChannelGroupId());
    					channelToGroupDao.insert(tmp);
    				}
    			}
    			
    			//删除通道组redis
    			AccessChannelGroup accessChannelGroupTmp = accessChannelGroupDao.selectByPrimaryKey(groupTmp.getChannelGroupId());
    			String result = RedisHttpUtil.sendGet(URLConstants.DEL_REDIS_URL,
    					accessChannelGroupTmp.getDispatchChannel(),URLConstants.DELALL);
    			if (!"OK".equals(result)) {
    				throw new MyException("删除Redis中信息出错,其请求URL参数为:" +accessChannelGroupTmp.getDispatchChannel()+"\t"+
    								accessChannelGroupTmp.getDispatchChannel());
    			}
    		}
    	}else {
    		//删除之前的对应关系
    		List<ChannelGroupToGroup> groupToGroupList = getGroupToGroupsByQuoteGroupId(data.getChannelGroupId());
    		if (null != groupToGroupList && !groupToGroupList.isEmpty()) {//对应关系删除
    			Map<String, Object> params = new HashMap<String, Object>();
    			params.put("channelGroupId", data.getChannelGroupId());
    			channelGroupToGroupDao.deleteByGrouplId(params);
    			for (ChannelGroupToGroup channelGroupToGroup : groupToGroupList) {
    				//删除自定义通道组对应通道组关系
    				params = new HashMap<String, Object>();
					params.put("channelGroupId", channelGroupToGroup.getChannelGroupId());
					channelGroupToGroupDao.deleteByGrouplId(params);
					
					//删除通道对应通道组关系
    				params = new HashMap<String, Object>();
    				params.put("channelGroupId", channelGroupToGroup.getChannelGroupId());
    				params.put("belongGroup", channelGroupToGroup.getQuoteGroupId());
    				channelToGroupDao.deleteQuoteGroup(params);
    				
    				//删除通道组redis
    				AccessChannelGroup accessChannelGroupTmp = accessChannelGroupDao.selectByPrimaryKey(channelGroupToGroup.getChannelGroupId());
    				String result = RedisHttpUtil.sendGet(URLConstants.DEL_REDIS_URL,
    						accessChannelGroupTmp.getDispatchChannel(),URLConstants.DELALL);
    				if (!"OK".equals(result)) {
    					throw new MyException("删除Redis中信息出错,其请求URL参数为:" +accessChannelGroupTmp.getDispatchChannel()+"\t"+
    									accessChannelGroupTmp.getDispatchChannel());
    				}
    			}
    	}
	}
  }
}
