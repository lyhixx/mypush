package com.lyh.gate.zk;

import java.util.List;

import javax.annotation.PostConstruct;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lyh.common.cache.ServerNode;
import com.lyh.common.cache.redis.ServerNodeCache;
@Component
public class ZkListen {

	@Autowired
	private ZkClient zkClient;
	@Autowired
	private ZkConfig zkConfig;
	@Autowired
	private ServerNodeCache serverNodeCache;
	
	@PostConstruct
	public void init(){
		System.out.println("初始化zk。。。");
		Thread thread = new Thread(new Zklistener(zkClient,zkConfig));
		thread.setName("zk-listener");
		thread.start() ;
	}
	
	class Zklistener implements Runnable{
		
		private ZkClient zkClient;
		private ZkConfig zkConfig;
		
		public Zklistener(ZkClient zkClient, ZkConfig zkConfig) {
			this.zkClient = zkClient;
			this.zkConfig = zkConfig;
		}

		@Override
		public void run() {
			System.out.println("开始监听zk。。。");
			zkClient.subscribeChildChanges(zkConfig.getZkRoot(), new IZkChildListener() {
	            @Override
	            public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
	                //更新所有缓存/先删除 再新增
	                System.out.println(parentPath+" "+currentChilds.toString());
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
	                
	            }
	        });
			
		}
		
	}
}
