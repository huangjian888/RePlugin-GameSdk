package com.example.funcellplugin;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.qihoo360.replugin.RePlugin;
import com.sdk.interactive.aidl.R;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class MainActivity extends AppCompatActivity {
    private Activity mHostActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        getHostActivity();
    }

    private void getHostActivity(){
        ClassLoader classLoader = RePlugin.getHostClassLoader();
        try {
            Class<?> aClass = classLoader.loadClass("com.example.hostdemo.FuncellImpl");
            Method getInstance_Method = aClass.getMethod("getInstance");
            Object Instance = getInstance_Method.invoke(null);
            Method getmCtx_Method = aClass.getMethod("getmCtx");
            mHostActivity = (Activity)getmCtx_Method.invoke(Instance);
            Toast.makeText(mHostActivity,"宿主上下文",Toast.LENGTH_SHORT).show();
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
