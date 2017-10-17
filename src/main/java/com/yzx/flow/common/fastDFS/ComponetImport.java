package com.yzx.flow.common.fastDFS;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.context.annotation.Import;
import org.springframework.jmx.support.RegistrationPolicy;

import com.github.tobato.fastdfs.FdfsClientConfig;
import com.github.tobato.fastdfs.domain.MateData;
import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.proto.storage.DownloadFileWriter;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.xiaoleilu.hutool.date.DateUtil;

/**
 * 导入FastDFS-Client组件
 * 
 * @author tobato
 *
 */
@Configuration
@Import(FdfsClientConfig.class)
// 解决jmx重复注册bean的问题
@EnableMBeanExport(registration = RegistrationPolicy.IGNORE_EXISTING)
public class ComponetImport {
    // 导入依赖组件
    @Autowired
    private FastFileStorageClient storageClient;
    
	protected final static Logger logger = LoggerFactory.getLogger(ComponetImport.class);

    /**
     * 上传文件
     * @param sourceFile "d:\\e.war"
     * @return
     */
    public StorePath uploadFile(String sourceFile){
        File file = new File(sourceFile);
        String fileExtName = FilenameUtils.getExtension(file.getName());
        // Metadata
        Set<MateData> metaDataSet = new HashSet<MateData>();
        metaDataSet.add(new MateData("Author", "yzx"));
        metaDataSet.add(new MateData("CreateDate", DateUtil.today()));
        InputStream in = null;
        StorePath path = null;
        try {
			in = new FileInputStream(file);
			// 上传文件和Metadata
			path = storageClient.uploadFile(in, file.length(), fileExtName,
					metaDataSet);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return path;
    }
    
    /**
     * 
     * @param groupName group1
     * @param path  M00/00/00/rBAGDlmv4NiANhKgAAAACpIItYQ959.war
     * @param downLoadTarget  D:\\a.war
     */
    public void downLoad(String groupName,String path,String downLoadTarget){
    	DownloadFileWriter downloadFileWriter = new DownloadFileWriter(downLoadTarget);
    	String downloadFile = storageClient.downloadFile(groupName, path, downloadFileWriter);
    	logger.info("downloadFile:"+downloadFile);
    }
    
    
    
    /**
     * 获取文件InputStream
     * 
     * @param path
     * @return
     * @throws FileNotFoundException
     */
    public static InputStream getFileInputStream(String path) throws FileNotFoundException {
        return new FileInputStream(getFile(path));
    }

    /**
     * 获取文件
     * 
     * @param path
     * @return
     */
    public static File getFile(String path) {
        URL url = ComponetImport.class.getResource(path);
        File file = new File(url.getFile());
        return file;
    }
    
}