package com.lyh.gate.model;

import java.io.Serializable;

public class MpUser implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8597788165603281918L;
	
	private	int id;
	private String bizUid;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getBizUid() {
		return bizUid;
	}
	public void setBizUid(String bizUid) {
		this.bizUid = bizUid;
	}
	
}
