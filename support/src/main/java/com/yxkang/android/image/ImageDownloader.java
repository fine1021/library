package com.yxkang.android.image;

import java.util.Locale;

public interface ImageDownloader {

    enum Protocol {

        HTTP("http"), HTTPS("https"), FILE("file"), CONTENT("content");

        private String protocol;
        private String uriPrefix;

        Protocol(String protocol) {
            this.protocol = protocol;
            uriPrefix = protocol + "://";
        }

        public boolean belongsTo(String uri) {
            return uri.toLowerCase(Locale.CHINA).startsWith(uriPrefix);
        }

        /**
         * Appends scheme to incoming path
         */
        public String wrap(String path) {
            return uriPrefix + path;
        }


        /**
         * Removed scheme part ("scheme://") from incoming URI
         */
        public String crop(String uri) {
            if (!belongsTo(uri)) {
                throw new IllegalArgumentException(
                        String.format("URI [%1$s] doesn't have expected protocol [%2$s]", uri, protocol));
            }
            return uri.substring(uriPrefix.length());
        }

    }
}
