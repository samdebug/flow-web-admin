package com.yzx.flow.modular.customer.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;

import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yzx.flow.common.constant.state.ManagerStatus;
import com.yzx.flow.common.constant.state.UserType;
import com.yzx.flow.common.exception.BizExceptionEnum;
import com.yzx.flow.common.exception.BussinessException;
import com.yzx.flow.common.exception.MyException;
import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.persistence.dao.AreaCodeMapper;
import com.yzx.flow.common.persistence.dao.UserMapper;
import com.yzx.flow.common.persistence.model.AreaCode;
import com.yzx.flow.common.persistence.model.CustomerInfo;
import com.yzx.flow.common.persistence.model.User;
import com.yzx.flow.common.util.Constant;
import com.yzx.flow.common.util.RedisHttpUtil;
import com.yzx.flow.common.util.URLConstants;
import com.yzx.flow.core.shiro.ShiroKit;
import com.yzx.flow.modular.customer.service.ICustomerInfoService;
import com.yzx.flow.modular.system.dao.CustomerInfoDao;
import com.yzx.flow.modular.system.dao.UserMgrDao;

/**
 * 
 * <b>Title：</b>CustomerInfoService.java<br/>
 * <b>Description：</b> <br/>
 * <b>@author： </b>v5480<br/>
 * <b>@date：</b>2015-07-08 18:05:18<br/>
 * <b>Copyright (c) 2015 szwisdom Tech.</b>
 * 
 */
@Service("customerInfoService")
public class CustomerInfoServiceImpl implements ICustomerInfoService {

	private static final Logger LOG = LoggerFactory.getLogger(CustomerInfoServiceImpl.class);
	
	@Autowired
	private CustomerInfoDao customerInfoDao;
	
	@Autowired
	private AreaCodeMapper areaCodeMapper;
	
	@Resource
    private UserMgrDao managerDao;

    @Resource
    private UserMapper userMapper;
    
    /**
     * 默认角色ID
     */
    @Value("${portal.customer.roleId}")
    private String portalCustomerRoleId;
	
	

//	@Autowired
//	private CheckTokenInfoMapper checkTokenInfoMapper;

//	@Autowired
//	private WxAccessConfMapper wxAccessConfMapper;
	
//	@Autowired
//	private CustomerInfoExtService customerInfoExtService;
	
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.customer.service.ICustomerInfoService#pageQuery(com.yzx.flow.common.page.Page)
	 */
	@Override
	public List<CustomerInfo> pageQuery(Page<CustomerInfo> page) {
		return customerInfoDao.pageQuery(page);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.customer.service.ICustomerInfoService#insert(com.yzx.flow.common.persistence.model.CustomerInfo)
	 */
	@Override
	public void insert(CustomerInfo data) {
		customerInfoDao.insert(data);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.customer.service.ICustomerInfoService#get(java.lang.Long)
	 */
	@Override
	public CustomerInfo get(Long customerId) {
		return customerInfoDao.selectByPrimaryKey(customerId);
	}
	
	
	

	private void saveCustomer2Portal(CustomerInfo data) {
		
		if ( data.getCustomerId() == null || data.getCustomerId().compareTo(0L) <= 0 
				|| StringUtils.isBlank(data.getLinkmanMobile()) ) {
			throw new BussinessException(BizExceptionEnum.REQUEST_NULL);
		}
		
		Map<String, Object> params = new HashedMap();
		params.put("target_id", data.getCustomerId());
		params.put("type", UserType.CUSTOMER.getCode());
		
		// 判断是否已经注册账号
        List<User> users = userMapper.selectByMap(params);
        User user = users != null && !users.isEmpty() ? users.get(0) : null;
        boolean registed = true;
        
        if ( user == null ) {
        	// 不存在账号，则检测账号是否可用
        	User theUser = managerDao.getByAccount(data.getAccount());
            if (theUser != null) {
                throw new BussinessException(BizExceptionEnum.USER_ALREADY_REG);
            }
            user = new User();
            user.setAccount(data.getAccount());
            // 完善账号信息
            user.setSalt(ShiroKit.getRandomSalt(5));
            // 默认手机号码后六位
            user.setPassword(ShiroKit.md5(String.format("%s888888", data.getAccount()), user.getSalt()));
            user.setCreatetime(new Date());
            user.setRoleid(portalCustomerRoleId);
            registed = false;
        }
        // 只允许添加客户类型
        user.setType(UserType.CUSTOMER.getCode());
        user.setTargetId(data.getCustomerId());
        user.setName(data.getCustomerName());
        user.setEmail(data.getLinkmanEmail());
        user.setPhone(data.getLinkmanMobile());
        // 客户状态若不是商用状态则暂时先冻结
        if ( !Constant.CUSTOMER_STATUS_ON.equals(data.getStatus()) ) {
        	user.setStatus(ManagerStatus.FREEZED.getCode());// 
        } else {
        	user.setStatus(ManagerStatus.OK.getCode());
        }
        
        if ( !registed ) {
        	this.userMapper.insert(user);
        } else {
        	this.userMapper.updateById(user);
        }
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.customer.service.ICustomerInfoService#saveAndUpdate(com.yzx.flow.common.persistence.model.CustomerInfo)
	 */
	@Override
	@Transactional
	public void saveAndUpdate(CustomerInfo data) {
		if (null != data.getCustomerId()) {// 判断有没有传主键，如果传了为更新，否则为新增
			saveCustomer2Portal(data);
			this.update(data);
		} else {
//			String passwd = SystemConfig.getInstance().getCustomerPassWd();
//			String payPasswd = SystemConfig.getInstance().getCustomerPayPassWd();
			data.setIsFirstLogin(0);
			data.setIsDeleted(0);
			data.setStatus(1);
			data.setPasswd("");
			data.setPayPasswd("");
			data.setSvrPasswd("");
			data.setSettlePrice(BigDecimal.ZERO);
			// TODO
//			data.setPasswd(MD5Util.md5Encode(data.getPasswd()).toLowerCase());
//			data.setPayPasswd(MD5Util.md5Encode(data.getPayPasswd()).toLowerCase());
			if (data.getPartnerType() == 1) {
				data.setPayPasswd("");
			}
			String uuid = UUID.randomUUID().toString();
			uuid = uuid.replaceAll("-", "");
			data.setIdentityId(uuid);
			data.setBalance(BigDecimal.ZERO);
			
			this.insert(data);
			saveCustomer2Portal(data);
			// TODO send msg
//			//插入客户扩展表
//			data.getCustomerInfoExt().setCustomerId(data.getCustomerId());
//			customerInfoExtService.saveAndUpdate(data.getCustomerInfoExt());
//			
//			if (data.getPartnerType() == 2) {
//				String msg = "您的初始密码为[" + passwd + "],支付初始密码为[" + payPasswd + "],请尽快登陆修改密码";
//				BCloudSMSClient sms = new BCloudSMSClient();
//				try {
//					String smsUrl = SystemConfig.getInstance().getSmsUrl();
//					if (StringUtils.isNotBlank(smsUrl)) {
//						sms.setGwUrl(smsUrl);
//					}
//					String smsUser = SystemConfig.getInstance().getSmsUser();
//					if (StringUtils.isNotBlank(smsUser)) {
//						sms.setGwUser(smsUser);
//					}
//					String smsPwd = SystemConfig.getInstance().getSmsPwd();
//					if (StringUtils.isNotBlank(smsPwd)) {
//						sms.setGwPwd(smsPwd);
//					}
//					sms.sendSMS(data.getLinkmanMobile(), msg);
//				} catch (FOSSException e) {
//					LOG.debug(e.getMessage(), e);
//					e.printStackTrace();
//				}
//			}
		}
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.customer.service.ICustomerInfoService#update(com.yzx.flow.common.persistence.model.CustomerInfo)
	 */
	@Override
	public void update(CustomerInfo data) {
		customerInfoDao.updateByPrimaryKey(data);
		// TODO clear redis
		String result = RedisHttpUtil.sendGet(URLConstants.DEL_REDIS_URL,
				URLConstants.T_CUSTOMER_INFO,data.getCustomerId()+"");
		if (!"OK".equals(result)) {
			throw new MyException("删除Redis中信息出错,其请求URL参数为:" + URLConstants.T_CUSTOMER_INFO+"\t"+data.getCustomerId());
		}
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.customer.service.ICustomerInfoService#updateDeletedFlag(com.yzx.flow.common.persistence.model.CustomerInfo)
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateDeletedFlag(CustomerInfo data) {
	    // 物理删除
	    // 删除验证APP单一账号在线
		// TODO 
//	    checkTokenInfoMapper.deleteByCustomerId(data.getCustomerId());
//	    // 删除微信接入配置信息
//	    wxAccessConfMapper.deleteCustomerId(data.getCustomerId());
//	    
//		AuthProxy proxy = AuthProxy.getProxy(SystemConfig.getInstance().getPortalUrl());
//		LOG.info("删除的账号信息为"+data.getAccount());
//		try {
//			proxy.deleteStaffByLoginName(data.getAccount());
//		} catch (Exception e) {
//			LOG.error(e.getMessage(), e);
//			throw new MyException("删除客户出错，请联系管理员。");
//		}
	    User theUser = managerDao.getByAccount(data.getAccount());
	    if ( theUser != null && UserType.CUSTOMER.getCode().equals(theUser.getType())
	    		&& data.getCustomerId().equals(theUser.getTargetId())) {
	    	theUser.setStatus(ManagerStatus.DELETED.getCode());
	    	this.userMapper.updateById(theUser);
	    }
		// 删除客户
		delete(data.getCustomerId());
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.customer.service.ICustomerInfoService#delete(java.lang.Long)
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public int delete(Long customerId) {
		int i = -1;
		try {
			i = customerInfoDao.deleteByPrimaryKey(customerId);
		} catch (Exception e) {
			LOG.error("删除客户异常", e);
			throw new BussinessException(BizExceptionEnum.CUSTOMER_FORMAT_ERROR, "当前客户有其他依赖资源，暂不能删除！");
		}
		
		// TODO clear redis
		String result = RedisHttpUtil.sendGet(URLConstants.DEL_REDIS_URL,
				URLConstants.T_CUSTOMER_INFO, customerId+"");
		if (!"OK".equals(result)) {
			throw new MyException("删除Redis中信息出错,其请求URL参数为:" +URLConstants.T_CUSTOMER_INFO+"\t"+customerId);
		}
		return i;
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.customer.service.ICustomerInfoService#getAccount(java.lang.String)
	 */
	@Override
	public List<CustomerInfo> getAccount(String account) {
		return customerInfoDao.getByAccount(account);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.customer.service.ICustomerInfoService#getMobile(java.lang.String)
	 */
	@Override
	public List<CustomerInfo> getMobile(String mobile) {
		return customerInfoDao.getByMobile(mobile);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.customer.service.ICustomerInfoService#getCustomerInfoByDetailId(java.lang.Long)
	 */
	@Override
	public CustomerInfo getCustomerInfoByDetailId(Long orderDetailId) {
		return customerInfoDao.getCustomerInfoByDetailId(orderDetailId);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.customer.service.ICustomerInfoService#getCustomerByCustomerInfo(com.yzx.flow.common.persistence.model.CustomerInfo)
	 */
	@Override
	public List<CustomerInfo> getCustomerByCustomerInfo(CustomerInfo customerInfo) {
		return customerInfoDao.getCustomerByCustomerInfo(customerInfo);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.customer.service.ICustomerInfoService#getByPartnerId(java.lang.Long)
	 */
	@Override
	public List<CustomerInfo> getByPartnerId(Long partnerId) {
	    return customerInfoDao.selectByPartnerId(partnerId);
	}
	
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.customer.service.ICustomerInfoService#selectALL()
	 */
	@Override
	public List<AreaCode> selectALL(){
		return areaCodeMapper.getAreaCodeAll();
	}
}