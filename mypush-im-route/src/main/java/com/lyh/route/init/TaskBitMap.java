package com.lyh.route.init;

import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.PostConstruct;

import org.redisson.api.RAtomicLong;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RFuture;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;

@Component
public class TaskBitMap {
	@Autowired
	RedissonClient redissonClient;
	private final static Timer watchdogForBloom = new Timer(true);
//	final static long cap = 1048576l;
	final static long cap = 1000l;
	/**
	 * 
	 * 初始化bitmap 2的20次方 128k，并且定时删除，替换新的bitmap
	 * @author liyanhui
	 * @date 2019年8月9日
	 */
	@PostConstruct
	public void init(){
		//初始化过滤器
		RBloomFilter<String> bloomFilter = redissonClient.getBloomFilter("mypush:im:bloom:mq:repeat");
		RAtomicLong atomicLong = redissonClient.getAtomicLong("mypush:im:bloom:mq:repeat:atomic");
		//初始化bimap
		if(bloomFilter.tryInit(cap, 0.03)){
			System.out.println("RBloomFilter 初始化100w成功");
		}
		//开守护线程，做清除任务
		watchdogForBloom.schedule(new TimerTask(){
			long last = atomicLong.get();
			@Override
			public void run() {
//				System.out.println("last="+last);
				if(atomicLong.compareAndSet(last, ++last)){
//					System.out.println("2 last="+last);
					RFuture<Boolean> future = bloomFilter.deleteAsync();
					future.addListener(new FutureListener<Boolean>() {

						@Override
						public void operationComplete(Future<Boolean> fu) throws Exception {
							if(fu.isSuccess()){
								if(bloomFilter.tryInit(cap, 0.03)){
									System.out.println("RBloomFilter 初始化100w成功");
								}
							}
						}
					});
				}
				last = atomicLong.get();
			}
			
		}, 3000, 60000);
	}
}
