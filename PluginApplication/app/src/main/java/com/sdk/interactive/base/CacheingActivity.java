package com.sdk.interactive.base;


import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class CacheingActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
		super.onCreate(savedInstanceState);
		BaseApplication.getInstance().setmCacheingActivity(this);
		onBackPressed();
	}

//	@Override
//	protected void onDestroy() {
//		// TODO Auto-generated method stub
//		Log.e("CacheingActivity","onDestroy");
//		super.onDestroy();
//		finish();
//	}
	
}
