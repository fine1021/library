package com.yxkang.android.sample;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.yxkang.android.sample.bean.MessageEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashSet;

@SuppressWarnings({"unused", "ConstantConditions"})
public class MediaActivity extends AppCompatActivity {

    private static final String TAG = MediaActivity.class.getSimpleName();
    private final HashSet<String> mMediaTitle = new HashSet<>();
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

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onThreadMessageEvent(MessageEvent event) {
        switch (event.eventType) {
            case MessageEvent.LOAD_MEDIA_INFO:
                loadMediaInfo();
                break;
        }
    }

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

    private void loadMediaInfo() {
        initMediaTitle();
        ContentResolver resolver = getContentResolver();
        Cursor cursor = resolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                String display_name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                Log.i(TAG, display_name + "/" + title);
                if (isNeedModify(display_name) && !display_name.contains(title)) {
                    title = parseTitle(display_name);
                    AudioInfo info = new AudioInfo();
                    info.data = data;
                    info.title = title;
                    info.display_name = display_name;
                    audioInfos.add(info);
                }
            }
            cursor.close();
        }
        if (audioInfos.size() > 0) {
            modifyMediaInfo();
        } else {
            Log.d(TAG, "no need to modify media information");
        }
        EventBus.getDefault().post(new MessageEvent(MessageEvent.DISMISS_DIALOG));
    }

    private void initMediaTitle() {
        mMediaTitle.clear();
        mMediaTitle.add("안녕");             // 金孝琳 - 안녕.mp3
        mMediaTitle.add("ユメセカイ");        // 戸松遥 - ユメセカイ.mp3
        mMediaTitle.add("醉赤壁");           // 林俊杰 - 醉赤壁.mp3
        mMediaTitle.add("i miss you");      // 罗百吉 - i miss you(罗百吉 宝贝).mp3
        mMediaTitle.add("欧若拉");           //  张韶涵 - 欧若拉.mp3
        mMediaTitle.add("依恋");             // 蔡淳佳 - 依恋.mp3
        mMediaTitle.add("泡沫");             //  G.E.M. 邓紫棋 - 泡沫.mp3
        mMediaTitle.add("勇敢");             // BY2 - 勇敢.mp3
        mMediaTitle.add("我可以");           // 蔡旻佑 - 我可以.mp3
        mMediaTitle.add("背叛灵魂");         // 韩庚 - 背叛灵魂.mp3
        audioInfos.clear();
    }

    private boolean isNeedModify(String displayName) {
        for (String s : mMediaTitle) {
            if (displayName.contains(s)) {
                return true;
            }
        }
        return false;
    }

    private String parseTitle(String displayName) {
        int start = displayName.indexOf("-") + 1;
        int end = displayName.lastIndexOf(".");
        return displayName.substring(start, end).trim();
    }

    private void modifyMediaInfo() {
        ContentResolver resolver = getContentResolver();
        for (AudioInfo info : audioInfos) {
            String where = MediaStore.Audio.Media.DATA + "=? and " + MediaStore.Audio.Media.DISPLAY_NAME + "=?";
            String[] selectionArgs = new String[]{info.data, info.display_name};
            ContentValues values = new ContentValues();
            values.put(MediaStore.Audio.Media.TITLE, info.title);
            int updated = resolver.update(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values, where, selectionArgs);
            Log.i(TAG, "updated = " + updated);
        }
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
