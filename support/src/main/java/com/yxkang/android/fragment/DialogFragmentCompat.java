package com.yxkang.android.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.IntDef;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by fine on 2016/9/11.
 */
public class DialogFragmentCompat extends DialogFragment {

    private static final String TAG = "DialogFragmentCompat";

    @IntDef({LAYOUT_NONE, LAYOUT_FULL, LAYOUT_HALF})
    @Retention(RetentionPolicy.SOURCE)
    public @interface DialogLayout {
    }

    public static final int LAYOUT_NONE = 0;
    public static final int LAYOUT_FULL = 1;
    public static final int LAYOUT_HALF = 2;

    private int dialogGravity = Gravity.CENTER;
    private int dialogLayout = LAYOUT_NONE;

    @CallSuper
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (!getShowsDialog()) {
            return;
        }

        View view = getView();
        if (view != null) {
            Dialog dialog = getDialog();
            if (dialog instanceof AlertDialog) {
                ((AlertDialog) dialog).setView(view);
            } else if (dialog instanceof android.app.AlertDialog) {
                ((android.app.AlertDialog) dialog).setView(view);
            } else {
                Log.v(TAG, "onActivityCreated: " + dialog.getClass().getName());
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        setupDialog();
    }


    /**
     * get the dialog gravity
     *
     * @return the gravity
     */
    public int getDialogGravity() {
        return dialogGravity;
    }

    /**
     * set the dialog gravity
     *
     * @param dialogGravity such as {@link Gravity#CENTER}, and so on
     */
    public void setDialogGravity(int dialogGravity) {
        this.dialogGravity = dialogGravity;
    }

    /**
     * get the dialog layout, default value is {@link #LAYOUT_NONE}, this means that keep the original layout status
     *
     * @return the dialog layout
     */
    @DialogLayout
    public int getDialogLayout() {
        return dialogLayout;
    }

    /**
     * set the dialog layout, this is used for height adjust, the width is always full screen width
     *
     * @param dialogLayout such as {@link #LAYOUT_NONE}, {@link #LAYOUT_FULL}, {@link #LAYOUT_HALF}
     */
    public void setDialogLayout(@DialogLayout int dialogLayout) {
        this.dialogLayout = dialogLayout;
    }

    protected void setupDialog() {
        final int layout = getDialogLayout();
        if (layout != LAYOUT_NONE) {
            DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
            getDialog().getWindow().setLayout(displayMetrics.widthPixels, displayMetrics.heightPixels / layout);
        }
        WindowManager.LayoutParams layoutParams = getDialog().getWindow().getAttributes();
        layoutParams.gravity = getDialogGravity();
        getDialog().getWindow().setAttributes(layoutParams);
    }
}
