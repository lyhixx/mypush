package com.lyh.server;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.lyh.common.cache.ServerNode;
import com.lyh.common.cache.redis.UserTokenCache;
import com.lyh.common.cache.redis.UserTokenCache.LoginInfo;

@Component
public class MyPushServerService {

	@Autowired
	private UserTokenCache userTokenCache;
	@Autowired
	private  ServerNode myself;
	
	public void clearUserAllConnectInfo(int mid){
		try {
			//清除用户的服务器连接信息
			List<LoginInfo> list = userTokenCache.getTokenList(mid);
			for(LoginInfo info:list){
				userTokenCache.removeConnectServer(mid, info.getProtocol());
			}
			//清除channlemap
			SessionHolder.removeChannel(mid);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void clearUserConnectInfo(int mid, String protocol){
		try {
			//清除用户的服务器连接信息
			userTokenCache.removeConnectServer(mid, protocol);
			//清除channlemap
			SessionHolder.removeChannel(mid);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void saveConnectInfo(int mid, String protocol){
		LoginInfo info = new LoginInfo();
		info.setMid(mid);
		info.setProtocol(protocol);
		userTokenCache.saveConnectServerInfo(info, myself.hashCode());
	}
}
