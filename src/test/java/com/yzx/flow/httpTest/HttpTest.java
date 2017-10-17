package com.yzx.flow.httpTest;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.alibaba.fastjson.JSONObject;
import com.yzx.flow.core.util.RSAUtils;

import com0oky.httpkit.http.HttpKit;
import com0oky.httpkit.http.request.FormPart;

public class HttpTest {
	public static void main(String[] args) {
//		String html = HttpKit.get("https://baidu.com").execute().getString();
//		System.out.println(html);
		
		
		//
/*		FormPart form = FormPart.create()
		// 添加额外参数
		.addParameter("age", 40)
		//添加文件
		.addParameter("fileName", new File("d:/test.txt"));
		
		String result = HttpKit.post("http://www.test.com").useForm(form).execute().getString();
		System.out.println(result);*/
		
		String userName = "测试";
		String password = "11111";
		Map<String,String> para = new HashMap<>();
		para.put("groupName", "group1");
		para.put("userName", userName);
		para.put("path", "M00/00/00/rBAGDlmv4NiANhKgAAAACpIItYQ959.war");
		para.put("fileName", "a.war");
		Map<String,String> map = new HashMap<>();
		
		String publickey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCvKbqSu3puJVqseyhg9Nby6wQ6MuaFuKHH1MsCSl6jo7s64hBzOPLH6dSsdOCtysDGFhRT/Dmpd8ou9NDJsaR/zwDELeTbzqPbgJ9HVe4zKbJcVfsBU+WKHTTgD/+3AwO0DozsDCllV49Ue01rv2Qx9lBitSRs0x1U+3fXTvLgnQIDAQAB";
		String secretCode = RSAUtils.getSecretCode(publickey,password);
		map.put("sign", secretCode);
		map.put("Content-Type", "application/json;charset=utf8");
		
		String url = "http://localhost:8080/download/war";
		InputStream inputStream = HttpKit.post(url).setParameterJson(para).addHeaders(map).execute().getInputStream();
		
	        FileOutputStream out = null;
	        InputStream in = null;
	        try {
	            out = new FileOutputStream("d:\\b.war");
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
