package com.haowan123.funcell.sdk.util;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haowan123.funcell.sdk.ui.FunSdkUiActivity;

/**
 * 可以永远显示在android屏幕最上方的浮动菜单
 * 
 */
public class FunFloatingTool {
	/**
	 * 浮动窗口在屏幕中的x坐标
	 */
	private static float x = 0;
	/**
	 * 浮动窗口在屏幕中的y坐标
	 */
	private static float y = 25;
	/**
	 * 屏幕触摸状态，暂时未使用
	 */
	private static float state = 0;
	/**
	 * 鼠标触摸开始位置
	 */
	private static float mTouchStartX = 0;
	/**
	 * 鼠标触摸结束位置
	 */
	private static float mTouchStartY = 0;
	/**
	 * windows 窗口管理器
	 */
	public static WindowManager wm = null;

	/**
	 * 浮动显示对象
	 */
	private static View mFloatingViewObj = null;

	/**
	 * 参数设定类
	 */
	public static WindowManager.LayoutParams params = new WindowManager.LayoutParams();
	public static int TOOL_BAR_HIGH = 0;
	/**
	 * 要显示在窗口最前面的对象
	 */
	private static View view_obj = null;

	public static boolean isShow = false;

	private static OnClickListener onClickListener = null;

	private static float mStartX = 0;
	private static float mStartY = 0;

	private static int screenWidth = 0;
	private static int screenHeight = 0;

	public static boolean isLeftPosition = true;

	private static LinearLayout linearLayout = null;
	private static ImageView floatButton = null;
	private static LinearLayout relativeLayout = null;
	private static LinearLayout.LayoutParams leftLayoutParams = null;
	private static LinearLayout.LayoutParams centerLayoutParams = null;
	private static LinearLayout.LayoutParams rightLayoutParams = null;
	private static TextView hideBtn, userBtn, snsBtn;

	/***************** 定时器相关操作，5秒内未点击浮标则将浮标缩小 *****************/
	private static Context mContext = null;
	private static int mSIGN = 0;
	private static boolean mIsHide = false;
	private static boolean mIsShow = false;
	private static Timer mTimer = null;
	private static CheckTimerTask mTimerTask = null;
	private static long mCheckTime = 3 * 1000;
	static Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if (msg.what == mSIGN) {
				Context context = (Context) msg.obj;
				if(linearLayout != null){
					linearLayout.setVisibility(View.GONE);
				}
				/**
				 * 区分是左边隐藏还是右边隐藏
				 */
				if (isLeftPosition) {
					// 更新浮动窗口位置参数
					params.x = params.width;
					if(wm != null && FunFloatingTool.mFloatingViewObj != null && FunFloatingTool.floatButton != null && FunFloatingTool.params != null){
						wm.updateViewLayout(mFloatingViewObj, params);
						floatButton.setImageResource(RUtils.drawable(context,
								"fun_left_hide"));
					}
				} else {
					params.x = (int) (screenWidth - params.width);
					if(wm != null && FunFloatingTool.mFloatingViewObj != null && FunFloatingTool.floatButton != null && FunFloatingTool.params != null){
						wm.updateViewLayout(mFloatingViewObj, params);
						floatButton.setImageResource(RUtils.drawable(context,
								"fun_right_hide"));
					}
				}
				mIsHide = true;
				mIsShow = false;
				isShow = false;
			}
		}
	};

	/************************************************/
	public static void show(Context context) {
		if (null == relativeLayout) {
			return;
		}

		show(context, relativeLayout);
	}

	/**
	 * 要显示在窗口最前面的方法
	 * 
	 * @param context
	 *            调用对象Context getApplicationContext()
	 * @param window
	 *            调用对象 Window getWindow()
	 * @param floatingViewObj
	 *            要显示的浮动对象 View
	 */
	public static void show(Context context, View floatingViewObj) {

		mFloatingViewObj = floatingViewObj;

		view_obj = mFloatingViewObj;
		Rect frame = new Rect();
		// 这一句是关键，让其在top 层显示
		// getWindow()
		// window.getDecorView().getWindowVisibleDisplayFrame(frame);
		TOOL_BAR_HIGH = frame.top;

		wm = (WindowManager) context// getApplicationContext()
				.getSystemService(Context.WINDOW_SERVICE);

		params.type = WindowManager.LayoutParams.TYPE_APPLICATION;// 2;
		// params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
		// | WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
		// params.type = 2003;//WindowManager.LayoutParams.TYPE_PHONE;
		params.format = -2;
		params.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL
				| LayoutParams.FLAG_NOT_FOCUSABLE;

		// 设置悬浮窗口长宽数据
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;
		params.height = HWUtils.dip2px(context, 60);
		// 设定透明度
		params.alpha = 80;
		// 设定内部文字对齐方式
		params.gravity = Gravity.LEFT | Gravity.TOP;

		screenWidth = wm.getDefaultDisplay().getWidth(); // 屏幕宽（像素，如：480px）
		screenHeight = wm.getDefaultDisplay().getHeight(); // 屏幕高（像素，如：800px）
		// 以屏幕左上角为原点，设置x、y初始值ֵ
		// params.x = (int) (screenWidth - params.width);
		String postions = HWPreferences.getData(context, "HW_postions");
		String mY = HWPreferences.getData(context, "HW_currentY");
		if (null != postions && null != mY && 0 < mY.length()) {
			if (Boolean.valueOf(postions)) {
				params.x = params.width;
			} else {
				params.x = (int) (screenWidth - params.width);
			}
			isLeftPosition = Boolean.valueOf(postions);
			params.y = Integer.valueOf(mY) + 60;

		} else {
			params.x = params.width;
			params.y = ((screenHeight - params.height) / 2) + 60;

		}
		// params.y = (int) y;
		// tv = new MyTextView(TopFrame.this);

		wm.addView(floatingViewObj, params);
		wm.updateViewLayout(mFloatingViewObj, params);
		isShow = true;
		mIsShow = true; // 定时器检测浮标是否已经显示出来的标志

	}

	/**
	 * 跟谁滑动移动
	 * 
	 * @param event
	 *            事件对象
	 * @param view
	 *            弹出对象实例（View）
	 * @return
	 */
	public static boolean onTouchEvent(MotionEvent event, View view) {

		// 获取相对屏幕的坐标，即以屏幕左上角为原点
		x = event.getRawX();
		y = event.getRawY(); // y = event.getRawY()-25; 25是系统状态栏的高度
								// //游戏为全屏,貌似不存在-25操作
		/*************** 定时器相关 **************/
		mIsShow = false;
		mIsHide = false;
		if (mTimer != null && mTimerTask != null) {
			mTimer.cancel();
			mTimer = null;
			mTimerTask = null;
		}
		/******************************/
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			state = MotionEvent.ACTION_DOWN;
			mTouchStartX = event.getX();
			mTouchStartY = event.getY();
			mStartX = event.getX();
			mStartY = event.getY();

			break;
		case MotionEvent.ACTION_MOVE:
			state = MotionEvent.ACTION_MOVE;
			float cTouchStartX = event.getX();
			float cTouchStartY = event.getY();

			if (!isShow) {
				break;
			}

			if (Math.abs(cTouchStartX - mTouchStartX) < 10
					&& Math.abs(cTouchStartY - mTouchStartY) < 10) {
				break;
			}
			updateViewPosition(view);

			break;

		case MotionEvent.ACTION_UP:
			state = MotionEvent.ACTION_UP;

			mTouchStartX = event.getX();
			mTouchStartY = event.getY();

			updateViewPositionForUp(view);

			if (Math.abs(mStartX - mTouchStartX) < 20
					&& Math.abs(mStartY - mTouchStartY) < 20) {
				if (null != onClickListener) {
					onClickListener.onClick(view);
				}
			}
			if (!isShow) {
				// wm.updateViewLayout(mFloatingViewObj, params);
			}

			/*************** 定时器相关 *************/
			mIsShow = true;
			if (mTimer == null && mTimerTask == null) {
				mTimer = new Timer();
				mTimerTask = new CheckTimerTask(mContext);
				// 开始一个定时任务
				mTimer.schedule(mTimerTask, mCheckTime, mCheckTime);
			}
			/**********************/
			break;
		}
		return true;
	}

	public static void setOnClickListener(OnClickListener l) {
		onClickListener = l;
	}

	/**
	 * 关闭浮动显示对象
	 */
	public static void close(Context context) {
		HWPreferences.addData(context, "HW_currentY", params.y + "");
		HWPreferences.addData(context, "HW_postions", isLeftPosition + "");

		if (view_obj != null) {
			// wm = (WindowManager) context
			// .getSystemService(Activity.WINDOW_SERVICE);
			wm.removeView(view_obj);
		}

		wm = null;
		view_obj = null;
		mFloatingViewObj = null;
		clearAllView();
		isShow = false;
		/************* 定时器相关 ***************/
		mIsShow = false;
		mIsHide = false;
		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}
		mTimerTask = null;
		/********************************/
	}

	public static void closeFloatToolsBar(Context context) {

		if (view_obj != null) {
			// wm = (WindowManager) context
			// .getSystemService(Activity.WINDOW_SERVICE);
			wm.removeView(view_obj);
		}

		wm = null;
		view_obj = null;
		mFloatingViewObj = null;
		clearAllView();
		isShow = false;
		/***************** 定时器相关 *****************/
		mIsShow = false;
		mIsHide = false;
		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}
		mTimerTask = null;
		/********************************/
	}

	/**
	 * 更新弹出窗口位置
	 */
	public static void updateViewPosition(View view) {
		// 更新浮动窗口位置参数
		params.x = (int) (x - mTouchStartX);
		params.y = (int) (y - mTouchStartY);
		wm.updateViewLayout(mFloatingViewObj, params);
	}

	/**
	 * 当MotionEvent.ACTION_UP时,更新窗口位置
	 * 
	 * @param view
	 */
	public static void updateViewPositionForUp(View view) {
		// 更新浮动窗口位置参数
		params.x = (int) (x - mTouchStartX);
//		params.y = (int) (y - mTouchStartY) + 60;
		params.y = (int) (y - mTouchStartY);
		if (screenWidth / 2 > params.x) {
			params.x = params.width;
			isLeftPosition = true;
		} else {
			params.x = screenWidth - params.width;
			isLeftPosition = false;
		}
		if (Math.abs(mStartX - mTouchStartX) < 20
				&& Math.abs(mStartY - mTouchStartY) < 20) {
			return;
		}
		wm.updateViewLayout(mFloatingViewObj, params);
	}

	public static void initFloatToolBar(final Context context) {
		closeFloatToolsBar(context);

		/******************* 定时器操作 *****************/
		mContext = context;
		if (mTimer == null && mTimerTask == null) {
			mTimer = new Timer();
			mTimerTask = new CheckTimerTask(context);
			// 开始一个定时任务
			mTimer.schedule(mTimerTask, 0, mCheckTime);
		}
		/******************************************/

		if (null == relativeLayout) {
			relativeLayout = new LinearLayout(context);
			floatButton = new ImageView(context);
			linearLayout = new LinearLayout(context);
			hideBtn = new TextView(context);
			userBtn = new TextView(context);
			snsBtn = new TextView(context);
		}
		linearLayout.removeAllViews();
		relativeLayout.removeAllViews();

		floatButton.setImageResource(RUtils.drawable(context, "tools_icon"));
		floatButton.setBackgroundColor(Color.TRANSPARENT);

		linearLayout.setLayoutParams(new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		linearLayout.setVisibility(View.GONE);
		linearLayout.setBackgroundResource(RUtils.drawable(context,
				"fun_bg_right_icon"));

		// hideBtn.setText("隐藏");
		hideBtn.setTextSize(HWUtils.sp2px(context, 3));
		hideBtn.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
		hideBtn.setTextColor(context.getResources().getColor(
				RUtils.color(context, "fun_floating_view_text")));
		final Drawable hideDrawableTop = context.getResources().getDrawable(
				RUtils.drawable(context, "fun_hide_icon"));
		hideBtn.setCompoundDrawablesWithIntrinsicBounds(null, hideDrawableTop,
				null, null);

		// userBtn.setText("账号");
		userBtn.setTextSize(HWUtils.sp2px(context, 3));
		userBtn.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
		userBtn.setTextColor(context.getResources().getColor(
				RUtils.color(context, "fun_floating_view_text")));
		final Drawable accountCenterDrawableTop = context
				.getResources()
				.getDrawable(RUtils.drawable(context, "fun_accountcenter_icon"));
		userBtn.setCompoundDrawablesWithIntrinsicBounds(null,
				accountCenterDrawableTop, null, null);

		// snsBtn.setText("社区");
		snsBtn.setTextSize(HWUtils.sp2px(context, 3));
		snsBtn.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
		snsBtn.setTextColor(context.getResources().getColor(
				RUtils.color(context, "fun_floating_view_text")));
		snsBtn.setCompoundDrawablesWithIntrinsicBounds(null,
				accountCenterDrawableTop, null, null);

		initViewEvents(context);

		android.widget.LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		leftLayoutParams = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		centerLayoutParams = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		rightLayoutParams = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

		leftLayoutParams.leftMargin = HWUtils.dip2px(context, 10);
		// leftLayoutParams.bottomMargin=HWUtils.dip2px(context, 1);

		rightLayoutParams.rightMargin = HWUtils.dip2px(context, 15);
		// rightLayoutParams.bottomMargin=HWUtils.dip2px(context, 1);

		centerLayoutParams.leftMargin = HWUtils.dip2px(context, 15);
		centerLayoutParams.rightMargin = HWUtils.dip2px(context, 15);

		linearLayout.setOrientation(LinearLayout.HORIZONTAL);
		linearLayout.setGravity(Gravity.TOP);
		linearLayout.setWeightSum(1);

		linearLayout.addView(hideBtn, leftLayoutParams);
		linearLayout.addView(userBtn, centerLayoutParams);
		// linearLayout.addView(snsBtn, rightLayoutParams);

		relativeLayout.setLayoutParams(new LayoutParams(
				LayoutParams.WRAP_CONTENT, HWUtils.dip2px(context, 50)));

		relativeLayout.addView(linearLayout, layoutParams);
		relativeLayout.addView(floatButton, layoutParams);

		setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// linearLayout.setVisibility(View.GONE);

				if (isShow) {
					updateBackground(context, isLeftPosition);
					updateLayout(context, isLeftPosition);
					linearLayout.setVisibility(View.VISIBLE);
					isShow = false;
				} else {
					linearLayout.setVisibility(View.GONE);
					floatButton.setImageResource(RUtils.drawable(context,
							"tools_icon"));
					isShow = true;
				}

			}
		});
		floatButton.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				onTouchEvent(event, v);
				return true;
			}
		});

		show(context, relativeLayout);
	}

	/**
	 * 初始化各个功能按钮事件
	 * 
	 * @param context
	 */
	private static void initViewEvents(final Context context) {
		hideBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				close(context);
				((Activity) context).runOnUiThread(new Runnable() {

					@Override
					public void run() {
						FunCustomDialog.Builder builder = new FunCustomDialog.Builder(
								context);
						builder.setMessage(context.getResources().getString(
								RUtils.string(context,
										"fun_error_page_dialog_msg_title")));
						builder.setTitle(context.getResources().getString(
								RUtils.string(context,
										"fun_error_page_head_title")));
						builder.setPositiveButton(
								context.getResources()
										.getString(
												RUtils.string(context,
														"fun_error_page_dialog_btn_sure_title")),
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
										close(context);
									}
								});

						builder.setNegativeButton(
								context.getResources()
										.getString(
												RUtils.string(context,
														"fun_error_page_dialog_btn_cancel_title")),
								new android.content.DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
										initFloatToolBar(context);
									}
								});

						builder.create().show();

					}
				});

			}
		});
		userBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, FunSdkUiActivity.class);
				intent.putExtra("fun_action", "usercenter");
				context.startActivity(intent);
			}
		});
		snsBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
			}
		});
	}

	private static void updateBackground(Context context, boolean isLeft) {

		if (isLeft) {
			linearLayout.setBackgroundResource(RUtils.drawable(context,
					"fun_bg_right_icon"));
			floatButton.setImageResource(RUtils.drawable(context,
					"fun_right_click_icon"));
		} else {
			linearLayout.setBackgroundResource(RUtils.drawable(context,
					"fun_bg_left_icon"));
			floatButton.setImageResource(RUtils.drawable(context,
					"fun_left_click_icon"));

		}
	}

	private static void updateLayout(Context context, boolean isLeft) {
		android.widget.LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, HWUtils.dip2px(context, 50));
		relativeLayout.removeAllViews();
		linearLayout.removeAllViews();
		linearLayout.setGravity(Gravity.TOP);
		linearLayout.setWeightSum(1);

		if (isLeft) {
			leftLayoutParams.leftMargin = HWUtils.dip2px(context, 10);
			leftLayoutParams.rightMargin = HWUtils.dip2px(context, 15);

			rightLayoutParams.leftMargin = HWUtils.dip2px(context, 0);
			rightLayoutParams.rightMargin = HWUtils.dip2px(context, 15);
			linearLayout.addView(hideBtn, leftLayoutParams);
			// linearLayout.addView(userBtn, centerLayoutParams);
			linearLayout.addView(userBtn, rightLayoutParams);

			relativeLayout.addView(floatButton, layoutParams);
			relativeLayout.addView(linearLayout, layoutParams);
		} else {
			leftLayoutParams.leftMargin = HWUtils.dip2px(context, 15);
			leftLayoutParams.rightMargin = HWUtils.dip2px(context, 0);

			rightLayoutParams.rightMargin = HWUtils.dip2px(context, 10);
			rightLayoutParams.leftMargin = HWUtils.dip2px(context, 15);
			linearLayout.addView(userBtn, leftLayoutParams);
			// linearLayout.addView(userBtn, centerLayoutParams);
			linearLayout.addView(hideBtn, rightLayoutParams);

			relativeLayout.addView(linearLayout, layoutParams);
			relativeLayout.addView(floatButton, layoutParams);

		}
	}

	private static void clearAllView() {
		linearLayout = null;
		floatButton = null;
		relativeLayout = null;
		leftLayoutParams = null;
		centerLayoutParams = null;
		rightLayoutParams = null;
		hideBtn = null;
		userBtn = null;
		snsBtn = null;
	}

	/********* 定时器检测类 ***********/
	private static class CheckTimerTask extends TimerTask {
		private Context mContext;

		public CheckTimerTask(Context context) {
			mContext = context;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			if (mIsShow && mIsHide == false &&(!((Activity)mContext).isFinishing())) {
				Message msg = new Message();
				msg.what = mSIGN;
				msg.obj = mContext;
				mHandler.sendMessage(msg);
			}
		}
	}
	/*******************/
}