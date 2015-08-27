package com.yxkang.android.os;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * <p>WeakReferenceHandler class is designed to solve memory leak problem.</p>
 * <p/>
 * <h2>Problem</h2>
 * <p>where exactly is the leak and how might it happen?
 * Let's determine the source of the problem by first documenting what we know:</p>
 * <ol>
 * <li>When an Android application first starts,
 * the framework creates a <code>Looper</code> object for the application's main thread.
 * A <code>Looper</code> implements a simple message queue, processing <code>Message</code> objects in a loop one after another.
 * All major application framework events (such as <code>Activity</code> lifecycle method calls, button clicks, etc.)
 * are contained inside <code>Message</code> objects, which are added to the Looper's message queue and are processed one-by-one.
 * The main thread's Looper exists throughout the application's lifecycle.</li>
 * <li>When a <code>Handler</code> is instantiated on the main thread, it is associated with the Looper's message queue.
 * <code>Message</code> posted to the message queue will hold a reference to the Handler so that
 * the framework can call {@link #handleMessage(Message)} when the <code>Handler</code> eventually processes the message.</li>
 * <li>In Java, non-static inner and anonymous classes hold an implicit reference to their outer class. Static inner classes, on the other hand, do not.</li>
 * </ol>
 * <p/>
 * <h2>Usage</h2>
 * <p>WeakReferenceHandler must be subclassed to be used. The subclass will override at least
 * one method ({@link #handleMessage(Object, Message)}).</p>
 * <p/>
 * <p>Here is an example of subclassing:</p>
 * <pre class="prettyprint">
 * private static class MyHandler extends WeakReferenceHandler&lt;MainActivity&gt; {
 * <p/>
 * &nbsp;&nbsp;public MyHandler(MainActivity reference) {
 * &nbsp;&nbsp;&nbsp;&nbsp;super(reference);
 * &nbsp;&nbsp;}
 * <p/>
 * &nbsp;&nbsp;<code>@Override</code>
 * &nbsp;&nbsp;protected void handleMessage(MainActivity reference, Message msg) {
 * &nbsp;&nbsp;&nbsp;&nbsp;// call the MainActivity methods to do some things
 * &nbsp;&nbsp;}
 * }
 * </pre>
 * <p/>
 * <p/>
 * <p>Once created, the usage is very simply, do the following things in your <code>Activity</code>: </p>
 * <pre class="prettyprint">
 * private final MyHandler mHandler = new MyHandler(this);
 * </pre>
 * <h2>WeakReferenceHandler's generic types</h2>
 * <p>The one types used by a WeakReferenceHandler is the following:</p>
 * <ol>
 * <li><code>T</code>, generic model, maybe Activity or other object</li>
 * </ol>
 * <p>To be more safe, you had better clear the message sent by your Handler: </p>
 * <pre>
 * <code>@Override</code>
 * protected void onDestroy() {
 * &nbsp;&nbsp;super.onDestroy();
 * &nbsp;&nbsp;mHandler.removeCallbacksAndMessages(null);
 * }
 * </pre>
 */
public abstract class WeakReferenceHandler<T> extends Handler {

    private WeakReference<T> mReference;

    public WeakReferenceHandler(T reference) {
        mReference = new WeakReference<>(reference);
    }

    @Override
    public void handleMessage(Message msg) {
        if (mReference.get() != null) {
            handleMessage(mReference.get(), msg);
        }
    }

    /**
     * <p>an abstract method for subclass to handle the msg</p>
     * <p>
     * <strong>NOTE:</strong> the weakReference object is always not null,
     * so you can do as you want, no need to worry about {@link NullPointerException}
     * </p>
     *
     * @param reference WeakReference object
     * @param msg       the message
     */
    protected abstract void handleMessage(T reference, Message msg);
}
