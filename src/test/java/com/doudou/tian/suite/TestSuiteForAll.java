package com.doudou.tian.suite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.doudou.tian.basic.AppTest;
import com.doudou.tian.basic.GcLogicTest;
import com.doudou.tian.concurrent.CollaborativeTransferQueueTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
  AppTest.class,
  CollaborativeTransferQueueTest.class,
  GcLogicTest.class,
})

public class TestSuiteForAll {

}
