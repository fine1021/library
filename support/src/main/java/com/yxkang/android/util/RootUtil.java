package com.yxkang.android.util;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;

/**
 * a root helper class, there are many useful usage.
 * <br>
 * for instance, you can use "rm /data/app/*.tmp" command delete the cache files,
 * or "ls /data/app" command to list the files in "data/app" directory
 * <p>
 * <p><strong>NOTE:</strong></p> this class need a rooted phone, otherwise the the command will be executed failed
 */
@SuppressWarnings("unused")
public class RootUtil {

    private static final String TAG = RootUtil.class.getSimpleName();


    /**
     * execute a root command and wait for the result, with the default separator
     * the method will call {@link #exeRootCommandForResult(String, String)}, give a
     * default separator string of {@code "\n"}
     *
     * @param command command to be executed
     * @return the result from the terminal, after the command was executed
     * @see #exeRootCommandForResult(String, String)
     */
    public static String exeRootCommandForResult(String command) {
        return exeRootCommandForResult(command, "\n");
    }

    /**
     * execute a root command and wait for the result
     *
     * @param command   command to be executed
     * @param separator the separator string of the each line
     * @return the result from the terminal, after the command was executed
     */
    public static String exeRootCommandForResult(String command, String separator) {
        StringBuilder builder = new StringBuilder();
        Process process = null;
        DataOutputStream os = null;
        BufferedReader br = null;
        try {
            Log.d(TAG, command);
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            os.writeBytes(command + "\n");
            os.flush();
            os.writeBytes("exit\n");
            os.flush();

            String line;
            while ((line = br.readLine()) != null) {
                builder.append(line).append(separator);
            }

            process.waitFor();
            Log.d(TAG, "Execute Success !");
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                if (br != null) {
                    br.close();
                }
                if (process != null) {
                    process.destroy();
                }
            } catch (Exception e) {
                Log.d(TAG, e.getMessage());
            }
        }
        return builder.toString();
    }

    /**
     * execute a root command, but don't care about the result
     *
     * @param command command to be executed
     * @return {@code true} if execute success, otherwise {@code false}
     */
    public static boolean exeRootCommand(String command) {
        Process process = null;
        DataOutputStream os = null;
        try {
            Log.d(TAG, command);
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(command + "\n");
            os.flush();
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
            Log.d(TAG, "Execute Success !");
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
            return false;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                if (process != null) {
                    process.destroy();
                }
            } catch (Exception e) {
                Log.d(TAG, e.getMessage());
            }
        }
        return true;
    }

}
