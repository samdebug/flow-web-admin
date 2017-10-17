package com.yzx.flow.modular.portal.service;

import org.springframework.transaction.annotation.Transactional;

import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.persistence.model.FileUploadRecord;

public interface IFileUploadRecordService {

	Page<FileUploadRecord> pageQuery(Page<FileUploadRecord> page);

	void insert(FileUploadRecord data);

	FileUploadRecord get(Long fileId);

	FileUploadRecord selectByPrimaryKeySms(Long fileId);

	void saveAndUpdate(FileUploadRecord data);

	void update(FileUploadRecord data);

	int delete(Long fileId);

	FileUploadRecord selectByFileGroupId(String fileGroupId);

}