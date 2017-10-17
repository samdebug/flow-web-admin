package com.yzx.flow.modular.portal;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.yzx.flow.common.annotion.PortalCustomer;
import com.yzx.flow.common.constant.tips.ErrorTip;
import com.yzx.flow.common.constant.tips.Tip;
import com.yzx.flow.common.controller.BaseController;
import com.yzx.flow.common.excel.TemplateExcel;
import com.yzx.flow.common.excel.TemplateExcelManager;
import com.yzx.flow.common.exception.BizExceptionEnum;
import com.yzx.flow.common.exception.BussinessException;
import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.page.PageInfoBT;
import com.yzx.flow.common.persistence.model.FileUploadRecord;
import com.yzx.flow.common.util.Constant;
import com.yzx.flow.core.portal.PortalParamSetter;
import com.yzx.flow.core.portal.annotation.AutoSetPortalCustomer;
import com.yzx.flow.core.portal.annotation.PortalParamMeta;
import com.yzx.flow.core.util.CheckPhone;
import com.yzx.flow.core.util.ToolUtil;
import com.yzx.flow.core.util.download.DownloadUtil;
import com.yzx.flow.modular.customer.service.ICustomerInfoService;
import com.yzx.flow.modular.portal.service.IFileUploadRecordService;

@Controller
@RequestMapping("/fileUploadRecord")
public class FileUploadRecordController extends BaseController {
    private static final Logger LOG = LoggerFactory.getLogger(FileUploadRecordController.class);
    
    @Autowired
    private IFileUploadRecordService fileUploadRecordService;

    @Autowired
    private ICustomerInfoService customerInfoService;
    
//    @Autowired
//    private AttachmentInterface attachmentInterface;
    
//    @Autowired
//    private SmsTmplService smsTmplService;
    
    @PortalCustomer
    @RequestMapping(value = "/query")
    @ResponseBody
    @AutoSetPortalCustomer({@PortalParamMeta(setter = PortalParamSetter.PAGE)})
    public Object pageQuery(Page<FileUploadRecord> page) {
    	if ( StringUtils.isBlank(page.getSort()) ) 
    		page.setSort("create_time");
    		
        Page<FileUploadRecord> list = fileUploadRecordService.pageQuery(page);
        return new PageInfoBT<>(list.getDatas(), list.getTotal());
    }
    
    
    /**
     * 解析excel中的mobile
     * @return
     */
    @PortalCustomer
    @RequestMapping(value = "/resolveMobile", method = RequestMethod.POST)
    @ResponseBody
    public Object resolveExcelMobile(@RequestParam("mobileList")MultipartFile file) {
    	if ( file == null ) 
    		return ErrorTip.buildErrorTip(BizExceptionEnum.REQUEST_NULL);
    	
    	if ( !file.getOriginalFilename().toLowerCase().endsWith(".xls") ) 
    		return ErrorTip.buildErrorTip(BizExceptionEnum.REQUEST_INVALIDATE); 
    	
    	try {
    		if ( file.getBytes().length > 2 * 1024 * 1024 ) 
        		return ErrorTip.buildErrorTip(BizExceptionEnum.CUSTOMER_FORMAT_ERROR, "文件大小超过限制（2MB）"); 
    		
    		List<String> mobiles = getExcelDataByFileInputStream(file.getInputStream());
    		if (mobiles.isEmpty()) 
				return ErrorTip.buildErrorTip(BizExceptionEnum.CUSTOMER_FORMAT_ERROR, "模板文件内容为空，请重新上传");
			
			if (mobiles.size() > 1000) 
				return ErrorTip.buildErrorTip(BizExceptionEnum.CUSTOMER_FORMAT_ERROR, "每次充值最大不能超过1000条，请重新上传");
			
			Map<String, Object> res = countMobileOfOperator(mobiles);
			res.put("code", Tip.CODE_SUCCESS);
    		return res;
    	} catch ( Exception e ) {
    		LOG.error("解析手机号码Excel异常", e);
    	}
    	return ErrorTip.buildErrorTip(BizExceptionEnum.CUSTOMER_FORMAT_ERROR, "解析文件出错");
    }
    
    /** 移动手机号码数 */
    private static final String FIELD_YD_COUNT = "YDCount";
    /** 电信手机号码数 */
    private static final String FIELD_DX_COUNT = "DXCount";
    /** 联通手机号码数 */
    private static final String FIELD_LT_COUNT = "LTCount";
    /** 错误手机号码列表 */
    private static final String FIELD_ERROR_MOBILES = "errorMobiles";
    /** 错误手机号码统计 */
    private static final String FIELD_ERROR_MOBILES_COUNT = "errorMobilesCount";
    /** 有效手机号码列表 */
    private static final String FIELD_MOBILES = "mobiles";
    /** 待校验值 */
    private static final String FIELD_CHECK = "checkStr";
    /** 随机字符 */
    private static final String FIELD_NONCESTR = "nonceStr";
    /** 校验字段 */
    private static final String FIELD_SIGN = "sign";
    /** 一个简单的凭证key */
    public static String FLOR_RECHARGE_MOBILES_KEY;
    
    
    private Map<String, Object> countMobileOfOperator(List<String> mobiles) {
    	List<String> errorMobiles = new ArrayList<>();
    	
    	int YDCount = 0;
    	int DXCount = 0;
    	int LTCount = 0;
    	
    	for ( String mobile : mobiles ) {
    		
    		if (StringUtils.isEmpty(mobile.trim())) continue;
			
			if (!CheckPhone.isMobileNO(mobile.trim())) {
				errorMobiles.add(mobile);
				continue;
			}
			
			String operator = CheckPhone.getMobileOpr(mobile);
			switch (operator) {
				case Constant.OPERATOR_YD:
					YDCount++;
					break;
				case Constant.OPERATOR_DX:
					DXCount++;
					break;
				case Constant.OPERATOR_LT:
					LTCount++;
					break;
				default:
					break;
			}
    	}
    	
    	Map<String, Object> res = new HashMap<>();
    	res.put(FIELD_YD_COUNT, YDCount);
    	res.put(FIELD_DX_COUNT, DXCount);
    	res.put(FIELD_LT_COUNT, LTCount);
    	res.put(FIELD_ERROR_MOBILES, errorMobiles);
    	res.put(FIELD_ERROR_MOBILES_COUNT, errorMobiles.size());
    	
    	if ( !errorMobiles.isEmpty() )
    		mobiles.removeAll(errorMobiles);
    	
		res.put(FIELD_MOBILES, mobiles);
		// 弄一些简单的校验信息
		String nonceStr = ToolUtil.getRandomNum();
		res.put(FIELD_NONCESTR, nonceStr);
		res.put(FIELD_CHECK, internalConvert2String(mobiles));
		res.put(FIELD_SIGN, ToolUtil.buildSignStr(res.get(FIELD_CHECK).toString(), nonceStr, FLOR_RECHARGE_MOBILES_KEY));
    	return res;
    }
    
    private String internalConvert2String(List<String> mobiles) {
    	if ( mobiles == null || mobiles.isEmpty() )
    		return "";
    	
    	StringBuilder str = new StringBuilder();
    	for (String m : mobiles) {
    		str.append(m).append(",");
    	}
    	str.deleteCharAt(str.length() -1);// delete the last ,
    	String res = str.toString();
    	str.setLength(0);// clear
    	return res;
    }

//    @RequestMapping(value = "/add.ajax", method = RequestMethod.POST)
//    @ResponseBody
//    public Map<String, Object> add(@ModelAttribute("fileUploadRecord") FileUploadRecord data,@RequestParam(required=false)String sendTime) {
//        Staff staff=getCurrentLogin();
//        CustomerInfo customerInfo = getCurrentCustomer();
//        if (customerInfo == null) {
//            return fail("非法客户");
//        }
//        // 读取文件并验证
//        if(StringUtils.isNotBlank(data.getFileGroupId())){
//            List<AttachmentFile> smAttachmentFileList1 = attachmentInterface.listAttachmentFile(data.getFileGroupId());
//            if (smAttachmentFileList1 == null || smAttachmentFileList1.isEmpty()) {
//                LOG.error("上传文件异常：smAttachmentFileList1 == null || smAttachmentFileList1.isEmpty()");
//                return fail("上传文件异常");
//            }
//            AttachmentFile af = smAttachmentFileList1.get(0);
//            // 批量充流量 & 批量下发卡密
//            if (!af.getFileName().endsWith(".xls")) {
//                return fail("请上传合法的模板文件");
//            }
//            try {
//                String uploadPath = AttachmentConfig.getInstance().getUploadPath();
//                // 批量充流量 & 批量下发卡密
//                HSSFSheet sh = getHSSFSheet(new FileInputStream(uploadPath.substring(uploadPath.indexOf("://") + 3,
//                        uploadPath.length()) + af.getFileSaveName()));
//                int rows = 0; // 总行数
//                // 批量下发卡密
//                if (Constant.SOURCE_TYPE_BATCH_SMS.equals(data.getSourceType())) {
//                    rows = sh.getLastRowNum() + 1; // 总行数(RowNum从0开始)
//                } else { // 批量充流量
//                    rows = sh.getLastRowNum(); // 总行数(除去第一行的标题)
//                }
//                if (rows == 0 || isEmptyExcel(sh, rows)) {
//                    return fail("模板文件内容为空，请重新上传");
//                }
//                if (rows > 1000) {
//                    return fail("每次下发最多不能超过1000条，请重新上传");
//                }
//                data.setRowNum(rows);
//            } catch (Exception e) {
//                LOG.error("读取文件出错：" + e.getMessage(), e);
//                return fail("读取文件出错，请确认使用了正确的模板");
//            }
//            data.setFileName(af.getFileName());
//            data.setFileSavePath(af.getFileSaveName());
//            attachmentInterface.formalAttachmentGroup(data.getFileGroupId());
//        }
//        
//        setBaseEntity(data, staff, data.getFileId());
//        data.setCustomerId(customerInfo.getCustomerId());
//        data.setIsValid(Constant.IS_NOT_VALID);
//        if(Constant.SOURCE_TYPE_BATCH_SMS.equals(data.getSourceType())){
//        	data.setSendTime(DateUtil.getDate(sendTime, DateUtil.YYYY_MM_DD_HH_MM_SS));
//        	data.setIsValid(Constant.IS_VALID);
//            fileUploadRecordService.saveAndUpdate(data);
//        }
//        return super.success(data);
//    }
//    
//    private boolean isEmptyExcel(HSSFSheet sh, int rows) {
//        boolean isEmptyExcel = true;
//        for (int i = 0; i < rows; i++ ) {
//            HSSFRow row = sh.getRow(i);
//            if (null == row) {
//                continue;
//            }
//            HSSFCell mobileInfo =  row.getCell(0);
//            HSSFCell flowAmountInfo =  row.getCell(1);
//            HSSFCell cardPasswordInfo =  row.getCell(2);
//            // 有一列不为空，就返回false
//            if (StringUtils.isNotEmpty(mobileInfo.toString().trim()) || StringUtils.isNotEmpty(flowAmountInfo.toString().trim()) 
//                    || StringUtils.isNotEmpty(cardPasswordInfo.toString().trim())) {
//                isEmptyExcel = false;
//                break;
//            }
//        }
//        return isEmptyExcel;
//    }
    
    /**
	 * 根据流路径得到excel文件内容
	 * 
	 * @param excelIS
	 * @return
	 */
	private static List<String> getExcelDataByFileInputStream(InputStream excelIS) {
		List<String> excelDataList = new ArrayList<String>(); // 装每一行的值
		Workbook workBook = null;
		try {
			workBook = new HSSFWorkbook(excelIS);
			// 得到book第一个工作薄sheet
			HSSFSheet sh = (HSSFSheet) workBook.getSheetAt(0);
			int rows = sh.getLastRowNum(); // 总行数
			for (int i = 1; i <=rows; i++) {
				HSSFRow row = sh.getRow(i);
				if (null == row) {
					continue;
				}
				HSSFCell mobileInfo = row.getCell(0);
				mobileInfo.setCellType(HSSFCell.CELL_TYPE_STRING);
				String excelData = mobileInfo.getStringCellValue();
				
				if (StringUtils.isNotBlank(excelData) && !excelDataList.contains(excelData) )// 排重
					excelDataList.add(excelData);
			}
		} catch (FileNotFoundException e) {
			LOG.error(e.getMessage(), e);
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
		return excelDataList;
	}
    
    
    /**
     * 导出充值模板文件
     */
	@PortalCustomer
    @RequestMapping(value = "/exportTemplateFile")
    public void downLoadOrder() {
        try {
            String txtUrl = TemplateExcelManager.getInstance().getTemplatePath(TemplateExcel.RECHARGE_MOBILE_TEMPLATE);
            DownloadUtil.downloadExcelFile(TemplateExcel.RECHARGE_MOBILE_TEMPLATE.getDisplayName(), new File(txtUrl), false);
        } catch (Exception e) {
            LOG.error("导出文件出错：" + e.getMessage(), e);
            throw new BussinessException(BizExceptionEnum.CUSTOMER_FORMAT_ERROR, "导出文件出错：" + e.getMessage());
        }
    }
//    
//    /**
//     * 导出短信模板文件
//     */
//    @RequestMapping(value = "/exportSmsTemplateFile.ajax")
//    public void downLoadSmsOrder(HttpServletResponse response) throws IOException {
//        try {
//            String txtUrl = SystemConfigs.getInstance().getConfigPath();
//            this.downloadExcelFile(response, new File(txtUrl + "sms-template.xls"));
//        } catch (IOException e) {
//            LOG.error("导出文件出错：" + e.getMessage(), e);
//        }
//    }
//
//    @RequestMapping(value = "/deleteSms.ajax")
//    @ResponseBody
//    public Map<String, Object> deleteSms(Long fileId) {
//    	FileUploadRecord fileUploadRecord=fileUploadRecordService.selectByPrimaryKeySms(fileId);
//    	if(fileUploadRecord!=null){
//    		return super.fail("当前批次卡密已下发或下发中，不能删除");
//    	}
//        fileUploadRecordService.delete(fileId);
//        return super.success("删除成功");
//    }
//    
//    @RequestMapping(value="/getSmsTmpl.ajax")
//    @ResponseBody
//    public Map<String, Object> getSmsTmpl(){
//    	 Staff staff=getCurrentLogin();
//         if(staff==null){
//             LOG.debug("当前没有登录用户");
//             return fail("当前没有登录用户");
//         }
//         String account = staff.getLoginName();
//         if (StringUtils.isBlank(account)) {
//             LOG.error("登录账号异常");
//             return fail("登录账号异常");
//         }
//         List<CustomerInfo> customerInfoList = customerInfoService.getAccount(account);
//         if (customerInfoList == null || customerInfoList.size() != 1) {
//             LOG.error("登录账号[{}]异常", account);
//             return fail("登录账号异常");
//         }
//         CustomerInfo customerInfo = customerInfoList.get(0);
//        List<SmsTmpl> smsTmplList= smsTmplService.getSmsTmpl(customerInfo.getCustomerId());
//        return super.success(smsTmplList);
//    }
    
    @PortalCustomer
    @RequestMapping(value = "/delete")
    @ResponseBody
    public Object delete(Long fileId) {
        fileUploadRecordService.delete(fileId);
        return SUCCESS_TIP;
    }
//
//    @RequestMapping(value = "/get.ajax")
//    @ResponseBody
//    public Map<String, Object> get(Long fileId) {
//        FileUploadRecord data = fileUploadRecordService.get(fileId);
//        return super.success(data);
//    }
//
//    /**
//     * 更新的时候需额外传递updId,值跟主键值一样,被@ModelAttribute注释的方法会在此controller每个方法执行前被执行，要谨慎使用
//     */
//    @ModelAttribute("fileUploadRecord")
//    public void getForUpdate(@RequestParam(value = "updId", required = false) Long updId, Model model) {
//        if (null != updId) {
//            model.addAttribute("fileUploadRecord", fileUploadRecordService.get(updId));
//        } else {
//            model.addAttribute("fileUploadRecord", new FileUploadRecord());
//        }
//    }
//    
//    /**
//     * 获取当前客户
//     */
//    private CustomerInfo getCurrentCustomer() {
//        CustomerInfo customerInfo = new CustomerInfo();
//        Staff staff=getCurrentLogin();
//        if(staff == null) {
//            LOG.debug("当前没有登录用户");
//            return null;
//        }
//        String account = staff.getLoginName();
//        if (StringUtils.isBlank(account)) {
//            LOG.error("登录账号异常");
//            return null;
//        }
//        customerInfo = customerInfoService.findCustomerInfoByAccount(account);
//        return customerInfo;
//    }
//    
//    public HSSFSheet getHSSFSheet(InputStream excelIS) {
//        HSSFSheet sh = null;
//        try {
//            Workbook workBook = new HSSFWorkbook(excelIS);
//            // 得到book第一个工作薄sheet
//            sh = (HSSFSheet)workBook.getSheetAt(0);
//        } catch (FileNotFoundException e) {
//            LOG.error(e.getMessage(), e);
//        } catch (IOException e) {
//            LOG.error(e.getMessage(), e);
//        }
//        return sh;
//    }
    
    //  注入一些静态资源
    @Value("${flow.recharge.mobiles.key}")
    public void setFlowRechargeMobilesKey(String flowRechargeMobilesKey) {
    	FLOR_RECHARGE_MOBILES_KEY = flowRechargeMobilesKey;
    }
}
