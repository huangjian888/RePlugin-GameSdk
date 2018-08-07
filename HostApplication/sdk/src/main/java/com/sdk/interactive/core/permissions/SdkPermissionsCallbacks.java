package com.sdk.interactive.core.permissions;

import com.sdk.interactive.util.SdkNotProguard;

import java.util.List;


public interface SdkPermissionsCallbacks {
    public abstract void onPermissionsGranted(int requestCode, List<String> perms);
    public abstract void onPermissionsDenied(int requestCode, List<String> perms);
}