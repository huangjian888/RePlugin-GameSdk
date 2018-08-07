package com.sdk.interactive.core;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.qihoo360.loader2.Constant;
import com.qihoo360.replugin.RePlugin;
import com.qihoo360.replugin.model.PluginInfo;
import com.sdk.interactive.aidl.IInteractiveService;
import com.sdk.interactive.aidl.IPermissionsCallBack;
import com.sdk.interactive.aidl.IPermissionsManager;
import com.sdk.interactive.aidl.IRegisterPluginBinder;
import com.sdk.interactive.aidl.IRegisterPluginBinderCallBack;
import com.sdk.interactive.aidl.PayInfo;
import com.sdk.interactive.bean.SdkConfig;
import com.sdk.interactive.core.callback.RegisterPluginBinderCallBack;
import com.sdk.interactive.core.callback.SdkExitCallBack;
import com.sdk.interactive.core.callback.SdkInitCallBack;
import com.sdk.interactive.core.callback.SdkPayCallBack;
import com.sdk.interactive.core.callback.SdkSessionCallBack;
import com.sdk.interactive.core.permissions.SdkPermissionsBuilder;
import com.sdk.interactive.core.permissions.SdkPermissionsCallbacks;
import com.sdk.interactive.core.permissions.SdkPermissionsManager;
import com.sdk.interactive.util.BaseUtil;
import com.sdk.interactive.util.SdkNotProguard;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import static android.R.attr.name;
import static com.qihoo360.replugin.RePlugin.fetchClassLoader;
import static com.qihoo360.replugin.RePlugin.getPluginInfo;

@SdkNotProguard
public class BaseController {
	private String TAG = "BaseController";
	private static BaseController mInstance;
	private Activity mContext;
	private SdkInitCallBack mInitCallBack;
	private SdkSessionCallBack mSessionCallBack;
	private SdkPayCallBack mPayCallBack;
	private JSONObject mJson;
	private SdkPermissionsManager mSdkPermissionsManager;
	private IPermissionsCallBack mPermissionsCallBack;
//	private boolean mInitFlag = false;
//	private IInteractiveService mIInteractiveService;


	public static BaseController getInstance() {
		if (mInstance == null) {
			synchronized (BaseController.class) {
				if (mInstance == null) {
					mInstance = new BaseController();
				}
			}
		}
		return mInstance;
	}
	
	public void init(final Activity ctx, final SdkConfig config, final SdkInitCallBack initCallBack, final SdkSessionCallBack sessionCallBack, final SdkPayCallBack payCallBack){
		mContext = ctx;
		mInitCallBack = initCallBack;
		mSessionCallBack = sessionCallBack;
		mPayCallBack = payCallBack;
		mJson = new JSONObject(config);

		/**
		 *check plugin
		 */
		PluginInfo plugin = RePlugin.getPluginInfo("funcellplugin");
		Log.e(TAG,"plugin:"+plugin);
		if(plugin != null){
			_init();
		}else{
			/**
			 * install plugin
			 */
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						InputStream assestInput = ctx.getAssets().open("funcellsdk.apk");
						File cacheFile = new File(ctx.getCacheDir(), "funcellsdk.apk");
						if(BaseUtil.copyFile(assestInput,cacheFile.getAbsolutePath())){
							PluginInfo pluginInfo = RePlugin.install(cacheFile.getAbsolutePath());
							Log.e(TAG,"pluginInfo:"+pluginInfo);
							if (pluginInfo != null) {
								boolean preload = RePlugin.preload(pluginInfo);
								if (preload){
									Log.e(TAG,"preload plugin success");
									_init();
								} else {
									Log.e(TAG,"install plugin fail");
									initCallBack.onInitFailure("install plugin fail");
								}
							}
						}else{
							initCallBack.onInitFailure("install plugin fail");
						}
					} catch (IOException e) {
						e.printStackTrace();
					}

				}
			}).start();
		}
	}

	private void _init(){
		mContext.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Log.e(TAG,"_init");
				ClassLoader pluginClassLoader = RePlugin.fetchClassLoader("funcellplugin");
				try {
					Class<?> aClass = pluginClassLoader.loadClass("com.sdk.interactive.base.BaseUtil");
					Method getInstance_Method = aClass.getMethod("getInstance");
					Object Instance = getInstance_Method.invoke(null);
					Method registerBinder_Method = aClass.getMethod("registerBinder");
					IBinder binder = (IBinder)registerBinder_Method.invoke(Instance);
					IRegisterPluginBinder registerPluginBinder =  IRegisterPluginBinder.Stub.asInterface(binder);
					try {
						registerPluginBinder.Register(new RegisterPluginBinderCallBack(){

							@Override
							public void onFailure(String paramString) {

							}

							@Override
							public void onSuccess(String paramString) {
								Log.e(TAG,"Register onSuccess");
								IBinder binder = RePlugin.fetchBinder("funcellplugin", "sdkImpl");
								if(binder == null){
									Log.e(TAG,"fetchBinder is null");
									mInitCallBack.onInitFailure("fetchBinder is null");
								}else {
									IInteractiveService service = IInteractiveService.Stub.asInterface(binder);
									try {
										service.Init(mJson.toString(),mInitCallBack,mSessionCallBack,mPayCallBack);
									} catch (RemoteException e) {
										e.printStackTrace();
									}
								}
							}
						});
					} catch (RemoteException e) {
						e.printStackTrace();
					}

				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}catch (NoSuchMethodException e) {
					e.printStackTrace();
				}catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void login(Activity ctx) {
		IBinder binder = RePlugin.fetchBinder("funcellplugin", "sdkImpl");
		if(binder == null){
			return;
		}
		IInteractiveService service = IInteractiveService.Stub.asInterface(binder);
		try {
			service.Login(false);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	public void logout(Activity ctx){
		IBinder binder = RePlugin.fetchBinder("funcellplugin", "sdkImpl");
		if(binder == null){
			return;
		}
		IInteractiveService service = IInteractiveService.Stub.asInterface(binder);
		try {
			service.Logout();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public void pay(Activity ctx, PayInfo info){
        IBinder binder = RePlugin.fetchBinder("funcellplugin", "sdkImpl");
        if(binder == null){
            return;
        }
        IInteractiveService service = IInteractiveService.Stub.asInterface(binder);
        try {
            service.Pay(info);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

	public void exit(Activity ctx,SdkExitCallBack exitCallBack){
		IBinder binder = RePlugin.fetchBinder("funcellplugin", "sdkImpl");
		if(binder == null){
			return;
		}
		IInteractiveService service = IInteractiveService.Stub.asInterface(binder);
		try {
			service.Exit(exitCallBack);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	public void onResume(Activity ctx) {
		IBinder binder = RePlugin.fetchBinder("funcellplugin", "sdkImpl");
		if(binder == null){
			return;
		}
		IInteractiveService service = IInteractiveService.Stub.asInterface(binder);
		try {
			service.onResume();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	public void onPause(Activity ctx) {
		IBinder binder = RePlugin.fetchBinder("funcellplugin", "sdkImpl");
		if(binder == null){
			return;
		}
		IInteractiveService service = IInteractiveService.Stub.asInterface(binder);
		try {
			service.onPause();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public void showFloat(Activity ctx){
		IBinder binder = RePlugin.fetchBinder("funcellplugin", "sdkImpl");
		if(binder == null){
			return;
		}
		IInteractiveService service = IInteractiveService.Stub.asInterface(binder);
		try {
			service.ShowFloat();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public void hideFloat(Activity ctx){
		IBinder binder = RePlugin.fetchBinder("funcellplugin", "sdkImpl");
		if(binder == null){
			return;
		}
		IInteractiveService service = IInteractiveService.Stub.asInterface(binder);
		try {
			service.HideFloat();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public void onDestroy(Activity ctx) {
		Log.e(TAG,"onDestroy");
	}
	
	public void onActivityResult(Activity ctx,int requestCode, int resultCode, Intent data){
		Log.e(TAG,"onActivityResult requestCode:"+requestCode);
		IBinder binder = RePlugin.fetchBinder("funcellplugin", "sdkImpl");
		if(binder == null){
			return;
		}
		IInteractiveService service = IInteractiveService.Stub.asInterface(binder);
		try {
			service.onActivityResult(requestCode, resultCode, data);
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

	public void onRequestPermissionsResult(int requestCode,String[] permissions, int[] grantResults) {
		if(mSdkPermissionsManager != null){
			mSdkPermissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults, mContext);
		}
	}

	/**
	 *
	 * @param json
	 * @param callback
	 */
	private void callRequestPermissions(String json, final IPermissionsCallBack callback){
		try {
			JSONObject jsonObject = new JSONObject(json);
			String _rationale4ReqPer = jsonObject.getString("rationale4ReqPer");
			String _rationale4NeverAskAgain = jsonObject.getString("rationale4NeverAskAgain");
			int _requestCode = jsonObject.getInt("requestCode");
			String _requestPermission = jsonObject.getString("requestPermission");
			SdkPermissionsBuilder permissionsBuilder = new SdkPermissionsBuilder(mContext)
					.onFuncellPermissionsCallbacks(new SdkPermissionsCallbacks() {
						@Override
						public void onPermissionsGranted(int requestCode, List<String> perms) {
							/**
							 *回调信息给插件
							 */
							try {
								callback.onPermissionsGranted(requestCode,perms);
							} catch (RemoteException e) {
								e.printStackTrace();
							}
						}

						@Override
						public void onPermissionsDenied(int requestCode, List<String> perms) {
							/**
							 *回调信息给插件
							 */
							try {
								callback.onPermissionsDenied(requestCode,perms);
							} catch (RemoteException e) {
								e.printStackTrace();
							}
						}
					})
					.rationale4ReqPer(_rationale4ReqPer)
					.rationale4NeverAskAgain(_rationale4NeverAskAgain);
			permissionsBuilder.requestCode(_requestCode);
			mSdkPermissionsManager = permissionsBuilder.build();
			mSdkPermissionsManager.requestPermissions(_requestPermission);

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	Handler mH = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what){
				case 0:
					callRequestPermissions((String) msg.obj,mPermissionsCallBack);
					break;
			}
		}
	};

	/**
	 * 提供给插件获取权限管理Binder对象
	 * @return
	 */
	public IBinder getPermissionsManager(){
		return new PermissionsManagerImpl();
	}

	private class PermissionsManagerImpl extends IPermissionsManager.Stub{

		@Override
		public void requestPermissions(String json, IPermissionsCallBack callback) throws RemoteException {
			mPermissionsCallBack = callback;
			Message msg = mH.obtainMessage();
			msg.what = 0;
			msg.obj = json;
			mH.sendMessage(msg);
		}
	}

	//	private static int mCount = 1;
//	private int mMaxNum = 3;
//	Handler mH = new Handler(){
//		@Override
//		public void handleMessage(Message msg) {
//			super.handleMessage(msg);
//			if(mCount <= mMaxNum){
//				_init();
//			}
//		}
//	};

//		IBinder binder = RePlugin.fetchBinder("funcellplugin", "sdkImpl");
//		if(binder == null){
//			Log.e(TAG,"binder is null");
////			Message msg = new Message();
////			switch (mCount){
////				case 1:
////					mCount++;
////					mH.sendMessageDelayed(msg,1000);
////					break;
////				case 2:
////					mCount++;
////					mH.sendMessageDelayed(msg,1000);
////					break;
////				case 3:
////					mCount++;
////					mH.sendMessageDelayed(msg,1000);
////					break;
////				default:
////					mInitCallBack.onInitFailure("install plugin fail");
////					break;
////			}
//		}else{
//			IInteractiveService service = IInteractiveService.Stub.asInterface(binder);
//			try {
//				service.Init(mJson.toString(),mInitCallBack,mSessionCallBack,mPayCallBack);
//			} catch (RemoteException e) {
//				e.printStackTrace();
//			}
//		}
}
