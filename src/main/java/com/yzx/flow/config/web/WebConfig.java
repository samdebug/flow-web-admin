package com.yzx.flow.config.web;

import java.util.Properties;

import javax.servlet.DispatcherType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextListener;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.spring.stat.BeanTypeAutoProxyCreator;
import com.alibaba.druid.support.spring.stat.DruidStatInterceptor;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import com.yzx.flow.common.inputSafe.EscapeManager;
import com.yzx.flow.common.inputSafe.web.IEscapeRejectExceptionHandler;
import com.yzx.flow.common.inputSafe.web.XssFilter;
import com.yzx.flow.core.intercept.SessionInterceptor;
import com.yzx.flow.core.listener.ConfigListener;
import com.yzx.flow.core.util.licence.LicenceFilter;
import com.yzx.flow.core.util.zk.ZookeeperUtil;

/**
 * web 配置类
 *
 * @author liuyufeng
 * @date 2016年11月12日 下午5:03:32
 */
@Configuration
public class WebConfig {

	// @Autowired
	// private LicenceProperties licenceProperties;
	@Autowired
	private ZookeeperUtil baseEnvInit;

	@Autowired
	private EscapeManager escapeManager;

	/**
	 * druidServlet注册
	 */
	@Bean
	public ServletRegistrationBean druidServletRegistration() {
		ServletRegistrationBean registration = new ServletRegistrationBean(new StatViewServlet());
		registration.addUrlMappings("/druid/*");
		return registration;
	}

	/**
	 * druid数据库连接池监控
	 */
	@Bean
	public DruidStatInterceptor druidStatInterceptor() {
		return new DruidStatInterceptor();
	}

	/**
	 * druid数据库连接池监控
	 */
	@Bean
	public BeanTypeAutoProxyCreator beanTypeAutoProxyCreator() {
		BeanTypeAutoProxyCreator beanTypeAutoProxyCreator = new BeanTypeAutoProxyCreator();
		beanTypeAutoProxyCreator.setTargetBeanType(DruidDataSource.class);
		beanTypeAutoProxyCreator.setInterceptorNames("druidStatInterceptor");
		return beanTypeAutoProxyCreator;
	}

	/**
	 * 检测使用licence
	 * 
	 * @return
	 */
	// @Bean
	public FilterRegistrationBean licenceFilterRegistration() {
		FilterRegistrationBean registration = new FilterRegistrationBean(new LicenceFilter(baseEnvInit));
		registration.addUrlPatterns("/*");
		registration.setOrder(1);
		return registration;
	}

	/**
	 * xssFilter注册
	 */
	@Bean
	public FilterRegistrationBean xssFilterRegistration() {
		XssFilter filter = new XssFilter();
		filter.setEscapeManager(escapeManager);
		filter.setEscapeRejectExceptionHandler(new IEscapeRejectExceptionHandler.SimpleEscapeRejectExceptionHandler());
		FilterRegistrationBean registration = new FilterRegistrationBean(filter);
		registration.addUrlPatterns("/*");
		registration.setOrder(2);
		registration.setDispatcherTypes(DispatcherType.REQUEST);// 只需要拦截request方式的请求
		return registration;
	}

	/**
	 * RequestContextListener注册
	 */
	@Bean
	public ServletListenerRegistrationBean<RequestContextListener> requestContextListenerRegistration() {
		return new ServletListenerRegistrationBean<>(new RequestContextListener());
	}

	/**
	 * ConfigListener注册
	 */
	@Bean
	public ServletListenerRegistrationBean<ConfigListener> configListenerRegistration() {
		return new ServletListenerRegistrationBean<>(new ConfigListener());
	}

	/**
	 * session的拦截器，用在非controller层调用session
	 */
	@Bean
	public SessionInterceptor sessionInterceptor() {
		return new SessionInterceptor();
	}

	/**
	 * 验证码生成相关
	 */
	@Bean
	public DefaultKaptcha kaptcha() {
		Properties properties = new Properties();
		properties.put("kaptcha.border", "no");
		properties.put("kaptcha.border.color", "105,179,90");
		properties.put("kaptcha.textproducer.font.color", "blue");
		properties.put("kaptcha.image.width", "125");
		properties.put("kaptcha.image.height", "45");
		properties.put("kaptcha.textproducer.font.size", "45");
		properties.put("kaptcha.session.key", "code");
		properties.put("kaptcha.textproducer.char.length", "4");
		properties.put("kaptcha.textproducer.font.names", "宋体,楷体,微软雅黑");
		Config config = new Config(properties);
		DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
		defaultKaptcha.setConfig(config);
		return defaultKaptcha;
	}
}
