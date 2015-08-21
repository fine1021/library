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

        try {
            eventType = getParser().getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {

                String title;
                String content;

                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        title = getParser().getName();
                        Log.d(TAG, title + " START_TAG");
                        int count = getParser().getAttributeCount();
                        for (int i = 0; i < count; i++) {
                            Log.d(TAG, getParser().getAttributeName(i) + " : " + getParser().getAttributeValue(i));
                        }
                        content = safetyNextText();
                        if (!TextUtils.isEmpty(content.trim())) {
                            Log.d(TAG, title + " : " + content);
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        title = getParser().getName();
                        Log.d(TAG, title + " END_TAG");
                        break;
                    case XmlPullParser.TEXT:
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
