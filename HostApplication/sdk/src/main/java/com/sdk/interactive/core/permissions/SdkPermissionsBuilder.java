package com.sdk.interactive.core.permissions;

import android.content.Context;

public class SdkPermissionsBuilder {
	private Context mContext;
    private SdkPermissionsCallbacks mFuncellPermissionsCallbacks;
    private String mRationale4ReqPer;
    private String mRationale4NeverAskAgain;
    private int mPositiveBtn4ReqPer = -1;
    private int mNegativeBtn4ReqPer = -1;
    private int mPositiveBtn4NeverAskAgain = -1;
    private int mNegativeBtn4NeverAskAgain = -1;
    private int mRequestCode = -1;

    /*
     * Basics RequestCode
     * use lower 16 bits for requestCode (0-65535)
     */
    private int mAnalyticsBasicsRequestCode = 0xea60; //60000
    private int mCrashBasicsRequestCode = 0xeac4; //60100
    private int mHelpShiftBasicsRequestCode = 0xeb28;//60200
    private int mPushBasicsRequestCode = 0xeb8c;//60300
    private int mShareBasicsRequestCode = 0xebf0;//60400
    private int mVoiceBasicsRequestCode = 0xec54;//60500
    
    
	public int getmAnalyticsBasicsRequestCode() {return mAnalyticsBasicsRequestCode;}
	public SdkPermissionsBuilder setmAnalyticsBasicsRequestCode(int mAnalyticsBasicsRequestCode) {this.mAnalyticsBasicsRequestCode = mAnalyticsBasicsRequestCode; return this;}

	public int getmCrashBasicsRequestCode() {return mCrashBasicsRequestCode;}
	public SdkPermissionsBuilder setmCrashBasicsRequestCode(int mCrashBasicsRequestCode) {this.mCrashBasicsRequestCode = mCrashBasicsRequestCode; return this;}

	public int getmHelpShiftBasicsRequestCode() {return mHelpShiftBasicsRequestCode;}
	public SdkPermissionsBuilder setmHelpShiftBasicsRequestCode(int mHelpShiftBasicsRequestCode) {this.mHelpShiftBasicsRequestCode = mHelpShiftBasicsRequestCode; return this;}

	public int getmPushBasicsRequestCode() {return mPushBasicsRequestCode;}
	public SdkPermissionsBuilder setmPushBasicsRequestCode(int mPushBasicsRequestCode) {this.mPushBasicsRequestCode = mPushBasicsRequestCode; return this;}

	public int getmShareBasicsRequestCode() {return mShareBasicsRequestCode;}
	public SdkPermissionsBuilder setmShareBasicsRequestCode(int mShareBasicsRequestCode) {this.mShareBasicsRequestCode = mShareBasicsRequestCode; return this;}
	
	public int getmVoiceBasicsRequestCode() {return mVoiceBasicsRequestCode;}
	public SdkPermissionsBuilder setmVoiceBasicsRequestCode(int mVoiceBasicsRequestCode) {this.mVoiceBasicsRequestCode = mVoiceBasicsRequestCode; return this;}

    public SdkPermissionsBuilder(Context context) {
        this.mContext = context;
    }

    public SdkPermissionsBuilder onFuncellPermissionsCallbacks(SdkPermissionsCallbacks funcellPermissionsCallbacks){
    	this.mFuncellPermissionsCallbacks = funcellPermissionsCallbacks;
    	return this;
    }
    
    public SdkPermissionsBuilder rationale4ReqPer(String rationale4ReqPer) {
        this.mRationale4ReqPer = rationale4ReqPer;
        return this;
    }

    public SdkPermissionsBuilder positiveBtn4ReqPer(int positiveBtn4ReqPer) {
        this.mPositiveBtn4ReqPer = positiveBtn4ReqPer;
        return this;
    }

    public SdkPermissionsBuilder positiveBtn4NeverAskAgain(int positiveBtn4NeverAskAgain) {
        this.mPositiveBtn4NeverAskAgain = positiveBtn4NeverAskAgain;
        return this;
    }

    public SdkPermissionsBuilder negativeBtn4ReqPer(int negativeBtn4ReqPer) {
        this.mNegativeBtn4ReqPer = negativeBtn4ReqPer;
        return this;
    }

    public SdkPermissionsBuilder negativeBtn4NeverAskAgain(int negativeBtn4NeverAskAgain) {
        this.mNegativeBtn4NeverAskAgain = negativeBtn4NeverAskAgain;
        return this;
    }

    public SdkPermissionsBuilder rationale4NeverAskAgain(String rationale4NeverAskAgain) {
        this.mRationale4NeverAskAgain = rationale4NeverAskAgain;
        return this;
    }

    public SdkPermissionsBuilder requestCode(int requestCode) {
        this.mRequestCode = requestCode;
        return this;
    }

    public SdkPermissionsManager build() {
        return new SdkPermissionsManager(
                mContext,
                mFuncellPermissionsCallbacks,
                mRationale4ReqPer,
                mRationale4NeverAskAgain,
                mPositiveBtn4ReqPer,
                mNegativeBtn4ReqPer,
                mPositiveBtn4NeverAskAgain,
                mNegativeBtn4NeverAskAgain,
                mRequestCode
        );
    }
}