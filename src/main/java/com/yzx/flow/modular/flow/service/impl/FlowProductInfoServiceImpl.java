package com.yzx.flow.modular.flow.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.persistence.model.FlowPlusProduct;
import com.yzx.flow.common.persistence.model.FlowProductInfo;
import com.yzx.flow.modular.flow.service.IFlowPlusProductService;
import com.yzx.flow.modular.flow.service.IFlowProductInfoService;
import com.yzx.flow.modular.system.dao.FlowProductInfoDao;

/**
 * 
 * <b>Title：</b>FlowProductInfoService.java<br/>
 * <b>Description：</b> <br/>
 * <b>@author： </b>v5480<br/>
 * <b>@date：</b>2015-07-08 18:05:18<br/>
 * <b>Copyright (c) 2015 szwisdom Tech.</b>
 * 
 */
@Service("flowProductInfoService")
public class FlowProductInfoServiceImpl implements IFlowProductInfoService {
	@Autowired
	private FlowProductInfoDao flowProductInfoDao;


	@Autowired
	private IFlowPlusProductService flowPlusProductService;

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.flow.service.impl.IFlowProductInfoService#pageQuery(com.yzx.flow.common.page.Page)
	 */
	public Page<FlowProductInfo> pageQuery(Page<FlowProductInfo> page) {
		List<FlowProductInfo> list = flowProductInfoDao.pageQuery(page);
		page.setDatas(list);
		return page;
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.flow.service.impl.IFlowProductInfoService#insert(com.yzx.flow.common.persistence.model.FlowProductInfo)
	 */
	@Transactional
	public void insert(FlowProductInfo flowProductInfo) {
		flowProductInfoDao.insert(flowProductInfo);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.flow.service.impl.IFlowProductInfoService#get(java.lang.Long)
	 */
	public FlowProductInfo get(Long productId) {
		FlowProductInfo flowProductInfo = flowProductInfoDao.selectByPrimaryKey(productId);
		return flowProductInfo;
	}
	
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.flow.service.impl.IFlowProductInfoService#getFlowProductInfoByProductId(java.lang.Long)
	 */
	public FlowProductInfo getFlowProductInfoByProductId(Long productId) {
	    FlowProductInfo flowProductInfo = flowProductInfoDao.getFlowProductInfoByProductId(productId);
	    return flowProductInfo;
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.flow.service.impl.IFlowProductInfoService#saveAndUpdate(com.yzx.flow.common.persistence.model.FlowProductInfo)
	 */
	@Transactional
	public void saveAndUpdate(FlowProductInfo data) {
		if (null != data.getProductId()) {// 判断有没有传主键，如果传了为更新，否则为新增
			this.update(data);
		} else {
			this.insert(data);
		}
		if (data.getFlowPlusProduct() != null) {
			data.getFlowPlusProduct().setProductId(data.getProductId());
			flowPlusProductService.saveAndUpdate(data.getFlowPlusProduct());
		}
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.flow.service.impl.IFlowProductInfoService#update(com.yzx.flow.common.persistence.model.FlowProductInfo)
	 */
	public void update(FlowProductInfo flowProductInfo) {
		flowProductInfoDao.updateByPrimaryKeySelective(flowProductInfo);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.flow.service.impl.IFlowProductInfoService#delete(java.lang.Long)
	 */
	@Transactional
	public int delete(Long productId) {
		FlowPlusProduct flowPlusProduct = flowPlusProductService.getByProductId(productId);
		if(flowPlusProduct!=null){
			flowPlusProductService.delete(flowPlusProduct.getProductPlusId());
		}
		return flowProductInfoDao.deleteByPrimaryKey(productId);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.flow.service.impl.IFlowProductInfoService#getByPartnerInfoType(java.lang.Long, java.lang.Integer, java.lang.Long[], java.lang.String, java.lang.String)
	 */
	public List<FlowProductInfo> getByPartnerInfoType(Long partnerInfoId, Integer productType, Long[] productIds,String operatorCode,String text) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("productType", productType);
		map.put("partnerInfoId", partnerInfoId);
		map.put("productIds", productIds);
		map.put("operatorCode", operatorCode);
		map.put("text", text);
		return flowProductInfoDao.getByPartnerInfoType(map);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.flow.service.impl.IFlowProductInfoService#selectPlusByOrderDetailId(java.lang.Long)
	 */
	public List<FlowProductInfo> selectPlusByOrderDetailId(Long orderDetailId) {
		return flowProductInfoDao.selectPlusByOrderDetailId(orderDetailId);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.flow.service.impl.IFlowProductInfoService#getByProductType(java.lang.Integer)
	 */
	public List<FlowProductInfo> getByProductType(Integer productType) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("productType", productType);
		return flowProductInfoDao.getByProductType(map);
	}
	
	
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.flow.service.impl.IFlowProductInfoService#solo(com.yzx.flow.common.persistence.model.FlowProductInfo)
	 */
	public boolean solo(FlowProductInfo data){
		List<FlowProductInfo> flowProductInfoList= getByProductInfo(data);
		if (data.getProductId() != null) {
			if (flowProductInfoList != null && flowProductInfoList.size() > 1) {
				return false;
			}
			if (flowProductInfoList != null) {
				for (int i = 0; i < flowProductInfoList.size(); i++) {
					if (!flowProductInfoList.get(i).getProductId() .equals(data.getProductId())) {
						return false;
					}
				}
			}
		} else {
			if (flowProductInfoList != null && flowProductInfoList.size() > 0) {
				return false;
			}
		}
		return true;
	}

	
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.flow.service.impl.IFlowProductInfoService#getByProductInfo(com.yzx.flow.common.persistence.model.FlowProductInfo)
	 */
	public List<FlowProductInfo> getByProductInfo(FlowProductInfo flowProductInfo) {
		return flowProductInfoDao.getByProductInfo(flowProductInfo);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.flow.service.impl.IFlowProductInfoService#selectCountByPackageId(java.lang.String)
	 */
	public int selectCountByPackageId(String packageId) {
		return flowProductInfoDao.selectCountByPackageId(packageId);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.flow.service.impl.IFlowProductInfoService#searchProduct(java.util.Map)
	 */
	public List<FlowProductInfo> searchProduct(Map<String, Object> params) {
		return flowProductInfoDao.searchProduct(params);
	}
}