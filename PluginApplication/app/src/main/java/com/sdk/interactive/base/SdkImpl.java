package com.sdk.interactive.base;


import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.haowan123.funcell.sdk.apiinterface.FunCellPlatformSdkApi;
import com.haowan123.funcell.sdk.apiinterface.FunPayInfo;
import com.haowan123.funcell.sdk.apiinterface.InitCallBack;
import com.haowan123.funcell.sdk.apiinterface.LoginCallBack;
import com.haowan123.funcell.sdk.apiinterface.LogoutCallBack;
import com.haowan123.funcell.sdk.apiinterface.RechargeCallBack;
import com.qihoo360.replugin.RePlugin;
import com.raink.korea.platform.android.widget.FloatView;
import com.sdk.interactive.aidl.IExitCallBack;
import com.sdk.interactive.aidl.IInitCallBack;
import com.sdk.interactive.aidl.IInteractiveService;
import com.sdk.interactive.aidl.IPayCallBack;
import com.sdk.interactive.aidl.ISessionCallBack;
import com.sdk.interactive.aidl.PayInfo;
import com.sdk.interactive.aidl.R;
import com.sdk.interactive.aidl.Session;
import com.sdk.interactive.callback.PermissionsCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.util.List;

public class SdkImpl extends IInteractiveService.Stub {
    private String TAG = "SdkImpl";
    private IInitCallBack mIInitCallBack;
    private ISessionCallBack mISessionCallBack;
    private IPayCallBack mIPayCallBack;
    private FloatView mFloatView;
    private boolean mIsInitialize = false;
    private boolean mLoginFlag = false;

    private void callInit(String json){
        Activity _ctx = getHostActivity();
        if (_ctx == null){
            return;
        }
        try {
            JSONObject jsonObject = new JSONObject(json);
            String appid = jsonObject.getString("appid");
            String appkey = jsonObject.getString("appkey");
            FunCellPlatformSdkApi.getInstance().setWindowMode(true);
            FunCellPlatformSdkApi.getInstance().init(appid, appkey, new InitCallBack() {
                @Override
                public void initSuccess() {
                    Log.e(TAG,"sdkimpl initSuccess");
                    try {
                        mIInitCallBack.onInitSuccess("init success");
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void initFail(String errorCode, String errorMsg) {
                    Log.e(TAG,"sdkimpl initFail");
                    try {
                        mIInitCallBack.onInitFailure("errorMsg:"+errorMsg);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }, new LogoutCallBack() {
                @Override
                public void logout() {
                    try {
                        mLoginFlag = false;
                        mISessionCallBack.onLogout();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            });
            mFloatView = new FloatView(_ctx);
            mIsInitialize = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void callLogin(){
        Activity _ctx = getHostActivity();
        if (_ctx == null){
            return;
        }
        FunCellPlatformSdkApi.getInstance().login(_ctx, new LoginCallBack() {
            @Override
            public void loginSuccess(String token, String fid) {
                Session session = new Session();
                session.setmChannelUserId(fid);
                session.setmChannelToken(token);
                try {
                    mLoginFlag = true;
                    mISessionCallBack.onLoginSuccess(session);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void loginFail(int errorCode, String errorMsg) {
                try {
                    mISessionCallBack.onLoginFailed("errorMsg:"+errorMsg);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void callLogout(){
        FunCellPlatformSdkApi.getInstance().logout();
    }

    private void callShowFloat(){
        if(this.mFloatView != null && mLoginFlag){
            mFloatView.show();
        }
    }
    private void callHideFloat(){
        if(this.mFloatView != null){
            mFloatView.hide();
        }
    }

    private void callOnResume(){
        if(!mIsInitialize){
            return;
        }
        callShowFloat();
    }

    private void callOnPause(){
        if (!mIsInitialize){
            return;
        }
        callHideFloat();
    }

    private void callPay(PayInfo info){
        Activity _ctx = getHostActivity();
        if (_ctx == null){
            return;
        }
        FunPayInfo funPayInfo = new FunPayInfo();
        funPayInfo.setAmount(info.getAmount());
        funPayInfo.setCpOrderId(info.getCpOrderId());
        funPayInfo.setExtData(info.getExtData());
        funPayInfo.setPrice(info.getPrice());
        funPayInfo.setProductId(info.getProductId());
        funPayInfo.setProductName(info.getProductName());
        FunCellPlatformSdkApi.getInstance().recharge(_ctx, funPayInfo, new RechargeCallBack() {
            @Override
            public void rechargeSuccess(String orderId) {
                try {
                    mIPayCallBack.onSuccess("",orderId,"");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void rechargeFail(int errorCode, String errorMsg) {
                try {
                    mIPayCallBack.onFail("errorCode:"+errorCode+" "+"errorMsg:"+errorMsg);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private Activity getHostActivity(){
        Activity _ret = null;
        ClassLoader classLoader = RePlugin.getHostClassLoader();
        try {
            Class<?> aClass = classLoader.loadClass("com.sdk.interactive.core.SdkController");
            Method getInstance_Method = aClass.getMethod("getInstance");
            Object Instance = getInstance_Method.invoke(null);
            Method getmCtx_Method = aClass.getMethod("getmCtx");
            _ret = (Activity)getmCtx_Method.invoke(Instance);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }catch (NoSuchMethodException e) {
            e.printStackTrace();
        }catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return _ret;
    }

    Handler mH = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case Constant.H_Init:
                    callInit((String)msg.obj);
                    break;
                case Constant.H_Login:
                    callLogin();
                    break;
                case Constant.H_Logout:
                    callLogout();
                    break;
                case Constant.H_ShowFloat:
                    callShowFloat();
                    break;
                case Constant.H_HideFloat:
                    callHideFloat();
                    break;
                case Constant.H_OnResume:
                    callOnResume();
                    break;
                case Constant.H_OnPause:
                    callOnPause();
                    break;
                case Constant.H_Pay:
                    PayInfo info = (PayInfo) msg.obj;
                    callPay(info);
                    break;
            }
        }
    };

    @Override
    public void Init(String hostInfo, IInitCallBack initCallBack, ISessionCallBack sessionCallBack, IPayCallBack payCallBack) throws RemoteException {
        Log.e(TAG,"sdkimpl init");
        Activity _ctx = getHostActivity();
        if(_ctx == null){
            return;
        }
        mIInitCallBack = initCallBack;
        mISessionCallBack = sessionCallBack;
        mIPayCallBack = payCallBack;

        Message msg = mH.obtainMessage();
        msg.what = Constant.H_Init;
        msg.obj = hostInfo;
        mH.sendMessage(msg);

    }

    @Override
    public void Login(boolean hasAlertPermission) throws RemoteException {
        Log.e(TAG,"sdkimpl Login");
        Activity _ctx = getHostActivity();
        if(_ctx == null){
            return;
        }

        Message msg = mH.obtainMessage();
        msg.what = Constant.H_Login;
        mH.sendMessage(msg);
    }

    @Override
    public void Logout() throws RemoteException {
        Log.e(TAG,"sdkimpl Logout");
        Message msg = mH.obtainMessage();
        msg.what = Constant.H_Logout;
        mH.sendMessage(msg);
    }

    @Override
    public void Pay(PayInfo info) throws RemoteException {
        Message msg = mH.obtainMessage();
        msg.what = Constant.H_Pay;
        msg.obj = info;
        mH.sendMessage(msg);
    }

    @Override
    public void setDatas() throws RemoteException {

    }

    @Override
    public int Exit(IExitCallBack exitCallBack) throws RemoteException {
        exitCallBack.onConfirm();
        return 0;
    }

    @Override
    public void ShowFloat() throws RemoteException {
        Log.e(TAG,"sdkimpl ShowFloat");
        Message msg = mH.obtainMessage();
        msg.what = Constant.H_ShowFloat;
        mH.sendMessage(msg);
    }

    @Override
    public void HideFloat() throws RemoteException {
        Log.e(TAG,"sdkimpl HideFloat");
        Message msg = mH.obtainMessage();
        msg.what = Constant.H_HideFloat;
        mH.sendMessage(msg);
    }

    @Override
    public void onCreate() throws RemoteException {

    }

    @Override
    public void onResume() throws RemoteException {
        Log.e(TAG,"sdkimpl onResume");
        Message msg = mH.obtainMessage();
        msg.what = Constant.H_OnResume;
        mH.sendMessage(msg);
    }

    @Override
    public void onPause() throws RemoteException {
        Log.e(TAG,"sdkimpl onPause");
        Message msg = mH.obtainMessage();
        msg.what = Constant.H_OnPause;
        mH.sendMessage(msg);
    }

    @Override
    public void onDestroy() throws RemoteException {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) throws RemoteException {
        Log.e(TAG,"sdkimpl onActivityResult");
    }

    @Override
    public void callFunction(String FunctionName, String parameter) throws RemoteException {

    }
}
