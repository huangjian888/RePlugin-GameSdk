package com.haowan123.funcell.sdk.apiinterface;

public interface RechargeCallBack {
	

	public void rechargeSuccess(String orderId);
	
	public void rechargeFail(int errorCode,String errorMsg);
}
