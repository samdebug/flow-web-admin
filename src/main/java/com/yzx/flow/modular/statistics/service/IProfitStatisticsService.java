package com.yzx.flow.modular.statistics.service;

import java.util.List;

import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.persistence.model.ProfitInfo;

public interface IProfitStatisticsService {

	public abstract Page<ProfitInfo> getCustomerInfoByProfit(
			Page<ProfitInfo> page);

	public abstract Page<ProfitInfo> getPartnerInfoByProfit(
			Page<ProfitInfo> page);

	public abstract Page<ProfitInfo> getChannelSupplierProfit(
			Page<ProfitInfo> page);

	public abstract List<ProfitInfo> getMonthlyConsumptionQuota(
			Page<ProfitInfo> page);

}