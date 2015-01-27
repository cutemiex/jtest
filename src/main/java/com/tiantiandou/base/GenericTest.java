package com.tiantiandou.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/****
 * 类说明
 * 
 * @author <a href="mailto:hukui@alibaba-inc.com">hukui</a>
 * @version 1.0
 * @since 2015年1月4日
 */
public final class GenericTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(GenericTest.class);

    private GenericTest() {

    }

    public static void main(String[] args) {
        GenericTest test = new GenericTest();
        String s = "I am chinese";
        String u = test.getResult(s);
        LOGGER.debug("u={}", u);
        Integer i = test.getResult(s);
        LOGGER.debug("i={}", i);
    }

    @SuppressWarnings("unchecked")
    public <V, T> V getResult(T input) {
        return (V) input;
    }
}
