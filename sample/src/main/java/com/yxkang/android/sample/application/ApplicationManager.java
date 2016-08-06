package com.yxkang.android.sample.application;

import android.content.Context;

/**
 * Created by fine on 2016/8/6.
 */
public class ApplicationManager {

    public static final String DATABASE_SERVICE = "database";

    // The service cache for the system services that are cached per-ApplicationManager.
    final Object[] mServiceCache = ServiceRegistry.createServiceCache();

    public Object getService(String name) {
        return getService(null, name);
    }

    public Object getService(Context context, String name) {
        return ServiceRegistry.getSystemService(this, context, name);
    }
}
