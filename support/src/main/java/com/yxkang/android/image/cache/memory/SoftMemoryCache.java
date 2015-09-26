package com.yxkang.android.image.cache.memory;

import android.graphics.Bitmap;
import android.util.Log;

import java.lang.ref.SoftReference;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * SoftMemoryCache, LRU algorithm
 */
@SuppressWarnings("ALL")
public final class SoftMemoryCache implements MemoryCache {

    private static final String TAG = SoftMemoryCache.class.getSimpleName();

    private static final LinkedHashMap<String, SoftReference<Bitmap>> mSoftCache = new LinkedHashMap<String, SoftReference<Bitmap>>(0, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Entry eldest) {
            return super.removeEldestEntry(eldest);
        }

    };

    private static SoftMemoryCache instance = null;

    private SoftMemoryCache() {
    }

    public static SoftMemoryCache getInstance() {
        if (instance == null) {
            instance = new SoftMemoryCache();
        }
        return instance;
    }

    @Override
    public void put(String key, Bitmap bitmap) {
        synchronized (mSoftCache) {
            if (bitmap != null) {
                mSoftCache.put(key, new SoftReference<>(bitmap));
            }
        }
    }

    @Override
    public Bitmap get(String key) {
        synchronized (mSoftCache) {
            Bitmap bitmap = null;
            SoftReference<Bitmap> softReference = mSoftCache.get(key);
            if ((softReference != null) && (softReference.get() != null)) {
                bitmap = softReference.get();
            }
            return bitmap;
        }
    }

    @Override
    public Bitmap remove(String key) {
        synchronized (mSoftCache) {
            Bitmap bitmap = null;
            SoftReference<Bitmap> softReference = mSoftCache.remove(key);
            if ((softReference != null) && (softReference.get() != null)) {
                bitmap = softReference.get();
            }
            return bitmap;
        }
    }

    @Override
    public void clear() {
        synchronized (mSoftCache) {
//            mSoftCache.clear();
            Iterator<Map.Entry<String, SoftReference<Bitmap>>> iterator = mSoftCache.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, SoftReference<Bitmap>> entry = iterator.next();
                if (entry.getValue().get() != null) {
                    recycle(entry.getKey(), entry.getValue().get());
                }
            }
        }
    }

    public void recycle(String key, Bitmap bitmap) {
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
            Log.i(TAG, "recycle : " + key);
        }
    }
}
