package com.sdk.interactive.aidl;

import android.os.Parcel;
import android.os.Parcelable;

public class PayInfo implements Parcelable{
	private String cpOrderId;
	private String productName;
	private String productId;
	private int amount;
	private int price;
	private String extData;

	public String getCpOrderId() {return cpOrderId;}
	public void setCpOrderId(String cpOrderId) {this.cpOrderId = cpOrderId;}

	public String getProductName() {return productName;}
	public void setProductName(String productName) {this.productName = productName;}

	public String getProductId() {return productId;}
	public void setProductId(String productId) {this.productId = productId;}

	public int getAmount() {return amount;}
	public void setAmount(Integer amount) {this.amount = amount;}

	public int getPrice() {return price;}
	public void setPrice(Integer price) {this.price = price;}

	public String getExtData() {return extData;}
	public void setExtData(String extData) {this.extData = extData;}

	public PayInfo() {}

	public PayInfo(Parcel source) {
		// TODO Auto-generated constructor stub
		cpOrderId=source.readString();
		productName=source.readString();
		productId=source.readString();
		amount=source.readInt();
		price=source.readInt();
		extData=source.readString();
	}

	public static final Creator<PayInfo> CREATOR = new Creator<PayInfo>() {

		@Override
		public PayInfo createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			return new PayInfo(source);
		}

		@Override
		public PayInfo[] newArray(int size) {
			// TODO Auto-generated method stub
			return new PayInfo[size];
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
		dest.writeString(cpOrderId);
		dest.writeString(productName);
		dest.writeString(productId);
		dest.writeInt(amount);
		dest.writeInt(price);
		dest.writeString(extData);
	}

}
