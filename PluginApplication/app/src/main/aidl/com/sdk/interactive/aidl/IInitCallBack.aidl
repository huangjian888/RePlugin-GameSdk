package com.sdk.interactive.aidl;

interface IInitCallBack{
	void onInitFailure(String msg);
	void onInitSuccess(String msg);
}