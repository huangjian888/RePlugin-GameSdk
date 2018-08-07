package com.sdk.interactive.core.callback;


import com.sdk.interactive.aidl.IRegisterPluginBinderCallBack;

/**
 * Created by hexin on 2017/8/8.
 */

public abstract class RegisterPluginBinderCallBack extends IRegisterPluginBinderCallBack.Stub{
    public abstract void onFailure(String paramString) ;
    public abstract void onSuccess(String paramString) ;
}
