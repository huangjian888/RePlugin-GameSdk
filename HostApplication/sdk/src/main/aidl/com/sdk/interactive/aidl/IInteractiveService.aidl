package com.sdk.interactive.aidl;
import com.sdk.interactive.aidl.IInitCallBack;
import com.sdk.interactive.aidl.ISessionCallBack;
import com.sdk.interactive.aidl.IPayCallBack;
import com.sdk.interactive.aidl.PayInfo;
import com.sdk.interactive.aidl.IExitCallBack;

interface IInteractiveService{
	/*** Business logic function */
	void Init(String hostInfo,IInitCallBack initCallBack,ISessionCallBack sessionCallBack,IPayCallBack payCallBack);
	void Login(boolean hasAlertPermission);
	void Logout();
	void Pay(in PayInfo info);
	void setDatas();
	int Exit(IExitCallBack exitCallBack);
	void ShowFloat();
	void HideFloat();
	
	/*** Activity Lifecycle */
	void onCreate();
	void onResume();
	void onPause();
	void onDestroy();
    void onActivityResult(int requestCode, int resultCode, in Intent data);
	
	/*** Extension Method */
	void callFunction(String FunctionName,String parameter);
	
}