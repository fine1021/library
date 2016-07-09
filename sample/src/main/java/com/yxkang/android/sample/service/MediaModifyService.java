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
        // 효린 (孝琳) - 안녕 (再见).flac
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
        // 정수정 (郑秀晶) - 울컥 (呜咽).mp3
        mMediaTitle.add("울컥");
        // 藍井エイル (蓝井艾露) - MEMORIA (记忆).flac
        mMediaTitle.add("MEMORIA");
        // 거미 (Gummy) - You Are My Everything (Korean Ver.).flac
        mMediaTitle.add("You Are My Everything");
        // SISTAR (씨스타) - 나혼자 (我一个人).mp3
        mMediaTitle.add("나혼자");
        // 전효성 (全孝盛)디액션 (D.Action) - 나를 찾아줘 (来找我吧).flac
        mMediaTitle.add("나를 찾아줘");
        // AOA (에이오에이) - 심쿵해 (怦然心动) (Korean Ver.).flac
        mMediaTitle.add("심쿵해");
        // 타이거 JK (Tiger JK)진실 (Jinsil) - Reset.mp3
        mMediaTitle.add("Reset");
        // 윤하 (Younha) - 기도 (祈祷).flac
        mMediaTitle.add("기도");
        // 스텔라 (Stellar) - 마리오네트 (提线木偶).flac
        mMediaTitle.add("마리오네트");
        // 阿悄 - 海海海.mp3
        mMediaTitle.add("海海海");
        // 윤하 (Younha) - 별에서 온 그대 (来自星星的你).flac
        mMediaTitle.add("별에서 온 그대");
        // Girl's Day (걸스데이) - Something.flac
        mMediaTitle.add("Something");
        // 현아 (泫雅)용준형 (龙俊亨) - Change.flac
        mMediaTitle.add("Change");
        // 백지영 (白智英)치타 (CHEETAH) - 사랑이 온다 (爱情来了).flac
        mMediaTitle.add("사랑이 온다");
        // Girl's Day (걸스데이) - 기대해 (期待).mp3
        mMediaTitle.add("기대해");
        // T-ara (티아라) - Roly-Poly (不倒翁).flac
        mMediaTitle.add("Roly-Poly");
        // 宮崎歩 - brave heart (勇敢的心).mp3
        mMediaTitle.add("brave heart");
        // 나비 (NAVI) - 거짓말이길 바랬어 (希望这是谎言).flac
        mMediaTitle.add("거짓말이길 바랬어");
        // T-ara (티아라) - 떠나지마 (不要离开).flac
        mMediaTitle.add("떠나지마");
        // T-ara (티아라) - HOLIDAY (假期).flac
        mMediaTitle.add("HOLIDAY");
        // 린 (LYn) - My Destiny.flac
        mMediaTitle.add("My Destiny");
        // 网络歌手 - La La Love on My Mind.mp3
        mMediaTitle.add("La La Love on My Mind");
        // 胡彦斌 - 月光.mp3
        mMediaTitle.add("月光");
        // 아이유 (IU) - 마음 (心情).flac
        mMediaTitle.add("마음");
        // BY2 - 不够成熟.flac
        mMediaTitle.add("不够成熟");
        // T-ara (티아라) - 넘버나인 (No.9) (Number 9).mp3
        mMediaTitle.add("넘버나인");
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
        int start = displayName.indexOf(" - ") + 3;
        if (start == -1) {
            start = displayName.indexOf("-") + 1;
        }
        int end = displayName.lastIndexOf("[");
        if (end == -1) {
            end = displayName.lastIndexOf(".");
        }
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
