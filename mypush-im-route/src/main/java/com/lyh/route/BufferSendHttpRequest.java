package com.lyh.route;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import com.lyh.common.utils.HttpClientUtil;


@Component
public class BufferSendHttpRequest {

	private static String url = "http://127.0.0.1:8088/server/api/recieveMsg";
	
	public String send(byte b[], int off, int len){
		System.out.println("BinaryRequestSend,b="+b.toString());
//		try {
//			Thread.sleep(200);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		HttpClient httpClient = HttpClientUtil.getHttpClient();
		HttpPost httpPost = new HttpPost(url);
		byte[] buf = new byte[len];
		System.arraycopy(b, off, buf, 0, len);
		httpPost.setEntity(new ByteArrayEntity(buf));
		HttpResponse httpResponse;
		buf = null;
		try {
			httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();
			String result = EntityUtils.toString(httpEntity, "utf-8");	
			System.out.println("BinaryRequestSend返回：" + result + ","+Thread.currentThread().getName());
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
