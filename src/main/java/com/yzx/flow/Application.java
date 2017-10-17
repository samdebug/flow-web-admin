
package com.yzx.flow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * SpringBoot方式启动类
 *
 * @author liuyufeng
 * @Date 2017/5/21 12:06
 */
@SpringBootApplication
@EnableScheduling
public class Application {

    protected final static Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        logger.info("Application is sussess!");
    }
}
