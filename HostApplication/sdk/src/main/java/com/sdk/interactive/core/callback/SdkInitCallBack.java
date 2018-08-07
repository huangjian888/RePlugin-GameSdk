package com.sdk.interactive.core.callback;
import com.sdk.interactive.aidl.IInitCallBack;
import com.sdk.interactive.util.SdkNotProguard;

@SdkNotProguard
public abstract class SdkInitCallBack extends IInitCallBack.Stub{
	public abstract void onInitFailure(String msg);
	public abstract void onInitSuccess(String msg);
}
