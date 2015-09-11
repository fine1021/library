package com.yxkang.android.image.cache;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.yxkang.android.util.BitmapUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * ImageCacheSDCard.
 */
@SuppressWarnings("ALL")
public class ImageCacheSDCard {

    /* Options used internally. */
    private static final int OPTIONS_NONE = 0x0;

    /**
     * Constant used to indicate we should recycle the input.
     */
    public static final int OPTIONS_RECYCLE_INPUT = 0x1;

    private static final String CacheDir = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "cache" + File.separator + "image";
    private static ImageCacheSDCard instance = null;

    private ImageCacheSDCard() {
    }

    public static ImageCacheSDCard getInstance() {
        if (instance == null) {
            instance = new ImageCacheSDCard();
        }
        return instance;
    }

    /**
     * save the bitmap to local sdcard
     *
     * @param key    image key
     * @param bitmap bitmap
     * @see #putCacheBitmap(String, Bitmap, int)
     * @see #putCacheBitmap(String, Bitmap, int, Bitmap.CompressFormat)
     */
    public void putCacheBitmap(String key, Bitmap bitmap) {
        putCacheBitmap(key, bitmap, OPTIONS_NONE);
    }

    /**
     * save the bitmap to local sdcard, with the options
     *
     * @param key     image key
     * @param bitmap  bitmap
     * @param options options used during saving the bitmap. such as {@link #OPTIONS_RECYCLE_INPUT}
     * @see #putCacheBitmap(String, Bitmap, int, Bitmap.CompressFormat)
     */
    public void putCacheBitmap(String key, Bitmap bitmap, int options) {
        putCacheBitmap(key, bitmap, options, Bitmap.CompressFormat.JPEG);
    }

    /**
     * save the bitmap to local sdcard, with the options and format
     *
     * @param key     image key
     * @param bitmap  bitmap
     * @param options options used during saving the bitmap. such as {@link #OPTIONS_RECYCLE_INPUT}
     * @param format  The format of the compressed image. see {@link android.graphics.Bitmap.CompressFormat}
     */
    public void putCacheBitmap(String key, Bitmap bitmap, int options, Bitmap.CompressFormat format) {
        boolean recycle = (options & OPTIONS_RECYCLE_INPUT) != 0;
        try {
            File file = new File(CacheDir, key);
            FileOutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(format, 100, outputStream);
            outputStream.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (recycle) {
            bitmap.recycle();
        }
    }

    /**
     * create a bitmap from inputstream.
     * if success, save the bitmap to local sdcard,
     * then recycle the bitmap
     *
     * @param key         image key
     * @param inputStream inputStream
     * @return the bitmap
     * @see #putCacheBitmap2(String, InputStream)
     */
    public void putCacheBitmap(String uri, InputStream inputStream) {
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        putCacheBitmap(uri, bitmap, OPTIONS_RECYCLE_INPUT);
    }

    /**
     * create a bitmap from inputstream.
     * if success, save the bitmap to local sdcard,
     * then return the bitmap
     *
     * @param key         image key
     * @param inputStream inputStream
     * @return the bitmap
     * @see #putCacheBitmap(String, InputStream)
     */
    public Bitmap putCacheBitmap2(String key, InputStream inputStream) {
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        putCacheBitmap(key, bitmap);
        return bitmap;
    }

    /**
     * get the bitmap according to the given key
     *
     * @param key image key
     * @return the bitmap or null if not found
     */
    public Bitmap getCacheBitmap(String key) {
        return BitmapUtil.createImageThumbnail(key, 200, 200);
    }
}
