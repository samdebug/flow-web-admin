package com.yzx.flow.modular.channel.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.page.PageInfoBT;
import com.yzx.flow.common.persistence.model.AccessChannelInfo;
import com.yzx.flow.common.persistence.model.FlowPackageInfo;
import com.yzx.flow.common.persistence.model.SuppilerTradeDay;
import com.yzx.flow.modular.channel.service.IAccessChannelInfoHisService;
import com.yzx.flow.modular.system.dao.AccessChannelInfoHisDao;


/**
 * 通道修改记录日志
 * @author hc4gw02
 *
 */
@Service("accessChannelInfoHisService")
public class AccessChannelInfoHisServiceImpl implements IAccessChannelInfoHisService {
    private static final Logger LOG = LoggerFactory.getLogger(AccessChannelInfoHisServiceImpl.class);
    
    @Autowired
    private AccessChannelInfoHisDao accessChannelInfoHisDao;//记录修改日志
    
    /* (non-Javadoc)
	 * @see com.yzx.flow.modular.channel.service.impl.IAccessChannelInfoHisService#pageQuery(com.yzx.flow.common.page.Page)
	 */
    @Override
	public PageInfoBT<AccessChannelInfo> pageQuery(Page<AccessChannelInfo> page) {
    	LOG.debug("分页搜索,params={}",page.getParams());
    	List<AccessChannelInfo> list = accessChannelInfoHisDao.pageQuery(page);
    	PageInfoBT<AccessChannelInfo> resultPage = new PageInfoBT<AccessChannelInfo>(list, page.getTotal());
        return resultPage;
    }

    /* (non-Javadoc)
	 * @see com.yzx.flow.modular.channel.service.impl.IAccessChannelInfoHisService#insert(com.yzx.flow.common.persistence.model.AccessChannelInfo, java.lang.String)
	 */
    @Override
	public void insert(AccessChannelInfo data,String operateCode) {
    	AccessChannelInfo accessChannelInfo = new AccessChannelInfo();
    	accessChannelInfo = (AccessChannelInfo) JSONObject.toBean(JSONObject.fromObject(data), AccessChannelInfo.class);//不影响之前的值
    	List<Map<String, Object>> productInfoList = new ArrayList<Map<String,Object>>();
    	for (FlowPackageInfo fpi : data.getFlowPackageInfoList()) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("packageId", fpi.getPackageId());
			map.put("productId", fpi.getChannelProductId());
			map.put("price", fpi.getCostPrice());
			map.put("quarzTime", fpi.getQuarzTime());
			productInfoList.add(map);
		}
    	if (null!=productInfoList && !productInfoList.isEmpty()
    			&& JSONArray.fromObject(productInfoList).toString().length() > 1024) {
    		accessChannelInfo.setProductInfos(JSONArray.fromObject(productInfoList).toString().substring(0, 1024));
		}else {
			accessChannelInfo.setProductInfos(JSONArray.fromObject(productInfoList).toString());
		}
    	String remark = "";
    	if ("0".equals(operateCode)) {
    		remark = "页面修改通道属性";
		}else if ("1".equals(operateCode)) {
			remark = "列表修改通道状态";
			accessChannelInfo.setProductInfos("通道列表修改通道状态为" + (1==accessChannelInfo.getIsValid()?"有效":"无效")+ ",产品无变更");
		}else if ("2".equals(operateCode)) {
			remark = "删除通道信息";
			accessChannelInfo.setProductInfos("删除通道信息,产品无变更");
		}
    	accessChannelInfo.setCreator(data.getUpdator());
    	accessChannelInfo.setCreateTime(data.getUpdateTime());
    	accessChannelInfo.setRemark(remark);
    	accessChannelInfoHisDao.insert(accessChannelInfo);
    }

    /* (non-Javadoc)
	 * @see com.yzx.flow.modular.channel.service.impl.IAccessChannelInfoHisService#get(java.lang.Long)
	 */
    @Override
	public AccessChannelInfo get(Long channelSeqId) {
        return accessChannelInfoHisDao.selectByPrimaryKey(channelSeqId);
    }

    /* (non-Javadoc)
	 * @see com.yzx.flow.modular.channel.service.impl.IAccessChannelInfoHisService#update(com.yzx.flow.common.persistence.model.AccessChannelInfo)
	 */
    @Override
	public void update(AccessChannelInfo data) {
    	accessChannelInfoHisDao.updateByPrimaryKey(data);
    }
}
