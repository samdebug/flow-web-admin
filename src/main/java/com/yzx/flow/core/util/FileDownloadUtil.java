package com.yzx.flow.core.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;



import org.apache.commons.io.IOUtils;

import com0oky.httpkit.http.HttpKit;



public class FileDownloadUtil {

	/**
	 * 下载war包信息
	 * @param path  包的路径
	 * @param publickey  公钥
	 * @param url   请求下载地址
	 * @param fileName  文件名称
	 * @param desPath  目标路径
	 */
	public static void downloadWarFile(String path,String publickey,String url,String fileName,String desPath,String userName,String password){
		Map<String,String> para = new HashMap<>();
		para.put("groupName", "group1");
		para.put("userName", userName);
		para.put("path", path);
		para.put("fileName", fileName);
		
		//文件下载续点
		File file = new File(desPath);
		if(file.exists()){
			para.put("fileOffset", file.length()+"");
		} else {
			para.put("fileOffset", "0");
		}
		
		Map<String,String> map = new HashMap<>();
		String secretCode = RSAUtils.getSecretCode(publickey,password);
		map.put("sign", secretCode);
		map.put("Content-Type", "application/json;charset=utf8");
		InputStream inputStream = HttpKit.post(url).setParameterJson(para).addHeaders(map).execute().getInputStream();
		
	        FileOutputStream out = null;
	        InputStream in = null;
	        try {
	            out = new FileOutputStream(desPath,true);
	            in = new BufferedInputStream(inputStream);
	            // 通过ioutil 对接输入输出流，实现文件下载
	            IOUtils.copy(in, out);
	            out.flush();
	        } catch (IOException e) {
	            e.printStackTrace();
	        } finally {
	            // 关闭流
	            IOUtils.closeQuietly(in);
	            IOUtils.closeQuietly(out);
	        }
		
	}
}
