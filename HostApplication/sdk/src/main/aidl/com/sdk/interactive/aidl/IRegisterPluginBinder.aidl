// IRegisterPluginBinder.aidl
package com.sdk.interactive.aidl;
import com.sdk.interactive.aidl.IRegisterPluginBinderCallBack;
// Declare any non-default types here with import statements

interface IRegisterPluginBinder {
    void Register(IRegisterPluginBinderCallBack callback);
}
