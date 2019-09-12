package com.lyh.gate.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;

import com.alibaba.druid.pool.DruidDataSource;

@Component
@PropertySource(value = "classpath:/application.properties")
public class JdbcConfig {

	@Value("${jdbc.driver}")
	private String driver;
	
	@Value("${jdbc.username}")
	private String username;
	
	@Value("${jdbc.url}")
	private String url;
	
	@Value("${jdbc.password}")
	private String password;
	
	@Bean
    public DataSource dataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(url);
        dataSource.setUsername(username);//用户名
        dataSource.setPassword(password);//密码
        dataSource.setDriverClassName(driver);
        dataSource.setInitialSize(20);
        dataSource.setMaxActive(200);
        dataSource.setMinIdle(20);
        dataSource.setMaxWait(60000);
        dataSource.setValidationQuery("SELECT 1");
        dataSource.setTestOnBorrow(false);
        dataSource.setTestWhileIdle(true);
        dataSource.setPoolPreparedStatements(false);
        return dataSource;
    }
	
	@Bean
	public JdbcTemplate jdbcTemplate() {
		return new JdbcTemplate(dataSource());
	}
	
	@Bean("txManager")
	public DataSourceTransactionManager tm() {
		return new DataSourceTransactionManager(dataSource());
	}
}
