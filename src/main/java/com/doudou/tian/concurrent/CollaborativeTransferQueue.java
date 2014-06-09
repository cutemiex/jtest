package com.doudou.tian.concurrent;

import java.lang.reflect.Field;
import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

public class CollaborativeTransferQueue<E> extends AbstractQueue<E> implements
		BlockingQueue<E>, java.io.Serializable {

	/**
     * 
     */
	private static final long serialVersionUID = 3446223889595819134L;

	abstract static class Transferer {
		abstract Object transfer(Object obj, boolean timed, long nanos);
	}
	
    /** The number of CPUs, for spin control */
    static final int NCPUS = Runtime.getRuntime().availableProcessors();

    /**
     * The number of times to spin before blocking in timed waits.
     * The value is empirically derived -- it works well across a
     * variety of processors and OSes. Empirically, the best value
     * seems not to vary with number of CPUs (beyond 2) so is just
     * a constant.
     */
    static final int maxTimedSpins = (NCPUS < 2) ? 0 : 32;

    /**
     * The number of times to spin before blocking in untimed waits.
     * This is greater than timed value because untimed waits spin
     * faster since they don't need to check times on each spin.
     */
    static final int maxUntimedSpins = maxTimedSpins * 16;

    /**
     * The number of nanoseconds for which it is faster to spin
     * rather than to use timed park. A rough estimate suffices.
     */
    static final long spinForTimeoutThreshold = 1000L;

    
	static class TransferStack extends Transferer {
		static final int REQUEST = 0; // request item;
		static final int DATA = 1; // data item,
		static final int FULFILLING = 2; // the item is fullfilled;

		static boolean isFulfilling(int m) {
			return (m & FULFILLING) != 0;
		}

		static final class SNode {
			volatile SNode next;
			volatile SNode match;
			Object item;
			volatile Thread waiter;
			int mode;

			SNode(Object item) {
				this.item = item;
			}

			boolean casNext(SNode cmp, SNode val) {
				return this.next == cmp
						&& UNSAFE.compareAndSwapObject(this, nextOffset, cmp,
								val);
			}

			boolean tryMatch(SNode node) {
				if (match == null
						&& UNSAFE.compareAndSwapObject(this, matchOffset, null,
								node)) {
					Thread w = waiter;
					if (w != null) { // waiters need at most one unpark
						waiter = null;
						LockSupport.unpark(w);
					}
					return true;
				} else {
					return match == node;
				}

			}

			void tryCancel() {
				UNSAFE.compareAndSwapObject(this, matchOffset, null, this);
			}

			boolean isCancelled() {
				return match == this;
			}

			private static final sun.misc.Unsafe UNSAFE;
			private static final long matchOffset;
			private static final long nextOffset;

			static {
				try {
					Field f = sun.misc.Unsafe.class
							.getDeclaredField("theUnsafe"); // Internal
															// reference
					f.setAccessible(true);
					UNSAFE = (sun.misc.Unsafe) f.get(null);
					Class<SNode> clazz = SNode.class;
					matchOffset = UNSAFE.objectFieldOffset(clazz
							.getDeclaredField("match"));
					nextOffset = UNSAFE.objectFieldOffset(clazz
							.getDeclaredField("next"));
				} catch (Exception e) {
					throw new Error(e);
				}
			}
		}

		volatile SNode head;

		boolean casHead(SNode h, SNode node) {
			return h == head
					&& UNSAFE.compareAndSwapObject(this, headOffset, h, node);
		}

		static SNode snode(SNode node, SNode next, Object item, int mode) {
			if (node == null) {
				node = new SNode(item);
			}
			node.next = next;
			node.mode = mode;
			return node;
		}

		Object transfer(Object obj, boolean timed, long nanos) {
			SNode s = null;
			int mode = (obj == null) ? REQUEST : DATA;
			for (;;) {
				SNode h = head;
				if (h == null || h.mode == mode) {
					if (timed && nanos <= 0) {
						if (h != null && h.isCancelled()) {
							casHead(h, h.next);
						} else {
							return null;
						}
					} else if (casHead(h, s = snode(s, h, obj, mode))) {
						SNode m = awaitFulfill(s, timed, nanos);
						if (m == s) {
							clean(s);
							return null;
						} else {
							return (mode == REQUEST) ? m.item : s.item;
						}
					}
				} else if (!isFulfilling(h.mode)) {
					if (h.isCancelled()) {
						casHead(h, h.next);
					} else if (casHead(h,
							s = snode(s, h, obj, mode | FULFILLING))) {
						for (;;) {
							SNode m = s.next;
							if (m == null) {
								casHead(s, null);
								s = null;
								break;
							}
							SNode mn = m.next;
							if (m.tryMatch(s)) {
								casHead(s, mn);
								return (mode == REQUEST) ? m.item : s.item;
							} else {
								s.casNext(m, mn);
							}
						}
					}

				} else {
					SNode m = h.next;
					if (m == null) {
						casHead(h, null);
					}
					SNode mn = m.next;
					if (m.tryMatch(h)) {
						casHead(h, mn);
					} else {
						h.casNext(m, mn);
					}
				}
			}
		}

		SNode awaitFulfill(SNode s, boolean timed, long nanos) {
			long lastTime = timed ? System.nanoTime() : 0;
            Thread w = Thread.currentThread();
            for (;;) {
                if (w.isInterrupted())
                    s.tryCancel();
                SNode m = s.match;
                if (m != null)
                    return m;
                if (timed) {
                    long now = System.nanoTime();
                    nanos -= now - lastTime;
                    lastTime = now;
                    if (nanos <= 0) {
                        s.tryCancel();
                        continue;
                    }
                }
                if (s.waiter == null)
                    s.waiter = w; // establish waiter so can park next iter
                else if (!timed)
                    LockSupport.park(this);
                else if (nanos > 0)
                    LockSupport.parkNanos(this, nanos);
            }
		}

		boolean shouldSpin(SNode s) {
//			SNode h = head;
//			return (h == s || h == null || isFulfilling(h.mode));
			return false;
		}

		void clean(SNode s) {
			s.item = null;
			s.waiter = null;

			SNode past = s.next;
			while (past != null && past.isCancelled()) {
				past = past.next;
			}
			SNode p = head;
			while (p != null && p != past && p.isCancelled()) {
				casHead(p, p.next);
				p = head;
			}

			while (p != null && p != past) {
				SNode n = p.next;
				if (n != null && n.isCancelled())
					p.casNext(n, n.next);
				else
					p = n;
			}
		}

		// Unsafe mechanics
		private static final sun.misc.Unsafe UNSAFE;
		private static final long headOffset;
		static {
			try {
				Field f = sun.misc.Unsafe.class.getDeclaredField("theUnsafe"); // Internal
																				// reference
				f.setAccessible(true);
				UNSAFE = (sun.misc.Unsafe) f.get(null);
				Class<TransferStack> k = TransferStack.class;
				headOffset = UNSAFE.objectFieldOffset(k
						.getDeclaredField("head"));
			} catch (Exception e) {
				throw new Error(e);
			}
		}
	}

	static class TransferQueue extends Transferer {
		static final class QNode {
			volatile QNode next;
			volatile Object item;
			volatile Thread waiter;
			boolean isData;

			private static final long nextOffset;

			private static final long itemOffset;

			private static final sun.misc.Unsafe UNSAFE;

			static {
				try {
					Field f = sun.misc.Unsafe.class
							.getDeclaredField("theUnsafe");
					f.setAccessible(true);
					UNSAFE = (sun.misc.Unsafe) f.get(null);
					nextOffset = UNSAFE.objectFieldOffset(QNode.class
							.getDeclaredField("next"));
					itemOffset = UNSAFE.objectFieldOffset(QNode.class
							.getDeclaredField("item"));
				} catch (Exception e) {
					throw new Error(e);
				}
			}

			QNode(Object item, boolean isData) {
				this.item = item;
				this.isData = isData;
			}

			boolean casNext(QNode cmp, QNode val) {
				return next == cmp
						&& UNSAFE.compareAndSwapObject(this, nextOffset, cmp,
								val);
			}

			boolean casItem(Object cmp, Object val) {
				return item == cmp
						&& UNSAFE.compareAndSwapObject(this, itemOffset, cmp,
								val);
			}

			/**
			 * Tries to cancel by CAS'ing ref to this as item.
			 */
			void tryCancel(Object cmp) {
				UNSAFE.compareAndSwapObject(this, itemOffset, cmp, this);
			}

			boolean isCancelled() {
				return item == this;
			}

			/**
			 * Returns true if this node is known to be off the queue because
			 * its next pointer has been forgotten due to an advanceHead
			 * operation.
			 */
			boolean isOffList() {
				return next == this;
			}
		}

		private volatile QNode head;
		private volatile QNode tail;
		volatile QNode cleanMe;

		private static final sun.misc.Unsafe UNSAFE;
		private static final long headOffset;
		private static final long tailOffset;
		private static final long cleanMeOffset;
		static {
			try {
				Field field = sun.misc.Unsafe.class
						.getDeclaredField("theUnsafe");
				field.setAccessible(true);
				UNSAFE = (sun.misc.Unsafe) field.get(null);
				Class k = TransferQueue.class;
				headOffset = UNSAFE.objectFieldOffset(k
						.getDeclaredField("head"));
				tailOffset = UNSAFE.objectFieldOffset(k
						.getDeclaredField("tail"));
				cleanMeOffset = UNSAFE.objectFieldOffset(k
						.getDeclaredField("cleanMe"));
			} catch (Exception e) {
				throw new Error(e);
			}
		}

		void advanceHead(QNode h, QNode nh) {
			if (h == head
					&& UNSAFE.compareAndSwapObject(this, headOffset, h, nh))
				h.next = h; // forget old next
		}

		/**
		 * Tries to cas nt as new tail.
		 */
		void advanceTail(QNode t, QNode nt) {
			if (tail == t)
				UNSAFE.compareAndSwapObject(this, tailOffset, t, nt);
		}

		/**
		 * Tries to CAS cleanMe slot.
		 */
		boolean casCleanMe(QNode cmp, QNode val) {
			return cleanMe == cmp
					&& UNSAFE.compareAndSwapObject(this, cleanMeOffset, cmp,
							val);
		}

		public TransferQueue() {
			head = new QNode(null, false);
			tail = head;
		}

		@Override
		Object transfer(Object obj, boolean timed, long nanos) {
			QNode s = null;
			boolean isData = (obj != null);

			for (;;) {
				QNode h = head;
				QNode t = tail;
				if (t == null || h == null) // saw uninitialized value, spin
					continue;
				if (h == t || t.isData == isData) {
					QNode tn = t.next;
					if (t != tail) {
						continue;
					}
					if (tn != null) {
						advanceTail(t, tn);
						continue;
					}
					if (timed && nanos <= 0) { // can't wait
						return null;
					}
					if (s == null) {
						s = new QNode(obj, isData);
					}
					if (!t.casNext(null, s)) { // failed to link in
						continue;
					}
					advanceTail(t, s);
					Object x = awaitFulfill(s, obj, timed, nanos);
					if (x == s) { // wait was cancelled
						clean(t, s);
						return null;
					}

					if (!s.isOffList()) { // not already unlinked
						advanceHead(t, s); // unlink if head
						if (x != null) // and forget fields
							s.item = s;
						s.waiter = null;
					}
					return (x != null) ? x : obj;
				} else {
					QNode m = h.next; // node to fulfill
					if (t != tail || m == null || h != head)
						continue; // inconsistent read

					Object x = m.item;
					if (isData == (x != null) || // m already fulfilled
							x == m || // m cancelled
							!m.casItem(x, obj)) { // lost CAS
						advanceHead(h, m); // dequeue and retry
						continue;
					}

					advanceHead(h, m); // successfully fulfilled
					LockSupport.unpark(m.waiter);
					return (x != null) ? x : obj;
				}
			}
		}

		Object awaitFulfill(QNode s, Object e, boolean timed, long nanos) {
			/* Same idea as TransferStack.awaitFulfill */
			long lastTime = timed ? System.nanoTime() : 0;
			Thread w = Thread.currentThread();

			for (;;) {
				if (w.isInterrupted())
					s.tryCancel(e);
				Object x = s.item;
				if (x != e)
					return x;
				if (timed) {
					long now = System.nanoTime();
					nanos -= now - lastTime;
					lastTime = now;
					if (nanos <= 0) {
						s.tryCancel(e);
						continue;
					}
				} 
				if (s.waiter == null)
					s.waiter = w;
				else if (!timed)
					LockSupport.park(this);
				else if (nanos > 0)
					LockSupport.parkNanos(this, nanos);
			}
		}

		void clean(QNode pred, QNode s) {
			s.waiter = null; // forget thread
			/*
			 * At any given time, exactly one node on list cannot be deleted --
			 * the last inserted node. To accommodate this, if we cannot delete
			 * s, we save its predecessor as "cleanMe", deleting the previously
			 * saved version first. At least one of node s or the node
			 * previously saved can always be deleted, so this always
			 * terminates.
			 */
			while (pred.next == s) { // Return early if already unlinked
				QNode h = head;
				QNode hn = h.next; // Absorb cancelled first node as head
				if (hn != null && hn.isCancelled()) {
					advanceHead(h, hn);
					continue;
				}
				QNode t = tail; // Ensure consistent read for tail
				if (t == h)
					return;
				QNode tn = t.next;
				if (t != tail)
					continue;
				if (tn != null) {
					advanceTail(t, tn);
					continue;
				}
				if (s != t) { // If not tail, try to unsplice
					QNode sn = s.next;
					if (sn == s || pred.casNext(s, sn))
						return;
				}
				QNode dp = cleanMe;
				if (dp != null) { // Try unlinking previous cancelled node
					QNode d = dp.next;
					QNode dn;
					if (d == null || // d is gone or
							d == dp || // d is off list or
							!d.isCancelled() || // d not cancelled or
							(d != t && // d not tail and
									(dn = d.next) != null && // has successor
									dn != d && // that is on list
							dp.casNext(d, dn))) // d unspliced
						casCleanMe(dp, null);
					if (dp == pred)
						return; // s is already saved node
				} else if (casCleanMe(null, pred))
					return; // Postpone cleaning s
			}
		}

	}

	private transient volatile Transferer transferer;

	/**
	 * Creates a <tt>SynchronousQueue</tt> with nonfair access policy.
	 */
	public CollaborativeTransferQueue() {
		this(false);
	}

	/**
	 * Creates a <tt>SynchronousQueue</tt> with the specified fairness policy.
	 * 
	 * @param fair
	 *            if true, waiting threads contend in FIFO order for access;
	 *            otherwise the order is unspecified.
	 */
	public CollaborativeTransferQueue(boolean fair) {
		transferer = new TransferStack();
	}

	/**
	 * Adds the specified element to this queue, waiting if necessary for
	 * another thread to receive it.
	 * 
	 * @throws InterruptedException
	 *             {@inheritDoc}
	 * @throws NullPointerException
	 *             {@inheritDoc}
	 */
	public void put(E o) throws InterruptedException {
		if (o == null)
			throw new NullPointerException();
		if (transferer.transfer(o, false, 0) == null) {
			Thread.interrupted();
			throw new InterruptedException();
		}
	}

	/**
	 * Inserts the specified element into this queue, waiting if necessary up to
	 * the specified wait time for another thread to receive it.
	 * 
	 * @return <tt>true</tt> if successful, or <tt>false</tt> if the specified
	 *         waiting time elapses before a consumer appears.
	 * @throws InterruptedException
	 *             {@inheritDoc}
	 * @throws NullPointerException
	 *             {@inheritDoc}
	 */
	public boolean offer(E o, long timeout, TimeUnit unit)
			throws InterruptedException {
		if (o == null)
			throw new NullPointerException();
		if (transferer.transfer(o, true, unit.toNanos(timeout)) != null)
			return true;
		if (!Thread.interrupted())
			return false;
		throw new InterruptedException();
	}

	/**
	 * Inserts the specified element into this queue, if another thread is
	 * waiting to receive it.
	 * 
	 * @param e
	 *            the element to add
	 * @return <tt>true</tt> if the element was added to this queue, else
	 *         <tt>false</tt>
	 * @throws NullPointerException
	 *             if the specified element is null
	 */
	public boolean offer(E e) {
		if (e == null)
			throw new NullPointerException();
		return transferer.transfer(e, true, 0) != null;
	}

	/**
	 * Retrieves and removes the head of this queue, waiting if necessary for
	 * another thread to insert it.
	 * 
	 * @return the head of this queue
	 * @throws InterruptedException
	 *             {@inheritDoc}
	 */
	public E take() throws InterruptedException {
		Object e = transferer.transfer(null, false, 0);
		if (e != null)
			return (E) e;
		Thread.interrupted();
		throw new InterruptedException();
	}

	/**
	 * Retrieves and removes the head of this queue, waiting if necessary up to
	 * the specified wait time, for another thread to insert it.
	 * 
	 * @return the head of this queue, or <tt>null</tt> if the specified waiting
	 *         time elapses before an element is present.
	 * @throws InterruptedException
	 *             {@inheritDoc}
	 */
	public E poll(long timeout, TimeUnit unit) throws InterruptedException {
		Object e = transferer.transfer(null, true, unit.toNanos(timeout));
		if (e != null || !Thread.interrupted())
			return (E) e;
		throw new InterruptedException();
	}

	/**
	 * Retrieves and removes the head of this queue, if another thread is
	 * currently making an element available.
	 * 
	 * @return the head of this queue, or <tt>null</tt> if no element is
	 *         available.
	 */
	public E poll() {
		return (E) transferer.transfer(null, true, 0);
	}

	/**
	 * Always returns <tt>true</tt>. A <tt>SynchronousQueue</tt> has no internal
	 * capacity.
	 * 
	 * @return <tt>true</tt>
	 */
	public boolean isEmpty() {
		return true;
	}

	/**
	 * Always returns zero. A <tt>SynchronousQueue</tt> has no internal
	 * capacity.
	 * 
	 * @return zero.
	 */
	public int size() {
		return 0;
	}

	/**
	 * Always returns zero. A <tt>SynchronousQueue</tt> has no internal
	 * capacity.
	 * 
	 * @return zero.
	 */
	public int remainingCapacity() {
		return 0;
	}

	/**
	 * Does nothing. A <tt>SynchronousQueue</tt> has no internal capacity.
	 */
	public void clear() {
	}

	/**
	 * Always returns <tt>false</tt>. A <tt>SynchronousQueue</tt> has no
	 * internal capacity.
	 * 
	 * @param o
	 *            the element
	 * @return <tt>false</tt>
	 */
	public boolean contains(Object o) {
		return false;
	}

	/**
	 * Always returns <tt>false</tt>. A <tt>SynchronousQueue</tt> has no
	 * internal capacity.
	 * 
	 * @param o
	 *            the element to remove
	 * @return <tt>false</tt>
	 */
	public boolean remove(Object o) {
		return false;
	}

	/**
	 * Returns <tt>false</tt> unless the given collection is empty. A
	 * <tt>SynchronousQueue</tt> has no internal capacity.
	 * 
	 * @param c
	 *            the collection
	 * @return <tt>false</tt> unless given collection is empty
	 */
	public boolean containsAll(Collection<?> c) {
		return c.isEmpty();
	}

	/**
	 * Always returns <tt>false</tt>. A <tt>SynchronousQueue</tt> has no
	 * internal capacity.
	 * 
	 * @param c
	 *            the collection
	 * @return <tt>false</tt>
	 */
	public boolean removeAll(Collection<?> c) {
		return false;
	}

	/**
	 * Always returns <tt>false</tt>. A <tt>SynchronousQueue</tt> has no
	 * internal capacity.
	 * 
	 * @param c
	 *            the collection
	 * @return <tt>false</tt>
	 */
	public boolean retainAll(Collection<?> c) {
		return false;
	}

	/**
	 * Always returns <tt>null</tt>. A <tt>SynchronousQueue</tt> does not return
	 * elements unless actively waited on.
	 * 
	 * @return <tt>null</tt>
	 */
	public E peek() {
		return null;
	}

	/**
	 * Returns an empty iterator in which <tt>hasNext</tt> always returns
	 * <tt>false</tt>.
	 * 
	 * @return an empty iterator
	 */
	public Iterator<E> iterator() {
		return Collections.emptyIterator();
	}

	/**
	 * Returns a zero-length array.
	 * 
	 * @return a zero-length array
	 */
	public Object[] toArray() {
		return new Object[0];
	}

	/**
	 * Sets the zeroeth element of the specified array to <tt>null</tt> (if the
	 * array has non-zero length) and returns it.
	 * 
	 * @param a
	 *            the array
	 * @return the specified array
	 * @throws NullPointerException
	 *             if the specified array is null
	 */
	public <T> T[] toArray(T[] a) {
		if (a.length > 0)
			a[0] = null;
		return a;
	}

	/**
	 * @throws UnsupportedOperationException
	 *             {@inheritDoc}
	 * @throws ClassCastException
	 *             {@inheritDoc}
	 * @throws NullPointerException
	 *             {@inheritDoc}
	 * @throws IllegalArgumentException
	 *             {@inheritDoc}
	 */
	public int drainTo(Collection<? super E> c) {
		if (c == null)
			throw new NullPointerException();
		if (c == this)
			throw new IllegalArgumentException();
		int n = 0;
		E e;
		while ((e = poll()) != null) {
			c.add(e);
			++n;
		}
		return n;
	}

	/**
	 * @throws UnsupportedOperationException
	 *             {@inheritDoc}
	 * @throws ClassCastException
	 *             {@inheritDoc}
	 * @throws NullPointerException
	 *             {@inheritDoc}
	 * @throws IllegalArgumentException
	 *             {@inheritDoc}
	 */
	public int drainTo(Collection<? super E> c, int maxElements) {
		if (c == null)
			throw new NullPointerException();
		if (c == this)
			throw new IllegalArgumentException();
		int n = 0;
		E e;
		while (n < maxElements && (e = poll()) != null) {
			c.add(e);
			++n;
		}
		return n;
	}

	/*
	 * To cope with serialization strategy in the 1.5 version of
	 * SynchronousQueue, we declare some unused classes and fields that exist
	 * solely to enable serializability across versions. These fields are never
	 * used, so are initialized only if this object is ever serialized or
	 * deserialized.
	 */

	static class WaitQueue implements java.io.Serializable {
	}

	static class LifoWaitQueue extends WaitQueue {
		private static final long serialVersionUID = -3633113410248163686L;
	}

	static class FifoWaitQueue extends WaitQueue {
		private static final long serialVersionUID = -3623113410248163686L;
	}

	// Unsafe mechanics
	static long objectFieldOffset(sun.misc.Unsafe UNSAFE, String field,
			Class<?> klazz) {
		try {
			return UNSAFE.objectFieldOffset(klazz.getDeclaredField(field));
		} catch (NoSuchFieldException e) {
			// Convert Exception to corresponding Error
			NoSuchFieldError error = new NoSuchFieldError(field);
			error.initCause(e);
			throw error;
		}
	}

}
