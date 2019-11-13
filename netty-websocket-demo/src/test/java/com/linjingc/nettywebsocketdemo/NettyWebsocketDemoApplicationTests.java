package com.linjingc.nettywebsocketdemo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
class NettyWebsocketDemoApplicationTests {

	@Autowired
   private ApplicationContext applicationContext;
	@Test
	void contextLoads() {
	}

	@Test
	public void test() {
		String[] beanDefinitionNames = applicationContext.getBeanDefinitionNames();
		List<String> strings = Arrays.asList(beanDefinitionNames);
		System.out.println(strings.toString());
	}



}
