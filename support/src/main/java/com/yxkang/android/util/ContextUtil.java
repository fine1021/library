package com.yxkang.android.util;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.Log;

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
     * @see #getExplicitServiceIntent(Context, String)
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
     * <p>
     * <p><strong>NOTE: </strong>When starting a Service,
     * you should always specify the component name. Otherwise,
     * you cannot be certain what service will respond to the intent,
     * and the user cannot see which service starts.</p>
     *
     * @param packageContext Context
     * @param action         The Intent action of one <code>Service</code>
     * @return the unique and explicit intent if found, otherwise null
     */
    public static Intent getExplicitServiceIntent(Context packageContext, String action) {
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
     * check the service is alive
     *
     * @param context   context
     * @param className the service class name
     * @return {@code true} if the service is alive, otherwise {@code false}
     */
    public static boolean isServiceAlive(Context context, String className) {
        boolean isAlive = false;
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceInfos = manager.getRunningServices(100);
        if (serviceInfos.isEmpty()) {
            isAlive = false;
        } else {
            for (ActivityManager.RunningServiceInfo serviceInfo : serviceInfos) {
                if (serviceInfo.service.getClassName().equals(className)) {
                    isAlive = true;
                    break;
                }
            }
        }
        return isAlive;
    }
}
