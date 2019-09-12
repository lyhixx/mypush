package com.lyh.common.protocol.binary;

public interface MyPushProtocol{
	/**
	 * 协议版本号
	 */
	public final static byte VERSION = 0x01;
	
	/**
	 * 消息体最多为多少
	 */
	public static final int MAX_LENGTH_OF_BODY = (int) (1024 * 1024 * 2.1); //只支持多少M数据

	/**
	 * 消息头最少为多少个字节
	 */
	public static final int LEAST_HEADER_LENGHT = 4;//1+1+2 + (2+4)
	
	/**
	 * 加密标识位mask，1为加密，否则不加密
	 */
	public static final byte FIRST_BYTE_MASK_ENCRYPT = -128;

	/**
	 * 压缩标识位mask，1为压缩，否则不压缩
	 */
	public static final byte FIRST_BYTE_MASK_COMPRESS = (byte)01000000;

	/**
	 * 是否有同步序列号标识位mask，如果有同步序列号，则消息头会带有同步序列号，否则不带
	 */
	public static final byte FIRST_BYTE_MASK_HAS_SYNSEQ = (byte)00100000;

	/**
	 * 是否是用4字节来表示消息体的长度
	 */
	public static final byte FIRST_BYTE_MASK_4_BYTE_LENGTH = (byte)00010000;

	/**
	 * 版本号mask
	 */
	public static final byte FIRST_BYTE_MASK_VERSION = (byte)00001111;
}