package com.sdk.interactive.base;

import android.app.Activity;
import android.app.Application;
import android.util.Log;

import com.qihoo360.replugin.RePlugin;
import com.sdk.interactive.aidl.IInitCallBack;
import com.sdk.interactive.aidl.IPayCallBack;
import com.sdk.interactive.aidl.ISessionCallBack;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class BaseApplication extends Application{

	private static BaseApplication mFuncellApplication;
	private Activity mCacheingActivity;
	private Map<String, Activity> mContainer = new HashMap();
	private IInitCallBack mIInitCallBack;
	private ISessionCallBack mISessionCallBack;
	private IPayCallBack mIPayCallBack;
	private boolean mLoginFlag = false;

	public static BaseApplication getInstance() {
		return mFuncellApplication;
	}

	public boolean getmLoginFlag() {return mLoginFlag;}
	public void setmLoginFlag(boolean mLoginFlag) {this.mLoginFlag = mLoginFlag;}

	public Activity getmCacheingActivity() {
		return mCacheingActivity;
	}
	public void setmCacheingActivity(Activity mCacheingActivity) {this.mCacheingActivity = mCacheingActivity;}

	public IPayCallBack getmIPayCallBack() {return mIPayCallBack;}
	public void setmIPayCallBack(IPayCallBack mIPayCallBack) {this.mIPayCallBack = mIPayCallBack;}

	public ISessionCallBack getmISessionCallBack() {return mISessionCallBack;}
	public void setmISessionCallBack(ISessionCallBack mISessionCallBack) {this.mISessionCallBack = mISessionCallBack;}

	public IInitCallBack getmIInitCallBack() {return mIInitCallBack;}
	public void setmIInitCallBack(IInitCallBack mIInitCallBack) {this.mIInitCallBack = mIInitCallBack;}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		Log.e("FuncellApplication", "onCreate");
		super.onCreate();
//		RePlugin.registerPluginBinder("sdkImpl",new SdkImpl());
	}

}
