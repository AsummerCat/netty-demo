package com.linjingc.nettysimpledemo;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;

public class NettyServer {
	public static void main(String[] args) {
		ServerBootstrap serverBootstrap = new ServerBootstrap();

		NioEventLoopGroup boos = new NioEventLoopGroup();
		NioEventLoopGroup worker = new NioEventLoopGroup();
		serverBootstrap
				//分组
				.group(boos, worker)
				//管道
				.channel(NioServerSocketChannel.class)
				//来监听已经连接的客户端的Channel的动作和状态。
				.childHandler(new ChannelInitializer<NioSocketChannel>() {

					              @Override
					              protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
						              //pipeline是伴随Channel的存在而存在的，交互信息通过它进行传递，我
						              nioSocketChannel.pipeline().addLast(new StringDecoder());
						              nioSocketChannel.pipeline().addLast(new SimpleChannelInboundHandler<String>() {
							              @Override
							              protected void channelRead0(ChannelHandlerContext channelHandlerContext, String msg) throws Exception {
								              System.out.println(msg);
							              }
						              });
					              }
				              }
				)
				.bind(8000);
	}

}
