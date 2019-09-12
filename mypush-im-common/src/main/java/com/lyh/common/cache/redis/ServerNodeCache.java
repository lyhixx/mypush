package com.lyh.common.cache.redis;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;

import com.alibaba.fastjson.JSON;
import com.axdoctor.tools.redis.RedisUtil;
import com.lyh.common.cache.ServerNode;
import com.lyh.common.constant.MyConstants;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.RandomUtil;
import redis.clients.jedis.Jedis;
public class ServerNodeCache{

	private RedisUtil redisUtil;
	
	private RedissonClient redissonClient;
    
    private final static String hashKey = "mypush:im:serverNodeCache";
    
    private static RReadWriteLock rwlock;
    
    public void init() {
    	rwlock = redissonClient.getReadWriteLock("mypush:im:rwlock");
    }
    
    public void put(ServerNode sn) {
    	try {
			if(rwlock.writeLock().tryLock(30, 10, TimeUnit.SECONDS)){
				redisUtil.hset(hashKey, sn.hashCode()+"", JSON.toJSONString(sn));
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			rwlock.writeLock().unlock();
		}
    }
    
    public ServerNode get(int serverNodeIndex) {
    	try {
			if(rwlock.readLock().tryLock(30, 10, TimeUnit.SECONDS)){
				String res = redisUtil.hget(hashKey, serverNodeIndex+"");
				if(StringUtils.isNotBlank(res)){
					return JSON.parseObject(res, ServerNode.class);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			rwlock.readLock().unlock();
		}
    	return null;
    }
    
    public boolean isServerNodeExsit(int serverNodeIndex) {
    	try {
			if(rwlock.readLock().tryLock(30, 10, TimeUnit.SECONDS)){
				if(StringUtils.isNotBlank(redisUtil.hget(hashKey, serverNodeIndex+""))){
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			rwlock.readLock().unlock();
		}
    	return false;
    }
    
	public long size() {
		long res = 0;
		try {
			if(rwlock.readLock().tryLock(30, 10, TimeUnit.SECONDS)){
				Jedis jedis = redisUtil.getResource();
				try {
					res = jedis.hlen(hashKey);
				} catch (Exception e) {
				} finally {
					redisUtil.returnResource(jedis);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			rwlock.readLock().unlock();
		}
		return res;
	}

	public List<ServerNode> getAll() {
		List<ServerNode> list = new ArrayList<ServerNode>();
		try {
			if(rwlock.readLock().tryLock(30, 10, TimeUnit.SECONDS)){
				Jedis jedis = redisUtil.getResource();
				try {
					Map<String, String> map = jedis.hgetAll(hashKey);
					for (String key : map.keySet()) {
						list.add(JSON.parseObject(map.get(key), ServerNode.class));
				    }
				} catch (Exception e) {
				} finally {
					redisUtil.returnResource(jedis);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			rwlock.readLock().unlock();
		}
		return list;
	}
	
	public ServerNode getRandomNode(String protocol){
		try {
			List<ServerNode> snlist = getAll();
			if(CollectionUtil.isEmpty(snlist)){
				return null;
			}
			List<ServerNode> list = new ArrayList<>();
			if(MyConstants.ProtocolType.WEBSOCKET.equals(protocol)){
				//找出ws协议服务节点
				for(ServerNode sn:snlist){
					if(sn.getProtocol().equals(MyConstants.ProtocolType.WEBSOCKET)){
						list.add(sn);
					}
				}
			}
			if(MyConstants.ProtocolType.TCP.equals(protocol)){
				//找出tcp服务节点
				for(ServerNode sn:snlist){
					if(sn.getProtocol().equals(MyConstants.ProtocolType.TCP)){
						list.add(sn);
					}
				}
			}
			int ran = RandomUtil.randomInt();
			return list.get(ran%list.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void deleteAll(){
		try {
			if(rwlock.writeLock().tryLock(30, 10, TimeUnit.SECONDS)){
				redisUtil.del(hashKey);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			rwlock.writeLock().unlock();
		}
	}

	public RedisUtil getRedisUtil() {
		return redisUtil;
	}

	public void setRedisUtil(RedisUtil redisUtil) {
		this.redisUtil = redisUtil;
	}

	public RedissonClient getRedissonClient() {
		return redissonClient;
	}

	public void setRedissonClient(RedissonClient redissonClient) {
		this.redissonClient = redissonClient;
	}
}
