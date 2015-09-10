package com.yxkang.android.image;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

/**
 * ImageCache
 */
@SuppressWarnings("ALL")
public class ImageCache {

    private static final int maxMemory = (int) Runtime.getRuntime().maxMemory();
    private static final int mCacheSize = maxMemory / 8;
    private static final LruCache<String, Bitmap> mMemoryCache = new LruCache<String, Bitmap>(mCacheSize) {

        @Override
        protected int sizeOf(String key, Bitmap value) {

            return value.getRowBytes() * value.getHeight();
        }

    };

    private static ImageCache instance = null;

    private ImageCache() {
    }

    public static ImageCache getInstance() {
        if (instance == null) {
            instance = new ImageCache();
        }
        return instance;
    }

    public void addCacheBitmap(String key, Bitmap bitmap) {
        synchronized (mMemoryCache) {
            if (getCacheBitmap(key) == null && bitmap != null) {
                mMemoryCache.put(key, bitmap);
            }
        }
    }

    public Bitmap getCacheBitmap(String key) {
        synchronized (mMemoryCache) {
            return mMemoryCache.get(key);
        }
    }

    public void clearMemoryCache() {
        synchronized (mMemoryCache) {
            mMemoryCache.evictAll();
        }
    }
}
