package com.tiantiandou.yasf.service.provider;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.Serializable;

import org.springframework.util.SerializationUtils;

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
public class YasfServiceProviderImpl implements YasfServiceProvider {
    private YasfServiceEntity serviceEntity;

    private YasfServiceHolder serviceHolder;
    
    private Class<?> serviceInstanceClass;

    public YasfServiceEntity getServiceEntity() {
        return serviceEntity;
    }

    public void setServiceEntity(YasfServiceEntity serviceEntity) {
        this.serviceEntity = serviceEntity;
    }

    public YasfServiceHolder getServiceHolder() {
        return serviceHolder;
    }

    public void setServiceHolder(YasfServiceHolder serviceHolder) {
        this.serviceHolder = serviceHolder;
    }

    public Class<?> getServiceInstanceClass() {
        return serviceInstanceClass;
    }
    
    public void setServiceInstanceClass(Class<?> serviceInstanceClass) {
        this.serviceInstanceClass = serviceInstanceClass;
    }

    public YasfResponse invoke(YasfRequest request) {
        Serializable result = serviceHolder.invokeProxyMethod(request);
        YasfResponse response = new YasfResponse();
        response.setData(SerializationUtils.serialize(result));
        response.setResultCode(0);
        response.setResultMessage("result is ok");
        YasfMessageHeader header = new YasfMessageHeader();
        header.setType(0);
        header.setVersion(1);
        header.setSubType(0);
        header.setReserverd(0L);
        header.setRequestType(0);
        response.setHeader(header);
        ByteBuf buf = Unpooled.buffer();
        int length = response.fillBodyTo(buf);
        header.setLength(length);
        buf.release();
        return response;
    }
}
