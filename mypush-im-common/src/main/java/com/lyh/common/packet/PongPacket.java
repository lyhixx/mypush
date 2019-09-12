package com.lyh.common.packet;

import cn.hutool.core.util.RandomUtil;

public class PongPacket extends BaseRequestPacket{
	public PongPacket() {
		super.setMsgId(RandomUtil.randomStringUpper(10));
		super.setMsgType("PONG");
	}
	
}
