package com.yzx.flow.modular.partner.service;

import java.util.List;

import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.persistence.model.OrderDealRecordWithBLOBs;
import com.yzx.flow.common.persistence.model.PartnerInfo;
import com.yzx.flow.core.shiro.ShiroUser;

public interface IPartnerService {

	List<PartnerInfo> pageQuery(Page<PartnerInfo> page);

	void insert(PartnerInfo data);

	/**
	 * @param partnerId
	 * @return
	 */
	PartnerInfo getAll(Long partnerId);

	/**
	 * @param partnerId
	 * @return
	 */
	PartnerInfo get(Long partnerId);

	List<PartnerInfo> finaByProductId(Long productId);

	void createStaffAndSave(PartnerInfo data);

	void updatePartnerInfo(PartnerInfo data);

	int delete(Long partnerId);

	List<PartnerInfo> queryAll(Long partnerId);

	//	@DataSource(DataSourceType.READ)
	PartnerInfo getByAccount(String loginName);

	List<PartnerInfo> findByAccount(String loginName);

	boolean resetPassword(PartnerInfo partnerInfo);
	
	void update(PartnerInfo data);

	//	@DataSource(DataSourceType.READ)
	List<PartnerInfo> selectPartnerInfoByName(String partnerName, Long partnerId);

	/**
	 * 构造【订单处理记录】对象 合作伙伴产品协议
	 */
	OrderDealRecordWithBLOBs createOrderDealRecordWithBLOBs(PartnerInfo partnerInfo, ShiroUser shiroUser);
	
	void saveAndUpdate(PartnerInfo data, ShiroUser staff);

}