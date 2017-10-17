package com.yzx.flow.config;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alibaba.druid.pool.DruidDataSource;
import com.baomidou.mybatisplus.enums.DBType;
import com.baomidou.mybatisplus.plugins.PaginationInterceptor;
import com.yzx.flow.config.properties.MasterDruidProperties;
import com.yzx.flow.config.properties.SlaveDruidProperties;
import com.yzx.flow.core.aop.dbrouting.ChooseDataSource;

/**
 * MybatisPlus配置
 *
 * @author liuyufeng
 * @Date 2017/5/20 21:58
 */
@Configuration
@MapperScan(basePackages = {"com.yzx.flow.modular.*.dao", "com.yzx.flow.common.persistence.dao"})
public class MybatisPlusConfig {

    @Autowired
    MasterDruidProperties masterDruidProperties;
    @Autowired
    SlaveDruidProperties slaveDruidProperties;

    /**
     * mybatis-plus分页插件
     */
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
        paginationInterceptor.setDialectType(DBType.MYSQL.getDb());
        return paginationInterceptor;
    }

    /**
     * druid数据库连接池
     */
/*    @Bean(initMethod = "init")
    public DruidDataSource dataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        druidProperties.coinfig(dataSource);
        return dataSource;
    }*/
    @Bean
    public ChooseDataSource dataSource() {
        DruidDataSource dataSourceWrite = new DruidDataSource();
        masterDruidProperties.coinfig(dataSourceWrite);
        try {
			dataSourceWrite.init();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       
        DruidDataSource dataSourceRead = new DruidDataSource();
        slaveDruidProperties.coinfig(dataSourceRead);
        try {
			dataSourceWrite.init();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        Map<Object,Object> map = new HashMap<>();
        map.put("write", dataSourceWrite);
        map.put("read", dataSourceRead);
        ChooseDataSource chooseDataSource = new ChooseDataSource();
        chooseDataSource.setTargetDataSources(map);
        chooseDataSource.setDefaultTargetDataSource(dataSourceWrite);
        
        return chooseDataSource;
    }
}
