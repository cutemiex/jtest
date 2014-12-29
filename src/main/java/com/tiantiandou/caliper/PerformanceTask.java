package com.tiantiandou.caliper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/***
 * 类说明 Caliper测试类， 测试运行速度。
 * 
 * @author <a href="mailto:hukui@alibaba-inc.com">hukui</a>
 * @version 1.0
 * @since 2014年12月27日
 */
public class PerformanceTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(PerformanceTask.class);
    private static final int CALC_LOOPS = 100 * 100;

    public void calc() {
        for (int i = 0; i < CALC_LOOPS; i++) {
            LOGGER.debug("{} is incremental", i);
        }
    }
}
