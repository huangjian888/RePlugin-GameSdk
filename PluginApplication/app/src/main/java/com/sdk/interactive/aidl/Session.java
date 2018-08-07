package com.sdk.interactive.aidl;

import android.os.Parcel;
import android.os.Parcelable;

public class Session implements Parcelable{
	private String mChannelUserId;
	private String mChannelUserName;
	private String mChannelToken;
	private String mChannelJson;
	
	public String getmChannelUserId() {return mChannelUserId;}
	public void setmChannelUserId(String mChannelUserId) {this.mChannelUserId = mChannelUserId;}

	public String getmChannelUserName() {return mChannelUserName;}
	public void setmChannelUserName(String mChannelUserName) {this.mChannelUserName = mChannelUserName;}

	public String getmChannelToken() {return mChannelToken;}
	public void setmChannelToken(String mChannelToken) {this.mChannelToken = mChannelToken;}

	public String getmChannelJson() {return mChannelJson;}
	public void setmChannelJson(String mChannelJson) {this.mChannelJson = mChannelJson;}
	
	public Session() {}
	
	public Session(Parcel source) {
		// TODO Auto-generated constructor stub
		mChannelUserId = source.readString();
		mChannelUserName = source.readString();
		mChannelToken = source.readString();
		mChannelJson = source.readString();
	}
	
	public static final Parcelable.Creator<Session> CREATOR = new Parcelable.Creator<Session>() {

		@Override
		public Session createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			return new Session(source);
		}

		@Override
		public Session[] newArray(int size) {
			// TODO Auto-generated method stub
			return new Session[size];
		}

	};

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeString(mChannelUserId);
		dest.writeString(mChannelUserName);
		dest.writeString(mChannelToken);
		dest.writeString(mChannelJson);
	}

}
