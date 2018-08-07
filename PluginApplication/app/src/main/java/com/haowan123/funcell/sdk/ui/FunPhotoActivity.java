package com.haowan123.funcell.sdk.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.haowan123.funcell.sdk.apiinterface.FunCellPlatformSdkApi;
import com.haowan123.funcell.sdk.permission.PermissionCallBack;
import com.haowan123.funcell.sdk.permission.PermissionConfig;
import com.haowan123.funcell.sdk.permission.PermissionUtil;
import com.haowan123.funcell.sdk.util.HWUtils;
import com.haowan123.funcell.sdk.util.RUtils;

public class FunPhotoActivity extends Activity {
	private String username, pwd, token,fid;
	private TextView userNameTv, pwdTv;
	private NumberProgressBar guestNpb;
	private int counter = 0;
	private Timer timer;
	Bitmap imageBitmap;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(RUtils.layout(FunPhotoActivity.this, "fun_login_guest"));
		HWUtils.setWindowDisplayMode(FunPhotoActivity.this, 180, 0.6f, 0.8f);
		initData();

		initView();
		
		PermissionUtil.getInstance().initialize(FunPhotoActivity.this, new PermissionCallBack(){

			@Override
			public void callBack(boolean paramBoolean) {
				// TODO Auto-generated method stub
				if(paramBoolean){
//					saveBitmap(imageBitmap);
					saveMyBitmap(imageBitmap);
					FunLoginActivity.mLoginCallBack.loginSuccess(token,fid);
				}
				finish();
			}
			
		});
//		counter = 0;
		timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						guestNpb.incrementProgressBy(2);
//						counter++;
//						if (counter >= 100) {
//							guestNpb.setProgress(100);
//
//						}
					}
				});
			}
		}, 0, 100);

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				timer.cancel();
				guestNpb.cancelLongPress();
				insertImage();
//				FunLoginActivity.mLoginCallBack.loginSuccess(token,fid);
//				finish();

			}
		}, 5000);

	}

	private void initView() {
		userNameTv = (TextView) findViewById(RUtils.id(FunPhotoActivity.this, "fun_login_guest_username"));
		pwdTv = (TextView) findViewById(RUtils.id(FunPhotoActivity.this, "fun_login_guest_pwd"));
		guestNpb = (NumberProgressBar) findViewById(RUtils.id(FunPhotoActivity.this, "fun_login_guest_npb"));

		userNameTv.setText(username);
		pwdTv.setText(pwd);

	}

	private Bitmap getPhotoBitmap() {
//		Bitmap bitmap = Bitmap.createBitmap(1000, 200, Config.ARGB_8888);// 创建一个你需要尺寸的Bitmap
//		Canvas canvas = new Canvas(bitmap);// 用这个Bitmap生成一个Canvas,然后canvas就会把内容绘制到上面这个bitmap中
//
//		Rect targetRect = new Rect(50, 50, 1000, 200);
//		Rect targetRect1 = new Rect(50, 200, 1000, 400);
//
//		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
//		paint.setStrokeWidth(3);
//		paint.setTextSize(60);
//
//		String testString = "用户名：xiaoweiwei";
//		String testString1 = "密码:1212312";
//		paint.setColor(Color.CYAN);
//		canvas.drawRect(targetRect, paint);
//		canvas.drawRect(targetRect1, paint);
//		paint.setColor(Color.RED);
//		FontMetricsInt fontMetrics = paint.getFontMetricsInt();
//
//		int baseline = targetRect.top
//				+ (targetRect.bottom - targetRect.top - fontMetrics.bottom + fontMetrics.top)
//				/ 2 - fontMetrics.top;
//		int baseline1 = targetRect1.top
//				+ (targetRect1.bottom - targetRect1.top - fontMetrics.bottom + fontMetrics.top)
//				/ 2 - fontMetrics.top;
//
//		// 下面这行是实现水平居中，drawText对应改为传入targetRect.centerX()
//		paint.setTextAlign(Paint.Align.CENTER);
//		canvas.drawText(testString, targetRect.centerX(), baseline, paint);
//		canvas.drawText(testString1, targetRect1.centerX(), baseline1, paint);

//		try {
//
//			ContentResolver cr = getContentResolver();
//
//			String url = MediaStore.Images.Media.insertImage(cr, bitmap,
//					"funcell", "");
//
//			HWUtils.logError("FunPhotoActivity", "photo url = " + url);
//			sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
//					Uri.parse("file://"
//							+ Environment.getExternalStorageDirectory())));
//
//			Toast.makeText(this, "保存成功!", Toast.LENGTH_SHORT).show();
//
//		} catch (Exception e) {
//
//			e.printStackTrace();
//
//		}
		
		
		insertImage();

		return null;
	}

	private void initData() {
		Intent intent = getIntent();

		if (null == intent) {
			return;
		}

		username = intent.getStringExtra("username");
		pwd = intent.getStringExtra("pwd");
		token = intent.getStringExtra("token");
		fid = intent.getStringExtra("fid");
	}
	
	public void onRequestPermissionsResult(int requestCode,String[] permissions, int[] grantResults) {
		// TODO Auto-generated method stub
		Log.e("FunPhotoActivity", "-------onRequestPermissionsResult-----");
		FunCellPlatformSdkApi.getInstance().onRequestPermissionsResult(requestCode, permissions, grantResults);
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		HWUtils.setWindowDisplayMode(FunPhotoActivity.this, 180, 0.6f, 0.8f);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return false;
		}

		return super.onKeyDown(keyCode, event);
	}

	public void insertImage() {
		// 1.构建Bitmap
		WindowManager windowManager = getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		int w = display.getWidth();// w=480
		int h = display.getHeight();// h=800
		imageBitmap = Bitmap.createBitmap(w, h, Config.ARGB_8888);// 最后一个参数叫位图结构
		// ARGB--Alpha,Red,Green,Blue.
		// ARGB为一种色彩模式,也就是RGB色彩模式附加上Alpha(透明度)通道,常见于32位位图的存储结构。

		// 2.获取屏幕
		View decorview = this.getWindow().getDecorView();// decor意思是装饰布置
		decorview.setDrawingCacheEnabled(true);
		imageBitmap = decorview.getDrawingCache();

		PermissionUtil.getInstance().getPermission(PermissionConfig.Permission.ANDROID_PERMISSION_WRITE_EXTERNAL_STORAGE);
//		saveBitmap(imageBitmap);
	}

	private void saveBitmap(Bitmap bitmap) {
//		ContentValues values = new ContentValues(8);
//		String newname = DateFormat.format("yyyy-MM-dd kk.mm.ss",
//				System.currentTimeMillis()).toString();
//		values.put(MediaStore.Images.Media.TITLE, newname);// 名称，随便
//		values.put(MediaStore.Images.Media.DISPLAY_NAME, newname);
//		values.put(MediaStore.Images.Media.DESCRIPTION, "test");// 描述，随便
//		values.put(MediaStore.Images.Media.DATE_TAKEN,
//				System.currentTimeMillis());// 图像的拍摄时间，显示时根据这个排序
//		values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");// 默认为jpg格式
//		values.put(MediaStore.Images.Media.ORIENTATION, 0);//
//
//		final String CAMERA_IMAGE_BUCKET_NAME = Environment
//				.getExternalStorageDirectory()
//				+ File.separator
//				+ Environment.DIRECTORY_DCIM;
//		final String CAMERA_IMAGE_BUCKET_ID = String
//				.valueOf(CAMERA_IMAGE_BUCKET_NAME.hashCode());
//		File parentFile = new File(CAMERA_IMAGE_BUCKET_NAME);
//		String name = parentFile.getName().toLowerCase();
//
//		values.put(Images.ImageColumns.BUCKET_ID, CAMERA_IMAGE_BUCKET_ID);// id
//		values.put(Images.ImageColumns.BUCKET_DISPLAY_NAME, name);
//
//		// 先得到新的URI
//		Uri uri = getContentResolver().insert(
//				MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
//		try {
//			// 写入数据
//			OutputStream outStream = getContentResolver().openOutputStream(uri);
//			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
//			outStream.close();
//			// bitmap.recycle();
//
//		} catch (Exception e) {
//			Log.e("MainActivity", "exception while writing image", e);
//
//		}
		try {
			Uri uri = getImageFileUri();
			if(uri == null){
				return;
			}
			OutputStream outStream = getContentResolver().openOutputStream(uri);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
			outStream.close();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
	}
	
	public Uri getImageFileUri() throws IOException{
	    
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {

			if (Environment.MEDIA_MOUNTED.equals(Environment
					.getExternalStorageState())) {
				Log.e("FunPhotoActivity", "1111111111111111111111");
				ContentValues values = new ContentValues(8);
				String newname = DateFormat.format("yyyy-MM-dd kk.mm.ss",
						System.currentTimeMillis()).toString();
				values.put(MediaStore.Images.Media.TITLE, newname);// 名称，随便
				values.put(MediaStore.Images.Media.DISPLAY_NAME, newname);
				values.put(MediaStore.Images.Media.DESCRIPTION, "test");// 描述，随便
				values.put(MediaStore.Images.Media.DATE_TAKEN,
						System.currentTimeMillis());// 图像的拍摄时间，显示时根据这个排序
				values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");// 默认为jpg格式
				values.put(MediaStore.Images.Media.ORIENTATION, 0);//

				final String CAMERA_IMAGE_BUCKET_NAME = Environment
						.getExternalStorageDirectory()
						+ File.separator
						+ Environment.DIRECTORY_DCIM;
				final String CAMERA_IMAGE_BUCKET_ID = String
						.valueOf(CAMERA_IMAGE_BUCKET_NAME.hashCode());
				File parentFile = new File(CAMERA_IMAGE_BUCKET_NAME);
				String name = parentFile.getName().toLowerCase();

				values.put(Images.ImageColumns.BUCKET_ID, CAMERA_IMAGE_BUCKET_ID);// id
				values.put(Images.ImageColumns.BUCKET_DISPLAY_NAME, name);

			
				return getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
			} else {
				Log.e("FunPhotoActivity", "2222222222222");
				File sdCardDir = Environment.getExternalStorageDirectory();

				String CAMERA_IMAGE_BUCKET_NAME = sdCardDir + File.separator
						+ "funcell123";

				ContentValues values = new ContentValues(8);
				String newname = DateFormat.format("yyyy-MM-dd kk.mm.ss",
						System.currentTimeMillis()).toString();
				values.put(MediaStore.Images.Media.TITLE, newname);// 名称，随便
				values.put(MediaStore.Images.Media.DISPLAY_NAME, newname);
				values.put(MediaStore.Images.Media.DESCRIPTION, "test");// 描述，随便
				values.put(MediaStore.Images.Media.DATE_TAKEN,
						System.currentTimeMillis());// 图像的拍摄时间，显示时根据这个排序
				values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");// 默认为jpg格式
				values.put(MediaStore.Images.Media.ORIENTATION, 0);//

				final String CAMERA_IMAGE_BUCKET_ID = String
						.valueOf(CAMERA_IMAGE_BUCKET_NAME.hashCode());
				File parentFile = new File(CAMERA_IMAGE_BUCKET_NAME);
				String name = parentFile.getName().toLowerCase();

				values.put(Images.ImageColumns.BUCKET_ID,
						CAMERA_IMAGE_BUCKET_ID);// id
				values.put(Images.ImageColumns.BUCKET_DISPLAY_NAME, name);

				return getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
			}

		}
		return null;

	}
	
	public void saveMyBitmap(Bitmap mBitmap){
//		  File f = new File("/sdcard/" + bitName + ".png");
		  
		  File sdCardDir = Environment.getExternalStorageDirectory();
		  File dirFile = new File(sdCardDir + File.separator + "funcell123"+File.separator+"photos");
		  String fileName = DateFormat.format("yyyy-MM-dd kk.mm.ss",System.currentTimeMillis()).toString();
		  File file = new File(sdCardDir + File.separator + "funcell123" + File.separator +"photos"+File.separator + fileName+".png");
		  if (!dirFile.exists()) {
			  dirFile.mkdirs();
		  } 
		
		  try {
			  file.createNewFile();
		  } catch (IOException e) {
		   // TODO Auto-generated catch block
			  Log.e("FunPhotoActivity","----save fail------");
		  }
		  FileOutputStream fOut = null;
		  try {
		   fOut = new FileOutputStream(file);
		  } catch (FileNotFoundException e) {
		   e.printStackTrace();
		  }
		  mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
		  try {
		   fOut.flush();
		  } catch (IOException e) {
		   e.printStackTrace();
		  }
		  try {
		   fOut.close();
		  } catch (IOException e) {
		   e.printStackTrace();
		  }
		 }
}
