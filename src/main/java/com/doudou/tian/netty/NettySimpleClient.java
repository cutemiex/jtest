package com.doudou.tian.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.Date;
import java.util.List;

public class NettySimpleClient {
	public static void main(String[] args) throws Exception {
		int port = 9999;
		EventLoopGroup workerGroup = new NioEventLoopGroup();

		try {
			Bootstrap b = new Bootstrap(); // (1)
			b.group(workerGroup).channel(NioSocketChannel.class)
					.option(ChannelOption.SO_KEEPALIVE, true); // (4)
			b.handler(new ChannelInitializer<SocketChannel>() {
				@Override
				public void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast(new TimeDecoder(), new TimeClientHandler());
				}
			});
            System.out.println("start connect.....");
			// Start the client.
			ChannelFuture f = b.connect("127.0.0.1", port).sync(); // (5)

			// Wait until the connection is closed.
			f.channel().closeFuture().sync();
		} finally {
			workerGroup.shutdownGracefully();
		}
	}
}

class TimeClientHandler extends ChannelInboundHandlerAdapter {
	private ByteBuf buf;
	private int cnt = 0;

	@Override
	public void handlerAdded(ChannelHandlerContext ctx) {
		buf = ctx.alloc().buffer(4); // (1)
		System.out.println("added");
	}

	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) {
		buf.release(); // (1)
		buf = null;
		System.out.println("release");
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		    cnt ++;
		    long currentTimeMillis = (((ByteBuf)msg).readInt() - 2208988800L) * 1000L;
			System.out.println(new Date(currentTimeMillis));
//			ctx.close();
			ByteBuf time = ctx.alloc().buffer(4);
			time.writeInt(1234);
			ctx.writeAndFlush(time);
			if(cnt >=10){
				ctx.close();
			}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}
}

//class TimeDecoder extends ByteToMessageDecoder { // (1)
//    @Override
//    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) { // (2)
//        if (in.readableBytes() < 4) {
//            return; // (3)
//        }
//
//        out.add(in.readBytes(4)); // (4)
//    }
//}

class TimeDecoder extends ReplayingDecoder<Void> {
    @Override
    protected void decode(
            ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        out.add(in.readBytes(4));
    }
}
