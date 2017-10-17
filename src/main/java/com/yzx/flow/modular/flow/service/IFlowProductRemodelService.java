package com.yzx.flow.modular.flow.service;

import java.util.List;
import java.util.Map;

import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.persistence.model.AreaCode;
import com.yzx.flow.common.persistence.model.FlowProductRemodel;

public interface IFlowProductRemodelService {
	/**
	 * 获取所有的地区信息
	 * @return
	 */
	List<AreaCode> selectAllArea();
	/**
	 * 主键查询
	 * @param productId
	 * @return
	 */
	FlowProductRemodel getProductById(Long productId);
	/**
	 * 条件查询判断
	 * @param data
	 * @return
	 */
	boolean solo(FlowProductRemodel flowProductRemodel);
	/**
	 * 条件查询
	 * @param data
	 * @return
	 */
	List<FlowProductRemodel> getByFlowProductRemodel(FlowProductRemodel flowProductRemodel);
	/**
	 * 条件查询
	 * @param ids
	 * @param zone
	 * @param operator
	 * @return
	 */
	List<FlowProductRemodel> getFlowProductRemodelList(String[] productCodes,String[] ids, String zone,String operator);
	/**
	 * 按条件搜索
	 * @param operatorCode
	 * @param text
	 * @return
	 */
	List<FlowProductRemodel> searchProduct(Map<String, Object> params);
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
	List<FlowProductRemodel> getByPartnerInfoType(Long partnerInfoId, Integer productType, Long[] productIds,String operatorCode, String text);
	/**
	 * 分页查询产品列表
	 * @param page
	 * @return
	 */
	Page<FlowProductRemodel> pageQuery(Page<FlowProductRemodel> page);
	/**
	 * 保存产品设置信息
	 * @param flowProductRemodel
	 */
	void saveAndUpdate(FlowProductRemodel flowProductRemodel);
	/**
	 * 删除产品设置
	 * @param productId
	 */
	void delete(Long productId);
}
