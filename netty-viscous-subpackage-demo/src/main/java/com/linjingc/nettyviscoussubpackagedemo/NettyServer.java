package com.linjingc.nettyviscoussubpackagedemo;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class NettyServer {

	/**
	 * 连接客户端
	 */
	public static ConcurrentHashMap<String, ChannelHandlerContext> map = new ConcurrentHashMap<String, ChannelHandlerContext>();
	/**
	 * 维护连接上的客户端
	 */
	private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	private static ChannelFuture serverChannelFuture;



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


					                             /**
					                              * 注意以下3个自带分包类 最好只用一个 不然会导致解析乱七八糟的格式
					                              */

				                             	//判断byteBuffer中是否有\n 或者\r\n
					                             nioSocketChannel.pipeline().addLast(new LineBasedFrameDecoder(1024));

					                             //需要指定需要分包的字符
//					                             ByteBuf delimiter = Unpooled.copiedBuffer("$$".getBytes());
//					                             nioSocketChannel.pipeline().addLast(new DelimiterBasedFrameDecoder(1024,delimiter));

					                             //固定长度解码器
//					                             nioSocketChannel.pipeline().addLast(new FixedLengthFrameDecoder(100));//参数为一次接受的数据长度


					                             //字符串解码和编码
					                             //LineBasedFrameDecoder + StringDecoder 就是一个按行切换的文本解码器。
					                             nioSocketChannel.pipeline().addLast(new StringDecoder());
					                             nioSocketChannel.pipeline().addLast(new StringEncoder());

									            //发送消息频率。单位秒。此设置是60秒发送一次消息
					                             //readerIdleTime为读超时时间（即测试端一定时间内未接受到被测试端消息）
					                             //writerIdleTime为写超时时间（即测试端一定时间内向被测试端发送消息）
					                             //allIdleTime：所有类型的超时时间
					                             nioSocketChannel.pipeline().addLast(new IdleStateHandler(60, 60, 60, TimeUnit.SECONDS));

					                             //接受客户端消息
					                             nioSocketChannel.pipeline().addLast(new SimpleChannelInboundHandler<String>() {
						                             /**
						                              * 读取客户端消息
						                              */
						                             @Override
						                             protected void channelRead0(ChannelHandlerContext channelHandlerContext, String msg) throws Exception {
						                             	System.out.println(msg);
							                             map.put(msg, channelHandlerContext);
							                             //回复
//							                             NettyServer.sendMessageClient(msg);
						                             }
					                             });
				                             }
			                             }
			);
			//启动netty服务
			serverChannelFuture= serverBootstrap.bind(8000).sync();
		}catch (Exception e){
			// 释放线程池资源
			boos.shutdownGracefully();
			worker.shutdownGracefully();
			e.printStackTrace();
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

	/**
	 * 群发广播
	 */
	public static void sendAllMessage(){
		channelGroup.writeAndFlush("新用户登录了"+new Date());
	}
}
