package com.yzx.flow.modular.channel.service.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.page.PageInfoBT;
import com.yzx.flow.common.persistence.model.ChannelCompany;
import com.yzx.flow.common.persistence.model.ChannelCompanyRecharge;
import com.yzx.flow.common.persistence.model.Staff;
import com.yzx.flow.core.aop.dbrouting.DataSource;
import com.yzx.flow.core.aop.dbrouting.DataSourceType;
import com.yzx.flow.core.util.DateUtil;
import com.yzx.flow.modular.channel.service.IChannelCompanyRechargeService;
import com.yzx.flow.modular.system.dao.ChannelCompanyDao;
import com.yzx.flow.modular.system.dao.ChannelCompanyRechargeDao;


/**
 * 充值记录
 * 
 * @author hc4gw02
 *
 */
@Service("channelCompanyRechargeService")
public class ChannelCompanyRechargeServiceImpl implements IChannelCompanyRechargeService {
	private static final Logger LOGGER = LoggerFactory.getLogger(ChannelCompanyRechargeServiceImpl.class);

	@Autowired
	private ChannelCompanyRechargeDao channelCompanyRechargeDao;

	@Autowired
	private ChannelCompanyDao channelCompanyDao;
	
	@Resource(name = "queueTemplate")
	private RabbitTemplate queueTemplate;

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.channel.service.impl.IChannelCompanyRechargeService#pageQuery(com.yzx.flow.common.page.Page)
	 */
	@Override
	public PageInfoBT<ChannelCompanyRecharge> pageQuery(Page<ChannelCompanyRecharge> page) {
		LOGGER.info("分页查询充值记录,params={}", page.getParams());
		List<ChannelCompanyRecharge> list = channelCompanyRechargeDao.pageQuery(page);
		PageInfoBT<ChannelCompanyRecharge> resPage=new PageInfoBT<ChannelCompanyRecharge>(list, page.getTotal());
        return resPage;
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.channel.service.impl.IChannelCompanyRechargeService#insert(com.yzx.flow.common.persistence.model.ChannelCompanyRecharge)
	 */
	@Override
	public void insert(ChannelCompanyRecharge data) {
		channelCompanyRechargeDao.insert(data);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.channel.service.impl.IChannelCompanyRechargeService#get(java.lang.Long)
	 */
	@Override
	public ChannelCompanyRecharge get(Long companyRechargeId) {
		return channelCompanyRechargeDao.selectByPrimaryKey(companyRechargeId);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.channel.service.impl.IChannelCompanyRechargeService#saveAndUpdate(com.yzx.flow.common.persistence.model.ChannelCompanyRecharge)
	 */
	@Override
	public void saveAndUpdate(ChannelCompanyRecharge data) {
		if (null != data.getCompanyRechargeId()) {// 判断有没有传主键，如果传了为更新，否则为新增
			this.update(data);
		} else {
			this.insert(data);
		}
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.channel.service.impl.IChannelCompanyRechargeService#update(com.yzx.flow.common.persistence.model.ChannelCompanyRecharge)
	 */
	@Override
	public void update(ChannelCompanyRecharge data) {
		channelCompanyRechargeDao.updateByPrimaryKey(data);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.channel.service.impl.IChannelCompanyRechargeService#delete(java.lang.Long)
	 */
	@Override
	public int delete(Long companyRechargeId) {
		return channelCompanyRechargeDao.deleteByPrimaryKey(companyRechargeId);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.channel.service.impl.IChannelCompanyRechargeService#saveSupplierRecharge(com.yzx.flow.common.persistence.model.ChannelCompanyRecharge, com.yzx.flow.common.persistence.model.Staff, java.lang.String)
	 */
	//@Transactional(rollbackFor = Exception.class)  异步发送通知，暂时去除事务 后续，通过页面ajax请求完成 mq触发
	@Override
	public void saveSupplierRecharge(ChannelCompanyRecharge data, Staff staff, String operateIp) {
		// 更新供应商累计充值
		ChannelCompany channelCompany = channelCompanyDao.selectByPrimaryKey(data.getCompanyId());
		// 当充值时间小于当前时间，需要做供应商数据修复
		//repairOldData(data);
		if ("0".equals(data.getType())) {// 充值
			if (channelCompany.getRechargeAmount() == null) {
				channelCompany.setRechargeAmount(new BigDecimal(0));
			}
			if (channelCompany.getStaticBalance() == null) {
				channelCompany.setStaticBalance(new BigDecimal(0));
			}
			// 累积充值
			channelCompany.setRechargeAmount(channelCompany.getRechargeAmount().add(data.getMoney()));
			// 统计余额
			channelCompany.setStaticBalance(channelCompany.getStaticBalance().add(data.getMoney()));
		}
		channelCompany.setUpdator(staff.getLoginName());
		channelCompanyDao.updateBalanceByPrimaryKey(channelCompany);

		// 新增充值记录
		data.setInputTime(new Date());
		data.setChargeTime(DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
		data.setLoginName(staff.getLoginName());
		data.setOperatorName(staff.getLoginName());
		data.setOperateIp(operateIp);
		channelCompanyRechargeDao.insert(data);
	}

	private boolean isSameDay(Calendar cal1, Calendar cal2) {
		boolean isSameYear = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
		boolean isSameMonth = isSameYear && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH);
		boolean isSameDate = isSameMonth && cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
		return isSameDate;
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.channel.service.impl.IChannelCompanyRechargeService#repairOldData(com.yzx.flow.common.persistence.model.ChannelCompanyRecharge)
	 */
	@DataSource(DataSourceType.READ)
	@Override
	public boolean repairOldData(ChannelCompanyRecharge data) {
		boolean flag = true;
		try {
			Calendar chargeTime = Calendar.getInstance();
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			String date = format.format(new Date());
			chargeTime.setTime(format.parse(data.getChargeTime()));
			Calendar endTime = Calendar.getInstance();
			endTime.setTime(format.parse(date));
			if (!isSameDay(chargeTime, endTime)) {

				// 修复数据
				// 1.当天的数据修复充值和期末余额
				LOGGER.info("修复当天数据 {}",chargeTime);
				repariTrad(data, chargeTime, 0);
				repariManage(data, chargeTime, 0);
				// 2.其他的数据修复期初余额，期末余额
				chargeTime.add(Calendar.DATE, 1);
				data.setChargeTime(format.format(chargeTime.getTime()));

				while (chargeTime.compareTo(endTime) < 0) {

					LOGGER.info("修复历史数据 {}",chargeTime);
					
					repariTrad(data, chargeTime, 1);
					repariManage(data, chargeTime, 1);
					chargeTime.add(Calendar.DATE, 1);
					data.setChargeTime(format.format(chargeTime.getTime()));

				}
				chargeTime.add(Calendar.DATE, -1);
				//type 1位财务报表 2个运营报表
				queueTemplate.convertAndSend("{\"type\":2,\"statisticsDate\":\""+format.format(chargeTime.getTime())+"\"}");
				queueTemplate.convertAndSend("{\"type\":1,\"statisticsDate\":\""+format.format(chargeTime.getTime())+"\"}");

			}

		} catch (Exception e) {
			LOGGER.error("修复数据失败 e={}", e);
			flag = false;

		}
		return flag;
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.channel.service.impl.IChannelCompanyRechargeService#repariTrad(com.yzx.flow.common.persistence.model.ChannelCompanyRecharge, java.util.Calendar, int)
	 */
	@DataSource(DataSourceType.READ)
	@Override
	public void repariTrad(ChannelCompanyRecharge data, Calendar chargeTime, int i) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("companyId", data.getCompanyId());
		params.put("statisticsDate", data.getChargeTime());
		params.put("money", data.getMoney());
		params.put("remark", data.getRemark());
		params.put("type", i);
		// 当日充值金额 当日余额 消耗时间预估
		channelCompanyRechargeDao.repariTrad(params);
		

	}
	
	
	
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.channel.service.impl.IChannelCompanyRechargeService#repariManage(com.yzx.flow.common.persistence.model.ChannelCompanyRecharge, java.util.Calendar, int)
	 */
	@DataSource(DataSourceType.READ)
	@Override
	public void repariManage(ChannelCompanyRecharge data, Calendar chargeTime, int i) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("statisticsDate", data.getChargeTime());
		params.put("money", data.getMoney());
		params.put("type", i);
		// 当日充值金额 当日余额 消耗时间预估
		channelCompanyRechargeDao.repariManage(params);
	}
	
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.channel.service.impl.IChannelCompanyRechargeService#batchSave(java.util.List)
	 */
	@Override
	public int batchSave(List<ChannelCompanyRecharge> list){
		return channelCompanyRechargeDao.batchInsert(list);
	}


}
