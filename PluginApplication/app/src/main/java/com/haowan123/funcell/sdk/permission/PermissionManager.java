package com.haowan123.funcell.sdk.permission;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

public class PermissionManager {
	private static final String TAG = PermissionManager.class.getName();
	private static PermissionManager mInstance = null;
	private static Activity mContext;
	private static PermissionCallBack mCallBack;
	private int permissionReqCode;
	
	public static PermissionManager getInstance() {
		if (mInstance == null) {
			synchronized (PermissionManager.class) {
				if (mInstance == null) {
					mInstance = new PermissionManager();
				}
			}
		}
		return mInstance;
	}
	
	public void initialize(Context context,PermissionCallBack callback) {
		mContext = (Activity) context;
		mCallBack = callback;
	}

	public boolean checkPermissionGranted(String permission){
		if (ActivityCompat.checkSelfPermission(mContext, permission) == PackageManager.PERMISSION_GRANTED) {
			return true;
		}else{
			return false;
		}
	}
	
	@NonNull
	public void checkPermission(String permission){
		Log.i(TAG, "----checkPermission----");
		if (!checkPermissionGranted(permission)) {
			requestPermission(permission);
			Log.i(TAG, "checkPermission: return false");
		}else{
			mCallBack.callBack(true);
		}
	}
	
	private void requestPermission(final String permission) {
		Log.i(TAG, "permission has not been granted. Requesting permission.");
		String permissionReqMsg;

		switch (permission) {
		case PermissionConfig.Permission.ANDROID_PERMISSION_READ_PHONE_STATE:
			permissionReqCode = PermissionConfig.PermissionReqCode.READ_PHONE_STATE;
			permissionReqMsg = PermissionConfig.PermissionReqMsg.PERMISSION_READ_PHONE_STATE;
			break;
		case PermissionConfig.Permission.ANDROID_PERMISSION_GET_ACCOUNTS:
			permissionReqCode = PermissionConfig.PermissionReqCode.GET_ACCOUNTS;
			permissionReqMsg = PermissionConfig.PermissionReqMsg.PERMISSION_GET_ACCOUNTS;
			break;
		case PermissionConfig.Permission.ANDROID_PERMISSION_WRITE_EXTERNAL_STORAGE:
			permissionReqCode = PermissionConfig.PermissionReqCode.WRITE_EXTERNAL_STORAGE;
			permissionReqMsg = PermissionConfig.PermissionReqMsg.PERMISSION_WRITE_EXTERNAL_STORAGE;
			break;
		case PermissionConfig.Permission.ANDROID_PERMISSION_READ_EXTERNAL_STORAGE:
			permissionReqCode = PermissionConfig.PermissionReqCode.READ_EXTERNAL_STORAGE;
			permissionReqMsg = PermissionConfig.PermissionReqMsg.PERMISSION_READ_EXTERNAL_STORAGE;
			break;
		default:
			permissionReqCode = -1;
			permissionReqMsg = "";
			break;
		}
		if (ActivityCompat.shouldShowRequestPermissionRationale(mContext,permission)) {
			Log.i(TAG, "permission rationale to provide additional context.");
			AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
			dialog.setMessage(permissionReqMsg).setTitle("需要權限")
			.setPositiveButton("知道了",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int which) {
					ActivityCompat.requestPermissions(mContext,new String[] { permission },permissionReqCode);
					dialog.dismiss();
				}
			}).show();
		} else {
			Log.i(TAG,"permission has not been granted yet. Request it directly.");
			ActivityCompat.requestPermissions(mContext,new String[] { permission }, permissionReqCode);
		}
	}
	
	public void onRequestPermissionsResult(int requestCode,String[] permissions, int[] grantResults) {
		Log.e(TAG, "-----------onRequestPermissionsResult------------");
		switch (requestCode) {
		case PermissionConfig.PermissionReqCode.READ_PHONE_STATE:
			responsePermission(PermissionConfig.PermissionResMsg.PERMISSION_AVAILABLE_READ_PHONE_STATE,permissions,grantResults);
			break;
		case PermissionConfig.PermissionReqCode.GET_ACCOUNTS:
			responsePermission(PermissionConfig.PermissionResMsg.PERMISSION_AVAILABLE_GET_ACCOUNTS,permissions,grantResults);
			break;
		case PermissionConfig.PermissionReqCode.WRITE_EXTERNAL_STORAGE:
			responsePermission(PermissionConfig.PermissionResMsg.PERMISSION_AVAILABLE_WRITE_EXTERNAL_STORAGE,permissions,grantResults);
			break;
		case PermissionConfig.PermissionReqCode.READ_EXTERNAL_STORAGE:
			responsePermission(PermissionConfig.PermissionResMsg.PERMISSION_AVAILABLE_READ_EXTERNAL_STORAGE,permissions,grantResults);
			break;
		default:
			break;
		}
	}
	
	private void responsePermission(String msg,String[] permissions,int[] grantResults){
		Log.i(TAG, " grantResults[0]:"+grantResults[0]);
		Log.i(TAG, " grantResults.length:"+grantResults.length);
		if ((grantResults.length == 1) && (grantResults[0] == 0)) {
			Log.i(TAG, permissions[0]+" permission has now been granted.");
//			Toast.makeText(mContext, msg, 0).show();
			mCallBack.callBack(true);
		} else {
			Log.i(TAG, permissions[0]+" permission was not granted.");
//			Toast.makeText(mContext, PermissionConfig.PermissionResMsg.PERMISSIONS_NOT_GRANTED, 0).show();
			mCallBack.callBack(false);
		}
	}
}
