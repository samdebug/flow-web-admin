package com.yzx.flow.modular.channel.service;

import java.util.Calendar;
import java.util.List;

import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.page.PageInfoBT;
import com.yzx.flow.common.persistence.model.ChannelCompanyRecharge;
import com.yzx.flow.common.persistence.model.Staff;

public interface IChannelCompanyRechargeService {

	/**
	 * 分页查询充值记录
	 * 
	 * @param page
	 * @return
	 */
	public abstract PageInfoBT<ChannelCompanyRecharge> pageQuery(
			Page<ChannelCompanyRecharge> page);

	public abstract void insert(ChannelCompanyRecharge data);

	public abstract ChannelCompanyRecharge get(Long companyRechargeId);

	public abstract void saveAndUpdate(ChannelCompanyRecharge data);

	public abstract void update(ChannelCompanyRecharge data);

	public abstract int delete(Long companyRechargeId);

	/**
	 * 保存充值记录
	 * 
	 * @param data
	 * @param staff
	 */
	//@Transactional(rollbackFor = Exception.class)  异步发送通知，暂时去除事务 后续，通过页面ajax请求完成 mq触发
	public abstract void saveSupplierRecharge(ChannelCompanyRecharge data,
			Staff staff, String operateIp);

	/**
	 * 如果充值时间不是当天，需要对之前数据进行修复 并邮件通知
	 * 
	 * @param data
	 */
	//@DataSource(DataSourceType.READ)
	public abstract boolean repairOldData(ChannelCompanyRecharge data);

	/**
	 * stat_suppiler_trade_day 数据修复 发送邮件
	 * 
	 * @param data
	 * @param chargeTime
	 * @param i
	 *            0 第一天数据 1 其他数据
	 */
	//@DataSource(DataSourceType.READ)
	public abstract void repariTrad(ChannelCompanyRecharge data,
			Calendar chargeTime, int i);

	/**
	 * stat_flow_manage_day 数据修复 发送邮件
	 * 
	 * @param data
	 * @param chargeTime
	 * @param i
	 *            0 第一天数据 1 其他数据
	 */
	//@DataSource(DataSourceType.READ)
	public abstract void repariManage(ChannelCompanyRecharge data,
			Calendar chargeTime, int i);

	public abstract int batchSave(List<ChannelCompanyRecharge> list);

}