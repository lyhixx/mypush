package com.lyh.client.tcp.packet;

import javax.validation.constraints.NotNull;

import com.lyh.common.packet.BaseRequestPacket;

/**
 * Function: Google Protocol 编解码发送
 *
 * @author crossoverJie
 *         Date: 2018/05/21 15:56
 * @since JDK 1.8
 */
public class GoogleProtocolVO extends BaseRequestPacket {
    @NotNull(message = "requestId 不能为空")
    private Integer requestId ;

    @NotNull(message = "msg 不能为空")
    private String msg ;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Integer getRequestId() {
        return requestId;
    }

    public void setRequestId(Integer requestId) {
        this.requestId = requestId;
    }

    @Override
    public String toString() {
        return "GoogleProtocolVO{" +
                "requestId=" + requestId +
                ", msg='" + msg + '\'' +
                "} " + super.toString();
    }
}
