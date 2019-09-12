package com.lyh.common.test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import com.alibaba.fastjson.JSONArray;

public class demo3 {

	private final ReentrantLock count = new ReentrantLock();
	
	public void test(){
		final ReentrantLock count = this.count;
		System.out.println(count);
		System.out.println(this.count);
	}
	
//	public static void main(String[] args) {
//		new demo3().test();
//	}
	
	public static void main(String[] args) {
		String a = "[{\"order_no\":\"80001201906150007338504724\",\"sno\":\"MO2019062700000000000022153570\",\"docid\":\"57342f6fed840f19398b45f2\",\"acct_no\":\"6216610100011684509\",\"id_card\":\"130202196901230621\",\"remark\":\"交易成功\",\"currency\":\"CNY\",\"acct_name\":\"卢颖州\",\"pay_end_time\":1561605969000,\"cash_fee\":2475.00,\"status\":2}{\"order_no\":\"80001201906161436539825378\",\"sno\":\"MO2019062700000000000022154209\",\"docid\":\"5a2a251ced840f14658b4683\",\"acct_no\":\"6228450088058964876\",\"id_card\":\"430902198109151020\",\"remark\":\"姓名不正确\",\"currency\":\"CNY\",\"acct_name\":\"袁俏奇\",\"pay_end_time\":1561607265000,\"cash_fee\":0,\"status\":3},{\"order_no\":\"80001201906201753385995833\",\"sno\":\"MO2019062700000000000022153554\",\"docid\":\"5c482f1fed840f52598b52da\",\"acct_no\":\"6103823100075868794\",\"id_card\":\"513125195404220025\",\"remark\":\"转入的帐户/银行卡号不正确\",\"currency\":\"CNY\",\"acct_name\":\"王泽勤\",\"pay_end_time\":1561607265000,\"cash_fee\":0,\"status\":3}]";
		JSONArray.parseArray(a);
	}
}
