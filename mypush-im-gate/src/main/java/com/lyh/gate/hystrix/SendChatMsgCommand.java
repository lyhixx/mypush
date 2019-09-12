package com.lyh.gate.hystrix;

import com.alibaba.fastjson.JSON;
import com.lyh.common.exception.MyPushException;
import com.lyh.common.packet.ChatSinglePacket;
import com.lyh.gate.GatewayService;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolKey;
import com.netflix.hystrix.HystrixThreadPoolProperties;

import cn.hutool.json.JSONObject;

public class SendChatMsgCommand extends HystrixCommand<String> {
	private final String group;
	private GatewayService service;
	private ChatSinglePacket chat;

	public SendChatMsgCommand(String group, ChatSinglePacket chat) {
		super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(group))
				.andCommandKey(HystrixCommandKey.Factory.asKey("sendChatMsgCommand"))
				.andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("sendChatMsgCommandThreadPool"))
				.andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
						.withCircuitBreakerRequestVolumeThreshold(5)
						.withCircuitBreakerErrorThresholdPercentage(60)//错误率超过60%,快速失败
						.withExecutionTimeoutInMilliseconds(2000)
						.withCircuitBreakerSleepWindowInMilliseconds(30000)//30s后放部分流量过去重试
						.withMetricsRollingStatisticalWindowInMilliseconds(1000))
				.andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter()
						.withCoreSize(100)// 这里我们设置了线程池大小为10
						.withMaxQueueSize(0))
		);
		this.group = group;
		this.chat = chat;
	}

	@Override
	protected String run() throws Exception {
		System.out.println("SendChatMsgCommand 发送消息："+JSON.toJSONString(chat));
		service.sendChat(chat);
		return null;
	}

	@Override
	protected String getFallback() {
		JSONObject res = new JSONObject();
		res.put("resCode", MyPushException.ErrorCode.SYSTEM_BUSY_MOCK.getCode());
		res.put("resDesc", MyPushException.ErrorCode.SYSTEM_BUSY_MOCK.getDes());
		return res.toString();
	}
	
	public void setService(GatewayService service) {
	    this.service = service;
	}
}
