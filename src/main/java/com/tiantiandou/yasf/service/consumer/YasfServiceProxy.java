package com.tiantiandou.yasf.service.consumer;

import io.netty.channel.Channel;

import java.lang.reflect.InvocationHandler;

import org.apache.commons.pool2.KeyedObjectPool;

import com.tiantiandou.yasf.service.YasfServiceEntity;

/****
 * 请求的代理类 类说明
 * 
 * @author <a href="mailto:hukui@alibaba-inc.com">hukui</a>
 * @version 1.0
 * @since 2015年1月4日
 */
public interface YasfServiceProxy extends InvocationHandler {
    void setServiceConsumer(YasfServiceConsumer consumer);

    void setServiceChannelPool(KeyedObjectPool<YasfServiceEntity, Channel> serviceChannelPool);
}
