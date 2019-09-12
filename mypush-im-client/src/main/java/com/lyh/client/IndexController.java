package com.lyh.client;

import com.lyh.client.tcp.TcpClient;
import com.lyh.client.tcp.packet.GoogleProtocolVO;

import cn.hutool.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Function:
 *
 * @author crossoverJie
 *         Date: 22/05/2018 14:46
 * @since JDK 1.8
 */
@Controller
@RequestMapping("/api")
public class IndexController {


    @Autowired
    private TcpClient heartbeatClient ;


    /**
     * 向服务端发消息 字符串
     * @param stringReqVO
     * @return
     */
    @RequestMapping(value = "sendStringMsg", method = RequestMethod.POST)
    @ResponseBody
    public String sendStringMsg(String msg){

        heartbeatClient.sendStringMsg(msg) ;

        JSONObject data = new JSONObject();
        data.put("resCode", 200);
        data.put("resDesc", "成功");
        return data.toString();
    }

    /**
     * 向服务端发消息 Google ProtoBuf
     * @param googleProtocolVO
     * @return
     */
    @RequestMapping(value = "sendProtoBufMsg", method = RequestMethod.POST)
    @ResponseBody
    public String sendProtoBufMsg(@RequestBody GoogleProtocolVO googleProtocolVO){
        heartbeatClient.sendGoogleProtocolMsg(googleProtocolVO) ;
        JSONObject data = new JSONObject();
        data.put("resCode", 200);
        data.put("resDesc", "成功");
        return data.toString();
    }
}
