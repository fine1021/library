package com.yxkang.android.sample.service;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 */
public class MediaModifyService extends IntentService {

    private final ArrayList<AudioInfo> audioInfos = new ArrayList<>();
    private static final String TAG = MediaModifyService.class.getSimpleName();

    private static final Logger logger = LoggerFactory.getLogger(MediaModifyService.class);

    public MediaModifyService() {
        super("MediaModifyService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            loadMediaInfo();
        }
    }

    private void loadMediaInfo() {
        ContentResolver resolver = getContentResolver();
        Cursor cursor = resolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                String display_name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                Log.i(TAG, "loadMediaInfo: " + display_name + "/" + title);
                if ((!display_name.contains(title) || (title.contains(";") | title.contains("[") | title.contains("]")))) {
                    String _title = parseTitle(display_name);
                    if (!_title.equals(title)) {
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                            logger.debug("{}/{}", display_name, title);
                        } else {
                            Log.d(TAG, "loadMediaInfo: " + display_name + "/" + title);
                        }
                        title = _title;
                        AudioInfo info = new AudioInfo();
                        info.data = data;
                        info.title = title;
                        info.display_name = display_name;
                        audioInfos.add(info);
                    }
                }
            }
            cursor.close();
        }
        if (audioInfos.size() > 0) {
            modifyMediaInfo();
        } else {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                logger.info("no need to modify media information");
            } else {
                Log.v(TAG, "no need to modify media information");
            }
        }
    }

    private String parseTitle(String displayName) {
        int end = displayName.lastIndexOf("[");
        if (end == -1) {
            end = displayName.lastIndexOf(".");
        }
        return displayName.substring(0, end).trim();
    }

    private void modifyMediaInfo() {
        ContentResolver resolver = getContentResolver();
        for (AudioInfo info : audioInfos) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                logger.info("{}/{}", info.display_name, info.title);
            } else {
                Log.i(TAG, "modifyMediaInfo: " + info.display_name + "/" + info.title);
            }
            String where = MediaStore.Audio.Media.DATA + "=? and " + MediaStore.Audio.Media.DISPLAY_NAME + "=?";
            String[] selectionArgs = new String[]{info.data, info.display_name};
            ContentValues values = new ContentValues();
            values.put(MediaStore.Audio.Media.TITLE, info.title);
            int updated = resolver.update(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values, where, selectionArgs);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                logger.debug("updated = {}", updated);
            } else {
                Log.d(TAG, "modifyMediaInfo: updated = " + updated);
            }
        }
        audioInfos.clear();
    }

    private static class AudioInfo {
        String data;
        String display_name;
        String title;
    }
}
