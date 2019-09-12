package com.lyh.common.utils;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.nio.reactor.ConnectingIOReactor;
import org.apache.http.nio.reactor.IOReactorException;
/**
 * httpclient连接池
 * @author liyanhui
 *
 */
public class HttpAsyncClientUtil {

	private static PoolingNHttpClientConnectionManager cm = null;
	private static RequestConfig requestConfig = null;
	public static final String CHARSET_GBK = "GBK";
	public static final String CHARSET_UTF8 = "UTF-8";
	// 将最大连接数增加到
	public static final int MAX_TOTAL = 50;
	// 将每个路由基础的连接增加到
	public static final int MAX_ROUTE_TOTAL = 20;
	public static final int REQUEST_TIMEOUT = 3000;
	public static final int REQUEST_SOCKET_TIME = 30000;

	static{
			requestConfig = RequestConfig.custom()
	                .setConnectTimeout(REQUEST_TIMEOUT)
	                .setSocketTimeout(REQUEST_SOCKET_TIME)
	                .setConnectionRequestTimeout(REQUEST_TIMEOUT)
	                .build();
			//配置io线程
	        IOReactorConfig ioReactorConfig = IOReactorConfig.custom().
	                setIoThreadCount(Runtime.getRuntime().availableProcessors())
	                .setSoKeepAlive(true)
	                .build();
	        ConnectingIOReactor ioReactor=null;
	        try {
	            ioReactor = new DefaultConnectingIOReactor(ioReactorConfig);
	        } catch (IOReactorException e) {
	            e.printStackTrace();
	        }
	        cm = new PoolingNHttpClientConnectionManager(ioReactor);
	        cm.setMaxTotal(MAX_TOTAL);
	        cm.setDefaultMaxPerRoute(MAX_ROUTE_TOTAL);
	}

	public static CloseableHttpAsyncClient getHttpClient() {
		CloseableHttpAsyncClient httpClient = HttpAsyncClients.custom().
                setConnectionManager(cm)
                .setDefaultRequestConfig(requestConfig)
                .build();
		return httpClient;
	}
}
