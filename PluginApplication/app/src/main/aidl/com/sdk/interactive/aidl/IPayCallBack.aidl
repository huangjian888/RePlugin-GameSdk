package com.sdk.interactive.aidl;

interface IPayCallBack{
	void onSuccess(String cpOrder,String sdkOrder,String extrasParams);
	void onFail(String paramString);
	void onCancel(String paramString);
	void onClosePayPage(String cpOrder,String sdkOrder,String extrasParams);
}