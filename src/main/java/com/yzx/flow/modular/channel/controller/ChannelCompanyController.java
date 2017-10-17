package com.yzx.flow.modular.channel.controller;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.annotations.Param;
import org.apache.log4j.Logger;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yzx.flow.common.constant.tips.ErrorTip;
import com.yzx.flow.common.constant.tips.SuccessTip;
import com.yzx.flow.common.controller.BaseController;
import com.yzx.flow.common.excel.TemplateExcel;
import com.yzx.flow.common.excel.TemplateExcelManager;
import com.yzx.flow.common.exception.BizExceptionEnum;
import com.yzx.flow.common.exception.BussinessException;
import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.page.PageInfoBT;
import com.yzx.flow.common.persistence.dao.ChannelCompanyMapper;
import com.yzx.flow.common.persistence.model.ChannelCompany;
import com.yzx.flow.common.persistence.model.Staff;
import com.yzx.flow.common.util.FileDownLoadUtils;
import com.yzx.flow.core.shiro.ShiroKit;
import com.yzx.flow.core.shiro.ShiroUser;
import com.yzx.flow.core.util.ToolUtil;
import com.yzx.flow.modular.channel.service.IChannelCompanyService;

/**
 * 上游供应商控制器
 *
 * @author liuyufeng
 * @Date 2017-08-11 17:14:56
 */
@Controller
@RequestMapping("/channelCompany")
public class ChannelCompanyController extends BaseController {
	
	private static Logger logger = Logger.getLogger(ChannelCompanyController.class);

    private String PREFIX = "/channel/channelCompany/";
    
    @Autowired
    private IChannelCompanyService channelCompanyService;
    
    @Autowired
    private ChannelCompanyMapper channelCompanyMapper;
    
    @Resource(name = "queueTemplate")
	private RabbitTemplate queueTemplate;

    /**
     * 跳转到上游供应商首页
     */
    @RequestMapping("")
    public String index() {
        return PREFIX + "channelCompany.html";
    }

    /**
     * 跳转到添加上游供应商
     */
    @RequestMapping("/channelCompany_add")
    public String channelCompanyAdd() {
        return PREFIX + "channelCompany_add.html";
    }

    /**
     * 跳转到修改上游供应商
     */
    @RequestMapping("/channelCompany_update/{channelCompanyId}")
    public String channelCompanyUpdate(@PathVariable Integer channelCompanyId, Model model) {
        ChannelCompany channelCompany = channelCompanyService.get(channelCompanyId);
        model.addAttribute("channelCompany",channelCompany);
        return PREFIX + "channelCompany_edit.html";
    }

    /**
     * 获取上游供应商列表
     */
    @RequestMapping(value = "/list")
    @ResponseBody
    public  PageInfoBT<ChannelCompany> list(Page<ChannelCompany> page) {
    	if (page.getOrder()==null) {
			page.setOrder("DESC");
		}
    	if (page.getSort()==null) {
			page.setSort("create_time");
		}
        PageInfoBT<ChannelCompany> resPage = channelCompanyService.pageQuery(page);
		return resPage;
    }

    /**
     * 新增上游供应商
     */
    @RequestMapping(value = "/add")
    @ResponseBody
    public Object add(ChannelCompany channelCompany) {
    	ShiroUser user = ShiroKit.getUser();
    	Staff staff = new Staff();
    	staff.setLoginName(user.getName());
    	channelCompanyService.saveAndUpdate(channelCompany, staff,channelCompany.getCompanyId()==0?"":channelCompany.getCompanyId()+"");
        return SUCCESS_TIP;
    }

    /**
     * 删除上游供应商
     */
    @RequestMapping(value = "/delete")
    @ResponseBody
    public Object delete(long companyId) {
		// 查询是否有供应商绑定了这个上游
		int count = channelCompanyService.getChannelSuppliersById(companyId);
		if (count > 0) {
			throw new BussinessException(BizExceptionEnum.CHANNEL_COMPANY_BUILD);
		}
		channelCompanyService.delete(companyId);
		return SUCCESS_TIP;
    }


    /**
     * 修改上游供应商
     */
    @RequestMapping(value = "/update")
    @ResponseBody
    public Object update( ChannelCompany channelCompany,@RequestParam(value = "updId", required = false) String updaId) {
    	ShiroUser user = ShiroKit.getUser();
    	Staff staff = new Staff();
    	staff.setLoginName(user.getName());
    	channelCompanyService.saveAndUpdate(channelCompany, staff,updaId);
        return SUCCESS_TIP;
    }

    /**
	 * 取出通道供应商信息
	 * 
	 * @return
	 */
	@RequestMapping(value = "/selectCompanyByName")
	@ResponseBody
	public Map<String, Object> selectChannelSupplierByName(String companyName) {
		Map<String, Object> map = new HashMap<String, Object>();
		List<ChannelCompany> list = new ArrayList<ChannelCompany>();
		list = channelCompanyService.selectChannelCompanyByName(companyName);
		map.put("channelCompanyList", list);
		return map;
	}

	/*
	 * 查看
	 */
	@RequestMapping(value = "/get")
	@ResponseBody
	public Map<String, Object> get(@RequestParam(name="companyId") String companyId) {
		Map<String, Object> resMap=new HashMap<String, Object>();
		if (ToolUtil.isEmpty(companyId)) {
			throw new  BussinessException(BizExceptionEnum.REQUEST_INVALIDATE);
		}
		try {
			ChannelCompany data = channelCompanyService.get(Long.parseLong(companyId));
			/*BigDecimal balance = data.getBalance();
			BigDecimal unCheckPrice = channelCompanyService.getPriceByCpId(Long.parseLong(companyId));
			data.setUnCheckPrice(unCheckPrice);
			BigDecimal unCheckMoney;
			BigDecimal allowPrice;
			BigDecimal unCheckPrice = channelCompanyService.getPriceByCpId(companyId);
			data.setUnCheckPrice(unCheckPrice);*/
			/**
			 * 暂时不查询可用余额以及未确认金额
			 */
			/**
			Map<String,BigDecimal> map = new HashMap<String,BigDecimal>();
			BigDecimal c = new BigDecimal(companyId);
			map.put("companyId", c);
			channelCompanyService.getAllowPrice(map);
			if(null == map.get("allowPrice") || "".equals(map.get("allowPrice"))){
				allowPrice = new BigDecimal( 0.00);
			}else{
				allowPrice = map.get("allowPrice");
			}
			if(null == map.get("unCheckMoney") || "".equals(map.get("unCheckMoney"))){
				unCheckMoney = new BigDecimal( 0.00);
			}else{
				unCheckMoney = map.get("unCheckMoney");
			}
			data.setUnCheckPrice(unCheckMoney);
			data.setAllowBalance(allowPrice); 
			AttachmentInterface attachmentInterface = SpringContextHolder
					.getBean("attachmentInterface");
			if (StringTools.isNotEmptyString(data.getContractFile())) {
				attachmentInterface
						.formalAttachmentGroup(data.getContractFile());
				List<AttachmentFile> smAttachmentFileList = attachmentInterface
						.listAttachmentFile(data.getContractFile());
				data.setAttachmentFileList(smAttachmentFileList);
			}*/
			resMap.put("data", data);
			resMap.put("success", Boolean.valueOf(true));
		} catch (Exception e) {
			e.printStackTrace();
			resMap.put("message", "参数错误");
			resMap.put("success", Boolean.valueOf(false));
		}
		return resMap;
	}

	@RequestMapping(value = "/sendSms")
	@ResponseBody
	public Object sendSms() {
		Staff staff = ShiroKit.getStaff();
		if (null == staff) {
			return new ErrorTip(0,"用户未登录，不能发送邮件");
		}
		Calendar chargeTime = Calendar.getInstance();
		chargeTime.add(Calendar.DATE, -1);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		queueTemplate.convertAndSend("{\"type\":2,\"statisticsDate\":\""+format.format(chargeTime.getTime())+"\",\"operater\":\""+staff.getRealName()+"\"}");
		queueTemplate.convertAndSend("{\"type\":1,\"statisticsDate\":\""+format.format(chargeTime.getTime())+"\",\"operater\":\""+staff.getRealName()+"\"}");
		return SUCCESS_TIP;
	}
	/**
	 * 导出供应商列表数据
	 * @param page
	 * @return
	 */
	@RequestMapping(value = "/export")
	public void export(HttpServletRequest request,HttpServletResponse response,Page<ChannelCompany> page) {
		try {
			  // 生成Excel
			  Map<String, Object> beanMap = new HashMap<String, Object>();
			  page.setRows(10000);
			  beanMap.put("list", channelCompanyService.pageQuery(page).getRows());
			  TemplateExcelManager.getInstance().createExcelFileAndDownload(TemplateExcel.CHANNEL_COMPANY, beanMap);
		} catch (Exception e) {
			logger.error("导出报表出错！！！", e);
		}
	}
	
	/**
	 * 校验上游供应商名称是否可用
	 * @param channelCompany
	 */
	@RequestMapping("/checkCompanyCode")
	@ResponseBody
	public Object checkCompanyCode(ChannelCompany company){
		if (ToolUtil.isEmpty(company.getCompanyCode())) {
			 return new ErrorTip(BizExceptionEnum.REQUEST_NULL);
		}
	   List<ChannelCompany>	list = channelCompanyService.checkCompanyByCode(company.getCompanyCode());
	   boolean flag = checkIsUsed(list, company);
       if (flag) {
			return new ErrorTip(0, "上游供应商代码不可用") ;
		}
		return new SuccessTip("上游供应商代码可用");
	}
	/**
	 * 校验上游供应商是否可用
	 * @param company
	 * @return
	 */
	@RequestMapping("/checkCompanyName")
	@ResponseBody
	public Object checkCompanyName(ChannelCompany company){
		if (ToolUtil.isEmpty(company.getCompanyName())) {
			 return new ErrorTip(BizExceptionEnum.REQUEST_NULL);
		}
	   List<ChannelCompany>	list = channelCompanyService.checkCompanyByName(company.getCompanyName());;
	   boolean flag = checkIsUsed(list, company);
		if (flag) { 
			return new ErrorTip(0, "上游供应商名称不可用");
		}
		return  new SuccessTip("上游供应商名称可用");
	}
	
	
	private boolean checkIsUsed(List<ChannelCompany> list,ChannelCompany checkData){
		  if ((list!=null) && (!list.isEmpty())) {
				for (int i = 0; i < list.size(); i++) {
					if(list.get(i).getCompanyId()!=checkData.getCompanyId())
						return true;
				}
			}
		  return false;
		}
	
}
