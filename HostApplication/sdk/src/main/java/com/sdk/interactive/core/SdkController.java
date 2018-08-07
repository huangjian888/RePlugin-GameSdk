package com.sdk.interactive.core;

import android.app.Activity;
import android.content.Intent;

import com.sdk.interactive.aidl.PayInfo;
import com.sdk.interactive.bean.SdkConfig;
import com.sdk.interactive.core.callback.SdkExitCallBack;
import com.sdk.interactive.core.callback.SdkInitCallBack;
import com.sdk.interactive.core.callback.SdkPayCallBack;
import com.sdk.interactive.core.callback.SdkSessionCallBack;
import com.sdk.interactive.util.SdkNotProguard;

@SdkNotProguard
public class SdkController {
	private String TAG = "SdkController";
	private static SdkController mInstance;
	private BaseController mBaseController = BaseController.getInstance();
	public Activity mCtx;
	
	public static SdkController getInstance() {
		if (mInstance == null) {
			synchronized (SdkController.class) {
				if (mInstance == null) {
					mInstance = new SdkController();
				}
			}
		}
		return mInstance;
	}

	/**
	 * provides context for the plugin
	 * @return
	 */
	public Activity getmCtx(){
		return mCtx;
	}

	public void init(Activity ctx,SdkConfig config,SdkInitCallBack initCallBack,SdkSessionCallBack sessionCallBack,SdkPayCallBack payCallBack){
		mCtx = ctx;
		mBaseController.init(ctx,config, initCallBack, sessionCallBack, payCallBack);
	}
	
	public void login(Activity ctx){
		mBaseController.login(ctx);
	}
	
	public void logout(Activity ctx) {
		mBaseController.logout(ctx);
	}
	
	public void exit(Activity ctx,SdkExitCallBack exitCallBack){
		mBaseController.exit(ctx, exitCallBack);
	}
    public void showFloat(Activity ctx){
        mBaseController.showFloat(ctx);
    }

    public void pay(Activity ctx, PayInfo info){
        mBaseController.pay(ctx,info);
    }

    public void hideFloat(Activity ctx){
        mBaseController.hideFloat(ctx);
    }

	public void onResume(Activity ctx){
		mBaseController.onResume(ctx);
	}
	
	public void onPause(Activity ctx){
		mBaseController.onPause(ctx);
	}
	
	public void onDestroy(Activity ctx){
		mBaseController.onDestroy(ctx);
	}
	
	public void onActivityResult(Activity ctx,int requestCode, int resultCode, Intent data){
		mBaseController.onActivityResult(ctx, requestCode, resultCode, data);
	}

	public void onRequestPermissionsResult(int requestCode,String[] permissions, int[] grantResults) {
		mBaseController.onRequestPermissionsResult(requestCode, permissions, grantResults);
	}

}
