package com.yzx.flow.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * RabbitMQ配置
 *
 * @author Liulei
 */
@Component
@ConfigurationProperties(prefix = RabbitMQProperties.PREFIX)
public class RabbitMQProperties {

    public static final String PREFIX = "rabbitmq";
	
    private String addresses;
    
    private String userName;
    
    private String password;
    
    private String queueName;
    
    private String exchange;
    

	public String getAddresses() {
		return addresses;
	}

	public void setAddresses(String addresses) {
		this.addresses = addresses;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getQueueName() {
		return queueName;
	}

	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}

	public String getExchange() {
		return exchange;
	}

	public void setExchange(String exchange) {
		this.exchange = exchange;
	}

}
