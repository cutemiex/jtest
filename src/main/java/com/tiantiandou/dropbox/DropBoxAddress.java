package com.tiantiandou.dropbox;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tiantiandou.caliper.PerformanceTask;

/***
 * 类说明
 * 
 * @author <a href="mailto:hukui@alibaba-inc.com">hukui</a>
 * @version 1.0
 * @since 2014年12月27日
 */
public final class DropBoxAddress {
    private static final Logger LOGGER = LoggerFactory.getLogger(PerformanceTask.class);
    private static final int ADDRESS_COUNT = 100;

    private DropBoxAddress() {

    }

    public static void main(String[] args) {
        for (int i = 1; i < ADDRESS_COUNT; i++) {
            LOGGER.debug("108.160.166.138 dl-client" + i + ".dropbox.com");
        }
    }
}
