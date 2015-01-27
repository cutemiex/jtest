package com.tiantiandou.concurrent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/***
 * 类说明
 * 
 * @author <a href="mailto:hukui@alibaba-inc.com">hukui</a>
 * @version 1.0
 * @since 2015年1月9日
 */
public final class ThreadLocalUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(ThreadLocalUtils.class);

    private final static ThreadLocal<String> serializerThreadLocal = new ThreadLocal<String>() {
        @Override
        protected String initialValue() {
            return "I like it";
        }
    };

    private ThreadLocalUtils() {

    }

    public static String getThreadLocalValue() {
        String value = serializerThreadLocal.get();
        serializerThreadLocal.remove();
        return value;
    }

    public static void main(String[] args) {
        LOGGER.debug(getThreadLocalValue());
        LOGGER.debug(getThreadLocalValue());
    }
}
