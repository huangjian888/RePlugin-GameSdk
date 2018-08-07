package com.haowan123.funcell.sdk.permission;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

public class PermissionUtil {
	private String TAG = PermissionUtil.class.getName();
	private static PermissionUtil mInstance = null;
	private static PermissionCallBack mCallBack;
	private Lock mLock = new ReentrantLock();
	
	public static PermissionUtil getInstance() {
		if (mInstance == null) {
			synchronized (PermissionUtil.class) {
				if (mInstance == null) {
					mInstance = new PermissionUtil();
				}
			}
		}
		return mInstance;
	}
	
	public void initialize(Context context,PermissionCallBack callback) {
		PermissionManager.getInstance().initialize(context, callback);
		mCallBack = callback;
	}
	
	/**
	 * 
	 * @param permission 
	 */
	public void getPermission(String permission) {
		mLock.lock();
		try {
			if (PermissionManager.getInstance().checkPermissionGranted(permission)) {
				Log.i(TAG, permission+" permission has been granted");
				mCallBack.callBack(true);
			}else{
				PermissionManager.getInstance().checkPermission(permission);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			mLock.unlock();
			Log.i(TAG, "getPermission, UN-LOCKED!");
		}
	}
	
	/**
	 * 
	 * @param requestCode
	 * @param permissions
	 * @param grantResults
	 */
	public void onRequestPermissionsResult(int requestCode,String[] permissions, int[] grantResults){
		PermissionManager.getInstance().onRequestPermissionsResult(requestCode, permissions, grantResults);
	}
}
