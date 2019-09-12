package com.lyh.server.init;

import java.net.UnknownHostException;

import javax.annotation.PostConstruct;

import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.axdoctor.tools.redis.RedisUtil;
import com.lyh.common.cache.ServerNode;
import com.lyh.common.cache.redis.ServerNodeCache;
import com.lyh.common.utils.IpConfigUtil;
import com.lyh.server.config.AppConfig;
import com.lyh.server.config.TcpConfig;

@Component
public class InitServerNodeCache {
	
	@Autowired
	RedisUtil redisUtil;
	@Autowired
	RedissonClient redissonClient;
	@Autowired
	private TcpConfig tcpConfig;
	@Autowired
	private AppConfig appConfig;

	@Bean("serverNodeCache")
	@PostConstruct
	public ServerNodeCache serverNodeCache(){
		ServerNodeCache serverNodeCache = new ServerNodeCache();
		serverNodeCache.setRedisUtil(redisUtil);
		serverNodeCache.setRedissonClient(redissonClient);
		return serverNodeCache;
	}
	
	@Bean(name = "myself")
	@PostConstruct
	public ServerNode serverNode() throws UnknownHostException{
		String ip = IpConfigUtil.getIpByNetName(appConfig.getEth());
		ServerNode serverNode = new ServerNode();
		serverNode.setIp(ip);
		serverNode.setHttpPort(tcpConfig.getServerPort()+"");
		serverNode.setTcpPort(tcpConfig.getTcpPort()+"");
		serverNode.setProtocol(appConfig.getMyProtocol());
		return serverNode;
	}
}
