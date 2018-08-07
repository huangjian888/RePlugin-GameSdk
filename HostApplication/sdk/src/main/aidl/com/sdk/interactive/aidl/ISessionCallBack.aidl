package com.sdk.interactive.aidl;
import com.sdk.interactive.aidl.Session;

interface ISessionCallBack{
	void onLoginSuccess(in Session session);
	void onLoginCancel();
	void onLoginFailed(String paramString);
	void onLogout();
	void onSwitchAccount(in Session session);
}