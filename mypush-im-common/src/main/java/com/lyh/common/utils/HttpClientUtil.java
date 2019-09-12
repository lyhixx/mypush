package com.lyh.common.utils;

import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;

import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
/**
 * httpclient连接池
 * @author liyanhui
 *
 */
public class HttpClientUtil {

	private static PoolingHttpClientConnectionManager cm = null;
	private static RequestConfig requestConfig = null;
	public static final String CHARSET_GBK = "GBK";
	public static final String CHARSET_UTF8 = "UTF-8";
	// 将最大连接数增加到
	public static final int MAX_TOTAL = 50;
	// 将每个路由基础的连接增加到
	public static final int MAX_ROUTE_TOTAL = 20;
	public static final int REQUEST_TIMEOUT = 300;
	public static final int REQUEST_SOCKET_TIME = 300;

	static{
			LayeredConnectionSocketFactory sslsf = null;
			try {
				sslsf = new SSLConnectionSocketFactory(SSLContext.getDefault());
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
			requestConfig = RequestConfig.custom().setConnectTimeout(REQUEST_TIMEOUT)
					.setSocketTimeout(REQUEST_SOCKET_TIME).build();

			Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
					.register("https", sslsf).register("http", new PlainConnectionSocketFactory()).build();
			cm = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
			cm.setMaxTotal(MAX_TOTAL);
			cm.setDefaultMaxPerRoute(MAX_ROUTE_TOTAL);
	}

	public static HttpClient getHttpClient() {
		CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(cm)
				.setDefaultRequestConfig(requestConfig).build();
		return httpClient;
	}
}
