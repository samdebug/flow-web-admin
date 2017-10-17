package com.yzx.flow.modular.job;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yzx.flow.common.persistence.model.SystemVersion;
import com.yzx.flow.common.util.RunShellUtil;
import com.yzx.flow.core.util.FileDownloadUtil;
import com.yzx.flow.modular.job.service.VersionUpdateService;
/**
 * 
 * @author liuyufeng
 * 执行更新
 *
 */
public class VersionUpdateExecute {
	
	private static final Logger logger = LoggerFactory.getLogger(VersionUpdateExecute.class);
	
	private VersionUpdateService versionUpdateService;
	
	private String war_download_url;
	
	public final static String COMPONENT ="flow-web-admin";
	
	public final static String parentNode ="/version/";
	
	public final static String orderNode ="order";
	
	public final static String rollbackOrderNode ="rollbackOrder";
	
	public final static String rollbackOrderTempNode ="rollbackOrderTemp";
	
	private String publickey;
	
	private String customerName;
	
	private String password;
	
	
	
	public VersionUpdateExecute() {
		super();
	}



	public VersionUpdateExecute(VersionUpdateService versionUpdateService,
			String war_download_url, String publickey, String customerName,
			String password) {
		super();
		this.versionUpdateService = versionUpdateService;
		this.war_download_url = war_download_url;
		this.publickey = publickey;
		this.customerName = customerName;
		this.password = password;
	}
	
	/**
	 * 执行升级
	 */
	public void updateExecute(){
		try {
			logger.info("更新flow-web-admin版本信息开始");
			SystemVersion systemVersion = versionUpdateService.getSystemUpdateingVersionInfo(COMPONENT);
			if(systemVersion !=null){
				FileDownloadUtil.downloadWarFile(systemVersion.getComponentUrl(), publickey, war_download_url, 
						"flow-web-admin.war", "/opt/flow/download/flow-web-admin/flow-web-admin.war",customerName,
						password);
				String basePath =VersionUpdateJob.class.getClassLoader().getResource("/").getPath();
				 InputStream in = new BufferedInputStream(new FileInputStream(  
		                    new File(basePath+"param.properties")));  
		            Properties prop = new Properties();  
		            prop.load(in);  
		            if(systemVersion.getId() !=null){
		            	prop.put("id", systemVersion.getId().toString());
		            }
		            if(StringUtils.isNoneBlank(systemVersion.getDeploymentUrl())){
		            	prop.put("httpurl", systemVersion.getDeploymentUrl());
		            }
		            in.close();
		            FileOutputStream fos = new FileOutputStream(new File(basePath+"param.properties"));
		            prop.store(fos, "save success");
		            fos.close();
		        logger.info("chmod 744 "+basePath+"/*.sh");
				RunShellUtil.chmodShell("chmod -R 744 "+basePath+"/*.sh");
				RunShellUtil.runShell( "at now + 1 minutes -f " + basePath+"update.sh");
			}
			logger.info("更新flow-web-admin版本信息结束");
		} catch (Exception e) {
			logger.error("updateExecute:",e);
		}
	}

	/**
	 * 回滚
	 */
	public void executeRollback(){
		try {
			logger.info("回滚flow-web-admin版本信息开始");
			SystemVersion systemVersion = versionUpdateService.getSystemRollBackingVersionInfo(COMPONENT);
			if(systemVersion !=null){
				String basePath =VersionUpdateJob.class.getClassLoader().getResource("/").getPath();
				 InputStream in = new BufferedInputStream(new FileInputStream(  
		                    new File(basePath+"param.properties")));  
		            Properties prop = new Properties();  
		            prop.load(in);  
		            prop.put("id", systemVersion.getId().toString());
		            prop.put("httpurl", systemVersion.getDeploymentUrl());
		            in.close();
		            FileOutputStream fos = new FileOutputStream(new File(basePath+"param.properties"));
		            prop.store(fos, "save success");
		            fos.close();
		            logger.info("chmod 777 "+basePath+"*.sh");
				RunShellUtil.chmodShell("chmod 777 "+basePath+"*.sh");
				RunShellUtil.runShell( "at now + 1 minutes -f " + basePath+"rollback.sh");
				logger.info("回滚flow-web-admin版本信息结束");
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	public VersionUpdateService getVersionUpdateService() {
		return versionUpdateService;
	}



	public void setVersionUpdateService(VersionUpdateService versionUpdateService) {
		this.versionUpdateService = versionUpdateService;
	}



	public String getWar_download_url() {
		return war_download_url;
	}



	public void setWar_download_url(String war_download_url) {
		this.war_download_url = war_download_url;
	}



	public String getPublickey() {
		return publickey;
	}



	public void setPublickey(String publickey) {
		this.publickey = publickey;
	}



	public String getCustomerName() {
		return customerName;
	}



	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}



	public String getPassword() {
		return password;
	}



	public void setPassword(String password) {
		this.password = password;
	}
	
	
}
