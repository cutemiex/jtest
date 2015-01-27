package com.tiantiandou.yasf.example;

import com.tiantiandou.yasf.service.YasfServiceEntity;
import com.tiantiandou.yasf.service.provider.YasfServiceHolder;
import com.tiantiandou.yasf.service.provider.YasfServiceProvider;
import com.tiantiandou.yasf.service.provider.YasfServiceProviderFactoryImpl;
import com.tiantiandou.yasf.service.provider.YasfServiceProviderImpl;

/***
 * 类说明
 * 
 * @author <a href="mailto:hukui@alibaba-inc.com">hukui</a>
 * @version 1.0
 * @since 2015年1月4日
 */
public final class CalcServer {

    private CalcServer() {

    }

    private static YasfServiceEntity initServiceEntity() {
        YasfServiceEntity serviceEntity = new YasfServiceEntity();
        serviceEntity.setName("com.tiantiandou.yasf.example.CalculationService");
        serviceEntity.setVersion("1.0");
        return serviceEntity;
    }

    public static void main(String[] args) throws Exception {
        YasfServiceEntity serviceEntity = initServiceEntity();
        YasfServiceProvider serviceProvider = new YasfServiceProviderImpl();
        serviceProvider.setServiceEntity(serviceEntity);
        serviceProvider.setServiceHolder(new YasfServiceHolder(serviceProvider));
        serviceProvider.setServiceInstanceClass(Class.forName("com.tiantiandou.yasf.example.CalculationServiceImpl"));

        YasfServiceProviderFactoryImpl factory = new YasfServiceProviderFactoryImpl();
        factory.registerServiceProvider(serviceEntity, serviceProvider);
        factory.init();
    }
}
