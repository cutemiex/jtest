package com.tiantiandou.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.caliper.Benchmark;

/***
 * 类说明 JDK 的学习和测试
 * 
 * @author <a href="mailto:hukui@alibaba-inc.com">hukui</a>
 * @version 1.0
 * @since 2014年12月22日
 */
public final class JdkTest extends Benchmark {
    public static final int LOOP_TIMES = 1000 * 10000;
    private static final Logger LOGGER = LoggerFactory.getLogger("JdkTest");

    private JdkTest() {

    }

    public static void testTime() {
        long start = System.currentTimeMillis();
        for (int i = 0; i < LOOP_TIMES; i++) {
            System.currentTimeMillis();
        }
        LOGGER.debug("currentTimeMills time is  {}", System.currentTimeMillis() - start);
    }

    public static void testJavaInstance() {
        Exception e = new Exception("just test");
        Class<RuntimeException> re = RuntimeException.class;
        if (re.isAssignableFrom(e.getClass())) {
            LOGGER.debug("RuntimeException is assignable from Exception");
        } else {
            LOGGER.debug("Error: RuntimeException is not assignable from Exception");
        }
        if (re.isInstance(e)) {
            LOGGER.debug("e is instance of RuntimeException");
        } else {
            LOGGER.debug("Error: e is not instance of RuntimeException");
        }
    }

    public static void main(String[] args) {
        testTime();
        testJavaInstance();
    }
}
