package com.d.music.component.cache.base;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by D on 2017/10/19.
 */
public class LruCache<K, V> {
    private int count = 12;
    private LinkedHashMap<K, V> map;

    public LruCache() {
        map = new LinkedHashMap<>();
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void put(K key, V value) {
        calculateSize(key);
        if (count > 0) {
            map.put(key, value);
        }
    }

    public V get(K key) {
        return map.get(key);
    }

    public boolean containsKey(K key) {
        return map.containsKey(key);
    }

    public void remove(K key) {
        map.remove(key);
    }

    public void clear() {
        map.clear();
    }

    private void calculateSize(K key) {
        if (map.size() >= count && !map.containsKey(key)) {
            Iterator ite = map.entrySet().iterator();
            if (ite.hasNext()) {
                Map.Entry<K, V> entry = (Map.Entry<K, V>) ite.next();
                map.remove(entry.getKey());
            }
        }
    }
}
