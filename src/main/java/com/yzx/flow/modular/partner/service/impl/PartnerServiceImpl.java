package com.yzx.flow.modular.partner.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yzx.flow.common.exception.BizExceptionEnum;
import com.yzx.flow.common.exception.BussinessException;
import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.persistence.model.OrderDealRecordWithBLOBs;
import com.yzx.flow.common.persistence.model.PartnerInfo;
import com.yzx.flow.common.persistence.model.PartnerProduct;
import com.yzx.flow.common.util.Constant;
import com.yzx.flow.common.util.RedisHttpUtil;
import com.yzx.flow.common.util.URLConstants;
import com.yzx.flow.core.aop.dbrouting.DataSource;
import com.yzx.flow.core.aop.dbrouting.DataSourceType;
import com.yzx.flow.core.shiro.ShiroKit;
import com.yzx.flow.core.shiro.ShiroUser;
import com.yzx.flow.modular.partner.service.IPartnerProductService;
import com.yzx.flow.modular.partner.service.IPartnerService;
import com.yzx.flow.modular.system.dao.OrderDealRecordDao;
import com.yzx.flow.modular.system.dao.PartnerInfoDao;
import com.yzx.flow.modular.system.dao.PartnerRechargeRecordDao;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
 * 
 * <b>Title：</b>PartnerInfoService.java<br/>
 * <b>Description：</b> <br/>
 * <b>@author： </b>v5480<br/>
 * <b>@date：</b>2015-07-08 18:05:18<br/>
 * <b>Copyright (c) 2015 szwisdom Tech.</b>
 * 
 */
@Service("partnerInfoService")
public class PartnerServiceImpl implements IPartnerService {
	
	private static final Logger LOG = LoggerFactory .getLogger(PartnerServiceImpl.class);
	
	
	@Autowired
	private PartnerInfoDao partnerInfoDao;

	@Autowired
	private OrderDealRecordDao orderDealRecordDao;

	@Autowired
	private PartnerRechargeRecordDao partnerRechargeRecordDao;

	@Autowired
	private IPartnerProductService partnerProductService;



	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.partner.service.impl.IPartnerService#pageQuery(com.yzx.flow.common.page.Page)
	 */
	@Override
	public List<PartnerInfo> pageQuery(Page<PartnerInfo> page) {
		return partnerInfoDao.pageQuery(page);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.partner.service.impl.IPartnerService#insert(com.yzx.flow.common.persistence.model.PartnerInfo)
	 */
	@Override
	public void insert(PartnerInfo data) {
		partnerInfoDao.insert(data);
	}
	
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.partner.service.impl.IPartnerService#getAll(java.lang.Long)
	 */
	@Override
	public PartnerInfo getAll(Long partnerId) {
		if (partnerId == null) {
			return null;
		}
		PartnerInfo partnerInfo = partnerInfoDao.selectByPrimaryKey(partnerId);
		if (partnerInfo == null) {
			return null;
		}
		List<PartnerProduct> PartnerProductList = partnerProductService.getByPartnerId(partnerId);
		partnerInfo.setPartnerProductList(PartnerProductList);
		return partnerInfo;
	}
	
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.partner.service.impl.IPartnerService#get(java.lang.Long)
	 */
	@Override
	public PartnerInfo get(Long partnerId) {
		if (partnerId == null) {
			return null;
		}
		PartnerInfo partnerInfo = partnerInfoDao
				.selectByPrimaryKey(partnerId);
		return partnerInfo;
	}
	
	
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.partner.service.impl.IPartnerService#finaByProductId(java.lang.Long)
	 */
	@Override
	public List<PartnerInfo> finaByProductId(Long productId) {
		return partnerInfoDao.finaByProductId(productId);
	}
	
	
	
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.partner.service.impl.IPartnerService#createStaffAndSave(com.yzx.flow.common.persistence.model.PartnerInfo)
	 */
	@Override
	public void createStaffAndSave(PartnerInfo data) {
		// TODO save to staff
		insert(data);
	}
	
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.partner.service.impl.IPartnerService#updatePartnerInfo(com.yzx.flow.common.persistence.model.PartnerInfo)
	 */
	@Override
	@Transactional
	public void updatePartnerInfo(PartnerInfo data) {
		
		PartnerInfo partner = null;
		
		if ( data.getPartnerId() == null || data.getPartnerId().compareTo(0L) <= 0 
				|| (partner = get(data.getPartnerId()) ) == null )
			throw new BussinessException(BizExceptionEnum.PARTNER_NOT_EXIST);
		
		partner.setRealName(data.getRealName());
		partner.setMobile(data.getMobile());
		partner.setEmail(data.getEmail());
//		partner.setCoopGroupId(data.getCoopGroupId());
		partner.setOrderBillingType(data.getOrderBillingType());
		partner.setSettlementPattern(data.getSettlementPattern());
		partner.setSettlementDiscount(data.getSettlementDiscount());
		partner.setSettlementDiscountRatio(data.getSettlementDiscountRatio());
		partner.setIsDeleted(PartnerInfo.NOT_DELETE);
		
		ShiroUser current = ShiroKit.getUser();
		partner.setUpdator(current.getAccount());
		partner.setUpdateTime(new Date());
		
		// TODO save to staff
		// ...
		this.update(partner);
	}
	
	@Transactional
	public void update(PartnerInfo data) {
		partnerInfoDao.updateByPrimaryKey(data);
		String result = RedisHttpUtil.sendGet(URLConstants.DEL_REDIS_URL,
				URLConstants.T_PARTNER_INFO, data.getPartnerId()+"");
		if (!"OK".equals(result)) {
			throw new BussinessException(BizExceptionEnum.CUSTOMER_FORMAT_ERROR, 
					"删除Redis中信息出错,其请求URL参数为:" + URLConstants.T_PARTNER_INFO+"\t"+data.getPartnerId());
		}
	}
	
	
	
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.partner.service.impl.IPartnerService#delete(java.lang.Long)
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public int delete(Long partnerId) {
		// 物理删除
		// 删除合作伙伴产品归属关联表
		partnerProductService.deleteByPartnerId(partnerId);
		// 删除合作伙伴充值记录
		partnerRechargeRecordDao.deleteByPartnerId(partnerId);
		// 删除合作伙伴
		PartnerInfo partnerInfo = partnerInfoDao
				.selectByPrimaryKey(partnerId);
		// TODO  delete staff
//		AuthProxy proxy = AuthProxy.getProxy(PortalClientConfig.getInstance()
//				.getPortalAuthUrl());
//		try {
//			proxy.deleteStaffByLoginName(partnerInfo.getLoginName());
//		} catch (Exception e) {
//			LOG.error(e.getMessage(), e);
//			throw new MyException("删除客户出错，请联系管理员。");
//		}
		int i = partnerInfoDao.deleteByPrimaryKey(partnerId);
		String result = RedisHttpUtil.sendGet(URLConstants.DEL_REDIS_URL,
				URLConstants.T_PARTNER_INFO, partnerId+"");
		if (!"OK".equals(result)) {
			throw new BussinessException(BizExceptionEnum.CUSTOMER_FORMAT_ERROR, 
					"删除Redis中信息出错,其请求URL参数为:" + URLConstants.T_PARTNER_INFO+"\t"+partnerId);
		}
		return i;
	}
	
	
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.partner.service.impl.IPartnerService#queryAll(java.lang.Long)
	 */
	@Override
	public List<PartnerInfo> queryAll(Long partnerId) {
		return partnerInfoDao.queryAll(partnerId);
	}
	


	
	@DataSource(DataSourceType.READ)
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.partner.service.impl.IPartnerService#getByAccount(java.lang.String)
	 */
	@Override
	@Transactional(readOnly=true)
	public PartnerInfo getByAccount(String loginName) {
		return partnerInfoDao.getByAccount(loginName);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.partner.service.impl.IPartnerService#findByAccount(java.lang.String)
	 */
	@Override
	public List<PartnerInfo> findByAccount(String loginName) {
		return partnerInfoDao.findByAccount(loginName);
	}
	
	
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.partner.service.impl.IPartnerService#resetPassword(com.yzx.flow.common.persistence.model.PartnerInfo)
	 */
	@Override
	public boolean resetPassword(PartnerInfo partnerInfo) {
//		if (StringUtils.isBlank(partnerInfo.getMobile())) {
//			return false;
//		}
//		String authUrl = PortalClientConfig.getInstance().getPortalAuthUrl();
//		if (StringTools.isEmptyString(authUrl)) {
//			throw new MyException("获取不到通信URL");
//		}
//		AuthProxy proxy = AuthProxy.getProxy(authUrl);
//		try {
//			String passwd = SystemConfig.getInstance()
//					.getChannelPartnerPasswd();
//			proxy.resetPassword(partnerInfo.getLoginName(), passwd);
//			String msg = "您的密码已重置为[" + passwd + "],请尽快登陆修改密码";
//			BCloudSMSClient sms = new BCloudSMSClient();
//			String smsUrl = SystemConfig.getInstance().getSmsUrl();
//			if (StringUtils.isNotBlank(smsUrl)) {
//				sms.setGwUrl(smsUrl);
//			}
//			String smsUser = SystemConfig.getInstance().getSmsUser();
//			if (StringUtils.isNotBlank(smsUser)) {
//				sms.setGwUser(smsUser);
//			}
//			String smsPwd = SystemConfig.getInstance().getSmsPwd();
//			if (StringUtils.isNotBlank(smsPwd)) {
//				sms.setGwPwd(smsPwd);
//			}
//			sms.sendSMS(partnerInfo.getMobile(), msg);
//			return true;
//		} catch (Exception e) {
//			LOG.debug(e.getMessage(), e);
//			throw new MyException("重置密码错误,msg=" + e.getMessage());
//		}
		return false;
	}

	
	@DataSource(DataSourceType.READ)
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.partner.service.impl.IPartnerService#selectPartnerInfoByName(java.lang.String, java.lang.Long)
	 */
	@Override
	@Transactional(readOnly=true)
	public List<PartnerInfo> selectPartnerInfoByName(String partnerName, Long partnerId) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("partnerName", partnerName);
		map.put("partnerId", partnerId);
		return partnerInfoDao.selectPartnerInfoByName(map);
	}
	
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.partner.service.impl.IPartnerService#createOrderDealRecordWithBLOBs(com.yzx.flow.common.persistence.model.PartnerInfo, com.yzx.flow.core.shiro.ShiroUser)
	 */
	@Override
	public OrderDealRecordWithBLOBs createOrderDealRecordWithBLOBs(PartnerInfo partnerInfo, ShiroUser shiroUser) {
		// TODO
		OrderDealRecordWithBLOBs data = new OrderDealRecordWithBLOBs();
		data.setType(Constant.RECORD_TYPE_PARTNER);
		data.setCreator(shiroUser.getAccount());
		data.setInputTime(new Date());
		data.setRemark("");
		data.setSourceId(String.valueOf(partnerInfo.getPartnerId()));
		JsonConfig jsonConfig = new JsonConfig();
//		jsonConfig.registerJsonValueProcessor(Date.class, new JsonDateValueProcessor());
		PartnerInfo info = getAll(partnerInfo.getPartnerId());
		String endRecord = JSONObject.fromObject(info, jsonConfig).toString();
		data.setEndRecord(endRecord);
		return data;
	}
	
	
	
	@Transactional
	public void saveAndUpdate(PartnerInfo data, ShiroUser staff) {
//		String authUrl = PortalClientConfig.getInstance().getPortalAuthUrl();
//		if (StringTools.isEmptyString(authUrl)) {
//			throw new MyException("获取不到通信URL");
//		}
//		AuthProxy proxy = AuthProxy.getProxy(authUrl);
//		Staff staff2 = new Staff();
		// 订单处理记录-记录前数据
		String startRecord = "";
		if (null != data.getPartnerId()) {// update
			// 订单处理记录-记录前数据
			JsonConfig jsonConfig = new JsonConfig();
//			jsonConfig.registerJsonValueProcessor(Date.class, new JsonDateValueProcessor());
			startRecord = JSONObject.fromObject(getAll(data.getPartnerId()), jsonConfig).toString();
			// TODO 
//			staff2 = proxy.findStaffByLoginName(data.getLoginName());
//			if (null == staff2) {
//				throw new MyException("通过登录帐号[" + data.getLoginName()
//						+ "]查找不到用户信息。");
//			}
//			staff2.setRealName(data.getRealName());
//			staff2.setEmail(data.getEmail());
//			staff2.setMobile(data.getMobile());
//			try {
//				proxy.updateStaff(staff2);
//			} catch (Exception e) {
//				LOG.error(e.getMessage(), e);
//				throw new MyException("更新帐号出错,msg=" + e.getMessage());
//			}
			this.update(data);
			// 添加扩展表
//			data.getPartnerInfoExt().setPartnerId(data.getPartnerId());
//			partnerInfoService.saveAndUpdate(data.getPartnerInfoExt());

			// 合作伙伴产品归属关联表(新增/修改)
			List<PartnerProduct> insertBatchList = new ArrayList<PartnerProduct>();
			List<PartnerProduct> updateBatchList = new ArrayList<PartnerProduct>();
			for (int i = 0; i < data.getPartnerProductList().size(); i++) {
				PartnerProduct partnerproduct = data.getPartnerProductList().get(i);
				if (partnerproduct == null) {
					continue;
				}
				if (partnerproduct.getSeqId() == null
						&& partnerproduct.getProductId() != null) { // insert
					partnerproduct.setCreator(staff.getAccount());
					partnerproduct.setCreateTime(new Date());
					partnerproduct.setPartnerId(data.getPartnerId());
					insertBatchList.add(partnerproduct);
				} else if (partnerproduct.getSeqId() != null) { // update
					PartnerProduct pp = partnerProductService.get(partnerproduct.getSeqId());
					pp.setSettlementAmount(partnerproduct.getSettlementAmount());
					pp.setRemark(partnerproduct.getRemark());
					pp.setUpdator(staff.getAccount());
					pp.setUpdateTime(new Date());
					updateBatchList.add(pp);
				}
			}
			// 批量新增/修改
			partnerProductService.insertBatch(insertBatchList);
			partnerProductService.updateBatch(updateBatchList);
		} else { // insert
//			staff2 = new Staff();
			String roleId = "";
			Long departmentId = null;
			String passwd = "";
//			if (PartnerInfo.PARTNER_TYPE_FLOW == data.getPartnerType()) {
//				roleId = SystemConfig.getInstance().getFlowPartnerRoleId();
//				departmentId = SystemConfig.getInstance()
//						.getFlowPartnerDepartmentId();
//				passwd = SystemConfig.getInstance().getFlowPartnerPasswd();
//			} else if (PartnerInfo.PARTNER_TYPE_CHANNEL == data
//					.getPartnerType()) {
//				roleId = SystemConfig.getInstance().getChannelPartnerRoleId();
//				departmentId = SystemConfig.getInstance()
//						.getChannelPartnerDepartmentId();
//				passwd = SystemConfig.getInstance().getChannelPartnerPasswd();
//			}
			// TODO
//			staff2.setLoginName(data.getLoginName());
//			staff2.setRealName(data.getRealName());
//			staff2.setEmail(data.getEmail());
//			staff2.setMobile(data.getMobile());
//			staff2.setDepartmentId(departmentId);
//			staff2.setPassword(passwd);
//			staff2.setCreateDate(new Date());
//			staff2.setStatus(Status.NORMAL);
//			staff2.setSex(Sex.MALE);
//			staff2.setCreateUser(staff.getLoginName());
//			try {
//				proxy.createStaff(staff2, roleId);
//			} catch (Exception e) {
//				LOG.error(e.getMessage(), e);
//				throw new MyException("创建帐号出错,msg=" + e.getMessage());
//			}
			data.setBalance(new BigDecimal(0));
			this.insert(data);

			// 插入扩展表
//			data.getPartnerInfoExt().setPartnerId(data.getPartnerId());
//			partnerInfoService.saveAndUpdate(data.getPartnerInfoExt());

			
			// TODO
			/**
			 * 海航去掉此段代码
			 */
//			String msg = "您的初始密码为[" + passwd + "],请尽快登陆修改密码";
//			BCloudSMSClient sms = new BCloudSMSClient();
//			try {
//				String smsUrl = SystemConfig.getInstance().getSmsUrl();
//				if (StringUtils.isNotBlank(smsUrl)) {
//					sms.setGwUrl(smsUrl);
//				}
//				String smsUser = SystemConfig.getInstance().getSmsUser();
//				if (StringUtils.isNotBlank(smsUser)) {
//					sms.setGwUser(smsUser);
//				}
//				String smsPwd = SystemConfig.getInstance().getSmsPwd();
//				if (StringUtils.isNotBlank(smsPwd)) {
//					sms.setGwPwd(smsPwd);
//				}
//				sms.sendSMS(data.getMobile(), msg);
//			} catch (FOSSException e) {
//				LOG.debug(e.getMessage(), e);
//				e.printStackTrace();
//			}

			// 合作伙伴产品归属关联表(新增)
			List<PartnerProduct> insertBatchList = new ArrayList<PartnerProduct>();
			for (int i = 0; i < data.getPartnerProductList().size(); i++) {
				PartnerProduct partnerproduct = data.getPartnerProductList()
						.get(i);
				if (partnerproduct == null) {
					continue;
				}
				if (partnerproduct.getSeqId() == null
						&& partnerproduct.getProductId() != null) {
					partnerproduct.setCreator(staff.getAccount());
					partnerproduct.setCreateTime(new Date());
				} else if (partnerproduct.getSeqId() != null) {
					partnerproduct.setUpdator(staff.getAccount());
					partnerproduct.setUpdateTime(new Date());
				} else {
					continue;
				}
				partnerproduct.setPartnerId(data.getPartnerId());
				insertBatchList.add(partnerproduct);
			}
			// 批量新增
			partnerProductService.insertBatch(insertBatchList);
		}
		// 新增订单处理记录
		OrderDealRecordWithBLOBs record = createOrderDealRecordWithBLOBs(data, staff);
		record.setStartRecord(startRecord);
		orderDealRecordDao.insert(record);
	}


}