package com.yzx.flow.modular.channel.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.util.JSONUtils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yzx.flow.common.exception.MyException;
import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.page.PageInfoBT;
import com.yzx.flow.common.persistence.dao.AreaCodeMapper;
import com.yzx.flow.common.persistence.model.AccessChannelGroup;
import com.yzx.flow.common.persistence.model.AccessChannelInfo;
import com.yzx.flow.common.persistence.model.AreaCode;
import com.yzx.flow.common.persistence.model.ChannelProductInfo;
import com.yzx.flow.common.persistence.model.ChannelToGroup;
import com.yzx.flow.common.persistence.model.FlowPackageInfo;
import com.yzx.flow.common.persistence.model.Staff;
import com.yzx.flow.common.util.Constant;
import com.yzx.flow.common.util.RedisHttpUtil;
import com.yzx.flow.common.util.URLConstants;
import com.yzx.flow.modular.channel.service.IAccessChannelInfoHisService;
import com.yzx.flow.modular.channel.service.IAccessChannelInfoService;
import com.yzx.flow.modular.system.dao.AccessChannelGroupDao;
import com.yzx.flow.modular.system.dao.AccessChannelInfoDao;
import com.yzx.flow.modular.system.dao.ChannelProductInfoDao;
import com.yzx.flow.modular.system.dao.ChannelToGroupDao;
import com.yzx.flow.modular.system.dao.FlowPackageInfoDao;


/**
 * <b>Title：</b>AccessChannelInfoService.java<br/> <b>Description：</b> <br/> <b>@author： </b>v5480<br/>
 * <b>@date：</b>2015-10-28 14:43:40<br/> <b>Copyright (c) 2015 szwisdom Tech.</b>
 */
@Service("accessChannelInfoService")
public class AccessChannelInfoServiceImpl implements IAccessChannelInfoService {
    private static final Logger LOG = LoggerFactory.getLogger(AccessChannelInfoServiceImpl.class);
    
    @Autowired
    private AccessChannelInfoDao accessChannelInfoDao;

    @Autowired
    private FlowPackageInfoDao flowPackageInfoDao;

    @Autowired
    private AreaCodeMapper areaCodeDao;

    @Autowired
    private ChannelProductInfoDao channelProductInfoDao;

    @Autowired
    private ChannelToGroupDao channelToGroupDao;

    @Autowired
    private IAccessChannelInfoHisService accessChannelInfoHisService;//记录修改日志
    
    @Autowired
    private AccessChannelGroupDao accessChannelGroupDao;
    
    /* (non-Javadoc)
	 * @see com.yzx.flow.modular.channel.service.impl.IAccessChannelInfoService#pageQuery(com.yzx.flow.common.page.Page)
	 */
    @Override
	public  PageInfoBT<AccessChannelInfo> pageQuery(Page<AccessChannelInfo> page) {
        List<AccessChannelInfo> list = accessChannelInfoDao.pageQuery(page);
        PageInfoBT<AccessChannelInfo> resPage=new PageInfoBT<AccessChannelInfo>(list, page.getTotal());
        return resPage;
    }

    /* (non-Javadoc)
	 * @see com.yzx.flow.modular.channel.service.impl.IAccessChannelInfoService#insert(com.yzx.flow.common.persistence.model.AccessChannelInfo)
	 */
    @Override
	public void insert(AccessChannelInfo data) {
        accessChannelInfoDao.insert(data);
    }

    /* (non-Javadoc)
	 * @see com.yzx.flow.modular.channel.service.impl.IAccessChannelInfoService#get(java.lang.Long)
	 */
    @Override
	public AccessChannelInfo get(Long channelSeqId) {
        return accessChannelInfoDao.selectByPrimaryKey(channelSeqId);
    }

    /* (non-Javadoc)
	 * @see com.yzx.flow.modular.channel.service.impl.IAccessChannelInfoService#saveAndUpdate(com.yzx.flow.common.persistence.model.AccessChannelInfo, com.yzx.flow.common.persistence.model.Staff)
	 */
    @Override
	@Transactional(rollbackFor = Exception.class)
    public void saveAndUpdate(AccessChannelInfo data,Staff staff) {
        // 判断有没有传主键，如果传了为更新，否则为新增
        if (data.getChannelSeqId() == null) {
            this.insert(data);
            // 通道产品信息表
            for (FlowPackageInfo fpi : data.getFlowPackageInfoList()) {
                if (null == fpi.getPackageId()) {
                    continue;
                }
                // 唯一性校验 (2015/11/30 去掉通道产品ID和Name的唯一性校验)
//                checkUniqueChannelProduct(fpi, Constant.TYPE_CHANNEL_ID);
//                checkUniqueChannelProduct(fpi, Constant.TYPE_CHANNEL_NAME);
                // 新增通道产品信息
                ChannelProductInfo info = new ChannelProductInfo();
                info.setChannelSeqId(data.getChannelSeqId());
                info.setChannelProductId(fpi.getChannelProductId());
                info.setChannelProductName(fpi.getChannelProductName());
                info.setPackageId(fpi.getPackageId());
                info.setPrice(fpi.getCostPrice());
                info.setQuarzTime(fpi.getQuarzTime());
                info.setRemark("");
                channelProductInfoDao.insert(info);
                String result = RedisHttpUtil.sendGet(URLConstants.DEL_REDIS_URL,
        				URLConstants.T_ACCESS_CHANNEL_PRODUCTS,data.getChannelSeqId()+"");
        		if (!"OK".equals(result)) {
        			throw new MyException("删除Redis中信息出错,其请求URL参数为:" + URLConstants.T_ACCESS_CHANNEL_PRODUCTS+"\t"+data.getChannelSeqId());
        		}
            	List<String> groupidlist=channelToGroupDao.selectChannelGroupId(data.getChannelSeqId());
            	for(String l:groupidlist){
            		String result1 = RedisHttpUtil.sendGet(URLConstants.DEL_REDIS_URL,
            				l,URLConstants.DELALL);
            		if (!"OK".equals(result1)) {
            			throw new MyException("删除Redis中信息出错,其请求URL参数为:" +l+"\t"+URLConstants.DELALL);
            		}
            	}
            }
            
        } else {
            this.update(data);
           List<String> groupidlist=channelToGroupDao.selectChannelGroupId(data.getChannelSeqId());
        	for(String l:groupidlist){
        		String result1 = RedisHttpUtil.sendGet(URLConstants.DEL_REDIS_URL,
        				l,URLConstants.DELALL);
        		if (!"OK".equals(result1)) {
        			throw new MyException("删除Redis中信息出错,其请求URL参数为:" +l+"\t"+URLConstants.DELALL);
        		}
        	}
            // 通道产品信息表
            // 表中原始数据
            ChannelProductInfo record = new ChannelProductInfo();
            record.setChannelSeqId(data.getChannelSeqId());
            List<ChannelProductInfo> originChannelProductList = channelProductInfoDao.selectByInfo(record);
            
            Map<String, ChannelProductInfo> originProductMap = new HashMap<String, ChannelProductInfo>();
            Map<String, FlowPackageInfo> finalProductMap = new HashMap<String, FlowPackageInfo>();
            for (ChannelProductInfo channelProductInfo : originChannelProductList) {
                originProductMap.put(channelProductInfo.getChannelSeqId() + "_" + channelProductInfo.getPackageId(), channelProductInfo);
            }
            for (FlowPackageInfo fpi : data.getFlowPackageInfoList()) {
                if (null == fpi.getPackageId()) {
                    continue;
                }
                finalProductMap.put(data.getChannelSeqId() + "_" + fpi.getPackageId(), fpi);
            }
            
            for (ChannelProductInfo channelProductInfo : originChannelProductList) {
                String key = channelProductInfo.getChannelSeqId() + "_" + channelProductInfo.getPackageId();
                if (finalProductMap.keySet().contains(key)) {
                    FlowPackageInfo fpi = finalProductMap.get(key);
                    // 唯一性校验 (2015/11/30 去掉通道产品ID和Name的唯一性校验)
//                    if (!fpi.getChannelProductId().equals(channelProductInfo.getChannelProductId())) {
//                        checkUniqueChannelProduct(fpi, Constant.TYPE_CHANNEL_ID);
//                    }
//                    if (!fpi.getChannelProductName().equals(channelProductInfo.getChannelProductName())) {
//                        checkUniqueChannelProduct(fpi, Constant.TYPE_CHANNEL_NAME);
//                    }
                    // 更新
                    ChannelProductInfo info = new ChannelProductInfo();
                    info.setChannelProductSeqId(channelProductInfo.getChannelProductSeqId());
                    info.setChannelSeqId(data.getChannelSeqId());
                    info.setChannelProductId(fpi.getChannelProductId());
                    info.setChannelProductName(fpi.getChannelProductName());
                    info.setPackageId(fpi.getPackageId());
                    info.setPrice(fpi.getCostPrice());
                    info.setQuarzTime(fpi.getQuarzTime());
                    /**
                     * 如果定时生效时间为空   则不操作 channel_product_info 表
                     */
                    //先删除表中有的历史数据
                    channelProductInfoDao.deleteChannelQuarz(info);
                    if(StringUtils.isBlank(fpi.getQuarzTime())){
                    	channelProductInfoDao.updateByPrimaryKey(info);                    	
                    	String result = RedisHttpUtil.sendGet(URLConstants.DEL_REDIS_URL,
                    			URLConstants.T_ACCESS_CHANNEL_PRODUCTS,channelProductInfo.getChannelSeqId()+"");
                    	if (!"OK".equals(result)) {
                    		throw new MyException("删除Redis中信息出错,其请求URL参数为:" + URLConstants.T_ACCESS_CHANNEL_PRODUCTS+"\t"+channelProductInfo.getChannelSeqId());
                    	}
                    }else{
                    	//如果定时时间不为空，则操作channel_info_quarz
                    	LOG.info("【新增定时修改订单价格】,更改人={},更改信息为",staff.getLoginName(),JSONUtils.valueToString(info));
                    	channelProductInfoDao.insertChannelQuarz(info);  
                    }
                    
                } else {
                    channelProductInfoDao.deleteByPrimaryKey(channelProductInfo.getChannelProductSeqId());
                    // 删除
            		String result = RedisHttpUtil.sendGet(URLConstants.DEL_REDIS_URL,
            				URLConstants.T_ACCESS_CHANNEL_PRODUCTS,channelProductInfo.getChannelSeqId()+"");
            		if (!"OK".equals(result)) {
            			throw new MyException("删除Redis中信息出错,其请求URL参数为:" + URLConstants.T_ACCESS_CHANNEL_PRODUCTS+"\t"+channelProductInfo.getChannelSeqId());
            		}
                }
            }
            for (FlowPackageInfo fpi : data.getFlowPackageInfoList()) {
                if (null == fpi.getPackageId()) {
                    continue;
                }
                String key = data.getChannelSeqId() + "_" + fpi.getPackageId();
                if (!originProductMap.keySet().contains(key)) {
                    // 唯一性校验 (2015/11/30 去掉通道产品ID和Name的唯一性校验)
//                    checkUniqueChannelProduct(fpi, Constant.TYPE_CHANNEL_ID);
//                    checkUniqueChannelProduct(fpi, Constant.TYPE_CHANNEL_NAME);
                    // 新增
                    ChannelProductInfo info = new ChannelProductInfo();
                    info.setChannelSeqId(data.getChannelSeqId());
                    info.setChannelProductId(fpi.getChannelProductId());
                    info.setChannelProductName(fpi.getChannelProductName());
                    info.setPackageId(fpi.getPackageId());
                    info.setPrice(fpi.getCostPrice());
                    info.setRemark("");
                    channelProductInfoDao.insert(info);
                }
            }
            
          //添加日志(修改)
          accessChannelInfoHisService.insert(data,"0");
        }
    }

    /* (non-Javadoc)
	 * @see com.yzx.flow.modular.channel.service.impl.IAccessChannelInfoService#update(com.yzx.flow.common.persistence.model.AccessChannelInfo)
	 */
    @Override
	public void update(AccessChannelInfo data) {
        accessChannelInfoDao.updateByPrimaryKey(data);
       List<String> groupidlist=channelToGroupDao.selectChannelGroupId(data.getChannelSeqId());
    	for(String l:groupidlist){
    		String result1 = RedisHttpUtil.sendGet(URLConstants.DEL_REDIS_URL,
    				l,URLConstants.DELALL);
    		if (!"OK".equals(result1)) {
    			throw new MyException("删除Redis中信息出错,其请求URL参数为:" +l+"\t"+URLConstants.DELALL);
    		}
    	}
    }
    
    /**
     * 更新通道维护区域
     */
    public void updateChannelMaintainZone(Map params){
    	accessChannelInfoDao.updateChannelMaintainZone(params);
    }

    /* (non-Javadoc)
	 * @see com.yzx.flow.modular.channel.service.impl.IAccessChannelInfoService#delete(java.lang.Long, com.yzx.flow.common.persistence.model.AccessChannelInfo)
	 */
    @Override
	@Transactional(rollbackFor = Exception.class)
    public int delete(Long channelSeqId,AccessChannelInfo data) {
        // 删除通道组关联表
        List<String> groupidlist=channelToGroupDao.selectChannelGroupId(channelSeqId);
        channelToGroupDao.deleteByChannelSeqId(channelSeqId);
    	for(String l:groupidlist){
    		String result1 = RedisHttpUtil.sendGet(URLConstants.DEL_REDIS_URL,
    				l,URLConstants.DELALL);
    		if (!"OK".equals(result1)) {
    			throw new MyException("删除Redis中信息出错,其请求URL参数为:" +l+"\t"+URLConstants.DELALL);
    		}
    	}
        // 删除通道产品信息表
        String key= channelSeqId+"";//channelProductInfoDao.selectByPrimaryKey(channelSeqId).getChannelSeqId()+"";
        channelProductInfoDao.deleteByChannelSeqId(channelSeqId);
		String result = RedisHttpUtil.sendGet(URLConstants.DEL_REDIS_URL,
				URLConstants.T_ACCESS_CHANNEL_PRODUCTS,key);
		if (!"OK".equals(result)) {
			throw new MyException("删除Redis中信息出错,其请求URL参数为:" + URLConstants.T_ACCESS_CHANNEL_PRODUCTS+"\t"+key);
		}
		//添加日志(修改)
		AccessChannelInfo info=accessChannelInfoDao.selectByPrimaryKey(channelSeqId);
		info.setUpdator(data.getUpdator());
		info.setUpdateTime(data.getUpdateTime());
        accessChannelInfoHisService.insert(info,"2");
        return accessChannelInfoDao.deleteByPrimaryKey(channelSeqId);
    }
    
    /* (non-Javadoc)
	 * @see com.yzx.flow.modular.channel.service.impl.IAccessChannelInfoService#selectALL()
	 */
    @Override
	public List<AreaCode> selectALL(){
        return areaCodeDao.getAreaCodeAll();
    }
    
    /* (non-Javadoc)
	 * @see com.yzx.flow.modular.channel.service.impl.IAccessChannelInfoService#selectByInfo(com.yzx.flow.common.persistence.model.AccessChannelInfo)
	 */
    @Override
	public List<AccessChannelInfo> selectByInfo(AccessChannelInfo info) {
        return accessChannelInfoDao.selectByInfo(info);
    }

    /* (non-Javadoc)
	 * @see com.yzx.flow.modular.channel.service.impl.IAccessChannelInfoService#selectAllChannelInfo(java.lang.Long[], java.lang.String)
	 */
    @Override
	public List<AccessChannelInfo> selectAllChannelInfo(Long[] channelSeqIds,String text) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (channelSeqIds != null && channelSeqIds.length == 0) {
            channelSeqIds = null;
        }
        map.put("channelSeqIds", channelSeqIds);
        map.put("text", text);
        return accessChannelInfoDao.selectAllChannelInfo(map);
    }
    
    /* (non-Javadoc)
	 * @see com.yzx.flow.modular.channel.service.impl.IAccessChannelInfoService#getFlowPackageInfoList(java.lang.String, java.lang.String[], java.lang.String, java.lang.String)
	 */
    @Override
	public List<FlowPackageInfo> getFlowPackageInfoList(String packageType, String[] packageIds, String zone, String operator) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (packageIds != null && packageIds.length == 0) {
            packageIds = null;
        }
        map.put("isCombo2", packageType);
        map.put("packageIds", packageIds);
        map.put("zone", zone);
        map.put("operator", operator);
        return flowPackageInfoDao.selectFlowPackageInfoList(map);
    }
    
    /**
     * check产品通道ID唯一
     */
    private void checkUniqueChannelProduct(FlowPackageInfo fpi, String type) {
        ChannelProductInfo info = new ChannelProductInfo();
        String errorMsg = "";
        if (Constant.TYPE_CHANNEL_ID.equals(type)) {
            info.setChannelProductId(fpi.getChannelProductId());
            errorMsg = "通道产品ID重复或已经存在，请重新输入";
        } else if (Constant.TYPE_CHANNEL_NAME.equals(type)) {
            errorMsg = "通道产品名称重复或已经存在，请重新输入";
            info.setChannelProductName(fpi.getChannelProductName());
        }
        if (!channelProductInfoDao.selectByInfo(info).isEmpty()) {
            LOG.error(errorMsg);
            throw new MyException(errorMsg);
        }
    }
    
    /* (non-Javadoc)
	 * @see com.yzx.flow.modular.channel.service.impl.IAccessChannelInfoService#selectByChannelSeqId(java.lang.Long)
	 */
    @Override
	public List<ChannelToGroup> selectByChannelSeqId(Long channelSeqId) {
		return channelToGroupDao.selectByChannelSeqId(channelSeqId);
	}
    
    /* (non-Javadoc)
	 * @see com.yzx.flow.modular.channel.service.impl.IAccessChannelInfoService#addToGroup(com.yzx.flow.common.persistence.model.AccessChannelInfo)
	 */
    @Override
	@Transactional(rollbackFor = Exception.class)
	public void addToGroup(AccessChannelInfo data) {
    	// 通道组关联表
		// 表中原始数据
		List<ChannelToGroup> originChannelToGroupList = channelToGroupDao.selectByChannelSeqId(data.getChannelSeqId());
		Map<String, ChannelToGroup> originChannelToGroupMap = new HashMap<String, ChannelToGroup>();
		Map<String, AccessChannelGroup> finalChannelToGroupMap = new HashMap<String, AccessChannelGroup>();
		for (ChannelToGroup channelToGroup : originChannelToGroupList) {
				originChannelToGroupMap.put(channelToGroup.getChannelGroupId()
						+ "_" + channelToGroup.getChannelSeqId(),channelToGroup);
				//删除通道组redis
				AccessChannelGroup accessChannelGroupTmp = accessChannelGroupDao.selectByPrimaryKey(channelToGroup.getChannelGroupId());
				String result = RedisHttpUtil.sendGet(URLConstants.DEL_REDIS_URL,
						accessChannelGroupTmp.getDispatchChannel(),URLConstants.DELALL);
				if (!"OK".equals(result)) {
					throw new MyException("删除Redis中信息出错,其请求URL参数为:" +URLConstants.T_ACCESS_CHANNEL_GROUP+"\t"+
									accessChannelGroupTmp.getDispatchChannel());
				}
		}
		for (AccessChannelGroup acg : data.getChannelGroupList()) {
			if (null == acg.getChannelGroupId()) {
					continue;
				}
			finalChannelToGroupMap.put(acg.getChannelGroupId() + "_" + data.getChannelSeqId(),acg);
			//删除通道组redis
			AccessChannelGroup accessChannelGroupTmp = accessChannelGroupDao.selectByPrimaryKey(acg.getChannelGroupId());
			String result = RedisHttpUtil.sendGet(URLConstants.DEL_REDIS_URL,
					accessChannelGroupTmp.getDispatchChannel(),URLConstants.DELALL);
			if (!"OK".equals(result)) {
				throw new MyException("删除Redis中信息出错,其请求URL参数为:" +URLConstants.T_ACCESS_CHANNEL_GROUP+"\t"+
								accessChannelGroupTmp.getDispatchChannel());
			}
		}

		for (ChannelToGroup channelToGroup : originChannelToGroupList) {
			String key = channelToGroup.getChannelGroupId() + "_"+ channelToGroup.getChannelSeqId();
			if (!finalChannelToGroupMap.keySet().contains(key)) {
				// 删除
				channelToGroupDao.deleteByPrimaryKey(channelToGroup.getSeqId());
			} 
		}
		for (AccessChannelGroup acg : data.getChannelGroupList()) {
			if (null == acg.getChannelGroupId()) {
				continue;
			}
			String key = acg.getChannelGroupId() + "_"+ data.getChannelSeqId();
			if (!originChannelToGroupMap.keySet().contains(key)) {
					// 新增
					ChannelToGroup info = new ChannelToGroup();
					info.setChannelGroupId(acg.getChannelGroupId());
					info.setChannelSeqId(data.getChannelSeqId());
					info.setWeight(0);
					channelToGroupDao.insert(info);
				}
			}
	}
}
