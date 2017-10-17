package com.yzx.flow.modular.flowOrder.Service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.persistence.model.AreaCode;
import com.yzx.flow.common.persistence.model.FlowPackageInfo;
import com.yzx.flow.common.persistence.model.FlowProductInfo;

public interface IFlowPackageInfoService {

	/**
	 * 获取所有的地区信息
	 * @return
	 */
	public abstract List<AreaCode> selectAllArea();
	
	Page<FlowPackageInfo> pageQuery(Page<FlowPackageInfo> page);

	List<FlowPackageInfo> selectByPackageId(String packageId);

	void insert(FlowPackageInfo data);

	FlowPackageInfo get(String packageId);

	void saveAndUpdate(FlowPackageInfo data);

	/**
	 * 添加基础流量包
	 * 
	 * @param data
	 * @param product
	 */
	void saveAndUpdate(FlowPackageInfo data, FlowProductInfo product);

	List<FlowPackageInfo> selectByZoneOperatorCode(String zone, String operatorCode, String packageid);

	List<FlowPackageInfo> selectByZoneOperatorCode(String zone, String operatorCode, String isCombo,
			Integer flowAmount);

	void update(FlowPackageInfo data);

	int delete(String packageId);

	List<FlowPackageInfo> selectInPackageId(String comboPackageStr);

	int selectCountInPackageId(String comboPackageStr);


}