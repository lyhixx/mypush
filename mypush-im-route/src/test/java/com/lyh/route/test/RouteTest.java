package com.lyh.route.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.lyh.route.MyPushRouteApplication;
import com.lyh.route.init.TaskBitMap;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MyPushRouteApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RouteTest {

	@Autowired
	TaskBitMap taskBitMap;
	
	@Test
	public void test1() throws Exception {
		System.out.println("init map");
	}
	
}
