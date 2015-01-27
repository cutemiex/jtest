package com.tiantiandou.yasf.service.consumer;

import io.netty.channel.Channel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.pool2.KeyedObjectPool;
import org.apache.commons.pool2.impl.GenericKeyedObjectPool;

import com.tiantiandou.yasf.service.YasfServiceEntity;

/***
 * 类说明
 * 
 * @author <a href="mailto:hukui@alibaba-inc.com">hukui</a>
 * @version 1.0
 * @since 2015年1月4日
 */
public class YasfServiceConsumerFactoryImpl implements YasfServiceConsumerFactory {
    private Map<YasfServiceEntity, YasfServiceConsumer> serviceConsumers =
            new ConcurrentHashMap<YasfServiceEntity, YasfServiceConsumer>();

    private KeyedObjectPool<YasfServiceEntity, Channel> serviceChannelPool;

    public YasfServiceConsumerFactoryImpl() {
        YasfServiceEntity serviceEntity = new YasfServiceEntity();
        serviceEntity.setName("com.tiantiandou.yasf.example.CalculationService");
        serviceEntity.setVersion("1.0");

        serviceChannelPool = new GenericKeyedObjectPool<YasfServiceEntity, Channel>(new NettyChannelFactoryImpl(
                serviceConsumers));

        YasfServiceConsumer consumer = new YasfServiceConsumerImpl(serviceEntity, new YasfServiceProxyNettyImpl());
        consumer.init();
        consumer.getServiceProxy().setServiceConsumer(consumer);
        consumer.getServiceProxy().setServiceChannelPool(serviceChannelPool);
        serviceConsumers.put(serviceEntity, consumer);
    }

    public YasfServiceConsumer getYasfServiceConsumer(YasfServiceEntity serviceEntity) {
        return serviceConsumers.get(serviceEntity);
    }
}
