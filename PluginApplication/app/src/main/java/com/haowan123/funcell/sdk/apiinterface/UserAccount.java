package com.haowan123.funcell.sdk.apiinterface;

public interface UserAccount {

	/**
	 * 获取用户名
	 * @return userName
	 */
	public String getUserName();
	
	/**
	 * 获取用户ID
	 * @return userId
	 */
	public String getUserId();

	/**
	 * 获取登录时间
	 * @return time
	 */
	public String getUnixTime();

	/**
	 * 获取session
	 * @return sessionId
	 */
	public String getSessionId();
	
	/**
	 * 获取token
	 * @return token
	 */
	public String getToken();

}
