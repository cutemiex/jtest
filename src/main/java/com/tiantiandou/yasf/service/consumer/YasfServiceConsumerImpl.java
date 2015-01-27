package com.tiantiandou.yasf.service.consumer;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tiantiandou.yasf.service.YasfServiceEntity;

/***
 * 类说明
 * 
 * @author <a href="mailto:hukui@alibaba-inc.com">hukui</a>
 * @version 1.0
 * @since 2015年1月4日
 */
public class YasfServiceConsumerImpl implements YasfServiceConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(YasfServiceConsumerImpl.class);

    private static final short SERVICE_PORT = 10000;

    private YasfServiceEntity serviceEntity;

    private YasfServiceProxy serviceProxy;

    private List<YasfServiceEndpoint> serviceEndPoints = new ArrayList<YasfServiceEndpoint>();

    private Object serviceInterface;


    public YasfServiceConsumerImpl(YasfServiceEntity serviceEntity, YasfServiceProxy serviceProxy) {
        super();
        this.serviceEntity = serviceEntity;
        this.serviceProxy = serviceProxy;
    }

    public YasfServiceEntity getServiceEntity() {
        return serviceEntity;
    }

    public void setServiceEntity(YasfServiceEntity serviceEntity) {
        this.serviceEntity = serviceEntity;
    }

    public void init() {
        serviceEndPoints.add(new YasfServiceEndpoint("127.0.0.1", SERVICE_PORT));
    }

    public List<YasfServiceEndpoint> getServiceEndpoint() {
        return serviceEndPoints;
    }

    public YasfServiceProxy getServiceProxy() {
        return serviceProxy;
    }

    public void setServiceProxy(YasfServiceProxy serviceProxy) {
        this.serviceProxy = serviceProxy;
    }

    @SuppressWarnings("unchecked")
    public synchronized <T> T getInvokableInterface() {
        if (serviceInterface != null) {
            return (T) serviceInterface;
        }
        Class<?>[] interfaces = new Class<?>[1];
        try {
            interfaces[0] = Class.forName(serviceEntity.getName());
        } catch (Exception e) {
            LOGGER.warn("serviceEntity.getName() not exists", e);
        }
        serviceInterface = Proxy.newProxyInstance(getClass().getClassLoader(), interfaces, serviceProxy);
        return (T) serviceInterface;
    }
}
