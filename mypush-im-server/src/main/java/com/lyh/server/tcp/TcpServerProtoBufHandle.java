package com.lyh.server.tcp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lyh.common.protocol.MyPushRequestProto;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Function:
 *
 * @author crossoverJie
 *         Date: 17/05/2018 18:52
 * @since JDK 1.8
 */
@ChannelHandler.Sharable
public class TcpServerProtoBufHandle extends SimpleChannelInboundHandler<MyPushRequestProto.MyPushReqProtocol> {

    private final static Logger LOGGER = LoggerFactory.getLogger(TcpServerProtoBufHandle.class);

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, MyPushRequestProto.MyPushReqProtocol msg) throws Exception {
		LOGGER.info("收到msg={}", msg.toString());
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("{}在线!",ctx.channel());
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("[{}] 掉线!",ctx.channel());
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        LOGGER.info("[{}]触发 {}事件!",ctx.channel(),evt.toString());
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		 LOGGER.error(cause.getMessage(), cause);
	}

	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("[{}]收到握手请求，新加入客户端",ctx.channel());
	}

	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("[{}]客户端退出",ctx.channel());
	}
    
	
}
