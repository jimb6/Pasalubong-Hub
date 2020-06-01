package com.allandroidprojects.ecomsample.data.models;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class LoggedInUser implements Parcelable {

    private String userId;
    private String displayName;
    private String email;
    private Uri photoUrl;
    public boolean isNew;
    public boolean isCreated;
    public boolean isAuthenticated;
    public String security_level;
    public String userStatus;


    public LoggedInUser(){}
    public LoggedInUser(LoggedInUser user){
        this.userId = user.getUserId();
        this.displayName = user.getDisplayName();
        this.email = user.getEmail();
        this.photoUrl = user.getPhotoUrl();
    }

    public LoggedInUser(String userId, String displayName, String email, Uri photoUrl) {
        this.userId = userId;
        this.displayName = displayName;
        this.email = email;
        this.photoUrl = photoUrl;
        this.security_level = "10";
    }


    protected LoggedInUser(Parcel in) {
        userId = in.readString();
        displayName = in.readString();
        email = in.readString();
        photoUrl = in.readParcelable(Uri.class.getClassLoader());
        isNew = in.readByte() != 0;
        isCreated = in.readByte() != 0;
        isAuthenticated = in.readByte() != 0;
        userStatus = in.readString();
        security_level = in.readString();
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

    public Uri getPhotoUrl() {
        return photoUrl;
    }

    public String getUserId() {
        return userId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getSecurity_level() {
        return security_level;
    }

    public void setSecurity_level(String security_level) {
        this.security_level = security_level;
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
        dest.writeParcelable(photoUrl, flags);
        dest.writeByte((byte) (isNew ? 1 : 0));
        dest.writeByte((byte) (isCreated ? 1 : 0));
        dest.writeByte((byte) (isAuthenticated ? 1 : 0));
        dest.writeString(userStatus);
        dest.writeString(security_level);
    }

}
