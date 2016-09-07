/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.yxkang.android.sample.application;

import android.content.Context;
import android.util.Log;

import com.yxkang.android.sample.db.DatabaseManager;

import java.util.HashMap;

/**
 * Manages all of the services that can be returned by {@link ServiceManager#getService(String)}.
 * Used by {@link ServiceManager}.
 */
@SuppressWarnings("all")
final class ServiceRegistry {

    private static final String TAG = "ServiceRegistry";

    private static final HashMap<String, ServiceFetcher<?>> SYSTEM_SERVICE_FETCHERS = new HashMap<>();

    private static int sServiceCacheSize;

    static {

        registerService(ServiceManager.DATABASE_SERVICE, new CachedServiceFetcher<DatabaseManager>() {
            @Override
            public DatabaseManager createService(Context context) {
                return new DatabaseManager(context);
            }
        });

        Log.v(TAG, "static initializer: ok");
    }

    /**
     * Creates an array which is used to cache per-Context service instances.
     */
    public static Object[] createServiceCache() {
        return new Object[sServiceCacheSize];
    }

    /**
     * Gets a system service from a given context.
     */
    public static Object getSystemService(ServiceManager manager, Context context, String name) {
        ServiceFetcher<?> fetcher = SYSTEM_SERVICE_FETCHERS.get(name);
        return fetcher != null ? fetcher.getService(manager, context) : null;
    }


    /**
     * Statically registers a system service with the context.
     * This method must be called during static initialization only.
     */
    private static <T> void registerService(String serviceName, ServiceFetcher<T> serviceFetcher) {
        SYSTEM_SERVICE_FETCHERS.put(serviceName, serviceFetcher);
    }

    /**
     * Base interface for classes that fetch services.
     * These objects must only be created during static initialization.
     */
    interface ServiceFetcher<T> {
        T getService(ServiceManager manager, Context context);
    }

    /**
     * Override this class when the service constructor needs a Context
     * and should be cached and retained by that context.
     */
    static abstract class CachedServiceFetcher<T> implements ServiceFetcher<T> {
        private final int mCacheIndex;

        public CachedServiceFetcher() {
            mCacheIndex = sServiceCacheSize++;
        }

        @Override
        public final T getService(ServiceManager manager, Context context) {
            final Object[] cache = manager.mServiceCache;
            synchronized (cache) {
                // Fetch or create the service.
                Object service = cache[mCacheIndex];
                if (service == null) {
                    service = createService(context);
                    cache[mCacheIndex] = service;
                }
                return (T) service;
            }
        }

        public abstract T createService(Context context);
    }

    /**
     * Override this class when the service does not need a Context
     * and should be cached and retained process-wide.
     */
    static abstract class StaticServiceFetcher<T> implements ServiceFetcher<T> {
        private T mCachedInstance;

        @Override
        public final T getService(ServiceManager manager, Context unused) {
            synchronized (StaticServiceFetcher.this) {
                if (mCachedInstance == null) {
                    mCachedInstance = createService();
                }
                return mCachedInstance;
            }
        }

        public abstract T createService();
    }

    /**
     * Like StaticServiceFetcher, creates only one instance of the service per process, but when
     * creating the service for the first time, passes it the application context of the creating
     * component.
     * <p/>
     * Is this safe in the case where multiple applications share the same process?
     * Delete this once its only user (ConnectivityManager) is known to work well in the
     * case where multiple application components each have their own ConnectivityManager object.
     */
    static abstract class StaticContextServiceFetcher<T> implements ServiceFetcher<T> {
        private T mCachedInstance;

        @Override
        public final T getService(ServiceManager manager, Context context) {
            synchronized (StaticContextServiceFetcher.this) {
                if (mCachedInstance == null) {
                    mCachedInstance = createService(context.getApplicationContext());
                }
                return mCachedInstance;
            }
        }

        public abstract T createService(Context applicationContext);
    }

}
