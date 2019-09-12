package com.lyh.server.zk;

import java.net.UnknownHostException;

import javax.annotation.PostConstruct;

import org.I0Itec.zkclient.ZkClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import com.lyh.common.utils.IpConfigUtil;
import com.lyh.server.config.AppConfig;
import com.lyh.server.config.TcpConfig;
import com.lyh.server.config.ZkConfig;
@Component
@PropertySource(value= "classpath:/application.properties")
public class ZkRegistry {
	@Autowired
	private ZkConfig zkConfig;
	@Autowired
	private ZkClient zkClient;
	@Autowired
	private TcpConfig tcpConfig;
	@Autowired
	private AppConfig appConfig;
	
	@PostConstruct
	public void init() throws UnknownHostException{
		String ip = IpConfigUtil.getIpByNetName(appConfig.getEth());
		if(StringUtils.isBlank(ip)){
			throw new UnknownHostException("获取机器ip失败，服务关闭");
		}
		 //创建父节点
        createRootNode();
        //是否要将自己注册到 ZK
        if (zkConfig.isZkSwitch()){
            String path = zkConfig.getZkRoot() + "/"+appConfig.getMyProtocol()+":" + ip + ":" + tcpConfig.getTcpPort() + ":" + tcpConfig.getServerPort();
            createNode(path);
        }
	}
	
	/**
     * 创建父级节点
     */
    public void createRootNode(){
        boolean exists = zkClient.exists(zkConfig.getZkRoot());
        if (exists){
            return;
        }

        //创建 root
        zkClient.createPersistent(zkConfig.getZkRoot()) ;
    }

    /**
     * 写入指定节点 临时目录
     *
     * @param path
     */
    public void createNode(String path) {
        zkClient.createEphemeral(path);
    }
}
