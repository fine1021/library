package com.yxkang.android.image;

import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.util.LruCache;
import android.util.Log;

import com.yxkang.android.media.MediaFile;
import com.yxkang.android.util.BitmapUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageLoader {

    private static final String TAG = ImageLoader.class.getSimpleName();
    private static LruCache<String, Bitmap> mMemoryCache;
    private static boolean isInited = false;
    private static ExecutorService mImageThreadPool = null;

    public ImageLoader() {
        if (!isInited) init();
    }

    private void init() {
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int mCacheSize = maxMemory / 8;
        mMemoryCache = new LruCache<String, Bitmap>(mCacheSize) {

            @Override
            protected int sizeOf(String key, Bitmap value) {

                return value.getRowBytes() * value.getHeight();
            }

        };
        isInited = true;
    }

    private ExecutorService getThreadPool() {
        if (mImageThreadPool == null) {
            synchronized (ExecutorService.class) {
                mImageThreadPool = Executors.newFixedThreadPool(3);
            }
        }
        return mImageThreadPool;
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

    public void displayImageAsync(String uri, OnImageLoaderListener listener) {

        listener.onImageLoaderStart(uri);

        Bitmap bitmap = getCacheBitmap(uri);
        if (bitmap != null) {
            listener.onImageLoaderSuccess(uri, bitmap);
            Log.i(TAG, "displayCacheBitmap : " + uri);
        } else {

            if (ImageDownloader.Protocol.FILE.belongsTo(uri)) {
                loadImage(uri, listener);
            } else if (ImageDownloader.Protocol.HTTP.belongsTo(uri) || ImageDownloader.Protocol.HTTPS.belongsTo(uri)) {
                downloadImage(uri, listener);
            }
        }

    }

    public void cancelTask() {
        if (mImageThreadPool != null) {
            mImageThreadPool.shutdown();
            mImageThreadPool = null;

        }
    }


    private void loadImage(final String uri, final OnImageLoaderListener listener) {
        final String filePath = ImageDownloader.Protocol.FILE.crop(uri);

        final Handler handler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0:
                        listener.onImageLoaderSuccess(uri, (Bitmap) msg.obj);
                        break;
                    case 1:
                        listener.onImageLoaderFail(uri);
                        break;
                    default:
                        break;
                }
            }
        };

        getThreadPool().submit(new Runnable() {

            @Override
            public void run() {

                Bitmap bitmap = null;
                if (MediaFile.isImageFileType(filePath)) {
                    bitmap = BitmapUtil.createImageThumbnail(filePath, 200, 200);
                } else if (MediaFile.isVideoFileType(filePath)) {
                    bitmap = ThumbnailUtils.createVideoThumbnail(filePath, MediaStore.Images.Thumbnails.MINI_KIND);
                }

                if (bitmap != null) {
                    Message message = handler.obtainMessage(0);
                    message.obj = bitmap;
                    handler.sendMessage(message);

                    Log.i(TAG, "loadImage : " + uri);

                    addCacheBitmap(uri, bitmap);
                } else {
                    Message message = handler.obtainMessage(1);
                    handler.sendMessage(message);
                }
            }
        });
    }

    private void downloadImage(String uri, OnImageLoaderListener listener) {

    }

    public interface OnImageLoaderListener {

        void onImageLoaderStart(String uri);

        void onImageLoaderSuccess(String uri, Bitmap bitmap);

        void onImageLoaderFail(String uri);
    }

}
