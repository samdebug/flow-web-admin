package com.yzx.flow.modular.portal.service;

import java.util.Map;

public interface IProvinceChannelPoolService {

	/**
	 * 根据operator_code以及zone校验是否走前向流量包
	 * @param map	
	 */
	boolean selectProvinceChannelPoolInfo(Map<String, Object> map);

	boolean checkTime();

}