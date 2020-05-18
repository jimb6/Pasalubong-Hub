package com.allandroidprojects.ecomsample.data.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Rating implements Parcelable {

    private String id;
    private String authorId;
    private double rating;
    private String comment;
    private String userImage;
    private ArrayList<String> urls;
    private String authornName;
    private String date;


    public Rating() {

    }

    protected Rating(Parcel in) {
        id = in.readString();
        authorId = in.readString();
        rating = in.readDouble();
        comment = in.readString();
        userImage = in.readString();
        urls = in.createStringArrayList();
        authornName = in.readString();
        date = in.readString();
    }

    public static final Creator<Rating> CREATOR = new Creator<Rating>() {
        @Override
        public Rating createFromParcel(Parcel in) {
            return new Rating(in);
        }

        @Override
        public Rating[] newArray(int size) {
            return new Rating[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthor() {
        return authorId;
    }

    public void setAuthor(String author) {
        this.authorId = author;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rateing) {
        this.rating = rateing;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public ArrayList<String> getUrls() {
        return urls;
    }

    public void setUrls(ArrayList<String> urls) {
        this.urls = urls;
    }

    public String getAuthornName() {
        return authornName;
    }

    public void setAuthornName(String authornName) {
        this.authornName = authornName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(authorId);
        dest.writeDouble(rating);
        dest.writeString(comment);
        dest.writeString(userImage);
        dest.writeStringList(urls);
        dest.writeString(authornName);
        dest.writeString(date);
    }
}
