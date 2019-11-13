package com.linjingc.nettywebsocketdemo.webSocket;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 *  Netty服务器处理链
 */
public class NioWebSocketChannelInitializer extends ChannelInitializer<SocketChannel> {
	@Override
	protected void initChannel(SocketChannel ch) {
		//设置log监听器，并且日志级别为debug，方便观察运行流程
		ch.pipeline().addLast("logging", new LoggingHandler("INFO"));
		//设置解码器
		ch.pipeline().addLast("http-codec", new HttpServerCodec());
		//聚合器，使用websocket会用到
		ch.pipeline().addLast("aggregator", new HttpObjectAggregator(65536));
		//用于大数据的分区传输
		ch.pipeline().addLast("http-chunked", new ChunkedWriteHandler());
		//用于处理websocket, /ws为访问websocket时的uri
		ch.pipeline().addLast(new WebSocketServerProtocolHandler("/ws"));
		//自定义的业务handler
		ch.pipeline().addLast("handler", new NioWebSocketHandler());
	}
}

