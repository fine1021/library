package com.yxkang.android.sample.util;

import com.yxkang.android.util.Pools;

/**
 * Created by fine on 2017/2/15.
 */

public class SimpleRecycler extends Pools.SimpleRecyclable {

    private static final Pools.SynchronizedPool<SimpleRecycler> sPool = new Pools.SynchronizedPool<>(10);

    public static SimpleRecycler obtain() {
        SimpleRecycler simpleRecycler = sPool.obtain();
        return simpleRecycler != null ? simpleRecycler : new SimpleRecycler();
    }

    @Override
    public void recycle() {
        sPool.recycle(this);
    }

}
