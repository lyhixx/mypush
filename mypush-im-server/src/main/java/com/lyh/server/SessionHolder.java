package com.lyh.server;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.netty.channel.Channel;

public class SessionHolder {

	public static final Map<Integer, Channel> channelHolder = new ConcurrentHashMap<Integer, Channel>(16);
	
	public static void addChannel(int mid, Channel channel) {
		channelHolder.put(mid, channel);
	}
	
	public static void removeChannel(int mid) {
		channelHolder.remove(mid);
	}
	
	public static Channel getChannel(int mid) {
		return channelHolder.get(mid);
	}
}
