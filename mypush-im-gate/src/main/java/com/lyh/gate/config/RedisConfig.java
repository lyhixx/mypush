package com.lyh.gate.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import com.axdoctor.tools.redis.RedisPool;
import com.axdoctor.tools.redis.RedisUtil;
import com.lyh.common.cache.redis.ServerNodeCache;
import com.lyh.common.cache.redis.UserTokenCache;

@Component
@PropertySource(value= "classpath:/application.properties")
public class RedisConfig {
	@Value("${redis.host}")
	public String redisHost;
	
	public RedisPool redisPool(){
		RedisPool redisPool = new RedisPool();
		redisPool.setServerHostMaster(redisHost);
		redisPool.setMaxIdle(10);
		redisPool.setMaxWaitMills(200);
		redisPool.init();
		return redisPool;
	}
	@Bean("redisUtil")
	public RedisUtil redisUtil(){
		RedisUtil redisUtil = new RedisUtil();
		redisUtil.setJedisPool(redisPool());
		return redisUtil;
	}
	@Bean("redissonClient")
    public RedissonClient redissonClient() {
        Config config = new Config();
 
        SingleServerConfig singleServerConfig = config.useSingleServer();
//        String schema = "redis://";
        singleServerConfig.setAddress(redisHost + ":" + 6379);
        singleServerConfig.setConnectionPoolSize(10);
        // 其他配置项都先采用默认值
        return Redisson.create(config);
    }
	
	@Bean("serverNodeCache")
	public ServerNodeCache serverNodeCache(RedisUtil redisUtil,RedissonClient redissonClient){
		ServerNodeCache serverNodeCache = new ServerNodeCache();
		serverNodeCache.setRedisUtil(redisUtil);
		serverNodeCache.setRedissonClient(redissonClient);
		serverNodeCache.init();
		return serverNodeCache;
	}
	
	@Bean("userTokenCache")
	public UserTokenCache userTokenCache(RedisUtil redisUtil,ServerNodeCache serverNodeCache){
		UserTokenCache userTokenCache = new UserTokenCache();
		userTokenCache.setRedisUtil(redisUtil);
		userTokenCache.setServerNodeCache(serverNodeCache);
		return userTokenCache;
	}
}
