package com.haowan123.funcell.sdk.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.apache.http.conn.util.InetAddressUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.Config;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.haowan123.funcell.sdk.apiinterface.FunCellPlatformSdkApi;

public class HWUtils {

	private static final boolean isDebug = true;
	final public static int IMEI = 1;
	final public static int NET_TYPE = IMEI + 1;
	final public static int SDCARD_SUM = NET_TYPE + 1;
	final public static int DEFAUL_TSDCARD_FREESIZE = SDCARD_SUM + 1;
	final public static int CPU = DEFAUL_TSDCARD_FREESIZE + 1;
	final public static int MEM = CPU + 1;
	final public static int SCREEN_PIEXL = MEM + 1;
	final public static int SYSTEM_VERSION = SCREEN_PIEXL + 1;
	final public static int MOBILE_TYPE = SYSTEM_VERSION + 1;
	final public static int MOBILE_SERVICE_PROVIDER = MOBILE_TYPE + 1;
	final public static int SYSTEM_TYPE = MOBILE_SERVICE_PROVIDER + 1;

	public static final String DIRECTORY_NAME = "funcell123";
	public static final String FILE_NAME_UID = ".fun_uid_log";

	public static long lastClickTime;

	public static String beginTime;

	/**
	 * 获取imei
	 * 
	 * @param context
	 * @return
	 */
	public static String getImei(Context context) {
		String imei="";
		try {
			imei=((TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return imei;
	}

	/**
	 * 获取网络类型
	 * 
	 * @param context
	 * @return
	 */
	public static String getNetType(Context context) {
		String ret = "";
		ConnectivityManager mConnectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
		if (mNetworkInfo != null && mNetworkInfo.isAvailable()) {
			int type = mNetworkInfo.getType();
			if (ConnectivityManager.TYPE_WIFI == type) {
				ret = "wifi";
			} else if (ConnectivityManager.TYPE_MOBILE == type) {
				ret = "3g";
				int subtype = mNetworkInfo.getSubtype();
				switch (subtype) {
				case TelephonyManager.NETWORK_TYPE_GPRS:
				case TelephonyManager.NETWORK_TYPE_EDGE:
				case TelephonyManager.NETWORK_TYPE_CDMA:
				case TelephonyManager.NETWORK_TYPE_1xRTT:
				case TelephonyManager.NETWORK_TYPE_IDEN:
					ret = "2g";
					break;

				case TelephonyManager.NETWORK_TYPE_UMTS:
				case TelephonyManager.NETWORK_TYPE_EVDO_0:
				case TelephonyManager.NETWORK_TYPE_EVDO_A:
				case TelephonyManager.NETWORK_TYPE_HSDPA:
				case TelephonyManager.NETWORK_TYPE_HSUPA:
				case TelephonyManager.NETWORK_TYPE_HSPA:
				case TelephonyManager.NETWORK_TYPE_EVDO_B:
				case TelephonyManager.NETWORK_TYPE_EHRPD:
				case TelephonyManager.NETWORK_TYPE_HSPAP:
					ret = "3g";
					break;

				case TelephonyManager.NETWORK_TYPE_LTE:
					ret = "4g";
					break;

				default:
					ret = "未知";
					break;
				}

			} else {
				ret = "未知";
			}
		}
		return ret;
	}

	public static String getSdcardSum(Context context) {
		String ret = "";

		Method methodGetPaths;
		String[] paths = null;
		StorageManager storageManager = (StorageManager) context
				.getSystemService(Activity.STORAGE_SERVICE);

		try {
			methodGetPaths = storageManager.getClass().getMethod(
					"getVolumePaths");

			paths = (String[]) methodGetPaths.invoke(storageManager);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		int sum = 0;
		if (paths != null) {
			for (String stringTmp : paths) {
				sum++;
			}
		} else {
			boolean sdCardExist = Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED);
			if (sdCardExist) {
				sum = 1;
			}
		}

		ret = "" + sum;
		return ret;
	}

	public static String getCPU() {
		String ret = "";
		ret = android.os.Build.CPU_ABI;
		return ret;
	}

	public static String getMEM() {
		String ret = "";
		String str1 = "/proc/meminfo";
		String str2;
		Double memory = 0.0;

		try {

			FileReader r = new FileReader(str1);
			BufferedReader bufferedRead = new BufferedReader(r, 8192);
			str2 = bufferedRead.readLine();
			String str4 = str2.substring(str2.length() - 9, str2.length() - 3);
			memory = Double.parseDouble(str4) / 1000;

		} catch (Exception e) {
			Log.e("----", "getMEM error info:" + e.getMessage());
		}

		ret = "" + memory;
		return ret;
	}

	/**
	 * 获取分辨率
	 * 
	 * @param context
	 * @return
	 */
	public static String getScreenPiexl(Context context) {
		String ret = "";

		DisplayMetrics metric = new DisplayMetrics();
		if (metric != null) {
			((Activity) context).getWindowManager().getDefaultDisplay()
					.getMetrics(metric);
			ret = "" + metric.widthPixels + "X" + metric.heightPixels;
		}
		return ret;
	}

	/**
	 * 获取系统版本
	 * 
	 * @return
	 */
	public static String getSystemVersion() {
		String ret = "";
		ret = "" + android.os.Build.VERSION.RELEASE;
		return ret;
	}

	/**
	 * 获取机型
	 * 
	 * @return
	 */
	public static String getMobileType() {
		String ret = "";
		ret = android.os.Build.MODEL;
		return ret;
	}

	/**
	 * 获取运营商
	 * 
	 * @param context
	 * @return
	 */
	public static String getMobileServiceProvider(Context context) {
		String ret = "";
		String IMSI = "";
		String ProvidersName = null;
		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);

		// 返回唯一的用户ID;就是这张卡的编号神马的
		try {
			IMSI = telephonyManager.getSubscriberId();
			if (IMSI != null) {
				// IMSI号前面3位460是国家，紧接着后面2位00 02是中国移动，01是中国联通，03是中国电信。
				if (IMSI.startsWith("46000") || IMSI.startsWith("46002")) {
					ProvidersName = "中国移动";
				} else if (IMSI.startsWith("46001")) {
					ProvidersName = "中国联通";
				} else if (IMSI.startsWith("46003")) {
					ProvidersName = "中国电信";
				} else {
					ProvidersName = "其他";
				}
			} else {
				ProvidersName = "无";
			}
		} catch (Exception ex) {
			Log.e("error", ex.getMessage());
			ProvidersName = "无";
		}
		ret = ProvidersName;
		return ret;
	}

	public static String getSystemType() {
		String ret = "";
		ret = "android";
		return ret;
	}

	/****************************************************************************
	 * Progress UI
	 ****************************************************************************/
	static ProgressDialog sProgressDialog;

	public static void startProgress(final String msg, final Context context) {
		((Activity) context).runOnUiThread(new Runnable() {
			@Override
			public void run() {
				try {
					dimssProgress(context);
					sProgressDialog = ProgressDialog.show(context, "提示", msg,
							true);
					sProgressDialog.setCancelable(false);
				} catch (Exception ex) {
					Log.v("ddd", ex.getMessage());
				}
			}
		});
	}

	public static void dimssProgress(final Context context) {
		((Activity) context).runOnUiThread(new Runnable() {

			@Override
			public void run() {
				try {
					if (sProgressDialog != null && sProgressDialog.isShowing()) {
						sProgressDialog.dismiss();
					}
				} catch (Exception ex) {
					Log.v("ddd", ex.getMessage());
				}
			}
		});
	}

	public static void alert(final String mess, final Context context) {
		((Activity) context).runOnUiThread(new Runnable() {
			@Override
			public void run() {
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setTitle("提示");
				// builder.setIcon(context.getResources().getDrawable(R.drawable.icon));
				builder.setMessage(mess);
				builder.setPositiveButton("OK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								dialog.dismiss();
							}
						});
				builder.create().show();
			}
		});
	}

	// 外部浏览器打开一个网址
	public static void openUrl(Context context, String Url) {
		try {
			new URL(Url);
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return;
		}
		try {
			Intent it = new Intent(Intent.ACTION_VIEW, Uri.parse(Url));
			context.startActivity(it);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {

		}
	}

	/**
	 * 检测是否有网络
	 * 
	 * @param act
	 * @return
	 */
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();
		if (info != null && info.getState() == NetworkInfo.State.CONNECTED) {
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @param intervalTime
	 * @return
	 */
	public static boolean isFastDoubleClick(long intervalTime) {
		long time = System.currentTimeMillis();
		if (time - lastClickTime < intervalTime) {
			return true;
		}
		lastClickTime = time;
		return false;
	}

	/**
	 * 获取当前时间（以秒为单位的时间戳）
	 * 
	 * @return
	 */
	public static String getTimestamp() {
		long time = System.currentTimeMillis();
		String timestamp = String.valueOf(time);
		if (timestamp.length() >= 10) {
			timestamp = timestamp.substring(0, 10);
		}
		return timestamp;
	}

	/**
	 * 获取当前手机型号
	 * 
	 * @return
	 */
	public static String getPhoneModel() {
		return Build.MODEL;
	}

	/**
	 * 获取当前手机系统版本
	 * 
	 * @return
	 */
	public static String getPhoneVersion() {
		return "android_" + Build.BRAND + "_" + Build.VERSION.RELEASE;
	}

	public static String getLocalLangage() {

		return Locale.getDefault().toString();
	}

	/**
	 * 获取当前手机高度
	 * 
	 * @return
	 */
	public static String getPhoneHeight(Activity context) {

		DisplayMetrics dm = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(dm);
		return String.valueOf(dm.heightPixels);
	}

	/**
	 * 获取当前手机宽度
	 * 
	 * @return
	 */
	public static String getPhoneWidht(Activity context) {

		DisplayMetrics dm = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(dm);
		return String.valueOf(dm.widthPixels);
	}

	/**
	 * 获取当前Ip地址
	 * 
	 * @return
	 */
	public static String getCurrentIp(Activity context) {

		try {

			String ipv4;

			List nilist = Collections.list(NetworkInterface
					.getNetworkInterfaces());
			for (Object object : nilist) {
				NetworkInterface ni = (NetworkInterface) object;

				List ialist = Collections.list(ni.getInetAddresses());

				for (Object obj : ialist) {

					InetAddress address = (InetAddress) obj;

					if (!address.isLoopbackAddress()
							&& InetAddressUtils.isIPv4Address(ipv4 = address
									.getHostAddress())) {

						return ipv4;

					}

				}

			}

		} catch (SocketException ex) {

		}

		return null;
	}

	/**
	 * 获取mac地址
	 * 
	 * @param context
	 * @return String
	 */
	public static String getLocalMacAddress(Context context) {
		WifiManager wifi = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		return info.getMacAddress();
	}
	
	public static String getLocalMacAddress() {  
        String Mac=null;  
        try{  
              
            String path="sys/class/net/wlan0/address";  
            if((new File(path)).exists())  
            {  
                FileInputStream fis = new FileInputStream(path);  
                byte[] buffer = new byte[8192];  
                int byteCount = fis.read(buffer);  
                if(byteCount>0)  
                {  
                    Mac = new String(buffer, 0, byteCount, "utf-8");  
                }  
            }  
//            logError("HWSDKLOG", "Mac1 = "+Mac);
            if(Mac==null||Mac.length()==0)  
            {  
                path="sys/class/net/eth0/address";  
                FileInputStream fis_name = new FileInputStream(path);  
                byte[] buffer_name = new byte[8192];  
                int byteCount_name = fis_name.read(buffer_name);  
                if(byteCount_name>0)  
                {  
                    Mac = new String(buffer_name, 0, byteCount_name, "utf-8");  
                }  
            }  
//            logError("HWSDKLOG", "Mac2 = "+Mac);  
              
            if(Mac.length()==0||Mac==null){  
                return "";  
            }  
        }catch(Exception io){  
        	logError("HWSDKLOG", "mac exception : "+io.toString());  
        }  
          
//        logError("HWSDKLOG", "Mac3 = "+Mac);  
        return Mac.trim();  
    } 

	public static void logError(String tag, String msg) {
		if (isDebug) {
			Log.e(tag, msg);
		}
	}

	public static String getMetaDataByKey(Context context, String key) {
		ApplicationInfo appi;
		Bundle infobundle = null;
		try {
			appi = context.getPackageManager().getApplicationInfo(
					context.getPackageName(), PackageManager.GET_META_DATA);
			infobundle = appi.metaData;
		} catch (NameNotFoundException e) {
			HWUtils.logError("HWSDK", "渠道code未配置");
			e.printStackTrace();
		}

		return null == infobundle ? null : infobundle.get(key).toString();
	}

	public static String getChannelCode(Context context) {

		String packageName = context.getPackageName();

		String funcellChanel = readChannelCodeFromSDCard(packageName);

		if (null != funcellChanel && 0 < funcellChanel.trim().length()) {
			return funcellChanel;
		}

		// read manifest.xml中滴meda-data
		funcellChanel = getMetaDataByKey(context, "WWS_CHANNEL");
		if (null == funcellChanel || 0 == funcellChanel.trim().length()) {
			logError("FuncellSDK", "WWS_CHANNEL未配置");
		}

		writeChannelCodeToSDCard(packageName, funcellChanel);

		return funcellChanel;
	}

	/**
	 * 将px值转换为dip或dp值，保证尺寸大小不变
	 * 
	 * @param pxValue
	 * @param scale
	 *            （DisplayMetrics类中属性density）
	 * @return
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * 将dip或dp值转换为px值，保证尺寸大小不变
	 * 
	 * @param dipValue
	 * @param scale
	 *            （DisplayMetrics类中属性density）
	 * @return
	 */
	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	/**
	 * 将px值转换为sp值，保证文字大小不变
	 * 
	 * @param pxValue
	 * @param fontScale
	 *            （DisplayMetrics类中属性scaledDensity）
	 * @return
	 */
	public static int px2sp(Context context, float pxValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (pxValue / fontScale + 0.5f);
	}

	/**
	 * 将sp值转换为px值，保证文字大小不变
	 * 
	 * @param spValue
	 * @param fontScale
	 *            （DisplayMetrics类中属性scaledDensity）
	 * @return
	 */
	public static int sp2px(Context context, float spValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (spValue * fontScale + 0.5f);
	}

	public static String readChannelCodeFromSDCard(String fileName) {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			File sdCardDir = Environment.getExternalStorageDirectory();
			File file = new File(sdCardDir + File.separator + DIRECTORY_NAME,
					fileName);
			if (!file.exists()) {
				System.err
						.println("channel file is not exists................");
				return null;
			}
			FileInputStream inputStream = null;
			try {
				inputStream = new FileInputStream(file);
				byte[] b = new byte[inputStream.available()];
				inputStream.read(b);
				return new String(b);
			} catch (Exception e) {

			} finally {
				if (null != inputStream) {
					try {
						inputStream.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

		}

		return null;
	}

	public static boolean writeChannelCodeToSDCard(String fileName,
			String channelCode) {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {

			File sdCardDir = Environment.getExternalStorageDirectory();// 获取SDCard目录,2.2的时候为:/mnt/sdcard
																		// 2.1的时候为：/sdcard，所以使用静态方法得到路径会好一点。
			File dirFile = new File(sdCardDir + File.separator + DIRECTORY_NAME);
			File file = new File(sdCardDir + File.separator + DIRECTORY_NAME
					+ File.separator + fileName);
			if (dirFile.exists()) {
				if (file.exists()) {
					return true;
				}
			} else {
				dirFile.mkdir();

			}

			FileOutputStream outStream = null;
			try {
				outStream = new FileOutputStream(file);
				outStream.write(channelCode.getBytes());
				return true;
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				if (null != outStream) {
					try {
						outStream.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

		}

		return false;
	}

	/**
	 * 设置窗口模式
	 */
	public static void setWindowDisplayMode(Activity context, int height,
			float hRate, float wRate) {

		WindowManager m = context.getWindowManager();
		Display d = m.getDefaultDisplay(); // 为获取屏幕宽、高
		android.view.WindowManager.LayoutParams p = context.getWindow()
				.getAttributes(); // 获取对话框当前的参数值
		if (FunCellPlatformSdkApi.getInstance().isWindowMode()) {
			if (d.getHeight() < d.getWidth()) {
				if (HWUtils.dip2px(context, height) < d.getHeight()) {
					p.height = HWUtils.dip2px(context, height);
				} else {
					p.height = (int) (d.getHeight() * hRate);

				}
				p.width = (int) (d.getHeight() * wRate);
				// getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

			} else {
				if (HWUtils.dip2px(context, height) < d.getWidth()) {
					p.height = HWUtils.dip2px(context, height);
				} else {
					p.height = (int) (d.getWidth() * hRate);

				}
				p.width = (int) (d.getWidth() * wRate);

			}
			// p.height=HWUtils.dip2px(context,280);
			p.alpha = 0.98f; // 设置本身透明度
			p.dimAmount = 0.0f; // 设置黑暗度

		} else {
			p.height = d.getHeight();
			p.width = d.getWidth();
		}

		context.getWindow().setAttributes(p); // 设置生效
		// context.getWindow().setGravity(Gravity.CENTER); // 设置居中对齐
	}

	/**
	 * 获取应用版本
	 * 
	 * @param context
	 * @return
	 */
	public static String getAppVersionName(Context context) {
		try {
			PackageManager manager = context.getPackageManager();
			PackageInfo info = manager.getPackageInfo(context.getPackageName(),
					0);
			return info.versionName;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public static String getAndroidId(Context context) {
		String aid=Secure
				.getString(context.getContentResolver(), Secure.ANDROID_ID);
		return null==aid||0==aid.length()?"":aid;
	}

	/**
	 * 对字符串进行md5 32位小写加密
	 * 
	 * @param s
	 * @return String
	 */
	public final static String stringTo32LowerCaseMD5(String s) {

		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'a', 'b', 'c', 'd', 'e', 'f' };
		try {
			byte[] strTemp = s.getBytes();
			// 使用MD5创建MessageDigest对象
			MessageDigest mdTemp = MessageDigest.getInstance("MD5");
			mdTemp.update(strTemp);
			byte[] md = mdTemp.digest();
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte b = md[i];
				// System.out.println((int)b);
				// 将没个数(int)b进行双字节加密
				str[k++] = hexDigits[b >> 4 & 0xf];
				str[k++] = hexDigits[b & 0xf];
			}
			return new String(str);
		} catch (Exception e) {
			return null;
		}
	}

	public static String getDeviceUID(Context context) {
		final String androidId = getAndroidId(context);
		final String imei = getImei(context);

		String uid = null;

		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			File file = new File(Environment.getExternalStorageDirectory(),
					FILE_NAME_UID);
			if (file.exists()) {
				try {
					return readFile(file);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			HWUtils.logError("HWLOG", "aid = " + androidId + " , did = " + imei);
			UUID uuid = UUID.randomUUID();

			if (null == androidId || 0 == androidId.trim().length()
					|| "9774d56d682e549c".equals(androidId)) {
				if (null != imei && 0 < imei.trim().length()
						&& !"000000000000000".equals(imei)) {
					uuid = UUID.nameUUIDFromBytes(imei.getBytes());
				}
			} else {
				uuid = UUID.nameUUIDFromBytes(androidId.getBytes());
			}

			uid = uuid.toString().replaceAll("-", "");

			try {
				writeFile(context, file, uid);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		HWUtils.logError("HWLOG", "uid = " + uid);
		return uid;
	}

	/**
	 * 读取指定文件中的类容
	 * 
	 * @param targetFile
	 *            目标file。
	 * @return 唯一标识符。
	 * @throws IOException
	 *             IO异常。
	 */
	private static String readFile(File targetFile) throws IOException {
		RandomAccessFile accessFile = new RandomAccessFile(targetFile, "r");
		byte[] bs = new byte[(int) accessFile.length()];
		accessFile.readFully(bs);
		accessFile.close();
		return new String(bs);
	}

	/**
	 * 将内容写入到指定文件中
	 * 
	 * @param context
	 *            Context对象。
	 * @param targetFile
	 *            保存内容的File对象。
	 * @param source
	 *            保存的内容。
	 * @throws IOException
	 *             IO异常。
	 */
	private static void writeFile(Context context, File targetFile,
			String source) throws IOException {
		FileOutputStream out = new FileOutputStream(targetFile);
		out.write(source.getBytes());
		out.close();
	}

}
