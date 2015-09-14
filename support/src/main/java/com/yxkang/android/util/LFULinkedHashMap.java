package com.yxkang.android.util;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.Set;

/**
 * LFULinkedHashMap
 */
public class LFULinkedHashMap<K, V> extends AbstractMap<K, V> implements Cloneable, Serializable {

    /**
     * Min capacity (other than zero) for a HashMap. Must be a power of two
     * greater than 1 (and less than 1 << 30).
     */
    private static final int MINIMUM_CAPACITY = 4;

    /**
     * Max capacity for a HashMap. Must be a power of two >= MINIMUM_CAPACITY.
     */
    private static final int MAXIMUM_CAPACITY = 1 << 30;

    /**
     * An empty table shared by all zero-capacity maps (typically from default
     * constructor). It is never written to, and replaced on first put. Its size
     * is set to half the minimum, so that the first resize will create a
     * minimum-sized table.
     */
    private static final Entry[] EMPTY_TABLE
            = new LFULinkedEntry[MINIMUM_CAPACITY >>> 1];

    /**
     * The default load factor. Note that this implementation ignores the
     * load factor, but cannot do away with it entirely because it's
     * mentioned in the API.
     * <p>
     * <p>Note that this constant has no impact on the behavior of the program,
     * but it is emitted as part of the serialized form. The load factor of
     * .75 is hardwired into the program, which uses cheap shifts in place of
     * expensive division.
     */
    static final float DEFAULT_LOAD_FACTOR = .75F;

    /**
     * The hash table. If this hash map contains a mapping for null, it is
     * not represented this hash table.
     */
    transient LFULinkedEntry<K, V>[] table;

    /**
     * The entry representing the null key, or null if there's no such mapping.
     */
    transient LFULinkedEntry<K, V> entryForNullKey;

    /**
     * The number of mappings in this hash map.
     */
    transient int size;

    /**
     * Incremented by "structural modifications" to allow (best effort)
     * detection of concurrent modification.
     */
    transient int modCount;

    /**
     * The table is rehashed when its size exceeds this threshold.
     * The value of this field is generally .75 * capacity, except when
     * the capacity is zero, as described in the EMPTY_TABLE declaration
     * above.
     */
    private transient int threshold;

    // Views - lazily initialized
    private transient Set<K> keySet;
    private transient Set<Entry<K, V>> entrySet;
    private transient Collection<V> values;


    transient LFULinkedEntry<K, V> header;

    /**
     * True if LFU ordered, false if insertion ordered.
     */
    private boolean accessLFU;

    public LFULinkedHashMap() {
        init();
        accessLFU = false;
    }

    public LFULinkedHashMap(int capacity) {
        if (capacity < 0) {
            throw new IllegalArgumentException("Capacity: " + capacity);
        }

        if (capacity == 0) {
            //noinspection unchecked
            LFULinkedEntry<K, V>[] tab = (LFULinkedEntry<K, V>[]) EMPTY_TABLE;
            table = tab;
            threshold = -1; // Forces first put() to replace EMPTY_TABLE
            return;
        }

        if (capacity < MINIMUM_CAPACITY) {
            capacity = MINIMUM_CAPACITY;
        } else if (capacity > MAXIMUM_CAPACITY) {
            capacity = MAXIMUM_CAPACITY;
        }
    }

    public LFULinkedHashMap(int capacity, float loadFactor) {
        this(capacity, loadFactor, false);
    }

    public LFULinkedHashMap(int capacity, float loadFactor, boolean accessLFU) {
        init();
        this.accessLFU = accessLFU;
    }

    private void init() {
        header = new LFULinkedEntry<>();
    }


    @Override
    public Set<Entry<K, V>> entrySet() {
        return null;
    }

    static class LFULinkedEntry<K, V> implements Entry<K, V> {

        final K key;
        V value;
        final int hash;
        LFULinkedEntry<K, V> next;
        LFULinkedEntry<K, V> prev;
        long accessCount;

        /**
         * Create the header entry
         */
        LFULinkedEntry() {
            this.key = null;
            this.value = null;
            this.hash = 0;
            prev = next = this;
            this.accessCount = 0;
        }

        /**
         * Create a normal entry
         */
        LFULinkedEntry(K key, V value, int hash, LFULinkedEntry<K, V> next, LFULinkedEntry<K, V> prev) {
            this.key = key;
            this.value = value;
            this.hash = hash;
            this.next = next;
            this.prev = prev;
            this.accessCount = 0;
        }

        public final K getKey() {
            return key;
        }

        public final V getValue() {
            return value;
        }

        public final V setValue(V value) {
            V oldValue = this.value;
            this.value = value;
            return oldValue;
        }

        @Override
        public final boolean equals(Object o) {
            if (!(o instanceof Entry)) {
                return false;
            }
            Entry<?, ?> e = (Entry<?, ?>) o;
            return e.getKey().equals(key) && e.getValue().equals(value);
        }

        @Override
        public final int hashCode() {
            return (key == null ? 0 : key.hashCode()) ^
                    (value == null ? 0 : value.hashCode());
        }

        @Override
        public final String toString() {
            return key + "=" + value;
        }
    }

    public Entry<K, V> eldest() {
        LFULinkedEntry<K, V> eldest = header.next;
        return eldest != header ? eldest : null;
    }


/*    @Override
    void addNewEntry(K key, V value, int hash, int index) {
        LFULinkedEntry<K, V> header = this.header;

        // Remove eldest entry if instructed to do so.
        LFULinkedEntry<K, V> eldest = header.next;
        if (eldest != header && removeEldestEntry(eldest)) {
            remove(eldest.key);
        }

        // Create new entry, link it on to list, and put it into table
        LFULinkedEntry<K, V> oldTail = header.prv;
        LFULinkedEntry<K, V> newTail = new LFULinkedEntry<K, V>(
                key, value, hash, table[index], header, oldTail);
        table[index] = oldTail.next = header.prv = newTail;
    }*/


}
