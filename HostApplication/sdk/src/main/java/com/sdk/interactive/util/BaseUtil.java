package com.sdk.interactive.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by hexin on 2017/8/7.
 */

public class BaseUtil {
    /**
     * 获取APK的包名
     * @param ctx
     * @param apkPath
     * @return
     */
    private String getPackageName(Context ctx,String apkPath) {
        PackageInfo pi = ctx.getPackageManager().getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);
        String packageName = null;
        if (pi != null) {
            packageName = pi.packageName;
        }
        return packageName;
    }

    /**
     * 获取APK版本名称(versionName)
     * @param ctx
     * @param apkPath
     * @return
     */
    private String getVersionName(Context ctx,String apkPath) {
        PackageInfo pi = ctx.getPackageManager().getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);
        String versionName = null;
        if (pi != null) {
            versionName = pi.versionName;
        }
        return versionName;
    }

    /**
     * 获取APK版本号(versionCode)
     * @param ctx
     * @param apkPath
     * @return
     */
    private int getVersionCode(Context ctx,String apkPath) {
        PackageInfo pi = ctx.getPackageManager().getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);
        int versionCode = 1;
        if (pi != null) {
            versionCode = pi.versionCode;
        }
        return versionCode;
    }

    public static boolean copyFile(final InputStream inputStream, String dest) {
        FileOutputStream oputStream = null;
        try {
            File destFile = new File(dest);
            File parentDir = destFile.getParentFile();
            if (!parentDir.isDirectory() || !parentDir.exists()) {
                destFile.getParentFile().mkdirs();
            }
            oputStream = new FileOutputStream(destFile);
            byte[] bb = new byte[48 * 1024];
            int len = 0;
            while ((len = inputStream.read(bb)) != -1) {
                oputStream.write(bb, 0, len);
            }
            oputStream.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (oputStream != null) {
                try {
                    oputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }
}
