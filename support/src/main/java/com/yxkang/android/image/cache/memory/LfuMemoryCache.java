package com.yxkang.android.image.cache.memory;

import android.graphics.Bitmap;
import android.util.Log;

import com.yxkang.android.util.LfuCache;
import com.yxkang.android.util.LfuLinkedHashMap;


/**
 * LfuMemoryCache.
 */
public final class LfuMemoryCache implements MemoryCache {

    private static final String TAG = LfuMemoryCache.class.getSimpleName();

    /**
     * params for StrongReference {@link LfuCache}
     */
    private static final int maxMemory = (int) Runtime.getRuntime().maxMemory();
    private static final int mLfuCacheSize = maxMemory / 8;

    /**
     * {@link LfuCache} is implemented by {@link LfuLinkedHashMap}, generate a access ordering list.
     * LFU algorithm, put the least recently used object to tail of the list.
     * <br/>
     * {@code put}, {@code get}, and {@code putAll} these methods will change the order of the entries
     */
    private static final LfuCache<String, Bitmap> mLfuCache = new LfuCache<String, Bitmap>(mLfuCacheSize) {

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

    private static LfuMemoryCache instance = null;

    private LfuMemoryCache() {
    }

    public static LfuMemoryCache getInstance() {
        if (instance == null) {
            instance = new LfuMemoryCache();
        }
        return instance;
    }

    @Override
    public void put(String key, Bitmap bitmap) {
        synchronized (mLfuCache) {
            if (bitmap != null) {
                mLfuCache.put(key, bitmap);
            }
        }
    }

    @Override
    public Bitmap get(String key) {
        synchronized (mLfuCache) {
            return mLfuCache.get(key);
        }
    }

    @Override
    public Bitmap remove(String key) {
        synchronized (mLfuCache) {
            return mLfuCache.remove(key);
        }
    }

    @Override
    public void clear() {
        synchronized (mLfuCache) {
            mLfuCache.evictAll();
        }
    }

    public synchronized final int hitCount() {
        return mLfuCache.hitCount();
    }

    public synchronized final int missCount() {
        return mLfuCache.missCount();
    }

    @Override
    public synchronized final String toString() {
        return mLfuCache.toString();
    }
}
