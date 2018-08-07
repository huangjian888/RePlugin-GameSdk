package com.haowan123.funcell.sdk.apiinterface;

import android.os.Parcel;
import android.os.Parcelable;

public class FunPayInfo implements Parcelable{
	private String cpOrderId;
	private String productName;
	private String productId;
	private Integer amount;
	private Integer price;
	private String extData;
	
	public String getCpOrderId() {
		return cpOrderId;
	}

	public void setCpOrderId(String cpOrderId) {
		this.cpOrderId = cpOrderId;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public Integer getAmount() {
		return amount;
	}

	public void setAmount(Integer amount) {
		if(null==amount||0==amount){
			this.amount=1;
		}else {
			this.amount = amount;
		}
	}

	public Integer getPrice() {
		return price;
	}

	public void setPrice(Integer price) {
		if(null==price){
			this.price=0;
		}else{
			this.price = price;
		}
	}

	public String getExtData() {
		return extData;
	}

	public void setExtData(String extData) {
		this.extData = extData;
	}

	public static Parcelable.Creator<FunPayInfo> getCreator() {
		return CREATOR;
	}

	public FunPayInfo(Parcel p) {
		cpOrderId=p.readString();
		productName=p.readString();
		productId=p.readString();
		amount=p.readInt();
		price=p.readInt();
		extData=p.readString();
	}
	
	public FunPayInfo() {
		
	}
	
	public static final Parcelable.Creator<FunPayInfo> CREATOR = new Parcelable.Creator<FunPayInfo>() {  
        public FunPayInfo createFromParcel(Parcel p) {  
            return new FunPayInfo(p);  
        }  
  
        public FunPayInfo[] newArray(int size) {  
            return new FunPayInfo[size];  
        }  
    };  

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel p, int flags) {
		p.writeString(cpOrderId);
		p.writeString(productName);
		p.writeString(productId);
		p.writeInt(amount);
		p.writeInt(price);
		p.writeString(extData);
	}

	
}
