package com.sdk.interactive.core.callback;
import com.sdk.interactive.aidl.ISessionCallBack;
import com.sdk.interactive.aidl.Session;
import com.sdk.interactive.util.SdkNotProguard;

@SdkNotProguard
public abstract class SdkSessionCallBack extends ISessionCallBack.Stub{
	public abstract void onLoginSuccess(Session session);
	public abstract void onLoginCancel();
	public abstract void onLoginFailed(String msg);
	public abstract void onLogout();
	public abstract void onSwitchAccount(Session session);
}
