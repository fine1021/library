package com.yxkang.android.image.core;

import java.util.Locale;

/**
 * ImageProtocol
 */
@SuppressWarnings("ALL")
public enum ImageProtocol {

    HTTP("http"), HTTPS("https"), FILE("file"), CONTENT("content");

    private String protocol;
    private String uriPrefix;

    ImageProtocol(String protocol) {
        this.protocol = protocol;
        this.uriPrefix = protocol + "://";
    }

    public boolean belongsTo(String uri) {
        return uri.toLowerCase(Locale.CHINA).startsWith(uriPrefix);
    }

    /**
     * Appends scheme to incoming path
     *
     * @param path the path
     * @return the path with the uri prefix
     */
    public String wrap(String path) {
        return uriPrefix + path;
    }


    /**
     * Removed scheme part ("scheme://") from incoming URI
     *
     * @param uri the incoming URI
     * @return the path without the uri prefix
     */
    public String crop(String uri) {
        if (!belongsTo(uri)) {
            throw new IllegalArgumentException(
                    String.format("URI [%1$s] doesn't have expected protocol [%2$s]", uri, protocol));
        }
        return uri.substring(uriPrefix.length());
    }
}
