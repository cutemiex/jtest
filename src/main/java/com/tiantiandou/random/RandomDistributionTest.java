package com.tiantiandou.random;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/***
 * 类说明
 * 
 * @author <a href="mailto:hukui@alibaba-inc.com">hukui</a>
 * @version 1.0
 * @since 2014年12月19日
 */
public final class RandomDistributionTest {
    private static final Logger LOGGER = LoggerFactory.getLogger("RandomDistributionTest");

    private static final int PERCENTAGE_AUGMENTION = 1000000;

    private static final int LOOPS_INTERVAL = 10000;

    private static final int TIMES_INTERVAL = 100;

    private static final int NUMBER_SIZE = 100;

    private RandomDistributionTest() {

    }

    /***
     * 主函数
     * 
     * @param args args not used
     */
    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        Random r = new Random(startTime);
        long[] freqs = new long[NUMBER_SIZE];
        int loops = 0;
        for (;;) {
            if (loops % LOOPS_INTERVAL == 0) {
                long now = System.currentTimeMillis();
                if (now - startTime > TIMES_INTERVAL) {
                    printTimes(freqs);
                    startTime = now;
                }
            }
            freqs[(int) (Math.abs(r.nextLong() % NUMBER_SIZE))]++;
            loops++;
        }
    }

    private static void printTimes(long[] freqs) {
        long total = 0;
        for (int i = 0; i < freqs.length; i++) {
            total += freqs[i];
        }
        long min = total / freqs.length;
        int maxIndex = 0;
        int minIndex = 0;

        for (int i = 1; i < freqs.length; i++) {
            if (freqs[i] > freqs[maxIndex]) {
                maxIndex = i;
            }
            if (freqs[i] < freqs[minIndex]) {
                minIndex = i;
            }
        }
        LOGGER.debug("min=" + min + " | floor=" + (freqs[maxIndex] - min) + " | ceil=" + (min - freqs[minIndex]));
        LOGGER.debug("floor percentage is =" + (((freqs[maxIndex] - min) * PERCENTAGE_AUGMENTION) / total));
        LOGGER.debug("ceil  percentage is =" + (((min - freqs[minIndex]) * PERCENTAGE_AUGMENTION) / total));
        LOGGER.debug("\nnext round:");
    }
}
