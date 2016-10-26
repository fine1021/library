package com.yxkang.android.util;

import android.content.Context;
import android.os.Build;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.support.annotation.NonNull;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fine on 2016/10/13.
 */

@SuppressWarnings("ALL")
public final class Storage {

    private static final String TAG = "Storage";

    @NonNull
    public static List<String> getVolumePaths(Context context) {
        List<String> list = new ArrayList<>();
        try {
            StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
            Method getVolumePathsMethod = StorageManager.class.getDeclaredMethod("getVolumePaths");
            getVolumePathsMethod.setAccessible(true);
            String[] paths = (String[]) getVolumePathsMethod.invoke(storageManager);
            if (paths != null && paths.length > 0) {
                for (String path : paths) {
                    StatFs statFs = new StatFs(path);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                        if (statFs.getBlockSizeLong() > 0 && statFs.getBlockCountLong() > 0) {
                            list.add(path);
                        }
                    } else {
                        if (statFs.getBlockSize() > 0 && statFs.getBlockCount() > 0) {
                            list.add(path);
                        }
                    }
                }
            } else {
                Log.w(TAG, "getStorageDirectory: not found");
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return list;
    }
}
