package com.tiantiandou.net;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.nio.charset.Charset;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/***
 * 类说明
 * 
 * @author <a href="mailto:hukui@alibaba-inc.com">hukui</a>
 * @version 1.0
 * @since 2014年12月19日
 */
public class NettyServer {
    private static final Logger LOGGER = LoggerFactory.getLogger("NettyServer");
    private static final int SO_BLACKLOG = 100;
    private static final int DEFAULT_PORT = 54151;
    private int port;

    public NettyServer(int port) {
        this.port = port;
    }

    public void startup() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_REUSEADDR, true).option(ChannelOption.SO_BACKLOG, SO_BLACKLOG)
                    .handler(new LoggingHandler(LogLevel.INFO));
            b.childOption(ChannelOption.TCP_NODELAY, true).childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline p = ch.pipeline();
                    p.addLast(new LoggingHandler(LogLevel.INFO));
                    // p.addLast(new ChannelTrafficShapingHandler(100000, 1, 1000, 3000));
                    p.addLast(new ServerRecordDecoderHandler());
                    p.addLast(new ServerHandler());
                }
            });

            // Start the server.
            ChannelFuture f = b.bind(port).sync();

            // Wait until the server socket is closed.
            f.channel().closeFuture().sync();
        } finally {
            // Shut down all event loops to terminate all threads.
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        NettyServer server = new NettyServer(DEFAULT_PORT);
        try {
            server.startup();
        } catch (Exception e) {
            LOGGER.error("start server", e);
        }
    }
}

/***
 * 类说明
 * 
 * @author <a href="mailto:hukui@alibaba-inc.com">hukui</a>
 * @version 1.0
 * @since 2014年12月19日
 */
class ServerRecordDecoderHandler extends ByteToMessageDecoder {
    private static final Logger LOGGER = LoggerFactory.getLogger("ServerRecordDecoderHandler");
    private static final int INT_SIZE = 4;
    private int status = 0;
    private int length = 0;
    private String data = null;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        LOGGER.debug("Status= " + status + "| length=" + length + "|data=" + data + "|avail="
                + in.readableBytes());
        if (status == 0 && in.readableBytes() >= INT_SIZE) {
            status = 1;
            length = in.readInt();
        }
        if (status == 1 && in.readableBytes() >= length) {
            ByteBuf buf = ctx.alloc().buffer(length);
            in.readBytes(buf);
            data = buf.toString(Charset.forName("UTF-8"));
            status = 0;
            out.add(data);
            buf.release();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        LOGGER.error("exceptionCaught", cause);
        ctx.close();
    }
}

/***
 * 类说明
 * 
 * @author <a href="mailto:hukui@alibaba-inc.com">hukui</a>
 * @version 1.0
 * @since 2014年12月19日
 */
class ServerHandler extends ChannelInboundHandlerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger("ServerHandler");

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        LOGGER.debug("handler " + System.identityHashCode(this) + "  channelactive");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        LOGGER.debug("handler " + System.identityHashCode(this) + "  channelaread");
        try {
            String data = (String) msg;
            LOGGER.debug(data);
            ByteBuf buf = ctx.alloc().buffer();
            buf.writeInt(2);
            ctx.writeAndFlush(buf);
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
        LOGGER.debug("handler " + System.identityHashCode(this) + "  channel added");
        super.handlerAdded(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LOGGER.debug("handler " + System.identityHashCode(this) + "exception caught");
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        LOGGER.debug("handler " + System.identityHashCode(this) + "removed");
        super.handlerRemoved(ctx);
    }
}
