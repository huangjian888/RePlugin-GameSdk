package com.haowan123.funcell.sdk.apiinterface;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.util.Log;

import com.example.funcellplugin.MainActivity;
import com.haowan123.funcell.sdk.permission.PermissionUtil;
import com.haowan123.funcell.sdk.ui.FunLoginActivity;
import com.haowan123.funcell.sdk.ui.FunSdkUiActivity;
import com.haowan123.funcell.sdk.util.FunFloatingTool;
import com.haowan123.funcell.sdk.util.FunLogStatsUtils;
import com.haowan123.funcell.sdk.util.FunLogStatsUtils.AtsLogTaskInfo;
import com.haowan123.funcell.sdk.util.FunLogStatsUtils.LogTaskInfo;
import com.haowan123.funcell.sdk.util.HWPreferences;
import com.haowan123.funcell.sdk.util.HWUtils;
import com.qihoo360.replugin.RePlugin;

/**
 * Funcell发行平台SDK接口类
 * 
 * @author id4lin
 * 
 */
public class FunCellPlatformSdkApi {
	public String mAppId;
	public String mAppKey;
	private boolean isAllowGuestLogin = false;
	private boolean isWindowMode = true;
	private boolean isLogin = false;
//	private Activity mContext;
	private static FunCellPlatformSdkApi instance = null;
	private LogoutCallBack mLogoutCallBack;
	
	/**
	 * 内部类，用于实现lzay机制
	 */
	private static class SingletonHolder {
		/** 单例变量 */
		private static FunCellPlatformSdkApi instance = new FunCellPlatformSdkApi();
	}

	/**
	 * 私有化的构造方法，保证外部的类不能通过构造器来实例化。
	 */
	private FunCellPlatformSdkApi() {

	}

	/**
	 * 获取单例对象 WwsPlatformSdkApi
	 * 
	 * @return WwsPlatformSdkApi
	 */
	public static FunCellPlatformSdkApi getInstance() {
		// if (SingletonHolder.instance == null) {
		// SingletonHolder.instance = new WwsPlatformSdkApi();
		// }
		// return SingletonHolder.instance;
		if (FunCellPlatformSdkApi.instance == null) {
			instance = new FunCellPlatformSdkApi();
		}
		return instance;
	}

	/**
	 * 初始化
	 * 
	 * @param appId
	 *            [由平台方分配]
	 * @param appKey
	 *            [由平台方分配]
	 */
	public void init(final String appId, final String appKey, final InitCallBack initCallBack,LogoutCallBack logoutCallBack) {
		// TODO appCode设置、激活统计等
		if ( null == appId || 0 == appId.trim().length()
				|| null == appKey || 0 == appKey.trim().length()
				|| null == initCallBack) {
			HWUtils.logError("HWSDKLOG", "FuncellSDK init--> parameter error");
			return;
		}

//		this.mContext = context;
		this.mAppId = appId;
		this.mAppKey = appKey;
		mLogoutCallBack = logoutCallBack;
		FunSdkUiActivity.setLogoutCallBack(logoutCallBack);
		initCallBack.initSuccess();
		HWUtils.beginTime = HWUtils.getTimestamp();

	}

	/**
	 * 登录接口
	 * 
	 * @param loginCallBack
	 */
	public void login(Activity ctx,final LoginCallBack loginCallBack) {
		FunLoginActivity.setOnLoginCallBack(loginCallBack);
//		Intent intent = new Intent(ctx, FunLoginActivity.class);
//		ctx.startActivity(intent);

		RePlugin.startActivity(ctx, RePlugin.createIntent("funcellplugin", "com.haowan123.funcell.sdk.ui.FunLoginActivity"));

	}
	
	public void clearUser(Activity context){
		HWPreferences.addData(context, "hw_username", "");
		HWPreferences.addData(context, "hw_refresh_token", "");
	}
	
	/**
	 * 登出接口
	 */
	public void logout() {
		mLogoutCallBack.logout();
	}

	/**
	 * 充值接口
	 * 
	 * @param payInfo
	 *            [订单信息]
	 * @param rechargeCallBack
	 *            [充值回调]
	 */
	public void recharge(final Activity ctx,final FunPayInfo payInfo,
			final RechargeCallBack rechargeCallBack) {
		FunSdkUiActivity.setRechargeCallBack(rechargeCallBack);
		Intent intent = new Intent();
		intent.putExtra("funPayInfo", payInfo);
		intent.putExtra("fun_action", "pay");
//		ctx.startActivity(intent);
		RePlugin.startActivity(ctx,intent,"funcellplugin","com.haowan123.funcell.sdk.ui.FunSdkUiActivity");
	}

	/**
	 * 用户中心接口
	 */
	public void openUserCenter(Context context) {
		// Intent intent = new Intent(context,WwsSdkUiActivity.class);
		// intent.putExtra("fun_action", "usercenter");
		// context.startActivity(intent);
	}

	/**
	 * 显示浮动工具条
	 */
//	private void showFuncellToolBar() {
//		if (null == mContext) {
//			return;
//		}
//
//		if (!isLogin) {
//			return;
//		}
//
//		mContext.runOnUiThread(new Runnable() {
//
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				FunFloatingTool.initFloatToolBar(mContext);
//
//			}
//		});
//	}

	/**
	 * 隐藏浮动工具条
	 */
//	private void hideFuncellToolBar() {
//		if (null == mContext) {
//			return;
//		}
//		mContext.runOnUiThread(new Runnable() {
//
//			@Override
//			public void run() {
//				FunFloatingTool.close(mContext);
//
//			}
//		});
//	}

	public boolean isAllowGuestLogin() {
		return isAllowGuestLogin;
	}

	/**
	 * 设置是否开启游客模式[开启:true,不开启:false],默认为不开启模式
	 * 
	 * @param isAllowGuestLogin
	 */
	public void setAllowGuestLogin(boolean isAllowGuestLogin) {
		this.isAllowGuestLogin = isAllowGuestLogin;
	}

	public boolean isWindowMode() {
		return isWindowMode;
	}

	/**
	 * 设置登录页面展示是否是窗口模式[true:窗口模式,false:全屏模式]
	 * 
	 * @param isWindowMode
	 */
	public void setWindowMode(boolean isWindowMode) {
		this.isWindowMode = isWindowMode;
	}

	public boolean isLogin() {
		return isLogin;
	}

	public void setLogin(boolean isLogin) {
		this.isLogin = isLogin;
	}

	public void onPause() {
//		hideFuncellToolBar();
	}

	public void onDestroy() {
//		hideFuncellToolBar();
	}

	public void onResume() {
//		showFuncellToolBar();
	}

	public void onConfigurationChanged(Configuration newConfig) {

	}

	public void funLevelUp(String level) {
		// cps levelup
//		AtsLogTaskInfo atsLogTaskInfo = new AtsLogTaskInfo();
//		atsLogTaskInfo.setMethod(AtsLogTaskInfo.ATS_METHOD_LEVEL_UP);
//		atsLogTaskInfo.setLevel(level);
//
//		String userAccount = HWPreferences.getData(mContext, "hw_account");
//		atsLogTaskInfo.setIdentity(userAccount);
//
//		FunLogStatsUtils.submit(new FunLogStatsUtils.AtsLogTaskRunnable(
//				mContext, atsLogTaskInfo));
//
//		HWUtils.logError("HWSDKLOG", "call funLevelUp over");
	}
	
	public void onRequestPermissionsResult(int requestCode,String[] permissions, int[] grantResults) {
		
		PermissionUtil.getInstance().onRequestPermissionsResult(requestCode, permissions, grantResults);
	}
	
	public void SubmitAtsPayInfo(Activity context,String amount){
//		AtsLogTaskInfo atsLogTaskInfo = new AtsLogTaskInfo();
//		atsLogTaskInfo.setMethod(AtsLogTaskInfo.ATS_METHOD_RECHARGE);
//		atsLogTaskInfo.setAmount(amount);
//		atsLogTaskInfo.setIdentity(FunLogStatsUtils.getInstance().getIdentity());
//		FunLogStatsUtils.submit(new FunLogStatsUtils.AtsLogTaskRunnable(context, atsLogTaskInfo));
	}
	
	public void SubmitAtsLevelUp(Activity context,String levelup){
//		AtsLogTaskInfo atsLogTaskInfo = new AtsLogTaskInfo();
//		atsLogTaskInfo.setMethod(AtsLogTaskInfo.ATS_METHOD_LEVEL_UP);
//		atsLogTaskInfo.setLevel(levelup);
//		atsLogTaskInfo.setIdentity(FunLogStatsUtils.getInstance().getIdentity());
//		FunLogStatsUtils.submit(new FunLogStatsUtils.AtsLogTaskRunnable(context, atsLogTaskInfo));
	}
}
