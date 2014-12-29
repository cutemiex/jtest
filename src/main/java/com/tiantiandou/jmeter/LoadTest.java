package com.tiantiandou.jmeter;

import org.apache.jmeter.protocol.tcp.sampler.TCPClient;

public class LoadTest {
    public static void main(String[] args) throws Exception {
        Class<?> cls = Class.forName("com.tiantiandou.jmeter.JmeterNettyClientHandler", false, Thread.currentThread()
                .getContextClassLoader());
        Object obj = cls.newInstance();
        if(TCPClient.class.isAssignableFrom(cls)){
            System.out.println("class name is: " + obj.getClass().getName());
        }
    }
}
