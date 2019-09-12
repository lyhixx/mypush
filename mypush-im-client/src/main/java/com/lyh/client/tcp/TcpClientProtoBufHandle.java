package com.lyh.client.tcp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lyh.common.protocol.MyPushRequestProto.MyPushReqProtocol;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@ChannelHandler.Sharable
public class TcpClientProtoBufHandle extends SimpleChannelInboundHandler<MyPushReqProtocol> {

	private final static Logger LOGGER = LoggerFactory.getLogger(TcpClientProtoBufHandle.class);

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, MyPushReqProtocol msg) throws Exception {
		LOGGER.info("收到msg={}", msg.toString());
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("{}客户端连上了!",ctx.channel());
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("[{}] 客户端断开了，重新连接!",ctx.channel());
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        LOGGER.info("[{}]触发 {}事件!",ctx.channel(),evt.toString());
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		 LOGGER.error(cause.getMessage(), cause);
	}

}
