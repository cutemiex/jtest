package com.doudou.tian.concurrent;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.Test;

import com.doudou.tian.concurrent.lock.LatchDown;

public class CollaborativeTransferQueueTest {

    CollaborativeTransferQueue<Integer> q = new CollaborativeTransferQueue<Integer>();
//	SynchronousQueue<Integer> q = new SynchronousQueue<Integer>();

	final LatchDown allExecutorThreadsReady = new LatchDown(10);
    final LatchDown afterInitBlocker = new LatchDown(1);
    final LatchDown allDone = new LatchDown(10);

    @Test
    public void assertConcurrent() throws InterruptedException, TimeoutException {
        final List<Throwable> exceptions = Collections.synchronizedList(new ArrayList<Throwable>());

        final ExecutorService threadPool = Executors.newFixedThreadPool(100);
        try {

            for (int i = 0; i < 10; i++) {

                threadPool.submit(new ThatRun(i));
            }
            // wait until all threads are ready
            allExecutorThreadsReady.await(10, TimeUnit.MILLISECONDS);
            
            // start all test runners
            afterInitBlocker.countDown();
            allDone.await(300000, TimeUnit.SECONDS);

        } finally {
            threadPool.shutdownNow();
        }
        assertTrue("failed with exception(s)" + exceptions, exceptions.isEmpty());

    }

    class ThatRun implements Runnable {
        private int mod;

        public ThatRun(int r) {
            this.mod = r;
        }

        public void run() {
            allExecutorThreadsReady.countDown();
            try {
                afterInitBlocker.await();
                for (int i =0; i< 5000 ; i++) {
                    Integer r = new Random().nextInt(500);
                    if (mod % 2 == 0) {
                        try {
                            Integer o = q.poll(3, TimeUnit.SECONDS);
                            System.out.println(Thread.currentThread().getId() + "Get value " + o);
                        } catch (InterruptedException e) {
                            System.out.println(Thread.currentThread().getId() + "Timeout for retrive");
                        }
                    } else {
                        try {
                            System.out.println(Thread.currentThread().getId() + "offer value " + r);
                            q.offer(r, 3, TimeUnit.SECONDS);
                        } catch (InterruptedException e) {
                            System.out.println(Thread.currentThread().getId() + "TimeOUt for val" + r);
                        }
                    }
//                    Thread.sleep(5);
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
