package com.lyh.route.collapser.oper;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSON;
import com.lyh.common.utils.HttpAsyncClientUtil;
import com.lyh.common.utils.HttpClientUtil;


public class BufferOperationHttpRequest implements BufferOperation{

	private String url;
	
	public BufferOperationHttpRequest(String url) {
		this.url = url;
	}

	public String oper(byte[] b, int off, int len) {
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
			//进入重试队列
			
		}
		return null;
	}

	@Override
	public void asyncOper(byte[] b, int off, int len) {
		CloseableHttpAsyncClient httpClient = HttpAsyncClientUtil.getHttpClient();
		HttpPost httpPost = new HttpPost(url);
		byte[] buf = new byte[len];
		System.arraycopy(b, off, buf, 0, len);
		httpPost.setEntity(new ByteArrayEntity(buf));
		httpClient.start();
		httpClient.execute(httpPost, new FutureCallback<HttpResponse>(){

			@Override
			public void completed(HttpResponse result) {
				System.out.println("Async异步回调成功：result="+result);
				//成功的忽略，失败的记录为留言
			}

			@Override
			public void failed(Exception ex) {
				System.out.println("Async异步回调结果是失败：ex="+JSON.toJSONString(ex)+",len="+len);
				//buf 重新放回重试队列,重试失败进入死信队列
			}

			@Override
			public void cancelled() {
			}
			
		});
	}
	
}
