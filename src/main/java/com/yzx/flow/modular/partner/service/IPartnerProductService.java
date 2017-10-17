package com.yzx.flow.modular.partner.service;

import java.util.List;

import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.persistence.model.PartnerProduct;

public interface IPartnerProductService {

	Page<PartnerProduct> pageQuery(Page<PartnerProduct> page);

	void insert(PartnerProduct data);

	void insertBatch(List<PartnerProduct> list);

	PartnerProduct get(Long seqId);

	/**
	 * @param data
	 */
	void saveAndUpdate(PartnerProduct data);

	void update(PartnerProduct data);

	void updateBatch(List<PartnerProduct> list);

	int delete(Long seqId);
	
	/**
	 * id批量删除
	 * @param ids
	 * @return
	 */
	int deleteByIds(long[] ids);

	List<PartnerProduct> getByPartnerId(Long partnerId);

	int deleteByProductId(Long productId);

	int getCountByproductId(Long productId);

	int deleteByPartnerId(Long partnerId);

}