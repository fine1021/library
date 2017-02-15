/*
 * Copyright (C) 2009 The Android Open Source Project
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

package com.yxkang.android.util;

import android.support.annotation.CallSuper;

/**
 * Helper class for crating pools of objects. An example use looks like this:
 * <pre>
 * public class MyPooledClass {
 *
 *     private static final SynchronizedPool<MyPooledClass> sPool =
 *             new SynchronizedPool<MyPooledClass>(10);
 *
 *     public static MyPooledClass obtain() {
 *         MyPooledClass instance = sPool.obtain();
 *         return (instance != null) ? instance : new MyPooledClass();
 *     }
 *
 *     public void recycle() {
 *          // Clear state if needed.
 *          sPool.recycle(this);
 *     }
 *
 *     . . .
 * }
 * </pre>
 */
@SuppressWarnings("ALL")
public final class Pools {

    /**
     * Interface for managing a pool of objects.
     *
     * @param <T> The pooled type.
     */
    public interface Pool<T> {

        /**
         * @return An instance from the pool if such, null otherwise.
         */
        T obtain();

        /**
         * Recycle an instance to the pool.
         *
         * @param instance The instance to release.
         * @return Whether the instance was put in the pool.
         * @throws IllegalStateException If the instance is already in the pool.
         */
        boolean recycle(T instance);
    }

    /**
     * Interface for these recyclable objects.
     */
    public interface Recyclable {

        /**
         * Clear the recycled flags, so it can be reused and recycled, if use {@link SimplePool} or
         * {@link SynchronizedPool}, it will be invoked automatically
         */
        void clearRecycledFlags();

        /**
         * Whether it has been recycled
         *
         * @return Whether the instance has been recycled.
         */
        boolean isRecycled();

        /**
         * Recycle the instance
         */
        void recycle();

        /**
         * Clear the resource for recycle, if use {@link SimplePool} or {@link SynchronizedPool},
         * it will be invoked automatically
         */
        void clearForRecycle();
    }

    /**
     * Simple implement for {@link Recyclable}
     */
    public static abstract class SimpleRecyclable implements Recyclable {

        private boolean mRecycled = false;

        @CallSuper
        @Override
        public void clearRecycledFlags() {
            mRecycled = false;
        }

        @Override
        public final boolean isRecycled() {
            return mRecycled;
        }

        @CallSuper
        @Override
        public void clearForRecycle() {
            mRecycled = true;
        }
    }

    private Pools() {
        /* do nothing - hiding constructor */
    }

    /**
     * Simple (non-synchronized) pool of objects.
     *
     * @param <T> The pooled type.
     */
    public static class SimplePool<T> implements Pool<T> {
        private final Object[] mPool;

        private int mPoolSize;

        /**
         * Creates a new instance.
         *
         * @param maxPoolSize The max pool size.
         * @throws IllegalArgumentException If the max pool size is less than zero.
         */
        public SimplePool(int maxPoolSize) {
            if (maxPoolSize <= 0) {
                throw new IllegalArgumentException("The max pool size must be > 0");
            }
            mPool = new Object[maxPoolSize];
        }

        @Override
        @SuppressWarnings("unchecked")
        public T obtain() {
            if (mPoolSize > 0) {
                final int lastPooledIndex = mPoolSize - 1;
                T instance = (T) mPool[lastPooledIndex];
                mPool[lastPooledIndex] = null;
                mPoolSize--;
                if (instance instanceof Recyclable) {
                    ((Recyclable) instance).clearRecycledFlags();
                }
                return instance;
            }
            return null;
        }

        @Override
        public boolean recycle(T instance) {
            if (instance instanceof Recyclable) {
                Recyclable recyclable = (Recyclable) instance;
                if (recyclable.isRecycled()) {
                    throw new IllegalStateException("Already recycled!");
                }
                recyclable.clearForRecycle();
            } else {
                if (isInPool(instance)) {
                    throw new IllegalStateException("Already in the pool!");
                }
            }
            if (mPoolSize < mPool.length) {
                mPool[mPoolSize] = instance;
                mPoolSize++;
                return true;
            }
            return false;
        }

        private boolean isInPool(T instance) {
            for (int i = 0; i < mPoolSize; i++) {
                if (mPool[i] == instance) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * (Synchronized) pool of objects.
     *
     * @param <T> The pooled type.
     */
    public static class SynchronizedPool<T> extends SimplePool<T> {
        private final Object mLock = new Object();

        /**
         * Creates a new instance.
         *
         * @param maxPoolSize The max pool size.
         * @throws IllegalArgumentException If the max pool size is less than zero.
         */
        public SynchronizedPool(int maxPoolSize) {
            super(maxPoolSize);
        }

        @Override
        public T obtain() {
            synchronized (mLock) {
                return super.obtain();
            }
        }

        @Override
        public boolean recycle(T element) {
            synchronized (mLock) {
                return super.recycle(element);
            }
        }
    }
}
