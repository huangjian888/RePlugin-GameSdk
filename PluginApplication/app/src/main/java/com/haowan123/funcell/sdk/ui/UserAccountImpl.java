package com.haowan123.funcell.sdk.ui;

import com.haowan123.funcell.sdk.apiinterface.UserAccount;

public class UserAccountImpl implements UserAccount {
	private String userName;
	private String userId;
	private String unixTime;
	private String sessionId;
	private String token;
	private String customString;//自定义字符串
	

	public UserAccountImpl() {
		this(null,null,null,null,null);
	}

	public UserAccountImpl(String userName, String userId, String unixTime,
			String sessionId, String token) {
		this.userName = userName;
		this.userId = userId;
		this.unixTime = unixTime;
		this.sessionId = sessionId;
		this.token = token;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUnixTime() {
		return unixTime;
	}

	public void setUnixTime(String unixTime) {
		this.unixTime = unixTime;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
	
	
}
