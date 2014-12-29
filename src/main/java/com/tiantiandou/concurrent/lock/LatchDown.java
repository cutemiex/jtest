package com.tiantiandou.concurrent.lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

public class LatchDown {

	private final Sync sync;

	private static final class Sync extends AbstractQueuedSynchronizer {
        public Sync(int count){
        	this.setState(count);
        }

		@Override
		protected int tryAcquireShared(int arg) {
			return (getState()==0) ? 1 : -1;
		}

		@Override
		protected boolean tryReleaseShared(int arg) {
			for(;;){
				int state = getState();
				int next = state -1;
				if(state ==  0){
					return false;
				}
				if(this.compareAndSetState(state, next)){
					return next == 0;
				}
			}
		}
        
        
	}

	public LatchDown(int count) {
		if (count < 0)
			throw new IllegalArgumentException("count < 0");
		this.sync = new Sync(count);
	}

	public void await() throws InterruptedException {
		sync.acquireSharedInterruptibly(1);
	}


	public boolean await(long timeout, TimeUnit unit)
			throws InterruptedException {
		return sync.tryAcquireSharedNanos(1, unit.toNanos(timeout));
	}

	public void countDown() {
		sync.releaseShared(1);
	}

}
