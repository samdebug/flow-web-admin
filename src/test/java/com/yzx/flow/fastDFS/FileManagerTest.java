package com.yzx.flow.fastDFS;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.http.StatusLine;
import org.junit.Test;

import com.yzx.flow.core.util.FileDownloadUtil;
import com.yzx.flow.core.util.RSAUtils;
import com.yzx.flow.core.util.zk.ZookeeperUtil;

import com0oky.httpkit.http.HttpKit;
import com0oky.httpkit.http.ResponseWrap;

public class FileManagerTest {
	
	@Test
	public void breakDownload(){
		Map<String,String> para = new HashMap<>();
		para.put("groupName", "group1");
		para.put("path", "M00/00/00/rBAGDlnEwRuAXo1RACNtMqvQJU0928.rar");
		String fileName = "flow-web-admin1.war";
		para.put("fileName", fileName);
		String desPath = "D://ceshi/breakTest.rar";
		//文件下载续点
		File file = new File(desPath);
		if(file.exists()){
			//判断file.length()和总的文件大小
			
			para.put("fileOffset",file.length()+"");
		} else {
			para.put("fileOffset", "0");
		}
		
//		para.put("fileOffset", "0");
//		para.put("fileSize", "100000000");
		Map<String,String> map = new HashMap<>();
		map.put("Content-Type", "application/json;charset=utf8");
		map.put("Content-Type", "application/json;charset=utf8");
		String url = "http://localhost:8080//download/war";
		InputStream inputStream = HttpKit.post(url ).setParameterJson(para).addHeaders(map).execute().getInputStream();
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
	            in = null ;
	            out = null ;
	        }
		
	}
	@Test
	public void download(){
		Map<String,String> para = new HashMap<>();
		para.put("groupName", "group1");
		para.put("path", "M00/00/00/flow-web-admin.war");
		para.put("fileName", "test.jpg");
		Map<String,String> map = new HashMap<>();
		map.put("Content-Type", "application/json;charset=utf8");
		String url = "http://localhost:8080//download/war";
		InputStream inputStream = HttpKit.post(url).setParameterJson(para).addHeaders(map).execute().getInputStream();
		FileOutputStream out = null;
		InputStream in = null;
		try {
			out = new FileOutputStream("D://admin1.war");
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
			in = null ;
			out = null ;
		}
		
	}
}
