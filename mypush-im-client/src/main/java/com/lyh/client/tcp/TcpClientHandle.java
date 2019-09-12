package com.lyh.client.tcp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.lyh.common.packet.PingPacket;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

@ChannelHandler.Sharable
public class TcpClientHandle extends ChannelInboundHandlerAdapter {

	private final static Logger LOGGER = LoggerFactory.getLogger(TcpClientHandle.class);

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		LOGGER.info("收到msg={}", msg.toString());
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("{}客户端连上了!",ctx.channel());
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("[{}] 客户端断开了，重新连接!",ctx.channel());
//        for(;;){
//        	//重新登录
//        	
//        	Thread.sleep(1000);
//        }
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        LOGGER.info("[{}]触发 {}事件!",ctx.channel(),evt.toString());
        String mid = ctx.channel().attr(TcpClient.ATTR_KEY_MID).get();
        if (evt instanceof IdleStateEvent) {
        	IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
        	if (idleStateEvent.state() == IdleState.READER_IDLE) {
        		//35秒没接收到任何消息，包括PONG，触发事件->断开连接-重连
        		LOGGER.info("客户端主动关闭连接，并重连："+mid);
        		ctx.channel().close();
        		reConnect(Integer.valueOf(mid));
        	}
        	if (idleStateEvent.state() == IdleState.WRITER_IDLE) {
        		//10秒没发送消息，触发事件->发PING
        		PingPacket p = TcpClient.pingHolder.get(ctx.channel().attr(TcpClient.ATTR_KEY_MID).get());
                ctx.writeAndFlush(JSONObject.toJSONString(p));
                LOGGER.info("客户端心跳消息发送：" + JSONObject.toJSONString(p));
        	}
        } else {
            super.userEventTriggered(ctx, evt);
        }
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		 LOGGER.error(cause.getMessage(), cause);
	}
	
	private void reConnect(int mid){
		TcpClient client = new TcpClient();
		try {
			client.start("127.0.0.1", 8080, 38088, mid);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("#####异常");
			try {
				client.close();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
	}
	
}
