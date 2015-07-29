package com.yxkang.android.sample.asynctask;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore.Audio;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.MediaColumns;
import android.provider.MediaStore.Video;
import android.util.Log;

import com.yxkang.android.sample.R;
import com.yxkang.android.sample.bean.FileInfoBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class LoadFilesTask extends AsyncTask<Void, String, List<FileInfoBean>> {

    private static final String TAG = "LoadFiles";
    private Context context;
    private List<FileInfoBean> list = new ArrayList<>();

    public LoadFilesTask(Context context) {
        super();
        this.context = context;
    }

    @Override
    protected List<FileInfoBean> doInBackground(Void... params) {

        ContentResolver resolver = context.getContentResolver();

        Uri uri = Video.Media.EXTERNAL_CONTENT_URI;
        Log.d(TAG, uri.toString());

        Cursor cursor = resolver.query(uri, null, null, null, MediaColumns.DATE_MODIFIED + " desc");

        if (cursor != null) {
            while (cursor.moveToNext()) {
                FileInfoBean info = new FileInfoBean();
                info.setId(cursor.getLong(cursor.getColumnIndex(MediaColumns._ID)));
                info.setAbsolutePath(cursor.getString(cursor.getColumnIndex(MediaColumns.DATA)));
                info.setDisplayName(cursor.getString(cursor.getColumnIndex(MediaColumns.DISPLAY_NAME)));
                info.setFileSize(cursor.getLong(cursor.getColumnIndex(MediaColumns.SIZE)));
                info.setFileTitle(cursor.getString(cursor.getColumnIndex(MediaColumns.TITLE)));
                info.setMimeType(cursor.getString(cursor.getColumnIndex(MediaColumns.MIME_TYPE)));
                info.setDateAdd(cursor.getLong(cursor.getColumnIndex(MediaColumns.DATE_ADDED)));
                info.setDateModified(cursor.getLong(cursor.getColumnIndex(MediaColumns.DATE_MODIFIED)));
                list.add(info);
            }
        }

        uri = Images.Media.EXTERNAL_CONTENT_URI;
        Log.d(TAG, uri.toString());


        cursor = resolver.query(uri, null, null, null, MediaColumns.DATE_MODIFIED + " desc");

        if (cursor != null) {
            while (cursor.moveToNext()) {
                FileInfoBean info = new FileInfoBean();
                info.setId(cursor.getLong(cursor.getColumnIndex(MediaColumns._ID)));
                info.setAbsolutePath(cursor.getString(cursor.getColumnIndex(MediaColumns.DATA)));
                info.setDisplayName(cursor.getString(cursor.getColumnIndex(MediaColumns.DISPLAY_NAME)));
                info.setFileSize(cursor.getLong(cursor.getColumnIndex(MediaColumns.SIZE)));
                info.setFileTitle(cursor.getString(cursor.getColumnIndex(MediaColumns.TITLE)));
                info.setMimeType(cursor.getString(cursor.getColumnIndex(MediaColumns.MIME_TYPE)));
                info.setDateAdd(cursor.getLong(cursor.getColumnIndex(MediaColumns.DATE_ADDED)));
                info.setDateModified(cursor.getLong(cursor.getColumnIndex(MediaColumns.DATE_MODIFIED)));
                list.add(info);
            }
        }

        uri = Audio.Media.EXTERNAL_CONTENT_URI;
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_action_audio);
        Drawable drawable = new BitmapDrawable(context.getResources(), bitmap);
        Log.d(TAG, uri.toString());

        cursor = resolver.query(uri, null, null, null, MediaColumns.DATE_MODIFIED + " desc");

        if (cursor != null) {
            while (cursor.moveToNext()) {
                FileInfoBean info = new FileInfoBean();
                info.setId(cursor.getLong(cursor.getColumnIndex(MediaColumns._ID)));
                info.setAbsolutePath(cursor.getString(cursor.getColumnIndex(MediaColumns.DATA)));
                info.setDisplayName(cursor.getString(cursor.getColumnIndex(MediaColumns.DISPLAY_NAME)));
                info.setFileSize(cursor.getLong(cursor.getColumnIndex(MediaColumns.SIZE)));
                info.setFileTitle(cursor.getString(cursor.getColumnIndex(MediaColumns.TITLE)));
                info.setMimeType(cursor.getString(cursor.getColumnIndex(MediaColumns.MIME_TYPE)));
                info.setDateAdd(cursor.getLong(cursor.getColumnIndex(MediaColumns.DATE_ADDED)));
                info.setDateModified(cursor.getLong(cursor.getColumnIndex(MediaColumns.DATE_MODIFIED)));
                info.setFileIcon(drawable);
                info.setUpdate(true);
                list.add(info);
            }
        }
        Collections.sort(list, new Comparator<Object>() {

            @Override
            public int compare(Object lhs, Object rhs) {
                FileInfoBean bean = (FileInfoBean) lhs;
                FileInfoBean bean2 = (FileInfoBean) rhs;
                return (int) (bean2.getDateModified() - bean.getDateModified());
            }

        });
        return list;
    }

}
