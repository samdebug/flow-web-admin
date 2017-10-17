package com.yzx.flow.modular.flowOrder.Service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yzx.flow.common.exception.MyException;
import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.persistence.dao.AreaCodeMapper;
import com.yzx.flow.common.persistence.model.AreaCode;
import com.yzx.flow.common.persistence.model.FlowPackageInfo;
import com.yzx.flow.common.persistence.model.FlowPackageInfoProduct;
import com.yzx.flow.common.persistence.model.FlowProductInfo;
import com.yzx.flow.common.persistence.model.FlowProductRule;
import com.yzx.flow.common.util.RedisHttpUtil;
import com.yzx.flow.common.util.URLConstants;
import com.yzx.flow.modular.flow.service.IFlowProductInfoService;
import com.yzx.flow.modular.flowOrder.Service.IFlowPackageInfoProductService;
import com.yzx.flow.modular.flowOrder.Service.IFlowPackageInfoService;
import com.yzx.flow.modular.system.dao.FlowPackageInfoDao;
import com.yzx.flow.modular.system.dao.FlowProductRuleDao;

/**
 * 
 * <b>Title：</b>FlowPackageInfoService.java<br/>
 * <b>Description：</b> <br/>
 * <b>@author： </b>v5480<br/>
 * <b>@date：</b>2015-07-14 13:34:15<br/>
 * <b>Copyright (c) 2015 szwisdom Tech.</b>
 * 
 */
@Service("flowPackageInfoService")
public class FlowPackageInfoService implements IFlowPackageInfoService {
	@Autowired
	private FlowPackageInfoDao flowPackageInfoDao;

	@Autowired
	private IFlowPackageInfoProductService flowPackageInfoProductService;

	@Autowired
	private IFlowProductInfoService flowProductInfoService;
	@Autowired
	private FlowProductRuleDao flowProductRuleDao;
	@Autowired
	private AreaCodeMapper areaCodeDao;

	private static final Logger log = LoggerFactory
			.getLogger(FlowPackageInfoService.class);

	
	 @Override
	public List<AreaCode> selectAllArea() {
		 return areaCodeDao.getAreaCodeAll();
	}
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.flowOrder.Service.impl.IFlowPackageInfoService#pageQuery(com.yzx.flow.common.page.Page)
	 */
	public Page<FlowPackageInfo> pageQuery(Page<FlowPackageInfo> page) {
		List<FlowPackageInfo> list = flowPackageInfoDao.pageQuery(page);
		page.setDatas(list);
		return page;
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.flowOrder.Service.impl.IFlowPackageInfoService#selectByPackageId(java.lang.String)
	 */
	public List<FlowPackageInfo> selectByPackageId(String packageId) {
		return flowPackageInfoDao.selectByPackageId(packageId);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.flowOrder.Service.impl.IFlowPackageInfoService#insert(com.yzx.flow.common.persistence.model.FlowPackageInfo)
	 */
	public void insert(FlowPackageInfo data) {
		flowPackageInfoDao.insert(data);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.flowOrder.Service.impl.IFlowPackageInfoService#get(java.lang.String)
	 */
	public FlowPackageInfo get(String packageId) {
		FlowPackageInfo flowPackageInfo = flowPackageInfoDao
				.selectByPrimaryKey(packageId);
		if (null != flowPackageInfo) {

			FlowProductRule record = new FlowProductRule();
			record.setPackageId(flowPackageInfo.getPackageId());
			flowPackageInfo.setFlowProductRules(flowProductRuleDao
					.query(record));

			if (FlowPackageInfo.COMBO_PACKAGE.equals(flowPackageInfo
					.getIsCombo())
					&& StringUtils.isNotBlank(flowPackageInfo
							.getComboPackageStr())) {
				Map<String, Integer> map = new HashMap<String, Integer>();
				String[] comboPackageStrs = flowPackageInfo
						.getComboPackageStr().split(",");
				for (int i = 0; i < comboPackageStrs.length; i++) {
					if (map.containsKey(comboPackageStrs[i])) {
						int count = map.get(comboPackageStrs[i]);
						map.put(comboPackageStrs[i], ++count);
					} else {
						map.put(comboPackageStrs[i], 0);
					}
				}
				List<FlowPackageInfo> flowPackageInfoList = selectInPackageId(flowPackageInfo
						.getComboPackageStr());
				Map<String, Boolean> cloneFlag = new HashMap<String, Boolean>();
				for (int i = 0; i < flowPackageInfoList.size(); i++) {
					FlowPackageInfo flowPackage = flowPackageInfoList.get(i);
					if (cloneFlag.containsKey(flowPackage.getPackageId())) {
						continue;
					}
					cloneFlag.put(flowPackage.getPackageId(), true);
					int packCount = map.get(flowPackage.getPackageId());
					for (int j = 0; j < packCount; j++) {
						try {
							FlowPackageInfo flowPackageClone = flowPackage
									.clone();
							flowPackageInfoList.add(flowPackageClone);
						} catch (CloneNotSupportedException e) {
							log.error(e.getMessage(), e);
						}
					}
				}
				flowPackageInfo.setFlowPackageInfos(flowPackageInfoList);
			}
		}
		return flowPackageInfo;
	}

	private void insertFlowProductRules(FlowPackageInfo data) {
		List<FlowProductRule> flowProductRules = data.getFlowProductRules();
		List<FlowProductRule> result = new ArrayList<FlowProductRule>();
		flowProductRuleDao.deleteByPackageId(data.getPackageId());
		Map<String, Integer> ratios = new HashMap<String, Integer>();
		if (null != flowProductRules && !flowProductRules.isEmpty()) {
			for (FlowProductRule flowProductRule : flowProductRules) {
				if (null == flowProductRule)
					continue;
				if (null == flowProductRule.getExchangeRatio()
						|| flowProductRule.getExchangeRatio() < 1
						|| flowProductRule.getExchangeRatio() > 100) {
					throw new MyException("兑换比例必须为数字且在1-100间");
				}
				FlowPackageInfo subflowPackageInfo = flowPackageInfoDao
						.selectByPrimaryKey(flowProductRule.getSubPackageId());
				if (null == subflowPackageInfo) {
					throw new MyException("关联的基础包["
							+ flowProductRule.getSubPackageId() + "]不存在");
				}
				String subOperatorCode = subflowPackageInfo.getOperatorCode();
				if (ratios.containsKey(subOperatorCode)) {
					ratios.put(
							subOperatorCode,
							flowProductRule.getExchangeRatio()
									+ ratios.get(subOperatorCode));
				} else {
					ratios.put(subOperatorCode,
							flowProductRule.getExchangeRatio());
				}
				flowProductRule.setPackageId(data.getPackageId());
				result.add(flowProductRule);
			}
			for (String key : ratios.keySet()) {
				if (ratios.get(key) != 100) {
					throw new MyException("同一运营商[" + key + "]兑换比例总和不为100");
				}
			}
			flowProductRuleDao.insertBatch(result);

		}
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.flowOrder.Service.impl.IFlowPackageInfoService#saveAndUpdate(com.yzx.flow.common.persistence.model.FlowPackageInfo)
	 */
	@Transactional
	public void saveAndUpdate(FlowPackageInfo data) {
		if (org.apache.commons.lang.StringUtils.isNotBlank(data.getUpdId())) {// 判断有没有传主键，如果传了为更新，否则为新增
			this.update(data);
		} else {
			FlowPackageInfo bean = flowPackageInfoDao
					.selectByPrimaryKey(data.getPackageId());
			if (null != bean) {
				throw new MyException("流量包ID=[" + data.getPackageId() + "]已存在。");
			}
			this.insert(data);
		}
		insertFlowProductRules(data);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.flowOrder.Service.impl.IFlowPackageInfoService#saveAndUpdate(com.yzx.flow.common.persistence.model.FlowPackageInfo, com.yzx.flow.common.persistence.model.FlowProductInfo)
	 */
	@Transactional
	public void saveAndUpdate(FlowPackageInfo data, FlowProductInfo product) {
		FlowPackageInfo flowPackageInfo = get(data.getPackageId());
		if (flowPackageInfo != null) {
			this.update(data);
		} else {
			this.insert(data);
			flowProductInfoService.saveAndUpdate(data.getFlowProductInfo());
			FlowPackageInfoProduct pp = new FlowPackageInfoProduct();
			pp.setPackageId(data.getPackageId());
			pp.setProductId(data.getFlowProductInfo().getProductId());
			flowPackageInfoProductService.saveAndUpdate(pp);
		}
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.flowOrder.Service.impl.IFlowPackageInfoService#selectByZoneOperatorCode(java.lang.String, java.lang.String, java.lang.String)
	 */
	public List<FlowPackageInfo> selectByZoneOperatorCode(String zone,
			String operatorCode, String packageid) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("zone", zone);
		map.put("operatorCode", operatorCode);
		map.put("packageId", packageid);
		map.put("isComboFlag", "true");
		map.put("isCombo", "0");
		return flowPackageInfoDao.selectByZoneOperatorCode(map);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.flowOrder.Service.impl.IFlowPackageInfoService#selectByZoneOperatorCode(java.lang.String, java.lang.String, java.lang.String, java.lang.Integer)
	 */
	public List<FlowPackageInfo> selectByZoneOperatorCode(String zone,
			String operatorCode, String isCombo, Integer flowAmount) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("zone", zone);
		map.put("operatorCode", operatorCode);
		if (StringUtils.isNotBlank(isCombo)) {
			map.put("isComboFlag", "true");
		}
		map.put("isCombo", isCombo);

		map.put("flowAmount", flowAmount);
		return flowPackageInfoDao.selectByZoneOperatorCode(map);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.flowOrder.Service.impl.IFlowPackageInfoService#update(com.yzx.flow.common.persistence.model.FlowPackageInfo)
	 */
	public void update(FlowPackageInfo data) {
		flowPackageInfoDao.updateByPrimaryKeySelective(data);
		//后续加入redis服务再开启
		String result = RedisHttpUtil.sendGet(URLConstants.DEL_REDIS_URL,
				URLConstants.T_FLOW_PACKAGE_INFO, data.getPackageId());
		if (!"OK".equals(result)) {
			throw new MyException("删除Redis中信息出错,其请求URL参数为:" + URLConstants.T_FLOW_PACKAGE_INFO+"\t"+data.getPackageId());
		}
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.flowOrder.Service.impl.IFlowPackageInfoService#delete(java.lang.String)
	 */
	@Transactional(rollbackFor = Exception.class)
	public int delete(String packageId) {
		int i = flowPackageInfoDao.deleteByPrimaryKey(packageId);
		//后续加入redis服务再开启
		String result = RedisHttpUtil.sendGet(URLConstants.DEL_REDIS_URL,
				URLConstants.T_FLOW_PACKAGE_INFO, packageId+"");
		if (!"OK".equals(result)) {
			throw new MyException("删除Redis中信息出错,其请求URL参数为:" + URLConstants.T_FLOW_PACKAGE_INFO+"\t"+packageId);
		}
		return i;
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.flowOrder.Service.impl.IFlowPackageInfoService#selectInPackageId(java.lang.String)
	 */
	public List<FlowPackageInfo> selectInPackageId(String comboPackageStr) {
		if (StringUtils.isEmpty(comboPackageStr)) {
			return null;
		}
		List<String> list = Arrays.asList(comboPackageStr.split(","));
		return flowPackageInfoDao.selectInPackageId(list);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.flowOrder.Service.impl.IFlowPackageInfoService#selectCountInPackageId(java.lang.String)
	 */
	public int selectCountInPackageId(String comboPackageStr) {
		if (StringUtils.isEmpty(comboPackageStr)) {
			return 0;
		}
		List<String> list = Arrays.asList(comboPackageStr.split(","));
		return flowPackageInfoDao.selectCountInPackageId(list);
	}
}