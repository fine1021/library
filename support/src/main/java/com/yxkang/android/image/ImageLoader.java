package com.yxkang.android.image;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.util.LruCache;
import android.util.Log;

import com.yxkang.android.media.MediaFile;
import com.yxkang.android.util.BitmapUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class ImageLoader {

    private static final String TAG = ImageLoader.class.getSimpleName();
    private static InternalHandler sHandler;

    private final Object mMutex = new Object();

    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final int KEEP_ALIVE = 1;

    private static final int MESSAGE_POST_TASK_START = 0x1;
    private static final int MESSAGE_POST_TASK_FAIL = 0x2;
    private static final int MESSAGE_POST_TASK_SUCCESS = 0x3;


    private static final BlockingQueue<Runnable> sPoolWorkQueue = new LinkedBlockingQueue<>();

    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        public Thread newThread(@NonNull Runnable r) {
            return new Thread(r, "ImageLoaderThread #" + mCount.getAndIncrement());
        }
    };

    private static volatile ExecutorService mImageThreadPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE,
            KEEP_ALIVE, TimeUnit.MILLISECONDS, sPoolWorkQueue, sThreadFactory);

    private static final List<ImageLoaderTask> sTaskQueue = Collections.synchronizedList(new ArrayList<ImageLoaderTask>());

    private static final int maxMemory = (int) Runtime.getRuntime().maxMemory();
    private static final int mCacheSize = maxMemory / 8;
    private static final LruCache<String, Bitmap> mMemoryCache = new LruCache<String, Bitmap>(mCacheSize) {

        @Override
        protected int sizeOf(String key, Bitmap value) {

            return value.getRowBytes() * value.getHeight();
        }

    };


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


    private static Handler getHandler() {
        synchronized (ImageLoader.class) {
            if (sHandler == null) {
                sHandler = new InternalHandler();
            }
            return sHandler;
        }
    }


    public void displayImageAsync(String uri, OnImageLoaderListener listener) {

        ImageLoaderTask task = new ImageLoaderTask(listener, null, uri);

        addTask(task);

        sendMessage(MESSAGE_POST_TASK_START, task);

        task.bitmap = getCacheBitmap(uri);
        if (task.bitmap != null) {
            Log.i(TAG, "displayCacheBitmap : " + uri);
            sendMessage(MESSAGE_POST_TASK_SUCCESS, task);
        } else {

            if (ImageDownloader.Protocol.FILE.belongsTo(uri)) {
                loadImage(task);
            } else if (ImageDownloader.Protocol.HTTP.belongsTo(uri) || ImageDownloader.Protocol.HTTPS.belongsTo(uri)) {
                downloadImage(task);
            }
        }

    }


    public void cancelCurrentTask() {
        for (ImageLoaderTask task : sTaskQueue) {
            task.cancelTask.set(true);
        }
        sTaskQueue.clear();
        sPoolWorkQueue.clear();
    }

    private void addTask(ImageLoaderTask task) {
        if (!sTaskQueue.contains(task)) {
            sTaskQueue.add(task);
        }
    }

    private void removeTask(ImageLoaderTask task) {
        if (sTaskQueue.contains(task)) {
            sTaskQueue.remove(task);
        }
    }


    private void sendMessage(int what, ImageLoaderTask task) {
        if (!task.cancelTask.get()) {
            Message message = getHandler().obtainMessage(what, task);
            message.sendToTarget();
        } else {
            Log.w(TAG, "task has been canceled ! uri : " + task.uri);
        }
    }

    private static class InternalHandler extends Handler {
        public InternalHandler() {
            super(Looper.getMainLooper());
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            ImageLoaderTask task = (ImageLoaderTask) msg.obj;
            switch (msg.what) {
                case MESSAGE_POST_TASK_START:
                    task.listener.onImageLoaderStart(task.uri);
                    break;
                case MESSAGE_POST_TASK_FAIL:
                    Log.w(TAG, "loadImageFail : " + task.uri);
                    task.listener.onImageLoaderFail(task.uri);
                    break;
                case MESSAGE_POST_TASK_SUCCESS:
                    Log.i(TAG, "loadImageSuccess : " + task.uri);
                    task.listener.onImageLoaderSuccess(task.uri, task.bitmap);
                    break;
            }
        }
    }


    private void loadImage(final ImageLoaderTask task) {
        final String filePath = ImageDownloader.Protocol.FILE.crop(task.uri);

        Callable<Boolean> callable = new Callable<Boolean>() {

            @Override
            public Boolean call() throws Exception {

                /**
                 * use mutex to lock the operation, only one thread can do the operation
                 */
                synchronized (mMutex) {

                    if (task.cancelTask.get()) {
                        Log.w(TAG, "loadImageCancel-1 : " + task.uri);
                        task.listener.onImageLoaderCancel(task.uri);
                        return false;
                    }

                    if (MediaFile.isImageFileType(filePath)) {
                        task.bitmap = BitmapUtil.createImageThumbnail(filePath, 200, 200);
                    } else if (MediaFile.isVideoFileType(filePath)) {
                        task.bitmap = BitmapUtil.createVideoThumbnail(filePath, MediaStore.Images.Thumbnails.MICRO_KIND);
                    }

                    if (task.cancelTask.get()) {
                        Log.w(TAG, "loadImageCancel-2 : " + task.uri);
                        task.listener.onImageLoaderCancel(task.uri);
                        return false;
                    }

                    if (task.bitmap != null) {
                        sendMessage(MESSAGE_POST_TASK_SUCCESS, task);
                        addCacheBitmap(task.uri, task.bitmap);
                    } else {
                        sendMessage(MESSAGE_POST_TASK_FAIL, task);
                    }
                    removeTask(task);
                    return true;
                }
            }
        };

        mImageThreadPool.submit(callable);
    }

    private void downloadImage(ImageLoaderTask task) {

    }

    private static class ImageLoaderTask {

        final OnImageLoaderListener listener;
        Bitmap bitmap;
        final String uri;
        AtomicBoolean cancelTask = new AtomicBoolean(false);

        public ImageLoaderTask(OnImageLoaderListener listener, Bitmap bitmap, String uri) {
            this.listener = listener;
            this.bitmap = bitmap;
            this.uri = uri;
        }

    }

    public interface OnImageLoaderListener {

        void onImageLoaderStart(String uri);

        void onImageLoaderSuccess(String uri, Bitmap bitmap);

        void onImageLoaderFail(String uri);

        void onImageLoaderCancel(String uri);
    }

}
