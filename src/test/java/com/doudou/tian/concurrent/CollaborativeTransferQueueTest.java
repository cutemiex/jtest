package com.doudou.tian.concurrent;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

public class CollaborativeTransferQueueTest {

    CollaborativeTransferQueue<Integer> q = new CollaborativeTransferQueue<Integer>();
    final CountDownLatch allExecutorThreadsReady = new CountDownLatch(10);
    final CountDownLatch afterInitBlocker = new CountDownLatch(1);
    final CountDownLatch allDone = new CountDownLatch(10);

    @Test
    public void assertConcurrent() throws InterruptedException {
        final List<Throwable> exceptions = Collections.synchronizedList(new ArrayList<Throwable>());

        final ExecutorService threadPool = Executors.newFixedThreadPool(100);
        try {

            for (int i = 0; i < 10; i++) {

                threadPool.submit(new ThatRun(i));
            }
            // wait until all threads are ready
            assertTrue(
                    "Timeout initializing threads! Perform long lasting initializations before passing runnables to assertConcurrent",
                    allExecutorThreadsReady.await(10, TimeUnit.MILLISECONDS));
            // start all test runners
            afterInitBlocker.countDown();
            assertTrue(" timeout! More than" + 3 + "seconds", allDone.await(1000, TimeUnit.SECONDS));

        } finally {
            threadPool.shutdownNow();
        }
        assertTrue("failed with exception(s)" + exceptions, exceptions.isEmpty());

    }

    class ThatRun implements Runnable {
        private int i;

        public ThatRun(int i) {
            this.i = i;
        }

        public void run() {
            allExecutorThreadsReady.countDown();
            try {
                afterInitBlocker.await();
                for (;;) {
                    Integer r = new Random().nextInt(1000000);
                    if (i % 2 == 0) {
                        try {
                            Integer o = q.poll(3, TimeUnit.SECONDS);
//                            System.out.println(Thread.currentThread().getId() + "Get value " + o);
                        } catch (InterruptedException e) {
//                            System.out.println(Thread.currentThread().getId() + "Timeout for retrive");
                        }
                    } else {
                        try {
//                            System.out.println(Thread.currentThread().getId() + "offer value " + r);
                            q.offer(r, 3, TimeUnit.SECONDS);
                        } catch (InterruptedException e) {
//                            System.out.println(Thread.currentThread().getId() + "TimeOUt for val" + r);
                        }
                    }
                    Thread.sleep(5);
                }
            } catch (final Throwable e) {
                System.out.println(Thread.currentThread().getId() + " is down");
                e.printStackTrace();
            } finally {
                allDone.countDown();
            }
        }
    }

}
