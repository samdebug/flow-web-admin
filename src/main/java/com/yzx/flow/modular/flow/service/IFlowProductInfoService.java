package com.yzx.flow.modular.flow.service;

import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.persistence.model.FlowProductInfo;

public interface IFlowProductInfoService {

	Page<FlowProductInfo> pageQuery(Page<FlowProductInfo> page);

	void insert(FlowProductInfo flowProductInfo);

	FlowProductInfo get(Long productId);

	FlowProductInfo getFlowProductInfoByProductId(Long productId);

	void saveAndUpdate(FlowProductInfo data);

	void update(FlowProductInfo flowProductInfo);

	int delete(Long productId);

	List<FlowProductInfo> getByPartnerInfoType(Long partnerInfoId, Integer productType, Long[] productIds,
			String operatorCode, String text);

	List<FlowProductInfo> selectPlusByOrderDetailId(Long orderDetailId);

	List<FlowProductInfo> getByProductType(Integer productType);

	boolean solo(FlowProductInfo data);

	List<FlowProductInfo> getByProductInfo(FlowProductInfo flowProductInfo);

	int selectCountByPackageId(String packageId);

	/**
	 * 按条件搜索产品
	 * @param params
	 * @return
	 */
	List<FlowProductInfo> searchProduct(Map<String, Object> params);

}