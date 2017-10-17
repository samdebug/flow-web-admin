package com.yzx.flow.modular.portal.service.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yzx.flow.common.exception.BizExceptionEnum;
import com.yzx.flow.common.exception.BussinessException;
import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.persistence.model.CustomerInfo;
import com.yzx.flow.common.persistence.model.FileUploadRecord;
import com.yzx.flow.common.persistence.model.FlowAppInfo;
import com.yzx.flow.common.persistence.model.FlowBatchRecord;
import com.yzx.flow.common.persistence.model.RechargePackage;
import com.yzx.flow.common.util.Constant;
import com.yzx.flow.common.util.OrderSeqGen;
import com.yzx.flow.core.shiro.ShiroKit;
import com.yzx.flow.core.shiro.ShiroUser;
import com.yzx.flow.core.util.CheckPhone;
import com.yzx.flow.modular.portal.service.IFlowBatchRecordService;
import com.yzx.flow.modular.system.dao.CustomerInfoDao;
import com.yzx.flow.modular.system.dao.FileUploadRecordDao;
import com.yzx.flow.modular.system.dao.FlowAppInfoDao;
import com.yzx.flow.modular.system.dao.FlowBatchRecordDao;

@Service
public class FlowBatchRecordServiceImpl implements IFlowBatchRecordService {
    private static final Logger LOG = LoggerFactory.getLogger(FlowBatchRecordServiceImpl.class);
    
    @Autowired
    private FlowBatchRecordDao flowBatchRecordDao;

    @Autowired
    private CustomerInfoDao customerInfoDao;

    @Autowired
    private FlowAppInfoDao flowAppInfoDao;
    
//    @Autowired
//    private AttachmentInterface attachmentInterface;

    @Autowired
    private FileUploadRecordDao fileUploadRecordDao;
    
//    protected Staff getCurrentLogin(){
//        Staff staff=null;
//        try {
//            staff= StaffUtil.getLoginStaff();
//        } catch (Exception e) {
//            LOG.error(e.getMessage(),e);
//            return null;
//        }
//        return staff;
//    }

    /* (non-Javadoc)
	 * @see com.yzx.flow.modular.portal.service.impl.IFlowBatchRecordService#pageQuery(com.yzx.flow.common.page.Page)
	 */
    public Page<FlowBatchRecord> pageQuery(Page<FlowBatchRecord> page) {
        List<FlowBatchRecord> list = flowBatchRecordDao.pageQuery(page);
        page.setDatas(list);
        return page;
    }

    /* (non-Javadoc)
	 * @see com.yzx.flow.modular.portal.service.impl.IFlowBatchRecordService#insert(com.yzx.flow.common.persistence.model.FlowBatchRecord)
	 */
    public void insert(FlowBatchRecord data) {
        flowBatchRecordDao.insert(data);
    }

    /* (non-Javadoc)
	 * @see com.yzx.flow.modular.portal.service.impl.IFlowBatchRecordService#get(java.lang.Long)
	 */
    public FlowBatchRecord get(Long flowBatchId) {
        return flowBatchRecordDao.selectByPrimaryKey(flowBatchId);
    }

    /* (non-Javadoc)
	 * @see com.yzx.flow.modular.portal.service.impl.IFlowBatchRecordService#saveAndUpdate(com.yzx.flow.common.persistence.model.FlowBatchRecord)
	 */
    public void saveAndUpdate(FlowBatchRecord data) {
        if (null != data.getFlowBatchId()) {// 判断有没有传主键，如果传了为更新，否则为新增
            this.update(data);
        } else {
            this.insert(data);
        }
    }

    /* (non-Javadoc)
	 * @see com.yzx.flow.modular.portal.service.impl.IFlowBatchRecordService#update(com.yzx.flow.common.persistence.model.FlowBatchRecord)
	 */
    public void update(FlowBatchRecord data) {
        flowBatchRecordDao.updateByPrimaryKey(data);
    }

    /* (non-Javadoc)
	 * @see com.yzx.flow.modular.portal.service.impl.IFlowBatchRecordService#delete(java.lang.Long)
	 */
    public int delete(Long flowBatchId) {
        return flowBatchRecordDao.deleteByPrimaryKey(flowBatchId);
    }
    
    /* (non-Javadoc)
	 * @see com.yzx.flow.modular.portal.service.impl.IFlowBatchRecordService#selectByFileId(java.lang.Long, java.lang.Integer)
	 */
    public List<FlowBatchRecord> selectByFileId(Long fileId, Integer status) {
        Map<String, Object> map = new HashMap<String, Object>();
        FileUploadRecord fileUploadRecord = fileUploadRecordDao.selectByPrimaryKey(fileId);
        map.put("fileId", fileId);
        map.put("status", status);
        map.put("createTime", fileUploadRecord.getCreateTime());
        return flowBatchRecordDao.selectByFileId(map);
    }
    
    /* (non-Javadoc)
	 * @see com.yzx.flow.modular.portal.service.impl.IFlowBatchRecordService#flowBatchIssued(com.yzx.flow.common.persistence.model.RechargePackage, java.util.List)
	 */
    @Transactional(rollbackFor = Exception.class)
    public void flowBatchIssued(RechargePackage data, List<String> list) throws BussinessException {
        // 1.插入file_upload_record表
        FileUploadRecord fileUploadRecord = new FileUploadRecord();
        fileUploadRecord = insertFileUploadRecord(data);
        fileUploadRecordDao.insert(fileUploadRecord);
        
        // 2.插入flow_batch_record表
        insertFlowBatchRecord(data, list, fileUploadRecord);
    }
    
    /**
     * 插入file_upload_record表
     */
    private FileUploadRecord insertFileUploadRecord(RechargePackage data) throws BussinessException {
        FileUploadRecord fileUploadRecord = new FileUploadRecord();
        
        ShiroUser staff = ShiroKit.getUser();
        CustomerInfo customerInfo = getCurrentCustomer();
        
        if (customerInfo == null) 
            throw new BussinessException(BizExceptionEnum.CUSTOMER_FORMAT_ERROR, "非法客户");
        
        // 读取文件并验证
        if(StringUtils.isNotBlank(data.getFileGroupId())){
//            List<AttachmentFile> smAttachmentFileList1 = attachmentInterface.listAttachmentFile(data.getFileGroupId());
//            if (smAttachmentFileList1 == null || smAttachmentFileList1.isEmpty()) {
//                LOG.error("上传文件异常：smAttachmentFileList1 == null || smAttachmentFileList1.isEmpty()");
//                throw new MyException("上传文件异常");
//            }
//            AttachmentFile af = smAttachmentFileList1.get(0);
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
//                fileUploadRecord.setRowNum(rows);
//            } catch (Exception e) {
//                LOG.error("读取文件出错：" + e.getMessage(), e);
//                throw new MyException("读取文件出错，请确认使用了正确的模板");
//            }
//            fileUploadRecord.setFileName(af.getFileName());
//            fileUploadRecord.setFileSavePath(af.getFileSaveName());
//            attachmentInterface.formalAttachmentGroup(data.getFileGroupId());
        }
        // TODO
        fileUploadRecord.setRowNum(0);
        fileUploadRecord.setFileName("");
        fileUploadRecord.setFileSavePath("");
        // end
        fileUploadRecord.setSourceType(data.getSourceType());
        // TODO 
//        fileUploadRecord.setFileGroupId(data.getFileGroupId());
        fileUploadRecord.setFileGroupId("");
        fileUploadRecord.setEventName(data.getEventName());
        fileUploadRecord.setCreator(staff.getAccount());
        fileUploadRecord.setCreateTime(new Date());
        fileUploadRecord.setCustomerId(customerInfo.getCustomerId());
        fileUploadRecord.setIsValid(Constant.IS_VALID);
        return fileUploadRecord;
    }
    
    /**
     * 插入flow_record_record表
     */
    @Transactional(rollbackFor = Exception.class)
    private void insertFlowBatchRecord(RechargePackage data, List<String> list, FileUploadRecord fileUploadRecord) throws BussinessException {
        if (null == fileUploadRecord.getFileId()) {
            throw new BussinessException(BizExceptionEnum.CUSTOMER_FORMAT_ERROR, "fileId为空");
        }
        String fileId = fileUploadRecord.getFileId().toString();
        String ydPackageId = data.getYdPackageId();
        String ltPackageId = data.getLtPackageId();
        String dxPackageId = data.getDxPackageId();
        String allPackageId = data.getAllPackageId();
        // 批量充值类型 1：基础包批量充值  2：三网通批量充值
        int rechargeType = data.getRechargeType();
        // 批量充值
        List<FlowBatchRecord> batchRecords = new ArrayList<FlowBatchRecord>();
        
        for (String phone : list) {
            if (StringUtils.isEmpty(phone.trim())) {
                continue;
            }
            if (!CheckPhone.isMobileNO(phone.trim())) {
                // 无法识别手机号码
                if (phone.trim().length() > Constant.ILLEGAL_MOBILE_MAX_LENGTH) {
                    throw new BussinessException(BizExceptionEnum.CUSTOMER_FORMAT_ERROR, "非法手机号码超出最大长度（32位）, 请重新上传");
                }
                // 构造一条非法手机号码的记录(未充值)
                FlowBatchRecord fbr = createIllegalPhoneFlowBatchRecord(fileId, phone);
                batchRecords.add(fbr);
                continue;
            }
            // 基础包批量充值
            if (Constant.RECHARGE_TYPE_BASE_PACKAGE == rechargeType || Constant.RECHARGE_TYPE_REDBAG == rechargeType) {
                String operator = CheckPhone.getMobileOpr(phone);
                switch (operator) {
                    case Constant.OPERATOR_YD:
                        if (!StringUtils.isEmpty(ydPackageId)) {
                            FlowBatchRecord ydFBR = createFlowBatchRecord(ydPackageId, fileId, phone, data.getYdOrderDetailId(), data.getYdProductId(),fileUploadRecord.getCreateTime());
                            batchRecords.add(ydFBR);
                        }
                        break;
                    case Constant.OPERATOR_DX:
                        if (!StringUtils.isEmpty(dxPackageId)) {
                            FlowBatchRecord dxFBR = createFlowBatchRecord(dxPackageId, fileId, phone, data.getDxOrderDetailId(), data.getDxProductId(),fileUploadRecord.getCreateTime());
                            batchRecords.add(dxFBR);
                        }
                        break;
                    case Constant.OPERATOR_LT:
                        if (!StringUtils.isEmpty(ltPackageId)) {
                            FlowBatchRecord ltFBR = createFlowBatchRecord(ltPackageId, fileId, phone, data.getLtOrderDetailId(), data.getLtProductId(),fileUploadRecord.getCreateTime());
                            batchRecords.add(ltFBR);
                        }
                        break;
                    default:
                        break;
                }
            } else {
                FlowBatchRecord allFBR = createFlowBatchRecord(allPackageId, fileId, phone, data.getAllOrderDetailId(), data.getAllProductId(),fileUploadRecord.getCreateTime());
                batchRecords.add(allFBR);
            }
        }
        
        // 批量插入flow_batch_record
        if (!batchRecords.isEmpty()) {
            insertBatch(batchRecords, rechargeType, Long.valueOf(fileId));
        }
    }
    
    /* (non-Javadoc)
	 * @see com.yzx.flow.modular.portal.service.impl.IFlowBatchRecordService#insertBatch(java.util.List, java.lang.Integer, java.lang.Long)
	 */
    @Transactional(rollbackFor = Exception.class)
    public void insertBatch(List<FlowBatchRecord> list, Integer rechargeType, Long fileId) {
        // 1.批量插入flow_batch_record
        // (为了提升性能，避免重复访问DB，此处批量插入的记录中的数据不完整)
        long aaa = System.currentTimeMillis();
        flowBatchRecordDao.insertBatch(list);
        
        long a = System.currentTimeMillis();
        System.out.println("批量插入不完整数据共耗时：\t" + (a - aaa) / 1000f + "秒");
        
        // 2.通过关联查询将flow_batch_record中的数据补充完整
        ShiroUser staff = ShiroKit.getUser();
        // 获取客户信息
        CustomerInfo customer = customerInfoDao.getCustomerInfoByCustomerId(staff.getTargetId());
        if (customer == null) {
            LOG.error("非法客户");
            throw new BussinessException(BizExceptionEnum.CUSTOMER_FORMAT_ERROR, "构造FlowBatchRecord对象出错： "+staff.getAccount()+" 获取对应的客户信息错误！");
        }
        // 根据客户id查询对对应app接入
        FlowAppInfo appInfo = flowAppInfoDao.getOrderInfoByOrderTypeAndCustomerId(customer.getCustomerId());
        if (appInfo == null) 
        	throw new BussinessException(BizExceptionEnum.CUSTOMER_FORMAT_ERROR, "当前客户没有APP接入，无法进行充值。");
        
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("orderId", appInfo.getOrderId());
        map.put("customerId", customer.getCustomerId());
        map.put("rechargeType", rechargeType);
        map.put("fileId", fileId);
        flowBatchRecordDao.completeFlowBatchRecord(map);
        System.out.println("数据补充完整共耗时：\t" + (System.currentTimeMillis() - a) / 1000f + "秒");
    }

    /* (non-Javadoc)
	 * @see com.yzx.flow.modular.portal.service.impl.IFlowBatchRecordService#updateBatch(java.lang.String, java.util.List, java.lang.Integer)
	 */
    public void updateBatch(String updator, List<Long> ids, Integer status) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("updator", updator);
        map.put("ids", ids);
        map.put("status", status);
        flowBatchRecordDao.updateBatch(map);
    }

    /* (non-Javadoc)
	 * @see com.yzx.flow.modular.portal.service.impl.IFlowBatchRecordService#updateBatchByInfo(java.util.List)
	 */
    public void updateBatchByInfo(List<FlowBatchRecord> list) {
        flowBatchRecordDao.updateBatchByInfo(list);
    }
    
    /* (non-Javadoc)
	 * @see com.yzx.flow.modular.portal.service.impl.IFlowBatchRecordService#selectFlowBatchRecords(java.lang.Integer)
	 */
    public List<FlowBatchRecord> selectFlowBatchRecords(Integer status) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("status", status);
        return flowBatchRecordDao.selectFlowBatchRecords(map);
    }
    
    /* (non-Javadoc)
	 * @see com.yzx.flow.modular.portal.service.impl.IFlowBatchRecordService#findCustomerInfoByAccount(java.lang.String)
	 */
    public CustomerInfo findCustomerInfoByAccount(String account) {
        List<CustomerInfo> list = customerInfoDao.getByAccount(account);
        if (null != list && !list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }
    
    /**
     * 获取当前客户
     */
    private CustomerInfo getCurrentCustomer() {
        ShiroUser staff = ShiroKit.getUser();
        if(staff == null || staff.getTargetId() == null) {
            LOG.debug("当前没有登录用户");
            return null;
        }
        return customerInfoDao.getCustomerInfoByCustomerId(staff.getTargetId());
    }
    
    /* (non-Javadoc)
	 * @see com.yzx.flow.modular.portal.service.impl.IFlowBatchRecordService#getHSSFSheet(java.io.InputStream)
	 */
    public HSSFSheet getHSSFSheet(InputStream excelIS) {
        HSSFSheet sh = null;
        try {
            Workbook workBook = new HSSFWorkbook(excelIS);
            // 得到book第一个工作薄sheet
            sh = (HSSFSheet)workBook.getSheetAt(0);
        } catch (FileNotFoundException e) {
            LOG.error(e.getMessage(), e);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
        return sh;
    }
    
    /* (non-Javadoc)
	 * @see com.yzx.flow.modular.portal.service.impl.IFlowBatchRecordService#createIllegalPhoneFlowBatchRecord(java.lang.String, java.lang.String)
	 */
    public FlowBatchRecord createIllegalPhoneFlowBatchRecord(String fileId, String mobile) {
        FlowBatchRecord fbr = new FlowBatchRecord();
        ShiroUser staff = ShiroKit.getUser();
        fbr.setFileId(Long.valueOf(fileId));
        fbr.setOrderDetailId(0L);
        fbr.setOrderId(0L);
        fbr.setProductId(0L);
        fbr.setProductName("");
        fbr.setPackageId("");
        fbr.setMobile(mobile);
        fbr.setFlowVoucherId("");
        fbr.setOperatorCode("");
        fbr.setZone("");
        fbr.setStatus(Constant.FLOW_ISSUED_STATUS_NOT);
        fbr.setSettlementPrice(new BigDecimal(0));
        fbr.setRemark("");
        fbr.setIsValid(Constant.IS_NOT_VALID);
        fbr.setCreator(staff.getAccount());
        fbr.setCreateTime(new Date());
        return fbr;
    }
    
    /* (non-Javadoc)
	 * @see com.yzx.flow.modular.portal.service.impl.IFlowBatchRecordService#createFlowBatchRecord(java.lang.String, java.lang.String, java.lang.String, java.lang.Long, java.lang.Long, java.util.Date)
	 */
    public FlowBatchRecord createFlowBatchRecord(String packageId, String fileId, String mobile, Long orderDetailId, Long productId,Date createTime) {
        FlowBatchRecord fbr = new FlowBatchRecord();
        ShiroUser staff = ShiroKit.getUser();
        fbr.setFileId(Long.valueOf(fileId));
        fbr.setOrderDetailId(orderDetailId);
        fbr.setOrderId(0L);
        fbr.setProductId(productId);
        fbr.setProductName("");
        fbr.setPackageId(packageId);
        fbr.setMobile(mobile);
        fbr.setFlowVoucherId(OrderSeqGen.createBatchApplyId());
        fbr.setOperatorCode("");
        fbr.setZone("");
        fbr.setStatus(Constant.FLOW_ISSUED_STATUS_NOT);
        fbr.setSettlementPrice(new BigDecimal(0));
        fbr.setRemark("");
        fbr.setIsValid(Constant.IS_VALID);
        fbr.setCreator(staff.getAccount());
        fbr.setCreateTime(createTime);
        return fbr;
    }
    
    /* (non-Javadoc)
	 * @see com.yzx.flow.modular.portal.service.impl.IFlowBatchRecordService#insertRec(com.yzx.flow.common.persistence.model.FlowBatchRecord)
	 */
    public void insertRec(FlowBatchRecord data) {
        flowBatchRecordDao.insertRec(data);
    }
    
    /* (non-Javadoc)
	 * @see com.yzx.flow.modular.portal.service.impl.IFlowBatchRecordService#selectRecByFileId(java.lang.Long, java.lang.Integer)
	 */
    public List<FlowBatchRecord> selectRecByFileId(Long fileId, Integer status) {
        Map<String, Object> map = new HashMap<String, Object>();
        FileUploadRecord fileUploadRecord = fileUploadRecordDao.selectByPrimaryKey(fileId);
        map.put("fileId", fileId);
        map.put("status", status);
        map.put("createTime", fileUploadRecord.getCreateTime());
        return flowBatchRecordDao.selectRecByFileId(map);
    }
    
    /* (non-Javadoc)
	 * @see com.yzx.flow.modular.portal.service.impl.IFlowBatchRecordService#selectIssuedingRecords(java.lang.Integer)
	 */
    public List<FlowBatchRecord> selectIssuedingRecords(Integer status) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("status", status);
        return flowBatchRecordDao.selectIssuedingRecords(map);
    }
}
