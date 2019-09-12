package com.lyh.route;


import java.io.IOException;
import java.nio.ByteBuffer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lyh.common.protocol.binary.BinaryRequestEncoder;
import com.lyh.common.utils.KryoUtil;
import com.lyh.route.collapser.BufferBus;
import com.lyh.route.collapser.pool.BufferBusPoolFactory;

import cn.hutool.json.JSONObject;

/**
 * 路由
 */
@Controller
@RequestMapping("/api")
public class RouteController {

//	@Autowired
//	private BufferOperationHttpRequest bufferOperationHttpRequest;

//    @RequestMapping(value = "test", method = RequestMethod.POST)
//    @ResponseBody
//    public String test(String msg){
//
//    	BufferBus bus1 = BufferBusFactory.getInstance(binaryRequestSend, 40, 20000, "127.0.0.1", "8088");
////    	BufferBus bus2 = BufferBusFactory.getInstance(binaryRequestSend, 40, 2000, "127.0.0.1", "8087");
//    	try {
//    		int i = 20;
//    		while(i-->0){
//    			byte[] body = KryoUtil.writeObjectToByteArray(i);
//            	ByteBuffer buffer = BinaryRequestEncoder.encode(body);
//    			bus1.write(buffer.array(), 0, buffer.array().length);
////				bus2.write(buffer.array(), 0, buffer.array().length);
//    		}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//        JSONObject data = new JSONObject();
//        data.put("resCode", 200);
//        data.put("resDesc", "成功");
//        return data.toString();
//    }
    
//    @RequestMapping(value = "test2", method = RequestMethod.POST)
//    @ResponseBody
//    public String test2(String msg){
//    	try {
//    		int i = 3;
//    		String[] portarr = {"8088","8087","8086"};
//    		while(i-->0){
//    			String data = i+msg;
//    			System.out.println(data);
//    			byte[] body = KryoUtil.writeObjectToByteArray(data);
//            	ByteBuffer buffer = BinaryRequestEncoder.encode(body);
//            	BufferBus bus1 = BufferBusPoolFactory.getBufferBus(bufferOperationHttpRequest, "127.0.0.1", portarr[i], buffer.array().length);
//    			bus1.write(buffer.array(), 0, buffer.array().length);
////				bus2.write(buffer.array(), 0, buffer.array().length);
//    		}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//        JSONObject data = new JSONObject();
//        data.put("resCode", 200);
//        data.put("resDesc", "成功");
//        return data.toString();
//    }
}
