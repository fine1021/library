package com.yxkang.android.image.cache.memory;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.util.Log;

import java.util.LinkedHashMap;

/**
 * LruMemoryCache {@link LruCache}
 */
@SuppressWarnings("ALL")
public class LruMemoryCache implements MemoryCache {

    private static final String TAG = LruMemoryCache.class.getSimpleName();

    /**
     * params for StrongReference {@link LruCache}
     */
    private static final int maxMemory = (int) Runtime.getRuntime().maxMemory();
    private static final int mLruCacheSize = maxMemory / 8;

    /**
     * {@link LruCache} is implemented by {@link LinkedHashMap}, generate a access ordering list.
     * LRU algorithm, put the least recently used object to tail of the list
     */
    private static final LruCache<String, Bitmap> mLruCache = new LruCache<String, Bitmap>(mLruCacheSize) {

        @Override
        protected int sizeOf(String key, Bitmap value) {
            return value.getRowBytes() * value.getHeight();
        }

        @Override
        protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
            if (oldValue != null && !oldValue.isRecycled()) {
                oldValue.recycle();
                Log.i(TAG, "oldValue recycle : " + key);
            }
        }
    };

    private static LruMemoryCache instance = null;

    private LruMemoryCache() {
    }

    public static LruMemoryCache getInstance() {
        if (instance == null) {
            instance = new LruMemoryCache();
        }
        return instance;
    }

    @Override
    public void put(String key, Bitmap bitmap) {
        synchronized (mLruCache) {
            if (get(key) == null && bitmap != null) {
                mLruCache.put(key, bitmap);
            }
        }
    }

    @Override
    public Bitmap get(String key) {
        synchronized (mLruCache) {
            return mLruCache.get(key);
        }

    }

    @Override
    public Bitmap remove(String key) {
        synchronized (mLruCache) {
            return mLruCache.remove(key);
        }
    }

    @Override
    public void clear() {
        synchronized (mLruCache) {
            mLruCache.evictAll();
        }
        instance = null;
    }

    public void recycle(String key, Bitmap bitmap) {
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
            Log.i(TAG, "recycle : " + key);
        }
    }

    public synchronized final int hitCount() {
        return mLruCache.hitCount();
    }

    public synchronized final int missCount() {
        return mLruCache.missCount();
    }

    @Override
    public synchronized final String toString() {
        return mLruCache.toString();
    }
}
