package com.sdk.interactive.base;

public class Constant{
	/**
	 * Handler
	 */
	public static final int H_Init = 0;
	public static final int H_OnResume = 1;
	public static final int H_OnPause = 2;
	public static final int H_Login = 3;
	public static final int H_Logout = 4;
	public static final int H_Pay = 5;
	public static final int H_Exit = 6;
	public static final int H_SetData = 7;
	public static final int H_Callfunction = 8;
	public static final int H_TimerDestory = 9;
	public static final int H_ShowFloat = 10;
	public static final int H_HideFloat = 11;
	public static final int H_RegisterBinder = 12;

	/**
	 * Business
	 */
	public static final String B_Business = "business";
	public static final String B_Business_Action_Login = "business_action_login";
	public static final String B_Business_Action_Pay = "business_action_pay";

	/**
	 * Pay
	 */
	public static final String P_Pay_Info ="pay_json";

	/**
	 * BroadcastReceiver
	 */
	public static final String B_LoginActivity_Action_finish = "login.activity.action.finish";

}
