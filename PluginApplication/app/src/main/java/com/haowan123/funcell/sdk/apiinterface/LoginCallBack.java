package com.haowan123.funcell.sdk.apiinterface;

public interface LoginCallBack {
	
	public void loginSuccess(String token,String fid);

	public void loginFail(int errorCode,String errorMsg);
}
