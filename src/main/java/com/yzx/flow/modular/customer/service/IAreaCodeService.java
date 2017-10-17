package com.yzx.flow.modular.customer.service;

import java.util.List;

import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.persistence.model.AreaCode;

public interface IAreaCodeService {

	Page<AreaCode> pageQuery(Page<AreaCode> page);

	void insert(AreaCode data);

	AreaCode get(String areaCode);

	void saveAndUpdate(AreaCode data);

	void update(AreaCode data);

	int delete(String areaCode);

	List<AreaCode> selectAll(String mobileOperator, Long partnerId, Long customerId, String inputStartTime,
			String inputEndTime);

}