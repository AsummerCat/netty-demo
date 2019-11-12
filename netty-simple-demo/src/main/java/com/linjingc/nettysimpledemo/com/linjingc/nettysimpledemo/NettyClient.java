package com.linjingc.nettysimpledemo;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class NettyClient {
	public static void main(String[] args) throws InterruptedException {
		Bootstrap bootstrap = new Bootstrap();
		NioEventLoopGroup client = new NioEventLoopGroup();
		bootstrap
				.group(client)
				.channel(NioSocketChannel.class)
				.handler(new ChannelInitializer<Channel>() {
					@Override
					protected void initChannel(Channel channel) throws Exception {
						//字符串编码解码
						channel.pipeline().addLast("decoder", new StringDecoder());
						channel.pipeline().addLast("encoder", new StringEncoder());
						//心跳检测
						channel.pipeline().addLast(new IdleStateHandler(0, 4, 0, TimeUnit.SECONDS));
						//接受服务端消息
						channel.pipeline().addLast(new SimpleChannelInboundHandler<String>() {
							@Override
							protected void channelRead0(ChannelHandlerContext channelHandlerContext, String msg) throws Exception {
								System.out.println("客户端收到消息了"+msg);
							}


						});
					}
				});

		//客户端连接
		Channel channel = bootstrap.connect("127.0.0.1", 8000).channel();
		while (true) {
			channel.writeAndFlush(new Date() + ":hello world");
			Thread.sleep(2000);
		}

	}
}
