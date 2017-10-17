package com.yzx.flow.modular.statistics.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.persistence.model.ProfitInfo;
import com.yzx.flow.modular.statistics.service.IProfitStatisticsService;
import com.yzx.flow.modular.system.dao.FlowOrderInfoDao;

@Service
public class ProfitStatisticsServiceImpl implements IProfitStatisticsService {

	@Autowired
	private FlowOrderInfoDao flowOrderInfoDao;
	
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.statistics.service.impl.IProfitStatisticsService#getCustomerInfoByProfit(com.yzx.flow.common.page.Page)
	 */
	@Override
	public Page<ProfitInfo> getCustomerInfoByProfit(Page<ProfitInfo> page){
	    List<ProfitInfo> list = flowOrderInfoDao.getCustomerInfoByProfit(page);
        page.setDatas(list);
        return page;
	}
	
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.statistics.service.impl.IProfitStatisticsService#getPartnerInfoByProfit(com.yzx.flow.common.page.Page)
	 */
	@Override
	public Page<ProfitInfo> getPartnerInfoByProfit(Page<ProfitInfo> page) {
		List<ProfitInfo> list = flowOrderInfoDao.getPartnerInfoByProfit(page);
        page.setDatas(list);
        return page;
	}
	
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.statistics.service.impl.IProfitStatisticsService#getChannelSupplierProfit(com.yzx.flow.common.page.Page)
	 */
	@Override
	public Page<ProfitInfo> getChannelSupplierProfit(Page<ProfitInfo> page){
		List<ProfitInfo> list = flowOrderInfoDao.getChannelSupplierProfit(page);
        page.setDatas(list);
        return page;
	}
	
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.statistics.service.impl.IProfitStatisticsService#getMonthlyConsumptionQuota(com.yzx.flow.common.page.Page)
	 */
	@Override
	public List<ProfitInfo> getMonthlyConsumptionQuota(Page<ProfitInfo> page){
		List<ProfitInfo> list = flowOrderInfoDao.getMonthlyConsumptionQuota(page);
//        page.setDatas(list);
        return list;
	}
}
