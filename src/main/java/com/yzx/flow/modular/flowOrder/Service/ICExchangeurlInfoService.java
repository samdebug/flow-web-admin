package com.yzx.flow.modular.flowOrder.Service;

import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.persistence.model.CExchangeurlInfo;

public interface ICExchangeurlInfoService {

	Page<CExchangeurlInfo> pageQuery(Page<CExchangeurlInfo> page);

	void insert(CExchangeurlInfo data);

	CExchangeurlInfo get(Long seqId);

	void saveAndUpdate(CExchangeurlInfo data);

	void update(CExchangeurlInfo data);

	int delete(Long seqId);

	CExchangeurlInfo getByCustomerId(Long customerId);

}