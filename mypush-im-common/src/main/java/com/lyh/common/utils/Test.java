package com.lyh.common.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Test {

	
	
	public static void main(String[] args) throws UnknownHostException {
		String ip = InetAddress.getLocalHost().getHostAddress();
		
//		System.out.println(InetAddress.getLocalHost().getHostName());
//		
//		System.out.println(InetAddress.getByName(InetAddress.getLocalHost().getHostName()).getHostAddress());
//		System.out.println(ip);
		
		System.err.println(("10.10.76.38"+"38088"+"8088").hashCode());
	}
}
