package com.tiantiandou.yasf.example;

/***
 * 测试的接口 类说明
 * 
 * @author <a href="mailto:hukui@alibaba-inc.com">hukui</a>
 * @version 1.0
 * @since 2015年1月4日
 */
public interface CalculationService {
    String claculate(String user, char[] message, int[] items, int optional);

    String claculateVoidValue();

    void claculateAllVoidValue();
}
