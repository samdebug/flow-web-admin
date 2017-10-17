package com.yzx.flow.modular.flowOrder.Service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.persistence.model.CExchangeurlInfo;
import com.yzx.flow.modular.flowOrder.Service.ICExchangeurlInfoService;
import com.yzx.flow.modular.system.dao.CExchangeurlInfoDao;

/**
 * 
 * <b>Title：</b>CExchangeurlInfoService.java<br/>
 * <b>Description：</b> <br/>
 * <b>@author： </b>v5480<br/>
 * <b>@date：</b>2015-07-28 11:37:23<br/>
 * <b>Copyright (c) 2015 szwisdom Tech.</b>
 * 
 */
@Service("cExchangeurlInfoService")
public class CExchangeurlInfoService implements ICExchangeurlInfoService {
	@Autowired
	private CExchangeurlInfoDao cExchangeurlInfoDao;

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.flowOrder.Service.impl.ICExchangeurlInfoService#pageQuery(com.yzx.flow.common.page.Page)
	 */
	public Page<CExchangeurlInfo> pageQuery(Page<CExchangeurlInfo> page) {
		List<CExchangeurlInfo> list = cExchangeurlInfoDao.pageQuery(page);
		page.setDatas(list);
		return page;
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.flowOrder.Service.impl.ICExchangeurlInfoService#insert(com.yzx.flow.common.persistence.model.CExchangeurlInfo)
	 */
	public void insert(CExchangeurlInfo data) {
		cExchangeurlInfoDao.insert(data);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.flowOrder.Service.impl.ICExchangeurlInfoService#get(java.lang.Long)
	 */
	public CExchangeurlInfo get(Long seqId) {
		return cExchangeurlInfoDao.selectByPrimaryKey(seqId);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.flowOrder.Service.impl.ICExchangeurlInfoService#saveAndUpdate(com.yzx.flow.common.persistence.model.CExchangeurlInfo)
	 */
	public void saveAndUpdate(CExchangeurlInfo data) {
		if (null != data.getSeqId()) {// 判断有没有传主键，如果传了为更新，否则为新增
			this.update(data);
		} else {
			this.insert(data);
		}
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.flowOrder.Service.impl.ICExchangeurlInfoService#update(com.yzx.flow.common.persistence.model.CExchangeurlInfo)
	 */
	public void update(CExchangeurlInfo data) {
		cExchangeurlInfoDao.updateByPrimaryKey(data);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.flowOrder.Service.impl.ICExchangeurlInfoService#delete(java.lang.Long)
	 */
	public int delete(Long seqId) {
		return cExchangeurlInfoDao.deleteByPrimaryKey(seqId);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.flowOrder.Service.impl.ICExchangeurlInfoService#getByCustomerId(java.lang.Long)
	 */
	public CExchangeurlInfo getByCustomerId(Long customerId) {
		return cExchangeurlInfoDao.getByCustomerId(customerId);
	}
}