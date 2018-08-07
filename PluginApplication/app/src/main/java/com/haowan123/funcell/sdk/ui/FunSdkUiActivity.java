package com.haowan123.funcell.sdk.ui;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebSettings;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.haowan123.funcell.sdk.apiinterface.FunCellPlatformSdkApi;
import com.haowan123.funcell.sdk.apiinterface.FunPayInfo;
import com.haowan123.funcell.sdk.apiinterface.LogoutCallBack;
import com.haowan123.funcell.sdk.apiinterface.RechargeCallBack;
import com.haowan123.funcell.sdk.util.FunErrorCode;
import com.haowan123.funcell.sdk.util.FunLogStatsUtils;
import com.haowan123.funcell.sdk.util.FunLogStatsUtils.AtsLogTaskInfo;
import com.haowan123.funcell.sdk.util.FunLogStatsUtils.LogTaskInfo;
import com.haowan123.funcell.sdk.util.HWHttpResponse;
import com.haowan123.funcell.sdk.util.HWPreferences;
import com.haowan123.funcell.sdk.util.HWUtils;
import com.haowan123.funcell.sdk.util.JsonObjectCoder;

public class FunSdkUiActivity extends Activity {
	private static final String TAG = "FunSdkUiActivity";
	public HWWebView mWebView;
	private RelativeLayout webViewLayout;

	private String urlString;
	private StringBuilder postData = new StringBuilder();

	private FunPayInfo funPayInfo = null;

	private String cp_orderid = null;
	private Integer price = null;
	private String ext_data = null;

	private Integer amount = null;
	private String product_id = null;
	private String product_name = null;

	private String hw_access_token = null;
	private String cp_id = null;
	private String platform = null;
	private int create_time;

	@SuppressLint("SetJavaScriptEnabled")
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		Bundle bundle = getIntent().getExtras();
		initData(bundle);

		initViews();

		initEvents();
	}

	private void initEvents() {
		HWWebView.setOnWebViewCallBack(new HWWebViewCallback() {

			@Override
			public void callBack(String action, String data) {
				HWUtils.logError(TAG, "action is " + action + ",  data is "
						+ data);

				if (HWWebView.PAY_CALLBACK_CODE_SUCCESS.equals(action)) {
					mRechargeCallBack.rechargeSuccess(data);
					
				} else if (HWWebView.PAY_CALLBACK_CODE_ERROR.equals(action)) {
					mRechargeCallBack.rechargeFail(
							FunErrorCode.FUN_ERROR_CODE_PAY_FAIL, "pay error");
				} else if (HWWebView.PAY_CALLBACK_CODE_FAIL.equals(action)) {
					mRechargeCallBack.rechargeFail(
							FunErrorCode.FUN_ERROR_CODE_PAY_CANCEL,
							"cancel pay");
				} else if (HWWebView.PAY_CALLBACK_CODE_NO_PAY.equals(action)) {
					mRechargeCallBack.rechargeFail(
							FunErrorCode.FUN_ERROR_CODE_PAY_CANCEL, "no pay");
				} else if (HWWebView.ACTION_CALLBACK_CODE_CLOSE.equals(action)) {
				} else if (HWWebView.ACTION_CALLBACK_CODE_CHANGE_USER.equals(action)) {
					if (null != mLogoutCallBack) {
						FunCellPlatformSdkApi.getInstance().setLogin(false);
						mLogoutCallBack.logout();
					}
				} else if (HWWebView.ACTION_CALLBACK_CODE_RELOGIN.equals(action)) {
					if (null != mLogoutCallBack) {
						FunCellPlatformSdkApi.getInstance().setLogin(false);
						mLogoutCallBack.logout();
					}
				} else if (HWWebView.ACTION_CALLBACK_CODE_NORMAL_CLOSE
						.equals(action)) {
				}
				Log.e(TAG,"finish begin...");
				finish();
				Log.e(TAG,"finish end...");
			}
		});
	}

	private void initViews() {
		webViewLayout = new RelativeLayout(this);
		webViewLayout.setBackgroundColor(0xFFFFFFFF);
		webViewLayout.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

		mWebView = new HWWebView(this);
		mWebView.setBackgroundColor(0xFFFFFFFF);
		mWebView.clearCache(true);
		mWebView.setFocusable(true);
		mWebView.requestFocus();
		mWebView.requestFocusFromTouch();
		mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		mWebView.setVerticalScrollBarEnabled(false);
		mWebView.setHorizontalScrollBarEnabled(false);
		LinearLayout.LayoutParams myWebViewParams = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		mWebView.setLayoutParams(myWebViewParams);

		webViewLayout.addView(mWebView, myWebViewParams);

		setContentView(webViewLayout);
		mWebView.loadurl(urlString + "?" + postData.toString());

		HWUtils.logError(TAG, "postData = " + postData.toString());
		// mWebView.posturl(urlString,
		// EncodingUtils.getBytes(postData.toString(), "base64"));

		WebSettings set = mWebView.getSettings();
		set.setSavePassword(false);
		set.setSaveFormData(false);
		set.setJavaScriptEnabled(true);
		set.setJavaScriptCanOpenWindowsAutomatically(true);
	}

	public void initData(Bundle bundle) {

		if (null != bundle) {
			String action = bundle.getString("fun_action");

			if (action.equals("pay")) {
				funPayInfo = (FunPayInfo) bundle.getParcelable("funPayInfo");

				recharge(funPayInfo);
			} else if (action.equals("usercenter")) {
				userCenter();
			} else if (action.equals("findpwd")) {
				findPwd();
			}else if(action.equals("term")){
				termOfService();
			}
		}
		// 上传统计数据
		// count();
	}

	//TODO 服务条款
	private void termOfService() {
		cp_id = FunCellPlatformSdkApi.getInstance().mAppId;
		postData.append("cp_id=").append(cp_id).append("&");
		postData.append("sign=").append(getFindPwdHWSign());

		urlString = "/public";

		try {
			urlString = URLDecoder.decode(urlString, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取找回密码请求数据
	 */
	private void findPwd() {
		cp_id = FunCellPlatformSdkApi.getInstance().mAppId;
		postData.append("cp_id=").append(cp_id).append("&");
		postData.append("sign=").append(getFindPwdHWSign());

		urlString = "/member/forgetpassword";

		try {
			urlString = URLDecoder.decode(urlString, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		//打开找回密码页面
		LogTaskInfo logTaskInfo = new LogTaskInfo();
		logTaskInfo.setEventCode(LogTaskInfo.EVENT_CODE_USER_FORGETPASS);
		logTaskInfo.setAccessToken(hw_access_token);
		
		FunLogStatsUtils.submit(new FunLogStatsUtils.LogTaskRunnable(FunSdkUiActivity.this,logTaskInfo));

	}

	/**
	 * 获取找回密码协议加密串
	 * 
	 * @return
	 */
	private String getFindPwdHWSign() {
		StringBuilder mSign = new StringBuilder();
		mSign.append("cp_id=").append(cp_id);

		mSign.append("#").append(
				FunCellPlatformSdkApi.getInstance().mAppKey.trim());

		HWUtils.logError(TAG, "HWSign : " + mSign.toString());
		return HWHttpResponse.md5(mSign.toString().trim());
	}

	/**
	 * 获取充值请求数据
	 * 
	 * @param mFunPayInfo
	 */

	public void recharge(FunPayInfo mFunPayInfo) {

		if (null == mFunPayInfo) {
			HWUtils.logError(TAG, "初始化支付页面失败");
			return;
		}
		String account = HWPreferences.getData(FunSdkUiActivity.this,
				"hw_account");

		cp_orderid = mFunPayInfo.getCpOrderId();
		price = mFunPayInfo.getPrice();
		ext_data = mFunPayInfo.getExtData();

		amount = mFunPayInfo.getAmount();
		product_id = mFunPayInfo.getProductId();
		product_name = mFunPayInfo.getProductName();

		hw_access_token = HWPreferences.getData(FunSdkUiActivity.this,
				"hw_access_token");
		cp_id = FunCellPlatformSdkApi.getInstance().mAppId;
		platform = HWUtils.getChannelCode(FunSdkUiActivity.this);
		create_time = Integer.valueOf(HWUtils.getTimestamp());

		if (null == cp_orderid || 0 == cp_orderid.trim().length()) {
			HWUtils.logError(TAG, "cp orderid is required!");
			return;
		}

		if (null == product_name || 0 == product_name.trim().length()) {
			HWUtils.logError(TAG, "cp product name is required!");
			return;
		}

		getPayPostData();

		urlString = "/charge";

		try {
			urlString = URLDecoder.decode(urlString, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//打开充值页面日志
		
		//cps charge
//		AtsLogTaskInfo atsLogTaskInfo = new AtsLogTaskInfo();
//		atsLogTaskInfo.setMethod(AtsLogTaskInfo.ATS_METHOD_RECHARGE);
//		atsLogTaskInfo.setAmount(price+"");
//		
//		String userAccount = HWPreferences.getData(FunSdkUiActivity.this, "hw_account");
//		atsLogTaskInfo.setIdentity(userAccount);
//		
//		FunLogStatsUtils.submit(new FunLogStatsUtils.AtsLogTaskRunnable(
//				FunSdkUiActivity.this, atsLogTaskInfo));

	}

	/**
	 * 获取充值请求数据
	 */
	private void getPayPostData() {

		if (null != price && 0 < price) {
			postData.append("price=").append(price).append("&");
		}

		if (null != amount && 0 < amount) {
			postData.append("amount=").append(amount).append("&");
		}

		if (null != ext_data && 0 < ext_data.trim().length()) {
			postData.append("ext_data=")
					.append(HWHttpResponse.stringToBase64(ext_data))
					.append("&");
		}

		if (null != product_id && 0 < product_id.trim().length()) {
			postData.append("product_id=").append(product_id).append("&");
		}

		postData.append("product_name=")
				.append(HWHttpResponse.stringToBase64(product_name))
				.append("&");

		postData.append("cp_orderid=").append(cp_orderid).append("&");
		postData.append("access_token=").append(hw_access_token).append("&");
		postData.append("cp_id=").append(cp_id).append("&");
		postData.append("platform=").append(platform).append("&");
		postData.append("create_time=").append(create_time).append("&");
		postData.append("sign=").append(getPayHWSign());
	}

	/**
	 * 获取充值协议加密串
	 * 
	 * @return
	 */
	private String getPayHWSign() {
		StringBuilder mSign = new StringBuilder();
		mSign.append("access_token=").append(hw_access_token.trim())
				.append("&");

		if (null != amount && 0 < amount) {
			mSign.append("amount=").append(amount).append("&");
		}

		mSign.append("cp_id=").append(cp_id).append("&");
		mSign.append("cp_orderid=").append(cp_orderid).append("&");
		mSign.append("create_time=").append(create_time).append("&");

		if (null != ext_data && 0 < ext_data.trim().length()) {
			mSign.append("ext_data=")
					.append(HWHttpResponse.stringToBase64(ext_data.trim()))
					.append("&");
		}

		mSign.append("platform=").append(platform.trim()).append("&");

		if (null != price && 0 < price) {
			mSign.append("price=").append(price).append("&");
		}

		if (null != product_id && 0 < product_id.trim().length()) {
			mSign.append("product_id=").append(product_id.trim()).append("&");
		}

		mSign.append("product_name=").append(
				HWHttpResponse.stringToBase64(product_name.trim()));

		mSign.append("#").append(
				FunCellPlatformSdkApi.getInstance().mAppKey.trim());

		HWUtils.logError(TAG, "HWSign : " + mSign.toString());
		return HWHttpResponse.md5(mSign.toString().trim());
	}

	public void userCenter() {
		hw_access_token = HWPreferences.getData(FunSdkUiActivity.this,
				"hw_access_token");
		cp_id = FunCellPlatformSdkApi.getInstance().mAppId;
		create_time = Integer.valueOf(HWUtils.getTimestamp());

		postData.append("access_token=").append(hw_access_token).append("&");
		postData.append("cp_id=").append(cp_id).append("&");
		postData.append("time=").append(create_time).append("&");
		postData.append("sign=").append(getUserCenterHWSign());

		urlString = "/member";

		try {
			urlString = URLDecoder.decode(urlString, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 获取用户中心协议加密串
	 * 
	 * @return
	 */
	private String getUserCenterHWSign() {
		StringBuilder mSign = new StringBuilder();
		mSign.append("access_token=").append(hw_access_token.trim())
				.append("&");

		mSign.append("cp_id=").append(cp_id).append("&");
		mSign.append("time=").append(create_time);

		mSign.append("#").append(
				FunCellPlatformSdkApi.getInstance().mAppKey.trim());

		HWUtils.logError(TAG, "HWSign : " + mSign.toString());
		return HWHttpResponse.md5(mSign.toString().trim());
	}

	public void dissmiss() {
		finish();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return true;
		}
		return false;
	}

	private static RechargeCallBack mRechargeCallBack;

	public static void setRechargeCallBack(RechargeCallBack rechargeCallBack) {
		mRechargeCallBack = rechargeCallBack;
	}

	private static LogoutCallBack mLogoutCallBack;

	public static void setLogoutCallBack(LogoutCallBack logoutCallBack) {
		mLogoutCallBack = logoutCallBack;
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

}
