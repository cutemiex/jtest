package com.tiantiandou.concurrent.lock;

import java.util.AbstractMap;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

public class IndexedSkipList<K, V> extends AbstractMap<K, V> implements ConcurrentMap<K, V> {

    public V putIfAbsent(K key, V value) {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean remove(Object key, Object value) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean replace(K key, V oldValue, V newValue) {
        // TODO Auto-generated method stub
        return false;
    }

    public V replace(K key, V value) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<java.util.Map.Entry<K, V>> entrySet() {
        // TODO Auto-generated method stub
        return null;
    }
}
