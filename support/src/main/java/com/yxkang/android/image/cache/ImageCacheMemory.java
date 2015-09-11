package com.yxkang.android.image.cache;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import java.lang.ref.SoftReference;
import java.util.LinkedHashMap;

/**
 * ImageCacheMemory
 * <br/>
 * double cache, include {@link LruCache} and {@link LinkedHashMap}
 */
@SuppressWarnings("ALL")
public class ImageCacheMemory {

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
            mSoftCache.put(key, new SoftReference<Bitmap>(oldValue));
        }
    };

    /**
     * params for SoftReference {@link LinkedHashMap}
     */
    private static final int mSoftCacheSize = 15;

    /**
     * {@link LinkedHashMap#get(Object)} and {@link LinkedHashMap#put(Object, Object)} will relinks the given entry to the tail of the list.
     * use the Constructor to generate a access ordering list, other then a insert ordering list
     */
    private static final LinkedHashMap<String, SoftReference<Bitmap>> mSoftCache = new LinkedHashMap<String, SoftReference<Bitmap>>(mSoftCacheSize, 0.75f, true) {

        @Override
        protected boolean removeEldestEntry(Entry<String, SoftReference<Bitmap>> eldest) {
            if (size() > mSoftCacheSize) {
                return true;
            }
            return false;
        }
    };

    private static int hitCount;
    private static int missCount;
    private static ImageCacheMemory instance = null;

    private ImageCacheMemory() {
    }

    public static ImageCacheMemory getInstance() {
        if (instance == null) {
            hitCount = 0;
            missCount = 0;
            instance = new ImageCacheMemory();
        }
        return instance;
    }

    public void putCacheBitmap(String key, Bitmap bitmap) {
        synchronized (mLruCache) {
            if (getCacheBitmap(key) == null && bitmap != null) {
                mLruCache.put(key, bitmap);
            }
        }
    }

    public Bitmap getCacheBitmap(String key) {
        Bitmap bitmap = null;
        synchronized (mLruCache) {
            bitmap = mLruCache.get(key);
            if (bitmap != null) {
                hitCount++;
                return bitmap;
            }
            missCount++;
        }

        synchronized (mSoftCache) {
            SoftReference<Bitmap> softReference = mSoftCache.get(key);
            if (softReference != null) {
                bitmap = softReference.get();
                if (bitmap != null) {
                    // put the bitmap back to LruCache
                    putCacheBitmap(key, bitmap);
                    // the bitmap has been moved to LruCache, so no need to save.
                    mSoftCache.remove(key);
                    hitCount++;
                    missCount--;
                    return bitmap;
                } else {
                    // clear the softReference if the bitmap has been recycled
                    mSoftCache.remove(key);
                }
            }
        }

        return bitmap;
    }

    public void clearMemoryCache() {
        synchronized (mLruCache) {
            mLruCache.evictAll();
        }
        synchronized (mSoftCache) {
            mSoftCache.clear();
        }
        hitCount = 0;
        missCount = 0;
    }

    public synchronized final int hitCount() {
        return hitCount;
    }

    public synchronized final int missCount() {
        return missCount;
    }

    @Override
    public synchronized final String toString() {
        int accesses = hitCount + missCount;
        int hitPercent = accesses != 0 ? (100 * hitCount / accesses) : 0;
        return String.format("MemoryCache[hits=%d,misses=%d,hitRate=%d%%]", hitCount, missCount, hitPercent);
    }
}
