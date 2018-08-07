package com.example.hostdemo;

import android.content.res.AssetManager;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.qihoo360.replugin.RePlugin;
import com.qihoo360.replugin.model.PluginInfo;
import com.sdk.interactive.aidl.PayInfo;
import com.sdk.interactive.aidl.Session;
import com.sdk.interactive.bean.SdkConfig;
import com.sdk.interactive.core.BaseController;
import com.sdk.interactive.core.SdkController;
import com.sdk.interactive.core.callback.SdkExitCallBack;
import com.sdk.interactive.core.callback.SdkInitCallBack;
import com.sdk.interactive.core.callback.SdkPayCallBack;
import com.sdk.interactive.core.callback.SdkSessionCallBack;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static android.R.attr.name;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private String TAG = "MainActivity";
    private Button install,init,login,logout,pay,exit,showfloat,hidefloat,uninstall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        install = (Button) findViewById(R.id.button);
        init = (Button) findViewById(R.id.button6);
        login = (Button) findViewById(R.id.button2);
        logout = (Button) findViewById(R.id.button3);
        pay = (Button) findViewById(R.id.button4);
        exit = (Button) findViewById(R.id.button5);
        showfloat = (Button) findViewById(R.id.button10);
        hidefloat = (Button) findViewById(R.id.button9);
        uninstall = (Button) findViewById(R.id.button11);

        install.setOnClickListener(this);
        init.setOnClickListener(this);
        login.setOnClickListener(this);
        logout.setOnClickListener(this);
        pay.setOnClickListener(this);
        exit.setOnClickListener(this);
        showfloat.setOnClickListener(this);
        hidefloat.setOnClickListener(this);
        uninstall.setOnClickListener(this);

        Log.e(TAG,"MainActivity.this:"+MainActivity.this);

//        FuncellImpl.getInstance().pay(this);
    }
    @Override
    public void onResume() {
        super.onResume();
        SdkController.getInstance().onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        SdkController.getInstance().onPause(this);
    }


    @Override
    public void onClick(View v) {
        if (install == v) {
            install();
        }else if(init == v){
            init();
        }else if(login == v){
            login();
        }else if(logout == v){
            logout();
        }else if(pay == v){
            pay();
        }else if(exit == v){
            exit();
        }else if(showfloat == v){
            showFloat();
        }else if(hidefloat == v){
            hideFloat();
        }else if(uninstall == v){
            uninstall();
        }
    }

    private void uninstall(){
        RePlugin.uninstall("funcellplugin");
    }

    private void install(){
        AssetManager assets = this.getAssets();
//        String path = "file:///android_asset/app-release.apk";
        String dest = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator+ "app-release.apk";
        PluginInfo pluginInfo = RePlugin.install(dest);
        Log.e(TAG,"pluginInfo:"+pluginInfo);
        if (pluginInfo != null) {
            Toast.makeText(MainActivity.this, "插件安装成功",Toast.LENGTH_SHORT).show();
            boolean preload = RePlugin.preload(pluginInfo);
            if (preload){
                Toast.makeText(MainActivity.this, "预加载完成", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "插件安装失败",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void init(){
//        RePlugin.startActivity(this, RePlugin.createIntent("funcellplugin", "com.haowan123.funcell.sdk.ui.FunLoginActivity"));
        SdkConfig config = new SdkConfig();
        config.putString("appid","10000");
        config.putString("appkey","f35y546ku5gd2aed562t5");
        SdkController.getInstance().init(this, config, new SdkInitCallBack() {

            @Override
            public void onInitFailure(String msg) {
                Log.e(TAG,"onInitFailure");
                Toast.makeText(MainActivity.this,"onInitFailure",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onInitSuccess(String msg) {
                Log.e(TAG,"onInitSuccess");
                Toast.makeText(MainActivity.this,"onInitSuccess",Toast.LENGTH_SHORT).show();
            }
        }, new SdkSessionCallBack() {
            @Override
            public void onLoginSuccess(Session session) {
                Log.e(TAG,"onLoginSuccess");
                Toast.makeText(MainActivity.this,"onLoginSuccess",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLoginCancel() {

            }

            @Override
            public void onLoginFailed(String msg) {
                Log.e(TAG,"onLoginFailed");
                Toast.makeText(MainActivity.this,"onLoginFailed",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLogout() {
                Toast.makeText(MainActivity.this,"onLogout",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSwitchAccount(Session session) {

            }
        }, new SdkPayCallBack() {
            @Override
            public void onSuccess(String cpOrder, String sdkOrder, String extrasParams) {
                Log.e(TAG,"pay onSuccess");
                Toast.makeText(MainActivity.this,"pay onSuccess",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFail(String msg) {
                Log.e(TAG,"pay onFail");
                Toast.makeText(MainActivity.this,"pay onFail",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel(String msg) {
                Log.e(TAG,"pay onCancel");
                Toast.makeText(MainActivity.this,"pay onCancel",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onClosePayPage(String cpOrder, String sdkOrder, String extrasParams) {
                Toast.makeText(MainActivity.this,"pay onClosePayPage",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void login(){
        SdkController.getInstance().login(this);
    }

    private void logout(){
        SdkController.getInstance().logout(this);
    }

    private void pay(){
        PayInfo info = new PayInfo();
        info.setAmount(60);
        info.setCpOrderId("testorderid");
        info.setPrice(6);
        info.setProductName("test");
        info.setProductId("100002");
        info.setExtData("exdata");
        SdkController.getInstance().pay(this,info);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions, int[] grantResults) {
        SdkController.getInstance().onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void showFloat(){
        SdkController.getInstance().showFloat(this);
    }
    public void hideFloat(){
        SdkController.getInstance().hideFloat(this);
    }

    private void exit(){
        SdkController.getInstance().exit(this, new SdkExitCallBack() {
            @Override
            public void onConfirm() {
                Toast.makeText(MainActivity.this,"exit onConfirm",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {

            }
        });
    }


}
