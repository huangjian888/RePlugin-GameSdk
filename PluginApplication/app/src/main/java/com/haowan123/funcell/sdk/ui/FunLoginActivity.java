package com.haowan123.funcell.sdk.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.haowan123.funcell.sdk.apiinterface.FunCellPlatformSdkApi;
import com.haowan123.funcell.sdk.apiinterface.LoginCallBack;
import com.haowan123.funcell.sdk.util.FunErrorCode;
import com.haowan123.funcell.sdk.util.FunLogStatsUtils;
import com.haowan123.funcell.sdk.util.FunLogStatsUtils.AtsLogTaskInfo;
import com.haowan123.funcell.sdk.util.FunLogStatsUtils.LogTaskInfo;
import com.haowan123.funcell.sdk.util.HWConstant;
import com.haowan123.funcell.sdk.util.HWHttpResponse;
import com.haowan123.funcell.sdk.util.HWPreferences;
import com.haowan123.funcell.sdk.util.HWUtils;
import com.haowan123.funcell.sdk.util.JsonObjectCoder;
import com.haowan123.funcell.sdk.util.RUtils;

public class FunLoginActivity extends Activity implements OnClickListener {
	private static final String TAG = "FunLoginActivity";
	private ListView listView;

	private TextView loginRerms;
	private ImageButton backGameBtn;
	private Button loginBtn;
	private TextView loginAccountTv;
	private TextView loginPwdTv;
	private TextView findPwdTv, registTv, quickIntoGameTv;

	private String userAccount = null;
	private String pwd = null;
	private String rePwd = null;
	private String cpId = null;
	private String hw_refresh_token = null;
	private String hw_platform = null;
	private String fid = null;

	private LoginAsyncTask loginAsyncTask = null;
	private ProgressBar loginProgressBar = null;
	private RelativeLayout loginRelativeLayout;
	private LinearLayout loginAccountLinearLayout;

	private static final String QUICK_LOGIN_DEFAULT_PWD = "**********";

	private boolean isQuickLogin = false;
	private int loginMode = 0;

	private static final int LOGIN_MODE_DEFAULT = 0;
	private static final int LOGIN_MODE_GUEST = 1;
	private static final int LOGIN_MODE_QUICK = 2;

	private static final int INTERVAL_TIME = 2000;

//	private static final String REGIST_URL_DOMIAN = "https://mainland-auth-channel.raink.com.cn/authorize";
	private static final String REGIST_URL_DOMIAN = "http://auth-beta.553.com/authorize";
	protected static LoginCallBack mLoginCallBack;

	private boolean isWindowMode = true;

	// private String[] userNames = new String[] {};
	// private String[] aTokens = new String[] {};
	// private String[] reTokens = new String[] {};
	private Map<String, String> userNameMap = new HashMap<String, String>();

	public static void setOnLoginCallBack(LoginCallBack loginCallBack) {
		mLoginCallBack = loginCallBack;
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// this.setTheme(android.R.style.Animation_Activity);
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		// // 全屏
		// getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		// WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(RUtils.layout(FunLoginActivity.this, "fun_login"));

		HWUtils.setWindowDisplayMode(FunLoginActivity.this, 280, 0.8f, 0.9f);

		initViews();

		initEvents();

		List<String> data = getData();

		adapter = new DropdownAdapter(this, data);
		listView = new ListView(this);
		TextView textView = new TextView(this);
		listView.setEmptyView(textView);
		listView.setAdapter(adapter);

		ColorDrawable colorDrawable = new ColorDrawable(Color.GRAY);
		listView.setDivider(colorDrawable);
		listView.setDividerHeight(1);

		// textView
		//

		selectUserImageBtn = (ImageButton) findViewById(RUtils.id(
				FunLoginActivity.this, "imgbtn"));
		selectUserImageBtn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// 构造方法写在onCreate方法体中会因为布局没有加载完毕而得不到宽高。
				int len = 0;
				if (3 > nameList.size()) {
					len = nameList.size();
				} else {
					len = 3;
				}
				if (null == pop) {
					// 创建一个在输入框下方的popup
					pop = new PopupWindow(listView, loginAccountLinearLayout
							.getWidth(), len
							* loginAccountLinearLayout.getHeight());
					pop.showAsDropDown(loginAccountLinearLayout);
					selectUserImageBtn.setImageResource(RUtils.drawable(
							FunLoginActivity.this, "fun_login_drop_up"));
				} else {
					if (pop.isShowing()) {
						pop.dismiss();
						selectUserImageBtn.setImageResource(RUtils.drawable(
								FunLoginActivity.this, "fun_login_drop_button"));
					} else {
						pop.setHeight(len
								* loginAccountLinearLayout.getHeight());
						pop.setWidth(loginAccountLinearLayout.getWidth());
						pop.showAsDropDown(loginAccountLinearLayout);
						selectUserImageBtn.setImageResource(RUtils.drawable(
								FunLoginActivity.this, "fun_login_drop_up"));
					}
				}
			}
		});
	}

	private void initViews() {
		// listView = (ListView) findViewById(R.id.fun_login_about_action_list);

		loginRelativeLayout = (RelativeLayout) findViewById(RUtils.id(
				FunLoginActivity.this, "fun_login_relativelayout"));

		// 导航--返回到登录界面的按钮
		backGameBtn = (ImageButton) findViewById(RUtils.id(
				FunLoginActivity.this, "fun_login_back_game_btn"));

		// 账号
		loginAccountLinearLayout = (LinearLayout) findViewById(RUtils.id(
				FunLoginActivity.this, "fun_login_account_linearLayout"));
		loginAccountTv = (TextView) findViewById(RUtils.id(
				FunLoginActivity.this, "fun_login_account_et"));

		// 密码
		loginPwdTv = (TextView) findViewById(RUtils.id(FunLoginActivity.this,
				"fun_login_pwd_et"));

		// 登录按钮
		loginBtn = (Button) findViewById(RUtils.id(FunLoginActivity.this,
				"fun_login_btn"));

		findPwdTv = (TextView) findViewById(RUtils.id(FunLoginActivity.this,
				"fun_login_find_pwd"));
		registTv = (TextView) findViewById(RUtils.id(FunLoginActivity.this,
				"fun_login_regist"));
		quickIntoGameTv = (TextView) findViewById(RUtils.id(
				FunLoginActivity.this, "fun_login_quick_into_game"));
	}

	private void initEvents() {
		// getListView();

		// access_token
		// refresh_token
		hw_refresh_token = HWPreferences.getData(FunLoginActivity.this,
				"hw_refresh_token");
		String hw_account = HWPreferences.getData(FunLoginActivity.this,
				"hw_account");
		HWUtils.logError(TAG, hw_refresh_token + "   ,   " + hw_account);
		if (null != hw_refresh_token && 0 < hw_refresh_token.trim().length()) {
			loginAccountTv.setText(hw_account);
			userAccount = hw_account;
			loginPwdTv.setText(QUICK_LOGIN_DEFAULT_PWD);
			quickIntoGameTv.setVisibility(View.INVISIBLE);
			isQuickLogin = true;
		}

		backGameBtn.setOnClickListener(this);
		loginBtn.setOnClickListener(this);
		findPwdTv.setOnClickListener(this);
		registTv.setOnClickListener(this);
		quickIntoGameTv.setOnClickListener(this);

		loginAccountTv.addTextChangedListener(loginAccountTextWatcher);
		loginPwdTv.addTextChangedListener(loginPwdTextWatcher);
	}

	private void getListView() {
		// 生成动态数组，加入数据
		ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
		final boolean isAllowGuestLogin = FunCellPlatformSdkApi.getInstance()
				.isAllowGuestLogin();
		if (isAllowGuestLogin) {
			HashMap<String, Object> guestMap = new HashMap<String, Object>();
			guestMap.put(
					"ItemTitle",
					getResources().getString(
							RUtils.string(FunLoginActivity.this,
									"fun_login_txt_guest_title")));
			listItem.add(guestMap);
		}
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("ItemTitle",
				getResources().getString(
						RUtils.string(FunLoginActivity.this,
								"fun_login_txt_regist_title")));
		listItem.add(map);
		HashMap<String, Object> map1 = new HashMap<String, Object>();
		map1.put(
				"ItemTitle",
				getResources().getString(
						RUtils.string(FunLoginActivity.this,
								"fun_login_txt_forget_title")));
		listItem.add(map1);

		// 生成适配器的Item和动态数组对应的元素
		SimpleAdapter listItemAdapter = new SimpleAdapter(this, listItem,// 数据源
				RUtils.layout(FunLoginActivity.this,
						"fun_login_about_action_list_item"),// ListItem的XML实现
				// 动态数组与TextView对应的子项
				new String[] { "ItemTitle" },
				// TextView ID
				new int[] { RUtils.id(FunLoginActivity.this,
						"fun_login_about_action_list_item_text_title") });

		listView.setAdapter(listItemAdapter);

		setListViewHeightBasedOnChildren(listView);

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				switch (position) {
				case 0:
					if (isAllowGuestLogin) {
						// TODO 游客登录功能
					} else {
						startActivityForResult(
								new Intent(FunLoginActivity.this,
										FunRegistActivity.class),
								HWConstant.REGIST_REQUEST_CODE);
					}
					finish();
					break;
				case 1:
					if (isAllowGuestLogin) {
						startActivityForResult(
								new Intent(FunLoginActivity.this,
										FunRegistActivity.class),
								HWConstant.REGIST_REQUEST_CODE);
					} else {
						Intent intent = new Intent(FunLoginActivity.this,
								FunSdkUiActivity.class);
						intent.putExtra("fun_action", "findpwd");
						startActivity(intent);

					}
					finish();
					break;
				case 2:
					if (isAllowGuestLogin) {
						Intent findPwdIntent = new Intent(
								FunLoginActivity.this, FunSdkUiActivity.class);
						findPwdIntent.putExtra("fun_action", "findpwd");
						startActivity(findPwdIntent);
					}
					finish();
					break;

				default:
					break;
				}
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == HWConstant.REGIST_REQUEST_CODE) {
			if (resultCode == HWConstant.RESULT_CODE_REGIST_SUCCESS) {
				HWUtils.logError(TAG, "hwsdk: login success...");

				String hwAccessToken = data.getStringExtra("hw_access_token");
				String mFid = data.getStringExtra("fid");
				// 登录成功回调
				mLoginCallBack.loginSuccess(hwAccessToken, mFid);

				finish();
			}
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == RUtils.id(FunLoginActivity.this, "fun_login_back_game_btn")) {
			if (HWUtils.isFastDoubleClick(1000)) {
				return;
			}
			mLoginCallBack.loginFail(FunErrorCode.FUN_ERROR_CODE_LOGIN_CANCEL,
					"cancel login");
			finish();
		} else if (id == RUtils.id(FunLoginActivity.this, "fun_login_btn")) {
			if (HWUtils.isFastDoubleClick(1000)) {
				return;
			}
			if (null == loginAccountTv.getText()
					|| 0 == loginAccountTv.getText().length()) {

				loginAccountTv.setError(getResources().getString(
						RUtils.string(FunLoginActivity.this,
								"fun_login_txt_error_account_isempty")));
			}
			if (null == loginPwdTv.getText()
					|| 0 == loginPwdTv.getText().length()) {

				loginPwdTv.setError(getResources().getString(
						RUtils.string(FunLoginActivity.this,
								"fun_login_txt_error_pwd_isempty")));
			}
			if (!isAccountLengthLegal(loginAccountTv.getText())) {
				return;
			}
			if (!isPwdLengthLegal(loginPwdTv.getText())) {
				return;
			}
			loginAsyncTask = new LoginAsyncTask();
			loginAsyncTask.execute(isQuickLogin);
		} else if (id == RUtils.id(FunLoginActivity.this, "fun_login_find_pwd")) {
			if (HWUtils.isFastDoubleClick(1000)) {
				return;
			}
			Intent findPwdIntent = new Intent(FunLoginActivity.this,
					FunSdkUiActivity.class);
			findPwdIntent.putExtra("fun_action", "findpwd");
			startActivity(findPwdIntent);
		} else if (id == RUtils.id(FunLoginActivity.this, "fun_login_regist")) {
			if (HWUtils.isFastDoubleClick(1000)) {
				return;
			}
			startActivityForResult(new Intent(FunLoginActivity.this,
					FunRegistActivity.class), HWConstant.REGIST_REQUEST_CODE);
		} else if (id == RUtils.id(FunLoginActivity.this,
				"fun_login_quick_into_game")) {
			if (HWUtils.isFastDoubleClick(1000)) {
				return;
			}
			loginAsyncTask = new LoginAsyncTask();
			loginAsyncTask.execute(LOGIN_MODE_GUEST);
		} else {
		}

	}

	/**
	 * TextWatcher：接口，继承它要实现其三个方法，分别为Text改变之前、改变的过程中、改变之后各自发生的动作
	 */
	private TextWatcher loginAccountTextWatcher = new TextWatcher() {

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
				loginAccountTv.setError(getResources().getString(
						RUtils.string(FunLoginActivity.this,
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

	private TextWatcher loginPwdTextWatcher = new TextWatcher() {

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
			isQuickLogin = false;
			// 限定EditText只能输入6-20个
			if (!isPwdLengthLegal(charSequence)) {
				loginPwdTv.setError(getResources().getString(
						RUtils.string(FunLoginActivity.this,
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

	private class LoginAsyncTask extends AsyncTask<Object, Void, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			loginProgressBar = new ProgressBar(FunLoginActivity.this, null,
					android.R.attr.progressBarStyle);
			loginProgressBar.setVisibility(View.VISIBLE);

			LayoutParams progressBarParams = new LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			progressBarParams.addRule(RelativeLayout.CENTER_IN_PARENT);
			loginRelativeLayout.addView(loginProgressBar, progressBarParams);
		}

		@Override
		protected String doInBackground(Object... params) {

			HWUtils.logError(TAG, "params[0] = " + params[0]);

			Boolean isCurrentQuickLogin = false;
			if (params[0] instanceof Boolean) {
				isCurrentQuickLogin = Boolean.valueOf(params[0].toString());

			} else if (params[0] instanceof Integer) {
				loginMode = Integer.valueOf(params[0].toString());
			}

			userAccount = loginAccountTv.getText().toString();

			hw_platform = HWUtils.getChannelCode(FunLoginActivity.this);

			// pwd = registPwdTv.getText().toString();
			//
			// rePwd = registRePwdTv.getText().toString();

			pwd = HWHttpResponse
					.stringToBase64(loginPwdTv.getText().toString());
			cpId = FunCellPlatformSdkApi.getInstance().mAppId;

			// HWUtils.logError(TAG, "getHWSign() = "
			// + getHWSign(isCurrentQuickLogin));

			HashMap<String, String> postData = new HashMap<String, String>();

			postData.put("cp_id", cpId);
			if (isCurrentQuickLogin) {
				hw_refresh_token = userNameMap.get(userAccount);

				postData.put("grant_type", "quick");
				postData.put("refresh_token", hw_refresh_token);
				postData.put("username", userAccount);

			} else if (LOGIN_MODE_DEFAULT == loginMode) {
				postData.put("grant_type", "login");
				postData.put("password", pwd);// base64
				postData.put("username", userAccount);
			} else if (LOGIN_MODE_GUEST == loginMode) {
				postData.put("grant_type", "guest");

			}

			postData.put("platform", hw_platform);
			postData.put("sign", getHWSign(isCurrentQuickLogin));
			postData.put("ext_data", getHWExtData());

			// String responseData = null;
			String responseData = HWHttpResponse.doSendHttpPostResponse(
					REGIST_URL_DOMIAN, postData);
			HWUtils.logError(TAG, "responseData = " + responseData);

			return responseData;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			// {"error_code":"A1005","message":"account exist","level":"error","data":{"recommend_username":"qqqqq123"}}
			if (loginProgressBar != null)
				loginProgressBar.setVisibility(View.GONE);
			loginProgressBar = null;

			if (null == result) {
				loginAccountTv.setError(getResources().getString(
						RUtils.string(FunLoginActivity.this,
								"fun_login_txt_error_fail")));
				loginAccountTv.requestFocus();
				HWUtils.logError(TAG, "login response is null...");
				return;
			}

			Map<String, Object> responseDataMap = (Map<String, Object>) JsonObjectCoder
					.decode(result, null);
			String errorCode = null == responseDataMap.get("error_code") ? ""
					: responseDataMap.get("error_code").toString();
			if ("P1111".equals(errorCode)) {// success
				HWUtils.logError(TAG, "login success ,errorCode = " + errorCode);

				FunCellPlatformSdkApi.getInstance().setLogin(true);

				Map<String, String> successDataMap = (Map<String, String>) responseDataMap
						.get("data");
				String hw_access_token = successDataMap.get("access_token");
				String hw_refresh_token = successDataMap.get("refresh_token");

				String guestUserName = successDataMap.get("username");
				String guestPwd = successDataMap.get("password");

				fid = successDataMap.get("fid");
				if (null != guestUserName && 0 < guestUserName.trim().length()) {

					userAccount = guestUserName;
				}

				HWUtils.logError(TAG, "login success ,guestUserName = "
						+ guestUserName + " , guestPwd = " + guestPwd);

				saveUserInfo(hw_access_token, hw_refresh_token);
				String eventCode = LogTaskInfo.EVENT_CODE_USER_LOGIN;
				String accounType = "official";
				if (loginMode == 1) {
					Intent intent = new Intent(FunLoginActivity.this,
							FunPhotoActivity.class);
					intent.putExtra("token", hw_access_token);
					intent.putExtra("username", guestUserName);
					intent.putExtra("pwd", guestPwd);
					intent.putExtra("fid", fid);
					startActivity(intent);
					eventCode = LogTaskInfo.EVENT_CODE_USER_REGISTER;
					accounType = "guest";
				} else {
					mLoginCallBack.loginSuccess(hw_access_token, fid);
				}


				// cps userlogin
				FunLogStatsUtils.getInstance().setIdentity(userAccount);
				AtsLogTaskInfo atsLogTaskInfo = new AtsLogTaskInfo();
				atsLogTaskInfo.setMethod(AtsLogTaskInfo.ATS_METHOD_USER_LOGIN);
				atsLogTaskInfo.setIdentity(userAccount);
				FunLogStatsUtils.submit(new FunLogStatsUtils.AtsLogTaskRunnable(
						FunLoginActivity.this, atsLogTaskInfo));

				finish();
			} else if ("A1001".equals(errorCode)) {// 账号不存在
				loginAccountTv.setError(getResources().getString(
						RUtils.string(FunLoginActivity.this,
								"fun_login_error_pwd"))
						+ "[" + errorCode + "]");
				loginAccountTv.requestFocus();
				HWUtils.logError(TAG, "account is not exist ,errorCode = "
						+ errorCode);
			} else if ("A1002".equals(errorCode)) {// 用户无权登录
				loginAccountTv.setError(getResources().getString(
						RUtils.string(FunLoginActivity.this,
								"fun_login_txt_error_account_illegal"))
						+ "[" + errorCode + "]");
				loginAccountTv.requestFocus();
				HWUtils.logError(TAG, "account no permission ,errorCode = "
						+ errorCode);
			} else if ("A1003".equals(errorCode)) {// 密码错误
				loginAccountTv.setError(getResources().getString(
						RUtils.string(FunLoginActivity.this,
								"fun_login_error_pwd"))
						+ "[" + errorCode + "]");
				loginAccountTv.requestFocus();
				HWUtils.logError(TAG, "pwd is error ,errorCode = " + errorCode);
			} else if ("A1009".equals(errorCode)) {// 账号格式错误
				loginAccountTv.setError(getResources().getString(
						RUtils.string(FunLoginActivity.this,
								"fun_login_txt_error_account_format_error"))
						+ "[" + errorCode + "]");
				loginAccountTv.requestFocus();
				HWUtils.logError(TAG, "pwd is error ,errorCode = " + errorCode);
			} else if ("A1019".equals(errorCode)) {// 账号格式错误
				loginAccountTv.setText("");
				loginAccountTv.setError(getResources().getString(
						RUtils.string(FunLoginActivity.this,
								"fun_login_txt_error_token_expired"))
						+ "[" + errorCode + "]");
				loginAccountTv.requestFocus();
				saveUserInfo(userAccount, "");
				HWUtils.logError(TAG, "token is expired ,errorCode = "
						+ errorCode);
			} else {// 其他失败情况
				loginAccountTv.setError(getResources().getString(
						RUtils.string(FunLoginActivity.this,
								"fun_login_txt_error_fail"))
						+ "[" + errorCode + "]");
				loginAccountTv.requestFocus();
				HWUtils.logError(TAG, "login error ,errorCode = " + errorCode);
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
					HWUtils.getImei(FunLoginActivity.this));
			extDataJSONObject.put("os", HWUtils.getPhoneVersion());
			extDataJSONObject.put("model", HWUtils.getPhoneModel());
			extDataJSONObject.put("network",
					HWUtils.getNetType(FunLoginActivity.this));
			extDataJSONObject.put("platform", hw_platform);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		HWUtils.logError(TAG, "extdata = " + extDataJSONObject.toString());

		return HWHttpResponse.stringToBase64(extDataJSONObject.toString());
	}

	/**
	 * 保存当前登录用户信息
	 * 
	 * @param hw_access_token
	 * @param hw_refresh_token
	 */
	private void saveUserInfo(String hw_access_token, String hw_refresh_token) {
		HWPreferences.addData(FunLoginActivity.this, "hw_account", userAccount);
		HWPreferences.addData(FunLoginActivity.this, "hw_access_token",
				hw_access_token);
		HWPreferences.addData(FunLoginActivity.this, "hw_refresh_token",
				hw_refresh_token);

		if (userNameMap.containsKey(userAccount)) {
			userNameMap.remove(userAccount);
		}
		userNameMap.put(userAccount, hw_refresh_token);

		String hwAccounts = null;
		String hwRefreshTokens = null;

		Set<Entry<String, String>> entrySet = userNameMap.entrySet();
		for (Entry<String, String> entry : entrySet) {
			String userName = entry.getKey();
			String reToken = entry.getValue();

			if (null == hwAccounts || 0 == hwAccounts.trim().length()) {
				hwAccounts = userName;
				hwRefreshTokens = reToken;
			} else {
				hwAccounts = hwAccounts + "," + userName;
				hwRefreshTokens = hwRefreshTokens + "," + reToken;
			}
		}

		HWPreferences.addData(FunLoginActivity.this, "hw_accounts", hwAccounts);
		HWPreferences.addData(FunLoginActivity.this, "hw_refresh_tokens",
				hwRefreshTokens);
	}

	/**
	 * 获取协议加密串
	 * 
	 * @return
	 */
	private String getHWSign(boolean isCurrentQuickLogin) {
		StringBuilder sign = new StringBuilder();
		sign.append("cp_id=").append(cpId).append("&");
		sign.append("ext_data=").append(getHWExtData()).append("&");
		if (isCurrentQuickLogin) {
			sign.append("grant_type=quick&");
			sign.append("platform=").append(hw_platform).append("&");
			sign.append("refresh_token=").append(hw_refresh_token).append("&");
			sign.append("username=").append(userAccount);

		} else if (LOGIN_MODE_DEFAULT == loginMode) {
			sign.append("grant_type=login&");
			sign.append("password=").append(pwd).append("&");
			sign.append("platform=").append(hw_platform).append("&");
			sign.append("username=").append(userAccount);
		} else if (LOGIN_MODE_GUEST == loginMode) {
			sign.append("grant_type=guest&");
			sign.append("platform=").append(hw_platform);

		}

		sign.append("#").append(FunCellPlatformSdkApi.getInstance().mAppKey);

		HWUtils.logError(TAG, "HWSign : " + sign.toString());

		return HWHttpResponse.stringTo32LowerCaseMD5(sign.toString());
	}

	public void setListViewHeightBasedOnChildren(ListView mListView) {

		ListAdapter listAdapter = mListView.getAdapter();

		if (listAdapter == null) {
			return;
		}

		int totalHeight = 0;

		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, mListView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = mListView.getLayoutParams();

		params.height = totalHeight
				+ (mListView.getDividerHeight() * (listAdapter.getCount() - 1));

		int margin = HWUtils.dip2px(this, 10);

		((MarginLayoutParams) params)
				.setMargins(margin, margin, margin, margin); // 可删除

		mListView.setLayoutParams(params);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		HWUtils.setWindowDisplayMode(FunLoginActivity.this, 280, 0.8f, 0.9f);

		if (null != pop && pop.isShowing()) {
			pop.dismiss();
			selectUserImageBtn.setImageResource(RUtils.drawable(
					FunLoginActivity.this, "fun_login_drop_button"));
		}

	}

	/** 用于显示popupWindow内容的适配器 */
	class DropdownAdapter extends BaseAdapter {

		public DropdownAdapter(Context context, List<String> list) {
			this.context = context;
			this.list = list;
		}

		public int getCount() {
			return null == list || list.isEmpty() ? 0 : list.size();
		}

		public Object getItem(int position) {
			return null;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {

			final int currentPosition = position;
			if (null == convertView) {
				layoutInflater = LayoutInflater.from(context);
				convertView = layoutInflater.inflate(
						RUtils.layout(FunLoginActivity.this, "fun_list_row"),
						null);
			}
			close = (ImageButton) convertView.findViewById(RUtils.id(
					FunLoginActivity.this, "fun_close_row"));
			content = (TextView) convertView.findViewById(RUtils.id(
					FunLoginActivity.this, "fun_text_row"));
			final String editContent = list.get(position);
			content.setText(list.get(position).toString());

			if (null != list.get(position)
					&& !TextUtils.isEmpty(loginAccountTv.getText())
					&& list.get(position).toString()
							.equals(loginAccountTv.getText().toString())) {
				close.setImageResource(RUtils.drawable(FunLoginActivity.this,
						"fun_login_selected"));
				close.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						loginAccountTv.setText(editContent);
						pop.dismiss();
						selectUserImageBtn.setImageResource(RUtils.drawable(
								FunLoginActivity.this, "fun_login_drop_button"));

						userAccount = editContent;

					}
				});

			} else {
				close.setImageResource(RUtils.drawable(FunLoginActivity.this,
						"fun_login_delete"));
				close.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						removeAccountDataFromList(currentPosition);
						adapter.notifyDataSetChanged();
					}
				});
			}

			content.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					loginAccountTv.setText(editContent);
					pop.dismiss();
					selectUserImageBtn.setImageResource(RUtils.drawable(
							FunLoginActivity.this, "fun_login_drop_button"));
					userAccount = editContent;
				}
			});

			return convertView;
		}

		private Context context;
		private LayoutInflater layoutInflater;
		private List<String> list;
		private TextView content;
		private ImageButton close;
	}

	private ImageButton selectUserImageBtn;
	private List<String> nameList;
	private DropdownAdapter adapter;
	private PopupWindow pop;

	@Override
	protected void onStop() {
		super.onStop();
		if (null != pop) {
			pop.dismiss();
			pop = null;
			selectUserImageBtn.setImageResource(RUtils.drawable(
					FunLoginActivity.this, "fun_login_drop_button"));
		}
	}

	public List<String> getData() {
		nameList = new ArrayList<String>();
		String hwAccounts = HWPreferences.getData(FunLoginActivity.this,
				"hw_accounts");
		String hwAccessTokens = HWPreferences.getData(FunLoginActivity.this,
				"hw_access_tokens");
		String hwRefreshTokens = HWPreferences.getData(FunLoginActivity.this,
				"hw_refresh_tokens");

		if (null == hwAccounts || 0 == hwAccounts.trim().length()
				|| null == hwRefreshTokens || 0 == hwRefreshTokens.trim().length()) {
			return null;
		}
		String[] userNames = hwAccounts.split(",");
		String[] reTokens = hwRefreshTokens.split(",");
		
		int length = userNames.length >= reTokens.length ? reTokens.length : userNames.length;
		
//		Log.e(TAG,"userNames:"+userNames.toString());
//		Log.e(TAG,"reTokens:"+reTokens.toString());
//		
//		Log.e(TAG,"userNames.length:"+length);
//		Log.e(TAG,"reTokens.length:"+reTokens.length);
		
		for (int i = 0; i < length; i++) {
			userNameMap.put(userNames[i], reTokens[i]);
		}

		List<String> list = Arrays.asList(userNames);

		nameList = new ArrayList<String>(list);

		return nameList;
	}

	public void removeAccountDataFromList(int position) {

		if (null == nameList || nameList.isEmpty()) {
			return;
		}
		String userName = nameList.get(position);

		nameList.remove(position);

		userNameMap.remove(userName);

		String hwAccounts = null;
		String hwRefreshTokens = null;

		Set<Entry<String, String>> entrySet = userNameMap.entrySet();
		for (Entry<String, String> entry : entrySet) {
			String name = entry.getKey();
			String reToken = entry.getValue();

			if (null == name || 0 == name.length()) {
				hwAccounts = name;
				hwRefreshTokens = reToken;
			} else {
				hwAccounts = hwAccounts + "," + name;
				hwRefreshTokens = hwRefreshTokens + "," + reToken;
			}
		}

		HWPreferences.addData(FunLoginActivity.this, "hw_accounts", hwAccounts);
		HWPreferences.addData(FunLoginActivity.this, "hw_refresh_tokens",
				hwRefreshTokens);

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
			mLoginCallBack.loginFail(FunErrorCode.FUN_ERROR_CODE_LOGIN_CANCEL, "login cancel");
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	
}
