package com.tiantiandou.base;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/****
 * 类说明 放射相关的一些类的测试
 * 
 * @author <a href="mailto:hukui@alibaba-inc.com">hukui</a>
 * @version 1.0
 * @since 2015年1月4日
 */
public final class ReflectionTest {
    private static final Logger LOGGER = LoggerFactory.getLogger("ReflectionTest");

    private ReflectionTest() {

    }

    public static void main(String[] args) throws Exception {
        Class<?> clazz = String.class;
        Method[] methods = clazz.getDeclaredMethods();
        for (int i = 0; i < methods.length; i++) {
            Method m = methods[i];
            if (m.getName().contains("getChars")) {
                LOGGER.debug("MethodName: " + m.getName());
                Class<?>[] types = m.getParameterTypes();
                for (int j = 0; j < types.length; j++) {
                    LOGGER.debug(types[j].getName() + "|" + types[j].getName());
                }
            }
        }
        char[] x = new char[0];
        LOGGER.debug("char[] class is : " + x.getClass().getCanonicalName());
        LOGGER.debug("Object[] class is : " + (new Object[0]).getClass().getCanonicalName());
        LOGGER.debug("char[][] refletec type is : " + Class.forName("[[C").getCanonicalName());
        LOGGER.debug("int refletec type is : " + Class.forName("int.class").getCanonicalName());
    }
}
