package com.yzx.flow.modular.open.myTask;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.yzx.flow.core.util.RSAUtils;
import com.yzx.flow.core.util.zk.ZookeeperUtil;
import com.yzx.flow.modular.open.service.IHeartData;
import com.yzx.flow.modular.open.utils.ResultData;

import com0oky.httpkit.http.HttpKit;


@Component
public class HeartData {
	//日志记录
	protected final static Logger logger = LoggerFactory.getLogger(HeartData.class);
	//每10分钟发送一次请求
	private static final int GAP_TIME =10 * 60 * 1000; //;20*1000; 
	
	@Autowired
	private IHeartData heartData;
	
	@Autowired
	private ZookeeperUtil zookeeperUtil;
	
	@Value("${serviceUrl}")
	private String serviceUrl;
	
	private static final String version = "通用型";
	
	@Scheduled(fixedDelay=GAP_TIME)
	public void postHeartData() {
		try {
			if(zookeeperUtil==null || zookeeperUtil.getLicence()==null){
				logger.info("无用户信息.");
				return;
			}
			String url = serviceUrl+"/receiveData";
			ResultData resultData = heartData.getHeartData(GAP_TIME);
			resultData.setUserName(zookeeperUtil.getLicence().getCustomerName());
			resultData.setVersion(version);
			Map<String,String> head = new HashMap<String,String>();
			head.put("accept", "*/*");
			head.put("connection", "Keep-Alive");
			head.put("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			head.put("Content-Type", "application/json;charset=utf8");
			
			//请求参数
			Map<String,Object> parameter = new HashMap<String,Object>();
			//用户名和公钥
			parameter.put("userName", zookeeperUtil.getLicence().getCustomerName());
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("passWord", zookeeperUtil.getLicence().getPassword());
			map.put("data", JSONObject.toJSONString(resultData));
			//签名
			String secretCode = RSAUtils.getSecretCode(zookeeperUtil.getPublickey(), JSONObject.toJSONString(map));
			
			parameter.put("sign", secretCode);
			
			String result= HttpKit.post(url).addHeaders(head).setConnectionRequestTimeout(100000).
					setParameterJson(parameter).execute().getString();
//			logger.info("postHeartData:"+result);
		} catch (Exception e) {
			logger.error("postHeartData",e);
		}
	}
}
