package com.yzx.flow.core.util.download;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yzx.flow.core.support.HttpUtil;


public class DownloadUtil {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DownloadUtil.class);
	
	
	public static void downloadFile(File file) throws IOException {
        downloadFile( null, file);
    }
	
	/**
	 * - 文件被下载完成后将自动删除
	 * @param downloadName 下载文件显示名称
	 * @param file
	 * @throws IOException
	 */
	public static void downloadFile(String downloadName, File file) throws IOException {
        downloadFile(downloadName, file, true);
    }
	
	
	
	/**
	 * 
	 * @param file
	 * @param autoDelete 文件被下载完成后是否自动删除文件
	 * @throws IOException
	 */
	public static void downloadFile(String downloadName, File file, boolean autoDelete) throws IOException {
        downloadFile(downloadName, file, "application/x-msdownload;charset=utf-8", autoDelete);
    }

	/**
	 *  - 文件被下载完成后将自动删除
	 * @param file
	 * @throws IOException
	 */
	public static void downloadExcelFile(File file) throws IOException {
		downloadExcelFile(null, file);
    }
	
	public static void downloadExcelFile(String downloadName, File file) throws IOException {
		downloadExcelFile(downloadName, file, true);
    }
	
	/**
	 * 
	 * @param downloadName
	 * @param file
	 * @param autoDelete
	 * @throws IOException
	 */
	public static void downloadExcelFile(String downloadName, File file, boolean autoDelete) throws IOException {
        downloadFile(downloadName, file, "application/vnd.ms-excel;charset=utf-8", autoDelete);
    }
	
	
	
	/**
	 * 文件下载
	 * @param downloadName
	 * @param file
	 * @param contentType
	 * @param autoDelete
	 * @throws IOException
	 */
	public static void downloadFile(String downloadName, File file, String contentType, boolean autoDelete) throws IOException {
		
		String fileName = file.getName();
		if ( downloadName != null && !downloadName.trim().isEmpty() ) {
			fileName = downloadName.trim();
		}
		
		HttpServletResponse response = HttpUtil.getResponse();
        response.reset();
        
        HttpServletRequest request = HttpUtil.getRequest();
        response.setContentType(contentType);
        String agent = request.getHeader("USER-AGENT");
        if (null != agent && -1 != agent.indexOf("MSIE")) {// IE
            // 设置文件头，文件名称或编码格式
            response.addHeader("Content-Disposition",
                    "attachment;filename=\"" + java.net.URLEncoder.encode(fileName, "UTF-8") + "\"");
        } else {// firefox
            response.addHeader("Content-Disposition",
                    "attachment;filename=\"" + new String(fileName.getBytes("UTF-8"), "ISO-8859-1") + "\"");
        }

        OutputStream myout = null;
        FileInputStream fis = null;
        try {
            // 读出文件到i/o流
            fis = new FileInputStream(file);
            BufferedInputStream buff = new BufferedInputStream(fis);
            byte[] b = new byte[1024];// 相当于我们的缓存
            long k = 0;// 该值用于计算当前实际下载了多少字节
            // 从response对象中得到输出流,准备下载
            myout = response.getOutputStream();
            // 开始循环下载
            while (k < file.length()) {
                int j = buff.read(b, 0, 1024);
                k += j;
                // 将b中的数据写到客户端的内存
                myout.write(b, 0, j);
            }
            myout.flush();
            if (buff != null) {
                buff.close();
            }
            
            if ( autoDelete )
            	file.delete();// delete
            
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            IOUtils.closeQuietly(myout);
            IOUtils.closeQuietly(fis);
        }
	}

	

}
