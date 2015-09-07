package com.yxkang.android.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.widget.ArrayAdapter;

import java.util.Collection;
import java.util.List;

/**
 * based on {@link ArrayAdapter}, Compatible with Android below {@link android.os.Build.VERSION_CODES#HONEYCOMB}
 */
@SuppressWarnings("ALL")
public class ArrayAdapterCompat<T> extends ArrayAdapter<T> {

    /**
     * Constructor
     *
     * @param context            The current context.
     * @param textViewResourceId The resource ID for a layout file containing a TextView to use
     *                           when instantiating views.
     */
    public ArrayAdapterCompat(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    /**
     * Constructor
     *
     * @param context            The current context.
     * @param resource           The resource ID for a layout file containing a layout to use
     *                           when instantiating views.
     * @param textViewResourceId The id of the TextView within the layout resource to be
     *                           populated
     */
    public ArrayAdapterCompat(Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
    }

    /**
     * Constructor
     *
     * @param context            The current context.
     * @param textViewResourceId The resource ID for a layout file containing a TextView to use
     *                           when instantiating views.
     * @param objects            The objects to represent in the ListView.
     */
    public ArrayAdapterCompat(Context context, int textViewResourceId, T[] objects) {
        super(context, textViewResourceId, objects);
    }

    /**
     * Constructor
     *
     * @param context            The current context.
     * @param textViewResourceId The resource ID for a layout file containing a TextView to use
     *                           when instantiating views.
     * @param objects            The objects to represent in the ListView.
     */
    public ArrayAdapterCompat(Context context, int textViewResourceId, List<T> objects) {
        super(context, textViewResourceId, objects);
    }

    /**
     * Constructor
     *
     * @param context            The current context.
     * @param resource           The resource ID for a layout file containing a layout to use
     *                           when instantiating views.
     * @param textViewResourceId The id of the TextView within the layout resource to be
     *                           populated
     * @param objects            The objects to represent in the ListView.
     */
    public ArrayAdapterCompat(Context context, int resource, int textViewResourceId, T[] objects) {
        super(context, resource, textViewResourceId, objects);
    }

    /**
     * Constructor
     *
     * @param context            The current context.
     * @param resource           The resource ID for a layout file containing a layout to use
     *                           when instantiating views.
     * @param textViewResourceId The id of the TextView within the layout resource to be
     *                           populated
     * @param objects            The objects to represent in the ListView.
     */
    public ArrayAdapterCompat(Context context, int resource, int textViewResourceId, List<T> objects) {
        super(context, resource, textViewResourceId, objects);
    }

    /**
     * Adds the specified Collection at the end of the array.
     *
     * @param collection The Collection to add at the end of the array.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void addAll(Collection<? extends T> collection) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            super.addAll(collection);
        } else {
            for (T object : collection) {
                super.add(object);
            }
        }
    }

    /**
     * Adds the specified items at the end of the array.
     *
     * @param items The items to add at the end of the array.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void addAll(T... items) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            super.addAll(items);
        } else {
            for (T object : items) {
                super.add(object);
            }
        }

    }

    /**
     * Set the adpter data, this method will clear the old data.
     *
     * @param collection The Collection to add at the end of the array.
     * @see #addAll(Collection)
     */
    public void setData(Collection<? extends T> collection) {
        clear();
        addAll(collection);
    }

    /**
     * Set the adpter data, this method will clear the old data.
     *
     * @param items The items to add at the end of the array.
     * @see #addAll(Object[])
     */
    public void setData(T... items) {
        clear();
        addAll(items);
    }

}
