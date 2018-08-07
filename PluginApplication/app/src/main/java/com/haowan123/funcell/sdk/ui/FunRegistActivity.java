package com.haowan123.funcell.sdk.ui;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.haowan123.funcell.sdk.apiinterface.FunCellPlatformSdkApi;
import com.haowan123.funcell.sdk.util.FunLogStatsUtils;
import com.haowan123.funcell.sdk.util.FunLogStatsUtils.AtsLogTaskInfo;
import com.haowan123.funcell.sdk.util.FunLogStatsUtils.LogTaskInfo;
import com.haowan123.funcell.sdk.util.HWConstant;
import com.haowan123.funcell.sdk.util.HWHttpResponse;
import com.haowan123.funcell.sdk.util.HWPreferences;
import com.haowan123.funcell.sdk.util.HWUtils;
import com.haowan123.funcell.sdk.util.JsonObjectCoder;
import com.haowan123.funcell.sdk.util.RUtils;

public class FunRegistActivity extends Activity implements OnClickListener {
	private static final String TAG = "FunRegistActivity";
	private TextView registRerms;
	private ImageButton backLoginBtn;
	private ImageButton registErrorHintIbtn;
	private TextView registErrorHintMsgTv;
	private Button registBtn;
	private TextView registAccountTv;
	private TextView registPwdTv;
	private TextView registRePwdTv;
	private RelativeLayout registErrorHintRt;

	private String userAccount = null;
	private String pwd = null;
	private String rePwd = null;
	private String cpId = null;
	private String hw_platform = null;
	private String fid = null;

	private RegistAsyncTask registAsyncTask = null;
	private ProgressBar registProgressBar = null;
	private RelativeLayout registRelativeLayout;

	private static final int INTERVAL_TIME = 2000;

//	private static final String REGIST_URL_DOMIAN = "https://mainland-auth-channel.raink.com.cn/authorize";
	private static final String REGIST_URL_DOMIAN = "http://auth-beta.553.com/authorize";

	private int screenWidth;
	private int screenHeight;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(RUtils.layout(FunRegistActivity.this, "fun_regist"));
		HWUtils.setWindowDisplayMode(FunRegistActivity.this, 280, 0.8f, 0.9f);

		initViews();

		initEvents();
	}

	private void initViews() {

		screenWidth = getWindowManager().getDefaultDisplay().getWidth(); // 屏幕宽（像素，如：480px）
		screenHeight = getWindowManager().getDefaultDisplay().getHeight(); // 屏幕高（像素，如：800p）

		registRelativeLayout = (RelativeLayout) findViewById(RUtils.id(
				FunRegistActivity.this, "fun_regist_relativelayout"));

		// 注册服务条款
		registRerms = (TextView) findViewById(RUtils.id(FunRegistActivity.this,
				"fun_regist_terms_btn"));
		registRerms.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); // 下划线
		registRerms.getPaint().setAntiAlias(true);// 抗锯齿

		// 导航--返回到登录界面的按钮
		backLoginBtn = (ImageButton) findViewById(RUtils.id(
				FunRegistActivity.this, "fun_regist_back_login_btn"));

		// 注册错误提示
		registErrorHintIbtn = (ImageButton) findViewById(RUtils.id(
				FunRegistActivity.this, "fun_regist_error_hint_close_btn"));

		registErrorHintMsgTv = (TextView) findViewById(RUtils.id(
				FunRegistActivity.this, "fun_regist_error_hint_msg_tv"));

		registErrorHintRt = (RelativeLayout) findViewById(RUtils.id(
				FunRegistActivity.this, "fun_regist_error_hint_rt"));

		// 账号
		registAccountTv = (TextView) findViewById(RUtils.id(
				FunRegistActivity.this, "fun_regist_account_tv"));

		// 密码
		registPwdTv = (TextView) findViewById(RUtils.id(FunRegistActivity.this,
				"fun_regist_pwd_tv"));
		registRePwdTv = (TextView) findViewById(RUtils.id(
				FunRegistActivity.this, "fun_regist_re_pwd_tv"));

		// 注册按钮
		registBtn = (Button) findViewById(RUtils.id(FunRegistActivity.this,
				"fun_regist_btn"));
	}

	private void initEvents() {
		registRerms.setOnClickListener(this);
		backLoginBtn.setOnClickListener(this);
		registErrorHintIbtn.setOnClickListener(this);
		registBtn.setOnClickListener(this);

		registAccountTv.addTextChangedListener(registAccountTextWatcher);
		registPwdTv.addTextChangedListener(registPwdTextWatcher);
		registRePwdTv.addTextChangedListener(registRePwdTextWatcher);

	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == RUtils.id(FunRegistActivity.this, "fun_regist_terms_btn")) {
			if (HWUtils.isFastDoubleClick(1000)) {
				return;
			}
			Intent intent = new Intent(FunRegistActivity.this,
					FunSdkUiActivity.class);
			intent.putExtra("fun_action", "term");
			startActivity(intent);
		} else if (id == RUtils.id(FunRegistActivity.this,
				"fun_regist_back_login_btn")) {
			if (HWUtils.isFastDoubleClick(1000)) {
				return;
			}
			// Intent loginIntent = new Intent(FunRegistActivity.this,
			// FunLoginActivity.class);
			// startActivity(loginIntent);
			finish();
		} else if (id == RUtils.id(FunRegistActivity.this,
				"fun_regist_error_hint_close_btn")) {
			registErrorHintRt.setVisibility(View.GONE);
		} else if (id == RUtils.id(FunRegistActivity.this, "fun_regist_btn")) {
			// if(!registErrorHintRt.isShown()){
			// registErrorHintRt.setVisibility(View.VISIBLE);
			// }
			if (HWUtils.isFastDoubleClick(1000)) {
				return;
			}
			if (null == registAccountTv.getText()
					|| 0 == registAccountTv.getText().length()) {

				registAccountTv.setError(getResources().getString(
						RUtils.string(FunRegistActivity.this,
								"fun_login_txt_error_account_isempty")));
			}
			if (null == registPwdTv.getText()
					|| 0 == registPwdTv.getText().length()) {

				registPwdTv.setError(getResources().getString(
						RUtils.string(FunRegistActivity.this,
								"fun_login_txt_error_pwd_isempty")));
			}
			if (null == registRePwdTv.getText()
					|| 0 == registRePwdTv.getText().length()) {

				registRePwdTv.setError(getResources().getString(
						RUtils.string(FunRegistActivity.this,
								"fun_login_txt_error_repwd_isempty")));
			}
			if (!isAccountLengthLegal(registAccountTv.getText())) {
				return;
			}
			if (!isPwdLengthLegal(registPwdTv.getText())) {
				return;
			}
			if (!isPwdLengthLegal(registRePwdTv.getText())) {
				return;
			}
			if (isPwdAndRepwdEquals(registPwdTv.getText(),
					registRePwdTv.getText())) {
				registAsyncTask = new RegistAsyncTask();
				registAsyncTask.execute();
			}
		} else {
		}
	}

	/**
	 * TextWatcher：接口，继承它要实现其三个方法，分别为Text改变之前、改变的过程中、改变之后各自发生的动作
	 */
	private TextWatcher registAccountTextWatcher = new TextWatcher() {

		private CharSequence charSequence;
		private int editStart;
		private int editEnd;

		@Override
		public void beforeTextChanged(CharSequence s, int arg1, int arg2,
				int arg3) {
			charSequence = s;
		}

		@Override
		public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {

		}

		@Override
		public void afterTextChanged(Editable s) {

			// // 输入的时候，只有一个光标，那么这两个值应该是相等的。。。
			// editStart = registAccountTv.getSelectionStart();
			// editEnd = registAccountTv.getSelectionEnd();
			// int len = charSequence.length();

			// 限定EditText只能输入5-20个
			if (!isAccountLengthLegal(charSequence)) {
				registAccountTv.setError(getResources().getString(
						RUtils.string(FunRegistActivity.this,
								"fun_login_txt_hint_account_input_rule")));
			}

			// // 限定EditText只能输入20个数字
			// if (len > 20) {
			// // 默认光标在最前端，所以当输入第21个数字的时候，删掉（光标位置从21-1到21）的数字，这样就无法输入超过20个以后的数字
			// // s.delete(editStart - 1, editEnd);
			// registAccountTv.setError("请输入5-20位数字|字母|下划线");
			// } else if (len < 5) {
			// registAccountTv.setError("请输入5-20位数字|字母|下划线");
			// }
		}
	};

	private TextWatcher registPwdTextWatcher = new TextWatcher() {

		private CharSequence charSequence;

		@Override
		public void beforeTextChanged(CharSequence s, int arg1, int arg2,
				int arg3) {
			charSequence = s;
		}

		@Override
		public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {

		}

		@Override
		public void afterTextChanged(Editable s) {

			// 限定EditText只能输入6-20个
			if (isPwdLengthLegal(charSequence)) {
				String currentPwd = null == registPwdTv.getText()
						|| 0 == registPwdTv.getText().length() ? ""
						: registPwdTv.getText().toString();
				String currentRePwd = null == registRePwdTv.getText()
						|| 0 == registRePwdTv.getText().length() ? ""
						: registRePwdTv.getText().toString();
				if (currentRePwd.length() > 6
						&& !currentRePwd.equals(currentPwd)) {
					registRePwdTv.setError(getResources().getString(
							RUtils.string(FunRegistActivity.this,
									"fun_login_txt_error_pwd_unsame")));
				}
			} else {
				registPwdTv.setError(getResources().getString(
						RUtils.string(FunRegistActivity.this,
								"fun_login_txt_hint_pwd_input_rule")));
			}
		}
	};

	private TextWatcher registRePwdTextWatcher = new TextWatcher() {

		private CharSequence charSequence;

		@Override
		public void beforeTextChanged(CharSequence s, int arg1, int arg2,
				int arg3) {
			charSequence = s;
		}

		@Override
		public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {

		}

		@Override
		public void afterTextChanged(Editable s) {
			// 限定EditText只能输入6-20个
			if (isPwdLengthLegal(charSequence)) {
				String currentPwd = null == registPwdTv.getText()
						|| 0 == registPwdTv.getText().length() ? ""
						: registPwdTv.getText().toString();
				String currentRePwd = null == registRePwdTv.getText()
						|| 0 == registRePwdTv.getText().length() ? ""
						: registRePwdTv.getText().toString();
				if (!currentRePwd.equals(currentPwd)) {
					registRePwdTv.setError(getResources().getString(
							RUtils.string(FunRegistActivity.this,
									"fun_login_txt_error_pwd_unsame")));
				}
			} else {
				registRePwdTv.setError(getResources().getString(
						RUtils.string(FunRegistActivity.this,
								"fun_login_txt_hint_pwd_input_rule")));
			}
		}
	};

	/**
	 * 验证密码长度是否合法
	 * 
	 * @param charSequence
	 * @return true|false
	 */
	private boolean isPwdLengthLegal(CharSequence charSequence) {
		if (null == charSequence) {
			return false;
		}

		int len = charSequence.length();

		// 限定EditText只能输入6-20个时,返回true
		if (len < 6 || len > 20) {
			return false;
		}

		return true;
	}

	/**
	 * 校验两次输入密码是否一致
	 * 
	 * @param pwdCharSequence
	 * @param rePwdCharSequence
	 * @return
	 */
	private boolean isPwdAndRepwdEquals(CharSequence pwdCharSequence,
			CharSequence rePwdCharSequence) {
		if (null == pwdCharSequence || null == rePwdCharSequence) {
			return false;
		}

		if (rePwdCharSequence.toString().equals(pwdCharSequence.toString())) {
			return true;
		}

		return false;
	}

	/**
	 * 验证用户名长度是否合法
	 * 
	 * @param charSequence
	 * @return true|false
	 */
	private boolean isAccountLengthLegal(CharSequence charSequence) {
		if (null == charSequence) {
			return false;
		}

		int len = charSequence.length();

		// 限定EditText只能输入5-20个时,返回true
		if (len < 5 || len > 20) {
			return false;
		}

		return true;
	}

	private class RegistAsyncTask extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			registProgressBar = new ProgressBar(FunRegistActivity.this, null,
					android.R.attr.progressBarStyle);
			registProgressBar.setVisibility(View.VISIBLE);

			LayoutParams progressBarParams = new LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			progressBarParams.addRule(RelativeLayout.CENTER_IN_PARENT);
			registRelativeLayout.addView(registProgressBar, progressBarParams);
		}

		@Override
		protected String doInBackground(Void... params) {

			hw_platform = HWUtils.getChannelCode(FunRegistActivity.this);

			userAccount = registAccountTv.getText().toString();

			// pwd = registPwdTv.getText().toString();
			//
			// rePwd = registRePwdTv.getText().toString();

			pwd = HWHttpResponse.stringToBase64(registPwdTv.getText()
					.toString());
			rePwd = HWHttpResponse.stringToBase64(registRePwdTv.getText()
					.toString());
			cpId = FunCellPlatformSdkApi.getInstance().mAppId;

			HWUtils.logError(TAG, "getHWSign() = " + getHWSign());

			HashMap<String, String> postData = new HashMap<String, String>();
			postData.put("grant_type", "register");
			postData.put("cp_id", cpId);
			postData.put("username", userAccount);
			postData.put("password", pwd);// base64
			postData.put("platform", hw_platform);
			postData.put("confirm_password", rePwd);// base64
			postData.put("sign", getHWSign());
			postData.put("ext_data", getHWExtData());

			// String responseData = null;
			String responseData = HWHttpResponse.doSendHttpPostResponse(
					REGIST_URL_DOMIAN, postData);
			// HWUtils.logError(TAG, "responseData = " + responseData);
			//
			// try {
			// TimeUnit.SECONDS.sleep(5);
			// } catch (InterruptedException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }

			return responseData;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			// {"error_code":"A1005","message":"account exist","level":"error","data":{"recommend_username":"qqqqq123"}}
			if (registProgressBar != null)
				registProgressBar.setVisibility(View.GONE);
			registProgressBar = null;

			if (null == result) {
				HWUtils.logError(TAG, "regist response is null...");
				return;
			}

			Map<String, Object> responseDataMap = (Map<String, Object>) JsonObjectCoder
					.decode(result, null);
			String errorCode = null == responseDataMap.get("error_code") ? ""
					: responseDataMap.get("error_code").toString();
			if ("P1111".equals(errorCode)) {// success
				HWUtils.logError(TAG, "regist success ,errorCode = "
						+ errorCode);
				FunCellPlatformSdkApi.getInstance().setLogin(true);

				Map<String, String> successDataMap = (Map<String, String>) responseDataMap
						.get("data");
				String hw_access_token = successDataMap.get("access_token");
				String hw_refresh_token = successDataMap.get("refresh_token");
				fid = successDataMap.get("fid");

				saveUserInfo(hw_access_token, hw_refresh_token);

				Intent intent = new Intent();
				intent.putExtra("hw_access_token", hw_access_token);
				intent.putExtra("fid", fid);

				setResult(HWConstant.RESULT_CODE_REGIST_SUCCESS, intent);


				// cps userlogin
				FunLogStatsUtils.getInstance().setIdentity(userAccount);
				AtsLogTaskInfo atsLogTaskInfo = new AtsLogTaskInfo();
				atsLogTaskInfo.setMethod(AtsLogTaskInfo.ATS_METHOD_USER_LOGIN);
				atsLogTaskInfo.setIdentity(userAccount);
				FunLogStatsUtils.submit(new FunLogStatsUtils.AtsLogTaskRunnable(FunRegistActivity.this, atsLogTaskInfo));
				
				finish();
			} else if ("A1004".equals(errorCode)) {// 用户名已存在
				registAccountTv.setError(getResources().getString(
						RUtils.string(FunRegistActivity.this,
								"fun_login_txt_error_pwd_unsame"))
						+ "[" + errorCode + "]");
				registAccountTv.requestFocus();
				HWUtils.logError(TAG, "confirm password error ,errorCode = "
						+ errorCode);
			} else if ("A1005".equals(errorCode)) {// 用户名已存在
				registAccountTv.setError(getResources().getString(
						RUtils.string(FunRegistActivity.this,
								"fun_login_txt_error_account_exsit"))
						+ "[" + errorCode + "]");
				registAccountTv.requestFocus();
				HWUtils.logError(TAG, "account is exist ,errorCode = "
						+ errorCode);
			} else if ("A1006".equals(errorCode)) {// 用户注册失败
				registAccountTv.setError(getResources().getString(
						RUtils.string(FunRegistActivity.this,
								"fun_login_txt_error_regist_fail"))
						+ "[" + errorCode + "]");
				registAccountTv.requestFocus();
				HWUtils.logError(TAG, "regist fail ,errorCode = " + errorCode);
			} else if ("A1009".equals(errorCode)) {// 账号格式错误
				registAccountTv.setError(getResources().getString(
						RUtils.string(FunRegistActivity.this,
								"fun_login_txt_error_account_format_error"))
						+ "[" + errorCode + "]");
				registAccountTv.requestFocus();
				HWUtils.logError(TAG, "account is error ,errorCode = "
						+ errorCode);
			} else {
				registAccountTv.setError(getResources().getString(
						RUtils.string(FunRegistActivity.this,
								"fun_login_txt_error_regist_fail"))
						+ "[" + errorCode + "]");
				registAccountTv.requestFocus();
				HWUtils.logError(TAG, "regist error ,errorCode = " + errorCode);
			}
		}

	};

	/**
	 * 获取ext_data参数
	 * 
	 * @return
	 */
	private String getHWExtData() {
		JSONObject extDataJSONObject = new JSONObject();
		try {
			extDataJSONObject.put("device_id",
					HWUtils.getImei(FunRegistActivity.this));
			extDataJSONObject.put("os", HWUtils.getPhoneVersion());
			extDataJSONObject.put("model", HWUtils.getPhoneModel());
			extDataJSONObject.put("network",
					HWUtils.getNetType(FunRegistActivity.this));
		} catch (JSONException e) {
			e.printStackTrace();
		}

		HWUtils.logError(TAG, "extdata = " + extDataJSONObject.toString());

		return HWHttpResponse.stringToBase64(extDataJSONObject.toString());
	}

	/**
	 * 获取协议加密串
	 * 
	 * @return
	 */
	private String getHWSign() {
		StringBuilder sign = new StringBuilder();
		sign.append("confirm_password=").append(rePwd).append("&");
		sign.append("cp_id=").append(cpId).append("&");
		sign.append("ext_data=").append(getHWExtData()).append("&");
		sign.append("grant_type=register&");
		sign.append("password=").append(pwd).append("&");
		sign.append("platform=").append(hw_platform).append("&");
		sign.append("username=").append(userAccount);
		sign.append("#").append(FunCellPlatformSdkApi.getInstance().mAppKey);

		HWUtils.logError(TAG, "HWSign : " + sign.toString());

		return HWHttpResponse.stringTo32LowerCaseMD5(sign.toString());
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		HWUtils.setWindowDisplayMode(FunRegistActivity.this, 280, 0.8f, 0.9f);
	}

	/**
	 * 保存用户信息
	 * 
	 * @param hw_access_token
	 * @param hw_refresh_token
	 */
	private void saveUserInfo(String hw_access_token, String hw_refresh_token) {
		HWPreferences
				.addData(FunRegistActivity.this, "hw_account", userAccount);
		HWPreferences.addData(FunRegistActivity.this, "hw_access_token",
				hw_access_token);
		HWPreferences.addData(FunRegistActivity.this, "hw_refresh_token",
				hw_refresh_token);

		String hwAccounts = HWPreferences.getData(FunRegistActivity.this,
				"hw_accounts");
		String hwRefreshTokens = HWPreferences.getData(FunRegistActivity.this,
				"hw_refresh_tokens");

		if (null == hwAccounts || 0 == hwAccounts.length()) {
			hwAccounts = userAccount;
		} else {
			hwAccounts = hwAccounts + "," + userAccount;
		}

		if (null == hwRefreshTokens || 0 == hwRefreshTokens.length()) {
			hwRefreshTokens = hw_refresh_token;
		} else {
			hwRefreshTokens = hwRefreshTokens + "," + hw_refresh_token;
		}

		HWPreferences
				.addData(FunRegistActivity.this, "hw_accounts", hwAccounts);
		HWPreferences.addData(FunRegistActivity.this, "hw_refresh_tokens",
				hwRefreshTokens);
	}
}
