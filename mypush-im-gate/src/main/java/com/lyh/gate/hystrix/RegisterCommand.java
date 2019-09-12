package com.lyh.gate.hystrix;

import com.lyh.gate.GatewayService;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolKey;
import com.netflix.hystrix.HystrixThreadPoolProperties;

public class RegisterCommand extends HystrixCommand<Boolean> {
	private final String group;
	private GatewayService service;
	private int mid;

	public RegisterCommand(String group, int mid) {
		super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(group))
				.andCommandKey(HystrixCommandKey.Factory.asKey("registerCommand"))
				.andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("registerCommandThreadPool"))
				.andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
						.withCircuitBreakerRequestVolumeThreshold(5)
						.withFallbackIsolationSemaphoreMaxConcurrentRequests(100)
						.withCircuitBreakerErrorThresholdPercentage(60)//错误率超过60%,快速失败
						.withExecutionTimeoutInMilliseconds(2000)
						.withCircuitBreakerSleepWindowInMilliseconds(10000)//10s后放部分流量过去重试
						.withMetricsRollingStatisticalWindowInMilliseconds(1000))
				.andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter()
						.withCoreSize(100)// 这里我们设置了线程池大小为10
						.withMaxQueueSize(0))
		);
		this.group = group;
		this.mid = mid;
	}

	@Override
	protected Boolean run() throws Exception {
//		System.out.println("注册command："+mid);
		return service.register(mid);
	}

	@Override
	protected Boolean getFallback() {
		System.out.println("注册command 降级返回false："+mid);
		return false;
	}
	
	public void setService(GatewayService service) {
	    this.service = service;
	  }
}
