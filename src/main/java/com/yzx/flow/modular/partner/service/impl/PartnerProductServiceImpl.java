package com.yzx.flow.modular.partner.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.persistence.model.PartnerProduct;
import com.yzx.flow.modular.partner.service.IPartnerProductService;
import com.yzx.flow.modular.system.dao.PartnerProductDao;

/**
 * 
 * <b>Title：</b>PartnerProductService.java<br/>
 * <b>Description：</b> <br/>
 * <b>@author： </b>v5480<br/>
 * <b>@date：</b>2015-07-14 13:34:15<br/>
 * <b>Copyright (c) 2015 szwisdom Tech.</b>
 * 
 */
@Service("partnerProductService")
public class PartnerProductServiceImpl implements IPartnerProductService {
	
	@Autowired
	private PartnerProductDao partnerProductDao;

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.partner.service.IPartnerProductService#pageQuery(com.yzx.flow.common.page.Page)
	 */
	@Override
	public Page<PartnerProduct> pageQuery(Page<PartnerProduct> page) {
		List<PartnerProduct> list = partnerProductDao.pageQuery(page);
		page.setDatas(list);
		return page;
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.partner.service.IPartnerProductService#insert(com.yzx.flow.common.persistence.model.PartnerProduct)
	 */
	@Override
	public void insert(PartnerProduct data) {
		partnerProductDao.insert(data);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.partner.service.IPartnerProductService#insertBatch(java.util.List)
	 */
	@Override
	public void insertBatch(List<PartnerProduct> list) {
	    if (!list.isEmpty()) {
	        partnerProductDao.insertBatch(list);
	    }
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.partner.service.IPartnerProductService#get(java.lang.Long)
	 */
	@Override
	public PartnerProduct get(Long seqId) {
		return partnerProductDao.selectByPrimaryKey(seqId);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.partner.service.IPartnerProductService#saveAndUpdate(com.yzx.flow.common.persistence.model.PartnerProduct)
	 */
	@Override
	public void saveAndUpdate(PartnerProduct data) {
		if (null != data.getSeqId()) {// 判断有没有传主键，如果传了为更新，否则为新增
			this.update(data);
		} else {
			this.insert(data);
		}
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.partner.service.IPartnerProductService#update(com.yzx.flow.common.persistence.model.PartnerProduct)
	 */
	@Override
	public void update(PartnerProduct data) {
		partnerProductDao.updateByPrimaryKey(data);
	}
	
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.partner.service.IPartnerProductService#updateBatch(java.util.List)
	 */
	@Override
	public void updateBatch(List<PartnerProduct> list) {
	    if (!list.isEmpty()) {
	        partnerProductDao.updateBatch(list);
	    }
    }

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.partner.service.IPartnerProductService#delete(java.lang.Long)
	 */
	@Override
	public int delete(Long seqId) {
		return partnerProductDao.deleteByPrimaryKey(seqId);
	}
	
	

	@Override
	public int deleteByIds(long[] ids) {
		if ( ids == null )
			return 0;
		return partnerProductDao.deleteByIds(ids);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.partner.service.IPartnerProductService#getByPartnerId(java.lang.Long)
	 */
	@Override
	public List<PartnerProduct> getByPartnerId(Long partnerId) {
		return partnerProductDao.getByPartnerId(partnerId);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.partner.service.IPartnerProductService#deleteByProductId(java.lang.Long)
	 */
	@Override
	public int deleteByProductId(Long productId) {
		return partnerProductDao.deleteByProductId(productId);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.partner.service.IPartnerProductService#getCountByproductId(java.lang.Long)
	 */
	@Override
	public int getCountByproductId(Long productId) {
		return partnerProductDao.getCountByproductId(productId);
	}
	
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.partner.service.IPartnerProductService#deleteByPartnerId(java.lang.Long)
	 */
	@Override
	public int deleteByPartnerId(Long partnerId) {
        return partnerProductDao.deleteByPartnerId(partnerId);
    }
}