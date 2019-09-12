package com.lyh.common.cache;

public class ServerNode {
	private String ip;
	private String tcpPort;
	private String httpPort;
	private String protocol;
	
	@Override
	public int hashCode() {
		return (ip+tcpPort+httpPort).hashCode();
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getTcpPort() {
		return tcpPort;
	}
	public void setTcpPort(String tcpPort) {
		this.tcpPort = tcpPort;
	}
	public String getHttpPort() {
		return httpPort;
	}
	public void setHttpPort(String httpPort) {
		this.httpPort = httpPort;
	}
	public String getProtocol() {
		return protocol;
	}
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	
}
