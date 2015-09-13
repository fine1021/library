package com.yxkang.android.image.cache;

import android.graphics.Bitmap;

import com.yxkang.android.image.cache.disk.DiskCache;
import com.yxkang.android.image.cache.memory.LruMemoryCache;

import java.io.IOException;
import java.io.InputStream;

/**
 * CacheManager
 */
@SuppressWarnings("ALL")
public class CacheManager {

    private boolean isCache2Memory = true;
    private boolean isCache2SDCard = true;
    private DiskCache diskCache = new DiskCache();

    public CacheManager() {
        this.diskCache.setOptions(DiskCache.OPTIONS_RECYCLE_INPUT);
    }

    public boolean isCache2Memory() {
        return isCache2Memory;
    }

    public void setIsCache2Memory(boolean isCache2Memory) {
        this.isCache2Memory = isCache2Memory;
    }

    public boolean isCache2SDCard() {
        return isCache2SDCard;
    }

    public void setIsCache2SDCard(boolean isCache2SDCard) {
        this.isCache2SDCard = isCache2SDCard;
    }

    public void putBitmapToMemory(String key, Bitmap bitmap) {
        if (isCache2Memory) {
            LruMemoryCache.getInstance().put(key, bitmap);
//            SoftMemoryCache.getInstance().put(key, bitmap);
        } else {
            throw new UnsupportedOperationException("has't enable memory cache strategy !");
        }
    }

    public Bitmap getBitmapFromMemory(String key) {
        if (isCache2Memory) {
            return LruMemoryCache.getInstance().get(key);
//            return SoftMemoryCache.getInstance().get(key);
        } else {
            throw new UnsupportedOperationException("has't enable memory cache strategy !");
        }
    }

    public void putBitmapToSDCard(String key, InputStream inputStream) {
        if (isCache2SDCard) {
            try {
                diskCache.put(key, inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            throw new UnsupportedOperationException("has't enable disk cache strategy !");
        }
    }

    public void putBitmapToSDCard(String key, Bitmap bitmap) {
        if (isCache2SDCard) {
            try {
                diskCache.put(key, bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            throw new UnsupportedOperationException("has't enable disk cache strategy !");
        }
    }

    public String memoryStatisticalData() {
        return LruMemoryCache.getInstance().toString();
    }
}
