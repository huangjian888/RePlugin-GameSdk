package com.sdk.interactive.base;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.haowan123.funcell.sdk.apiinterface.FunCellPlatformSdkApi;
import com.haowan123.funcell.sdk.apiinterface.LoginCallBack;
import com.sdk.interactive.aidl.ISessionCallBack;
import com.sdk.interactive.aidl.Session;


public class LoginActivity extends Activity{
	private String TAG = "LoginActivity";
//	private FinishReceiver mFinishReceiver = new FinishReceiver();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
		super.onCreate(savedInstanceState);
//		IntentFilter intentFilter = new IntentFilter();
//		intentFilter.addAction(Constant.B_LoginActivity_Action_finish);
//		registerReceiver(mFinishReceiver,intentFilter);
		FunCellPlatformSdkApi.getInstance().login(this,new LoginCallBack(){

			@Override
			public void loginSuccess(String token, String fid) {
				BaseApplication.getInstance().setmLoginFlag(true);
				ISessionCallBack sessionCallBack = BaseApplication.getInstance().getmISessionCallBack();
				Session session = new Session();
				session.setmChannelToken(token);
				session.setmChannelUserId(fid);
				try {
					sessionCallBack.onLoginSuccess(session);
				} catch (RemoteException e) {
					e.printStackTrace();
				}finally {
					finish();
				}
			}

			@Override
			public void loginFail(int errorCode, String errorMsg) {
				ISessionCallBack sessionCallBack = BaseApplication.getInstance().getmISessionCallBack();
				try {
					sessionCallBack.onLoginFailed("errorCode:"+errorCode);
				} catch (RemoteException e) {
					e.printStackTrace();
				}finally {
					finish();
				}
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
//		unregisterReceiver(mFinishReceiver);
	}

//	class FinishReceiver extends BroadcastReceiver{
//
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			Log.e(TAG,"onReceive finish");
//			LoginActivity.this.finish();
//		}
//	}
}
