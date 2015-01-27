package com.tiantiandou.yasf.service.consumer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

import java.io.Serializable;
import java.lang.reflect.Method;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.pool2.KeyedObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tiantiandou.yasf.message.YasfArgument;
import com.tiantiandou.yasf.message.YasfMessageHeader;
import com.tiantiandou.yasf.message.YasfRequest;
import com.tiantiandou.yasf.message.YasfResponse;
import com.tiantiandou.yasf.service.YasfServiceEntity;

/***
 * 具体的proxy代理实现类. 类说明
 * 
 * @author <a href="mailto:hukui@alibaba-inc.com">hukui</a>
 * @version 1.0
 * @since 2015年1月4日
 */
public class YasfServiceProxyNettyImpl implements YasfServiceProxy {

    private static final Logger LOGGER = LoggerFactory.getLogger(YasfServiceProxyNettyImpl.class);
    private YasfServiceConsumer consumer;
    private KeyedObjectPool<YasfServiceEntity, Channel> serviceChannelPool;

    public void setServiceConsumer(YasfServiceConsumer consumer) {
        this.consumer = consumer;
    }

    public void setServiceChannelPool(KeyedObjectPool<YasfServiceEntity, Channel> serviceChannelPool) {
        this.serviceChannelPool = serviceChannelPool;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Exception {
        YasfRequest request = makeRequest(method, args);
        Channel channel = getChannel();

        ChannelPromise promise = channel.newPromise();
        ResponseHandler responseHandler = new ResponseHandler(promise);
        channel.pipeline().addLast(responseHandler);
        ByteBuf buf = Unpooled.buffer();
        request.fillTo(buf);
        channel.writeAndFlush(buf);
        promise.await();
        YasfResponse response = responseHandler.getResponse();
        channel.pipeline().removeLast();
        serviceChannelPool.returnObject(consumer.getServiceEntity(), channel);
        return getResponse(response);
    }

    private YasfRequest makeRequest(Method method, Object[] args) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        YasfRequest request = new YasfRequest();
        request.setMethodName(method.getName());
        request.setYasfService(consumer.getServiceEntity());

        YasfArgument[] arguments = null;
        if (args != null) {
            arguments = new YasfArgument[args.length];
            for (int i = 0; i < args.length; i++) {
                YasfArgument arg = new YasfArgument();
                arg.setType(parameterTypes[i].getName());
                arg.setValue(SerializationUtils.serialize((Serializable) args[i]));
                arguments[i] = arg;
            }
        }
        request.setArguments(arguments);
        request.setHeader(makeHeader());
        ByteBuf buf = Unpooled.buffer();
        request.getHeader().setLength(request.fillBodyTo(buf));
        buf.release();
        return request;
    }

    private YasfMessageHeader makeHeader() {
        YasfMessageHeader header = new YasfMessageHeader();
        header.setType(0);
        header.setVersion(1);
        header.setSubType(0);
        header.setReserverd(0L);
        header.setRequestType(0);
        return header;
    }

    private Object getResponse(YasfResponse response) {
        byte[] data = response.getData();
        if (data == null) {
            return null;
        }
        return SerializationUtils.deserialize(data);
    }

    private Channel getChannel() {
        try {
            Channel channel = serviceChannelPool.borrowObject(consumer.getServiceEntity());
            return channel;
        } catch (Exception e) {
            LOGGER.debug("get netty channel error", e);
        }
        return null;
    }

    /***
     * 类说明
     * 
     * @author <a href="mailto:hukui@alibaba-inc.com">hukui</a>
     * @version 1.0
     * @since 2015年1月7日
     */
    static class ResponseHandler extends ChannelInboundHandlerAdapter {
        private YasfResponse response = null;
        private ChannelPromise promise;

        public ResponseHandler(ChannelPromise promise) {
            this.promise = promise;
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) {
            LOGGER.debug("<<<handler " + System.identityHashCode(this) + "  channelaread");
            ByteBuf buf = (ByteBuf) msg;
            if (buf.readableBytes() < YasfMessageHeader.HEADER_LENGTH) {
                return;
            }
            if (response == null) {
                response = new YasfResponse();
                YasfMessageHeader header = new YasfMessageHeader();
                ByteBuf hdata = Unpooled.buffer(YasfMessageHeader.HEADER_LENGTH);
                buf.readBytes(hdata);
                header.initFrom(hdata);
                response.setHeader(header);
                hdata.release();
                if (buf.readableBytes() < header.getLength()) {
                    response = null;
                    return;
                }
            }
            int bodyLength = response.getHeader().getLength();
            ByteBuf bdata = Unpooled.buffer(bodyLength);
            buf.readBytes(bdata);
            response.initBodyFrom(bdata);
            bdata.release();
            buf.release();
            promise.setSuccess();
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            LOGGER.debug("handler " + System.identityHashCode(this) + "  exception caught");
            cause.printStackTrace();
            ctx.close();
        }

        public YasfResponse getResponse() {
            return response;
        }
    }
}
