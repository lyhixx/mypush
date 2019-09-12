package com.lyh.common.packet;

import com.lyh.common.constant.MyConstants;

import cn.hutool.core.util.RandomUtil;

public class FirstMeetPacket extends BaseRequestPacket{

	public FirstMeetPacket() {
		setMsgId(RandomUtil.randomStringUpper(10));
		setMsgType(MyConstants.MsgType.PING);
	}
}
