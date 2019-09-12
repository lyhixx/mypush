package com.lyh.common.constant;

/**
 * Function:常量
 *
 * @author crossoverJie
 *         Date: 28/03/2018 17:41
 * @since JDK 1.8
 */
public class MyConstants {

    /**
     * 协议类型
     */
    public static class ProtocolType{
        /**
         * 业务消息
         */
        public static final String TCP = "tcp" ;

        /**
         * ping
         */
        public static final String WEBSOCKET = "ws" ;
    }

    /**
     * 协议类型
     */
    public static class MsgType{
        /**
         * 业务消息
         */
        public static final String PING = "PING" ;

        /**
         * ping
         */
        public static final String PONG = "PONG" ;
    }

}
