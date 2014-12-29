package com.doudou.tian.concurrent.lock;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

public class IndexedSkipList<K, V> extends AbstractMap<K, V> implements ConcurrentMap<K, V> {
    
    @Override
    public V putIfAbsent(K key, V value) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public boolean remove(Object key, Object value) {
	// TODO Auto-generated method stub
	return false;
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
	// TODO Auto-generated method stub
	return false;
    }

    @Override
    public V replace(K key, V value) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public Set<java.util.Map.Entry<K, V>> entrySet() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public int size() {
	// TODO Auto-generated method stub
	return super.size();
    }

    @Override
    public boolean isEmpty() {
	// TODO Auto-generated method stub
	return super.isEmpty();
    }

    @Override
    public boolean containsValue(Object value) {
	// TODO Auto-generated method stub
	return super.containsValue(value);
    }

    @Override
    public boolean containsKey(Object key) {
	// TODO Auto-generated method stub
	return super.containsKey(key);
    }

    @Override
    public V get(Object key) {
	// TODO Auto-generated method stub
	return super.get(key);
    }

    @Override
    public V put(K key, V value) {
	// TODO Auto-generated method stub
	return super.put(key, value);
    }

    @Override
    public V remove(Object key) {
	// TODO Auto-generated method stub
	return super.remove(key);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
	// TODO Auto-generated method stub
	super.putAll(m);
    }

    @Override
    public void clear() {
	// TODO Auto-generated method stub
	super.clear();
    }

    @Override
    public Set<K> keySet() {
	// TODO Auto-generated method stub
	return super.keySet();
    }

    @Override
    public Collection<V> values() {
	// TODO Auto-generated method stub
	return super.values();
    }

    @Override
    public boolean equals(Object o) {
	// TODO Auto-generated method stub
	return super.equals(o);
    }

    @Override
    public int hashCode() {
	// TODO Auto-generated method stub
	return super.hashCode();
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
	// TODO Auto-generated method stub
	return super.clone();
    }

    private int height;
    
    private static final int MAX_HEIGHT = 128;
    
    private ArrayList<SNode> heads = new ArrayList<>(MAX_HEIGHT);
    
    static class SNode<T> {
	private SNode<T> right;
	private SNode<T> down;
	private T        data;
	
	SNode(T data, SNode<T> right, SNode<T> down){
	    this.data = data;
	    this.right = right;
	    this.down = down;
	}

	SNode<T> getRight() {
	    return right;
	}

	void setRight(SNode<T> right) {
	    this.right = right;
	}

	SNode<T> getDown() {
	    return down;
	}

	void setDown(SNode<T> down) {
	    this.down = down;
	}

	T getData() {
	    return data;
	}

        void setData(T data) {
	    this.data = data;
	}
    }
}
