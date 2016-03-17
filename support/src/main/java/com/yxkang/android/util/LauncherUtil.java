package com.yxkang.android.util;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import java.util.HashSet;
import java.util.List;

/**
 * Created by yexiaokang on 2016/3/7.
 */
public class LauncherUtil {

    private static final String TAG = LauncherUtil.class.getSimpleName();
    private static final HashSet<String> sWritePermission = new HashSet<>();
    private static final String TABLE_FAVORITES = "favorites";
    private static final String PARAMETER_NOTIFY = "notify";

    static {
        sWritePermission.clear();
        sWritePermission.add("com.android.launcher.permission.WRITE_SETTINGS");
        sWritePermission.add("com.android.launcher2.permission.WRITE_SETTINGS");
        sWritePermission.add("com.android.launcher3.permission.WRITE_SETTINGS");
        sWritePermission.add("com.huawei.launcher2.permission.WRITE_SETTINGS");
        sWritePermission.add("com.huawei.launcher3.permission.WRITE_SETTINGS");
        sWritePermission.add("com.huawei.android.launcher.permission.WRITE_SETTINGS");
    }


    public static String getAuthorityFromPermission(Context context) {
        PackageManager manager = context.getPackageManager();

        Intent launcher = new Intent(Intent.ACTION_MAIN);
        launcher.addCategory(Intent.CATEGORY_HOME);
        ResolveInfo resolveInfo = manager.resolveActivity(launcher, 0);
        if (resolveInfo == null) {
            return null;
        }
        List<ProviderInfo> list = manager.queryContentProviders(resolveInfo.activityInfo.processName,
                resolveInfo.activityInfo.applicationInfo.uid, PackageManager.GET_PROVIDERS);
        if (list != null && list.size() > 0) {
            for (ProviderInfo info : list) {
                if (TextUtils.isEmpty(info.writePermission)) {
                    continue;
                }
                if (sWritePermission.contains(info.writePermission)) {
                    return info.authority;
                }
            }
        }
        return null;
    }

    public static boolean shortcutExists(Context context, String title, Intent intent) {
        final ContentResolver resolver = context.getContentResolver();
        String authority = getAuthorityFromPermission(context);
        if (TextUtils.isEmpty(authority)) {
            return false;
        }
        Uri uri = Uri.parse("content://" + authority + "/" + TABLE_FAVORITES + "?" + PARAMETER_NOTIFY + "=true");
        Cursor cursor = resolver.query(uri, new String[]{"title", "intent"}, "title=? and intent=?", new String[]{title, intent.toUri(0)}, null);
        boolean result = false;
        try {
            if (cursor != null) {
                result = cursor.moveToFirst();
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return result;
    }

    public static boolean shortcutExists(Context context, String title) {
        final ContentResolver resolver = context.getContentResolver();
        String authority = getAuthorityFromPermission(context);
        if (TextUtils.isEmpty(authority)) {
            return false;
        }
        Uri uri = Uri.parse("content://" + authority + "/" + TABLE_FAVORITES + "?" + PARAMETER_NOTIFY + "=true");
        Cursor cursor = resolver.query(uri, new String[]{"title"}, "title=?", new String[]{title}, null);
        boolean result = false;
        try {
            if (cursor != null) {
                result = cursor.moveToFirst();
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return result;
    }

    public static void dumpShortcut(Context context) {
        final ContentResolver resolver = context.getContentResolver();
        String authority = getAuthorityFromPermission(context);
        if (TextUtils.isEmpty(authority)) {
            return;
        }
        Uri uri = Uri.parse("content://" + authority + "/" + TABLE_FAVORITES + "?" + PARAMETER_NOTIFY + "=true");
        Cursor cursor = resolver.query(uri, new String[]{"title", "intent"}, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String title = cursor.getString(cursor.getColumnIndex("title"));
                String intent = cursor.getString(cursor.getColumnIndex("intent"));
                Log.i(TAG, "title = " + title);
                Log.i(TAG, "intent = " + intent);
            }
            cursor.close();
        }
    }
}
