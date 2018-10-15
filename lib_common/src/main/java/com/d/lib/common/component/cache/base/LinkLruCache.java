package com.d.lib.common.component.cache.base;

import android.support.annotation.NonNull;

/**
 * LinkLruCache
 * Created by D on 2018/10/15.
 */
public class LinkLruCache<K, V> {
    private final static long VALIDATE_TIME = 2 * 60 * 60 * 1000;

    /**
     * Current number of elements
     */
    private int count = 0;
    /**
     * Head of linked list.
     * Invariant: head.item == null
     */
    private transient Node<Item<K, V>> head;

    /**
     * Tail of linked list.
     * Invariant: last.next == null
     */
    private transient Node<Item<K, V>> last;

    private int maxCount = 12;
    private long validateTime = VALIDATE_TIME;
    private final Object lock = new Object();

    public LinkLruCache() {
        last = head = new Node<>(null);
    }

    public void setCount(int count) {
        this.maxCount = count;
    }

    public void setValidate(long time) {
        this.validateTime = time;
        this.clear();
    }

    public void put(K key, V value) {
        if (key == null) {
            return;
        }

        Item<K, V> e = new Item<>(key, value, System.currentTimeMillis());
        Node<Item<K, V>> node = new Node<>(e);
        synchronized (lock) {
            Item<K, V> contain = containsKey(e.key);
            if (contain != null) {
                contain.value = e.value;
                return;
            }
            if (count > 0 && count >= maxCount) {
                dequeue();
            }
            enqueue(node);
            count++;
        }
    }

    private void enqueue(Node<Item<K, V>> node) {
        last = last.next = node;
    }

    @SuppressWarnings("UnusedReturnValue")
    private Item<K, V> dequeue() {
        Node<Item<K, V>> h = head;
        Node<Item<K, V>> first = h.next;
        h.next = h; // help GC
        head = first;
        Item<K, V> x = first.item;
        first.item = null;
        return x;
    }

    public V get(K key) {
        synchronized (lock) {
            if (!isValidate(key)) {
                dequeue();
                return null;
            }
            Item<K, V> contain = containsKey(key);
            return contain != null ? contain.value : null;
        }
    }

    @SuppressWarnings("unused")
    public boolean contains(K key) {
        synchronized (lock) {
            return containsKey(key) != null;
        }
    }

    private Item<K, V> containsKey(K key) {
        for (Node<Item<K, V>> p = head.next; p != null; p = p.next) {
            if (p.item != null && key.equals(p.item.key)) {
                return p.item;
            }
        }
        return null;
    }

    private boolean isValidate(K key) {
        Item<K, V> contain = containsKey(key);
        return contain == null || System.currentTimeMillis() - contain.time <= validateTime;
    }

    public boolean remove(K key) {
        if (key == null) {
            return false;
        }

        synchronized (lock) {
            for (Node<Item<K, V>> trail = head, p = trail.next;
                 p != null;
                 trail = p, p = p.next) {
                if (p.item != null && key.equals(p.item.key)) {
                    unlink(p, trail);
                    return true;
                }
            }
        }
        return false;
    }

    private void unlink(@NonNull Node<Item<K, V>> p, @NonNull Node<Item<K, V>> trail) {
        p.item = null;
        trail.next = p.next;
        if (last == p) {
            last = trail;
        }
    }

    public void clear() {
        synchronized (lock) {
            for (Node<Item<K, V>> p, h = head; (p = h.next) != null; h = p) {
                h.next = h;
                p.item = null;
            }
            head = last;
            count = 0;
        }
    }

    public int size() {
        synchronized (lock) {
            return count;
        }
    }

    @SuppressWarnings("unused")
    public Object[] toArray() {
        Object[] a = new Object[count];
        int k = 0;
        for (Node<Item<K, V>> p = head.next; p != null; p = p.next) {
            a[k++] = p.item;
        }
        return a;
    }

    /**
     * Item class.
     */
    static class Item<Key, Value> {
        Key key;
        Value value;
        long time;

        Item(Key key, Value value, long time) {
            this.key = key;
            this.value = value;
            this.time = time;
        }
    }

    /**
     * Linked list node class.
     */
    static class Node<E> {
        E item;

        /**
         * One of:
         * - the real successor Node
         * - this Node, meaning the successor is head.next
         * - null, meaning there is no successor (this is the last node)
         */
        Node<E> next;

        Node(E x) {
            item = x;
        }
    }
}
