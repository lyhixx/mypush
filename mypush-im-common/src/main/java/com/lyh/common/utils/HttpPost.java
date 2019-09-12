package com.lyh.common.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.axdoctor.tools.log.LogPhase;
import com.axdoctor.tools.log.LoggerModel;

public class HttpPost {

	private static Logger logger = LoggerFactory.getLogger(HttpPost.class);

	/**
	 * post请求
	 * 
	 * @param httpUrl
	 * @param param
	 * @return
	 */
	public static String httpPost(String httpUrl, String param, String charsetName) {
		long start = System.currentTimeMillis();
		logger.info(new LoggerModel("网络请求", LogPhase.REQ).put("httpUrl", httpUrl).put("param", param)
				.put("charsetName", charsetName).toString());
		String result = "";
		try {
			URL url = new URL(httpUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true); // 可以发送数据
			conn.setDoInput(true); // 可以接收数据
			conn.setRequestMethod("POST"); // POST方法
			// 必须注意此处需要设置UserAgent，否则google会返回403
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1;SV1)");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.connect();

			// 写入的POST数据
			OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream(), charsetName);
			osw.write(param);
			osw.flush();
			osw.close();

			// 读取响应数据
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), charsetName));
			String line;
			StringBuffer strBuff = new StringBuffer();
			while ((line = in.readLine()) != null) {
				strBuff.append(line);
			}
			in.close();
			result = strBuff.toString();
		} catch (Exception e) {
			logger.error(
					new LoggerModel("网络请求", LogPhase.HANDLING).put("httpUrl", httpUrl).put("param", param).toString());
		}
		logger.info(new LoggerModel("网络请求", LogPhase.RES).put("httpUrl", httpUrl).put("param", param)
				.put("result", result).put("elapsedTime", (System.currentTimeMillis() - start)).toString());
		return result;
	}

	/**
	 * post请求无返�?
	 * 
	 * @param httpUrl
	 * @param param
	 * @return
	 */
	public static String httpPostNoResult(String httpUrl, String param) {
		String result = "";
		try {
			URL url = new URL(httpUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true); // 可以发送数据
			conn.setDoInput(true); // 可以接收数据
			conn.setRequestMethod("POST"); // POST方法
			// 必须注意此处需要设置UserAgent，否则google会返回403
			conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.0)");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.connect();
			// 写入的POST数据
			OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
			osw.write(param);
			osw.flush();
			osw.close();
		} catch (Exception e) {
			logger.error(
					new LoggerModel("网络请求", LogPhase.HANDLING).put("httpUrl", httpUrl).put("param", param).toString());
		}

		return result;
	}

}