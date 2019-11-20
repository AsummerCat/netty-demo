package com.linjingc.nettyviscoussubpackagedemo;

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

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class NettyClient {
	public static void main(String[] args) throws InterruptedException, UnsupportedEncodingException {
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
//								System.out.println("客户端收到消息了" + msg);
							}
						});
					}
				});

		//客户端连接
		Channel channel = bootstrap.connect("127.0.0.1", 8000).channel();
		for (int i=0;i<100000;i++){
			//第一种LineBasedFrameDecoder
			channel.writeAndFlush(new Date() + ":hello world by LineBasedFrameDecoder"+" num"+i+"\n");
			//第二种DelimiterBasedFrameDecoder
			channel.writeAndFlush(new Date() + ":hello world by DelimiterBasedFrameDecoder"+" num"+i+"$$");
			//第三种 FixedLengthFrameDecoder

			/**
			 * 生成固定长度byte方式一：使用String 拼接
			 */
			String s1 = new Date() + ":hello world by FixedLengthFrameDecoder"+" num"+i;
			byte[] bytes1 = s1.getBytes("UTF-8");
			byte[] msgBytes1 = new byte[100];
			for (int i1 = 0; i1 < msgBytes1.length; i1++) {
				if (i1 < bytes1.length) {
					msgBytes1[i1] = bytes1[i1];
				} else {
					/**32 表示空格，等价于：msgBytes1[i] = " ".getBytes()[0];*/
					msgBytes1[i1] = 32;
				}
			}

			/**
			 * 生成固定长度byte方式二：使用 System.arraycopy 快速复制数组
			 */
			byte[] bytes2 = s1.getBytes("UTF-8");
			byte[] msgBytes2 = new byte[100];
			System.arraycopy(bytes2, 0, msgBytes2, 0, bytes2.length);
			System.out.println(new String(msgBytes2) + "," + msgBytes2.length);


			channel.writeAndFlush(new String(msgBytes1, "UTF-8"));

		}

	}
}