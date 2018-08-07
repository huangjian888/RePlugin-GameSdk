package com.haowan123.funcell.sdk.util;

import java.lang.reflect.Field;

import android.content.Context;
import android.util.Log;
import com.sdk.interactive.aidl.BuildConfig;

/**
 * 获取资源的ID
 *
 */
public class RUtils {

	public static int layout(Context paramContext, String paramString) {
//		Log.e("RUtils", " package name is "+paramContext.getPackageName());
		return paramContext.getResources().getIdentifier(paramString, "layout",
				BuildConfig.APPLICATION_ID);
	}

	public static int string(Context paramContext, String paramString) {
		return paramContext.getResources().getIdentifier(paramString, "string",
				BuildConfig.APPLICATION_ID);
	}

	public static int drawable(Context paramContext, String paramString) {
		return paramContext.getResources().getIdentifier(paramString,
				"drawable", BuildConfig.APPLICATION_ID);
	}

	public static int style(Context paramContext, String paramString) {
		return paramContext.getResources().getIdentifier(paramString, "style",
				BuildConfig.APPLICATION_ID);
	}

	public static int id(Context paramContext, String paramString) {
		return paramContext.getResources().getIdentifier(paramString, "id",
				BuildConfig.APPLICATION_ID);
	}

	public static int color(Context paramContext, String paramString) {
		return paramContext.getResources().getIdentifier(paramString, "color",
				BuildConfig.APPLICATION_ID);
	}

	public static int anim(Context paramContext, String paramString) {
		return paramContext.getResources().getIdentifier(paramString, "anim",
				BuildConfig.APPLICATION_ID);
	}

	public static int attr(Context paramContext, String paramString) {
		return paramContext.getResources().getIdentifier(paramString, "attr",
				BuildConfig.APPLICATION_ID);
	}

	public static int raw(Context paramContext, String paramString) {
		return paramContext.getResources().getIdentifier(paramString, "raw",
				BuildConfig.APPLICATION_ID);
	}

	public static int styleable(Context paramContext, String paramString) {

		return (Integer) getResourceId(paramContext, paramString, "styleable");
	}

	public static int[] styleableArray(Context paramContext, String paramString) {

		return (int[]) getResourceId(paramContext, paramString, "styleable");
	}

	/**
	 *
	 * 对于 context.getResources().getIdentifier 无法获取的数据 , 或者数组
	 *
	 * 资源反射值
	 *
	 * @paramcontext
	 *
	 * @param name
	 *
	 * @param type
	 *
	 * @return
	 */

	private static Object getResourceId(Context context, String name,
										String type) {

		String className = BuildConfig.APPLICATION_ID + ".R";

		try {

			Class<?> cls = Class.forName(className);

			for (Class<?> childClass : cls.getClasses()) {

				String simple = childClass.getSimpleName();

				if (simple.equals(type)) {

					for (Field field : childClass.getFields()) {

						String fieldName = field.getName();


						if (fieldName.equals(name)) {


							return field.get(null);

						}

					}

				}

			}

		} catch (Exception e) {

			e.printStackTrace();

		}

		return null;

	}
}
