package com.lyh.gate;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lyh.common.cache.redis.UserTokenCache;
import com.lyh.common.cache.redis.UserTokenCache.LoginInfo;
import com.lyh.common.exception.MyPushException;
import com.lyh.common.packet.ChatSinglePacket;
import com.lyh.gate.dao.MpUserDao;
import com.lyh.gate.model.MpUser;
import com.lyh.gate.mq.RabbitChatSend;

@Service
public class GatewayService {

	@Autowired
	private RabbitChatSend rabbitChatSend;
	@Autowired
	private UserTokenCache userTokenCache;
	@Autowired
	MpUserDao mpUserDao;
	
	public boolean isLogin(LoginInfo info) {
		if(!userTokenCache.isLoginAndTokenVaild(info)){
        	//未登录
			throw new MyPushException(MyPushException.ErrorCode.INVALID_TOKEN.getCode(), MyPushException.ErrorCode.INVALID_TOKEN.getDes());
        }
		return true;
	}
	
	public boolean register(int mid){
		try {
			if(mpUserDao.getBeanById(Integer.valueOf(mid))!=null){
				return true;
			}
			MpUser mpUser = new MpUser();
			mpUser.setId(Integer.valueOf(mid));
			mpUser.setBizUid("biz"+mid);
			mpUserDao.save(mpUser);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public void sendChat(ChatSinglePacket chat) throws MyPushException{
		rabbitChatSend.send("exchange.im.master", "1111", chat);
	}
	
	public void reSendChat(CorrelationData data) {
		try {
			rabbitChatSend.reSend("exchange.im.master", "1111", data);
		} catch (AmqpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
