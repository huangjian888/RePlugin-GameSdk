package com.sdk.interactive.callback;

import com.sdk.interactive.aidl.IPermissionsCallBack;

import java.util.List;

/**
 * Created by hexin on 2017/8/9.
 */

public abstract class PermissionsCallBack extends IPermissionsCallBack.Stub{
    public abstract void onPermissionsGranted(int requestCode, List<String> perms);
    public abstract void onPermissionsDenied(int requestCode, List<String> perms);
}
