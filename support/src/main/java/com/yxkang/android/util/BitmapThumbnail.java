package com.yxkang.android.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.util.Log;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Bitmap Utils
 */
@SuppressWarnings("ALL")
public class BitmapThumbnail {

    private static final String TAG = "BitmapThumbnail";

    private static final int UNCONSTRAINED = -1;


    public static Bitmap createImageThumbnail(String filePath, int reqWidth, int reqHeight, boolean extract) {
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
            options.inJustDecodeBounds = false;

            options.inDither = false;
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            bm = BitmapFactory.decodeFileDescriptor(fd, null, options);
        } catch (IOException ex) {
            Log.e(TAG, "", ex);
        } catch (OutOfMemoryError oom) {
            Log.e(TAG, "Unable to decode file " + filePath + ". OutOfMemoryError.", oom);
        } finally {
            IoUtils.closeQuietly(stream);
        }

        if (extract) {
            try {
                bm = ThumbnailUtils.extractThumbnail(bm, reqWidth, reqHeight, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
            } catch (OutOfMemoryError oom) {
                Log.e(TAG, "Unable to extractThumbnail " + filePath + ". OutOfMemoryError.", oom);
            }
        }

        return bm;
    }

    public static Bitmap createVideoThumbnail(String filePath, int reqWidth, int reqHeight, boolean extract) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(filePath);
            bitmap = retriever.getFrameAtTime(0);
        } catch (IllegalArgumentException ex) {
            // Assume this is a corrupt video file
        } catch (RuntimeException ex) {
            // Assume this is a corrupt video file.
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException ex) {
                // Ignore failures while cleaning up.
            }
        }

        if (extract) {
            try {
                bitmap = ThumbnailUtils.extractThumbnail(bitmap, reqWidth, reqHeight, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
            } catch (OutOfMemoryError oom) {
                Log.e(TAG, "Unable to extractThumbnail " + filePath + ". OutOfMemoryError.", oom);
            }
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

    private static int computeSampleSize2(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int roundedSize = 0;
        float w = options.outWidth;
        float h = options.outHeight;

        if (w <= reqWidth && h <= reqHeight) {
            roundedSize = 1;
        } else {
            int scaleW = Math.round(w / reqWidth);
            int scaleH = Math.round(h / reqHeight);
            roundedSize = Math.max(scaleW, scaleH);
        }

        return roundedSize;
    }
}
