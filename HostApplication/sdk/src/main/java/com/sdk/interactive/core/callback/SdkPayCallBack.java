package com.sdk.interactive.core.callback;
import com.sdk.interactive.aidl.IPayCallBack;
import com.sdk.interactive.util.SdkNotProguard;

@SdkNotProguard
public abstract class SdkPayCallBack extends IPayCallBack.Stub{
	public abstract void onSuccess(String cpOrder,String sdkOrder,String extrasParams);
	public abstract void onFail(String msg);
	public abstract void onCancel(String msg);
	public abstract void onClosePayPage(String cpOrder,String sdkOrder,String extrasParams);
}
