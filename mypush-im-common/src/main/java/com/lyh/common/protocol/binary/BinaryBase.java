package com.lyh.common.protocol.binary;

public class BinaryBase {
	public static byte encodeEncrypt(byte bs,boolean isEncrypt){
		if(isEncrypt){
			return (byte) (bs | MyPushProtocol.FIRST_BYTE_MASK_ENCRYPT);
		}else{
			return (byte)(MyPushProtocol.FIRST_BYTE_MASK_ENCRYPT & (byte)01111111);
		}
	}
	public static boolean decodeCompress(byte version)
	{
		return (MyPushProtocol.FIRST_BYTE_MASK_COMPRESS & version) != 0;
	}

	public static byte encodeCompress(byte bs, boolean isCompress)
	{
		if (isCompress)
		{
			return (byte) (bs | MyPushProtocol.FIRST_BYTE_MASK_COMPRESS);
		} else
		{
			return (byte) (bs & (MyPushProtocol.FIRST_BYTE_MASK_COMPRESS ^ (byte)01111111));
		}
	}

	public static boolean decodeHasSynSeq(byte maskByte)
	{
		return (MyPushProtocol.FIRST_BYTE_MASK_HAS_SYNSEQ & maskByte) != 0;
	}

	public static byte encodeHasSynSeq(byte bs, boolean hasSynSeq)
	{
		if (hasSynSeq)
		{
			return (byte) (bs | MyPushProtocol.FIRST_BYTE_MASK_HAS_SYNSEQ);
		} else
		{
			return (byte) (bs & (MyPushProtocol.FIRST_BYTE_MASK_HAS_SYNSEQ ^ (byte)01111111));
		}
	}

	public static boolean decode4ByteLength(byte version)
	{
		return (MyPushProtocol.FIRST_BYTE_MASK_4_BYTE_LENGTH & version) != 0;
	}

	public static byte encode4ByteLength(byte bs, boolean is4ByteLength)
	{
		if (is4ByteLength)
		{
			return (byte) (bs | MyPushProtocol.FIRST_BYTE_MASK_4_BYTE_LENGTH);
		} else
		{
			return (byte) (bs & (MyPushProtocol.FIRST_BYTE_MASK_4_BYTE_LENGTH ^ (byte)01111111));
		}
	}

	public static byte decodeVersion(byte version)
	{
		return (byte) (MyPushProtocol.FIRST_BYTE_MASK_VERSION & version);
	}
}
