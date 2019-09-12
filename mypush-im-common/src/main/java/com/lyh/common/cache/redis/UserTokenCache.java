package com.lyh.common.cache.redis;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.axdoctor.tools.redis.RedisUtil;
import com.lyh.common.cache.ServerNode;
import com.lyh.common.constant.MyConstants;
import com.lyh.common.exception.MyPushException;
public class UserTokenCache{

	private RedisUtil redisUtil;
	private ServerNodeCache serverNodeCache;
    
    private final static String tokenTcp = "mypush:im:token:tcp:";
    private final static String tokenWs = "mypush:im:token:ws:";
//    private final static String isLoginPreix = "mypush:im:userIsLogin";
    private final static String connectHashKeyTcp = "mypush:im:connect:tcp";
    private final static String connectHashKeyWs = "mypush:im:connect:ws";

//    private final static int offset = 102400;//预计共连接10w用户
    
//    public void init(){
//    	Jedis jedis = redisUtil.getResource();
//    	try {
//    		jedis.setbit(isLoginPreix, offset, false);
//		} catch (Exception e) {
//		} finally {
//			redisUtil.returnResource(jedis);
//		}
//    }
    
	public LoginInfo saveOrUpdateToken(LoginInfo info) throws Exception {
		String uuid = UUID.randomUUID().toString().replace("-", "");
		info.setToken(uuid);
		if(MyConstants.ProtocolType.TCP.equals(info.getProtocol())){
			//tcp协议
			redisUtil.set(tokenTcp + info.getMid()+"", JSON.toJSONString(info));
			return info;
		}
		if(MyConstants.ProtocolType.WEBSOCKET.equals(info.getProtocol())){
			//ws协议
			redisUtil.set(tokenWs + info.getMid()+"", JSON.toJSONString(info));
			return info;
		}
		throw new MyPushException(MyPushException.ErrorCode.UNKOWN_PROTOCOL.getCode(), MyPushException.ErrorCode.UNKOWN_PROTOCOL.getDes());
	}
	
	public LoginInfo getToken(int mid, String protocol) {
		if(MyConstants.ProtocolType.TCP.equals(protocol)){
			//tcp协议
			String res = redisUtil.get(tokenTcp + mid+"");
			if(StringUtils.isNotBlank(res)){
				return JSON.parseObject(res, LoginInfo.class);
			}
			return null;
		}
		if(MyConstants.ProtocolType.WEBSOCKET.equals(protocol)){
			//ws协议
			String res = redisUtil.get(tokenWs + mid+"");
			if(StringUtils.isNotBlank(res)){
				return JSON.parseObject(res, LoginInfo.class);
			}
			return null;
		}
		throw new MyPushException(MyPushException.ErrorCode.UNKOWN_PROTOCOL.getCode(), MyPushException.ErrorCode.UNKOWN_PROTOCOL.getDes());
	}
	
	public List<LoginInfo> getTokenList(int mid) {
		List<LoginInfo> list = new ArrayList<>();
		//tcp协议
		String res = redisUtil.get(tokenTcp + mid+"");
		if(StringUtils.isNotBlank(res)){
			list.add(JSON.parseObject(res, LoginInfo.class));
		}
		//ws协议
		res = redisUtil.get(tokenWs + mid+"");
		if(StringUtils.isNotBlank(res)){
			list.add(JSON.parseObject(res, LoginInfo.class));
		}
		return list;
	}
	
	public void removeConnectServer(int mid, String protocol){
		if(MyConstants.ProtocolType.TCP.equals(protocol)){
			//tcp协议
			redisUtil.hdel(connectHashKeyTcp, mid+"");
			return ;
		}
		if(MyConstants.ProtocolType.WEBSOCKET.equals(protocol)){
			//ws协议
			redisUtil.hdel(connectHashKeyWs, mid+"");
			return ;
		}
		throw new MyPushException(MyPushException.ErrorCode.UNKOWN_PROTOCOL.getCode(), MyPushException.ErrorCode.UNKOWN_PROTOCOL.getDes());
	}
	
	public void saveConnectServerInfo(LoginInfo info, int serverNodeIndex) {
		if(MyConstants.ProtocolType.TCP.equals(info.getProtocol())){
			//tcp协议
			redisUtil.hset(connectHashKeyTcp, info.getMid()+"", serverNodeIndex+"");
			return ;
		}
		if(MyConstants.ProtocolType.WEBSOCKET.equals(info.getProtocol())){
			//ws协议
			redisUtil.hset(connectHashKeyWs, info.getMid()+"", serverNodeIndex+"");
			return ;
		}
		throw new MyPushException(MyPushException.ErrorCode.UNKOWN_PROTOCOL.getCode(), MyPushException.ErrorCode.UNKOWN_PROTOCOL.getDes());
	}
	
	public ServerNode getConnectServerInfo(LoginInfo info) {
		String serverNodeIndex = null;
		if(MyConstants.ProtocolType.TCP.equals(info.getProtocol())){
			//tcp协议
			serverNodeIndex = redisUtil.hget(connectHashKeyTcp, info.getMid()+"");
		}
		if(MyConstants.ProtocolType.TCP.equals(info.getProtocol())){
			//ws协议
			serverNodeIndex = redisUtil.hget(connectHashKeyWs, info.getMid()+"");
		}
		if(StringUtils.isNotBlank(serverNodeIndex)){
			return serverNodeCache.get(Integer.valueOf(serverNodeIndex));
		}
		return null;
	}
	
	public List<ServerNode> findConnectServerInfoListByMid(int mid){
		List<ServerNode> list = new ArrayList<>();
		String serverNodeIndex = redisUtil.hget(connectHashKeyTcp, mid+"");
		if(StringUtils.isNotBlank(serverNodeIndex)){
			list.add(serverNodeCache.get(Integer.valueOf(serverNodeIndex)));
		}
		String serverNodeIndex2 = redisUtil.hget(connectHashKeyWs, mid+"");
		if(StringUtils.isNotBlank(serverNodeIndex2)){
			list.add(serverNodeCache.get(Integer.valueOf(serverNodeIndex2)));
		}
		return list;
	}
	
	public boolean isLogined(LoginInfo info){
		if(MyConstants.ProtocolType.TCP.equals(info.getProtocol())){
			//tcp协议
			String res = redisUtil.get(tokenTcp + info.getMid()+"");
			if(StringUtils.isBlank(res)){
				return false;
			}
			return true;
		}
		if(MyConstants.ProtocolType.WEBSOCKET.equals(info.getProtocol())){
			//ws协议
			String res = redisUtil.get(tokenWs + info.getMid()+"");
			if(StringUtils.isBlank(res)){
				return false;
			}
			return true;
		}
		throw new MyPushException(MyPushException.ErrorCode.UNKOWN_PROTOCOL.getCode(), MyPushException.ErrorCode.UNKOWN_PROTOCOL.getDes());
	}
	
	public boolean isLoginAndTokenVaild(LoginInfo info){
		if(MyConstants.ProtocolType.TCP.equals(info.getProtocol())){
			//tcp协议
			String res = redisUtil.get(tokenTcp + info.getMid()+"");
			if(StringUtils.isBlank(res)){
				return false;
			}
			LoginInfo tokenInfo = JSON.parseObject(res, LoginInfo.class);
			if(!tokenInfo.getToken().equals(info.getToken())){
				return false;
			}
			return true;
		}
		if(MyConstants.ProtocolType.WEBSOCKET.equals(info.getProtocol())){
			//ws协议
			String res = redisUtil.get(tokenWs + info.getMid()+"");
			if(StringUtils.isBlank(res)){
				return false;
			}
			LoginInfo tokenInfo = JSON.parseObject(res, LoginInfo.class);
			if(!tokenInfo.getToken().equals(info.getToken())){
				return false;
			}
			return true;
		}
		throw new MyPushException(MyPushException.ErrorCode.UNKOWN_PROTOCOL.getCode(), MyPushException.ErrorCode.UNKOWN_PROTOCOL.getDes());
	}
	
	public boolean isConnect(LoginInfo info){
		if(MyConstants.ProtocolType.TCP.equals(info.getProtocol())){
			//tcp协议
			String serverNodeIndex = redisUtil.hget(connectHashKeyTcp, info.getMid()+"");
			if(StringUtils.isBlank(serverNodeIndex) || !serverNodeCache.isServerNodeExsit(Integer.valueOf(serverNodeIndex))){
				return false;
			}
			return true;
		}
		if(MyConstants.ProtocolType.WEBSOCKET.equals(info.getProtocol())){
			//ws协议
			String serverNodeIndex = redisUtil.hget(connectHashKeyWs, info.getMid()+"");
			if(StringUtils.isBlank(serverNodeIndex) || !serverNodeCache.isServerNodeExsit(Integer.valueOf(serverNodeIndex))){
				return false;
			}
			return true;
		}
		throw new MyPushException(MyPushException.ErrorCode.UNKOWN_PROTOCOL.getCode(), MyPushException.ErrorCode.UNKOWN_PROTOCOL.getDes());
	}

	public RedisUtil getRedisUtil() {
		return redisUtil;
	}

	public void setRedisUtil(RedisUtil redisUtil) {
		this.redisUtil = redisUtil;
	}
	
	public ServerNodeCache getServerNodeCache() {
		return serverNodeCache;
	}

	public void setServerNodeCache(ServerNodeCache serverNodeCache) {
		this.serverNodeCache = serverNodeCache;
	}

	public static class LoginInfo{
		private int mid;
		private String token;
		private String protocol;
		
		public LoginInfo() {
			super();
		}

		public LoginInfo(int mid, String token, String protocol) {
			this.mid = mid;
			this.token = token;
			this.protocol = protocol;
		}
		
		public LoginInfo(int mid, String protocol) {
			this.mid = mid;
			this.protocol = protocol;
		}
		public int getMid() {
			return mid;
		}
		public void setMid(int mid) {
			this.mid = mid;
		}
		public String getToken() {
			return token;
		}
		public void setToken(String token) {
			this.token = token;
		}
		public String getProtocol() {
			return protocol;
		}
		public void setProtocol(String protocol) {
			this.protocol = protocol;
		}
	}
	
}
