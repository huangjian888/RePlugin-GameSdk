package com.sdk.interactive.bean;

import com.sdk.interactive.util.SdkNotProguard;

import java.io.Serializable;
import java.util.HashMap;

@SdkNotProguard
public class SdkConfig extends HashMap<Object, Object> implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public Object get(Object paramObject) {
		if (containsKey(paramObject)) {
			return super.get(paramObject);
		}
		return "";
	}

	public String getString(Object paramObject) {
		if (containsKey(paramObject)) {
			return super.get(paramObject).toString();
		}
		return "";
	}

	public Object put(String paramString, Object paramObject) {
		return super.put(paramString, paramObject);
	}
	
	public Object put(Object paramString,Object paramObject){
		return super.put(paramString, paramObject);
	}
	
	public void putString(String paramString1, String paramString2) {
		super.put(paramString1, paramString2);
	}

	public void putInt(String paramString, int paramInt) {
		super.put(paramString, Integer.valueOf(paramInt));
	}
	
}
