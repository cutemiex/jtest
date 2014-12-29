package com.tiantiandou.net;

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
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/***
 * 
 * 类说明
 * 
 * @author <a href="mailto:hukui@alibaba-inc.com">hukui</a>
 * @version 1.0
 * @since 2014年12月19日
 */
public class NettyClient {
    private static final int DEFAULT_PORT = 10000;
    private int port = 0;
    private InetAddress addr;
    

    public NettyClient(int port, String host) {
        try {
            addr = InetAddress.getByName("127.0.0.1");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        this.port = port;
    }

    public void startup() throws Exception {
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup).channel(NioSocketChannel.class).option(ChannelOption.SO_KEEPALIVE, false)
                    .option(ChannelOption.TCP_NODELAY, true);
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new LoggingHandler(LogLevel.WARN));
                    ch.pipeline().addLast(new ClientRecordDecoderHandler());
                }
            });
            ChannelFuture f = b.connect(addr, port).sync();
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        NettyClient client = new NettyClient(DEFAULT_PORT, "127.0.0.1");
        try {
            client.startup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

/***
 * 
 * 类说明
 * 
 * @author <a href="mailto:hukui@alibaba-inc.com">hukui</a>
 * @version 1.0
 * @since 2014年12月19日
 */
class ClientRecordDecoderHandler extends ChannelInboundHandlerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger("ClientRecordDecoderHandler");
    private static final int DEFAULT_BUFFER_SIZE = 200;
    private static final int INT_SIZE = 4;
    private static int num;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        LOGGER.debug("handler " + System.identityHashCode(this) + "  channelactive");
        ByteBuf data = ctx.alloc().buffer(DEFAULT_BUFFER_SIZE);
        data.writeBytes("test_1|".getBytes("UTF-8"));
        data.writeBytes(("" + num++).getBytes());
        data.writeBytes(("|" + System.identityHashCode(this)).getBytes("UTF-8"));
        ByteBuf header = ctx.alloc().buffer(INT_SIZE + data.readableBytes());
        header.writeInt(data.readableBytes());
        data.readBytes(header);

        ctx.writeAndFlush(header);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        LOGGER.debug("handler " + System.identityHashCode(this) + "  channelaread");
        // ReferenceCountUtil.release(msg);
        try {
            ByteBuf data = ctx.alloc().buffer(DEFAULT_BUFFER_SIZE);
            data.writeBytes("test_1|".getBytes("UTF-8"));
            data.writeBytes(("" + num++).getBytes());
            data.writeBytes(("|" + System.identityHashCode(this)).getBytes("UTF-8"));
            ByteBuf header = ctx.alloc().buffer(INT_SIZE + data.readableBytes());
            header.writeInt(data.readableBytes());
            data.readBytes(header);

            ctx.writeAndFlush(header);
            // Thread.sleep(100);
        } catch (Exception e) {
            LOGGER.error("channelRead exception", e);
        }

    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        LOGGER.debug("handler " + System.identityHashCode(this) + "  registered");
        super.channelRegistered(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        LOGGER.debug("handler " + System.identityHashCode(this) + "  unregistered");
        super.channelUnregistered(ctx);
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        LOGGER.debug("handler " + System.identityHashCode(this) + "  added");
        super.handlerAdded(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LOGGER.debug("handler " + System.identityHashCode(this) + "  exception caught");
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        LOGGER.debug("handler " + System.identityHashCode(this) + "  removed");
        super.handlerRemoved(ctx);
    }
}
