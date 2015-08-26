package com.yxkang.android.util;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * a class to scan the password of the wifi which has connected
 */
public class WifiPassword {

    private static final String TAG = WifiPassword.class.getSimpleName();
    private static final String SOURCE_PATH = "/data/misc/wifi/wpa_supplicant.conf";
    private static final String PWD_FILE = "wpa_supplicant.conf";
    private static final String DIR = Environment.getExternalStorageDirectory().getAbsolutePath();
    private static final String LOCAL_PATH = DIR + File.separator + PWD_FILE;
    private static final String PWD_TAG = "network";
    private static final String MID = "=";
    private ArrayList<Password> list = new ArrayList<>();


    public ArrayList<Password> getWifiPwds() throws IOException {

//        String cmd = "cp " + SOURCE_PATH + " " + DIR;                   //  linux cp command
        String cmd = "cat " + SOURCE_PATH + " > " + LOCAL_PATH;           //  linux cat command
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Log.e(TAG, "sdcard don't have read/write access!");
            return list;
        }
        if (!RootUtil.exeRootCommand(cmd)) {
            Log.e(TAG, "phone have not been rooted!");
            return list;
        }
        RootUtil.exeRootCommand("ls -l " + LOCAL_PATH);
        File file = new File(LOCAL_PATH);
        if (!file.exists()) {
            Log.e(TAG, "file is not exists!");
            return list;
        }
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        String line, prefix, suffix;
        while ((line = br.readLine()) != null) {
            line = line.trim();
            if (TextUtils.isEmpty(line) || line.startsWith("#")) continue;
            Log.d(TAG, line);
            if (line.startsWith("}")) {
                continue;
            }
            prefix = getPrefix(line);
            if (prefix.startsWith(PWD_TAG)) {
                Password password = new Password();
                line = br.readLine().trim();
                while (!line.startsWith("}")) {
                    Log.d(TAG, line);
                    prefix = getPrefix(line);
                    suffix = getSuffix(line);

                    switch (prefix) {
                        case Password.SSID:
                            password.ssid = suffix;
                            break;
                        case Password.PSK:
                            password.psk = suffix;
                            break;
                        case Password.KEY_MGMT:
                            password.key_mgmt = suffix;
                            break;
                        case Password.PRIORITY:
                            password.priority = suffix;
                            break;
                    }
                    line = br.readLine().trim();
                }
                Log.d(TAG, line);
                list.add(password);
            }
        }
        fr.close();
        br.close();
        if (file.exists()) {
//            boolean result = RootUtil.exeRootCommand("rm " + LOCAL_PATH);
            boolean result = file.delete();     // chmod 0777 file
            Log.d(TAG, "delete file " + result);
        }
        return list;
    }

    private String getPrefix(String str) {
        int index = str.indexOf(MID);
        String prefix = null;
        if (index > 0) {
            prefix = str.substring(0, index);
        }
        return prefix;
    }

    private String getSuffix(String str) {
        int index = str.indexOf(MID);
        String suffix = null;
        if (index > 0) {
            suffix = str.substring(index + 1);
        }
        return suffix;
    }

    /**
     * Wifi Password
     */
    public class Password {

        public static final String SSID = "ssid";
        public static final String PSK = "psk";
        public static final String KEY_MGMT = "key_mgmt";
        public static final String PRIORITY = "priority";

        public String ssid;
        public String psk;
        public String key_mgmt;
        public String priority;

        @Override
        public String toString() {
            return "ssid : " + ssid + "\npsk : " + psk + "\n";
        }
    }
}
