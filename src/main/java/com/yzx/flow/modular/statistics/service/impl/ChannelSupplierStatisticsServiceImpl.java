package com.yzx.flow.modular.statistics.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.page.PageInfoBT;
import com.yzx.flow.common.persistence.dao.AreaCodeMapper;
import com.yzx.flow.common.persistence.model.AreaCode;
import com.yzx.flow.common.persistence.model.ChannelSupplierStatistics;
import com.yzx.flow.common.persistence.model.SuppilerTradeDay;
import com.yzx.flow.core.aop.dbrouting.DataSource;
import com.yzx.flow.core.aop.dbrouting.DataSourceType;
import com.yzx.flow.core.shiro.ShiroKit;
import com.yzx.flow.core.shiro.ShiroUser;
import com.yzx.flow.modular.statistics.service.IChannelSupplierStatisticsService;
import com.yzx.flow.modular.system.dao.ChannelSupplierStatisticsDao;

@Service
public class ChannelSupplierStatisticsServiceImpl implements IChannelSupplierStatisticsService{
	@Autowired
	private ChannelSupplierStatisticsDao channelSupplierStatisticsDao;
	
	@Autowired
    private AreaCodeMapper areaCodeMapper;
	
	/**
	 * 分页统计客户通道消耗数据
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@DataSource(DataSourceType.READ)
	@Transactional(readOnly=true)
	public Page<ChannelSupplierStatistics> customerPageQuery(Page<ChannelSupplierStatistics> page) throws Exception {
//        if (!StaffUtil.isAdmin()) {
        	ShiroUser user = ShiroKit.getUser();
        	page.getParams().put("loginName", user.getName());
//        }
        List<ChannelSupplierStatistics> resultList = null;
        List<ChannelSupplierStatistics> list = null;
        if (StringUtils.isNotEmpty(page.getParams().get("dateType").toString()) && "month".equals(page.getParams().get("dateType").toString())) {//按月统计
        	page.setAutoCountTotal(false);
        	page.setTotal(channelSupplierStatisticsDao.detailMonthCount(page.getParams()));
	        list = channelSupplierStatisticsDao.detailMonth(page);
        }else {
        	list = channelSupplierStatisticsDao.detail(page);
		}
        if (null != list && !list.isEmpty()) {
        	resultList = new ArrayList<ChannelSupplierStatistics>();
        	page.getParams().put("customerType", 1);
			List<Map<String,Object>> customerTotal = channelSupplierStatisticsDao.detailTotal(page.getParams());
			
			for (int i = 0; i < list.size(); i++) {
				resultList.add(list.get(i));
				if ((i+1)==list.size()) {//最后一组合计
					for (Map<String, Object> map : customerTotal) {//最后一组合计
						if (null != map.get("channelSeqId") && list.get(i).getChannelSeqId().longValue() == new Long(map.get("channelSeqId").toString()).longValue()) {//插入一条合计
							ChannelSupplierStatistics temp= new ChannelSupplierStatistics();
							temp.setStatisticsDate("合计");
							temp.setChannelConsume(new BigDecimal(map.get("detailChannelConsume").toString()));
							temp.setCustomerConsume(new BigDecimal(map.get("detailCustomerConsume").toString()));
							temp.setProfit(new BigDecimal(map.get("profit").toString()));
							temp.setProfitRate(new BigDecimal(map.get("profitRate").toString()));
							temp.setSuccessNum(Integer.valueOf(map.get("detailSuccessNum").toString()));
							temp.setFailNum(Integer.valueOf(map.get("detailFailNum").toString()));
							temp.setSuccessRate(new BigDecimal(map.get("successRate").toString()));
							temp.setMobileOperator("");
							resultList.add(temp);
						}
					}
				}else {
					if (list.get(i).getChannelSeqId().intValue() != list.get(i+1).getChannelSeqId().intValue()) {
						for (Map<String, Object> map : customerTotal) {
							if (null != map.get("channelSeqId") && list.get(i).getChannelSeqId().longValue() == new Long(map.get("channelSeqId").toString()).longValue()) {//插入一条合计
								ChannelSupplierStatistics temp= new ChannelSupplierStatistics();
								temp.setStatisticsDate("合计");
								temp.setChannelConsume(new BigDecimal(map.get("detailChannelConsume").toString()));
								temp.setCustomerConsume(new BigDecimal(map.get("detailCustomerConsume").toString()));
								temp.setProfit(new BigDecimal(map.get("profit").toString()));
								temp.setProfitRate(new BigDecimal(map.get("profitRate").toString()));
								temp.setSuccessNum(Integer.valueOf(map.get("detailSuccessNum").toString()));
								temp.setFailNum(Integer.valueOf(map.get("detailFailNum").toString()));
								temp.setSuccessRate(new BigDecimal(map.get("successRate").toString()));
								temp.setMobileOperator("");
								resultList.add(temp);
							}
						}
					}
				}
			}
		}
        page.setDatas(resultList);
        return page;
	}
	
	/**
	 * 统计客户详情总和
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@DataSource(DataSourceType.READ)
	@Transactional(readOnly=true)
	public Map<String, Object> detailTotal(Page<ChannelSupplierStatistics> page) throws Exception {
        return channelSupplierStatisticsDao.detailTotal(page.getParams()).get(0);
	}
	
	/**
     * 获取所有的地区信息
     * @return
     */
    public List<AreaCode> selectALL(){
        return areaCodeMapper.getAreaCodeAll();
    }
    
    /**
	 * 分页统计通道信息
	 * @param page
	 * @return
	 * @throws Exception
	 */
//	@DataSource(DataSourceType.READ)
	@Transactional(readOnly=true)
	public Page<ChannelSupplierStatistics> view(Page<ChannelSupplierStatistics> page) throws Exception {
		List<ChannelSupplierStatistics> resultList = null;
		List<ChannelSupplierStatistics> list = null;
		if (StringUtils.isNotEmpty(page.getParams().get("dateType").toString()) && "month".equals(page.getParams().get("dateType").toString())) {//按月统计
			page.setAutoCountTotal(false);
	        page.setTotal(channelSupplierStatisticsDao.viewMonthCount(page.getParams()));
	        list = channelSupplierStatisticsDao.viewMonth(page);
		}else {
			page.setAutoCountTotal(false);
	        page.setTotal(channelSupplierStatisticsDao.countViewForPage(page.getParams()));
	        list = channelSupplierStatisticsDao.view(page);
		}
        if (null != list && !list.isEmpty()) {
        	resultList = new ArrayList<ChannelSupplierStatistics>();
        	page.getParams().put("channelType", 1);
			List<Map<String,Object>> channelTotal = channelSupplierStatisticsDao.viewTotal(page.getParams());
			for (int i = 0; i < list.size(); i++) {
				resultList.add(list.get(i));
				if ((i+1)==list.size()) {//最后一组合计
					for (Map<String, Object> map : channelTotal) {//最后一组合计
						if (null != map.get("channelSeqId") && list.get(i).getChannelSeqId().longValue() == new Long(map.get("channelSeqId").toString()).longValue()) {//插入一条合计
							ChannelSupplierStatistics temp= new ChannelSupplierStatistics();
							temp.setStatisticsDate("合计");
							temp.setCustomerNum(Integer.valueOf(map.get("customerNum").toString()));
							temp.setChannelConsume(new BigDecimal(map.get("totalChannelConsume").toString()));
							temp.setProfit(new BigDecimal(map.get("profit").toString()));
							temp.setProfitRate(new BigDecimal(map.get("profitRate").toString()));
							temp.setSuccessNum(Integer.valueOf(map.get("totalSuccessNum").toString()));
							temp.setFailNum(Integer.valueOf(map.get("totalFailNum").toString()));
							temp.setSuccessRate(new BigDecimal(map.get("successRate").toString()));
							temp.setMobileOperator("");
							resultList.add(temp);
						}
					}
				}else {
					if (list.get(i).getChannelSeqId().intValue() != list.get(i+1).getChannelSeqId().intValue()) {
						for (Map<String, Object> map : channelTotal) {
							if (null != map.get("channelSeqId") && list.get(i).getChannelSeqId().longValue() == new Long(map.get("channelSeqId").toString()).longValue()) {//插入一条合计
								ChannelSupplierStatistics temp= new ChannelSupplierStatistics();
								temp.setStatisticsDate("合计");
								temp.setCustomerNum(Integer.valueOf(map.get("customerNum").toString()));
								temp.setChannelConsume(new BigDecimal(map.get("totalChannelConsume").toString()));
								temp.setProfit(new BigDecimal(map.get("profit").toString()));
								temp.setProfitRate(new BigDecimal(map.get("profitRate").toString()));
								temp.setSuccessNum(Integer.valueOf(map.get("totalSuccessNum").toString()));
								temp.setFailNum(Integer.valueOf(map.get("totalFailNum").toString()));
								temp.setSuccessRate(new BigDecimal(map.get("successRate").toString()));
								temp.setMobileOperator("");
								resultList.add(temp);
							}
						}
					}
				}
			}
		}
        page.setDatas(resultList);
        return page;
	}
	
	/**
	 * 统计客户通道详情总和
	 * @param page
	 * @return
	 * @throws Exception
	 */
//	@DataSource(DataSourceType.READ)
	@Transactional(readOnly=true)
	public Map<String, Object> viewTotal(Page<ChannelSupplierStatistics> page) throws Exception {
        return channelSupplierStatisticsDao.viewTotal(page.getParams()).get(0);
	}
	
	/**
	 * 分页统计供应商信息
	 * @param page
	 * @return
	 * @throws Exception
	 */
//	@DataSource(DataSourceType.READ)
	@Transactional(readOnly=true)
	public Page<ChannelSupplierStatistics> query(Page<ChannelSupplierStatistics> page) throws Exception {
		List<ChannelSupplierStatistics> resultList = null;
		List<ChannelSupplierStatistics> list = null;
		if (StringUtils.isNotEmpty(page.getParams().get("dateType").toString()) && "month".equals(page.getParams().get("dateType").toString())) {
			page.setAutoCountTotal(false);
			page.setTotal(channelSupplierStatisticsDao.queryMonthCount(page.getParams()));
			list = channelSupplierStatisticsDao.queryMonth(page);
		}else {
			list = channelSupplierStatisticsDao.query(page);
		}
        if (null != list && !list.isEmpty()) {
        	resultList = new ArrayList<ChannelSupplierStatistics>();
        	page.getParams().put("supplierType", 1);
			List<Map<String,Object>> supplierTotal = channelSupplierStatisticsDao.queryTotal(page.getParams());
			for (int i = 0; i < list.size(); i++) {
				resultList.add(list.get(i));
				if ((i+1) == list.size()) { // 最后一条数据
					for (Map<String, Object> map : supplierTotal) {
						if (null != map.get("supplierCode") && list.get(i).getSupplierCode().equals(map.get("supplierCode").toString())) {//插入一条合计
							ChannelSupplierStatistics temp= new ChannelSupplierStatistics();
							temp.setStatisticsDate("合计");
							temp.setChannelConsume(new BigDecimal(map.get("totalChannelConsume").toString()));
							temp.setRechargeAmount(new BigDecimal(map.get("totalRechargeAmount").toString()));
							temp.setProfit(new BigDecimal(map.get("totalProfit").toString()));
							temp.setProfitRate(new BigDecimal(map.get("totalProfitRate").toString()));
							temp.setSuccessNum(Integer.valueOf(map.get("totalSuccessNum").toString()));
							temp.setFailNum(Integer.valueOf(map.get("totalFailNum").toString()));
							temp.setSuccessRate(new BigDecimal(map.get("successRate").toString()));
							resultList.add(temp);
						}
					}
				}else {   
					if (! list.get(i).getSupplierCode().equals(list.get(i+1).getSupplierCode())) {
						for (Map<String, Object> map : supplierTotal) {
							if (null != map.get("supplierCode") && list.get(i).getSupplierCode().equals(map.get("supplierCode").toString())) {//插入总计
								ChannelSupplierStatistics temp= new ChannelSupplierStatistics();
								temp.setStatisticsDate("合计");
								temp.setChannelConsume(new BigDecimal(map.get("totalChannelConsume").toString()));
								temp.setRechargeAmount(new BigDecimal(map.get("totalRechargeAmount").toString()));
								temp.setProfit(new BigDecimal(map.get("totalProfit").toString()));
								temp.setProfitRate(new BigDecimal(map.get("totalProfitRate").toString()));
								temp.setSuccessNum(Integer.valueOf(map.get("totalSuccessNum").toString()));
								temp.setFailNum(Integer.valueOf(map.get("totalFailNum").toString()));
								temp.setSuccessRate(new BigDecimal(map.get("successRate").toString()));
								resultList.add(temp);
							}
						}
					}
				}
			}
		}
        page.setDatas(resultList);
        return page;
	}
	
	/**
	 * 统计客户通道详情总和
	 * @param page
	 * @return
	 * @throws Exception
	 */
//	@DataSource(DataSourceType.READ)
	@Transactional(readOnly=true)
	public Map<String, Object> queryTotal(Page<ChannelSupplierStatistics> page) throws Exception {
        return channelSupplierStatisticsDao.queryTotal(page.getParams()).get(0);
	}
	
	/**
	 * 分页查询供应商出款数据统计
	 * @param page
	 * @return
	 */
//	@DataSource(DataSourceType.READ)
	@Transactional(readOnly=true)
	public PageInfoBT<SuppilerTradeDay> suppilerTradeQuery(Page<SuppilerTradeDay> page) {
		
		List<SuppilerTradeDay> list = channelSupplierStatisticsDao.suppilerTradeQuery(page);
		
		PageInfoBT<SuppilerTradeDay> resultPage = new PageInfoBT<>(list, page.getTotal());
//		page.setDatas(list);
//		return list;
		return resultPage;
	}
	
	/**
	 * 分页统计供应利润信息
	 * @param page
	 * @return
	 * @throws Exception
	 */
//	@DataSource(DataSourceType.READ)
	@Transactional(readOnly=true)
	public Page<ChannelSupplierStatistics> queryProfitRate(Page<ChannelSupplierStatistics> page) throws Exception {

		// 客户和合作伙伴参数分割
		String customerNames = (String) page.getParams().get("customer_names");//获取页面中
		String partnerNames = (String) page.getParams().get("partner_names");
		if (StringUtils.isNotEmpty(customerNames)) {
			String [] c = customerNames.split(",");
			String customer = "";
			for (String str : c) {
				customer += "'" + str + "',";
			}
			customer = customer.endsWith(",") ? customer.substring(0, customer.lastIndexOf(","))  :  "" ;
			page.getParams().put("customer_names", customer);
		}
		if (StringUtils.isNotEmpty(partnerNames)) {
			String [] p=partnerNames.split(",");
			String partner="";
			for (String str : p) {
				partner+="'"+str+"',";
			}
			partner=partner.endsWith(",")?partner.substring(0, partner.lastIndexOf(",")):"";
			page.getParams().put("partner_names", partner);
		}
		
		List<ChannelSupplierStatistics> resultList = null;
		List<ChannelSupplierStatistics> list = null;
		
		//按月查询供应商利润数据
		String dateType = page.getParams().get("dateType").toString() ;
		if (StringUtils.isNotEmpty(dateType) && "month".equals(dateType)) {
			page.setAutoCountTotal(false);
			page.setTotal(channelSupplierStatisticsDao.queryMonthProfitRateCount(page.getParams()));
			list = channelSupplierStatisticsDao.queryMonthProfitRate(page);
		}else {
			list = channelSupplierStatisticsDao.queryProfitRate(page);
		}
		
        if (null != list && ! list.isEmpty()) {
        	resultList = new ArrayList<ChannelSupplierStatistics>();
        	page.getParams().put("supplierType", 1);
        	List<Map<String,Object>> profitRateTotal = channelSupplierStatisticsDao.queryProfitRateTotal(page.getParams());
        	//计算客户消耗占比、供应商消耗占比、利润占比
        	BigDecimal totalChannelConsume = (BigDecimal) profitRateTotal.get(0).get("totalChannelConsume");
        	BigDecimal channelConsume = null;
        	BigDecimal consumeRatio = null ; 
        	
        	BigDecimal totalProfit = (BigDecimal) profitRateTotal.get(0).get("totalProfit");
        	BigDecimal profit = null;
        	BigDecimal profitRatio = null; // 利润占比
        	
        	BigDecimal totalExpend = (BigDecimal) profitRateTotal.get(0).get("totalExpend");
        	BigDecimal expend = null;
        	BigDecimal expendRatio = null; // 利润占比
        	for(ChannelSupplierStatistics temp : list){
        		//供应商消耗占比
        		channelConsume = temp.getChannelConsume();
        		if(channelConsume == null || totalChannelConsume == null){
        			consumeRatio = BigDecimal.ZERO;
        		}else{
        			consumeRatio = channelConsume.divide(totalChannelConsume,4,BigDecimal.ROUND_HALF_EVEN).movePointRight(2);
        		}
        		temp.setConsumeRatio(consumeRatio);

        		//利润消耗占比
        		profit = temp.getProfit() ; 
        		if(totalProfit == null || profit == null ){
        			profitRatio = BigDecimal.ZERO;
        		}else{
        			profitRatio = profit.divide(totalProfit,4,BigDecimal.ROUND_HALF_EVEN).movePointRight(2);
        		}
        		temp.setProfitRatio(profitRatio);
        		
        		//客户消耗占比
        		expend = temp.getExpend();
        		if(expend == null || totalExpend == null){
        			expendRatio = BigDecimal.ZERO;
        		}else{
        			expendRatio = expend.divide(totalExpend,4,BigDecimal.ROUND_HALF_EVEN).movePointRight(2);
        		}
        		temp.setExpendRatio(expendRatio);
        	}
        	
			for (int i = 0; i < list.size(); i++) {
				resultList.add(list.get(i));
				if ((i+1) == list.size()) {  // 最后一条，进行数据统计
					for (Map<String, Object> map : profitRateTotal) {
						if (null != map.get("statisticsDate") && list.get(i).getStatisticsDate().equals(map.get("statisticsDate").toString())) {
							ChannelSupplierStatistics temp= new ChannelSupplierStatistics();
							temp.setStatisticsDate("合计");
							temp.setChannelConsume(new BigDecimal(map.get("totalChannelConsume").toString()));
							temp.setProfit(new BigDecimal(map.get("totalProfit").toString()));
							temp.setProfitRate(new BigDecimal(map.get("totalProfitRate").toString()));
							temp.setSuccessNum(Integer.valueOf(map.get("totalSuccessNum").toString()));
							temp.setFailNum(Integer.valueOf(map.get("totalFailNum").toString()));
							temp.setSuccessRate(new BigDecimal(map.get("successRate").toString()));
							temp.setExpend(new BigDecimal(map.get("totalExpend").toString()));
							resultList.add(temp);
						}
					}
				}
			}
		}
        page.setDatas(resultList);
        return page;
	}
	
	
	/**
	 * 合计
	 * @param page
	 * @return
	 * @throws Exception
	 */
//	@DataSource(DataSourceType.READ)
	@Transactional(readOnly=true)
	public Map<String, Object> queryProfitRateTotal(Page<ChannelSupplierStatistics> page) throws Exception {
		// 客户和合作伙伴参数分割
		String customerNames = (String) page.getParams().get("customer_names");
		String partnerNames = (String) page.getParams().get("partner_names");
		if (StringUtils.isNotEmpty(customerNames)) {
			String [] c = customerNames.split(",");
			String customer = "";
			for (String str : c) {
				customer += "'" + str + "',";
			}
			customer = customer.endsWith(",") ? customer.substring(0, customer.lastIndexOf(","))  :  "" ;
			page.getParams().put("customer_names", customer);
		}
		
		if (StringUtils.isNotEmpty(partnerNames)) {
			String [] p=partnerNames.split(",");
			String partner="";
			for (String str : p) {
				partner+="'"+str+"',";
			}
			partner=partner.endsWith(",")?partner.substring(0, partner.lastIndexOf(",")):"";
			page.getParams().put("partner_names", partner);
		}
		
		List<Map<String,Object>> supplierTotal = channelSupplierStatisticsDao.queryProfitRateTotal(page.getParams());
		if(supplierTotal != null && supplierTotal.size()> 0 ){
			return supplierTotal.get(0);
		}
		else{
			return null;
		}
	}
	
}
