package com.yxkang.android.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.util.Log;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Bitmap Utils
 */
public class BitmapUtil {

    private static final String TAG = "BitmapUtil";

    private static final int UNCONSTRAINED = -1;

    public static final int TARGET_SIZE_MICRO_THUMBNAIL = 96;

    public static Bitmap createImageThumbnail(String filePath, int reqWidth, int reqHeight) {
        Bitmap bm = null;
        FileInputStream stream = null;
        try {
            stream = new FileInputStream(filePath);
            FileDescriptor fd = stream.getFD();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 1;
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFileDescriptor(fd, null, options);
            if (options.mCancel || options.outWidth == -1 || options.outHeight == -1) {
                return null;
            }
            options.inSampleSize = computeSampleSize(options, Math.min(reqWidth, reqHeight), reqWidth * reqHeight);
            Log.d(TAG, String.valueOf(options.inSampleSize));
            options.inJustDecodeBounds = false;

            options.inDither = false;
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            bm = BitmapFactory.decodeFileDescriptor(fd, null, options);
        } catch (IOException ex) {
            Log.e(TAG, "", ex);
        } catch (OutOfMemoryError oom) {
            Log.e(TAG, "Unable to decode file " + filePath + ". OutOfMemoryError.", oom);
        } finally {
            try {
                if (stream != null) {
                    stream.close();
                }
            } catch (IOException ex) {
                Log.e(TAG, "", ex);
            }
        }
        return bm;
    }

    /**
     * Create a video thumbnail for a video. May return null if the video is
     * corrupt or the format is not supported.
     *
     * @param filePath the path of video file
     * @param kind     could be MINI_KIND or MICRO_KIND
     */
    public static Bitmap createVideoThumbnail(String filePath, int kind) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(filePath);
            bitmap = retriever.getFrameAtTime();
        } catch (RuntimeException ex) {
            // Assume this is a corrupt video file.
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException ex) {
                // Ignore failures while cleaning up.
            }
        }

        if (bitmap == null) return null;

        if (kind == MediaStore.Images.Thumbnails.MINI_KIND) {
            // Scale down the bitmap if it's too large.
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            int max = Math.max(width, height);
            if (max > 512) {
                float scale = 512f / max;
                int w = Math.round(scale * width);
                int h = Math.round(scale * height);
                bitmap = Bitmap.createScaledBitmap(bitmap, w, h, true);
            }
        } else if (kind == MediaStore.Images.Thumbnails.MICRO_KIND) {
            bitmap = ThumbnailUtils.extractThumbnail(bitmap,
                    TARGET_SIZE_MICRO_THUMBNAIL,
                    TARGET_SIZE_MICRO_THUMBNAIL,
                    ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        }
        return bitmap;
    }


    private static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);

        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }

        return roundedSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;

        int lowerBound = (maxNumOfPixels == UNCONSTRAINED) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == UNCONSTRAINED) ? 128
                : (int) Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));

        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }

        if ((maxNumOfPixels == UNCONSTRAINED) && (minSideLength == UNCONSTRAINED)) {
            return 1;
        } else if (minSideLength == UNCONSTRAINED) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }
}
