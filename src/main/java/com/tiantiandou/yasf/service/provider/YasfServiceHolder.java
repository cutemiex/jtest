package com.tiantiandou.yasf.service.provider;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tiantiandou.yasf.message.YasfArgument;
import com.tiantiandou.yasf.message.YasfRequest;

/****
 * 服务提供的代理类 TODO 类说明
 * 
 * @author <a href="mailto:hukui@alibaba-inc.com">hukui</a>
 * @version 1.0
 * @since 2015年1月4日
 */
public class YasfServiceHolder {
    private static final Logger LOGGER = LoggerFactory.getLogger(YasfServiceHolder.class);

    private YasfServiceProvider serviceProvider;

    public YasfServiceHolder(YasfServiceProvider serviceProvider) {
        super();
        this.serviceProvider = serviceProvider;
    }

    public Serializable invokeProxyMethod(YasfRequest request) {
        try {
            Object instance = serviceProvider.getServiceInstanceClass().newInstance();
            String methodName = request.getMethodName();
            YasfArgument[] yasfArgs = request.getArguments();
            Class<?>[] parameterTypes = null;
            Object[] args = null;
            if (yasfArgs != null) {
                parameterTypes = new Class<?>[yasfArgs.length];
                args = new Object[yasfArgs.length];
                for (int i = 0; i < yasfArgs.length; i++) {
                    parameterTypes[i] = getClassType(yasfArgs[i].getType());
                    args[i] = deserializeArgumentValue(yasfArgs[i].getValue());
                }
            }
            Method method = serviceProvider.getServiceInstanceClass().getDeclaredMethod(methodName, parameterTypes);
            Serializable result = (Serializable) method.invoke(instance, args);
            return result;
        } catch (InstantiationException e) {
            LOGGER.debug("InstantiationException", e);
        } catch (IllegalAccessException e) {
            LOGGER.debug("InstantiationException", e);
        } catch (NoSuchMethodException e) {
            LOGGER.debug("InstantiationException", e);
        } catch (IllegalArgumentException e) {
            LOGGER.debug("InstantiationException", e);
        } catch (InvocationTargetException e) {
            LOGGER.debug("InstantiationException", e);
        } catch (ClassNotFoundException e) {
            LOGGER.debug("InstantiationException", e);
        }
        return null;
    }

    private Object deserializeArgumentValue(byte[] data) {
        if (data == null) {
            return null;
        } else {
            return SerializationUtils.deserialize(data);
        }
    }

    private Class<?> getClassType(String type) throws ClassNotFoundException {
        return ClassUtils.getClass(type);
    }

}
