package com.yxkang.android.image.core;

import android.content.Context;
import android.graphics.Bitmap;

import com.yxkang.android.image.cache.disk.CommonDiskCache;
import com.yxkang.android.image.cache.memory.FifoMemoryCache;
import com.yxkang.android.image.cache.memory.LruMemoryCache;
import com.yxkang.android.image.cache.memory.MemoryCacheStrategy;
import com.yxkang.android.image.cache.memory.SoftMemoryCache;
import com.yxkang.android.image.core.util.ImageSize;

import java.io.IOException;
import java.io.InputStream;

/**
 * ImageLoaderConfiguration.
 */
public final class ImageLoaderConfiguration {

    ImageSize imageSize;
    boolean isCache2Memory;
    boolean isCache2SDCard;
    MemoryCacheStrategy memoryCacheStrategy;
    private CommonDiskCache diskCache = new CommonDiskCache();

    private ImageLoaderConfiguration(Builder builder) {
        this.imageSize = builder.imageSize;
        this.isCache2Memory = builder.isCache2Memory;
        this.isCache2SDCard = builder.isCache2SDCard;
        this.memoryCacheStrategy = builder.memoryCacheStrategy;
    }

    public void putBitmapToMemory(String key, Bitmap bitmap) {
        if (isCache2Memory) {
            if (memoryCacheStrategy == MemoryCacheStrategy.LRU) {
                LruMemoryCache.getInstance().put(key, bitmap);
            } else if (memoryCacheStrategy == MemoryCacheStrategy.LFU) {
                throw new UnsupportedOperationException("not support !");
            } else if (memoryCacheStrategy == MemoryCacheStrategy.FIFO) {
                FifoMemoryCache.getInstance().put(key, bitmap);
            } else if (memoryCacheStrategy == MemoryCacheStrategy.SOFT) {
                SoftMemoryCache.getInstance().put(key, bitmap);
            }
        } else {
            throw new UnsupportedOperationException("please enable memory cache strategy first !");
        }
    }

    public Bitmap getBitmapFromMemory(String key) {
        if (isCache2Memory) {
            Bitmap bitmap = null;
            if (memoryCacheStrategy == MemoryCacheStrategy.LRU) {
                bitmap = LruMemoryCache.getInstance().get(key);
            } else if (memoryCacheStrategy == MemoryCacheStrategy.LFU) {
                throw new UnsupportedOperationException("not support !");
            } else if (memoryCacheStrategy == MemoryCacheStrategy.FIFO) {
                bitmap = FifoMemoryCache.getInstance().get(key);
            } else if (memoryCacheStrategy == MemoryCacheStrategy.SOFT) {
                bitmap = SoftMemoryCache.getInstance().get(key);
            }
            return bitmap;
        } else {
            throw new UnsupportedOperationException("please enable memory cache strategy first !");
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
            throw new UnsupportedOperationException("please enable disk cache strategy first !");
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
            throw new UnsupportedOperationException("please enable disk cache strategy first !");
        }
    }

    public static ImageLoaderConfiguration getDefault(Context context) {
        return new Builder(context).build();
    }

    public static class Builder {

        private ImageSize imageSize;
        private boolean isCache2Memory = true;
        private boolean isCache2SDCard = true;
        private MemoryCacheStrategy memoryCacheStrategy = MemoryCacheStrategy.LRU;

        public Builder(Context context) {
            this.imageSize = new ImageSize(context);
        }

        public Builder setWidth(int width) {
            this.imageSize.setWidth(width);
            return this;
        }

        public Builder setHeight(int height) {
            this.imageSize.setHeight(height);
            return this;
        }

        public Builder setIsCache2Memory(boolean isCache2Memory) {
            this.isCache2Memory = isCache2Memory;
            return this;
        }

        public Builder setIsCache2SDCard(boolean isCache2SDCard) {
            this.isCache2SDCard = isCache2SDCard;
            return this;
        }

        public Builder setMemoryCacheStrategy(MemoryCacheStrategy memoryCacheStrategy) {
            this.memoryCacheStrategy = memoryCacheStrategy;
            return this;
        }

        public ImageLoaderConfiguration build() {
            return new ImageLoaderConfiguration(this);
        }
    }
}
