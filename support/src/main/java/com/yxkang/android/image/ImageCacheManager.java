package com.yxkang.android.image;

import android.graphics.Bitmap;

import com.yxkang.android.image.cache.ImageCacheMemory;
import com.yxkang.android.image.cache.ImageCacheSDCard;

import java.io.InputStream;

/**
 * ImageCacheManager
 */
@SuppressWarnings("ALL")
public class ImageCacheManager {

    private boolean isCache2Memory = true;
    private boolean isCache2SDCard = true;

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
            ImageCacheMemory.getInstance().putCacheBitmap(key, bitmap);
        } else {
            throw new UnsupportedOperationException("has't enable memory cache strategy !");
        }
    }

    public Bitmap getBitmapFromMemory(String key) {
        if (isCache2Memory) {
            return ImageCacheMemory.getInstance().getCacheBitmap(key);
        } else {
            throw new UnsupportedOperationException("has't enable memory cache strategy !");
        }
    }

    public void putBitmapToSDCard(String key, InputStream inputStream) {
        if (isCache2SDCard) {
            ImageCacheSDCard.getInstance().putCacheBitmap(key, inputStream);
        } else {
            throw new UnsupportedOperationException("has't enable sdcard cache strategy !");
        }
    }

    public void putBitmapToSDCard(String key, Bitmap bitmap) {
        if (isCache2SDCard) {
            ImageCacheSDCard.getInstance().putCacheBitmap(key, bitmap);
        } else {
            throw new UnsupportedOperationException("has't enable sdcard cache strategy !");
        }
    }

    public Bitmap getBitmapFromSDCard(String key) {
        if (isCache2SDCard) {
            return ImageCacheSDCard.getInstance().getCacheBitmap(key);
        } else {
            throw new UnsupportedOperationException("has't enable sdcard cache strategy !");
        }
    }

    public String memoryStatisticalData() {
        return ImageCacheMemory.getInstance().toString();
    }
}
