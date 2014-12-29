package com.tiantiandou.caliper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.caliper.Benchmark;
import com.google.caliper.runner.CaliperMain;

/***
 * 类说明 测试学习google caliper
 * 
 * @author <a href="mailto:hukui@alibaba-inc.com">hukui</a>
 * @version 1.0
 * @since 2014年12月27日
 */
public class CaliperTest extends Benchmark {
    private static final Logger LOGGER = LoggerFactory.getLogger(PerformanceTask.class);

    public void timeMyOperation(int reps) {
        for (int i = 0; i < reps; i++) {
            new PerformanceTask().calc();
        }
    }

    public static void main(String[] args) throws Exception {
        LOGGER.debug("start of test");
        CaliperMain.main(CaliperTest.class, args);
        LOGGER.debug("end of test");
    }
}
