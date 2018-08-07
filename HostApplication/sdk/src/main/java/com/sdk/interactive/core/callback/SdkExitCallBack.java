package com.sdk.interactive.core.callback;
import com.sdk.interactive.aidl.IExitCallBack;
import com.sdk.interactive.util.SdkNotProguard;

@SdkNotProguard
public abstract class SdkExitCallBack extends IExitCallBack.Stub{
	public abstract void onConfirm();
	public abstract void onCancel();
}
