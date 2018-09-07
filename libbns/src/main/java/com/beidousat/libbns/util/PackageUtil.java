package com.beidousat.libbns.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.SystemProperties;
import android.widget.Toast;

import com.beidousat.libbns.model.Common;

import java.io.File;

/**
 * Created by J Wong on 2015/12/7 18:54.
 */
public class PackageUtil {

    public static int getVersionCode(Context context) {
        try {
            int code = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0).versionCode;
            Common.versioncode=code;
            return code;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;

    }

    public static String getVersionName(Context context) {
        try {
            String name = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0).versionName;
            return name;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";

    }

    public static String getApplicationName(Context context) {
        try {
            PackageManager packageManager = context.getApplicationContext().getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
            String applicationName =
                    (String) packageManager.getApplicationLabel(applicationInfo);
            return applicationName;
        } catch (Exception e) {
            Logger.w("PackageUtil", e.toString());
        }

        return "";
    }

    public static void installApk(Context context, File apkFile, String activityName) {
        Toast.makeText(context, "版本升级中...", Toast.LENGTH_LONG).show();
        Intent intent = new Intent();
        intent.setAction("com.ynh.update_apk");
        intent.putExtra("apkname", apkFile.getAbsolutePath());  //abc.apk 为Apk名
        intent.putExtra("packagename", context.getPackageName()); //abcd 为包名
        intent.putExtra("activityname", activityName);
        Logger.i("installApk", "apk file:" + apkFile.getAbsolutePath() + "  package name:" + context.getPackageName() + "  activityName:" + activityName);
        context.sendBroadcast(intent);
    }

    public static void installApkH8(Context context, File apkFile, String activityName) {
        Toast.makeText(context, "版本升级中...", Toast.LENGTH_LONG).show();
        Intent intent = new Intent();
        intent.setAction("com.ynh.update_apk");
        intent.putExtra("apkname", apkFile.getName());  //abc.apk 为Apk名
        intent.putExtra("packagename", context.getPackageName()); //abcd 为包名
        intent.putExtra("activityname", activityName);
        Logger.i("installApk", "apk file:" + apkFile.getAbsolutePath() + "  package name:" + context.getPackageName() + "  activityName:" + activityName);
        context.sendBroadcast(intent);
    }

    public static void installApkByApi(Context context, File apkFile) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.parse("file://" + apkFile.getAbsolutePath()),
                "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    public static void installApkFor901(Context context, File apkFile, String activityName) {
        Toast.makeText(context, "901版本升级中...", Toast.LENGTH_LONG).show();
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction("com.ynh.update_apk");
        intent.putExtra("apkname", apkFile.getName());  //abc.apk 为Apk名
        intent.putExtra("packagename", context.getPackageName()); //abcd 为包名
        intent.putExtra("activityname", activityName);
        Logger.i("installApk", "installApkFor901 apk file:" + apkFile.getName() + "  package name:" + context.getPackageName() + "  activityName:" + activityName);
        context.sendBroadcast(intent);
    }

//    public static void rk3288InstallApk(Context context, File apkFile, String activityName) {
//        Toast.makeText(context, "版本升级中...", Toast.LENGTH_LONG).show();
//        Intent intent = new Intent();
//        intent.setAction("com.ynh.update_apk");
//        intent.putExtra("apkname", apkFile.getName());  //abc.apk 为Apk名
//        intent.putExtra("packagename", context.getPackageName()); //abcd 为包名
//        intent.putExtra("activityname", activityName);
//        Logger.i("installApk", "apk file:" + apkFile.getAbsolutePath() + "  package name:" + context.getPackageName() + "  activityName:" + activityName);
//        context.sendBroadcast(intent);
//    }


    public static void upgradeSystem(Context context) {
        Intent intent = new Intent("softwinner.intent.action.autoupdate");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


    public static int getSystemVersionCode() {
        try {
            String version = SystemProperties.get(DiskFileUtil.is901() ? "ro.product.version" : "ro.sw.version");
            Logger.i("PackageUtil", "SystemProperties version:" + version);
            if (version.contains("V")) {
                version = version.replace("V", "");
            }
            return Integer.valueOf(version);
        } catch (Exception e) {
            Logger.w("PackageUtil", "ex:" + e.toString());
        }
        return -1;
    }


    public static void hideSystemUI(Context context, boolean hide) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.hideNaviBar");
        intent.putExtra("hide", hide);
        context.sendBroadcast(intent);
    }


    public static void rk3288upgradeSystem(Context context, File file) {
        Logger.d("PackageUtil", "rk3288upgradeSystem :" + file.getAbsolutePath());
        Intent intent = new Intent("android.intent.ota.UPDATE");
        Logger.d("PackageUtil", "rk3288upgradeSystem:" + file.getAbsolutePath());
        intent.putExtra("path", file.getAbsolutePath());
        context.sendBroadcast(intent);
    }

    public static void rk3288upgradeSystemFor901(Context context, String path) {
        Logger.d("PackageUtil", "rk3288upgradeSystemFor901 path:" + path);
        Intent intent = new Intent("softwinner.intent.action.autoupdate");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("path", path);
//        intent.putExtra("path", "/mnt/internal_sd/BNS/OTA/update.zip");
        context.startActivity(intent);
    }

    public static String getPackageNameFromApk(Context context, String apkPath) {
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);
        ApplicationInfo appInfo = null;
        if (info != null) {
            appInfo = info.applicationInfo;
            String packageName = appInfo.packageName;
            return packageName;
        }
        return "";

    }
}
