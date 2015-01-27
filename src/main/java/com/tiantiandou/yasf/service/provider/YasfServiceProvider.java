package com.tiantiandou.yasf.service.provider;

import com.tiantiandou.yasf.message.YasfRequest;
import com.tiantiandou.yasf.message.YasfResponse;
import com.tiantiandou.yasf.service.YasfServiceEntity;

/***
 * 类说明
 * 
 * @author <a href="mailto:hukui@alibaba-inc.com">hukui</a>
 * @version 1.0
 * @since 2014年12月19日
 */
public interface YasfServiceProvider {
    YasfServiceEntity getServiceEntity();

    void setServiceEntity(YasfServiceEntity serviceEntity);

    YasfServiceHolder getServiceHolder();

    void setServiceHolder(YasfServiceHolder serviceHolder);

    Class<?> getServiceInstanceClass();
    
    void setServiceInstanceClass(Class<?> serviceInstanceClass);

    YasfResponse invoke(YasfRequest request);
}
