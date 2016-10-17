package com.yxkang.android.sample.bean;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;

import com.yxkang.android.util.Storage;

import java.util.List;
import java.util.Locale;

/**
 * Created by fine on 2016/10/13.
 */

@SuppressWarnings("ALL")
public class StorageInfo {

    private static final int K = 1024;
    private static final int M = 1024 * 1024;
    private static final int G = 1024 * 1024 * 1024;

    public String getStorageInformation(Context context) {
        StringBuilder builder = new StringBuilder();
        List<String> list = Storage.getVolumePaths(context);
        if (!list.isEmpty()) {
            for (String s : list) {
                builder.append("StorageDirectory：").append(s).append("\n");
                StatFs statFs = new StatFs(s);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    builder.append("Available：").append(getSize(statFs.getBlockSizeLong() * statFs.getAvailableBlocksLong())).append("\n");
                    builder.append("Total：").append(getSize(statFs.getBlockSizeLong() * statFs.getBlockCountLong())).append("\n");
                } else {
                    builder.append("Available：").append(getSize(statFs.getBlockSize() * statFs.getAvailableBlocks())).append("\n");
                    builder.append("Total：").append(getSize(statFs.getBlockSize() * statFs.getBlockCount())).append("\n");
                }
            }
            builder.append("DefaultDirectory：").append(Environment.getExternalStorageDirectory().getAbsolutePath());
        }
        return builder.toString();
    }

    private String getSize(long bytes) {
        if (bytes < K) {
            return String.format(Locale.CHINA, "%.2f B", bytes * 1.0f);
        } else if (bytes < M) {
            return String.format(Locale.CHINA, "%.2f KB", bytes * 1.0f / K);
        } else if (bytes < G) {
            return String.format(Locale.CHINA, "%.2f MB", bytes * 1.0f / M);
        } else {
            return String.format(Locale.CHINA, "%.2f GB", bytes * 1.0f / G);
        }
    }
}
