package com.lyh.route.collapser.pool;

import com.lyh.route.collapser.oper.BufferOperation;
/**
 * bus池配置
 * @author liyanhui
 *
 */
public class BufferBusPoolConfig {

	private int maxTotal;
	private int defaultTotal;
	private String destIp;
	private String destPort;
	private BufferOperation bufferOperation;
	private int size;
	private int preiod;
	
	public int getMaxTotal() {
		return maxTotal;
	}
	public BufferBusPoolConfig setMaxTotal(int maxTotal) {
		this.maxTotal = maxTotal;
		return this;
	}
	public int getDefaultTotal() {
		return defaultTotal;
	}
	public BufferBusPoolConfig setDefaultTotal(int defaultTotal) {
		this.defaultTotal = defaultTotal;
		return this;
	}
	public String getDestIp() {
		return destIp;
	}
	public BufferBusPoolConfig setDestIp(String destIp) {
		this.destIp = destIp;
		return this;
	}
	public String getDestPort() {
		return destPort;
	}
	public BufferBusPoolConfig setDestPort(String destPort) {
		this.destPort = destPort;
		return this;
	}
	public int getSize() {
		return size;
	}
	public BufferBusPoolConfig setSize(int size) {
		this.size = size;
		return this;
	}
	public int getPreiod() {
		return preiod;
	}
	public BufferBusPoolConfig setPreiod(int preiod) {
		this.preiod = preiod;
		return this;
	}
	public BufferOperation getBufferOperation() {
		return bufferOperation;
	}
	public BufferBusPoolConfig setBufferOperation(BufferOperation bufferOperation) {
		this.bufferOperation = bufferOperation;
		return this;
	}
	
}
