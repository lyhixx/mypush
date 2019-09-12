package com.lyh.route.collapser.pool;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.lyh.route.collapser.BufferBus;
import com.lyh.route.collapser.oper.BufferOperation;

public class BufferBusPoolFactory {

	private static final Map<String,BufferBusPoolManager> busPoolCache = new ConcurrentHashMap<String, BufferBusPoolManager>();
	private static Object lock = new Object();
	
	public static BufferBus getBufferBus(BufferOperation bufferOperation, String destIp, String destPort, int target){
		String address = destIp+":"+destPort;
		BufferBusPoolManager bpm = busPoolCache.get(address);
		if(bpm==null){
			synchronized (lock) {
                if (bpm == null) {
                	BufferBusPoolConfig config = new BufferBusPoolConfig()
            				.setDefaultTotal(10)
            				.setDestIp(destIp)
            				.setDestPort(destPort)
            				.setMaxTotal(100)
            				.setPreiod(1000)//100ms触发写网络io
            				.setSize(8*1024)//写网络io的缓冲大小，单位字节
            				.setBufferOperation(bufferOperation);
                	bpm = new BufferBusPoolManager(config);
                	busPoolCache.put(address, bpm);
                }
            }
		}
		return bpm.get(target);
	}
}
