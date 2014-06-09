package com.doudou.tian.concurrent.lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

public class SyncCylicBarrier {

	private final Sync sync;

	public SyncCylicBarrier(int cylics) {
		sync = new Sync(cylics);
	}

	public void await() {
		sync.releaseShared(1);
		sync.acquireShared(0);
	}

	public void await(long timeout, TimeUnit unit) throws TimeoutException, InterruptedException {
		sync.releaseShared(1);
		boolean istimeout = false;
		boolean t = false;
		try {
			istimeout = !sync.tryAcquireSharedNanos(0, unit.toNanos(timeout));
		} catch (InterruptedException e) {
			t = true;
		}
		if (t || istimeout) {
			if(!sync.decreaseCylics()){
				await();
			}else{
				if(istimeout){
					throw new TimeoutException();
				}else{
					throw new InterruptedException();
				}
			}
        }
        
	}

	class Sync extends AbstractQueuedSynchronizer {
		final int cylics;

		Sync(int cylics) {
			this.cylics = cylics;
			this.setState(0);
		}

		int getAvailableCylics() {
			return this.getState();
		}

		int getAimCylics() {
			return this.cylics;
		}

		@Override
		protected int tryAcquireShared(int arg) {
			if (this.getState() >= cylics) {
				return 1;
			} else {
				return -1;
			}

		}

		@Override
		protected boolean tryReleaseShared(int arg) {
			for (;;) {
				int state = this.getAvailableCylics();
				int next = state + 1;
				if (compareAndSetState(state, next)) {
					return next == cylics;
				}
			}
		}

		boolean decreaseCylics() {
            for(;;){
                int state = this.getAvailableCylics();
                int cylics = this.getAimCylics();
                if(state >= cylics){
            		return false;
            	}
            	if(compareAndSetState(state, state -1)){
            		return true;
            	}
            }
		}

	}
}
