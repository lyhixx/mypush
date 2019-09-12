package com.lyh.server.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource(value= "classpath:/application.properties")
public class AppConfig {
	@Value("${my.protocol}")
	public String myProtocol;
	@Value("${net.ip.eth}")
	public String eth;

	public String getMyProtocol() {
		return myProtocol;
	}

	public String getEth() {
		return eth;
	}
	
}
