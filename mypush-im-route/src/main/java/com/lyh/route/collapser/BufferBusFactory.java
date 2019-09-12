//package com.lyh.route.collapser;
//
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//
//import com.lyh.route.collapser.oper.BufferOperation;
///**
// * 单例模式工厂
// * @author liyanhui
// *
// */
//public class BufferBusFactory {
//
//	private static final Map<String,BufferBus> busCache = new ConcurrentHashMap<String, BufferBus>();
//	private static Object lock = new Object();
//	
//	public static BufferBus getInstance(BufferOperation bufferOperation, String destIp, String destPort) {
//		String address = destIp+":"+destPort;
//		BufferBus instance = busCache.get(address);
//        if(instance == null) {
//            synchronized (lock) {
//                if (instance == null) {
//                    instance = new BufferBus(bufferOperation, destIp, destPort);
//                    busCache.put(address, instance);
//                }
//            }
//        }
//        return instance;
//	}
//	
//	public static BufferBus getInstance(BufferOperation bufferOperation, int size, int preiod, String destIp, String destPort) {
//		String address = destIp+":"+destPort;
//		BufferBus instance = busCache.get(address);
//        if(instance == null) {
//            synchronized (lock) {
//                if (instance == null) {
//                    instance = new BufferBus(bufferOperation, size, preiod, destIp, destPort);
//                    busCache.put(address, instance);
//                }
//            }
//        }
//        return instance;
//    }
//}
