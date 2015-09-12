package com.yxkang.android.image.cache.disk;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * DiskCache.
 */
@SuppressWarnings("ALL")
public class DiskCache implements BaseDiskCache {

    /**
     * Options used internally
     */
    private static final int OPTIONS_NONE = 0x0;

    /**
     * Constant used to indicate we should recycle the input.
     */
    public static final int OPTIONS_RECYCLE_INPUT = 0x1;

    /**
     * Default value of disk cache
     */
    private static final String DEFAULT_CACHE_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "cache" + File.separator + "image";
    private static final Bitmap.CompressFormat DEFAULT_COMPRESS_FORMAT = Bitmap.CompressFormat.JPEG;
    public static final int DEFAULT_COMPRESS_QUALITY = 100;

    protected Bitmap.CompressFormat compressFormat = DEFAULT_COMPRESS_FORMAT;
    protected int compressQuality = DEFAULT_COMPRESS_QUALITY;
    protected int options = OPTIONS_NONE;
    protected String cacheDirectory = DEFAULT_CACHE_DIR;


    public DiskCache() {
    }

    public DiskCache(String cacheDirectory) {
        this.cacheDirectory = cacheDirectory;
    }

    @Override
    public File getDirectory() {
        File file = new File(cacheDirectory);
        if (!file.exists()) file.mkdirs();
        return file;
    }

    @Override
    public File get(String imageUri) {
        return getFile(imageUri);
    }

    @Override
    public boolean put(String imageUri, Bitmap bitmap) throws IOException {
        boolean result = false;
        boolean recycle = (options & OPTIONS_RECYCLE_INPUT) != 0;
        File file = get(imageUri);
        FileOutputStream stream = new FileOutputStream(file);
        try {
            bitmap.compress(compressFormat, compressQuality, stream);
            result = true;
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
        if (recycle && bitmap != null) {
            bitmap.recycle();
        }
        return result;
    }

    @Override
    public boolean put(String imageUri, InputStream imageStream) throws IOException {
        Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
        return put(imageUri, bitmap);
    }

    @Override
    public boolean remove(String imageUri) {
        return getFile(imageUri).delete();
    }

    @Override
    public void clear() {
        File file = new File(cacheDirectory);
        if (!file.exists()) return;
        File[] files = file.listFiles();
        for (File f : files) {
            f.delete();
        }
    }

    public void setCompressFormat(Bitmap.CompressFormat compressFormat) {
        this.compressFormat = compressFormat;
    }

    public void setCompressQuality(int compressQuality) {
        this.compressQuality = compressQuality;
    }

    public void setOptions(int options) {
        this.options = options;
    }

    public void setCacheDirectory(String cacheDirectory) {
        this.cacheDirectory = cacheDirectory;
    }

    protected File getFile(String imageUri) {
        File dir = new File(cacheDirectory);
        if (!dir.exists()) dir.mkdirs();
        return new File(dir, imageUri);
    }
}
