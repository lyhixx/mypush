package com.lyh.client.tcp;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;

public class TcpClientHandleInitializer extends ChannelInitializer<Channel> {
//    private final TcpClientProtoBufHandle tcpClientProtoBufHandle = new TcpClientProtoBufHandle();
    private final TcpClientHandle tcpClientHandle = new TcpClientHandle();

	@Override
	protected void initChannel(Channel ch) throws Exception {
		ch.pipeline()
        //10秒没发送消息，触发事件->发PING
		//35秒没接收到任何消息，包括PONG，触发事件->断开连接-重连
        .addLast(new IdleStateHandler(35, 10, 0))

        //心跳解码
        //.addLast(new HeartbeatEncode())

        // google Protobuf 编解码
        //拆包解码
//        .addLast(new ProtobufVarint32FrameDecoder())
//        .addLast(new ProtobufDecoder(MyPushRequestProto.MyPushReqProtocol.getDefaultInstance()))
        //
        //拆包编码
//        .addLast(new ProtobufVarint32LengthFieldPrepender())
//        .addLast(new ProtobufEncoder())
        .addLast(new StringDecoder())
        .addLast(new StringEncoder())
        .addLast(tcpClientHandle)
;
	}

}
