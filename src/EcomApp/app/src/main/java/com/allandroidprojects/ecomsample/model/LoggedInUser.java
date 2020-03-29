package com.allandroidprojects.ecomsample.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class LoggedInUser implements Parcelable {

    private String userId;
    private String displayName;
    private String email;
    private String photoUrl;
    public boolean isNew;
    public boolean isCreated;
    public boolean isAuthenticated;
    public String userStatus;

    public LoggedInUser(){}
    public LoggedInUser(LoggedInUser user){
        this.userId = user.getUserId();
        this.displayName = user.getDisplayName();
        this.email = user.getEmail();
        this.photoUrl = user.getPhotoUrl();
    }
    public LoggedInUser(String userId, String displayName, String email, String photoUrl) {
        this.userId = userId;
        this.displayName = displayName;
        this.email = email;
        this.photoUrl = photoUrl;
    }

    protected LoggedInUser(Parcel in) {
        userId = in.readString();
        displayName = in.readString();
        email = in.readString();
        photoUrl = in.readString();
        isNew = in.readByte() != 0;
        isCreated = in.readByte() != 0;
        isAuthenticated = in.readByte() != 0;
        userStatus = in.readString();
    }

    public static final Creator<LoggedInUser> CREATOR = new Creator<LoggedInUser>() {
        @Override
        public LoggedInUser createFromParcel(Parcel in) {
            return new LoggedInUser(in);
        }

        @Override
        public LoggedInUser[] newArray(int size) {
            return new LoggedInUser[size];
        }
    };

    public String getEmail() {
        return email;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public String getUserId() {
        return userId;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userId);
        dest.writeString(displayName);
        dest.writeString(email);
        dest.writeString(photoUrl);
        dest.writeByte((byte) (isNew ? 1 : 0));
        dest.writeByte((byte) (isCreated ? 1 : 0));
        dest.writeByte((byte) (isAuthenticated ? 1 : 0));
        dest.writeString(userStatus);
    }
}
