package com.allandroidprojects.ecomsample.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Business extends LoggedInUser implements Parcelable {

    private String businessName;
    private String businessAddress;
    private ArrayList<String> businessPhotos;

    public Business(){}

    public Business(LoggedInUser user){
        super(user);
    }

    public Business(LoggedInUser user, String businessName, String businessAddress){
        super(user);
        this.businessAddress = businessAddress;
        this.businessName = businessName;
    }

    protected Business(Parcel in) {
        businessName = in.readString();
        businessAddress = in.readString();
        businessPhotos = in.createStringArrayList();
    }

    public static final Creator<Business> CREATOR = new Creator<Business>() {
        @Override
        public Business createFromParcel(Parcel in) {
            return new Business(in);
        }

        @Override
        public Business[] newArray(int size) {
            return new Business[size];
        }
    };

    public String getBusinessName() {
        return businessName;
    }

    public String getBusinessAddress() {
        return businessAddress;
    }

    public ArrayList<String> getBusinessPhotos() {
        return businessPhotos;
    }

    public void setBusinessPhotos(ArrayList<String> businessPhotos) {
        this.businessPhotos = businessPhotos;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(businessName);
        dest.writeString(businessAddress);
        dest.writeStringList(businessPhotos);
    }
}
