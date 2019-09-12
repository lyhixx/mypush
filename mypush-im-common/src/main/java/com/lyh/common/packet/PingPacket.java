package com.lyh.common.packet;

import com.lyh.common.constant.MyConstants;

import cn.hutool.core.util.RandomUtil;

public class PingPacket extends BaseRequestPacket{

	public PingPacket() {
		setMsgId(RandomUtil.randomStringUpper(10));
		setMsgType(MyConstants.MsgType.PING);
	}
	
}
