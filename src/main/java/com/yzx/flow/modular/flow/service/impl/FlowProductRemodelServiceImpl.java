package com.yzx.flow.modular.flow.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.persistence.dao.AreaCodeMapper;
import com.yzx.flow.common.persistence.model.AreaCode;
import com.yzx.flow.common.persistence.model.FlowPackageInfo;
import com.yzx.flow.common.persistence.model.FlowProductInfo;
import com.yzx.flow.common.persistence.model.FlowProductRemodel;
import com.yzx.flow.modular.flow.service.IFlowProductRemodelService;
import com.yzx.flow.modular.system.dao.FlowPackageInfoDao;
import com.yzx.flow.modular.system.dao.FlowProductInfoDao;
import com.yzx.flow.modular.system.dao.FlowProductRemodelDao;

@Service("flowProductRemodelService")
@Transactional
public class FlowProductRemodelServiceImpl implements IFlowProductRemodelService {
	
	@Autowired
	private AreaCodeMapper areaCodeDao;
	
	@Autowired
	private FlowProductInfoDao flowProductInfoDao;
	
	@Autowired
	private FlowPackageInfoDao flowPackageInfoDao;
	
	@Autowired
	private FlowProductRemodelDao flowProductRemodelDao;
	
	/**
	 * 获取所有的地区信息
	 * @return
	 */
	@Override
	public List<AreaCode> selectAllArea() {
		 return this.areaCodeDao.getAreaCodeAll();
	}
	
	/**
	 * 主键查询
	 */
	@Override
	public FlowProductRemodel getProductById(Long productId) {
		return this.flowProductRemodelDao.selectByPrimaryKey(productId);
	}
	

	/**
	 * 条件查询判断
	 * @param data
	 * @return
	 */
	@Override
	public boolean solo(FlowProductRemodel flowProductRemodel) {
		List<FlowProductRemodel> flowProductRemodels= getByFlowProductRemodel(flowProductRemodel);
		if(null == flowProductRemodels || flowProductRemodels.size() < 1) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * 条件查询
	 * @param data
	 * @return
	 */
	@Override
	public List<FlowProductRemodel> getByFlowProductRemodel(FlowProductRemodel flowProductRemodel) {
		return this.flowProductRemodelDao.getByFlowProductRemodel(flowProductRemodel);
	}
	
	/**
	 * 条件查询
	 * @param productType
	 * @param productIds
	 * @param zone
	 * @param operator
	 * @return
	 */
	@Override
	public List<FlowProductRemodel> getFlowProductRemodelList(String[] productCodes,String[] productIds, String zone, String operator) {
		 Map<String, Object> map = new HashMap<String, Object>();
	        if (productIds != null && productIds.length == 0) {
	        	productIds = null;
	        }
	        if (productCodes != null && productCodes.length == 0) {
	        	productCodes = null;
	        }
	        map.put("productIds", productIds);
	        map.put("productCodes", productCodes);
	        map.put("zone", zone);
	        map.put("operatorCode", operator);
	        return this.flowProductRemodelDao.selectFlowProductRemodelList(map);

	}
	
	/**
	 * 按条件搜索
	 * @param operatorCode
	 * @param text
	 * @return
	 */
	@Override
	public List<FlowProductRemodel> searchProduct(Map<String, Object> params) {
		return this.flowProductRemodelDao.searchProduct(params);
	}
	
	/**
	 * 客户产品条件查询
	 * @param partnerInfoId
	 * @param productType
	 * @param productIds
	 * @param customerId
	 * @param operatorCode
	 * @param text
	 * @return
	 */
	@Override
	public List<FlowProductRemodel> getByPartnerInfoType(Long partnerInfoId, Integer productType, Long[] productIds,
			String operatorCode, String text) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("productType", productType);
		map.put("partnerInfoId", partnerInfoId);
		map.put("productIds", productIds);
		map.put("operatorCode", operatorCode);
		map.put("text", text);
		return this.flowProductRemodelDao.getByPartnerInfoType(map);
	}
	
	/**
	 * 分页查询产品列表
	 * @param page
	 * @return
	 */
	@Override
	public Page<FlowProductRemodel> pageQuery(Page<FlowProductRemodel> page) {
		List<FlowProductRemodel> list = this.flowProductRemodelDao.pageQuery(page);
		page.setDatas(list);
		return page;
	}
	
	/**
	 * 保存产品设置信息
	 * @param flowProductRemodel
	 */
	@Override
	public void saveAndUpdate(FlowProductRemodel flowProductRemodel) {
		FlowProductInfo flowProductInfo = new FlowProductInfo();
		FlowPackageInfo flowPackageInfo = new FlowPackageInfo();
		transformRemodelToOriginal(flowProductRemodel,flowPackageInfo,flowProductInfo);
		
		if(null == flowProductRemodel.getProductId()) {
			this.flowPackageInfoDao.insert(flowPackageInfo);
			this.flowProductInfoDao.insert(flowProductInfo);
			//主键统一
			flowProductRemodel.setProductId(flowProductInfo.getProductId());
			this.flowProductRemodelDao.insert(flowProductRemodel);
		} else {
			this.flowProductInfoDao.updateByPrimaryKeySelective(flowProductInfo);
			this.flowPackageInfoDao.updateByPrimaryKeySelective(flowPackageInfo);
			this.flowProductRemodelDao.updateSelective(flowProductRemodel);
		}
	}
	
	/**
	 * 删除产品设置
	 * @param productId
	 */
	@Override
	public void delete(Long productId) {
		FlowProductRemodel flowProductRemodel = this.flowProductRemodelDao.selectByPrimaryKey(productId);
		this.flowProductInfoDao.deleteByPrimaryKey(productId);
		this.flowPackageInfoDao.deleteByPrimaryKey(flowProductRemodel.getPackageId());
		this.flowProductRemodelDao.deleteByPrimaryKey(productId);
	}
	
	private void transformRemodelToOriginal(FlowProductRemodel flowProductRemodel, FlowPackageInfo flowPackageInfo,
			FlowProductInfo flowProductInfo) {
			flowPackageInfo.setPackageId(flowProductRemodel.getProductCode());
			flowPackageInfo.setPackageName(flowProductRemodel.getProductName());
			flowPackageInfo.setOperatorCode(flowProductRemodel.getOperatorCode());
			flowPackageInfo.setZone(flowProductRemodel.getZone());
			flowPackageInfo.setFlowAmount(flowProductRemodel.getFlowAmount());
			flowPackageInfo.setActivePeriod(flowProductRemodel.getActivePeriod());
			flowPackageInfo.setCostPrice(flowProductRemodel.getProductPrice());
			flowPackageInfo.setSalePrice(flowProductRemodel.getProductPrice());
			flowPackageInfo.setPackageType(flowProductRemodel.getProductType());
			flowPackageInfo.setIsCombo("0");
			flowPackageInfo.setIsValid(0);
			flowPackageInfo.setCreator(flowProductRemodel.getCreator());
			flowPackageInfo.setCreateTime(flowProductRemodel.getCreateTime());
			flowPackageInfo.setRemark(flowProductRemodel.getProductDesc());
			
			flowProductInfo.setProductId(flowProductRemodel.getProductId());
			flowProductInfo.setPackageId(flowProductRemodel.getProductCode());
			flowProductInfo.setProductCode(flowProductRemodel.getProductCode());
			flowProductInfo.setProductName(flowProductRemodel.getProductName());
			flowProductInfo.setZone(flowProductRemodel.getZone());
			//基础包
			flowProductInfo.setProductType(1);
			flowProductInfo.setProductPrice(flowProductRemodel.getProductPrice());
			flowProductInfo.setProductDesc(flowProductRemodel.getProductDesc());
			flowProductInfo.setCreator(flowProductRemodel.getCreator());
			flowProductInfo.setCreateTime(flowProductRemodel.getCreateTime());
	}
}
