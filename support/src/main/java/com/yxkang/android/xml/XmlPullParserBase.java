package com.yxkang.android.xml;

import android.text.TextUtils;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;


public abstract class XmlPullParserBase {

    private static final String charset = "utf-8";
    private static final String TAG = "XmlPullParserBase";
    private XmlPullParser parser = null;

    public XmlPullParserBase() {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            parser = factory.newPullParser();
            Log.i(TAG, "XmlPullParser Init Success !");
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            Log.e(TAG, "XmlPullParser Init Failed !");
        }
    }

    /**
     * Sets the input stream the parser is going to process.
     * This call resets the parser state and sets the event type
     * to the initial value START_DOCUMENT.
     * This is a convenient Method to invoke, with the default encoding format "utf-8"
     *
     * @param inputStream contains a raw byte input stream of possibly
     *                    unknown encoding (when inputEncoding is null).
     * @throws XmlPullParserException XmlPullParserException
     * @see #parse(InputStream, String)
     */
    public final void parse(InputStream inputStream) throws XmlPullParserException {
        parse(inputStream, null);
    }

    /**
     * Sets the input stream the parser is going to process.
     * This call resets the parser state and sets the event type
     * to the initial value START_DOCUMENT.
     * <br>
     * <p><strong>NOTE:</strong> If an input encoding string is passed,
     * it MUST be used. Otherwise,
     * if inputEncoding is null, the parser SHOULD try to determine
     * input encoding following XML 1.0 specification (see below).
     * If encoding detection is supported then following feature
     * <a href="http://xmlpull.org/v1/doc/features.html#detect-encoding">http://xmlpull.org/v1/doc/features.html#detect-encoding</a>
     * MUST be true amd otherwise it must be false
     *
     * @param inputStream   contains a raw byte input stream of possibly
     *                      unknown encoding (when inputEncoding is null).
     * @param inputEncoding if not null it MUST be used as encoding for inputStream
     * @throws XmlPullParserException XmlPullParserException
     */
    public final void parse(InputStream inputStream, String inputEncoding) throws XmlPullParserException {
        if (TextUtils.isEmpty(inputEncoding)) {
            parser.setInput(inputStream, charset);
        } else {
            parser.setInput(inputStream, inputEncoding);
        }
        getContent();
    }

    /**
     * Set the input source for parser to the given string and
     * resets the parser. The event type is set to the initial value
     * START_DOCUMENT.
     * This Method will transform string to stringReader,then call {@link #parse(Reader)} to handle
     *
     * @param xml a xml string
     * @throws XmlPullParserException XmlPullParserException
     * @see #parse(Reader)
     */
    public final void parse(String xml) throws XmlPullParserException {
        parse(new StringReader(xml));
    }

    /**
     * Set the input source for parser to the given reader and
     * resets the parser. The event type is set to the initial value
     * START_DOCUMENT.
     * Setting the reader to null will just stop parsing and
     * reset parser state,
     * allowing the parser to free internal resources
     * such as parsing buffers.
     *
     * @param reader reader
     * @throws XmlPullParserException XmlPullParserException
     */
    public final void parse(Reader reader) throws XmlPullParserException {
        parser.setInput(reader);
        getContent();
    }

    /**
     * get the current {@code parser}
     *
     * @return parser
     */
    protected XmlPullParser getParser() {
        return parser;
    }

    /**
     * Prior to API level 14, the pull parser returned by {@code android.util.Xml}
     * did not always advance to the END_TAG event when {@link XmlPullParser#nextText()}
     * method was called.
     * <br>
     * This is a safety method to get the nextText.
     *
     * @return element content or empty string
     * @throws XmlPullParserException XmlPullParserException
     * @throws IOException            IOException
     */
    protected String safetyNextText() throws XmlPullParserException, IOException {
        String result = parser.nextText();
        if (parser.getEventType() != XmlPullParser.END_TAG) {
            parser.nextTag();
        }
        return result;
    }

    /**
     * Implement custom parse
     */
    protected abstract void getContent();
}
