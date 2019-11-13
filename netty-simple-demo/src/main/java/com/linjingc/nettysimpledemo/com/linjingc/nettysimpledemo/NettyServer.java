package com.linjingc.nettysimpledemo;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
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
					             /*
					             LineBasedFrameDecoder的工作原理是：依次遍历ByteBuf中的可读字节，
                                判断看其是否有”\n” 或 “\r\n”， 如果有就以此位置为结束位置。
                                从可读索引到结束位置的区间的字节就组成了一行。 它是以换行符为结束标志的解码器，
                                支持携带结束符和不带结束符两种解码方式，同时支持配置单行的最大长度，
                                如果读到了最大长度之后仍然没有发现换行符，则抛出异常，同时忽略掉之前读到的异常码流
					              */
					                             nioSocketChannel.pipeline().addLast(new LineBasedFrameDecoder(1024));

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
							                             NettyServer.sendMessageClient(msg);
						                             }

						                             /**
						                              * 服务端监听到客户端活动
						                              */
						                             @Override
						                             public void channelActive(ChannelHandlerContext ctx) throws Exception {
							                             //移除全局用户中的这个人
							                             channelGroup.add(ctx.channel());
							                             System.out.println(ctx.channel().localAddress().toString()+"已经成功连接");
							                             //发送全体广播
							                             sendAllMessage();
						                             }

						                             /**
						                              * 服务端监听到客户端不活动
						                              */
						                             @Override
						                             public void channelInactive(ChannelHandlerContext ctx) throws Exception {
							                             //移除全局用户中的这个人
							                             channelGroup.remove(ctx.channel());
							                             System.out.println(ctx.channel().localAddress().toString()+"已经断开");
						                             }
					                             });
				                             }
			                             }
			);
			//启动netty服务
//			serverBootstrap.bind(8000);
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
