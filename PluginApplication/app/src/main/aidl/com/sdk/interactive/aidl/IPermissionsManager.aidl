// IPermissionsManager.aidl
package com.sdk.interactive.aidl;
import com.sdk.interactive.aidl.IPermissionsCallBack;
// Declare any non-default types here with import statements

interface IPermissionsManager {
    void requestPermissions(String json,IPermissionsCallBack callback);
}
