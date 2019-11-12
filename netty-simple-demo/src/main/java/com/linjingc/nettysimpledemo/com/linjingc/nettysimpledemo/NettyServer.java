package com.linjingc.nettysimpledemo;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.concurrent.ConcurrentHashMap;

public class NettyServer {

	//连接客户端
	public static ConcurrentHashMap<String, ChannelHandlerContext> map = new ConcurrentHashMap<String, ChannelHandlerContext>();

	public static void main(String[] args) {
		ServerBootstrap serverBootstrap = new ServerBootstrap();

		//接收客户端连接
		NioEventLoopGroup boos = new NioEventLoopGroup();
		//处理已连接客户端请求
		NioEventLoopGroup worker = new NioEventLoopGroup();
		try {
			//分组 绑定线程池
			serverBootstrap.group(boos, worker);
			//管道
			serverBootstrap.channel(NioServerSocketChannel.class);
			// 2小时无数据激活心跳机制
			serverBootstrap.option(ChannelOption.SO_KEEPALIVE, true);
			//指定此套接口排队的最大连接个数
			serverBootstrap.option(ChannelOption.SO_BACKLOG, 1024);
			//来监听已经连接的客户端的Channel的动作和状态。
			serverBootstrap.childHandler(new ChannelInitializer<NioSocketChannel>() {

				                             @Override
				                             protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
					             /*
					             LineBasedFrameDecoder的工作原理是：依次遍历ByteBuf中的可读字节，
                                判断看其是否有”\n” 或 “\r\n”， 如果有就以此位置为结束位置。
                                从可读索引到结束位置的区间的字节就组成了一行。 它是以换行符为结束标志的解码器，
                                支持携带结束符和不带结束符两种解码方式，同时支持配置单行的最大长度，
                                如果读到了最大长度之后仍然没有发现换行符，则抛出异常，同时忽略掉之前读到的异常码流
					              */
					                             //    nioSocketChannel.pipeline().addLast(new LineBasedFrameDecoder(10010));

					                             //字符串解码和编码
					                             //LineBasedFrameDecoder + StringDecoder 就是一个按行切换的文本解码器。
					                             nioSocketChannel.pipeline().addLast(new StringDecoder());
					                             nioSocketChannel.pipeline().addLast(new StringEncoder());
//
					                             //接受客户端消息
					                             nioSocketChannel.pipeline().addLast(new SimpleChannelInboundHandler<String>() {
						                             @Override
						                             protected void channelRead0(ChannelHandlerContext channelHandlerContext, String msg) throws Exception {
							                             System.out.println(msg);
							                             map.put(msg, channelHandlerContext);
							                             //回复
							                             NettyServer.sendMessageClient(msg);
						                             }
					                             });
				                             }
			                             }
			);
			//启动netty服务
			serverBootstrap.bind(8000);
		} finally {
			// 释放线程池资源
			//boos.shutdownGracefully();
			//worker.shutdownGracefully();
		}
	}


	/**
	 * 发送消息给客户端
	 */
	public static void sendMessageClient(String msg) {
		//*****回复*********
		//1.判断客户端是否在线
		ChannelHandlerContext client = map.get(msg);
		if (client == null) {
			return;
		}
		//2.回复客户端
		if (!client.channel().isActive()) {
			System.out.println("客户端下线");
		}
		client.channel().writeAndFlush("嘿嘿 你来了");
		//*****回复*********
	}
}
