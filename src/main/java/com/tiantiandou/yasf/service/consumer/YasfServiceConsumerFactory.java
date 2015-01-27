package com.tiantiandou.yasf.service.consumer;

import com.tiantiandou.yasf.service.YasfServiceEntity;

/****
 * 类说明
 * 
 * @author <a href="mailto:hukui@alibaba-inc.com">hukui</a>
 * @version 1.0
 * @since 2015年1月4日
 */
public interface YasfServiceConsumerFactory {
    YasfServiceConsumer getYasfServiceConsumer(YasfServiceEntity serviceEntity);
}
