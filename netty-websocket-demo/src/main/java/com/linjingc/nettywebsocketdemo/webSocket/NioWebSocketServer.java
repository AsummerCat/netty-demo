package com.linjingc.nettywebsocketdemo.webSocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.stereotype.Component;

/**
 * 启动nettyServer服务器
 */
@Component
public class NioWebSocketServer {
	private void init() {
		System.out.println("正在启动websocket服务器");
		NioEventLoopGroup boss = new NioEventLoopGroup();
		NioEventLoopGroup work = new NioEventLoopGroup();
		try {
			ServerBootstrap bootstrap = new ServerBootstrap();
			bootstrap.group(boss, work);
			bootstrap.channel(NioServerSocketChannel.class);
			//自定义业务handler
			bootstrap.childHandler(new NioWebSocketChannelInitializer());
			Channel channel = bootstrap.bind(8081).sync().channel();
			System.out.println("webSocket服务器启动成功");
			channel.closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.out.println("运行出错" + e);

		} finally {
			boss.shutdownGracefully();
			work.shutdownGracefully();
			System.out.println("websocket服务器已关闭");
		}
	}

	/**
	 * 启动初始化NettyServer
	 */
	public static void startNettyServer() {
		new NioWebSocketServer().init();
	}
}