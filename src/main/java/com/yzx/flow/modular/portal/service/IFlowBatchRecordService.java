package com.yzx.flow.modular.portal.service;

import java.io.InputStream;
import java.util.Date;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.springframework.transaction.annotation.Transactional;

import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.persistence.model.CustomerInfo;
import com.yzx.flow.common.persistence.model.FlowBatchRecord;
import com.yzx.flow.common.persistence.model.RechargePackage;

public interface IFlowBatchRecordService {

	Page<FlowBatchRecord> pageQuery(Page<FlowBatchRecord> page);

	void insert(FlowBatchRecord data);

	FlowBatchRecord get(Long flowBatchId);

	void saveAndUpdate(FlowBatchRecord data);

	void update(FlowBatchRecord data);

	int delete(Long flowBatchId);

	List<FlowBatchRecord> selectByFileId(Long fileId, Integer status);

	/**
	 * 批量下发流量主业务方法
	 */
	void flowBatchIssued(RechargePackage data, List<String> list);

	void insertBatch(List<FlowBatchRecord> list, Integer rechargeType, Long fileId);

	void updateBatch(String updator, List<Long> ids, Integer status);

	void updateBatchByInfo(List<FlowBatchRecord> list);

	List<FlowBatchRecord> selectFlowBatchRecords(Integer status);

	CustomerInfo findCustomerInfoByAccount(String account);

	HSSFSheet getHSSFSheet(InputStream excelIS);

	/**
	 * 构造非法手机号码的FlowBatchRecord对象
	 */
	FlowBatchRecord createIllegalPhoneFlowBatchRecord(String fileId, String mobile);

	/**
	 * 构造FlowBatchRecord对象
	 */
	FlowBatchRecord createFlowBatchRecord(String packageId, String fileId, String mobile, Long orderDetailId,
			Long productId, Date createTime);

	/**
	 * 插入完成记录
	 * @param data
	 */
	void insertRec(FlowBatchRecord data);

	/**
	 * 下发记录
	 * @param fileId
	 * @param status
	 * @return
	 */
	List<FlowBatchRecord> selectRecByFileId(Long fileId, Integer status);

	/**
	 * 获取下发中记录
	 * @param status
	 * @return
	 */
	List<FlowBatchRecord> selectIssuedingRecords(Integer status);

}