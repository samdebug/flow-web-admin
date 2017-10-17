package com.yzx.flow.modular.channel.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.yzx.flow.common.controller.BaseController;
import com.yzx.flow.common.excel.TemplateExcel;
import com.yzx.flow.common.excel.TemplateExcelManager;
import com.yzx.flow.common.exception.BizExceptionEnum;
import com.yzx.flow.common.exception.BussinessException;
import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.page.PageInfoBT;
import com.yzx.flow.common.persistence.model.ChannelCompanyRecharge;
import com.yzx.flow.common.persistence.model.Staff;
import com.yzx.flow.common.util.CommonUtil;
import com.yzx.flow.core.shiro.ShiroKit;
import com.yzx.flow.core.shiro.ShiroUser;
import com.yzx.flow.core.util.ToolUtil;
import com.yzx.flow.modular.channel.service.IChannelCompanyRechargeService;
import com.yzx.flow.modular.channel.service.IChannelCompanyService;
import com.yzx.flow.modular.system.dao.ChannelCompanyDao;

/**
 * 佛年供应商充值记录
 *
 */
@Controller
@RequestMapping("/channelCompanyRecharge")
public class ChannelCompanyRechargeController extends BaseController {
	private static final Logger LOG = LoggerFactory.getLogger(ChannelCompanyRechargeController.class);
	@Autowired
	@Qualifier("channelCompanyRechargeService")
	private IChannelCompanyRechargeService channelCompanyRechargeService;

	@Autowired
	private IChannelCompanyService channelCompanyService;
	
	@Autowired
	private ChannelCompanyDao channelCompanyMapper;

	
	  private String PREFIX = "/channel/channelCompanyRecharge/";

	    /**
	     * 跳转到充值明细首页
	     */
	    @RequestMapping("")
	    public String index(@RequestParam String companyId,Model model) {
	    	model.addAttribute("companyId", companyId);
	        return PREFIX + "channelCompanyRecharge_view.html";
	    }
	
	
	@RequestMapping(value = "/query")
	@ResponseBody
	public PageInfoBT<ChannelCompanyRecharge> pageQuery(Page<ChannelCompanyRecharge> page, String companyId) {
		LOG.info("分页查询充值记录,params={}", page.getParams());
		if ( StringUtils.isBlank(page.getSort()) ) {
			page.setSort("input_time");
		}
		page.getParams().put("companyId", companyId);
		PageInfoBT<ChannelCompanyRecharge> resPage = channelCompanyRechargeService.pageQuery(page);
		return resPage;
	}

	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ResponseBody
	public Object add(@RequestBody ChannelCompanyRecharge data, HttpServletRequest request) {
		ShiroUser user =ShiroKit.getUser();
		Staff staff = new Staff();
		staff.setLoginName(user.getName());
		channelCompanyRechargeService.saveSupplierRecharge(data, staff, CommonUtil.getIp(request));
		channelCompanyRechargeService.repairOldData(data);// 修复数据
		return SUCCESS_TIP;
	}

	@RequestMapping(value = "/delete")
	@ResponseBody
	public Object delete(@RequestParam() String companyRechargeId) {
		if (ToolUtil.isEmpty(companyRechargeId)) {
			throw new BussinessException(BizExceptionEnum.REQUEST_INVALIDATE);
		}
		channelCompanyRechargeService.delete(Long.parseLong(companyRechargeId));
		return SUCCESS_TIP;
	}

	@RequestMapping(value = "/get")
	@ResponseBody
	public Map<String, Object> get(Long companyRechargeId) {
		ChannelCompanyRecharge data = channelCompanyRechargeService.get(companyRechargeId);
		Map<String, Object> resMap=new HashMap<String, Object>();
		resMap.put("data", data);
		resMap.put("success", Boolean.valueOf(true));
		return resMap;
	}

	

	/**
	 * 报表导出
	 * 
	 * @param response
	 * @param page
	 * @throws Exception
	 */
	@RequestMapping(value = "/export")
	public void export(Page<ChannelCompanyRecharge> page) throws Exception {
		try {
			// 生成Excel
			Map<String, Object> beanMap = new HashMap<String, Object>();
			page.setRows(10000);
			beanMap.put("list", channelCompanyRechargeService.pageQuery(page).getRows());
			TemplateExcelManager.getInstance().createExcelFileAndDownload(TemplateExcel.CHANNEL_COMPANY_RECHARGE, beanMap);
		} catch (Exception e) {
			LOG.error("导出报表出错！！！", e);
		}
	}

	/**
	 * 批量上传充值文件
	 * @throws UnknownHostException 
	 *//*
	@RequestMapping(value = "/uploadFile")
	@ResponseBody
	public Map<String, Object> uploadFile(@RequestParam(value = "uploadFile", required = false) MultipartFile file,
			HttpServletRequest request, @RequestParam(value = "updId", required = false) String updaId) throws UnknownHostException {
		int maxSize = 4 * 1024 * 1024;
		SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
		Date date = new Date();
		String now = sf.format(date);
		InetAddress address = InetAddress.getLocalHost();//获取的是本地的IP地址 //PC-20140317PXKX/192.168.0.121
		String hostAddress = address.getHostAddress();//192.168.0.121           
		try {
			Staff staff = getCurrentLogin();
			if (null == staff) {
				return super.fail("非法请求");
			}
			*//**
			 * 读取上传的批量文件
			 *//*
			String filename = file.getOriginalFilename();
			// 项目中tomcat/upload目录
			String path = System.getProperty("catalina.home");
			path = path + "/upload/batchRecharge";

//			String path = SystemConfig.getInstance().getConfigPathTemp();
			// 后缀
			String suffix = filename.substring(filename.length() - 4, filename.length()).toLowerCase();
			if (!(".xls").equals(suffix)) {
				return super.fail("上传的文件格式必须为.xls ");
			}
			// filename = now +"-"+ staff.getLoginName();
			filename = now+staff.getLoginName().concat(suffix);
			String filepath = path + File.separator + filename;
			// 写入文件
			File oldFile = null;
			File f = null;
			List<ChannelCompanyRecharge> list = new ArrayList<ChannelCompanyRecharge>();
			try {
				oldFile = new File(path);
				if (!oldFile.exists()) {
					oldFile.mkdirs();
				}
				f = new File(path + File.separator + filename);
				if (f.length() > maxSize) {
					return super.fail("上传的文件大小不能超过4M!");
				}
				if (f.exists()) {
					f.delete();
				}
				InputStream is = file.getInputStream();
				OutputStream os = new FileOutputStream(path + File.separator + filename);
				int read = 0;
				byte[] bytes = new byte[1024];
				while ((read = is.read(bytes, 0, 1024)) != -1) {

					os.write(bytes, 0, read);
				}
				os.close();
				is.close();
				ReadExcelUtils readExcelUtils = new ReadExcelUtils(filepath);// 分析文件的格式是否正确
				Map<Integer, Map<Integer, Object>> map = readExcelUtils.readExcelContent();
				if (map.isEmpty()) {
					return super.fail("没有需要批量充值的记录!");
				}
				LOG.info("获得Excel表格的内容:");
				for (int i = 1; i <= map.size(); i++) {
					int j = i-1;
					ChannelCompanyRecharge ccp = new ChannelCompanyRecharge();
					if (null == map.get(j).get(0) || null == map.get(j).get(1) || null == map.get(j).get(2)
							|| null == map.get(j).get(3) || "".equals(map.get(j).get(0)) || "".equals(map.get(j).get(1))
							|| "".equals(map.get(j).get(2)) || "".equals(map.get(j).get(3))) {
						return super.fail("导入失败,导入的数据中除备注外不允许空!");
					}
					ccp.setChargeTime(now);
					String companyCode = (String) map.get(j).get(0);
					String companyName = (String) map.get(j).get(1);
					if(companyCode.indexOf(".") !=-1){
						companyCode = companyCode.substring(0, companyCode.indexOf("."));
					}
					ChannelCompany data = channelCompanyService.getChannelCompany(companyCode.trim(),companyName.trim());
					if (null == data) {
						return super.fail("导入失败,导入的供应商编码:" + companyCode + ",供应商名称:"+companyName+"不存在!");
					}
					ccp.setCompanyId(Long.valueOf(data.getCompanyId()));
					ccp.setMoney(new BigDecimal((String) map.get(j).get(3)));
					String type = (String) map.get(j).get(2);
					if (type.equals("充值")) {
						ccp.setType("0");
						ccp.setTypeDesc("充值");
						// 累积充值
						if(null == data.getRechargeAmount() || "".equals(data.getRechargeAmount())){
							data.setRechargeAmount(new BigDecimal(0));
						}
						data.setRechargeAmount(data.getRechargeAmount().add(ccp.getMoney()));
						// 统计余额
						if(null == data.getStaticBalance() || "".equals(data.getStaticBalance())){
							data.setStaticBalance(new BigDecimal(0));
						}
						data.setStaticBalance(data.getStaticBalance().add(ccp.getMoney()));
						data.setUpdator(staff.getLoginName());
						channelCompanyMapper.updateBalanceByPrimaryKey(data);
					} else {
						ccp.setType("1");
						ccp.setTypeDesc("冲账");
					}
					ccp.setRemark(map.get(j).get(4).toString());
					ccp.setLoginName(staff.getLoginName());
					ccp.setOperatorName(staff.getLoginName());
					ccp.setInputTime(date);
					ccp.setOperateIp(hostAddress);
					list.add(ccp);
				}
				int result = channelCompanyRechargeService.batchSave(list);
				if (result > 0) {
					return super.success("导入成功,共新增" + map.size() + "条充值记录");
				} else {
					return super.success("没有需要批量充值的记录!");
				}
			} catch (Exception e) {
				e.printStackTrace();
				LOG.info(e.getMessage(), e);
				return super.fail("导入失败!");
			}
		} catch (MyException e) {
			e.printStackTrace();
			LOG.error(e.getMessage(), e);
			return super.fail("导入失败!");
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e.getMessage(), e);
			return super.fail("导入失败!");
		}
	}

	*//**
	 * 导出充值模板文件
	 *//*
	@RequestMapping(value = "/exportChargeTemplateFile")
	public void downLoadChargeOrder(HttpServletResponse response) throws IOException {
		try {
			String txtUrl = SystemConfig.getInstance().getConfigPath();
			this.downloadExcelFile(response, new File(txtUrl + "batchRecharge.xls"));
		} catch (IOException e) {
			e.printStackTrace();
			LOG.error("导出文件出错：" + e.getMessage(), e);
		}
	}*/
}