package com.yzx.flow.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

import com.yzx.flow.config.evn.IAvatorRepository;
import com.yzx.flow.config.evn.LinuxCondition;
import com.yzx.flow.config.evn.WindowCondition;

@Configuration
@ConfigurationProperties(prefix = AvatorProperties.AVATOR_PREFIX)
public class AvatorProperties {
	
	public static final String AVATOR_PREFIX = "manager.user.avator";
	
		
	private String winRepository;
	private String linuxRepository;
	
	
	@Bean
	@Conditional(LinuxCondition.class)
	public IAvatorRepository getLinuxAvatorRepository() {
		return new IAvatorRepository.LinuxAvatorRepository(linuxRepository);
	}
	
	@Bean
	@Conditional(WindowCondition.class)
	public IAvatorRepository getWindowAvatorRepository() {
		return new IAvatorRepository.WindowAvatorRepository(winRepository);
	}

	public String getWinRepository() {
		return winRepository;
	}

	public void setWinRepository(String winRepository) {
		this.winRepository = winRepository;
	}

	public String getLinuxRepository() {
		return linuxRepository;
	}

	public void setLinuxRepository(String linuxRepository) {
		this.linuxRepository = linuxRepository;
	}
	  
}
