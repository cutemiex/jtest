package com.tiantiandou.yasf.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tiantiandou.yasf.service.YasfServiceEntity;
import com.tiantiandou.yasf.service.consumer.YasfServiceConsumer;
import com.tiantiandou.yasf.service.consumer.YasfServiceConsumerFactoryImpl;

/***
 * 类说明
 * 
 * @author <a href="mailto:hukui@alibaba-inc.com">hukui</a>
 * @version 1.0
 * @since 2015年1月4日
 */
public final class CalcClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(CalcClient.class);
    private static final int ARRAY_LENGTH = 100;

    private CalcClient() {

    }

    private static YasfServiceEntity initServiceEntity() {
        YasfServiceEntity serviceEntity = new YasfServiceEntity();
        serviceEntity.setName("com.tiantiandou.yasf.example.CalculationService");
        serviceEntity.setVersion("1.0");
        return serviceEntity;
    }

    public static void main(String[] args) throws Exception {
        YasfServiceEntity serviceEntity = initServiceEntity();
        YasfServiceConsumerFactoryImpl factory = new YasfServiceConsumerFactoryImpl();
        YasfServiceConsumer comsumer = factory.getYasfServiceConsumer(serviceEntity);
        CalculationService service = comsumer.getInvokableInterface();
        int[] data = new int[ARRAY_LENGTH];
        for (int i = 0; i < ARRAY_LENGTH; i++) {
            data[i] = i * 2;
        }
        String res = service.claculate("tommy", "test service".toCharArray(), data, 1);
        LOGGER.debug("result is = " + res);

        res = service.claculateVoidValue();
        LOGGER.debug("result is = " + res);

        service.claculateAllVoidValue();
        LOGGER.debug("finish claculateAllVoidValue");
    }
}
