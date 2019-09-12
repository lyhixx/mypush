package com.lyh.client.tcp;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lyh.client.tcp.packet.GoogleProtocolVO;
import com.lyh.common.packet.PingPacket;
import com.lyh.common.utils.HttpClientUtil;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.DefaultThreadFactory;

@Component
public class TcpClient {
    private final static Logger LOGGER = LoggerFactory.getLogger(TcpClient.class);
    
	private EventLoopGroup group = new NioEventLoopGroup(0, new DefaultThreadFactory("mypush-work"));
	private SocketChannel channel;
	
	private String url_register = "http://[ip]:[port]/api/user/register";
	private String url_login = "http://[ip]:[port]/api/user/login";
	
    static final AttributeKey<String> ATTR_KEY_MID = AttributeKey.valueOf("mid");
    static final Map<String, PingPacket> pingHolder  = new ConcurrentHashMap<String, PingPacket>();
    
	public void start(String ip, int httpPort, int tcpPort, int mid) throws InterruptedException,Exception {
		//注册
		{
			HttpClient httpClient = HttpClientUtil.getHttpClient();
			HttpPost httpPost = new HttpPost(url_register.replace("[ip]", ip).replace("[port]", httpPort+"")+"?id="+mid);
			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();
			String result = EntityUtils.toString(httpEntity, "utf-8");	
			JSONObject obj = JSONObject.parseObject(result);
			System.out.println("注册结果，mid="+mid+",result="+result);
			if(obj.getInteger("resCode")!=200){
				throw new Exception("注册异常");
			}
		}
		//登录
		{
			HttpClient httpClient = HttpClientUtil.getHttpClient();
			HttpPost httpPost = new HttpPost(url_login.replace("[ip]", ip).replace("[port]", httpPort+"")+"?mid="+mid+"&protocol=tcp");
			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();
			String result = EntityUtils.toString(httpEntity, "utf-8");	
			JSONObject obj = JSONObject.parseObject(result);
			System.out.println("登录结果，mid="+mid+",result="+result);
			if(obj.getInteger("resCode")!=200){
				throw new Exception("登录异常");
			}
			String token = obj.getJSONObject("token").getString("token");
			PingPacket ping = new PingPacket();
			ping.setMid(mid);
			ping.setProtocol("tcp");
			ping.setToken(token);
			pingHolder.put(mid+"", ping);
		}
		
		Bootstrap bootstrap = new Bootstrap();
		bootstrap.group(group).channel(NioSocketChannel.class).option(ChannelOption.SO_KEEPALIVE, true).handler(new TcpClientHandleInitializer());
		ChannelFuture future = null;
		future = bootstrap.connect(ip, tcpPort).sync();
		if (!future.isSuccess()) {
			throw new Exception();
		}
		System.out.println("启动 mypush client 成功");
		channel = (SocketChannel) future.channel();
		channel.attr(ATTR_KEY_MID).set(mid+"");
	}
	
	public void close() throws InterruptedException {
        if (channel != null){
            channel.close();
        }
    }
	
	 /**
     * 发送消息字符串
     *
     * @param msg
     */
    public void sendStringMsg(String msg) {
        ByteBuf message = Unpooled.buffer(msg.getBytes().length);
        message.writeBytes(msg.getBytes());
        ChannelFuture future = channel.writeAndFlush(message);
        future.addListener((ChannelFutureListener) channelFuture ->
                LOGGER.info("客户端手动发消息成功={}", msg));

    }

    /**
     * 发送 Google Protocol 编解码字符串
     *
     * @param googleProtocolVO
     */
    public void sendGoogleProtocolMsg(GoogleProtocolVO googleProtocolVO) {

//        MyPushRequestProto.MyPushReqProtocol protocol = MyPushRequestProto.MyPushReqProtocol.newBuilder()
//                .setRequestId(googleProtocolVO.getRequestId())
//                .setReqMsg(googleProtocolVO.getMsg())
//                .setType(MyConstants.CommandType.MSG)
//                .build();


        ChannelFuture future = channel.writeAndFlush(JSON.toJSONString(googleProtocolVO));
        future.addListener((ChannelFutureListener) channelFuture ->
                LOGGER.info("客户端手动发送 Google Protocol 成功={}", googleProtocolVO.toString()));

    }
}
