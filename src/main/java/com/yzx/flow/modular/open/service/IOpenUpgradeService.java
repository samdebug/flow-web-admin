package com.yzx.flow.modular.open.service;

import com.yzx.flow.common.persistence.model.SystemVersion;

public interface IOpenUpgradeService {

	Integer UpgrdeByIdAndStatus(Integer id, Integer status);

	SystemVersion selectById(Integer id);
	
	public Integer checkSuccess(Integer id);
	
}
