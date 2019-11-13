package com.linjingc.nettywebsocketdemo.webSocket;

import com.linjingc.nettywebsocketdemo.SpringContextUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 启动nettyServer服务器
 */
@Component
public class NioWebSocketServer {
	private Integer port = 8082;

	private  NioWebSocketChannelInitializer nioWebSocketChannelInitializer;

	public NioWebSocketServer() {
		this.nioWebSocketChannelInitializer = SpringContextUtil.getBean("nioWebSocketChannelInitializer");
	}


	private void init() {
		System.out.println("正在启动websocket服务器");
		NioEventLoopGroup boss = new NioEventLoopGroup();
		NioEventLoopGroup work = new NioEventLoopGroup();
		try {
			long begin = System.currentTimeMillis();
			ServerBootstrap bootstrap = new ServerBootstrap();
			bootstrap.group(boss, work);
			bootstrap.channel(NioServerSocketChannel.class);
			//自定义业务handler
			bootstrap.childHandler(nioWebSocketChannelInitializer);
			Channel channel = bootstrap.bind(port).sync().channel();
			System.out.println("webSocket服务器启动成功");
			long end = System.currentTimeMillis();
			System.out.println("Netty Websocket服务器启动完成，耗时 " + (end - begin) + " ms，已绑定端口 " + port + " 阻塞式等候客户端连接");
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