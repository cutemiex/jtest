package com.tiantiandou.yasf.example;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/****
 * 类说明
 * 
 * @author <a href="mailto:hukui@alibaba-inc.com">hukui</a>
 * @version 1.0
 * @since 2015年1月4日
 */
public class CalculationServiceImpl implements CalculationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CalculationServiceImpl.class);

    public String claculate(String user, char[] message, int[] items, int optional) {
        int sum = 0;
        for (int i = 0; i < items.length; i++) {
            sum += items[i];
        }
        sum += optional;

        String res = "user=" + user + "| message=" + new String(message) + "|sum=" + sum;
        LOGGER.debug(res);
        return res;
    }

    public String claculateVoidValue() {
        return "I think it is " + new Random().nextInt();
    }

    public void claculateAllVoidValue() {
        return;
    }

}
