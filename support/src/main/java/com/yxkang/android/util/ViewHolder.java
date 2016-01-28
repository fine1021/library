package com.yxkang.android.util;

import android.util.SparseArray;
import android.view.View;

import java.lang.ref.WeakReference;

/**
 * Created by yexiaokang on 2016/1/28.
 */
@SuppressWarnings({"unchecked", "unused"})
public class ViewHolder {

    public static <T extends View> T getView(View convertView, int id) {
        SparseArray<View> viewHolder = (SparseArray<View>) convertView.getTag();
        if (viewHolder == null) {
            viewHolder = new SparseArray<>();
            convertView.setTag(viewHolder);
        }
        View childView = viewHolder.get(id);
        if (childView == null) {
            childView = convertView.findViewById(id);
            viewHolder.put(id, childView);
        }
        return (T) childView;
    }

    public static <T extends View> T getWeakReferenceView(View convertView, int id) {
        SparseArray<WeakReference<View>> viewHolder = (SparseArray<WeakReference<View>>) convertView.getTag();
        if (viewHolder == null) {
            viewHolder = new SparseArray<>();
            convertView.setTag(viewHolder);
        }
        WeakReference<View> childView = viewHolder.get(id);
        if (childView.get() == null) {
            childView = new WeakReference<>(convertView.findViewById(id));
            viewHolder.put(id, childView);
        }
        return (T) childView.get();
    }
}
