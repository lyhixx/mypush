package com.lyh.client;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.lyh.client.config.AppConfig;
import com.lyh.client.tcp.TcpClient;

@SpringBootApplication
public class MyPushClientApplication {

	@Autowired
	AppConfig appConfig;

	static AppConfig appConfigClone;

	@PostConstruct
	void init() {
		appConfigClone = appConfig;
	}

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(MyPushClientApplication.class, args);
		// TcpClient client = context.getBean(TcpClient.class);
		// try {
		// client.start("192.168.3.7", 38088);
		// } catch (Exception e) {
		// e.printStackTrace();
		// try {
		// client.close();
		// } catch (InterruptedException e1) {
		// e1.printStackTrace();
		// }
		// }

		for (int i = appConfigClone.getUserIdStart(); i < appConfigClone.getUserIdEnd(); i++) {
			TcpClient client = new TcpClient();
			try {
				client.start("127.0.0.1", 8080, 38088, i);
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("#####异常");
				try {
					client.close();
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
		System.out.println("启动 Client 服务成功");
	}
}
