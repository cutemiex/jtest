package com.tiantiandou.yasf.service.consumer;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.util.Map;

import org.apache.commons.pool2.KeyedPooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

import com.tiantiandou.yasf.service.YasfServiceEntity;

/***
 * 类说明
 * 
 * @author <a href="mailto:hukui@alibaba-inc.com">hukui</a>
 * @version 1.0
 * @since 2015年1月6日
 */
public class NettyChannelFactoryImpl implements KeyedPooledObjectFactory<YasfServiceEntity, Channel> {
    private Map<YasfServiceEntity, YasfServiceConsumer> serviceConsumers;

    private Bootstrap bootstrap;

    public NettyChannelFactoryImpl(Map<YasfServiceEntity, YasfServiceConsumer> serviceConsumers) {
        super();
        this.serviceConsumers = serviceConsumers;
        initNetty();
    }

    public PooledObject<Channel> makeObject(YasfServiceEntity key) throws Exception {
        YasfServiceConsumer consumer = serviceConsumers.get(key);
        YasfServiceEndpoint endpoint = consumer.getServiceEndpoint().get(0);
        ChannelFuture cf = bootstrap.connect(endpoint.getHost(), endpoint.getPort());
        cf.awaitUninterruptibly();
        return new DefaultPooledObject<Channel>(cf.channel());
    }

    public void destroyObject(YasfServiceEntity key, PooledObject<Channel> p) throws Exception {
        Channel channel = p.getObject();
        channel.close();
    }

    public boolean validateObject(YasfServiceEntity key, PooledObject<Channel> p) {
        Channel channel = p.getObject();
        return channel.isActive();
    }

    public void activateObject(YasfServiceEntity key, PooledObject<Channel> p) throws Exception {

    }

    public void passivateObject(YasfServiceEntity key, PooledObject<Channel> p) throws Exception {

    }

    private void initNetty() {
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(workerGroup).channel(NioSocketChannel.class).option(ChannelOption.SO_KEEPALIVE, false)
                .option(ChannelOption.TCP_NODELAY, true);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new LoggingHandler(LogLevel.WARN));
            }
        });
    }
}
