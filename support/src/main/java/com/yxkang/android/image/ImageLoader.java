package com.yxkang.android.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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
import java.util.concurrent.locks.ReentrantReadWriteLock;

@SuppressWarnings("ALL")
public class ImageLoader {

    private static final String TAG = ImageLoader.class.getSimpleName();
    private static InternalHandler sHandler;

    /**
     * use mutex to lock the create bitmap operation
     */
    private final Object mMutex = new Object();

    /**
     * Iterator and for-each are unsafe-Thread Operation, they don't allow to
     * modify the content of List when accessing. use the ReentrantReadWriteLock
     * to avoid the problem
     */
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);

    private Context context;

    private int mThumbnailWidth;

    private int mThumbnailHeight;

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

        public Thread newThread(Runnable r) {
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

    public ImageLoader(Context context) {
        this.context = context;
        int dpi = context.getResources().getDisplayMetrics().densityDpi;
        if (dpi < 300) {
            mThumbnailWidth = 200;
            mThumbnailHeight = 150;
        } else if (dpi < 400) {
            mThumbnailWidth = 300;
            mThumbnailHeight = 250;
        } else {
            mThumbnailWidth = 400;
            mThumbnailHeight = 350;
        }
    }

    public void setThumbnailSize(int width, int height) {
        mThumbnailWidth = width;
        mThumbnailHeight = height;
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

        lock.readLock().lock();
        try {
            for (ImageLoaderTask task : sTaskQueue) {
                task.cancelTask.set(true);
            }
        } finally {
            lock.readLock().unlock();
        }

        lock.writeLock().lock();
        try {
            sTaskQueue.clear();
        } finally {
            lock.writeLock().unlock();
        }

        sPoolWorkQueue.clear();
    }

    private void addTask(ImageLoaderTask task) {
        lock.writeLock().lock();
        try {
            if (!sTaskQueue.contains(task)) {
                sTaskQueue.add(task);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    private void removeTask(ImageLoaderTask task) {
        lock.writeLock().lock();
        try {
            if (sTaskQueue.contains(task)) {
                sTaskQueue.remove(task);
            }
        } finally {
            lock.writeLock().unlock();
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
                 * use mutex to lock the operation, only one thread can handle
                 */
                synchronized (mMutex) {

                    if (task.cancelTask.get()) {
                        Log.w(TAG, "loadImageCancel-1 : " + task.uri);
                        task.listener.onImageLoaderCancel(task.uri);
                        return false;
                    }

                    if (MediaFile.isImageFileType(filePath)) {
                        task.bitmap = BitmapUtil.createImageThumbnail(filePath, mThumbnailWidth, mThumbnailHeight);
                    } else if (MediaFile.isVideoFileType(filePath)) {
                        task.bitmap = BitmapUtil.createVideoThumbnail(filePath, mThumbnailWidth, mThumbnailHeight);
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
        final AtomicBoolean cancelTask = new AtomicBoolean(false);

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
