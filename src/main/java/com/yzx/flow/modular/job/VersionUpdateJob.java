package com.yzx.flow.modular.job;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.yzx.flow.common.persistence.model.SystemVersion;
import com.yzx.flow.core.util.zk.ZookeeperUtil;
import com.yzx.flow.modular.job.service.VersionUpdateService;

@Component
public class VersionUpdateJob {
	
	
	@Value("${war_download_url}")
	public  String war_download_url ;
	@Autowired
	private VersionUpdateService versionUpdateService;
	@Autowired
	private ZookeeperUtil zookeeperUtil;
	private static final Logger logger = LoggerFactory.getLogger(VersionUpdateJob.class);
	/**
	 * 执行任务  每天0点扫描一次
	 */
	@Scheduled(cron="0 0 0 * * ?")
	public void execute() {
		try {
			List<SystemVersion> versionList = versionUpdateService.getSystemUpdateingVersionInfoList();
			if(versionList!=null && !versionList.isEmpty()){
				String order  = "";
				for(SystemVersion systemVersion : versionList){
					String comp = systemVersion.getComponent();
					String version = systemVersion.getVersion();
					if(StringUtils.isNotBlank(comp) && StringUtils.isNotBlank(version)){
						order = order + comp + ",";//控制顺序
					}
				}
				//如果这个节点上没有值则执行，有值就不执行(可能其他web已经执行)
				String date = zookeeperUtil.getDate(VersionUpdateExecute.parentNode+VersionUpdateExecute.orderNode);
				if(StringUtils.isBlank(date)){
					zookeeperUtil.setDate(VersionUpdateExecute.parentNode+VersionUpdateExecute.orderNode, order,true);
				}
				//先存order再存单个
				for(SystemVersion systemVersion : versionList){
					String comp = systemVersion.getComponent();
					String nodeDate = zookeeperUtil.getDate(VersionUpdateExecute.parentNode+comp);
					if(StringUtils.isBlank(nodeDate)){
						zookeeperUtil.setDate(VersionUpdateExecute.parentNode+comp, systemVersion.getStatus()+"",true);//控制分布式
					}
				}
			}
			} catch (Exception e) {
			logger.error(e.getMessage());
		}
		
	}
}
