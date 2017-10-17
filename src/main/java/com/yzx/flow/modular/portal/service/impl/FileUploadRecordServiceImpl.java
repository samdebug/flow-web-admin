package com.yzx.flow.modular.portal.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.persistence.model.FileUploadRecord;
import com.yzx.flow.modular.portal.service.IFileUploadRecordService;
import com.yzx.flow.modular.system.dao.FileUploadRecordDao;

/**
 * <b>Title：</b>FileUploadRecordService.java<br/> <b>Description：</b> <br/> <b>@author： </b>v5480<br/>
 * <b>@date：</b>2015-09-28 15:45:34<br/> <b>Copyright (c) 2015 szwisdom Tech.</b>
 */
@Service
public class FileUploadRecordServiceImpl implements IFileUploadRecordService {
    @Autowired
    private FileUploadRecordDao fileUploadRecordDao;
    
    /* (non-Javadoc)
	 * @see com.yzx.flow.modular.portal.service.impl.IFileUploadRecordService#pageQuery(com.yzx.flow.common.page.Page)
	 */
    public Page<FileUploadRecord> pageQuery(Page<FileUploadRecord> page) {
        List<FileUploadRecord> list = fileUploadRecordDao.pageQuery(page);
        page.setDatas(list);
        return page;
    }

    /* (non-Javadoc)
	 * @see com.yzx.flow.modular.portal.service.impl.IFileUploadRecordService#insert(com.yzx.flow.common.persistence.model.FileUploadRecord)
	 */
    public void insert(FileUploadRecord data) {
        fileUploadRecordDao.insert(data);
    }

    /* (non-Javadoc)
	 * @see com.yzx.flow.modular.portal.service.impl.IFileUploadRecordService#get(java.lang.Long)
	 */
    public FileUploadRecord get(Long fileId) {
        return fileUploadRecordDao.selectByPrimaryKey(fileId);
    }
    
    /* (non-Javadoc)
	 * @see com.yzx.flow.modular.portal.service.impl.IFileUploadRecordService#selectByPrimaryKeySms(java.lang.Long)
	 */
    public FileUploadRecord selectByPrimaryKeySms(Long fileId){
    	return fileUploadRecordDao.selectByPrimaryKeySms(fileId);
    }

    /* (non-Javadoc)
	 * @see com.yzx.flow.modular.portal.service.impl.IFileUploadRecordService#saveAndUpdate(com.yzx.flow.common.persistence.model.FileUploadRecord)
	 */
    @Transactional(rollbackFor = Exception.class)
    public void saveAndUpdate(FileUploadRecord data) {
        if (null != data.getFileId()) {// 判断有没有传主键，如果传了为更新，否则为新增
            this.update(data);
        } else {
            this.insert(data);
        }
    }

    /* (non-Javadoc)
	 * @see com.yzx.flow.modular.portal.service.impl.IFileUploadRecordService#update(com.yzx.flow.common.persistence.model.FileUploadRecord)
	 */
    public void update(FileUploadRecord data) {
        fileUploadRecordDao.updateByPrimaryKey(data);
    }

    /* (non-Javadoc)
	 * @see com.yzx.flow.modular.portal.service.impl.IFileUploadRecordService#delete(java.lang.Long)
	 */
    public int delete(Long fileId) {
        return fileUploadRecordDao.deleteByPrimaryKey(fileId);
    }
    
    /* (non-Javadoc)
	 * @see com.yzx.flow.modular.portal.service.impl.IFileUploadRecordService#selectByFileGroupId(java.lang.String)
	 */
    public FileUploadRecord selectByFileGroupId(String fileGroupId) {
        return fileUploadRecordDao.selectByFileGroupId(fileGroupId);
    }
}
