package com.haowan123.funcell.sdk.permission;

public class PermissionConfig {

	/**
	 * Permission
	 */
	public class Permission {
		public static final String ANDROID_PERMISSION_READ_PHONE_STATE 		 = "android.permission.READ_PHONE_STATE";
		public static final String ANDROID_PERMISSION_GET_ACCOUNTS 			 = "android.permission.GET_ACCOUNTS";
		public static final String ANDROID_PERMISSION_READ_EXTERNAL_STORAGE  = "android.permission.READ_EXTERNAL_STORAGE";
		public static final String ANDROID_PERMISSION_WRITE_EXTERNAL_STORAGE = "android.permission.WRITE_EXTERNAL_STORAGE";
	}

	/**
	 * RequestCode
	 */
	public class PermissionReqCode {
		public static final int READ_PHONE_STATE 		= 1 << 0;
		public static final int GET_ACCOUNTS 			= 1 << 1;
		public static final int READ_EXTERNAL_STORAGE 	= 1 << 2;
		public static final int WRITE_EXTERNAL_STORAGE 	= 1 << 3;
	}

	/**
	 * Permission Request Message
	 */
	public class PermissionReqMsg {
		public static final String PERMISSION_READ_PHONE_STATE 			= "读取设备状态及识别码权限";
		public static final String PERMISSION_GET_ACCOUNTS 	   			= "读取设备上的账户信息权限";
		public static final String PERMISSION_READ_EXTERNAL_STORAGE		= "读取设备外部存储信息权限";
		public static final String PERMISSION_WRITE_EXTERNAL_STORAGE	= "修改设备外部存储信息权限";
	}
	
	/**
	 * Permission Response Message
	 */
	public class PermissionResMsg{
		public static final String PERMISSION_AVAILABLE_READ_PHONE_STATE 	   = "已允许读取设备状态及识别码";
		public static final String PERMISSION_AVAILABLE_GET_ACCOUNTS 	 	   = "已允许读取设备上的账户信息";
		public static final String PERMISSION_AVAILABLE_WRITE_EXTERNAL_STORAGE = "已允许修改设备外部存储信息";
		public static final String PERMISSION_AVAILABLE_READ_EXTERNAL_STORAGE  = "已允许读取设备外部存储信息";
		public static final String PERMISSIONS_NOT_GRANTED 				 	   = "您不允许此权限";
	}

}
