// IPermissionCallBack.aidl
package com.sdk.interactive.aidl;

// Declare any non-default types here with import statements

interface IPermissionsCallBack {
    void onPermissionsGranted(int requestCode, in List<String> perms);
    void onPermissionsDenied(int requestCode, in List<String> perms);
}
