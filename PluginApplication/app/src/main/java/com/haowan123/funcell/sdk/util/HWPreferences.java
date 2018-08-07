package com.haowan123.funcell.sdk.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class HWPreferences {
	
	public static String getData(Context context,String key) {
		String value = "";
		SharedPreferences sp = context.getSharedPreferences("hw_platform_store", 0);
		value = sp.getString(key, "");
		return value;
	}
	
	public static void addData(Context context, String key, String value) {
		SharedPreferences sp = context.getSharedPreferences("hw_platform_store", 0);
		Editor editor = sp.edit();
		editor.putString(key, value);
		editor.commit();
	}

}
