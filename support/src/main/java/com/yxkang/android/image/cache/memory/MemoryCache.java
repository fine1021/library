package com.yxkang.android.image.cache.memory;

import android.graphics.Bitmap;

/**
 * MemoryCache interface
 */
@SuppressWarnings("ALL")
public interface MemoryCache {

    void put(String key, Bitmap bitmap);

    Bitmap get(String key);

    Bitmap remove(String key);

    void clear();
}
