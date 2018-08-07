package com.sdk.interactive.base;

/**
 * Created by hexin on 2017/6/22.
 */

import android.app.Activity;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.Window;
import android.view.WindowManager;

import com.haowan123.funcell.sdk.apiinterface.FunCellPlatformSdkApi;
import com.haowan123.funcell.sdk.apiinterface.FunPayInfo;
import com.haowan123.funcell.sdk.apiinterface.LoginCallBack;
import com.haowan123.funcell.sdk.apiinterface.RechargeCallBack;
import com.sdk.interactive.aidl.IPayCallBack;
import com.sdk.interactive.aidl.ISessionCallBack;
import com.sdk.interactive.aidl.PayInfo;
import com.sdk.interactive.aidl.Session;


public class BusinessActivity extends Activity {
    private String TAG = "BusinessActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        initBundle(bundle);
    }

    private void initBundle(Bundle bundle){
        if (bundle != null){
            switch (bundle.getString(Constant.B_Business)){
                case Constant.B_Business_Action_Login:
                    callLogin();
                    break;
                case Constant.B_Business_Action_Pay:
                    callPay(bundle);
                    break;
            }
        }
    }

    private void callLogin(){
        FunCellPlatformSdkApi.getInstance().login(this,new LoginCallBack(){

            @Override
            public void loginSuccess(String token, String fid) {
                BaseApplication.getInstance().setmLoginFlag(true);
                ISessionCallBack sessionCallBack = BaseApplication.getInstance().getmISessionCallBack();
                Session session = new Session();
                session.setmChannelToken(token);
                session.setmChannelUserId(fid);
                try {
                    sessionCallBack.onLoginSuccess(session);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }finally {
                    finish();
                }
            }

            @Override
            public void loginFail(int errorCode, String errorMsg) {
                ISessionCallBack sessionCallBack = BaseApplication.getInstance().getmISessionCallBack();
                try {
                    sessionCallBack.onLoginFailed("errorCode:"+errorCode);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }finally {
                    finish();
                }
            }
        });
    }

    private void callPay(Bundle bundle){
        PayInfo payInfo = bundle.getParcelable(Constant.P_Pay_Info);
        FunPayInfo info = new FunPayInfo();
        info.setAmount(payInfo.getAmount());
        info.setCpOrderId(payInfo.getCpOrderId());
        info.setExtData(payInfo.getExtData());
        info.setPrice(payInfo.getPrice());
        info.setProductId(payInfo.getProductId());
        info.setProductName(payInfo.getProductName());
        FunCellPlatformSdkApi.getInstance().recharge(this,info,new RechargeCallBack(){
            @Override
            public void rechargeSuccess(String orderId) {
                IPayCallBack payCallBack = BaseApplication.getInstance().getmIPayCallBack();
                try {
                    payCallBack.onSuccess(orderId,"","");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }finally {
                    finish();
                }
            }

            @Override
            public void rechargeFail(int errorCode, String errorMsg) {
                IPayCallBack payCallBack = BaseApplication.getInstance().getmIPayCallBack();
                try {
                    payCallBack.onFail("errorCode:"+errorCode+" "+"errorMsg:"+errorMsg);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }finally {
                    finish();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
