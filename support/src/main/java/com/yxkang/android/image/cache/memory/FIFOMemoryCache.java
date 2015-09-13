package com.yxkang.android.image.cache.memory;

import android.graphics.Bitmap;
import android.util.Log;

import com.yxkang.android.util.FIFOCache;

/**
 * FIFOMemoryCache
 */
@SuppressWarnings("ALL")
public class FIFOMemoryCache implements MemoryCache {

    private static final String TAG = FIFOMemoryCache.class.getSimpleName();

    /**
     * params for StrongReference {@link FIFOCache}
     */
    private static final int maxMemory = (int) Runtime.getRuntime().maxMemory();
    private static final int mFIFOCacheSize = maxMemory / 8;

    private static final FIFOCache<String, Bitmap> mFIFOCache = new FIFOCache<String, Bitmap>(mFIFOCacheSize) {
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

    @Override
    public void put(String key, Bitmap bitmap) {
        synchronized (mFIFOCache) {
            if (get(key) == null && bitmap != null) {
                mFIFOCache.put(key, bitmap);
            }
        }
    }

    @Override
    public Bitmap get(String key) {
        synchronized (mFIFOCache) {
            return mFIFOCache.get(key);
        }
    }

    @Override
    public Bitmap remove(String key) {
        synchronized (mFIFOCache) {
            return mFIFOCache.remove(key);
        }
    }

    @Override
    public void clear() {
        synchronized (mFIFOCache) {
            mFIFOCache.evictAll();
        }
    }

    public synchronized final int hitCount() {
        return mFIFOCache.hitCount();
    }

    public synchronized final int missCount() {
        return mFIFOCache.missCount();
    }

    @Override
    public synchronized final String toString() {
        return mFIFOCache.toString();
    }
}
