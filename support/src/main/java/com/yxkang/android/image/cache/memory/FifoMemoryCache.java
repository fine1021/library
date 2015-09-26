package com.yxkang.android.image.cache.memory;

import android.graphics.Bitmap;
import android.util.Log;

import com.yxkang.android.util.FifoCache;

/**
 * FifoMemoryCache
 */
@SuppressWarnings("ALL")
public final class FifoMemoryCache implements MemoryCache {

    private static final String TAG = FifoMemoryCache.class.getSimpleName();

    /**
     * params for StrongReference {@link FifoCache}
     */
    private static final int maxMemory = (int) Runtime.getRuntime().maxMemory();
    private static final int mFifoCacheSize = maxMemory / 8;

    private static final FifoCache<String, Bitmap> mFifoCache = new FifoCache<String, Bitmap>(mFifoCacheSize) {
        @Override
        protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
            if (oldValue != null && !oldValue.isRecycled()) {
                oldValue.recycle();
                Log.i(TAG, "oldValue recycle : " + key);
            }
        }

        @Override
        protected int sizeOf(String key, Bitmap value) {
            return value.getRowBytes() * value.getHeight();
        }
    };

    private static FifoMemoryCache instance = null;

    private FifoMemoryCache() {
    }

    public static FifoMemoryCache getInstance() {
        if (instance == null) {
            instance = new FifoMemoryCache();
        }
        return instance;
    }

    @Override
    public void put(String key, Bitmap bitmap) {
        synchronized (mFifoCache) {
            if (bitmap != null) {
                mFifoCache.put(key, bitmap);
            }
        }
    }

    @Override
    public Bitmap get(String key) {
        synchronized (mFifoCache) {
            return mFifoCache.get(key);
        }
    }

    @Override
    public Bitmap remove(String key) {
        synchronized (mFifoCache) {
            return mFifoCache.remove(key);
        }
    }

    @Override
    public void clear() {
        synchronized (mFifoCache) {
            mFifoCache.evictAll();
        }
    }

    public synchronized final int hitCount() {
        return mFifoCache.hitCount();
    }

    public synchronized final int missCount() {
        return mFifoCache.missCount();
    }

    @Override
    public synchronized final String toString() {
        return mFifoCache.toString();
    }
}
