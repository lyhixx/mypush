package com.lyh.gate.init;

import java.util.List;

import javax.annotation.PostConstruct;

import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lyh.common.cache.ServerNode;
import com.lyh.common.cache.redis.ServerNodeCache;
import com.lyh.gate.zk.ZkConfig;
@Component
public class InitServerNodeCache {
	@Autowired
	private ServerNodeCache serverNodeCache;
	@Autowired
	private ZkClient zkClient;
	@Autowired
	private ZkConfig zkConfig;
	@PostConstruct
	public void init() {
		//跳板机初始化 imserver服务器节点
		List<String> currentChilds = zkClient.getChildren(zkConfig.getZkRoot());
        serverNodeCache.deleteAll();
        for(String server:currentChilds){
        	String[] arr = server.split(":");
        	String protocol = arr[0];
        	String ip = arr[1];
        	String tcpPort = arr[2];
        	String httpPort = arr[3];
        	//保存cache
        	ServerNode sn = new ServerNode();
            sn.setIp(ip);
            sn.setHttpPort(httpPort);
            sn.setTcpPort(tcpPort);
            sn.setProtocol(protocol);
            serverNodeCache.put(sn);
        }
        System.out.println("网关初始化imserver服务器节点,currentChilds="+currentChilds.toString());
	}
}
