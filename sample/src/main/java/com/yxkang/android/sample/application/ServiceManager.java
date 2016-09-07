package com.yxkang.android.sample.application;

import android.content.Context;
import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by fine on 2016/8/6.
 */
public class ServiceManager {

    @StringDef({DATABASE_SERVICE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ServiceName {
    }

    public static final String DATABASE_SERVICE = "database";

    // The service cache for the system services that are cached per-ServiceManager.
    final Object[] mServiceCache = ServiceRegistry.createServiceCache();

    public Object getService(@ServiceName String name) {
        return getService(null, name);
    }

    public Object getService(Context context, @ServiceName String name) {
        return ServiceRegistry.getSystemService(this, context, name);
    }
}
