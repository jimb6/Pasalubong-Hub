package com.allandroidprojects.ecomsample.model;

import java.io.Serializable;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class LoggedInUser implements Serializable {

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
}
