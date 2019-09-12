package com.lyh.server.tcp;


import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;

@Component
@Qualifier("tcpServerInitializer")
public class TcpServerInitializer extends ChannelInitializer<Channel> {

//    private final TcpServerProtoBufHandle tcpServerProtoBufHandle = new TcpServerProtoBufHandle() ;
    private final TcpServerHandle tcpServerHandle = new TcpServerHandle() ;

    @Override
    protected void initChannel(Channel ch) throws Exception {

        ch.pipeline()
                //35秒 没有调用channelRead()方法，触发事件
                .addLast(new IdleStateHandler(35, 0, 0))
                // google Protobuf 编解码
//                .addLast(new ProtobufVarint32FrameDecoder())
//                .addLast(new ProtobufDecoder(MyPushRequestProto.MyPushReqProtocol.getDefaultInstance()))
//                .addLast(new ProtobufVarint32LengthFieldPrepender())
//                .addLast(new ProtobufEncoder())
//                .addLast(tcpServerHandle);
                .addLast(new StringDecoder())
                .addLast(new StringEncoder())
                .addLast(tcpServerHandle);
    }
}
