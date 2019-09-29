package com.lyh.server.apollo;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfig;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfigChangeListener;
@Component
public class ApolloListener {

	@ApolloConfig
	private Config config;

	@ApolloConfigChangeListener
	private void onChange(ConfigChangeEvent changeEvent) {
		System.out.println("接收到apollo配置更新事件"+JSON.toJSONString(changeEvent));
	}
	
	@PostConstruct
	private void printConfig(){
		System.out.println("打印初始化的apollo配置"+JSON.toJSONString(config));
	}
}
