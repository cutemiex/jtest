package com.doudou.tian.concurrent;


import java.lang.reflect.Field;
import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

public class CollaborativeTransferQueue<E> extends AbstractQueue<E>
implements BlockingQueue<E>, java.io.Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = 3446223889595819134L;


    abstract static class Transferer {
        abstract Object transfer(Object obj, boolean timed, long nanos);
    }

    static final int REQUEST = 0; // request item;
    static final int DATA = 1; // data item,
    static final int FULFILLING = 2; // the item is fullfilled;

    static boolean isFulfilling(int m) {
        return (m & FULFILLING) != 0;
    }

    static class TransferStack extends Transferer {
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
                return this.next == cmp && UNSAFE.compareAndSwapObject(this, nextOffset, cmp, val);
            }

            boolean tryMatch(SNode node) {
                if (match == null && UNSAFE.compareAndSwapObject(this, matchOffset, null, node)) {
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
                    Field f = sun.misc.Unsafe.class.getDeclaredField("theUnsafe"); //Internal reference
                    f.setAccessible(true);
                    UNSAFE = (sun.misc.Unsafe) f.get(null);
                    Class<SNode> clazz = SNode.class;
                    matchOffset = UNSAFE.objectFieldOffset(clazz.getDeclaredField("match"));
                    nextOffset = UNSAFE.objectFieldOffset(clazz.getDeclaredField("next"));
                } catch (Exception e) {
                    throw new Error(e);
                }
            }
        }

        volatile SNode head;

        boolean casHead(SNode h, SNode node) {
            return h == head && UNSAFE.compareAndSwapObject(this, headOffset, h, node);
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
                    } else if (casHead(h, s = snode(s, h, obj, mode | FULFILLING))) {
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
        
        SNode awaitFulfill(SNode s , boolean timed, long nanos){
            long lastTime = timed ? System.nanoTime() : 0;
            Thread w = Thread.currentThread();
           
            for(;;){
                if(w.isInterrupted()){
                    s.tryCancel();
                }
                SNode m = s.match;
                if(m != null){
                    return m;
                }
                if(timed){
                    long now = System.nanoTime();
                    nanos = nanos -(now - lastTime);
                    lastTime = now;
                    if(nanos < 0){
                        s.tryCancel();
                    }
                }else if (s.waiter == null)
                    s.waiter = w; // establish waiter so can park next iter
                else if (!timed)
                    LockSupport.park(this);
                else if (nanos > 0)
                    LockSupport.parkNanos(this, nanos);
            }
        }
        
        void clean(SNode s){
            s.item = null;
            s.waiter = null;
            
            SNode past = s.next;
            while(past != null && past.isCancelled()){
                past = past.next;
            }
            SNode p = head ;
            while(p!= null && p != past && p.isCancelled()){
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
                Field f = sun.misc.Unsafe.class.getDeclaredField("theUnsafe"); //Internal reference
                f.setAccessible(true);
                UNSAFE = (sun.misc.Unsafe) f.get(null);
                Class<TransferStack> k = TransferStack.class;
                headOffset = UNSAFE.objectFieldOffset(k.getDeclaredField("head"));
            } catch (Exception e) {
                throw new Error(e);
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
     * @param fair if true, waiting threads contend in FIFO order for
     *        access; otherwise the order is unspecified.
     */
    public CollaborativeTransferQueue(boolean fair) {
        transferer =  new TransferStack();
    }

    /**
     * Adds the specified element to this queue, waiting if necessary for
     * another thread to receive it.
     *
     * @throws InterruptedException {@inheritDoc}
     * @throws NullPointerException {@inheritDoc}
     */
    public void put(E o) throws InterruptedException {
        if (o == null) throw new NullPointerException();
        if (transferer.transfer(o, false, 0) == null) {
            Thread.interrupted();
            throw new InterruptedException();
        }
    }

    /**
     * Inserts the specified element into this queue, waiting if necessary
     * up to the specified wait time for another thread to receive it.
     *
     * @return <tt>true</tt> if successful, or <tt>false</tt> if the
     *         specified waiting time elapses before a consumer appears.
     * @throws InterruptedException {@inheritDoc}
     * @throws NullPointerException {@inheritDoc}
     */
    public boolean offer(E o, long timeout, TimeUnit unit)
        throws InterruptedException {
        if (o == null) throw new NullPointerException();
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
     * @param e the element to add
     * @return <tt>true</tt> if the element was added to this queue, else
     *         <tt>false</tt>
     * @throws NullPointerException if the specified element is null
     */
    public boolean offer(E e) {
        if (e == null) throw new NullPointerException();
        return transferer.transfer(e, true, 0) != null;
    }

    /**
     * Retrieves and removes the head of this queue, waiting if necessary
     * for another thread to insert it.
     *
     * @return the head of this queue
     * @throws InterruptedException {@inheritDoc}
     */
    public E take() throws InterruptedException {
        Object e = transferer.transfer(null, false, 0);
        if (e != null)
            return (E)e;
        Thread.interrupted();
        throw new InterruptedException();
    }

    /**
     * Retrieves and removes the head of this queue, waiting
     * if necessary up to the specified wait time, for another thread
     * to insert it.
     *
     * @return the head of this queue, or <tt>null</tt> if the
     *         specified waiting time elapses before an element is present.
     * @throws InterruptedException {@inheritDoc}
     */
    public E poll(long timeout, TimeUnit unit) throws InterruptedException {
        Object e = transferer.transfer(null, true, unit.toNanos(timeout));
        if (e != null || !Thread.interrupted())
            return (E)e;
        throw new InterruptedException();
    }

    /**
     * Retrieves and removes the head of this queue, if another thread
     * is currently making an element available.
     *
     * @return the head of this queue, or <tt>null</tt> if no
     *         element is available.
     */
    public E poll() {
        return (E)transferer.transfer(null, true, 0);
    }

    /**
     * Always returns <tt>true</tt>.
     * A <tt>SynchronousQueue</tt> has no internal capacity.
     *
     * @return <tt>true</tt>
     */
    public boolean isEmpty() {
        return true;
    }

    /**
     * Always returns zero.
     * A <tt>SynchronousQueue</tt> has no internal capacity.
     *
     * @return zero.
     */
    public int size() {
        return 0;
    }

    /**
     * Always returns zero.
     * A <tt>SynchronousQueue</tt> has no internal capacity.
     *
     * @return zero.
     */
    public int remainingCapacity() {
        return 0;
    }

    /**
     * Does nothing.
     * A <tt>SynchronousQueue</tt> has no internal capacity.
     */
    public void clear() {
    }

    /**
     * Always returns <tt>false</tt>.
     * A <tt>SynchronousQueue</tt> has no internal capacity.
     *
     * @param o the element
     * @return <tt>false</tt>
     */
    public boolean contains(Object o) {
        return false;
    }

    /**
     * Always returns <tt>false</tt>.
     * A <tt>SynchronousQueue</tt> has no internal capacity.
     *
     * @param o the element to remove
     * @return <tt>false</tt>
     */
    public boolean remove(Object o) {
        return false;
    }

    /**
     * Returns <tt>false</tt> unless the given collection is empty.
     * A <tt>SynchronousQueue</tt> has no internal capacity.
     *
     * @param c the collection
     * @return <tt>false</tt> unless given collection is empty
     */
    public boolean containsAll(Collection<?> c) {
        return c.isEmpty();
    }

    /**
     * Always returns <tt>false</tt>.
     * A <tt>SynchronousQueue</tt> has no internal capacity.
     *
     * @param c the collection
     * @return <tt>false</tt>
     */
    public boolean removeAll(Collection<?> c) {
        return false;
    }

    /**
     * Always returns <tt>false</tt>.
     * A <tt>SynchronousQueue</tt> has no internal capacity.
     *
     * @param c the collection
     * @return <tt>false</tt>
     */
    public boolean retainAll(Collection<?> c) {
        return false;
    }

    /**
     * Always returns <tt>null</tt>.
     * A <tt>SynchronousQueue</tt> does not return elements
     * unless actively waited on.
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
     * @return a zero-length array
     */
    public Object[] toArray() {
        return new Object[0];
    }

    /**
     * Sets the zeroeth element of the specified array to <tt>null</tt>
     * (if the array has non-zero length) and returns it.
     *
     * @param a the array
     * @return the specified array
     * @throws NullPointerException if the specified array is null
     */
    public <T> T[] toArray(T[] a) {
        if (a.length > 0)
            a[0] = null;
        return a;
    }

    /**
     * @throws UnsupportedOperationException {@inheritDoc}
     * @throws ClassCastException            {@inheritDoc}
     * @throws NullPointerException          {@inheritDoc}
     * @throws IllegalArgumentException      {@inheritDoc}
     */
    public int drainTo(Collection<? super E> c) {
        if (c == null)
            throw new NullPointerException();
        if (c == this)
            throw new IllegalArgumentException();
        int n = 0;
        E e;
        while ( (e = poll()) != null) {
            c.add(e);
            ++n;
        }
        return n;
    }

    /**
     * @throws UnsupportedOperationException {@inheritDoc}
     * @throws ClassCastException            {@inheritDoc}
     * @throws NullPointerException          {@inheritDoc}
     * @throws IllegalArgumentException      {@inheritDoc}
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
     * SynchronousQueue, we declare some unused classes and fields
     * that exist solely to enable serializability across versions.
     * These fields are never used, so are initialized only if this
     * object is ever serialized or deserialized.
     */

    static class WaitQueue implements java.io.Serializable { }
    static class LifoWaitQueue extends WaitQueue {
        private static final long serialVersionUID = -3633113410248163686L;
    }
    static class FifoWaitQueue extends WaitQueue {
        private static final long serialVersionUID = -3623113410248163686L;
    }

    
    // Unsafe mechanics
    static long objectFieldOffset(sun.misc.Unsafe UNSAFE,
                                  String field, Class<?> klazz) {
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
