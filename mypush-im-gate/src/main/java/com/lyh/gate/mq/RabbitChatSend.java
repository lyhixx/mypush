package com.lyh.gate.mq;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.axdoctor.tools.redis.RedisUtil;
import com.lyh.common.exception.MyPushException;
import com.lyh.common.packet.ChatSinglePacket;
import com.lyh.common.utils.KryoUtil;

import redis.clients.jedis.Jedis;

@Service("rabbitChatSend")
public class RabbitChatSend {

	@Autowired
	private RabbitTemplate rabbitTemplate;
	@Autowired
	private RedisUtil redisUtil;

	public void send(String exchange, String routingKey, ChatSinglePacket obj) throws AmqpException{
		Jedis jedis = redisUtil.getResource();
		try {
			Message message = MessageBuilder.withBody(JSON.toJSONBytes(obj))
					.setContentType(MessageProperties.CONTENT_TYPE_TEXT_PLAIN).setCorrelationId(String.valueOf(obj.getChatId())).setHeader("retry", 0).build();
			CorrelationData data = new CorrelationData(String.valueOf(obj.getChatId()));
			// 缓存关系对
			jedis.setex(data.getId().getBytes(), 30, KryoUtil.writeObjectToByteArray(message));
			rabbitTemplate.convertAndSend(exchange, routingKey, message, data);
		} catch (AmqpException e) {
			throw e;// 发送失败率较高，熔断
		} finally {
			redisUtil.returnResource(jedis);
		}
	}

	public void reSend(String exchange, String routingKey, CorrelationData data) throws AmqpException,Exception{
		Jedis jedis = redisUtil.getResource();
		try {
			byte[] bytes = jedis.get(data.getId().getBytes());
			//落表，mongo 考虑数据量不大，成本低
			if (bytes == null) {
				// 消息丢失
				System.out.println("消息丢失，data=" + data.getId());
				throw new MyPushException(MyPushException.ErrorCode.RECHAT_FAIL.getCode(),
						MyPushException.ErrorCode.RECHAT_FAIL.getDes());
			}
			Message message = KryoUtil.readFromByteArray(bytes);
			rabbitTemplate.convertAndSend(exchange, routingKey, message, data);
		} catch (Exception e) {
			throw e;
		} finally {
			redisUtil.returnResource(jedis);
		}

	}
}
