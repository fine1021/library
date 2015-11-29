package com.yxkang.android.xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * Created by fine on 2015/11/26.
 */
public abstract class XmlMessageParser extends XmlPullParserBase {

    @Override
    protected void getContent() {
        String tagName;
        try {
            int eventType = getParser().getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        onDocumentStart();
                        break;
                    case XmlPullParser.START_TAG:
                        tagName = getParser().getName();
                        process(tagName);
                        break;
                    case XmlPullParser.END_TAG:
                        tagName = getParser().getName();
                        if (isXmlHead(tagName)) {
                            onHeadTagEnd(tagName);
                        }
                        break;
                    case XmlPullParser.TEXT:
                        onTextStart();
                        break;
                }
                eventType = getParser().next();
            }
            onDocumentEnd();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void process(String tagName) throws IOException, XmlPullParserException {
        if (isXmlHead(tagName)) {
            onHeadTagStart(tagName);
            return;
        }
        String tagValue = getParser().nextText();
        onContentTag(tagName, tagValue);
    }

    protected void onDocumentStart() {
    }

    protected void onHeadTagStart(String tagName) {
    }

    protected void onHeadTagEnd(String tagName) {
    }

    protected void onContentTag(String tagName, String tagValue) {
    }

    protected void onTextStart() {
    }

    protected void onDocumentEnd() {
    }

    /**
     * if the tag is a head, it means that it doesn't have a direct {@code TEXT}.
     * <p><strong>Note:</strong> a empty tag (e.g. &lt;foobar/&gt;) is not the xml head tag, it will call
     * {@link #onContentTag(String, String)} method to return the {@code tagName} and a empty string.
     *
     * @param tagName tagName
     * @return {@code true} if the tag is a head, otherwise {@code false}
     */
    protected abstract boolean isXmlHead(String tagName);
}
