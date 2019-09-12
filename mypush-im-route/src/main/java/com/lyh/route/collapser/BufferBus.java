package com.lyh.route.collapser;

import com.lyh.route.collapser.oper.BufferOperation;

public class BufferBus extends BufferRequest2{

	private String destIp;
	private String destPort;
	
	public BufferBus(BufferOperation bufferOperation, String destIp, String destPort) {
		super(bufferOperation);
		this.setDestIp(destIp);
		this.setDestPort(destPort);
	}
	
	public BufferBus(BufferOperation bufferOperation, int size, int preiod, String destIp, String destPort) {
		super(bufferOperation, size, preiod);
		this.setDestIp(destIp);
		this.setDestPort(destPort);
	}
	
	public String getDestIp() {
		return destIp;
	}

	public void setDestIp(String destIp) {
		this.destIp = destIp;
	}

	public String getDestPort() {
		return destPort;
	}

	public void setDestPort(String destPort) {
		this.destPort = destPort;
	}
}
