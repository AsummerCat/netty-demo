package com.linjingc.nettywebsocketdemo.webSocket;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 初始化NioWebSocketHandler
 * Netty服务器HTTP请求处理器 针对websocket
 *
 * @author Administrator
 */
@Component()
@Qualifier("nioWebSocketHandler")
@ChannelHandler.Sharable
public class NioWebSocketHandler extends SimpleChannelInboundHandler<WebSocketFrame> {


	private WebSocketServerHandshaker handshaker;







	/**
	 * 描述：读取完连接的消息后，对消息进行处理。
	 * 这里主要是处理WebSocket请求
	 */
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame msg) throws Exception {
		System.out.println("收到消息：" + msg);
		//处理websocket客户端的消息
		handlerWebSocketFrame(ctx, msg);
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		//添加连接
		System.out.println("客户端加入连接：" + ctx.channel());
		ChannelSupervise.addChannel(ctx.channel());
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		//断开连接
		System.out.println("客户端断开连接：" + ctx.channel());
		ChannelSupervise.removeChannel(ctx.channel());
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}

	private void handlerWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
		// 判断是否关闭链路的指令
		if (frame instanceof CloseWebSocketFrame) {
			handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
			return;
		}
		// 判断是否ping消息
		if (frame instanceof PingWebSocketFrame) {
			ctx.channel().write(
					new PongWebSocketFrame(frame.content().retain()));
			return;
		}
		// 本例程仅支持文本消息，不支持二进制消息
		if (!(frame instanceof TextWebSocketFrame)) {
			System.out.println("本例程仅支持文本消息，不支持二进制消息");
			throw new UnsupportedOperationException(String.format(
					"%s frame types not supported", frame.getClass().getName()));
		}
		// 返回应答消息
		String request = ((TextWebSocketFrame) frame).text();
		System.out.println("服务端收到：" + request);
		TextWebSocketFrame tws = new TextWebSocketFrame(new Date().toString()
				+ ctx.channel().id() + "：" + request);
		// 群发
		ChannelSupervise.send2All(tws);
		// 返回【谁发的发给谁】
		// ctx.channel().writeAndFlush(tws);
	}
}