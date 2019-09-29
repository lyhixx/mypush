package com.lyh;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import com.lyh.server.config.AppConfig;
import com.lyh.server.tcp.TcpServer;

@SpringBootApplication
@EnableApolloConfig
public class MyPushServerApplication {

	public static void main(String[] args) throws InterruptedException {
		ConfigurableApplicationContext context = SpringApplication.run(MyPushServerApplication.class, args);
		AppConfig appConfig = context.getBean(AppConfig.class);
		if(appConfig.getMyProtocol().equals("tcp")){
			Server tcpServer = context.getBean(TcpServer.class);
			tcpServer.start();
		}
	}
}
