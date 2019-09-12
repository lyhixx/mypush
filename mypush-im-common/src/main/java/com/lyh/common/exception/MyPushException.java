package com.lyh.common.exception;

import com.alibaba.fastjson.JSONObject;

public class MyPushException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2932648718913790920L;
	/**
	 * 异常信息
	 */
	protected String msg;

	/**
	 * 具体异常码
	 */
	protected int code;
	
	public enum ErrorCode {
		SYS_ERROR(999,"系统错误"),
		UNKOWN_PROTOCOL(600,"未知协议"),
		INVALID_TOKEN(601,"token无效"),
		SYSTEM_BUSY_MOCK(602,"系统忙请稍后再试"),
		RECHAT_FAIL(603,"重新发送聊天信息失败"),
		TCP_700(700, "协议版本号不匹配!"),
		TCP_701(701, "协议包内容为空!");
		
		private Integer code;
		
		private String des;
		
		ErrorCode(Integer code, String des) {
			this.code = code;
			this.des = des;
		}

		public Integer getCode() {
			return code;
		}

		public String getDes() {
			return des;
		}
		
		public JSONObject getJson(){
			JSONObject jsonObject=new JSONObject();
			jsonObject.put("resCode", code);
			jsonObject.put("resDes", des);
			return jsonObject;
		}
	}

	public MyPushException(int code, String msg, Object... args) {
		super(String.format(msg, args));
		this.code = code;
		this.msg = String.format(msg, args);
	}

	public MyPushException() {
		super();
	}

	public String getMsg() {
		return msg;
	}

	public int getCode() {
		return code;
	}

	/**
	 * 实例化异常
	 * 
	 * @param msg
	 * @param args
	 * @return
	 */
	public MyPushException newInstance(String msg, Object... args) {
		return new MyPushException(this.code, msg, args);
	}

	public MyPushException(String message, Throwable cause) {
		super(message, cause);
	}

	public MyPushException(Throwable cause) {
		super(cause);
	}

	public MyPushException(String message) {
		super(message);
	}
}
