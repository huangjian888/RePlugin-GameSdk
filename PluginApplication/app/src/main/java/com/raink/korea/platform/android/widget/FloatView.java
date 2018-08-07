package com.raink.korea.platform.android.widget;

import java.util.Timer;
import java.util.TimerTask;


import android.content.Context;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;

import com.qihoo360.replugin.RePlugin;

public class FloatView extends FrameLayout implements View.OnTouchListener{
	
	private String TAG = "FloatView";
	private final int HANDLER_TYPE_HIDE_LOGO = 100;
  private final int HANDLER_TYPE_CANCEL_ANIM = 101;
  private WindowManager.LayoutParams mWmParams;
  private WindowManager mWindowManager;
  private Context mHostContext;
  private ImageView mIvFloatLogo;
  private ImageView mIvFloatLoader;
  private LinearLayout mLlFloatMenu;
  private ImageButton btnAccount;
  private ImageButton btnAccountProblem;
  private ImageButton btnNotice;
  private ImageButton btnCommunity;
  private FrameLayout mFlFloatLogo;
  private boolean mIsRight;
  private boolean mCanHide;
  private float mTouchStartX;
  private float mTouchStartY;
  private int mScreenWidth;
  private int mScreenHeight;
  private boolean mDraging;
  private boolean mShowLoader = true;
  private LinearLayout mFloatLayout;
  private Timer mTimer;
  private TimerTask mTimerTask;
	  final Handler mTimerHandler = new Handler()
	  {
	    public void handleMessage(Message msg)
	    {
	      if (msg.what == HANDLER_TYPE_HIDE_LOGO)
	      {
	        if (mCanHide)
	        {
	          mCanHide = false;
	          if (mIsRight) {
	            mIvFloatLogo.setImageResource(RUtils.drawable(RePlugin.getPluginContext(), "fun_right_hide"));
	          } else {
	            mIvFloatLogo.setImageResource(RUtils.drawable(RePlugin.getPluginContext(), "fun_left_hide"));
	          }
	          mWmParams.alpha = 0.5F;
	          mWindowManager.updateViewLayout(FloatView.this, mWmParams);
	          refreshFloatMenu(mIsRight);
	          mLlFloatMenu.setVisibility(View.GONE);
	        }
	      }
	      else if (msg.what == HANDLER_TYPE_CANCEL_ANIM)
	      {
	        mIvFloatLoader.clearAnimation();
	        mIvFloatLoader.setVisibility(View.GONE);
	        mShowLoader = false;
	      }
	      super.handleMessage(msg);
	    }
	  };
	  
	  public FloatView(Context hostContext)
	  {
	    super(hostContext);
	    init(hostContext);
	  }
	  
	  private void init(Context hostContext)
	  {
	    mHostContext = hostContext;
	    
	    mWindowManager = ((WindowManager)mHostContext.getSystemService(Context.WINDOW_SERVICE));
	    
	    DisplayMetrics dm = new DisplayMetrics();
	    
	    mWindowManager.getDefaultDisplay().getMetrics(dm);
	    mScreenWidth = dm.widthPixels;
	    mScreenHeight = dm.heightPixels;
	    mWmParams = new WindowManager.LayoutParams();
	    // 设置window type
	    mWmParams.type = WindowManager.LayoutParams.TYPE_APPLICATION;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            mWmParams.type = WindowManager.LayoutParams.TYPE_TOAST;
//        } else {
//         		mWmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
//        }
        // 设置图片格式，效果为背景透明
//	    mWmParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        mWmParams.format = PixelFormat.RGBA_8888;
//	    mWmParams.format = PixelFormat.TRANSLUCENT | WindowManager.LayoutParams.FIRST_SYSTEM_WINDOW;
	    
        // 设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        mWmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
	    
        // 调整悬浮窗显示的停靠位置为左侧置�?
        mWmParams.gravity = Gravity.LEFT | Gravity.TOP;
	    
	    mScreenHeight = mWindowManager.getDefaultDisplay().getHeight();
	    
	    // 以屏幕左上角为原点，设置x、y初始值，相对于gravity
	    mWmParams.x = 0;
	    mWmParams.y = (mScreenHeight / 2);
	    // 设置悬浮窗口长宽数据
	    mWmParams.width = LayoutParams.WRAP_CONTENT;
	    mWmParams.height = LayoutParams.WRAP_CONTENT;
	    addView(createView(mHostContext));
	    mWindowManager.addView(this, mWmParams);
	    mTimer = new Timer();
	    hide();
	  }

	  protected void onConfigurationChanged(Configuration newConfig)
	  {
	    super.onConfigurationChanged(newConfig);
	    

	    DisplayMetrics dm = new DisplayMetrics();
	    
	    mWindowManager.getDefaultDisplay().getMetrics(dm);
	    mScreenWidth = dm.widthPixels;
	    mScreenHeight = dm.heightPixels;
	    int oldX = mWmParams.x;
	    int oldY = mWmParams.y;
	    switch (newConfig.orientation)
	    {
	    case Configuration.ORIENTATION_LANDSCAPE: 
	      if (mIsRight)
	      {
	        mWmParams.x = mScreenWidth;
	        mWmParams.y = oldY;
	      }
	      else
	      {
	        mWmParams.x = oldX;
	        mWmParams.y = oldY;
	      }
	      break;
	    case Configuration.ORIENTATION_PORTRAIT: 
	      if (mIsRight)
	      {
	        mWmParams.x = mScreenWidth;
	        mWmParams.y = oldY;
	      }
	      else
	      {
	        mWmParams.x = oldX;
	        mWmParams.y = oldY;
	      }
	      break;
	    }
	    mWindowManager.updateViewLayout(this, mWmParams);
	  }
	  
	  private View createView(Context hostContext)
	  {
	    LayoutInflater inflater = LayoutInflater.from(RePlugin.getPluginContext());
	    
	    View rootFloatView = inflater.inflate(RUtils.layout(RePlugin.getPluginContext(), "raink_widget_float_view"), null);
	    mFlFloatLogo = ((FrameLayout)rootFloatView.findViewById(RUtils.id(RePlugin.getPluginContext(), "raink_float_view")));
	    
	    mIvFloatLogo = ((ImageView)rootFloatView.findViewById(RUtils.id(RePlugin.getPluginContext(), "raink_float_view_icon_imageView")));
	    
	    mIvFloatLoader = ((ImageView)rootFloatView.findViewById(RUtils.id(RePlugin.getPluginContext(), "raink_float_view_icon_notify")));
	    
	    mLlFloatMenu = ((LinearLayout)rootFloatView.findViewById(RUtils.id(RePlugin.getPluginContext(), "ll_menu")));
	    
	    btnAccount = (ImageButton) (rootFloatView.findViewById(RUtils.id(RePlugin.getPluginContext(), "buttonAccount")));
	    
	    btnAccount.setOnClickListener(new View.OnClickListener()
	    {
	      public void onClick(View arg0)
	      {
	        mLlFloatMenu.setVisibility(View.GONE);
	        openUserCenter();
	      }
	    });
	    btnAccountProblem = (ImageButton) (rootFloatView.findViewById(RUtils.id(RePlugin.getPluginContext(), "buttonAccountProblem")));
	    
	    btnAccountProblem.setOnClickListener(new View.OnClickListener()
	    {
	      public void onClick(View arg0)
	      {
	    	openProblemfeedback();
	        mLlFloatMenu.setVisibility(View.GONE);
	      }
	    });
	    
	    btnNotice = (ImageButton) (rootFloatView.findViewById(RUtils.id(RePlugin.getPluginContext(), "buttonNotice")));
	    btnNotice.setOnClickListener(new View.OnClickListener()
	    {
	      public void onClick(View arg0)
	      {
	    	openNotice();
	        mLlFloatMenu.setVisibility(View.GONE);
	      }
	    });
	    
	    btnCommunity = (ImageButton) (rootFloatView.findViewById(RUtils.id(RePlugin.getPluginContext(), "buttonCommunity")));
	    btnCommunity.setOnClickListener(new View.OnClickListener()
	    {
	      public void onClick(View arg0)
	      {
	    	openCommunity();
	        mLlFloatMenu.setVisibility(View.GONE);
	      }
	    });
	    
	    rootFloatView.setOnTouchListener(this);
	    rootFloatView.setOnClickListener(new View.OnClickListener()
	    {
	      public void onClick(View v)
	      {
	        if (!mDraging) {
	          if (mLlFloatMenu.getVisibility() == View.VISIBLE) {
	            mLlFloatMenu.setVisibility(View.GONE);
	          } else {
	            mLlFloatMenu.setVisibility(View.VISIBLE);
	          }
	        }
	      }
	    });
	    rootFloatView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
	    
	    return rootFloatView;
	  }
	  
	  @Override
	  public boolean onTouch(View v, MotionEvent event)
	  {
	    removeTimerTask();
	    
	    int x = (int)event.getRawX();
	    int y = (int)event.getRawY();
	    switch (event.getAction())
	    {
	    case MotionEvent.ACTION_DOWN: 
	      mTouchStartX = event.getX();
	      mTouchStartY = event.getY();
	      mIvFloatLogo.setImageResource(RUtils.drawable(RePlugin.getPluginContext(), "fun_default_icon"));
	      
	      mWmParams.alpha = 1.0F;
	      mWindowManager.updateViewLayout(this, mWmParams);
	      mDraging = false;
	      break;
	    case MotionEvent.ACTION_MOVE: 
	      float mMoveStartX = event.getX();
	      float mMoveStartY = event.getY();
	      if ((Math.abs(mTouchStartX - mMoveStartX) > 3.0F) && (Math.abs(mTouchStartY - mMoveStartY) > 3.0F))
	      {
	        mDraging = true;
	        
	        mWmParams.x = ((int)(x - mTouchStartX));
	        mWmParams.y = ((int)(y - mTouchStartY));
	        mWindowManager.updateViewLayout(this, mWmParams);
	        mLlFloatMenu.setVisibility(View.GONE);
	        return false;
	      }
	      break;
	    case MotionEvent.ACTION_UP: 
	    case MotionEvent.ACTION_CANCEL: 
	      if (mWmParams.x >= mScreenWidth / 2)
	      {
	        mWmParams.x = mScreenWidth;
	        mIsRight = true;
	      }
	      else if (mWmParams.x < mScreenWidth / 2)
	      {
	        mIsRight = false;
	        mWmParams.x = 0;
	      }
	      mIvFloatLogo.setImageResource(RUtils.drawable(RePlugin.getPluginContext(), "fun_default_icon"));
	      
	      refreshFloatMenu(mIsRight);
	      timerForHide();
	      mWindowManager.updateViewLayout(this, mWmParams);
	      
	      mTouchStartX = (mTouchStartY = 0.0F);
	    }
	    return false;
	  }
	  
	  private void removeTimerTask()
	  {
	    if (mTimerTask != null)
	    {
	      mTimerTask.cancel();
	      mTimerTask = null;
	    }
	  }
	  
	  private void removeFloatView()
	  {
	    try
	    {
	      mWindowManager.removeView(this);
	    }
	    catch (Exception ex)
	    {
	      ex.printStackTrace();
	    }
	  }
	  
	  public void hide()
	  {
	    setVisibility(View.GONE);
	    Message message = mTimerHandler.obtainMessage();
	    message.what = HANDLER_TYPE_HIDE_LOGO;
	    mTimerHandler.sendMessage(message);
	    removeTimerTask();
	  }
	  
	  public void show()
	  {
	    if (getVisibility() != View.VISIBLE)
	    {
	      setVisibility(View.VISIBLE);
	      if (mShowLoader)
	      {
	        mIvFloatLogo.setImageResource(RUtils.drawable(RePlugin.getPluginContext(), "fun_default_icon"));
	        
	        mWmParams.alpha = 1.0F;
	        mWindowManager.updateViewLayout(this, mWmParams);
	        
	        timerForHide();
	        
	        mShowLoader = false;
	        Animation rotaAnimation = AnimationUtils.loadAnimation(RePlugin.getPluginContext(), RUtils.anim(RePlugin.getPluginContext(), "raink_loading_anim"));
	        
	        rotaAnimation.setInterpolator(new LinearInterpolator());
	        mIvFloatLoader.startAnimation(rotaAnimation);
	        mTimer.schedule(new TimerTask()
	        {
	          public void run()
	          {
	            mTimerHandler.sendEmptyMessage(HANDLER_TYPE_CANCEL_ANIM);
	          }
	        }, 3000L);
	      }
	    }
	  }
	  
	  private void refreshFloatMenu(boolean right)
	  {
	    if (right)
	    {
	      FrameLayout.LayoutParams paramsFloatImage = (FrameLayout.LayoutParams)mIvFloatLogo.getLayoutParams();
	      paramsFloatImage.gravity = 5;
	      mIvFloatLogo.setLayoutParams(paramsFloatImage);
	      FrameLayout.LayoutParams paramsFlFloat = (FrameLayout.LayoutParams)mFlFloatLogo.getLayoutParams();
	      paramsFlFloat.gravity = 5;
	      mFlFloatLogo.setLayoutParams(paramsFlFloat);
	      
	      int padding = (int)TypedValue.applyDimension(1, 4.0F, mHostContext.getResources().getDisplayMetrics());
	      int padding52 = (int)TypedValue.applyDimension(1, 52.0F, mHostContext.getResources().getDisplayMetrics());
	      LinearLayout.LayoutParams paramsMenuAccount = (LinearLayout.LayoutParams)btnAccount.getLayoutParams();
	      paramsMenuAccount.rightMargin = padding;
	      paramsMenuAccount.leftMargin = padding;
	      btnAccount.setLayoutParams(paramsMenuAccount);
	      
	      LinearLayout.LayoutParams paramsMenuFb = (LinearLayout.LayoutParams)btnAccountProblem.getLayoutParams();
	      paramsMenuFb.rightMargin = padding52;
	      paramsMenuFb.leftMargin = padding;
	      btnAccountProblem.setLayoutParams(paramsMenuFb);
	    }
	    else
	    {
	      FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)mIvFloatLogo.getLayoutParams();
	      params.setMargins(0, 0, 0, 0);
	      params.gravity = 3;
	      mIvFloatLogo.setLayoutParams(params);
	      FrameLayout.LayoutParams paramsFlFloat = (FrameLayout.LayoutParams)mFlFloatLogo.getLayoutParams();
	      paramsFlFloat.gravity = 3;
	      mFlFloatLogo.setLayoutParams(paramsFlFloat);
	      
	      int padding = (int)TypedValue.applyDimension(1, 4.0F, mHostContext.getResources().getDisplayMetrics());
	      int padding52 = (int)TypedValue.applyDimension(1, 52.0F, mHostContext.getResources().getDisplayMetrics());
	      
	      LinearLayout.LayoutParams paramsMenuAccount = (LinearLayout.LayoutParams)btnAccount.getLayoutParams();
	      paramsMenuAccount.rightMargin = padding;
	      paramsMenuAccount.leftMargin = padding52;
	      btnAccount.setLayoutParams(paramsMenuAccount);
	      
	      LinearLayout.LayoutParams paramsMenuFb = (LinearLayout.LayoutParams)btnAccountProblem.getLayoutParams();
	      paramsMenuFb.rightMargin = padding;
	      paramsMenuFb.leftMargin = padding;
	      btnAccountProblem.setLayoutParams(paramsMenuFb);
	    }
	  }
	  
	  private void timerForHide()
	  {
	    mCanHide = true;
	    if (mTimerTask != null) {
	      try
	      {
	        mTimerTask.cancel();
	        mTimerTask = null;
	      }
	      catch (Exception localException) {}
	    }
	    mTimerTask = new TimerTask()
	    {
	      public void run()
	      {
	        Message message = mTimerHandler.obtainMessage();
	        message.what = 100;
	        mTimerHandler.sendMessage(message);
	      }
	    };
	    if (mCanHide) {
	      mTimer.schedule(mTimerTask, 6000L, 3000L);
	    }
	  }
	  
	  private void openUserCenter() {
//		  RainkClient.getInstance().userCenter(mContext);
	  }
	  
	private void openProblemfeedback() {
//		Map<FuncellDataTypes, ParamsContainer> maps = FuncellPlatformInterface.getInstance().GetDatasMap();
//		String serverno=null;
//		String role_name = null;
//		for (FuncellDataTypes key : maps.keySet()) {
//			if (FuncellDataTypes.DATA_SERVER_ROLE_INFO == key) {
//				ParamsContainer info = maps.get(key);
//				role_name = info.getString("role_name");
//				serverno = info.getString("serverno");
//			}
//		}
//		RainkClient.getInstance().problemFeedback(mContext,serverno,role_name);
	  }
	  
	  private void openCommunity()
	  {
//		  Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Urls.COMMUNITY));
//		  mContext.startActivity(intent);
//		  try {
//				String roleId = PlatformInterface.GetRoleID();
//		  } catch (Exception e) {
//				// TODO: handle exception
//				Toast.makeText(mContext, RUtils.string(mContext, "raink_sdk_problemfeedback"), Toast.LENGTH_SHORT).show(); //请选择角色和服务器后使用该功能
//				return;
//		  }
//		  String naverOauthClientId = Tools.getMetadata(mContext, "naver_client_id");
//		  Glink.setGameUserId((Activity)mContext, PlatformInterface.GetRoleID(), naverOauthClientId);
	  }
	  
	  private void openNotice()
	  {
		  /*NoticeMode notice = NoticeMode.getInstance().getNotice();
		  if(notice != null && ((notice.getContent() != null &&  notice.getContent().length() > 0) || (notice.getLink() != null &&  notice.getLink().length() > 0))){
			  NoticeWebViewDialog noticeWebViewDialog = new NoticeWebViewDialog(mContext);
			  noticeWebViewDialog.show();
		  }else{
			  RainkSDK.getInstance().initNotice(mContext,new NoticeCallBack() {
				
				@Override
				public void callBack() {
					// TODO Auto-generated method stub
					NoticeWebViewDialog noticeWebViewDialog = new NoticeWebViewDialog(mContext);
					noticeWebViewDialog.show();
				}
			});
		  }*/
//		  RainkSDK.getInstance().showNoticeDialog(Tools.getMetadata(mContext, "igaworks_spacekey"));
	  }
	  
	  public void destroy()
	  {
	    hide();
	    removeFloatView();
	    removeTimerTask();
	    if (mTimer != null)
	    {
	      mTimer.cancel();
	      mTimer = null;
	    }
	    try
	    {
	      mTimerHandler.removeMessages(1);
	    }
	    catch (Exception localException) {}
	  }
}
