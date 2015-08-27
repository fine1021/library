package com.yxkang.android.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import java.util.List;

/**
 * Context Util
 */
@SuppressWarnings("unused")
public class ContextUtil {

    /**
     * start an application according to the given packageName
     *
     * @param packageContext Context
     * @param packageName    The name of the package that the component exists in.  Can
     *                       not be null.
     * @return {@code true} if start success, otherwise {@code false}
     */
    public static boolean startApplication(Context packageContext, String packageName) {
        Intent resolve = new Intent(Intent.ACTION_MAIN);
        resolve.addCategory(Intent.CATEGORY_LAUNCHER);
        resolve.setPackage(packageName);

        PackageManager manager = packageContext.getPackageManager();
        List<ResolveInfo> list = manager.queryIntentActivities(resolve, 0);
        if (list.size() > 0) {
            ResolveInfo info = list.iterator().next();
            if (info != null) {
                String pkg = info.activityInfo.packageName;
                String cls = info.activityInfo.name;

                Intent application = new Intent();
                application.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                application.setComponent(new ComponentName(pkg, cls));
                packageContext.startActivity(application);
                return true;
            }
        }
        return false;
    }


    /**
     * start a service according to the given action
     *
     * @param packageContext Context
     * @param action         The Intent action of one <code>Service</code>
     * @return {@code true} if start success, otherwise {@code false}
     * @see #getExplicitServiceIntent(Context, String)
     */
    public static boolean startService(Context packageContext, String action) {
        Intent resolve = new Intent(action);

        PackageManager manager = packageContext.getPackageManager();
        List<ResolveInfo> list = manager.queryIntentServices(resolve, 0);
        if (list.size() > 0) {
            ResolveInfo info = list.iterator().next();
            if (info != null) {
                String pkg = info.serviceInfo.packageName;
                String cls = info.serviceInfo.name;

                Intent service = new Intent(action);
                service.setComponent(new ComponentName(pkg, cls));
                packageContext.startService(service);
                return true;
            }
        }
        return false;
    }

    /**
     * Get the explicit intent of one <code>Service</code>, according to the given action
     * <br/>
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
     * <p/>
     * <p><strong>NOTE: </strong>When starting a Service,
     * you should always specify the component name. Otherwise,
     * you cannot be certain what service will respond to the intent,
     * and the user cannot see which service starts.</p>
     *
     * @param packageContext Context
     * @param action         The Intent action of one <code>Service</code>
     * @return the explicit intent if found, otherwise null
     */
    public static Intent getExplicitServiceIntent(Context packageContext, String action) {
        Intent resolve = new Intent(action);

        PackageManager manager = packageContext.getPackageManager();
        List<ResolveInfo> list = manager.queryIntentServices(resolve, 0);
        if (list.size() > 0) {
            ResolveInfo info = list.iterator().next();
            if (info != null) {
                String pkg = info.serviceInfo.packageName;
                String cls = info.serviceInfo.name;

                Intent service = new Intent(action);
                service.setComponent(new ComponentName(pkg, cls));
                return service;
            }
        }
        return null;
    }
}
