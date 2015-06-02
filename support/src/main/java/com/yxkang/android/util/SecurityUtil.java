package com.yxkang.android.util;

import android.util.Base64;

/**
 * Created by fine on 2015/6/17.
 */
public class SecurityUtil {


    /**
     * base64 encrypt
     *
     * @param str a string which is to be encrypted
     * @return a encrypted string
     */
    public static String encryptBase64(String str) {
        return new String(Base64.encode(str.getBytes(), 0));
    }

    /**
     * base64 decrypt
     *
     * @param str a string which is to be decrypted
     * @return decrypted string
     */
    public static String decryptBase64(String str) {
        return new String(Base64.decode(str, 0));
    }
}
