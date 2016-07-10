package com.yxkang.android.sample;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.yxkang.android.sample.bean.MessageEvent;
import com.yxkang.android.sample.media.MediaScannerService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

@SuppressWarnings("ConstantConditions")
public class MediaActivity extends AppCompatActivity {

    private static final String TAG = MediaActivity.class.getSimpleName();
    private final ArrayList<AudioInfo> audioInfos = new ArrayList<>();
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media);
        EventBus.getDefault().register(this);
        findViewById(R.id.bt_am_modify).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new MessageEvent(MessageEvent.SHOW_DIALOG));
                EventBus.getDefault().post(new MessageEvent(MessageEvent.LOAD_MEDIA_INFO));
            }
        });
        findViewById(R.id.bt_am_scan_qq_music).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanQQMusicFiles();
            }
        });
    }

    private synchronized void showProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.waiting_please));
        progressDialog.show();
    }

    private synchronized void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onThreadMessageEvent(MessageEvent event) {
        switch (event.eventType) {
            case MessageEvent.LOAD_MEDIA_INFO:
                loadMediaInfo();
                break;
        }
    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        switch (event.eventType) {
            case MessageEvent.SHOW_DIALOG:
                showProgressDialog();
                break;
            case MessageEvent.DISMISS_DIALOG:
                dismissProgressDialog();
                break;
        }
    }

    private void scanQQMusicFiles() {
        String root = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/com.tencent.qqmusic/files/qqmusic/song";
        Intent service = new Intent(this, MediaScannerService.class);
        service.putExtra(MediaScannerService.EXTRA_SCAN_TYPE, MediaScannerService.SCAN_DIR);
        service.putExtra(MediaScannerService.EXTRA_SCAN_PATH, root);
        startService(service);
        Toast.makeText(MediaActivity.this, "Start Scan Files", Toast.LENGTH_SHORT).show();
    }

    private void loadMediaInfo() {
        ContentResolver resolver = getContentResolver();
        Cursor cursor = resolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                String display_name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                Log.i(TAG, display_name + "/" + title);
                if ((!display_name.contains(title) || (title.contains(";") | title.contains("[") | title.contains("]")))) {
                    String _title = parseTitle(display_name);
                    if (!_title.equals(title)) {
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
            Log.v(TAG, "no need to modify media information");
        }
        EventBus.getDefault().post(new MessageEvent(MessageEvent.DISMISS_DIALOG));
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
            Log.i(TAG, info.display_name + "/" + info.title);
            String where = MediaStore.Audio.Media.DATA + "=? and " + MediaStore.Audio.Media.DISPLAY_NAME + "=?";
            String[] selectionArgs = new String[]{info.data, info.display_name};
            ContentValues values = new ContentValues();
            values.put(MediaStore.Audio.Media.TITLE, info.title);
            int updated = resolver.update(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values, where, selectionArgs);
            Log.d(TAG, "updated = " + updated);
        }
        audioInfos.clear();
    }

    private static class AudioInfo {
        String data;
        String display_name;
        String title;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
