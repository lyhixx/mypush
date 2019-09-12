package com.lyh.server.util;

import io.netty.channel.Channel;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

public class NettyAttrUtil {

    private static final AttributeKey<String> ATTR_KEY_READER_TIME = AttributeKey.valueOf("readerTime");
    private static final AttributeKey<String> ATTR_KEY_FIRST_TIME = AttributeKey.valueOf("firstTime");
    private static final AttributeKey<Integer> ATTR_KEY_MID = AttributeKey.valueOf("mid");
    private static final AttributeKey<String> ATTR_KEY_PROTOCOL = AttributeKey.valueOf("protocol");
    private static final AttributeKey<String> ATTR_KEY_TOKEN = AttributeKey.valueOf("token");

    public static void setReaderTime(Channel channel, Long time) {
        channel.attr(ATTR_KEY_READER_TIME).set(time.toString());
    }

    public static Long getReaderTime(Channel channel) {
        String value = getAttribute(channel, ATTR_KEY_READER_TIME);

        if (value != null) {
            return Long.valueOf(value);
        }
        return null;
    }
    
    public static void setFirstTime(Channel channel) {
        channel.attr(ATTR_KEY_FIRST_TIME).set("1");
    }

    public static boolean getIsHadFirstTime(Channel channel) {
        return getAttribute(channel, ATTR_KEY_FIRST_TIME)==null;
    }
    
    public static void setMid(Channel channel, Integer mid) {
        channel.attr(ATTR_KEY_MID).set(mid);
    }

    public static Integer getMid(Channel channel) {
        return getAttribute(channel, ATTR_KEY_MID);
    }
    
    public static void setProtocol(Channel channel, String protocol) {
        channel.attr(ATTR_KEY_PROTOCOL).set(protocol);
    }

    public static String getProtocol(Channel channel) {
        return getAttribute(channel, ATTR_KEY_PROTOCOL);
    }
    
    public static void setToken(Channel channel, String token) {
        channel.attr(ATTR_KEY_TOKEN).set(token);
    }

    public static String getToken(Channel channel) {
        return getAttribute(channel, ATTR_KEY_TOKEN);
    }

    private static <T> T getAttribute(Channel channel, AttributeKey<T> key) {
        Attribute<T> attr = channel.attr(key);
        return attr.get();
    }
}
