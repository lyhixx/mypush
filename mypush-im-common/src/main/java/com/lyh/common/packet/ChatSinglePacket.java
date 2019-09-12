package com.lyh.common.packet;

import java.io.Serializable;

/**
 * 单聊
 * @author liyanhui
 *
 */
public class ChatSinglePacket implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4434214292683140434L;
	private String fromMid;
	private String toMid;
	private String token;
	private String content;
	private long time;
	private String protocol;
	private long chatId;
	public String getFromMid() {
		return fromMid;
	}
	public void setFromMid(String fromMid) {
		this.fromMid = fromMid;
	}
	public String getToMid() {
		return toMid;
	}
	public void setToMid(String toMid) {
		this.toMid = toMid;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public String getProtocol() {
		return protocol;
	}
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	public long getChatId() {
		return chatId;
	}
	public void setChatId(long chatId) {
		this.chatId = chatId;
	}
	
}
