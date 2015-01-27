package com.tiantiandou.yasf.service.consumer;

import java.util.List;

import com.tiantiandou.yasf.service.YasfServiceEntity;

/***
 * 类说明
 * 
 * @author <a href="mailto:hukui@alibaba-inc.com">hukui</a>
 * @version 1.0
 * @since 2014年12月26日
 */
public interface YasfServiceConsumer {
    YasfServiceEntity getServiceEntity();

    void setServiceEntity(YasfServiceEntity serviceEntity);

    void init();

    <T> T getInvokableInterface();

    List<YasfServiceEndpoint> getServiceEndpoint();

    YasfServiceProxy getServiceProxy();

    void setServiceProxy(YasfServiceProxy serviceProxy);
}
