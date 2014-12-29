package com.tiantiandou.suite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.tiantiandou.base.AppTest;
import com.tiantiandou.base.GcLogicTest;
import com.tiantiandou.concurrent.CollaborativeTransferQueueTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
  AppTest.class,
  CollaborativeTransferQueueTest.class,
  GcLogicTest.class,
})

public class TestSuiteForAll {

}
