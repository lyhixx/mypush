//package com.lyh.route;
//
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import com.lyh.route.collapser.oper.BufferOperation;
//
//
//@Component("bufferOperationHttpRequest")
//public class BufferOperationHttpRequest implements BufferOperation{
//
//	@Autowired
//	BufferSendHttpRequest bufferSendHttpRequest;
//	final static ExecutorService executor = Executors.newCachedThreadPool();
//	
//	public String oper(byte[] b, int off, int len) {
//		String res = bufferSendHttpRequest.send(b, off, len);
//		System.out.println("oper 返回res="+res);
////		JSONObject json = JSONObject.parseObject(res);
////		JSONObject data = json.getJSONObject("data");
////		JSONArray list = data.getJSONArray("list");
////		for(int i=0;i<list.size();i++){
////			JSONObject item = (JSONObject) list.get(i);
////			long chatId = item.getLong("chatId");
////			int status = item.getIntValue("status");
////		}
//		return null;
//	}
//	
//}
