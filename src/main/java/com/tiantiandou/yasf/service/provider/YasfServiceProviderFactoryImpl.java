package com.tiantiandou.yasf.service.provider;

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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tiantiandou.yasf.message.YasfMessageHeader;
import com.tiantiandou.yasf.message.YasfRequest;
import com.tiantiandou.yasf.message.YasfResponse;
import com.tiantiandou.yasf.service.YasfServiceEntity;

/***
 * 类说明
 * 
 * @author <a href="mailto:hukui@alibaba-inc.com">hukui</a>
 * @version 1.0
 * @since 2014年12月31日
 */
public class YasfServiceProviderFactoryImpl implements YasfServiceProviderFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(YasfServiceProviderFactoryImpl.class);
    private static final int SO_BLACKLOG = 100;
    private static final int DEFAULT_PORT = 10000;
    private Map<YasfServiceEntity, YasfServiceProvider> entityProviderMap =
            new ConcurrentHashMap<YasfServiceEntity, YasfServiceProvider>();

    public YasfServiceProviderFactoryImpl() {
        
    }

    public YasfServiceProvider getServiceProvider(YasfServiceEntity serviceEntity) {
        return entityProviderMap.get(serviceEntity);
    }

    public void registerServiceProvider(YasfServiceEntity serviceEntity, YasfServiceProvider serviceProvider) {
        entityProviderMap.put(serviceEntity, serviceProvider);
    }

    public void init() {
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
                    p.addLast(new ServerHandler(new ServiceCallbackImpl(entityProviderMap)));
                }
            });

            // Start the server.
            ChannelFuture f = b.bind(DEFAULT_PORT).sync();

            // Wait until the server socket is closed.
            f.channel().closeFuture().sync();
        } catch (Exception e) {
            LOGGER.error("Service Channel exception ", e);
        } finally {
            // Shut down all event loops to terminate all threads.
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    /***
     * 类说明
     * 
     * @author <a href="mailto:hukui@alibaba-inc.com">hukui</a>
     * @version 1.0
     * @since 2015年1月7日
     */
    interface ServiceCallback {
        YasfResponse callService(YasfRequest request);
    }

    /***
     * 类说明
     * 
     * @author <a href="mailto:hukui@alibaba-inc.com">hukui</a>
     * @version 1.0
     * @since 2015年1月7日
     */
    static class ServiceCallbackImpl implements ServiceCallback {
        private Map<YasfServiceEntity, YasfServiceProvider> entityProviderMap;

        public ServiceCallbackImpl(Map<YasfServiceEntity, YasfServiceProvider> entityProviderMap) {
            this.entityProviderMap = entityProviderMap;
        }

        public YasfResponse callService(YasfRequest request) {
            return entityProviderMap.get(request.getYasfServiceEntity()).invoke(request);
        }
    }

    /***
     * 类说明
     * 
     * @author <a href="mailto:hukui@alibaba-inc.com">hukui</a>
     * @version 1.0
     * @since 2015年1月7日
     */
    static class ServerHandler extends ChannelInboundHandlerAdapter {
        private static final Logger LOGGER = LoggerFactory.getLogger("ServerHandler");

        private ServiceCallback callback;

        private YasfRequest request;

        public ServerHandler(ServiceCallback callback) {
            this.callback = callback;
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) {
            LOGGER.debug("handler " + System.identityHashCode(this) + "  channelaread");
            ByteBuf buf = (ByteBuf) msg;
            if (request == null) {
                if (buf.readableBytes() < YasfMessageHeader.HEADER_LENGTH) {
                    return;
                }
                request = new YasfRequest();
                YasfMessageHeader header = new YasfMessageHeader();
                ByteBuf hdata = Unpooled.buffer(YasfMessageHeader.HEADER_LENGTH);
                buf.readBytes(hdata);
                header.initFrom(hdata);
                request.setHeader(header);
                hdata.release();
                if (buf.readableBytes() < header.getLength()) {
                    request = null;
                    return;
                }
            }
            int bodyLength = request.getHeader().getLength();
            ByteBuf bdata = Unpooled.buffer(bodyLength);
            buf.readBytes(bdata);
            request.initBodyFrom(bdata);
            YasfResponse response = callback.callService(request);
            request = null;
            ByteBuf rdata = Unpooled.buffer();
            response.fillTo(rdata);
            ctx.writeAndFlush(rdata);
            bdata.release();
            buf.release();
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            LOGGER.debug("handler " + System.identityHashCode(this) + "exception caught");
            cause.printStackTrace();
            ctx.close();
        }
    }
}
