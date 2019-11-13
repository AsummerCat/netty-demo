package com.linjingc.nettywebsocketdemo;

import com.linjingc.nettywebsocketdemo.webSocket.NioWebSocketServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NettyWebsocketDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(NettyWebsocketDemoApplication.class, args);

		NioWebSocketServer.startNettyServer();
	}

}
