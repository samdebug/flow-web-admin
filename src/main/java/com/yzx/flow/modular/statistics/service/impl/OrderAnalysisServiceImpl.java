package com.yzx.flow.modular.statistics.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.page.PageInfoBT;
import com.yzx.flow.common.persistence.model.OrderAnalysisInfo;
import com.yzx.flow.core.aop.dbrouting.DataSource;
import com.yzx.flow.core.aop.dbrouting.DataSourceType;
import com.yzx.flow.modular.statistics.service.IOrderAnalysisService;
import com.yzx.flow.modular.system.dao.OrderAnalysisDao;

/**
 * 
 * @author xiangrui
 *
 *         2017年3月21日下午5:02:12 TODO 订单分析
 */
@Service("orderAnalysisService")
public class OrderAnalysisServiceImpl implements IOrderAnalysisService {
	private static final Logger logger = LoggerFactory.getLogger(OrderAnalysisServiceImpl.class);
	@Autowired
	private OrderAnalysisDao orderAnalysisDao;

	@DataSource(DataSourceType.READ)
	@Override
	@Transactional(readOnly = true)
	public PageInfoBT<OrderAnalysisInfo> pageQuery(Page<OrderAnalysisInfo> page)
			throws Exception {
		logger.info("【进入订单分析报表】");
		// 开始时间
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
		if (createEndTime.equals(df.format(new Date()))) {
			page.getParams().put("flag", "1");
		} else {
			page.getParams().put("flag", "");
		}
//		page.setTotal(orderAnalysisDao.countForPage(page.getParams()));
		List<OrderAnalysisInfo> list = orderAnalysisDao.pageQuery(page);
		PageInfoBT<OrderAnalysisInfo> resultPage = new PageInfoBT<OrderAnalysisInfo>(list,page.getTotal());
		return resultPage;
	}
	
	@Override
	@Transactional(readOnly = true)
	public Map<String, Object> queryTotal(Page<OrderAnalysisInfo> page)
			throws Exception {
		String createStartTime = (String) page.getParams()
				.get("beginCheckTime");
		String createEndTime = (String) page.getParams().get("endCheckTime");
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		if (createStartTime == null && createEndTime == null) {
			createStartTime = df.format(new Date());
			createEndTime = createStartTime;
		}
		page.getParams().put("beginCheckTime", createStartTime);
		page.getParams().put("endCheckTime", createEndTime);
		if (createEndTime.equals(df.format(new Date()))) {
			page.getParams().put("flag", "1");
		} else {
			page.getParams().put("flag", "");
		}
		page.setAutoCountTotal(false);

		Map<String, Object> map = orderAnalysisDao.totalSum(page);
		map = map == null ? new HashMap<String, Object>() : map;
		return map;
	}
	
	@DataSource(DataSourceType.READ)
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.statistics.service.impl.IOrderAnalysisService#export(com.yzx.flow.common.page.Page)
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<OrderAnalysisInfo> export(Page<OrderAnalysisInfo> page) {
		String createStartTime = (String) page.getParams()
				.get("beginCheckTime");
		String createEndTime = (String) page.getParams().get("endCheckTime");
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		if (createStartTime == null && createEndTime == null) {
			createStartTime = df.format(new Date());
			createEndTime = createStartTime;
		}
		if (createEndTime.equals(df.format(new Date()))) {
			page.getParams().put("flag", "1");
		} else {
			page.getParams().put("flag", "");
		}
		page.getParams().put("beginCheckTime", createStartTime);
		page.getParams().put("endCheckTime", createEndTime);
		
        page.setAutoCountTotal(false);
        page.setTotal(orderAnalysisDao.countForPage(page.getParams()));
        List<OrderAnalysisInfo> list = orderAnalysisDao.pageQuery(page);
        int pageNo = page.getPage();
        page.setPage(pageNo);
        page.setDatas(list);
        return page;
	}
}
