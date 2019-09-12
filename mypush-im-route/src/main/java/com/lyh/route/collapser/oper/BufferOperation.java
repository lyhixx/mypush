package com.lyh.route.collapser.oper;

public interface BufferOperation{

	public Object oper(byte b[], int off, int len);
	
	public void asyncOper(byte b[], int off, int len);
}
