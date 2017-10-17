package com.yzx.flow.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.yzx.flow.config.properties.RabbitMQProperties;

/**
 * RabbitMQ
 * @author Liulei
 *
 */
@Configuration
public class RabbitMQConfig {
	
	/**
	 * Queue bean的名字（非队列名）
	 */
	public static final String QUEUE_BEAN_NAME = "flow-queue";
	public static final String RABBITTEMPLATE_BEAN_NAME = "queueTemplate";
	
	@Autowired
	private RabbitMQProperties rabbitMQProperties;
	
	/**
	 * 设置连接信息
	 * @return
	 */
	@Bean  
    public ConnectionFactory connectionFactory() {  
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();  
        connectionFactory.setAddresses(rabbitMQProperties.getAddresses());  
        connectionFactory.setUsername(rabbitMQProperties.getUserName());  
        connectionFactory.setPassword(rabbitMQProperties.getPassword());  
        connectionFactory.setPublisherConfirms(true); //必须要设置  
        return connectionFactory; 
    }
	
	
//	@Bean
//	public RabbitAdmin admin(ConnectionFactory connectionFactory) {
//		return new RabbitAdmin(connectionFactory);
//	}
	
	
	@Bean(RABBITTEMPLATE_BEAN_NAME)
//	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public RabbitTemplate rabbitTemplate() {  
	    RabbitTemplate template = new RabbitTemplate(connectionFactory());  
	    return template;  
	}
	
	/**
	 * queue
	 * @return
	 */
	@Bean(QUEUE_BEAN_NAME)
    public Queue queue() {
        return new Queue(rabbitMQProperties.getQueueName(), true, false, false);
    }
	
	/**
	 * fanout exchange
	 * @return
	 */
	@Bean
	public FanoutExchange fanoutExchange() {
        return new FanoutExchange(rabbitMQProperties.getExchange(), true, false);
    }

	/**
	 * binding queue to a fanout exchange
	 * @param queue
	 * @param fanoutExchange
	 * @return
	 */
	@Bean
    Binding bindingExchangeA(@Qualifier(QUEUE_BEAN_NAME) Queue queue, FanoutExchange fanoutExchange) {
        return BindingBuilder.bind(queue).to(fanoutExchange);
    }
	

}
