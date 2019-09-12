package com.lyh.server.init;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.axdoctor.tools.redis.RedisUtil;
import com.lyh.common.cache.redis.ServerNodeCache;
import com.lyh.common.cache.redis.UserTokenCache;

@Component
public class InitUserTokenCache {

	@Autowired
	RedisUtil redisUtil;
	@Autowired
	ServerNodeCache serverNodeCache;
	
	@Bean("userTokenCache")
	@PostConstruct
	public UserTokenCache userTokenCache(){
		UserTokenCache userTokenCache = new UserTokenCache();
		userTokenCache.setRedisUtil(redisUtil);
		userTokenCache.setServerNodeCache(serverNodeCache);
		return userTokenCache;
	}
}
