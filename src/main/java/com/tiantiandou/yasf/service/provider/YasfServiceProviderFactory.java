package com.tiantiandou.yasf.service.provider;

import com.tiantiandou.yasf.service.YasfServiceEntity;

/***
 * 类说明
 * 
 * @author <a href="mailto:hukui@alibaba-inc.com">hukui</a>
 * @version 1.0
 * @since 2014年12月31日
 */
public interface YasfServiceProviderFactory {
    YasfServiceProvider getServiceProvider(YasfServiceEntity serviceEntity);
    
    void registerServiceProvider(YasfServiceEntity serviceEntity, YasfServiceProvider serviceProvider);
    
    void init();
}
