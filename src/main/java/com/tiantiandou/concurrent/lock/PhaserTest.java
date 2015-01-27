package com.tiantiandou.concurrent.lock;

import java.util.concurrent.Phaser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/****
 * 类说明
 * 
 * @author <a href="mailto:hukui@alibaba-inc.com">hukui</a>
 * @version 1.0
 * @since 2015年1月8日
 */
public final class PhaserTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(PhaserTest.class);
    private static Phaser ph = new Phaser(1);

    private PhaserTest() {

    }

    public static void main(String[] args) {
        ph.arrive();
        ph.arrive();
        LOGGER.debug("finished");
    }

}
