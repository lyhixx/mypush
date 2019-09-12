/**
 * 
 */
package com.lyh.common.protocol.binary;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * 将request的body序列化 并加上协议头
 */
public class BinaryRequestEncoder extends BinaryBase{

	public static ByteBuffer encode(byte[] body){
		int bodyLen = 0;
		if (body != null)
		{
			bodyLen = body.length;
		}
		boolean isCompress = true;
		boolean is4ByteLength = true;
		boolean isEncrypt = true;
		boolean isHasSynSeq = true;
		//协议版本号
		byte version = MyPushProtocol.VERSION;
		
		//协议标志位mask
		byte maskByte = encodeEncrypt(version, isEncrypt);
		maskByte = encodeCompress(maskByte, isCompress);
		maskByte = encodeHasSynSeq(maskByte, isHasSynSeq);
		maskByte = encode4ByteLength(maskByte, is4ByteLength);
		
		//byteBuffer的总长度是 = 1byte协议版本号+1byte消息标志位+4byte同步序列号(如果是同步发送则都4byte同步序列号,否则无4byte序列号)+4byte消息的长度+消息体
		int allLen = 1+1;
		if(isHasSynSeq){
			allLen += 4;
		}
		allLen += 4+bodyLen;
		ByteBuffer buffer = ByteBuffer.allocate(allLen);
		//设置字节序
		ByteOrder byteOrder = ByteOrder.BIG_ENDIAN;
		buffer.order(byteOrder);
		buffer.put(version);
		buffer.put(maskByte);
		//同步发送设置4byte，同步序列号;
		if(isHasSynSeq){
			buffer.putInt(0);
		}
		buffer.putInt(bodyLen);
		buffer.put(body);
		return buffer;
	}
	
}
