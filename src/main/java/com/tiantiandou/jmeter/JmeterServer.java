package com.tiantiandou.jmeter;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
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
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tiantiandou.net.NettyServer;

/***
 * Jmeter测试用的类 TODO 类说明
 * 
 * @author <a href="mailto:hukui@alibaba-inc.com">hukui</a>
 * @version 1.0
 * @since 2014年12月27日
 */
public class JmeterServer {
    private static final Logger LOGGER = LoggerFactory.getLogger("JmeterServer");
    private static final int SO_BLACKLOG = 100;
    private static final int DEFAULT_PORT = 10000;
    private int port;

    public JmeterServer(int port) {
        this.port = port;
    }

    public void startup() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).
                    option(ChannelOption.SO_BACKLOG, SO_BLACKLOG).handler(new LoggingHandler(LogLevel.INFO));
            b.childOption(ChannelOption.TCP_NODELAY, true).childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline p = ch.pipeline();
                    p.addLast(new LoggingHandler(LogLevel.INFO));
                    // p.addLast(new ChannelTrafficShapingHandler(100000, 1, 1000, 3000));
                    p.addLast(new ServerRecordDecoderHandler());
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
class ServerRecordDecoderHandler extends ChannelInboundHandlerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger("ServerRecordDecoderHandler");
    private static final int INT_BYTE_SIZE = 4;
    private static final int DEFAULT_BUFFER_SIZE = 100;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        if (buf.readableBytes() < INT_BYTE_SIZE) {
            return;
        }
        int seq = buf.readInt();
        ByteBuf data = Unpooled.buffer(DEFAULT_BUFFER_SIZE);
        data.writeBytes(("I like chinese " + (seq + 1)).getBytes("UTF-8"));
        LOGGER.debug("finish process of " + seq);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // TODO Auto-generated method stub
        super.exceptionCaught(ctx, cause);
    }
}
