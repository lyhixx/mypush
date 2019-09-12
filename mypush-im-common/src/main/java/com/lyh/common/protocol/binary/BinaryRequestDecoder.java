/**
 * 
 */
package com.lyh.common.protocol.binary;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.lyh.common.exception.MyPushException;
import com.lyh.common.utils.KryoUtil;

/**
 */
public class BinaryRequestDecoder extends BinaryBase{
	
	private static Logger logger = Logger.getLogger(BinaryRequestDecoder.class);
	
	public static <T> List<T> decode(byte[] bodys, Class<?> E) throws MyPushException{
		List<T> list = new ArrayList<>();
		if(bodys.length<=0){
			throw new MyPushException("decode kyro解码失败，bodys is null");
		}
		ByteBuffer buffer = ByteBuffer.wrap(bodys);
		int readableLength = buffer.limit() - buffer.position();
		while(readableLength>0){
			//校验协议头
			if(!isHeaderLength(buffer)) {
				throw new MyPushException("decode kyro解码失败，校验协议头失败");
			}
			//获取第一个字节协议版本号;
			byte version = buffer.get();
			if(version != MyPushProtocol.VERSION){
				throw new MyPushException(MyPushException.ErrorCode.TCP_700.getCode(), MyPushException.ErrorCode.TCP_700.getDes());
			}
			//标志位
			byte maskByte = buffer.get();
			Integer synSeq = 0;
			//同步发送;
	//		if(decodeHasSynSeq(maskByte)){
				synSeq = buffer.getInt();
	//		}
			int bodyLen = buffer.getInt();
			//数据不正确，则抛出异常
			if (bodyLen < 0)
			{
				throw new MyPushException(MyPushException.ErrorCode.TCP_701.getCode(), MyPushException.ErrorCode.TCP_701.getDes());
			}
			readableLength = buffer.limit() - buffer.position();
			int validateBodyLen = readableLength - bodyLen;
			// 不够消息体长度(剩下的buffer组不了消息体)
			if (validateBodyLen < 0)
			{
				break;
			}
			byte[] body = new byte[bodyLen];
			try{
				buffer.get(body,0,bodyLen);
			}catch(Exception e){
				logger.error(e.toString());
			}
			readableLength = buffer.limit() - buffer.position();
			list.add((T) KryoUtil.readObjectFromByteArray(body, E));
			//byteBuffer的总长度是 = 1byte协议版本号+1byte消息标志位+4byte同步序列号(如果是同步发送则多4byte同步序列号,否则无4byte序列号)+4byte消息的长度+消息体的长度
			buffer = ByteBuffer.wrap(bodys, buffer.position(), readableLength); 
		}
		logger.info("TCP解码成功...");
		return list;
	}
	/**
	 * 判断是否符合协议头长度
	 * @param buffer
	 * @return
	 * @throws AioDecodeException
	 */
	private static boolean isHeaderLength(ByteBuffer buffer) {
		int readableLength = buffer.limit() - buffer.position();
		if(readableLength == 0) {
			return false;
		}
		//协议头索引;
		int index = buffer.position();
		try{
			//获取第一个字节协议版本号;
			buffer.get(index);
			index++;
			//标志位
			byte maskByte = buffer.get(index);
			//同步发送;
			if(decodeHasSynSeq(maskByte)){
				index += 4;
			}
			index++;
			//消息体长度
			buffer.getInt(index);
			index += 4;
			return true;
		}catch(Exception e){
			return false;
		}
	}
}
