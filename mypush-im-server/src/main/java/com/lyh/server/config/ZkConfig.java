package com.lyh.server.config;

import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource(value= "classpath:/application.properties")
public class ZkConfig {

	@Value("${zk.root}")
    private String zkRoot;

    @Value("${zk.addr}")
    private String zkAddr;

    @Value("${zk.switch}")
    private boolean zkSwitch;
	
    @Value("${zk.connect.timeout}")
    private int zkConnectTimeout;

	public String getZkRoot() {
		return zkRoot;
	}

	public void setZkRoot(String zkRoot) {
		this.zkRoot = zkRoot;
	}

	public String getZkAddr() {
		return zkAddr;
	}

	public void setZkAddr(String zkAddr) {
		this.zkAddr = zkAddr;
	}

	public boolean isZkSwitch() {
		return zkSwitch;
	}

	public void setZkSwitch(boolean zkSwitch) {
		this.zkSwitch = zkSwitch;
	}

	public int getZkConnectTimeout() {
		return zkConnectTimeout;
	}

	public void setZkConnectTimeout(int zkConnectTimeout) {
		this.zkConnectTimeout = zkConnectTimeout;
	}
	
	@Bean
    public ZkClient buildZKClient() {
        return new ZkClient(getZkAddr(), getZkConnectTimeout());
    }
    
}
