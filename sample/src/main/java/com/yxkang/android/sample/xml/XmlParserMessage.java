package com.yxkang.android.sample.xml;

import android.text.TextUtils;
import android.util.Log;

import com.yxkang.android.xml.XmlPullParserBase;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * Created by yexiaokang on 2015/8/21.
 */
public class XmlParserMessage extends XmlPullParserBase {

    private static final String TAG = "XmlParserMessage";

    @Override
    protected void getContent() {
        int eventType;
        String title;
        String content;

        try {
            eventType = getParser().getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {

                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        Log.d(TAG, "START_DOCUMENT");
                        break;
                    case XmlPullParser.START_TAG:
                        title = getParser().getName();
                        Log.d(TAG, title + " START_TAG");
                        int count = getParser().getAttributeCount();
                        for (int i = 0; i < count; i++) {
                            Log.d(TAG, getParser().getAttributeName(i) + " : " + getParser().getAttributeValue(i));
                        }

                        if (title.equals("ns:return")) {
                            content = safetyNextText();
                            Log.d(TAG, title + " : " + content);
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        title = getParser().getName();
                        Log.d(TAG, title + " END_TAG");
                        break;
                    case XmlPullParser.TEXT:
                        content = getParser().getText().trim();
                        if (!TextUtils.isEmpty(content)) {
                            Log.d(TAG, "TEXT : " + content);
                        }
                        break;
                    case XmlPullParser.COMMENT:
                        break;
                    default:
                        break;
                }
                eventType = getParser().next();
            }

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
