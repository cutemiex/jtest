package com.tiantiandou.base;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.junit.Test;

import com.tiantiandou.base.GcLogic;

public class GcLogicTest {

    @Test
    public void test() {
        try {
            GcLogic.test();
        } catch (Exception e) {
            assertThat(e, instanceOf(IOException.class));
        }
    }

}
