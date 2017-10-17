package com.yzx.flow.core.aop.dbrouting;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Order(1)
@Component
public class DataSourceAspect {
	private static Logger log = LoggerFactory.getLogger(DataSourceAspect.class);

	@Pointcut("@annotation(com.yzx.flow.core.aop.dbrouting.DataSource)")
	public void pointCut() {
	};

	@Before(value = "pointCut()")
	public void before(JoinPoint point) {
		DataSource dataSource = ((MethodSignature) point.getSignature()).getMethod().getAnnotation(DataSource.class);
		if (dataSource != null) {
			log.debug("设置数据源为{}" ,dataSource.value());
			HandleDataSource.putDataSource(dataSource.value());
		} else {
			log.error("设置数据源失败,将使用默认的数据源");
		}
	}

	@After(value = "pointCut()")
	public void after() {
		HandleDataSource.setDefault(DataSourceType.WRITE);
		log.debug("业务结束,设置默认数据源为write");
	}

	@AfterThrowing(value = "pointCut()")
	public void afterThrowing() {
		HandleDataSource.setDefault(DataSourceType.WRITE);
		log.debug("业务出现异常,设置数据源为为write") ;
	}

}