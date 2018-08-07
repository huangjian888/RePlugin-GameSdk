package com.sdk.interactive.base;

import android.Manifest;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;

import com.qihoo360.replugin.RePlugin;
import com.sdk.interactive.aidl.IPermissionsManager;
import com.sdk.interactive.aidl.IRegisterPluginBinder;
import com.sdk.interactive.aidl.IRegisterPluginBinderCallBack;
import com.sdk.interactive.aidl.R;
import com.sdk.interactive.callback.PermissionsCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by hexin on 2017/8/8.
 */

public class BaseUtil {
    private String TAG = "BaseUtil";
    private static BaseUtil mInstance;
    private IRegisterPluginBinderCallBack mRegisterCallBack;
    public static BaseUtil getInstance() {
        if (mInstance == null) {
            synchronized (BaseUtil.class) {
                if (mInstance == null) {
                    mInstance = new BaseUtil();
                }
            }
        }
        return mInstance;
    }

    private void callRegisterBinder(){
        RePlugin.registerPluginBinder("sdkImpl",new SdkImpl());
        if(mRegisterCallBack == null){
            Log.e(TAG,"mRegisterCallBack is null");
            return;
        }
        try {
            mRegisterCallBack.onSuccess("success");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    Handler mH = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case Constant.H_RegisterBinder:
                    callRegisterBinder();
                    break;
            }
        }
    };

    /**
     * 提供给宿主sdk调用,用于注册插件Binder
     * @return
     */
    public IBinder registerBinder(){
        return new RegisterPluginBinderImpl();
    }

    private class RegisterPluginBinderImpl extends IRegisterPluginBinder.Stub{

        @Override
        public void Register(IRegisterPluginBinderCallBack callback) throws RemoteException {
            Log.e(TAG,"Register plugin");
            mRegisterCallBack = callback;
            Message msg = mH.obtainMessage();
            msg.what = Constant.H_RegisterBinder;
            mH.sendMessage(msg);
        }
    }

    void requestPermissionsDemo(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("rationale4ReqPer", RePlugin.getPluginContext().getResources().getString(R.string.fun_rationale4ReqPer)); //询问是否前往请求权限的提示语
            jsonObject.put("rationale4NeverAskAgain",RePlugin.getPluginContext().getResources().getString(R.string.fun_rationale4NeverAskAgain)); //询问在之前选择了"不再询问"之后是否选择去设置里修改权限的提示语
            jsonObject.put("requestCode",123); //请求码
            jsonObject.put("requestPermission", Manifest.permission.READ_EXTERNAL_STORAGE);//请求权限
        } catch (JSONException e) {
            e.printStackTrace();
        }
        BaseUtil.getInstance().requestPermissions(jsonObject.toString(), new PermissionsCallBack() {
            @Override
            public void onPermissionsGranted(int requestCode, List<String> perms) {
                Log.e(TAG,"-------onPermissionsGranted------");
            }

            @Override
            public void onPermissionsDenied(int requestCode, List<String> perms) {
                Log.e(TAG,"-------onPermissionsDenied------");
            }
        });
    }

    /**
     * 动态请求权限
     * @param json
     * @param callBack
     */
    public void requestPermissions(String json, PermissionsCallBack callBack){
        ClassLoader classLoader = RePlugin.getHostClassLoader();
        try {
            Class<?> aClass = classLoader.loadClass("com.sdk.interactive.core.BaseController");
            Method getInstance_Method = aClass.getMethod("getInstance");
            Object Instance = getInstance_Method.invoke(null);
            Method getPermissionsManager_Method = aClass.getMethod("getPermissionsManager");
            IBinder binder = (IBinder)getPermissionsManager_Method.invoke(Instance);
            IPermissionsManager permissionsManager = IPermissionsManager.Stub.asInterface(binder);
            try {
                permissionsManager.requestPermissions(json,callBack);
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

}
