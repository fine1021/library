package com.yxkang.android.util;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;

/**
 * Context Util
 */
@SuppressWarnings("unused")
public class ContextUtil {

    private static final String TAG = ContextUtil.class.getSimpleName();

    /**
     * indicate the operation is executed successfully
     */
    public static final int TYPE_SUCCESS = 0x01;
    /**
     * indicate the operation is executed failed
     */
    public static final int TYPE_FAILED = 0x02;
    /**
     * the case happens when the queryIntent result is more than one.
     * so post the problem to the user, rather then execute randomly
     */
    public static final int TYPE_NOT_UNIQUE = 0x03;

    /**
     * start an application according to the given packageName
     *
     * @param packageContext Context
     * @param packageName    The name of the package that the component exists in.  Can
     *                       not be null.
     * @return result type, such as {@link #TYPE_SUCCESS}, {@link #TYPE_FAILED} or {@link #TYPE_NOT_UNIQUE}
     */
    public static int startApplication(Context packageContext, String packageName) {
        Intent resolve = new Intent(Intent.ACTION_MAIN);
        resolve.addCategory(Intent.CATEGORY_LAUNCHER);
        resolve.setPackage(packageName);

        PackageManager manager = packageContext.getPackageManager();
        Intent intent = manager.getLaunchIntentForPackage(packageName);
        if (intent != null) {
            ComponentName componentName = intent.getComponent();
            if (componentName != null) {
                String pkg = componentName.getPackageName();
                String cls = componentName.getClassName();
                Log.i(TAG, "LaunchIntent: " + pkg + "/" + cls);
            }
            packageContext.startActivity(intent);
            return TYPE_SUCCESS;
        }
        List<ResolveInfo> list = manager.queryIntentActivities(resolve, 0);
        if (list.size() > 1) {
            Log.e(TAG, "the queryIntent result is more than one");
            return TYPE_NOT_UNIQUE;
        } else if (list.size() == 1) {
            ResolveInfo info = list.iterator().next();
            if (info != null) {
                String pkg = info.activityInfo.packageName;
                String cls = info.activityInfo.name;

                Log.i(TAG, pkg + "/" + cls);
                Intent application = new Intent();
                application.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                application.setComponent(new ComponentName(pkg, cls));
                packageContext.startActivity(application);
                return TYPE_SUCCESS;
            }
        }
        return TYPE_FAILED;
    }


    /**
     * start a service according to the given action
     *
     * @param packageContext Context
     * @param action         The Intent action of one <code>Service</code>
     * @return result type, such as {@link #TYPE_SUCCESS}, {@link #TYPE_FAILED} or {@link #TYPE_NOT_UNIQUE}
     * @see #explicitServiceIntent(Context, String)
     */
    public static int startService(Context packageContext, String action) {
        Intent resolve = new Intent(action);

        PackageManager manager = packageContext.getPackageManager();
        List<ResolveInfo> list = manager.queryIntentServices(resolve, 0);
        if (list.size() > 1) {
            Log.e(TAG, "the queryIntent result is more than one");
            return TYPE_NOT_UNIQUE;
        } else if (list.size() == 1) {
            ResolveInfo info = list.iterator().next();
            if (info != null) {
                String pkg = info.serviceInfo.packageName;
                String cls = info.serviceInfo.name;

                Log.i(TAG, pkg + "/" + cls);
                Intent service = new Intent(action);
                service.setComponent(new ComponentName(pkg, cls));
                packageContext.startService(service);
                return TYPE_SUCCESS;
            }
        }
        return TYPE_FAILED;
    }

    /**
     * Get the explicit intent of one <code>Service</code>, according to the given action
     * <br>
     * <h2>Change Beginning with Android 5.0</h2>
     * <p><strong>Caution: </strong>
     * To ensure your app is secure, always use an explicit intent when
     * starting a Service and do not declare intent filters for your services.
     * Using an implicit intent to start a service is a security hazard
     * because you cannot be certain what service will respond to the intent,
     * and the user cannot see which service starts.
     * Beginning with {@link android.os.Build.VERSION_CODES#LOLLIPOP},
     * the system throws an exception if you call
     * {@link Context#bindService(Intent, ServiceConnection, int)} with an implicit intent.</p>
     * <br>
     * <p><strong>NOTE: </strong>When starting a Service,
     * you should always specify the component name. Otherwise,
     * you cannot be certain what service will respond to the intent,
     * and the user cannot see which service starts.</p>
     *
     * @param packageContext Context
     * @param action         The Intent action of one <code>Service</code>
     * @return the unique and explicit intent if found, otherwise null
     */
    public static Intent explicitServiceIntent(Context packageContext, String action) {
        Intent resolve = new Intent(action);

        PackageManager manager = packageContext.getPackageManager();
        List<ResolveInfo> list = manager.queryIntentServices(resolve, 0);
        if (list.size() > 1) {
            Log.e(TAG, "the queryIntent result is more than one");
            return null;
        } else if (list.size() == 1) {
            ResolveInfo info = list.iterator().next();
            if (info != null) {
                String pkg = info.serviceInfo.packageName;
                String cls = info.serviceInfo.name;

                Log.i(TAG, pkg + "/" + cls);
                Intent service = new Intent(action);
                service.setComponent(new ComponentName(pkg, cls));
                return service;
            }
        }
        return null;
    }

    /**
     * check the service is running
     *
     * @param context   context
     * @param className the service class name
     * @return {@code true} if the service is running, otherwise {@code false}
     */
    public static boolean isServiceRunning(Context context, String className) {
        boolean result = false;
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceInfos = manager.getRunningServices(Integer.MAX_VALUE);
        if (serviceInfos.isEmpty()) {
            result = false;
        } else {
            Log.i(TAG, "serviceInfos size = " + serviceInfos.size());
            for (ActivityManager.RunningServiceInfo serviceInfo : serviceInfos) {
                if (serviceInfo.service.getClassName().equals(className)) {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }

    /**
     * check the service is running, which process name is the same as current process
     *
     * @param context   context
     * @param className the service class name
     * @return {@code true} if the service is running, otherwise {@code false}
     */
    public static boolean isServiceRunningWithProcess(Context context, String className) {
        boolean result = false;
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceInfos = manager.getRunningServices(Integer.MAX_VALUE);
        if (serviceInfos.isEmpty()) {
            result = false;
        } else {
            Log.i(TAG, "isServiceRunningWithProcess: size = " + serviceInfos.size());
            for (ActivityManager.RunningServiceInfo serviceInfo : serviceInfos) {
                if (serviceInfo.service.getClassName().equals(className)) {
                    String servicePN = getProcessName(serviceInfo.pid);
                    Log.i(TAG, "isServiceRunningWithProcess: servicePN = " + servicePN);
                    if (!TextUtils.isEmpty(servicePN) && servicePN.equals(getProcessName())) {
                        result = true;
                        break;
                    }
                }
            }
        }
        return result;
    }

    /**
     * check the service is running, which process name is the same as the given name
     *
     * @param context     context
     * @param className   the service class name
     * @param processName the process name
     * @return {@code true} if the service is running, otherwise {@code false}
     */
    public static boolean isServiceRunningWithProcess(Context context, String className, String processName) {
        boolean result = false;
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceInfos = manager.getRunningServices(Integer.MAX_VALUE);
        if (serviceInfos.isEmpty()) {
            result = false;
        } else {
            Log.i(TAG, "isServiceRunningWithProcess: size = " + serviceInfos.size());
            for (ActivityManager.RunningServiceInfo serviceInfo : serviceInfos) {
                if (serviceInfo.service.getClassName().equals(className)) {
                    String servicePN = getProcessName(serviceInfo.pid);
                    Log.i(TAG, "isServiceRunningWithProcess: servicePN = " + servicePN);
                    if (!TextUtils.isEmpty(servicePN) && servicePN.equals(processName)) {
                        result = true;
                        break;
                    }
                }
            }
        }
        return result;
    }

    /**
     * get current process name
     *
     * @return the current process name
     */
    public static String getProcessName() {
        try {
            File file = new File("/proc/" + android.os.Process.myPid() + "/" + "cmdline");
            BufferedReader mBufferedReader = new BufferedReader(new FileReader(file));
            String processName = mBufferedReader.readLine().trim();
            mBufferedReader.close();
            return processName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * get the process name with the given process pid
     *
     * @param pid the process pid
     * @return the process name
     */
    public static String getProcessName(int pid) {
        try {
            File file = new File("/proc/" + pid + "/" + "cmdline");
            BufferedReader mBufferedReader = new BufferedReader(new FileReader(file));
            String processName = mBufferedReader.readLine().trim();
            mBufferedReader.close();
            return processName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * get current process name, using the <code>Context</code> and <code>pid</code>
     *
     * @param cxt context
     * @param pid the pid of this process; 0 if none
     * @return the current process name
     */
    public static String getProcessName(Context cxt, int pid) {
        ActivityManager am = (ActivityManager) cxt.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps == null) {
            return null;
        }
        for (ActivityManager.RunningAppProcessInfo procInfo : runningApps) {
            if (procInfo.pid == pid) {
                return procInfo.processName;
            }
        }
        return null;
    }
}
