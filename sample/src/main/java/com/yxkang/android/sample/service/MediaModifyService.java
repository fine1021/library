package com.yxkang.android.sample.service;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 */
public class MediaModifyService extends IntentService {

    private final HashSet<String> mMediaTitle = new HashSet<>();
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
        initMediaTitle();
        ContentResolver resolver = getContentResolver();
        Cursor cursor = resolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                String display_name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                Log.i(TAG, display_name + "/" + title);
                if (isNeedModify(display_name) || (!display_name.contains(title) && title.contains(";"))) {
                    String _title = parseTitle(display_name);
                    if (!_title.equals(title)) {
                        logger.warn("{}/{}", display_name, title);
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
            logger.info("no need to modify media information");
        }
    }

    private void initMediaTitle() {
        mMediaTitle.clear();
        // 金孝琳 - 안녕.mp3
        mMediaTitle.add("안녕");
        // 戸松遥 - ユメセカイ.mp3
        mMediaTitle.add("ユメセカイ");
        // 林俊杰 - 醉赤壁.mp3
        mMediaTitle.add("醉赤壁");
        // 罗百吉 - i miss you(罗百吉 宝贝).mp3
        mMediaTitle.add("i miss you");
        // 张韶涵 - 欧若拉.mp3
        mMediaTitle.add("欧若拉");
        // 蔡淳佳 - 依恋.mp3
        mMediaTitle.add("依恋");
        // G.E.M. 邓紫棋 - 泡沫.mp3
        mMediaTitle.add("泡沫");
        // BY2 - 勇敢.mp3
        mMediaTitle.add("勇敢");
        // 蔡旻佑 - 我可以.mp3
        mMediaTitle.add("我可以");
        // 韩庚 - 背叛灵魂.mp3
        mMediaTitle.add("背叛灵魂");
        // 萧亚轩 - 爱的主打歌.flac
        mMediaTitle.add("爱的主打歌");
        // 庄心妍 - 繁星点点.ape
        mMediaTitle.add("繁星点点");
        // 萧亚轩 - 类似爱情.flac
        mMediaTitle.add("类似爱情");
        // 零点乐队 - 相信自己.mp3
        mMediaTitle.add("相信自己");
        // 丁当 - 我爱他.flac
        mMediaTitle.add("我爱他");
        // 张靓颖 - 我们说好的.flac
        mMediaTitle.add("我们说好的");
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
            logger.info("{}/{}", info.display_name, info.title);
            String where = MediaStore.Audio.Media.DATA + "=? and " + MediaStore.Audio.Media.DISPLAY_NAME + "=?";
            String[] selectionArgs = new String[]{info.data, info.display_name};
            ContentValues values = new ContentValues();
            values.put(MediaStore.Audio.Media.TITLE, info.title);
            int updated = resolver.update(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values, where, selectionArgs);
            logger.debug("updated = {}", updated);
        }
    }

    private static class AudioInfo {
        String data;
        String display_name;
        String title;
    }
}
