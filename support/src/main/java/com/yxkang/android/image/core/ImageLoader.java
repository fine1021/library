package com.yxkang.android.image.core;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.widget.ImageView;

import com.yxkang.android.image.core.ref.RefImageView;
import com.yxkang.android.media.MediaFile;
import com.yxkang.android.util.BitmapUtil;
import com.yxkang.android.util.Logger;

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


public class ImageLoader {

    private static final String TAG = ImageLoader.class.getSimpleName();
    private static InternalHandler sHandler = null;

    /**
     * use mutex to lock the create bitmap operation
     */
    private final Object mMutex = new Object();

    /**
     * Iterator and for-each are unsafe-Thread Operation, they don't allow to
     * modify the content of List when accessing. use the {@link ReentrantReadWriteLock}
     * to avoid the problem
     */
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);

    private ImageLoaderConfiguration configuration = null;

    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final int KEEP_ALIVE = 1;

    /**
     * Post message type
     */
    private static final int MESSAGE_POST_TASK_START = 0x1;
    private static final int MESSAGE_POST_TASK_FAIL = 0x2;
    private static final int MESSAGE_POST_TASK_SUCCESS = 0x3;


    /**
     * The threadPool block queue
     */
    private static final BlockingQueue<Runnable> sPoolWorkQueue = new LinkedBlockingQueue<>();

    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        public Thread newThread(Runnable r) {
            return new Thread(r, "ImageLoaderThread #" + mCount.getAndIncrement());
        }
    };

    private static volatile ExecutorService mImageThreadPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE,
            KEEP_ALIVE, TimeUnit.MILLISECONDS, sPoolWorkQueue, sThreadFactory);

    /**
     * The task list, use save all the executed task
     */
    private static final List<ImageLoaderTask> sTaskQueue = Collections.synchronizedList(new ArrayList<ImageLoaderTask>());

    /**
     * Print the log information
     */
    private static Logger logger = new Logger(Logger.WARN);

    /**
     * Constructor
     *
     * @param configuration the configuration of imageLoader
     */
    public ImageLoader(ImageLoaderConfiguration configuration) {
        this.configuration = configuration;
    }

    private static Handler getHandler() {
        synchronized (ImageLoader.class) {
            if (sHandler == null) {
                sHandler = new InternalHandler();
            }
            return sHandler;
        }
    }

    /**
     * display the image created from the uri to the given imageView, without listening the progress
     *
     * @param uri       image uri
     * @param imageView the imageView to display the bitmap
     * @see #displayImageAsync(String, ImageView, OnImageLoaderListener)
     * @see #displayImageAsync(String, OnImageLoaderListener)
     */
    public void displayImageAsync(String uri, ImageView imageView) {
        displayImageAsync(uri, imageView, listenerEmpty);
    }

    /**
     * display the image created from the uri to the given imageView
     *
     * @param uri       image uri
     * @param imageView the imageView to display the bitmap
     * @param listener  a callback of image load progress. see {@link com.yxkang.android.image.core.ImageLoader.OnImageLoaderListener}
     * @see #displayImageAsync(String, ImageView)
     * @see #displayImageAsync(String, OnImageLoaderListener)
     */
    public void displayImageAsync(String uri, ImageView imageView, OnImageLoaderListener listener) {

        if (TextUtils.isEmpty(uri)) {
            throw new IllegalArgumentException("uri is empty");
        }

        if (listener == null) {
            throw new NullPointerException("listener == null");
        }

        ImageLoaderTask task;

        if (imageView == null) {
            task = new ImageLoaderTask(uri, listener);
        } else {
            task = new ImageLoaderTask(uri, new RefImageView(imageView), listener);
        }

        addTask(task);

        sendMessage(MESSAGE_POST_TASK_START, task);

        task.bitmap = getCacheBitmap(uri);
        if (task.bitmap != null) {
            logger.i(TAG, "displayCacheBitmap : " + uri);
            sendMessage(MESSAGE_POST_TASK_SUCCESS, task);
        } else {

            if (ImageProtocol.FILE.belongsTo(uri)) {
                loadImage(task);
            } else if (ImageProtocol.HTTP.belongsTo(uri) || ImageProtocol.HTTPS.belongsTo(uri)) {
                downloadImage(task);
            }
        }
    }

    /**
     * load the image asynchronous, when load success it will return a bitmap created from the given uri
     *
     * @param uri      image uri
     * @param listener a callback of image load progress. see {@link com.yxkang.android.image.core.ImageLoader.OnImageLoaderListener}
     * @see #displayImageAsync(String, ImageView)
     * @see #displayImageAsync(String, ImageView, OnImageLoaderListener)
     */
    public void displayImageAsync(String uri, OnImageLoaderListener listener) {
        displayImageAsync(uri, null, listener);
    }

    public Bitmap getCacheBitmap(String key) {
        return configuration.getBitmapFromMemory(key);
    }

    /**
     * cancel all the tasks
     */
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
            logger.w(TAG, "task has been canceled ! uri : " + task.uri);
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
                    logger.w(TAG, "loadImageFail : " + task.uri);
                    task.listener.onImageLoaderFail(task.uri);
                    break;
                case MESSAGE_POST_TASK_SUCCESS:
                    logger.i(TAG, "loadImageSuccess : " + task.uri);
                    task.listener.onImageLoaderSuccess(task.uri, task.bitmap);
                    handleRefImageView(task, task.bitmap);
                    break;
            }
        }

        private void handleRefImageView(ImageLoaderTask loaderTask, Bitmap bitmap) {
            if (loaderTask.refImageView != null) {
                loaderTask.refImageView.setImageBitmap(bitmap);
            }
        }
    }


    /**
     * load the image in sdcard
     *
     * @param task ImageLoaderTask
     */
    private void loadImage(final ImageLoaderTask task) {

        Callable<Boolean> callable = new Callable<Boolean>() {

            @Override
            public Boolean call() throws Exception {

                /**
                 * use mutex to lock the operation, only one thread can handle
                 */
                synchronized (mMutex) {

                    String filePath = ImageProtocol.FILE.crop(task.uri);

                    if (task.cancelTask.get()) {
                        logger.w(TAG, "loadImageCancel-1 : " + task.uri);
                        task.listener.onImageLoaderCancel(task.uri);
                        return false;
                    }

                    if (MediaFile.isImageFileType(filePath)) {
                        task.bitmap = BitmapUtil.createImageThumbnail(filePath, configuration.imageSize.getWidth(), configuration.imageSize.getHeight(), true);
                    } else if (MediaFile.isVideoFileType(filePath)) {
                        task.bitmap = BitmapUtil.createVideoThumbnail(filePath, configuration.imageSize.getWidth(), configuration.imageSize.getHeight(), true);
                    }

                    if (task.cancelTask.get()) {
                        if (task.bitmap != null) {
                            configuration.putBitmapToMemory(task.uri, task.bitmap);
                        }
                        logger.w(TAG, "loadImageCancel-2 : " + task.uri);
                        task.listener.onImageLoaderCancel(task.uri);
                        return false;
                    }

                    if (task.bitmap != null) {
                        sendMessage(MESSAGE_POST_TASK_SUCCESS, task);
                        configuration.putBitmapToMemory(task.uri, task.bitmap);
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

    /**
     * a ImageLoader helper class, save the current task
     */
    private static class ImageLoaderTask {

        Bitmap bitmap;
        final String uri;
        RefImageView refImageView;
        final OnImageLoaderListener listener;
        final AtomicBoolean cancelTask = new AtomicBoolean(false);

        public ImageLoaderTask(String uri, OnImageLoaderListener listener) {
            this(uri, null, listener);
        }

        public ImageLoaderTask(String uri, RefImageView refImageView, OnImageLoaderListener listener) {
            this.uri = uri;
            this.refImageView = refImageView;
            this.listener = listener;
            this.bitmap = null;
        }
    }

    private final OnImageLoaderListener listenerEmpty = new OnImageLoaderListener() {
        @Override
        public void onImageLoaderStart(String uri) {

        }

        @Override
        public void onImageLoaderSuccess(String uri, Bitmap bitmap) {

        }

        @Override
        public void onImageLoaderFail(String uri) {

        }

        @Override
        public void onImageLoaderCancel(String uri) {

        }
    };

    /**
     * a callback when loading the image
     */
    public interface OnImageLoaderListener {

        void onImageLoaderStart(String uri);

        void onImageLoaderSuccess(String uri, Bitmap bitmap);

        void onImageLoaderFail(String uri);

        void onImageLoaderCancel(String uri);
    }

}
