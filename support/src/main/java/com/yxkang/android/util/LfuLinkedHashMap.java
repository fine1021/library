package com.yxkang.android.util;


import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * LfuLinkedHashMap
 */
public class LfuLinkedHashMap<K, V> extends HashMap<K, V> {


    /**
     * A dummy entry in the circular linked list of entries in the map.
     * The first real entry is header.nxt, and the last is header.prv.
     * If the map is empty, header.next == header && header.prev == header.
     */
    transient LfuLinkedEntry<K, V> header;

    /**
     * True if LFU ordered, false if insertion ordered.
     */
    private final boolean accessLFU;

    public LfuLinkedHashMap() {
        init();
        accessLFU = false;
    }

    public LfuLinkedHashMap(int capacity) {
        this(capacity, DEFAULT_LOAD_FACTOR);
    }

    public LfuLinkedHashMap(int capacity, float loadFactor) {
        this(capacity, loadFactor, false);
    }

    public LfuLinkedHashMap(int capacity, float loadFactor, boolean accessLFU) {
        super(capacity, loadFactor);
        this.accessLFU = accessLFU;
    }

    public LfuLinkedHashMap(Map<? extends K, ? extends V> map) {
        this(capacityForInitSize(map.size()));
        constructorPutAll(map);
    }

    @Override
    void init() {
        header = new LfuLinkedEntry<>();
    }

    static class LfuLinkedEntry<K, V> extends HashMapEntry<K, V> {

        LfuLinkedEntry<K, V> next1;
        LfuLinkedEntry<K, V> prev1;
        long accessCount;

        /**
         * Create the header entry
         */
        LfuLinkedEntry() {
            super(null, null, 0, null);
            prev1 = next1 = this;
            this.accessCount = 1;
        }

        /**
         * Create a normal entry
         */
        public LfuLinkedEntry(K key, V value, int hash, HashMapEntry<K, V> next, LfuLinkedEntry<K, V> next1, LfuLinkedEntry<K, V> prev) {
            super(key, value, hash, next);
            this.next1 = next1;
            this.prev1 = prev;
            this.accessCount = 1;
        }
    }

    public Entry<K, V> eldest() {
        LfuLinkedEntry<K, V> eldest = header.next1;
        return eldest != header ? eldest : null;
    }


    @Override
    void addNewEntry(K key, V value, int hash, int index) {
        LfuLinkedEntry<K, V> header = this.header;

        // Remove eldest entry if instructed to do so.
        LfuLinkedEntry<K, V> eldest = header.next1;
        if (eldest != header && removeEldestEntry(eldest)) {
            remove(eldest.key);
        }

        // Create new entry, link it on to list, and put it into table
        LfuLinkedEntry<K, V> oldTail = header.prev1;
        LfuLinkedEntry<K, V> newTail = new LfuLinkedEntry<>(
                key, value, hash, table[index], header, oldTail);
        table[index] = oldTail.next1 = header.prev1 = newTail;
    }

    @Override
    void addNewEntryForNullKey(V value) {
        LfuLinkedEntry<K, V> header = this.header;

        // Remove eldest entry if instructed to do so.
        LfuLinkedEntry<K, V> eldest = header.next1;
        if (eldest != header && removeEldestEntry(eldest)) {
            remove(eldest.key);
        }

        // Create new entry, link it on to list, and put it into table
        LfuLinkedEntry<K, V> oldTail = header.prev1;
        LfuLinkedEntry<K, V> newTail = new LfuLinkedEntry<>(
                null, value, 0, null, header, oldTail);
        entryForNullKey = oldTail.next1 = header.prev1 = newTail;
    }

    // use for constructorPutAll
    @Override
    HashMapEntry<K, V> constructorNewEntry(K key, V value, int hash, HashMapEntry<K, V> first) {
        LfuLinkedEntry<K, V> header = this.header;
        LfuLinkedEntry<K, V> oldTail = header.prev1;
        LfuLinkedEntry<K, V> newTail
                = new LfuLinkedEntry<>(key, value, hash, first, header, oldTail);
        return oldTail.next1 = header.prev1 = newTail;
    }

    /**
     * Returns the value of the mapping with the specified key.
     *
     * @param key the key.
     * @return the value of the mapping with the specified key, or {@code null}
     * if no mapping for the specified key is found.
     */
    @Override
    public V get(Object key) {
        /*
         * This method is overridden to eliminate the need for a polymorphic
         * invocation in superclass at the expense of code duplication.
         */
        if (key == null) {
            HashMapEntry<K, V> e = entryForNullKey;
            if (e == null)
                return null;
            if (accessLFU)
                transform((LfuLinkedEntry<K, V>) e);
            return e.value;
        }

        int hash = Collections.secondaryHash(key);
        HashMapEntry<K, V>[] tab = table;
        for (HashMapEntry<K, V> e = tab[hash & (tab.length - 1)];
             e != null; e = e.next) {
            K eKey = e.key;
            if (eKey == key || (e.hash == hash && key.equals(eKey))) {
                if (accessLFU)
                    transform((LfuLinkedEntry<K, V>) e);
                return e.value;
            }
        }
        return null;
    }

    /**
     * Relinks the given entry to the  the list. According to access count,
     * this method is invoked whenever the value of a  pre-existing entry is
     * read by Map.get or modified by Map.put.
     */
    private void transform(LfuLinkedEntry<K, V> e) {

        // Add access count
        e.accessCount++;
        // Modify count
        modCount++;
        // Get the next
        LfuLinkedEntry<K, V> next = e.next1;
        if (e.accessCount < next.accessCount) {
            return;                   // no need to do anything
        }
        // Unlink e
        e.prev1.next1 = e.next1;
        e.next1.prev1 = e.prev1;

        boolean done = false;
        for (; next != this.header; next = next.next1) {
            if (e.accessCount < next.accessCount) {
                e.next1 = next;
                e.prev1 = next.prev1;
                next.prev1.next1 = e;
                next.prev1 = e;
                done = true;
                break;
            }
        }
        if (!done) {
            // Relink e as tail in the list
            LfuLinkedEntry<K, V> header = this.header;
            LfuLinkedEntry<K, V> oldTail = header.prev1;
            e.next1 = header;
            e.prev1 = oldTail;
            oldTail.next1 = header.prev1 = e;
        }
    }

    @Override
    void preModify(HashMapEntry<K, V> e) {
        if (accessLFU) {
            transform((LfuLinkedEntry<K, V>) e);
        }
    }

    @Override
    void postRemove(HashMapEntry<K, V> e) {
        LfuLinkedEntry<K, V> e1 = (LfuLinkedEntry<K, V>) e;
        e1.prev1.next1 = e1.next1;
        e1.next1.prev1 = e1.prev1;
        e1.next1 = e1.prev1 = null;   // Help the GC (for performance)
    }

    @Override
    public boolean containsValue(Object value) {
        if (value == null) {
            for (LfuLinkedEntry<K, V> header = this.header, e = header.next1;
                 e != header; e = e.next1) {
                if (e.value == null) {
                    return true;
                }
            }
            return false;
        }

        // for not null value
        for (LfuLinkedEntry<K, V> header = this.header, e = header.next1;
             e != header; e = e.next1) {
            if (value.equals(e.value)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void clear() {
        super.clear();

        // Help the GC (for performance)
        LfuLinkedEntry<K, V> header = this.header;
        for (LfuLinkedEntry<K, V> e = header.next1; e != header; ) {
            LfuLinkedEntry<K, V> next = e.next1;
            e.prev1 = e.next1 = null;
            e = next;
        }
        header.prev1 = header.next1 = header;
    }

    private abstract class LfuLinkedHashIterator<T> implements Iterator<T> {
        LfuLinkedEntry<K, V> next = header.next1;
        LfuLinkedEntry<K, V> lastReturned = null;
        int expectedModCount = modCount;

        public final boolean hasNext() {
            return next != header;
        }

        final LfuLinkedEntry<K, V> nextEntry() {
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
            LfuLinkedEntry<K, V> e = next;
            if (e == header)
                throw new NoSuchElementException();
            next = e.next1;
            return lastReturned = e;
        }

        public final void remove() {
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
            if (lastReturned == null)
                throw new IllegalStateException();
            LfuLinkedHashMap.this.remove(lastReturned.key);
            lastReturned = null;
            expectedModCount = modCount;
        }
    }

    private final class KeyIterator extends LfuLinkedHashIterator<K> {
        @Override
        public K next() {
            return nextEntry().key;
        }
    }

    private final class ValueIterator extends LfuLinkedHashIterator<V> {
        @Override
        public V next() {
            return nextEntry().value;
        }
    }

    private final class EntryIterator extends LfuLinkedHashIterator<Map.Entry<K, V>> {
        @Override
        public Entry<K, V> next() {
            return nextEntry();
        }
    }

    @Override
    Iterator<K> newKeyIterator() {
        return new KeyIterator();
    }

    @Override
    Iterator<V> newValueIterator() {
        return new ValueIterator();
    }

    @Override
    Iterator<Entry<K, V>> newEntryIterator() {
        return new EntryIterator();
    }

    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return false;
    }
}
