package com.yzx.flow.modular.open.myTask;
import org.apache.commons.lang.StringUtils;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yzx.flow.common.exception.MyException;
import com.yzx.flow.common.persistence.model.SystemVersion;
import com.yzx.flow.core.util.RSAUtils;
import com.yzx.flow.core.util.zk.ZookeeperUtil;
import com.yzx.flow.modular.open.utils.EnumType;
import com.yzx.flow.modular.open.utils.ResponseBean;
import com.yzx.flow.modular.open.utils.ResponseTranscation;
import com.yzx.flow.modular.system.dao.SystemVersionDao;
import com.yzx.flow.modular.system.service.IUpgradeService;

import com0oky.httpkit.http.HttpKit;

/**
 * 定时发送请求到服务器端，检测是否有可更新版本
 * 使用时再启动
 * @author wxl
 *
 */
@Component
public class Heartbeat {
	//日志记录
	protected final static Logger logger = LoggerFactory.getLogger(Heartbeat.class);
	//每5分钟发送一次请求
	private static final long FIVE_MINUTE = 5 * 60 * 1000;
	//测试用
	private static final long TEN_SECOND = 10 * 1000;
	
	@Autowired
	private IUpgradeService upgradeService;
	
	@Autowired
	private ZookeeperUtil zookeeperUtil;
	//请求版本信息地址
	@Value("${serviceUrl}")
	private String serviceUrl;
	
	@Scheduled(fixedDelay=FIVE_MINUTE)
	public void postSendVersionInfo() {
		try {
			if(StringUtils.isBlank(serviceUrl)){
				return;
			}
			if(zookeeperUtil==null || zookeeperUtil.getLicence()==null){
				if(zookeeperUtil==null){
					logger.info("zookeeperUtil为null");
				}
				if(zookeeperUtil.getLicence()==null){
					logger.info("licence为null");
				}
				logger.debug("无用户信息.");
				return;
			}
			String versionInfoUrl = serviceUrl+"/getSystemVersion";
			//请求参数
			Map<String,Object> parameter = new HashMap<String,Object>();
			//用户名和公钥
			parameter.put("userName", zookeeperUtil.getLicence().getCustomerName());
			
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("passWord", zookeeperUtil.getLicence().getPassword());
			//查询当前版本号
			SystemVersion curVersion = upgradeService.selectCurVersion();
			map.put("curVersion", curVersion.getVersion());
			//签名
			String secretCode = RSAUtils.getSecretCode(zookeeperUtil.getPublickey(), JSONObject.toJSONString(map));
			
			parameter.put("sign", secretCode);
			//签名
			//请求头
			Map<String,String> head = new HashMap<String,String>();
			head.put("accept", "*/*");
			head.put("connection", "Keep-Alive");
			head.put("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			head.put("Content-Type", "application/json;charset=utf8");
			String result = null ;
			//发送请求		100s请求超时
			result= HttpKit.post(versionInfoUrl).addHeaders(head).setConnectionRequestTimeout(100000).setParameterJson(parameter).execute().getString();
			logger.debug("result="+result);
			//返回结果处理
			ObjectMapper om = new ObjectMapper();
			om.setSerializationInclusion(Include.NON_NULL);
			ResponseBean responseBean = om.readValue(result, ResponseBean.class);
			
			logger.debug(responseBean.getResMsg());
			
			String resCode = responseBean.getResCode();
			//请求成功
			if(EnumType.busiEnum.busi_000000.getCode().equals(resCode)){
				//签名验证
				if(true){
					if(!responseBean.getResData().isEmpty()) {
						List<SystemVersion> systemVersions = new ArrayList<SystemVersion>();
						ResponseTranscation rTranscation = new ResponseTranscation();
						rTranscation.transTo(responseBean.getResData(),systemVersions);
						upgradeService.saveOrUpdate(systemVersions);
					}
				}
			} else {
				throw new MyException(EnumType.busiEnum.valueOf(resCode).getMessage());
			}
		} catch (Exception e) {
			logger.debug(e.getMessage());
		}
	}
}
