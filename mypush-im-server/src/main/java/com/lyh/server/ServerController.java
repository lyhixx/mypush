package com.lyh.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lyh.common.packet.ChatSinglePacket;
import com.lyh.common.protocol.binary.BinaryRequestDecoder;

import cn.hutool.json.JSONObject;
import io.netty.channel.Channel;

/**
 * 路由
 */
@Controller
@RequestMapping("/server/api")
public class ServerController {

	@RequestMapping(value = "recieveMsg", method = RequestMethod.POST)
	@ResponseBody
	public String recieveMsg(HttpServletRequest request) throws IOException {
		byte[] buffer = new byte[8 * 1024];
		ServletInputStream sis = request.getInputStream();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int length = 0;
		while ((length = sis.read(buffer)) > 0) {
			baos.write(buffer, 0, length);
		}
		byte[] body = baos.toByteArray();
		List<ChatSinglePacket> list = BinaryRequestDecoder.decode(body, ChatSinglePacket.class);
		for(ChatSinglePacket csp:list){
			String toMid = csp.getToMid();
			Channel c = SessionHolder.getChannel(Integer.valueOf(toMid));
			c.writeAndFlush(csp.getContent());
		}
		
		JSONObject data = new JSONObject();
		data.put("resCode", 200);
		data.put("resDesc", "成功");
		data.put("list", list);
		return data.toString();
	}
}
