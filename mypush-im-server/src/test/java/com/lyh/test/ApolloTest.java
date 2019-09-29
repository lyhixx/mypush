package com.lyh.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfig;
import com.lyh.MyPushServerApplication;
@RunWith(SpringRunner.class)   
@SpringBootTest(classes={MyPushServerApplication.class})// 指定启动类
public class ApolloTest {

	@ApolloConfig
	private Config config;
	
	@Value("${server.port}")
    private  String port;
	
	@Test
    public void index(){
       System.out.println("hello world");
        System.out.println("+++++++"+port);
    }
	
	
}
