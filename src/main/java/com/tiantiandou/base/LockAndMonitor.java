package com.tiantiandou.base;

import java.util.concurrent.locks.ReentrantLock;

public class LockAndMonitor {
    static Integer a;
    static Object b;
    static Thread t = null;
    static ReentrantLock lock = new ReentrantLock();

    public static void main(String[] args) {
        a = new Integer(3);
        b = new Object();
        System.out.println(Integer.toHexString(b.hashCode()));
        Thread t1 = new Thread("Tommy") {
            public void run() {
                synchronized (b) {
                    try {
                        System.out.println("Enter Tommy");
                        b.notify();
                        lock.lock();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("Leave Tommy");
            }
        };
        t = Thread.currentThread();

        synchronized (b) {
            synchronized (a) {
                try {
                    lock.lock();
                    t1.start();
                    b.wait();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
