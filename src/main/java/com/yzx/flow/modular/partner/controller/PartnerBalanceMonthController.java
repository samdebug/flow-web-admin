package com.yzx.flow.modular.partner.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yzx.flow.common.constant.tips.ErrorTip;
import com.yzx.flow.common.controller.BaseController;
import com.yzx.flow.common.excel.TemplateExcel;
import com.yzx.flow.common.excel.TemplateExcelManager;
import com.yzx.flow.common.exception.BizExceptionEnum;
import com.yzx.flow.common.exception.BussinessException;
import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.page.PageInfoBT;
import com.yzx.flow.common.persistence.model.AreaCode;
import com.yzx.flow.common.persistence.model.BillsDetail;
import com.yzx.flow.common.persistence.model.BillsExcel;
import com.yzx.flow.common.persistence.model.CustomerBalanceDay;
import com.yzx.flow.common.persistence.model.PartnerBalanceMonth;
import com.yzx.flow.common.persistence.model.PartnerInfo;
import com.yzx.flow.common.util.Constant;
import com.yzx.flow.common.util.DateUtil;
import com.yzx.flow.core.shiro.ShiroKit;
import com.yzx.flow.core.shiro.ShiroUser;
import com.yzx.flow.modular.customer.service.IAreaCodeService;
import com.yzx.flow.modular.customer.service.ICustomerBalanceDayService;
import com.yzx.flow.modular.partner.service.IPartnerBalanceMonthService;
import com.yzx.flow.modular.partner.service.IPartnerService;

/**
 * 
 * <b>Title：</b>PartnerBalanceMonthController.java<br/>
 * <b>Description：</b> <br/>
 * <b>@author： </b>v5480<br/>
 * <b>@date：</b>2015-09-02 10:17:41<br/>
 * <b>Copyright (c) 2015 szwisdom Tech.</b>
 * 
 */
@Controller
@RequestMapping("/partnerBalanceMonth")
public class PartnerBalanceMonthController extends BaseController {
	
	private static final Logger LOG = LoggerFactory.getLogger(PartnerBalanceMonthController.class);
	
	
	@Autowired
	@Qualifier("partnerBalanceMonthService")
	private IPartnerBalanceMonthService partnerBalanceMonthService;

	@Autowired
	@Qualifier("partnerInfoService")
	private IPartnerService partnerService;

	@Autowired
	@Qualifier("customerBalanceDayService")
	private ICustomerBalanceDayService customerBalanceDayService;

	@Autowired
	@Qualifier("areaCodeService")
	private IAreaCodeService areaCodeService;
	
	
	
	@RequestMapping(value = "/query")
	@ResponseBody
	public Object pageQuery(Page<PartnerBalanceMonth> page) {
//	    Staff staff = getCurrentLogin();
//        if (!isAdmin()) {
//            // 合作伙伴
//            PartnerInfo partnerInfo = partnerInfoService.getByAccount(staff.getLoginName());
//            if (partnerInfo == null) {
//                LOG.error("合作伙伴不存在");
//                return fail("合作伙伴不存在");
//            }
//            page.getParams().put("partnerId", partnerInfo.getPartnerId());
//        }
		if ( page.getSort() == null || page.getSort().trim().isEmpty() ) {
			page.setSort("month");
		}
		Page<PartnerBalanceMonth> list = partnerBalanceMonthService.pageQuery(page);
//		return list.getDatas();
		return new PageInfoBT<PartnerBalanceMonth>(list.getDatas(), list.getTotal());
//		return com.alibaba.fastjson.JSONArray.parseArray("[{\"balanceMonthId\":2708,\"partnerId\":4,\"month\":\"201707\",\"payMoney\":10.8,\"balanceStatus\":1,\"balanceMoney\":10.8,\"adjustMoney\":0,\"creator\":\"admin\",\"createTime\":\"2017-08-16 18:09:00\",\"updator\":\"admin\",\"updateTime\":\"2017-08-17 17:12:00\",\"remark\":\"8月，系统自动月结算\",\"startDate\":\"20170701\",\"endDate\":\"20170731\",\"partnerInfo\":{\"creator\":\"钟诚\",\"createTime\":\"2016-05-27 11:40:41\",\"updator\":\"钟诚\",\"updateTime\":\"2017-08-11 16:25:48\",\"partnerId\":4,\"identityId\":\"02bcc83ffae24f96afe6f118c18e009d\",\"partnerName\":\"测试合作伙伴\",\"shorterName\":\"测试合作伙伴\",\"partnerNo\":\"测试合作伙伴\",\"loginName\":\"测试合作伙伴\",\"realName\":\"测试合作伙伴\",\"mobile\":\"13510860000\",\"email\":\"450681176@qq.com\",\"address\":\"\",\"partnerType\":1,\"partnerTypeDesc\":\"流量营销\",\"status\":\"1\",\"statusDesc\":\"商用\",\"balance\":49842797.5,\"creditAmount\":19999998,\"settlementDiscount\":70,\"coopGroupId\":\"16\",\"isDeleted\":0,\"attachmentFileList\":null,\"partnerProductList\":[],\"currentAmount\":69.56,\"availableCredit\":0,\"orderBillingType\":0,\"orderBillingTypeDesc\":\"否\",\"partnerInfoExt\":{\"partnerId\":4,\"adviserName\":\"测试合作伙伴\",\"adviserMobile\":\"13510860000\",\"customServiceName\":\"\",\"customServiceMobile\":\"\",\"otherLinkman\":\"\",\"otherMobile\":\"\",\"remark\":\"\"},\"partnerLevel\":5,\"settlementPattern\":1,\"settlementDiscountRatio\":null,\"settlementPatternDesc\":\"按固定折扣\"}},{\"balanceMonthId\":2706,\"partnerId\":4,\"month\":\"201609\",\"payMoney\":43.2,\"balanceStatus\":1,\"balanceMoney\":43.2,\"adjustMoney\":0,\"creator\":\"admin\",\"createTime\":\"2016-10-02 01:00:00\",\"updator\":\"admin\",\"updateTime\":\"2016-10-11 01:00:00\",\"remark\":\"10月，系统自动月结算\",\"startDate\":\"20160901\",\"endDate\":\"20160930\",\"partnerInfo\":{\"creator\":\"钟诚\",\"createTime\":\"2016-05-27 11:40:41\",\"updator\":\"钟诚\",\"updateTime\":\"2017-08-11 16:25:48\",\"partnerId\":4,\"identityId\":\"02bcc83ffae24f96afe6f118c18e009d\",\"partnerName\":\"测试合作伙伴\",\"shorterName\":\"测试合作伙伴\",\"partnerNo\":\"测试合作伙伴\",\"loginName\":\"测试合作伙伴\",\"realName\":\"测试合作伙伴\",\"mobile\":\"13510860000\",\"email\":\"450681176@qq.com\",\"address\":\"\",\"partnerType\":1,\"partnerTypeDesc\":\"流量营销\",\"status\":\"1\",\"statusDesc\":\"商用\",\"balance\":49842797.5,\"creditAmount\":19999998,\"settlementDiscount\":70,\"coopGroupId\":\"16\",\"isDeleted\":0,\"attachmentFileList\":null,\"partnerProductList\":[],\"currentAmount\":69.56,\"availableCredit\":0,\"orderBillingType\":0,\"orderBillingTypeDesc\":\"否\",\"partnerInfoExt\":{\"partnerId\":4,\"adviserName\":\"测试合作伙伴\",\"adviserMobile\":\"13510860000\",\"customServiceName\":\"\",\"customServiceMobile\":\"\",\"otherLinkman\":\"\",\"otherMobile\":\"\",\"remark\":\"\"},\"partnerLevel\":5,\"settlementPattern\":1,\"settlementDiscountRatio\":null,\"settlementPatternDesc\":\"按固定折扣\"}},{\"balanceMonthId\":2707,\"partnerId\":6,\"month\":\"201609\",\"payMoney\":0.05,\"balanceStatus\":1,\"balanceMoney\":0.05,\"adjustMoney\":0,\"creator\":\"admin\",\"createTime\":\"2016-10-02 01:00:00\",\"updator\":\"admin\",\"updateTime\":\"2016-10-11 01:00:00\",\"remark\":\"10月，系统自动月结算\",\"startDate\":\"20160901\",\"endDate\":\"20160930\",\"partnerInfo\":{\"creator\":\"陈蕾\",\"createTime\":\"2016-07-05 18:11:31\",\"updator\":\"陈蕾\",\"updateTime\":\"2017-07-27 11:43:32\",\"partnerId\":6,\"identityId\":\"43e04a743ea242308ed8b7a49b63f8fa\",\"partnerName\":\"微信H5充值客户\",\"shorterName\":\"微信H5充值客户\",\"partnerNo\":\"wx_h5_test\",\"loginName\":\"wx_h5_test\",\"realName\":\"微信H5充值客户\",\"mobile\":\"15279368541\",\"email\":\"wxtest@ucpaas.com\",\"address\":\"\",\"partnerType\":1,\"partnerTypeDesc\":\"流量营销\",\"status\":\"1\",\"statusDesc\":\"商用\",\"balance\":882.16,\"creditAmount\":0,\"settlementDiscount\":80,\"coopGroupId\":\"19\",\"isDeleted\":0,\"attachmentFileList\":null,\"partnerProductList\":[],\"currentAmount\":24.8,\"availableCredit\":0,\"orderBillingType\":0,\"orderBillingTypeDesc\":\"否\",\"partnerInfoExt\":{\"partnerId\":6,\"adviserName\":\"微信客户测试\",\"adviserMobile\":\"15086436578\",\"customServiceName\":\"\",\"customServiceMobile\":\"\",\"otherLinkman\":\"\",\"otherMobile\":\"\",\"remark\":\"\"},\"partnerLevel\":5,\"settlementPattern\":1,\"settlementDiscountRatio\":null,\"settlementPatternDesc\":\"按固定折扣\"}},{\"balanceMonthId\":256,\"partnerId\":6,\"month\":\"201608\",\"payMoney\":0.1,\"balanceStatus\":1,\"balanceMoney\":0.1,\"adjustMoney\":0,\"creator\":\"admin\",\"createTime\":\"2016-09-15 02:21:50\",\"updator\":\"admin\",\"updateTime\":\"2016-09-18 17:23:04\",\"remark\":\"9月，系统自动月结算\",\"startDate\":\"20160801\",\"endDate\":\"20160831\",\"partnerInfo\":{\"creator\":\"陈蕾\",\"createTime\":\"2016-07-05 18:11:31\",\"updator\":\"陈蕾\",\"updateTime\":\"2017-07-27 11:43:32\",\"partnerId\":6,\"identityId\":\"43e04a743ea242308ed8b7a49b63f8fa\",\"partnerName\":\"微信H5充值客户\",\"shorterName\":\"微信H5充值客户\",\"partnerNo\":\"wx_h5_test\",\"loginName\":\"wx_h5_test\",\"realName\":\"微信H5充值客户\",\"mobile\":\"15279368541\",\"email\":\"wxtest@ucpaas.com\",\"address\":\"\",\"partnerType\":1,\"partnerTypeDesc\":\"流量营销\",\"status\":\"1\",\"statusDesc\":\"商用\",\"balance\":882.16,\"creditAmount\":0,\"settlementDiscount\":80,\"coopGroupId\":\"19\",\"isDeleted\":0,\"attachmentFileList\":null,\"partnerProductList\":[],\"currentAmount\":24.8,\"availableCredit\":0,\"orderBillingType\":0,\"orderBillingTypeDesc\":\"否\",\"partnerInfoExt\":{\"partnerId\":6,\"adviserName\":\"微信客户测试\",\"adviserMobile\":\"15086436578\",\"customServiceName\":\"\",\"customServiceMobile\":\"\",\"otherLinkman\":\"\",\"otherMobile\":\"\",\"remark\":\"\"},\"partnerLevel\":5,\"settlementPattern\":1,\"settlementDiscountRatio\":null,\"settlementPatternDesc\":\"按固定折扣\"}},{\"balanceMonthId\":512,\"partnerId\":6,\"month\":\"201608\",\"payMoney\":0.1,\"balanceStatus\":1,\"balanceMoney\":0.1,\"adjustMoney\":0,\"creator\":\"admin\",\"createTime\":\"2016-09-15 10:53:50\",\"updator\":\"admin\",\"updateTime\":\"2016-09-18 17:23:06\",\"remark\":\"9月，系统自动月结算\",\"startDate\":\"20160801\",\"endDate\":\"20160831\",\"partnerInfo\":{\"creator\":\"陈蕾\",\"createTime\":\"2016-07-05 18:11:31\",\"updator\":\"陈蕾\",\"updateTime\":\"2017-07-27 11:43:32\",\"partnerId\":6,\"identityId\":\"43e04a743ea242308ed8b7a49b63f8fa\",\"partnerName\":\"微信H5充值客户\",\"shorterName\":\"微信H5充值客户\",\"partnerNo\":\"wx_h5_test\",\"loginName\":\"wx_h5_test\",\"realName\":\"微信H5充值客户\",\"mobile\":\"15279368541\",\"email\":\"wxtest@ucpaas.com\",\"address\":\"\",\"partnerType\":1,\"partnerTypeDesc\":\"流量营销\",\"status\":\"1\",\"statusDesc\":\"商用\",\"balance\":882.16,\"creditAmount\":0,\"settlementDiscount\":80,\"coopGroupId\":\"19\",\"isDeleted\":0,\"attachmentFileList\":null,\"partnerProductList\":[],\"currentAmount\":24.8,\"availableCredit\":0,\"orderBillingType\":0,\"orderBillingTypeDesc\":\"否\",\"partnerInfoExt\":{\"partnerId\":6,\"adviserName\":\"微信客户测试\",\"adviserMobile\":\"15086436578\",\"customServiceName\":\"\",\"customServiceMobile\":\"\",\"otherLinkman\":\"\",\"otherMobile\":\"\",\"remark\":\"\"},\"partnerLevel\":5,\"settlementPattern\":1,\"settlementDiscountRatio\":null,\"settlementPatternDesc\":\"按固定折扣\"}},{\"balanceMonthId\":768,\"partnerId\":6,\"month\":\"201608\",\"payMoney\":0.1,\"balanceStatus\":1,\"balanceMoney\":0.1,\"adjustMoney\":0,\"creator\":\"admin\",\"createTime\":\"2016-09-15 19:25:50\",\"updator\":\"admin\",\"updateTime\":\"2016-09-18 17:23:08\",\"remark\":\"9月，系统自动月结算\",\"startDate\":\"20160801\",\"endDate\":\"20160831\",\"partnerInfo\":{\"creator\":\"陈蕾\",\"createTime\":\"2016-07-05 18:11:31\",\"updator\":\"陈蕾\",\"updateTime\":\"2017-07-27 11:43:32\",\"partnerId\":6,\"identityId\":\"43e04a743ea242308ed8b7a49b63f8fa\",\"partnerName\":\"微信H5充值客户\",\"shorterName\":\"微信H5充值客户\",\"partnerNo\":\"wx_h5_test\",\"loginName\":\"wx_h5_test\",\"realName\":\"微信H5充值客户\",\"mobile\":\"15279368541\",\"email\":\"wxtest@ucpaas.com\",\"address\":\"\",\"partnerType\":1,\"partnerTypeDesc\":\"流量营销\",\"status\":\"1\",\"statusDesc\":\"商用\",\"balance\":882.16,\"creditAmount\":0,\"settlementDiscount\":80,\"coopGroupId\":\"19\",\"isDeleted\":0,\"attachmentFileList\":null,\"partnerProductList\":[],\"currentAmount\":24.8,\"availableCredit\":0,\"orderBillingType\":0,\"orderBillingTypeDesc\":\"否\",\"partnerInfoExt\":{\"partnerId\":6,\"adviserName\":\"微信客户测试\",\"adviserMobile\":\"15086436578\",\"customServiceName\":\"\",\"customServiceMobile\":\"\",\"otherLinkman\":\"\",\"otherMobile\":\"\",\"remark\":\"\"},\"partnerLevel\":5,\"settlementPattern\":1,\"settlementDiscountRatio\":null,\"settlementPatternDesc\":\"按固定折扣\"}},{\"balanceMonthId\":1024,\"partnerId\":6,\"month\":\"201608\",\"payMoney\":0.1,\"balanceStatus\":1,\"balanceMoney\":0.1,\"adjustMoney\":0,\"creator\":\"admin\",\"createTime\":\"2016-09-16 03:57:50\",\"updator\":\"admin\",\"updateTime\":\"2016-09-18 17:23:09\",\"remark\":\"9月，系统自动月结算\",\"startDate\":\"20160801\",\"endDate\":\"20160831\",\"partnerInfo\":{\"creator\":\"陈蕾\",\"createTime\":\"2016-07-05 18:11:31\",\"updator\":\"陈蕾\",\"updateTime\":\"2017-07-27 11:43:32\",\"partnerId\":6,\"identityId\":\"43e04a743ea242308ed8b7a49b63f8fa\",\"partnerName\":\"微信H5充值客户\",\"shorterName\":\"微信H5充值客户\",\"partnerNo\":\"wx_h5_test\",\"loginName\":\"wx_h5_test\",\"realName\":\"微信H5充值客户\",\"mobile\":\"15279368541\",\"email\":\"wxtest@ucpaas.com\",\"address\":\"\",\"partnerType\":1,\"partnerTypeDesc\":\"流量营销\",\"status\":\"1\",\"statusDesc\":\"商用\",\"balance\":882.16,\"creditAmount\":0,\"settlementDiscount\":80,\"coopGroupId\":\"19\",\"isDeleted\":0,\"attachmentFileList\":null,\"partnerProductList\":[],\"currentAmount\":24.8,\"availableCredit\":0,\"orderBillingType\":0,\"orderBillingTypeDesc\":\"否\",\"partnerInfoExt\":{\"partnerId\":6,\"adviserName\":\"微信客户测试\",\"adviserMobile\":\"15086436578\",\"customServiceName\":\"\",\"customServiceMobile\":\"\",\"otherLinkman\":\"\",\"otherMobile\":\"\",\"remark\":\"\"},\"partnerLevel\":5,\"settlementPattern\":1,\"settlementDiscountRatio\":null,\"settlementPatternDesc\":\"按固定折扣\"}},{\"balanceMonthId\":1280,\"partnerId\":6,\"month\":\"201608\",\"payMoney\":0.1,\"balanceStatus\":1,\"balanceMoney\":0.1,\"adjustMoney\":0,\"creator\":\"admin\",\"createTime\":\"2016-09-16 12:29:50\",\"updator\":\"admin\",\"updateTime\":\"2016-09-18 17:23:11\",\"remark\":\"9月，系统自动月结算\",\"startDate\":\"20160801\",\"endDate\":\"20160831\",\"partnerInfo\":{\"creator\":\"陈蕾\",\"createTime\":\"2016-07-05 18:11:31\",\"updator\":\"陈蕾\",\"updateTime\":\"2017-07-27 11:43:32\",\"partnerId\":6,\"identityId\":\"43e04a743ea242308ed8b7a49b63f8fa\",\"partnerName\":\"微信H5充值客户\",\"shorterName\":\"微信H5充值客户\",\"partnerNo\":\"wx_h5_test\",\"loginName\":\"wx_h5_test\",\"realName\":\"微信H5充值客户\",\"mobile\":\"15279368541\",\"email\":\"wxtest@ucpaas.com\",\"address\":\"\",\"partnerType\":1,\"partnerTypeDesc\":\"流量营销\",\"status\":\"1\",\"statusDesc\":\"商用\",\"balance\":882.16,\"creditAmount\":0,\"settlementDiscount\":80,\"coopGroupId\":\"19\",\"isDeleted\":0,\"attachmentFileList\":null,\"partnerProductList\":[],\"currentAmount\":24.8,\"availableCredit\":0,\"orderBillingType\":0,\"orderBillingTypeDesc\":\"否\",\"partnerInfoExt\":{\"partnerId\":6,\"adviserName\":\"微信客户测试\",\"adviserMobile\":\"15086436578\",\"customServiceName\":\"\",\"customServiceMobile\":\"\",\"otherLinkman\":\"\",\"otherMobile\":\"\",\"remark\":\"\"},\"partnerLevel\":5,\"settlementPattern\":1,\"settlementDiscountRatio\":null,\"settlementPatternDesc\":\"按固定折扣\"}},{\"balanceMonthId\":1536,\"partnerId\":6,\"month\":\"201608\",\"payMoney\":0.1,\"balanceStatus\":1,\"balanceMoney\":0.1,\"adjustMoney\":0,\"creator\":\"admin\",\"createTime\":\"2016-09-16 21:01:50\",\"updator\":\"admin\",\"updateTime\":\"2016-09-18 17:23:12\",\"remark\":\"9月，系统自动月结算\",\"startDate\":\"20160801\",\"endDate\":\"20160831\",\"partnerInfo\":{\"creator\":\"陈蕾\",\"createTime\":\"2016-07-05 18:11:31\",\"updator\":\"陈蕾\",\"updateTime\":\"2017-07-27 11:43:32\",\"partnerId\":6,\"identityId\":\"43e04a743ea242308ed8b7a49b63f8fa\",\"partnerName\":\"微信H5充值客户\",\"shorterName\":\"微信H5充值客户\",\"partnerNo\":\"wx_h5_test\",\"loginName\":\"wx_h5_test\",\"realName\":\"微信H5充值客户\",\"mobile\":\"15279368541\",\"email\":\"wxtest@ucpaas.com\",\"address\":\"\",\"partnerType\":1,\"partnerTypeDesc\":\"流量营销\",\"status\":\"1\",\"statusDesc\":\"商用\",\"balance\":882.16,\"creditAmount\":0,\"settlementDiscount\":80,\"coopGroupId\":\"19\",\"isDeleted\":0,\"attachmentFileList\":null,\"partnerProductList\":[],\"currentAmount\":24.8,\"availableCredit\":0,\"orderBillingType\":0,\"orderBillingTypeDesc\":\"否\",\"partnerInfoExt\":{\"partnerId\":6,\"adviserName\":\"微信客户测试\",\"adviserMobile\":\"15086436578\",\"customServiceName\":\"\",\"customServiceMobile\":\"\",\"otherLinkman\":\"\",\"otherMobile\":\"\",\"remark\":\"\"},\"partnerLevel\":5,\"settlementPattern\":1,\"settlementDiscountRatio\":null,\"settlementPatternDesc\":\"按固定折扣\"}},{\"balanceMonthId\":1792,\"partnerId\":6,\"month\":\"201608\",\"payMoney\":0.1,\"balanceStatus\":1,\"balanceMoney\":0.1,\"adjustMoney\":0,\"creator\":\"admin\",\"createTime\":\"2016-09-17 05:33:50\",\"updator\":\"admin\",\"updateTime\":\"2016-09-18 17:23:14\",\"remark\":\"9月，系统自动月结算\",\"startDate\":\"20160801\",\"endDate\":\"20160831\",\"partnerInfo\":{\"creator\":\"陈蕾\",\"createTime\":\"2016-07-05 18:11:31\",\"updator\":\"陈蕾\",\"updateTime\":\"2017-07-27 11:43:32\",\"partnerId\":6,\"identityId\":\"43e04a743ea242308ed8b7a49b63f8fa\",\"partnerName\":\"微信H5充值客户\",\"shorterName\":\"微信H5充值客户\",\"partnerNo\":\"wx_h5_test\",\"loginName\":\"wx_h5_test\",\"realName\":\"微信H5充值客户\",\"mobile\":\"15279368541\",\"email\":\"wxtest@ucpaas.com\",\"address\":\"\",\"partnerType\":1,\"partnerTypeDesc\":\"流量营销\",\"status\":\"1\",\"statusDesc\":\"商用\",\"balance\":882.16,\"creditAmount\":0,\"settlementDiscount\":80,\"coopGroupId\":\"19\",\"isDeleted\":0,\"attachmentFileList\":null,\"partnerProductList\":[],\"currentAmount\":24.8,\"availableCredit\":0,\"orderBillingType\":0,\"orderBillingTypeDesc\":\"否\",\"partnerInfoExt\":{\"partnerId\":6,\"adviserName\":\"微信客户测试\",\"adviserMobile\":\"15086436578\",\"customServiceName\":\"\",\"customServiceMobile\":\"\",\"otherLinkman\":\"\",\"otherMobile\":\"\",\"remark\":\"\"},\"partnerLevel\":5,\"settlementPattern\":1,\"settlementDiscountRatio\":null,\"settlementPatternDesc\":\"按固定折扣\"}}]");
	}

	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ResponseBody
	public Object add(@ModelAttribute("partnerBalanceMonth") PartnerBalanceMonth data) {
		partnerBalanceMonthService.saveAndUpdate(data);
		return SUCCESS_TIP;
	}

	@RequestMapping(value = "/delete")
	@ResponseBody
	public Object delete(Long balanceMonthId) {
		partnerBalanceMonthService.delete(balanceMonthId);
		return SUCCESS_TIP;
	}

	@RequestMapping(value = "/get")
	@ResponseBody
	public Object get(Long balanceMonthId) {
		PartnerBalanceMonth data = partnerBalanceMonthService.get(balanceMonthId);
		return data;
	}

	/**
	 * 更新的时候需额外传递updId,值跟主键值一样,被@ModelAttribute注释的方法会在此controller每个方法执行前被执行，
	 * 要谨慎使用
	 */
	@ModelAttribute("partnerBalanceMonth")
	public void getForUpdate(@RequestParam(value = "updId", required = false) Long updId, Model model) {
		if (null != updId) {
			model.addAttribute("partnerBalanceMonth", partnerBalanceMonthService.get(updId));
		} else {
			model.addAttribute("partnerBalanceMonth", new PartnerBalanceMonth());
		}
	}

	/**
     * 修改结算状态
     * @return
     */
    @RequestMapping(value = "/changeStatus")
    @ResponseBody
    public Object changeStatus(Integer status, Long balanceMonthId) {
        
    	ShiroUser current = ShiroKit.getUser();
        // 更新partner_info表
        PartnerBalanceMonth data = partnerBalanceMonthService.get(balanceMonthId);
        PartnerInfo partnerInfo = partnerService.get(data.getPartnerId());
        partnerInfo.setBalance(partnerInfo.getBalance().subtract(data.getAdjustMoney()));
        partnerInfo.setUpdator(current.getAccount());
        partnerInfo.setUpdateTime(new Date());
        partnerService.update(partnerInfo);
        
        if (status < 0 || status > 1) {
            return new ErrorTip(BizExceptionEnum.OPERATION_ILLEGALE);
        }
        data.setBalanceStatus(status);
        data.setUpdator(current.getAccount());
        data.setUpdateTime(new Date());
        partnerBalanceMonthService.saveAndUpdate(data);
        return SUCCESS_TIP;
    }
    
    /**
     * 调整结算金额
     * @return
     */
    @RequestMapping(value = "/changeAdjustBalance")
    @ResponseBody
    public Object changeAdjustBalance(Long balanceMonthId, BigDecimal changeBalance, String changeReason) {
    	ShiroUser current = ShiroKit.getUser();
        // 更新月结算明细表
        PartnerBalanceMonth data = partnerBalanceMonthService.get(balanceMonthId);
        data.setAdjustMoney(changeBalance);
        data.setRemark(changeReason);
        data.setBalanceStatus(Constant.BALANCE_STATUS_CONFIRM);
        data.setBalanceMoney(data.getBalanceMoney().add(changeBalance));
        data.setUpdator(current.getAccount());
        data.setUpdateTime(new Date());
        partnerBalanceMonthService.savePartnerBalanceMonth(data, current);
        return SUCCESS_TIP;
    }
    
    /**
     * 下载合作伙伴月账单
     * @param response
     */
    @RequestMapping(value = "/downLoadSettlementOrder")
    @ResponseBody
    public void downLoadCustomerBill(HttpServletResponse response, Page<CustomerBalanceDay> page, Long balanceMonthId) {

        PartnerBalanceMonth data = partnerBalanceMonthService.get(balanceMonthId);
        PartnerInfo partnerInfo = partnerService.get(data.getPartnerId());
        String startDateStr = DateUtil.formatDate(data.getStartDate());
        String endDateStr = DateUtil.formatDate(data.getEndDate());
        page.getParams().put("partnerId", data.getPartnerId());
        page.getParams().put("inputStartTime", startDateStr);
        page.getParams().put("inputEndTime", endDateStr);
        page.setRows(50000);
        // 构造数据结构 START
        List<BillsExcel> billsExcels = new ArrayList<BillsExcel>();
        BillsExcel billsExcel = new BillsExcel();
        List<BillsDetail> billsDetails = new ArrayList<BillsDetail>();
        // 取得账单金额
        BigDecimal payMoney = data.getPayMoney();
        // 取得调整金额
        BigDecimal adjustMoney = data.getAdjustMoney();
        // 取得应结算金额
        BigDecimal balanceMoney = data.getBalanceMoney();
        // 取得所有{YD/LT/DX}+{全国/广东/河南...}组合的数据
        billsDetails = genBillsDetailList(data.getPartnerId(), null, page, Constant.OBJ_TYPE_PARTNER);
        
        billsExcel.setPartnerInfo(partnerInfo);
        billsExcel.setPayMoney(payMoney);
        billsExcel.setAdjustMoney(adjustMoney);
        billsExcel.setBalanceMoney(balanceMoney);
        billsExcel.setInputStartTime(startDateStr);
        billsExcel.setInputEndTime(endDateStr);
        billsExcel.setBillsDetails(billsDetails);
        billsExcels.add(billsExcel);
        
        // 生成Excel
        
        Map<String, Object> beanParams = new HashMap<String, Object>();
        beanParams.put("pbdList", billsExcels);
        try {
            TemplateExcelManager.getInstance().createExcelFileAndDownload(TemplateExcel.PARTNER_BALANCE_MONTH, beanParams);
        } catch (Exception e) {
            LOG.error("导出报表出错！！！【"+ e.getMessage() +"】", e);
            throw new BussinessException(BizExceptionEnum.CUSTOMER_FORMAT_ERROR, "导出报表出错！！！【" + e.getMessage() + "】");
        }
    }
    
    public List<BillsDetail> genBillsDetailList(Long partnerId, Long customerId, Page<CustomerBalanceDay> page, String objType) {
        List<BillsDetail> billsDetails = new ArrayList<BillsDetail>();
        String inputStartTime = String.valueOf(page.getParams().get("inputStartTime"));
        String inputEndTime = String.valueOf(page.getParams().get("inputEndTime"));
        // 遍历运营商
        for (String key : Constant.MOBILE_OPERATOR_MAP.keySet()) {
            List<AreaCode> acList = areaCodeService.selectAll(key, partnerId, customerId, inputStartTime, inputEndTime);
            // 遍历区域
            for (AreaCode areaCode : acList) {
                if (areaCode != null) {
                    BillsDetail bd = new BillsDetail();
                    List<CustomerBalanceDay> customerBalanceDays = new ArrayList<CustomerBalanceDay>();
                    BigDecimal cbdBalance = new BigDecimal(0);
                    page.getParams().put("mobileOperator", key);
                    page.getParams().put("zone", areaCode.getAreaCode());
                    if (Constant.OBJ_TYPE_PARTNER.equals(objType)) {
                        customerBalanceDays = customerBalanceDayService.pageQueryPartnerMonthByPartner(page).getDatas();
                    } else if (Constant.OBJ_TYPE_CUSTOMER.equals(objType)) {
                        customerBalanceDays = customerBalanceDayService.pageQueryPartnerMonth(page).getDatas();
                    }
                    cbdBalance = getBalanceTotal(customerBalanceDays, objType);
                    bd.setOperatorCN(Constant.MOBILE_OPERATOR_MAP.get(key));
                    bd.setZoneName(areaCode.getAreaName());
                    bd.setCbdList(customerBalanceDays);
                    bd.setCbdBalance(cbdBalance);
                    billsDetails.add(bd);
                }
            }
        }
        return billsDetails;
    }
    
    public BigDecimal getBalanceTotal(List<CustomerBalanceDay> list, String objType) {
        BigDecimal priceTotal = new BigDecimal(0);
        for (CustomerBalanceDay cbd : list) {
            if (Constant.OBJ_TYPE_PARTNER.equals(objType) && cbd.getPartnerAmount() != null) {
                priceTotal = priceTotal.add(cbd.getPartnerAmount());
            } else if (Constant.OBJ_TYPE_CUSTOMER.equals(objType) && cbd.getCustomerAmount() != null) {
                priceTotal = priceTotal.add(cbd.getCustomerAmount());
            }
        }
        return priceTotal;
    }
}