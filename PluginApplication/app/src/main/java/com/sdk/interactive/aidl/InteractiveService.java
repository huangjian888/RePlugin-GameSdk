package com.sdk.interactive.aidl;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import com.haowan123.funcell.sdk.apiinterface.FunCellPlatformSdkApi;
import com.haowan123.funcell.sdk.apiinterface.InitCallBack;
import com.haowan123.funcell.sdk.apiinterface.LogoutCallBack;
import com.raink.korea.platform.android.widget.FloatView;
import com.sdk.interactive.aidl.IInitCallBack;
import com.sdk.interactive.aidl.IInteractiveService;
import com.sdk.interactive.aidl.IPayCallBack;
import com.sdk.interactive.aidl.ISessionCallBack;
import com.sdk.interactive.base.BusinessActivity;
import com.sdk.interactive.base.CacheingActivity;
import com.sdk.interactive.base.BaseApplication;
import com.sdk.interactive.base.Constant;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;

public class InteractiveService extends Service {
	private String TAG= "InteractiveService";
	private IInitCallBack mIInitCallBack;
	private ISessionCallBack mISessionCallBack;
	private IPayCallBack mIPayCallBack;

	private String mHostInfo;
	private Activity mActivity;
	private Timer mTimer;
	private TimerTask mTimerTask;
	private FloatView mFloatView;

	Handler mH = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case Constant.H_Init:
				callInit();
				break;
			case Constant.H_Login:
				boolean hasAlertPermission = (boolean) msg.obj;
				if(hasAlertPermission){
					mFloatView = new FloatView(getApplicationContext());
				}
				callLogin();
				break;
			case Constant.H_Logout:
				callLogout();
				break;
			case Constant.H_Pay:
				PayInfo info = (PayInfo) msg.obj;
				callPay(info);
				break;
			case Constant.H_TimerDestory:
				timerDestory();
				break;
			case Constant.H_OnResume:
				if (mFloatView != null && BaseApplication.getInstance().getmLoginFlag()) {
					mFloatView.show();
				}
				startCheckApp();
				break;
			case Constant.H_OnPause:
				if (mFloatView != null) {
					mFloatView.hide();
				}
				break;
			case Constant.H_Exit:

				break;
			}
		}

	};

	private void callInit(){
		if(mHostInfo != null){
			try {
				JSONObject json = new JSONObject(mHostInfo);
				String appid = json.getString("appid");
				String appkey = json.getString("appkey");
				FunCellPlatformSdkApi.getInstance().setWindowMode(true);
				FunCellPlatformSdkApi.getInstance().init(appid,appkey,new InitCallBack(){
					@Override
					public void initSuccess() {
						try {
							mIInitCallBack.onInitSuccess("init success");
						} catch (RemoteException e) {
							e.printStackTrace();
						}
					}

					@Override
					public void initFail(String errorCode, String errorMsg) {
						try {
							mIInitCallBack.onInitFailure("errorCode:"+errorCode);
						} catch (RemoteException e) {
							e.printStackTrace();
						}
					}
				},new LogoutCallBack(){
					@Override
					public void logout() {
						try {
							if (mFloatView != null){
								mFloatView.hide();
							}
							BaseApplication.getInstance().setmLoginFlag(false);
							mISessionCallBack.onLogout();
						} catch (RemoteException e) {
							e.printStackTrace();
						}
					}
				});
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}

	}

	private void callLogin(){
		Intent localIntent = new Intent(this, BusinessActivity.class);
		localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		localIntent.putExtra(Constant.B_Business, Constant.B_Business_Action_Login);
		startActivity(localIntent);
	}

	private void callLogout(){
		FunCellPlatformSdkApi.getInstance().logout();
	}

	private void callPay(PayInfo info){
		Intent localIntent = new Intent(this, BusinessActivity.class);
		localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		localIntent.putExtra(Constant.B_Business, Constant.B_Business_Action_Pay);
		localIntent.putExtra(Constant.P_Pay_Info,info);
		startActivity(localIntent);
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
//		startCheckApp();
		return new InteractiveServiceImpl();
	}
	
	/**
	 * App应用检测，需在登录之后，显示浮标后进行调用，确保浮标已经显示出来
	 */
	private void startCheckApp(){
		if(mTimer == null){
			mTimer = new Timer();
			mTimerTask = new TimerTask(){

				@Override
				public void run() {
					// TODO Auto-generated method stub
					if(mHostInfo != null){
						try {
							JSONObject json = new JSONObject(mHostInfo);
							String hostPackageName = json.getString("HostPackageName");
							boolean isCurrentApp = hostPackageName.equalsIgnoreCase(getTopActivityPkgName(InteractiveService.this));
							Log.e(TAG, "isCurrentApp:"+isCurrentApp);
							if(!isCurrentApp){
								Message msg = mH.obtainMessage();
								msg.what = Constant.H_TimerDestory;
								mH.sendMessage(msg);
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			};
			mTimer.schedule(mTimerTask, 0L, 3000L);
		}
	}
	
	/**
	 * 这个方法获取最近运行任何中最上面的一个应用的包名,<br>
	 * 进行了api版本的判断,然后利用不同的方法获取包名,具有兼容性
	 * 
	 * @param context
	 *            上下文对象
	 * @return 返回包名,如果出现异常或者获取失败返回""
	 */
	public static String getTopAppInfoPackageName(Context context) {
		if (Build.VERSION.SDK_INT < 21) { // 如果版本低于22
			// 获取到activity的管理的类
			ActivityManager m = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
			// 获取最近的一个运行的任务的信息
			List<RunningTaskInfo> tasks = m.getRunningTasks(1);
			if (tasks != null && tasks.size() > 0) { // 如果集合不是空的
				// 返回任务栈中最上面的一个
				RunningTaskInfo info = m.getRunningTasks(1).get(0);
				// 获取到应用的包名
				// String packageName =
				// info.topActivity.getPackageName();
				return info.baseActivity.getPackageName();
			} else {
				return "";
			}
		} else {

			final int PROCESS_STATE_TOP = 2;
			try {
				// 获取正在运行的进程应用的信息实体中的一个字段,通过反射获取出来
				Field processStateField = ActivityManager.RunningAppProcessInfo.class.getDeclaredField("processState");
				// 获取所有的正在运行的进程应用信息实体对象
				List<ActivityManager.RunningAppProcessInfo> processes = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getRunningAppProcesses();
				// 循环所有的进程,检测某一个进程的状态是最上面,也是就最近运行的一个应用的状态的时候,就返回这个应用的包名
				for (ActivityManager.RunningAppProcessInfo process : processes) {
					if (process.importance <= ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
							&& process.importanceReasonCode == 0) {
						int state = processStateField.getInt(process);
						if (state == PROCESS_STATE_TOP) { // 如果这个实体对象的状态为最近的运行应用
							String[] packname = process.pkgList;
							// 返回应用的包名
							return packname[0];
						}
					}
				}
			} catch (Exception e) {
			}
			return "";
		}
	}
	
	
	public static String getTopActivityPkgName(Context context) {
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		if (isLollipop()) {
			return getNewTopActivityPkgName(activityManager);
		}
		return getOldTopActivityPkgName(activityManager);
	}

	public static boolean isLollipop() {
		return Build.VERSION.SDK_INT >= 21;
	}

	private static String getOldTopActivityPkgName(ActivityManager activityManager) {
		try {
			ActivityManager.RunningTaskInfo info = (ActivityManager.RunningTaskInfo) activityManager.getRunningTasks(1).get(0);
			String packageName = info.topActivity.getPackageName();
			if (packageName == null) {
				return "";
			}
			return packageName;
		} catch (Exception e) {
		}
		return "";
	}

	private static String getNewTopActivityPkgName(ActivityManager activityManager) {
		try {
			List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
			if (appProcesses == null) {
				return "";
			}
			for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
				int importance = appProcess.importance;
				String processName = appProcess.processName;
				if ((importance == 100) || (importance == 200)) {
					return processName;
				}
			}
			return "";
		} catch (Exception e) {
		}
		return "";
	}
	
	
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		Log.e(TAG, "onCreate begin");
		Intent localIntent = new Intent(this, CacheingActivity.class);
		localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(localIntent);
//		mFloatView = new FloatView(getApplicationContext());
		Log.e(TAG, "onCreate end");
	}

	private void timerDestory(){
		Log.e(TAG, "timerDestory");
		if (mFloatView != null) {
			mFloatView.hide();
//			mFloatView = null;
		}
		if(mTimer != null){
			mTimer.cancel();
			mTimer = null;
		}
		if(mTimerTask != null){
			mTimerTask.cancel();
			mTimerTask = null;
		}
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		timerDestory();
		Log.e(TAG, "onDestroy");
		BaseApplication.getInstance().setmCacheingActivity(null);
		super.onDestroy();
	}

	@Override
	public boolean onUnbind(Intent intent) {
		// TODO Auto-generated method stub
		timerDestory();
		Log.e(TAG, "onUnbind");
		return super.onUnbind(intent);
	}
	
	public class InteractiveServiceImpl extends IInteractiveService.Stub{

		@Override
		public void Init(String hostInfo,IInitCallBack initCallBack,
				ISessionCallBack sessionCallBack, IPayCallBack payCallBack)
				throws RemoteException {
			// TODO Auto-generated method stub
			Log.e(TAG,"Init...");
			mIInitCallBack = initCallBack;
			mISessionCallBack = sessionCallBack;
			mIPayCallBack = payCallBack;
			BaseApplication.getInstance().setmISessionCallBack(mISessionCallBack);
			BaseApplication.getInstance().setmIPayCallBack(mIPayCallBack);
			BaseApplication.getInstance().setmIInitCallBack(mIInitCallBack);
			mHostInfo = hostInfo;
			Message msg = mH.obtainMessage();
			msg.what = Constant.H_Init;
			mH.sendMessage(msg);
		}

		@Override
		public void Login(boolean hasAlertPermission) throws RemoteException {
			// TODO Auto-generated method stub
			Log.e(TAG,"Login...");
			Message msg = mH.obtainMessage();
			msg.what = Constant.H_Login;
			msg.obj = hasAlertPermission;
			mH.sendMessage(msg);
		}

		@Override
		public void Logout() throws RemoteException {
			// TODO Auto-generated method stub
			Log.e(TAG,"Logout...");
			Message msg = mH.obtainMessage();
			msg.what = Constant.H_Logout;
			mH.sendMessage(msg);
		}

		@Override
		public void Pay(PayInfo info) throws RemoteException {
			// TODO Auto-generated method stub
			Log.e(TAG,"Pay...");
			Message msg = mH.obtainMessage();
			msg.what = Constant.H_Pay;
			msg.obj = info;
			mH.sendMessage(msg);
		}

		@Override
		public void setDatas() throws RemoteException {
			// TODO Auto-generated method stub
			Log.e(TAG,"setDatas...");
			Message msg = mH.obtainMessage();
			msg.what = Constant.H_SetData;
			mH.sendMessage(msg);
		}

		@Override
		public int Exit(IExitCallBack exitCallBack) throws RemoteException {
			// TODO Auto-generated method stub

//			Message msg = mH.obtainMessage();
//			msg.what = Constant.H_Exit;
//			mH.sendMessage(msg);
			exitCallBack.onConfirm();
			return 0;
		}

		@Override
		public void ShowFloat() throws RemoteException {

		}

		@Override
		public void HideFloat() throws RemoteException {

		}

		@Override
		public void onCreate() throws RemoteException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onResume() throws RemoteException {
			// TODO Auto-generated method stub
			Log.e(TAG, "onResume");
			Message msg = mH.obtainMessage();
			msg.what = Constant.H_OnResume;
			mH.sendMessage(msg);
		}

		@Override
		public void onPause() throws RemoteException {
			// TODO Auto-generated method stub
			Log.e(TAG, "onPause");
			Message msg = mH.obtainMessage();
			msg.what = Constant.H_OnPause;
			mH.sendMessage(msg);
		}

		@Override
		public void onDestroy() throws RemoteException {
			// TODO Auto-generated method stub
			Log.e(TAG, "onPause");
		}

		@Override
		public void onActivityResult(int requestCode, int resultCode, Intent data) throws RemoteException {
			Log.e(TAG, "onActivityResult");
		}

		@Override
		public void callFunction(String FunctionName, String parameter)
				throws RemoteException {
			// TODO Auto-generated method stub
			Log.e(TAG, "callFunction");
			
		}
	}
	
}
