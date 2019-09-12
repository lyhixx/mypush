package com.lyh.route.mq;

import java.nio.ByteBuffer;
import java.util.List;

import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.lyh.common.cache.ServerNode;
import com.lyh.common.cache.redis.UserTokenCache;
import com.lyh.common.packet.ChatSinglePacket;
import com.lyh.common.protocol.binary.BinaryRequestEncoder;
import com.lyh.common.utils.KryoUtil;
import com.lyh.route.collapser.BufferBus;
import com.lyh.route.collapser.oper.BufferOperationHttpRequest;
import com.lyh.route.collapser.pool.BufferBusPoolFactory;

import cn.hutool.core.collection.CollectionUtil;

@Component
@RabbitListener(queues = "queue.im.master", containerFactory="rabbitListenerContainerFactory")
public class ChatHandler {
	
	@Autowired
	UserTokenCache userTokenCache;
	
	@Autowired
	RedissonClient redissonClient;
	
	@Autowired
	private RabbitTemplate rabbitTemplate;
	
	private String url = "http://[ip]:[port]/server/api/recieveMsg";
	
	@RabbitHandler
	public void handle(String message){
		RBloomFilter<String> bloomFilter = redissonClient.getBloomFilter("mypush:im:bloom:mq:repeat");
		try {
			System.out.println("接收到消息"+message);
			//2. 落聊天数据 1.新消息 2.发送成功 3.发送失败、留言 4.采用mongo
			//2. 手动ack
			ChatSinglePacket cp = JSON.parseObject(message, ChatSinglePacket.class);
			//{"chatId":602509243043549184,"content":"喔喔喔喔日瑞日日日日","fromMid":"2","protocol":"tcp","time":12345,"toMid":"1001","token":"df672d10b0d14e1cb289be465b6c1303"}
			//1. 去重处理 加一个消息表去重，单表只留一周数据，mongo数据归档
			if(bloomFilter.contains(cp.getChatId()+"")){
				//消息id重复，扔掉不处理
				System.out.println("接收到消息，重复消息"+message);
				return;
			}
			//设置bitmap
			bloomFilter.add(cp.getChatId()+"");
			String toMid = cp.getToMid();
			List<ServerNode> list = userTokenCache.findConnectServerInfoListByMid(Integer.valueOf(toMid));
			if(CollectionUtil.isEmpty(list)){
				//没有mid对应的服务器信息，进入留言
				System.out.println("没有mid对应的服务器信息，进入留言,toMid="+toMid);
			}
			for(ServerNode sn:list){
				byte[] body = KryoUtil.writeObjectToByteArray(cp);
				ByteBuffer buffer = BinaryRequestEncoder.encode(body);
				BufferBus bus = BufferBusPoolFactory.getBufferBus(new BufferOperationHttpRequest(url.replace("[ip]", sn.getIp()).replace("[port]", sn.getHttpPort())), sn.getIp(), sn.getHttpPort(), buffer.array().length);
				bus.write(buffer.array(), 0, buffer.array().length);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("接收到消息，处理失败，进入重试队列，重试3次"+message);
			ChatSinglePacket cp = JSON.parseObject(message, ChatSinglePacket.class);
			Message msg = MessageBuilder.withBody(JSON.toJSONBytes(cp))
					.setContentType(MessageProperties.CONTENT_TYPE_TEXT_PLAIN).setCorrelationId(String.valueOf(cp.getChatId())).setHeader("retry", 1).build();
			CorrelationData data = new CorrelationData(String.valueOf(cp.getChatId()));
			rabbitTemplate.convertAndSend("exchange.im.retry", "1121", msg, data);
		}
	}
}
