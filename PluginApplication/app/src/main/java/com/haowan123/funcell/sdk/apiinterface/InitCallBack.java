package com.haowan123.funcell.sdk.apiinterface;

public interface InitCallBack {
	
	public void initSuccess();

	public void initFail(String errorCode,String errorMsg);
}
