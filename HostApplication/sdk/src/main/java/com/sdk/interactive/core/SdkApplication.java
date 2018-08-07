package com.sdk.interactive.core;

import com.qihoo360.replugin.RePluginApplication;
import com.qihoo360.replugin.RePluginConfig;
import com.sdk.interactive.util.SdkNotProguard;

/**
 * Created by hexin on 2017/8/10.
 */

@SdkNotProguard
public class SdkApplication extends RePluginApplication {
    @Override
    protected RePluginConfig createConfig() {
        RePluginConfig config = new RePluginConfig();
        config.setVerifySign(false);
        return config;
    }
}
