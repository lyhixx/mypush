package com.lyh.server.tcp;

import java.net.InetSocketAddress;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.lyh.Server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
@Component
public class TcpServer implements Server{
	
    @Autowired
    @Qualifier("serverBootstrap")
    private ServerBootstrap serverBootstrap;

    @Autowired
    @Qualifier("tcpSocketAddress")
    private InetSocketAddress tcpPort;

    private Channel serverChannel;
	@Override
	public void start() throws InterruptedException {
		ChannelFuture future =  serverBootstrap.bind(tcpPort).sync().channel().closeFuture().sync();
		if(future.isSuccess()){
			System.out.println("启动 mypush tcp server 成功");
		}
		serverChannel = future.channel();
	}

	@Override
	public void stop() {
		serverChannel.close();
        serverChannel.parent().close();
	}

}
