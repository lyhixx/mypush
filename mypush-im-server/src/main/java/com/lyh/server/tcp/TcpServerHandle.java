package com.lyh.server.tcp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.lyh.common.packet.BaseRequestPacket;
import com.lyh.common.packet.PongPacket;
import com.lyh.server.MyPushServerService;
import com.lyh.server.SessionHolder;
import com.lyh.server.util.NettyAttrUtil;
import com.lyh.server.util.SpringBeanFactory;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Function:
 * 字符串
 */
@ChannelHandler.Sharable
public class TcpServerHandle extends ChannelInboundHandlerAdapter {

    private final static Logger LOGGER = LoggerFactory.getLogger(TcpServerHandle.class);

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		LOGGER.info("收到channel={},msg={}", ctx.channel(),msg.toString());
		
		BaseRequestPacket rp  = JSONObject.parseObject((String)msg, BaseRequestPacket.class);
		//获取是否第一次收到该渠道消息
		if(NettyAttrUtil.getIsHadFirstTime(ctx.channel())){
			//设置第一次接收该渠道消息
			NettyAttrUtil.setFirstTime(ctx.channel());
			//关联渠道和用户
			NettyAttrUtil.setMid(ctx.channel(), rp.getMid());
			//关联协议和用户
			NettyAttrUtil.setProtocol(ctx.channel(), rp.getProtocol());
			//关联token和用户
			NettyAttrUtil.setToken(ctx.channel(), rp.getToken());
			//保存连接信息
			MyPushServerService myPushServerService = SpringBeanFactory.getBean(MyPushServerService.class);
			myPushServerService.saveConnectInfo(rp.getMid(),rp.getProtocol());
			//保存用户和渠道map
			SessionHolder.addChannel(rp.getMid(), ctx.channel());
		}
		if(rp.getMsgType().equals("PING")){
			//设置收到心跳的时间
			NettyAttrUtil.setReaderTime(ctx.channel(), System.currentTimeMillis());
			//心跳，回复pong
			PongPacket pong = new PongPacket();
			ctx.channel().writeAndFlush(JSONObject.toJSONString(pong));
//			.addListeners((ChannelFutureListener) future -> {
//                if (!future.isSuccess()) {
//                    LOGGER.error("IO error,close Channel");
//                    future.channel().close();
//                }
//            }) ;
		}
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("{}在线!",ctx.channel());
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("[{}] 掉线!",ctx.channel());
        Integer mid = NettyAttrUtil.getMid(ctx.channel());
        String protocol = NettyAttrUtil.getProtocol(ctx.channel());
        //清除连接信息、channelmap信息,保持登录信息
        if(mid==null){
    		//连上之后，未发送任何消息，立即断开，不需要清理
    		LOGGER.info("{}没有mid",ctx.channel());
    		return ;
    	}
        MyPushServerService myPushServerService = SpringBeanFactory.getBean(MyPushServerService.class);
    	myPushServerService.clearUserConnectInfo(mid, protocol);
    	ctx.channel().close();
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        LOGGER.info("{}触发 {}事件 channelId={}，35秒没有收到该渠道的任何信息，包括心跳!",ctx.channel(),evt.toString(),ctx.channel().id());
        Long lastReadTime = NettyAttrUtil.getReaderTime(ctx.channel());
        long now = System.currentTimeMillis();
        if (lastReadTime == null || now - lastReadTime > 30000){
        	//尝试发送PING，看是否可以成功送出
//        	PingPacket p = new PingPacket();
//            ctx.writeAndFlush(JSONObject.toJSONString(p)).addListeners((ChannelFutureListener) future -> {
//            	LOGGER.info("####channelId={}",future.channel().id());
//            	if (!future.isSuccess()) {
//                    LOGGER.error("关闭该channel");
//                    future.channel().close();
//                }
//            }) ;
        	//清除连接信息、channelmap信息,保持登录信息
        	Integer mid = NettyAttrUtil.getMid(ctx.channel());
        	if(mid==null){
        		//连上之后，一直网络不好，ping不通serverNode，导致没有在channel上记录mid，则没有connect和channel信息，不需要清楚操作，直接关闭channel
        		LOGGER.info("{}没有mid",ctx.channel());
        		ctx.channel().close();
        		return ;
        	}
        	MyPushServerService myPushServerService = SpringBeanFactory.getBean(MyPushServerService.class);
        	myPushServerService.clearUserConnectInfo(mid, NettyAttrUtil.getProtocol(ctx.channel()));
        	ctx.channel().close();
        }
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		 LOGGER.error(cause.getMessage(), cause);
	}

	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("{}收到握手请求，新加入客户端,地址：{}",ctx.channel(),ctx.channel().remoteAddress());
        //检查mid是否已有服务器连接，有的话去掉(无法确认身份)
	}

	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("{}客户端退出，地址：{}",ctx.channel(),ctx.channel().remoteAddress());
	}
    
	
}
