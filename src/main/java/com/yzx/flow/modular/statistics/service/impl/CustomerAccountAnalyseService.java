package com.yzx.flow.modular.statistics.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.page.PageInfoBT;
import com.yzx.flow.common.persistence.model.CustomerStatisticsDay;
import com.yzx.flow.core.aop.dbrouting.DataSource;
import com.yzx.flow.core.aop.dbrouting.DataSourceType;
import com.yzx.flow.core.shiro.ShiroKit;
import com.yzx.flow.core.shiro.ShiroUser;
import com.yzx.flow.modular.statistics.service.ICustomerAccountAnalyseService;
import com.yzx.flow.modular.system.dao.CustomerStatisticsDayDao;

@Service
public class CustomerAccountAnalyseService implements ICustomerAccountAnalyseService{
	@Autowired
	private CustomerStatisticsDayDao customerStatisticsDayDao;
	
	/**
	 * 分页统计数据
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@DataSource(DataSourceType.READ)
	@Transactional(readOnly=true)
	public PageInfoBT<CustomerStatisticsDay> pageQuery(Page<CustomerStatisticsDay> page) throws Exception {
		String createStartTime = (String) page.getParams()
				.get("beginCheckTime");
		String createEndTime = (String) page.getParams().get("endCheckTime");
		// 默认不能为空
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		if (createStartTime == null && createEndTime == null) {
			createStartTime = df.format(new Date());
			createEndTime = createStartTime;
		}
		page.getParams().put("beginCheckTime", createStartTime);
		page.getParams().put("endCheckTime", createEndTime);
        ShiroUser user = ShiroKit.getUser();
        page.getParams().put("loginName", user.getName());
        page.setAutoCountTotal(false);
        page.setTotal(customerStatisticsDayDao.countForPage(page.getParams()));
        List<CustomerStatisticsDay> list = customerStatisticsDayDao.pageQuery(page);
        PageInfoBT<CustomerStatisticsDay> resultPage = new PageInfoBT<>(list, page.getTotal());
        return resultPage;
	}
	
	/**
	 * 统计总和
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@DataSource(DataSourceType.READ)
	@Transactional(readOnly=true)
	public Map<String, Object> statisticsTotal(Page<CustomerStatisticsDay> page) throws Exception {
        ShiroUser user = ShiroKit.getUser();
        page.getParams().put("loginName", user.getName());
        return customerStatisticsDayDao.statisticsTotal(page.getParams());
	}
}
