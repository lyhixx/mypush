package com.lyh.client.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource(value= "classpath:/application.properties")
public class AppConfig {

	@Value("${user.id.start}")
	public int userIdStart;
	
	@Value("${user.id.end}")
	public int userIdEnd;

	public int getUserIdStart() {
		return userIdStart;
	}

	public int getUserIdEnd() {
		return userIdEnd;
	}
}
