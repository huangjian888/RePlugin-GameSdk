package com.haowan123.funcell.sdk.util;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import android.content.Context;

import com.haowan123.funcell.sdk.apiinterface.FunCellPlatformSdkApi;

/**
 * 日志统计工具类
 * 
 */
public class FunLogStatsUtils {
	private static ExecutorService executorService = null;
	private static final int workThread = 3;

	private static final String LOG_URL = "http://log.553.com/client";
	private static final String ATS_LOG_URL = "http://ats.haowan123.com/index.php?r=/api2/";
	private static final boolean IS_LOG_ENABLE = false;
	private static final boolean IS_ATS_LOG_ENABLE = true;
	private static FunLogStatsUtils instance = null;
	
	public static FunLogStatsUtils getInstance()
	  {
	    if (instance == null) {
	      instance = new FunLogStatsUtils();
	    }
	    return instance;
	  }
	
	private static synchronized ExecutorService getExecutorService() {
		if (null == executorService) {
			executorService = Executors.newFixedThreadPool(workThread);

		}

		return executorService;
	}

	public static Future<?> submit(Callable<?> callable) {
		return getExecutorService().submit(callable);
	}

	public static void submit(Runnable runnable) {
		getExecutorService().submit(runnable);
	}

	public static <T> Future<T> submit(Runnable runnable, T t) {
		return getExecutorService().submit(runnable, t);
	}

	public static void shutdown() {
		if (!getExecutorService().isShutdown()) {
			getExecutorService().shutdown();

		}
	}

	public static void shutdownNow() {
		if (!isShutdown()) {
			getExecutorService().shutdownNow();
		}
	}

	public static boolean isShutdown() {
		return getExecutorService().isShutdown();
	}

	/**
	 * 日志统计任务
	 * 
	 */
	public static class LogTaskRunnable implements Runnable {
		private static final String LOG_ERROR_CODE_SUCCESS = "P1111";
		private LogTaskInfo mLogTaskInfo;
		private Context mContext;

		public LogTaskRunnable(Context context, LogTaskInfo logTaskInfo) {
			this.mLogTaskInfo = logTaskInfo;
			this.mContext = context;
		}

		public String getTotalTime() {
			long totalTime = Long.valueOf(HWUtils.getTimestamp())
					- Long.valueOf(HWUtils.beginTime);
			return totalTime + "";
		}

		@Override
		public void run() {

			if (!IS_LOG_ENABLE) {
				return;
			}

			Map<String, String> postData = new HashMap<String, String>();

			String accessToken = mLogTaskInfo.getAccessToken();
			String accountId = mLogTaskInfo.getAccountId();
			String extData = mLogTaskInfo.getExtData();

			if (null != accessToken && 0 < accessToken.trim().length()) {
				postData.put("access_token", accessToken);
			}

			if (null != accountId && 0 < accountId.trim().length()) {
				postData.put("account_id", accountId);
			}

			if (null != extData && 0 < extData.trim().length()) {
				postData.put("ext_data", mLogTaskInfo.getExtData());
			}

			postData.put("cp_id", FunCellPlatformSdkApi.getInstance().mAppId);
			postData.put("cp_version", HWUtils.getAppVersionName(mContext));
			postData.put("platform", HWUtils.getChannelCode(mContext));
			postData.put("device_id", HWUtils.getDeviceUID(mContext));
			postData.put("network", HWUtils.getNetType(mContext));
			postData.put("event_code", mLogTaskInfo.getEventCode());
			postData.put("model", HWUtils.getPhoneModel());
			postData.put("os", HWUtils.getPhoneVersion());
			postData.put("resolution", HWUtils.getScreenPiexl(mContext));
			postData.put("language", HWUtils.getLocalLangage());
			postData.put("operators",
					HWUtils.getMobileServiceProvider(mContext));
			postData.put("create_time", HWUtils.getTimestamp());
			postData.put("total_time", getTotalTime());

			postData.put("sign", getSign(mLogTaskInfo));

			// HWUtils.logError("HWSDKLOG", "postData = "+postData);

			String logResponse = HWHttpResponse.doSendHttpPostResponse(LOG_URL,
					postData);

			if (null == logResponse || 0 == logResponse.trim().length()) {
				return;
			}

			Map<String, String> result = (Map<String, String>) JsonObjectCoder.decode(logResponse, null);
			String errorCode = result.get("error_code");
			String message = result.get("message");

			HWUtils.logError("HWSDKLOG", "errorCode = " + errorCode
					+ " , message = " + message);

			if (LOG_ERROR_CODE_SUCCESS.equals(errorCode)) {
				HWUtils.logError("HWSDKLOG", "日志上报成功");
			} else {
				HWUtils.logError("HWSDKLOG", "日志上报失败");

				// TODO 记录文件
			}
		}

		public String getSign(LogTaskInfo logTaskInfo) {
			String accessToken = logTaskInfo.getAccessToken();
			String accountId = logTaskInfo.getAccountId();
			String extData = logTaskInfo.getExtData();

			StringBuilder signBuilder = new StringBuilder();
			if (null != accessToken && 0 < accessToken.trim().length()) {
				signBuilder.append("access_token=").append(accessToken)
						.append("&");
			}

			if (null != accountId && 0 < accountId.trim().length()) {
				signBuilder.append("account_id=").append(accountId).append("&");
			}

			signBuilder.append("cp_id=")
					.append(FunCellPlatformSdkApi.getInstance().mAppId)
					.append("&");
			signBuilder.append("cp_version=")
					.append(HWUtils.getAppVersionName(mContext)).append("&");
			signBuilder.append("create_time=").append(HWUtils.getTimestamp())
					.append("&");

			signBuilder.append("device_id=")
					.append(HWUtils.getDeviceUID(mContext)).append("&");

			signBuilder.append("event_code=")
					.append(mLogTaskInfo.getEventCode()).append("&");

			if (null != extData && 0 < extData.trim().length()) {
				signBuilder.append("ext_data=").append(extData).append("&");
			}

			signBuilder.append("language=").append(HWUtils.getLocalLangage())
					.append("&");
			signBuilder.append("model=").append(HWUtils.getPhoneModel())
					.append("&");
			signBuilder.append("network=").append(HWUtils.getNetType(mContext))
					.append("&");

			signBuilder.append("operators=")
					.append(HWUtils.getMobileServiceProvider(mContext))
					.append("&");
			signBuilder.append("os=").append(HWUtils.getPhoneVersion())
					.append("&");

			signBuilder.append("platform=")
					.append(HWUtils.getChannelCode(mContext)).append("&");
			signBuilder.append("resolution=")
					.append(HWUtils.getScreenPiexl(mContext)).append("&");
			signBuilder.append("total_time=").append(getTotalTime());
			signBuilder.append("#").append(
					FunCellPlatformSdkApi.getInstance().mAppKey);
			;

			// HWUtils.logError("HWSDKLOG", "sign = "+signBuilder.toString());
			return HWUtils.stringTo32LowerCaseMD5(signBuilder.toString());
		}

	}

	// sdk_device_active 设备激活
	// sdk_user_login 用户登录
	// sdk_user_register 用户注册
	// sdk_user_modify 用户信息更新
	// sdk_user_forgetpass 忘记密码页面打开
	// sdk_pay_start 支付开始
	// sdk_pay_fail 支付失败
	// sdk_pay_exit 支付中断退出
	// sdk_pay_success 支付成功

	public static class LogTaskInfo {
		/**
		 * 设备激活
		 */
		public static final String EVENT_CODE_DEVICE_ACTIVE = "sdk_device_active";
		/**
		 * 用户登录
		 */
		public static final String EVENT_CODE_USER_LOGIN = "sdk_user_login";
		/**
		 * 用户注册
		 */
		public static final String EVENT_CODE_USER_REGISTER = "sdk_user_register";
		/**
		 * 用户信息更新
		 */
		public static final String EVENT_CODE_MODIFY = "sdk_user_modify";
		/**
		 * 忘记密码页面打开
		 */
		public static final String EVENT_CODE_USER_FORGETPASS = "sdk_user_forgetpass";
		/**
		 * 支付开始
		 */
		public static final String EVENT_CODE_PAY_START = "sdk_pay_start";
		/**
		 * 支付失败
		 */
		public static final String EVENT_CODE_PAY_FAIL = "sdk_pay_fail";
		/**
		 * 支付中断退出
		 */
		public static final String EVENT_CODE_PAY_EXIT = "sdk_pay_exit";
		/**
		 * 支付成功
		 */
		public static final String EVENT_CODE_PAY_SUCCESS = "sdk_pay_success";

		// 登陆access_token ，可选
		private String accessToken;
		// 用户标识,可选
		private String accountId;
		// cp编号
		private String cpId;
		// 设备语言
		private String language;
		// 应用版本号
		private String cpVersion;
		// 渠道信息
		private String platform;
		// 设备编号 MD5(model+device_id+resolution+os)
		private String deviceId;
		// 当前网络标识 wifi、2g、3g、4g
		private String network;
		// 事件类型
		private String eventCode;
		// 机型
		private String model;
		// 系统类型版本
		private String os;
		// 分辨率
		private String resolution;
		// 运营商 10086,10011,10000,0
		private String operators;
		// 日志发起时间
		private String createTime;
		// 登陆到发起总时间(秒)
		private String totleTime;
		// 附加参数为JSON 没有附加参数时为空 ，可选
		private String extData;

		public String getAccessToken() {
			return accessToken;
		}

		public void setAccessToken(String accessToken) {
			this.accessToken = accessToken;
		}

		public String getAccountId() {
			return accountId;
		}

		public void setAccountId(String accountId) {
			this.accountId = accountId;
		}

		public String getCpId() {
			return cpId;
		}

		public void setCpId(String cpId) {
			this.cpId = cpId;
		}

		public String getPlatform() {
			return platform;
		}

		public void setPlatform(String platform) {
			this.platform = platform;
		}

		public String getDeviceId() {
			return deviceId;
		}

		public void setDeviceId(String deviceId) {
			this.deviceId = deviceId;
		}

		public String getNetwork() {
			return network;
		}

		public void setNetwork(String network) {
			this.network = network;
		}

		public String getEventCode() {
			return eventCode;
		}

		public void setEventCode(String eventCode) {
			this.eventCode = eventCode;
		}

		public String getModel() {
			return model;
		}

		public void setModel(String model) {
			this.model = model;
		}

		public String getOs() {
			return os;
		}

		public void setOs(String os) {
			this.os = os;
		}

		public String getResolution() {
			return resolution;
		}

		public void setResolution(String resolution) {
			this.resolution = resolution;
		}

		public String getOperators() {
			return operators;
		}

		public void setOperators(String operators) {
			this.operators = operators;
		}

		public String getCreateTime() {
			return createTime;
		}

		public void setCreateTime(String createTime) {
			this.createTime = createTime;
		}

		public String getTotleTime() {
			return totleTime;
		}

		public void setTotleTime(String totleTime) {
			this.totleTime = totleTime;
		}

		public String getExtData() {
			return extData;
		}

		public void setExtData(String extData) {
			this.extData = extData;
		}

		public String getLanguage() {
			return language;
		}

		public void setLanguage(String language) {
			this.language = language;
		}

		public String getCpVersion() {
			return cpVersion;
		}

		public void setCpVersion(String cpVersion) {
			this.cpVersion = cpVersion;
		}

	}

	public static class AtsLogTaskRunnable implements Runnable {
		// private static final String LOG_ERROR_CODE_SUCCESS = "P1111";
		private AtsLogTaskInfo mAtsLogTaskInfo;
		private Context mContext;

		public AtsLogTaskRunnable(Context context, AtsLogTaskInfo atsLogTaskInfo) {
			this.mAtsLogTaskInfo = atsLogTaskInfo;
			this.mContext = context;
		}

		@Override
		public void run() {
			if (!IS_ATS_LOG_ENABLE) {
				return;
			}

			String method = mAtsLogTaskInfo.getMethod();

			if (null == method || 0 == method.length()) {

				HWUtils.logError("HWSDKLOG", "ats log method is null");

				return;
			}

			HWUtils.logError("HWSDKLOG", "ats log method : [" + method
					+ "] begin...");

			Map<String, String> postData = new HashMap<String, String>();

			postData.put("appid", FunCellPlatformSdkApi.getInstance().mAppId);
			// postData.put("identity", mAtsLogTaskInfo.getIdentity());

			if (AtsLogTaskInfo.ATS_METHOD_CPS.equals(method)) {
//				postData.put("device", HWUtils.getDeviceUID(mContext));
//				idfa
//				imie
//				mac
//				aid
				postData.put("idfa", "");
				postData.put("imei", HWUtils.getImei(mContext));
				postData.put("mac", HWUtils.getLocalMacAddress());
				postData.put("aid", HWUtils.getAndroidId(mContext));
				postData.put("channel1_id", HWUtils.getChannelCode(mContext));

			} else if (AtsLogTaskInfo.ATS_METHOD_USER_LOGIN.equals(method)) {
//				postData.put("device", HWUtils.getDeviceUID(mContext));
//				idfa
//				imie
//				mac
//				aid
				postData.put("idfa", "");
				postData.put("imei", HWUtils.getImei(mContext));
				postData.put("mac", HWUtils.getLocalMacAddress());
				postData.put("aid", HWUtils.getAndroidId(mContext));
				postData.put("identity", mAtsLogTaskInfo.getIdentity());
				// postData.put("ext", mAtsLogTaskInfo.getExt());

			} else if (AtsLogTaskInfo.ATS_METHOD_RECHARGE.equals(method)) {
				postData.put("amount", mAtsLogTaskInfo.getAmount());
				postData.put("identity", mAtsLogTaskInfo.getIdentity());

			} else if (AtsLogTaskInfo.ATS_METHOD_LEVEL_UP.equals(method)) {
				postData.put("identity", mAtsLogTaskInfo.getIdentity());
				postData.put("level", mAtsLogTaskInfo.getLevel());
			}

			postData.put("sign", getAtsSign(mAtsLogTaskInfo));

			HWUtils.logError("HWSDKLOG", "ats log postData = "+postData+" , url = "+ATS_LOG_URL+method);

			String logResponse = HWHttpResponse.doSendHttpPostResponse(
					ATS_LOG_URL + method, postData);

			HWUtils.logError("HWSDKLOG", "ats log logResponse = " + logResponse);
			HWUtils.logError("HWSDKLOG", "ats log method : [" + method
					+ "] end...");
		}

		private String getAtsSign(AtsLogTaskInfo atsLogTaskInfo) {

			String method = mAtsLogTaskInfo.getMethod();

			StringBuilder signBuilder = new StringBuilder();

			signBuilder.append(FunCellPlatformSdkApi.getInstance().mAppId);

			if (AtsLogTaskInfo.ATS_METHOD_CPS.equals(method)) {
//				signBuilder.append(HWUtils.getDeviceUID(mContext));
				signBuilder.append(HWUtils.getChannelCode(mContext));

			} else if (AtsLogTaskInfo.ATS_METHOD_USER_LOGIN.equals(method)) {

				signBuilder.append(mAtsLogTaskInfo.getIdentity());
//				signBuilder.append(HWUtils.getDeviceUID(mContext));

			} else if (AtsLogTaskInfo.ATS_METHOD_RECHARGE.equals(method)) {
				signBuilder.append(mAtsLogTaskInfo.getIdentity());
				signBuilder.append(mAtsLogTaskInfo.getAmount());

			} else if (AtsLogTaskInfo.ATS_METHOD_LEVEL_UP.equals(method)) {
				signBuilder.append(mAtsLogTaskInfo.getIdentity());

				signBuilder.append(mAtsLogTaskInfo.getLevel());
			}

			signBuilder.append(FunCellPlatformSdkApi.getInstance().mAppKey);

			 HWUtils.logError("HWSDKLOG", "ats log sign = "+signBuilder.toString());
			return HWUtils.stringTo32LowerCaseMD5(signBuilder.toString());
		}

	}

	public static class AtsLogTaskInfo {
		public static final String ATS_METHOD_USER_LOGIN = "userlogin";
		public static final String ATS_METHOD_LEVEL_UP = "levelup";
		public static final String ATS_METHOD_RECHARGE = "recharge";
		public static final String ATS_METHOD_CPS = "haowancps";

		private String appId;
		private String identity;// 账号
		private String channelId;
		private String channelName;
		private String amount;
		private String level;
		private String ext;
		private String extData;
		private String method;
		private String device;// 设备标识
		private String idfa;
		private String imei;
		private String mac;
		private String aid;
		
		
		

		public String getIdfa() {
			return idfa;
		}

		public void setIdfa(String idfa) {
			this.idfa = idfa;
		}

		public String getImei() {
			return imei;
		}

		public void setImei(String imei) {
			this.imei = imei;
		}

		public String getMac() {
			return mac;
		}

		public void setMac(String mac) {
			this.mac = mac;
		}

		public String getAid() {
			return aid;
		}

		public void setAid(String aid) {
			this.aid = aid;
		}

		public String getAppId() {
			return appId;
		}

		public void setAppId(String appId) {
			this.appId = appId;
		}

		public String getIdentity() {
			return identity;
		}

		public void setIdentity(String identity) {
			this.identity = identity;
		}

		public String getChannelId() {
			return channelId;
		}

		public void setChannelId(String channelId) {
			this.channelId = channelId;
		}

		public String getChannelName() {
			return channelName;
		}

		public void setChannelName(String channelName) {
			this.channelName = channelName;
		}

		public String getAmount() {
			return amount;
		}

		public void setAmount(String amount) {
			this.amount = amount;
		}

		public String getLevel() {
			return level;
		}

		public void setLevel(String level) {
			this.level = level;
		}

		public String getExt() {
			return ext;
		}

		public void setExt(String ext) {
			this.ext = ext;
		}

		public String getExtData() {
			return extData;
		}

		public void setExtData(String extData) {
			this.extData = extData;
		}

		public String getMethod() {
			return method;
		}

		public void setMethod(String method) {
			this.method = method;
		}

		public String getDevice() {
			return device;
		}

		public void setDevice(String device) {
			this.device = device;
		}

	}

	static String Identity;
	public static void setIdentity(String identity) {
		Identity = identity;
	} 
	
	public static String getIdentity() {
		return Identity;
	} 
	
	public static void main(String[] args) {
		// submit(new LogTaskRunnable(new LogTaskInfo()));
	}
}
