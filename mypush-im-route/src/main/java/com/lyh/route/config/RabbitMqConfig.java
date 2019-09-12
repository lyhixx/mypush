package com.lyh.route.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = "classpath:/application.properties")
public class RabbitMqConfig {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(RabbitMqConfig.class);

	@Value("${spring.rabbitmq.host}")
	private String host;
	@Value("${spring.rabbitmq.port}")
	private String port;
	@Value("${spring.rabbitmq.username}")
	private String username;
	@Value("${spring.rabbitmq.password}")
	private String password;
	@Value("${spring.rabbitmq.vhost}")
	private String virtualHost;
	@Value("${spring.rabbitmq.confirm}")
	private Boolean publisherConfirms;
//	@Bean
//	public Queue queue() {
//		return new Queue("queue.test.default", true);
//	}
//
//	@Bean
//	TopicExchange exchange() {
//		return new TopicExchange("amq.topic", true, false);
//	}
//
//	@Bean
//	Binding bindingExchangeQueue() {
//		return BindingBuilder.bind(queue()).to(exchange()).with("1111");
//	}
	
	@Bean
	public ConnectionFactory connectionFactory() {
		CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
		connectionFactory.setHost(host);
		connectionFactory.setPort(Integer.valueOf(port));
		connectionFactory.setUsername(username);
		connectionFactory.setPassword(password);
		connectionFactory.setVirtualHost(virtualHost);
		return connectionFactory;
	}
	
	@Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setPrefetchCount(100);
        factory.setAcknowledgeMode(AcknowledgeMode.AUTO);
        return factory;
    }
	
	@Bean
	public RabbitTemplate rabbitTemplate() {
		// 若使用confirm-callback或return-callback，必须要配置publisherConfirms或publisherReturns为true
		// 每个rabbitTemplate只能有一个confirm-callback和return-callback，如果这里配置了，那么写生产者的时候不能再写confirm-callback和return-callback
		// 使用return-callback时必须设置mandatory为true，或者在配置中设置mandatory-expression的值为true
		CachingConnectionFactory connectionFactory = (CachingConnectionFactory) connectionFactory();
		connectionFactory.setPublisherConfirms(publisherConfirms);
		connectionFactory.setPublisherReturns(true);
		RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
//		rabbitTemplate.setExchange("amq.topic");
		rabbitTemplate.setMandatory(true);
		// /**
		// * 如果消息没有到exchange,则confirm回调,ack=false
		// * 如果消息到达exchange,则confirm回调,ack=true
		// * exchange到queue成功,则不回调return
		// * exchange到queue失败,则回调return(需设置mandatory=true,否则不回回调,消息就丢了)
		// */
		rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
			@Override
			public void confirm(CorrelationData correlationData, boolean ack, String cause) {
				System.out.println("1111111"+correlationData);
				if (ack) {
					LOGGER.info("消息发送成功:correlationData({}),ack({}),cause({})", correlationData, ack, cause);
				} else {
					LOGGER.info("消息发送失败:correlationData({}),ack({}),cause({})", correlationData, ack, cause);
					//尝试重发，加入重试队列，梯度重试，达到一定次数，停止重试，并报警
				}
			}
		});
		rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
			@Override
			public void returnedMessage(Message message, int replyCode, String replyText, String exchange,
					String routingKey) {
				System.out.println("222222");
				LOGGER.info("消息丢失:exchange({}),route({}),replyCode({}),replyText({}),message:{}", exchange, routingKey,
						replyCode, replyText, message);
				//交换机发到queue丢失，让mq自行处理
			}
		});
		return rabbitTemplate;
	}
}
