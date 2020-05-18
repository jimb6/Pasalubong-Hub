package com.allandroidprojects.ecomsample.data.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Business implements Parcelable {

    private String businessName;
    private String businessAddress;
    private String ownerId;
    private String businessEmail;
    private ArrayList<String> businessPhotos;
    private String coverUri;
    private String lat;
    private String lng;

    public Business() {
    }

    public Business(String businessName,
                    String businessAddress,
                    String ownerId,
                    String businessEmail,
                    ArrayList<String> businessPhotos,
                    String coverImage) {
        this.businessAddress = businessAddress;
        this.businessName = businessName;
        this.ownerId = ownerId;
        this.businessEmail = businessEmail;
        this.businessPhotos = businessPhotos;
        this.coverUri = coverImage;
    }

    protected Business(Parcel in) {
        businessName = in.readString();
        businessAddress = in.readString();
        ownerId = in.readString();
        businessEmail = in.readString();
        businessPhotos = in.createStringArrayList();
        lat = in.readString();
        lng = in.readString();
        coverUri = in.readString();
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

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getBusinessAddress() {
        return businessAddress;
    }

    public void setBusinessAddress(String businessAddress) {
        this.businessAddress = businessAddress;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getBusinessEmail() {
        return businessEmail;
    }

    public void setBusinessEmail(String businessEmail) {
        this.businessEmail = businessEmail;
    }

    public ArrayList<String> getBusinessPhotos() {
        return businessPhotos;
    }

    public void setBusinessPhotos(ArrayList<String> businessPhotos) {
        this.businessPhotos = businessPhotos;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getCoverUri() {
        return coverUri;
    }

    public void setCoverUri(String coverUri) {
        this.coverUri = coverUri;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(businessName);
        dest.writeString(businessAddress);
        dest.writeString(ownerId);
        dest.writeString(businessEmail);
        dest.writeStringList(businessPhotos);
        dest.writeString(lat);
        dest.writeString(lng);
        dest.writeString(coverUri);
    }
}
