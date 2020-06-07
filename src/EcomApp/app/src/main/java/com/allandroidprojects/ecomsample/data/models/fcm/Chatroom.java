package com.allandroidprojects.ecomsample.data.models.fcm;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Map;


public class Chatroom implements Parcelable{

    private String chatroom_name;
    private String creator_id;
    private String security_level;
    private String chatroom_id;
    private String businessId;
    private Map<String, ChatroomUsers> users;


    public Chatroom(Parcel in) {
        chatroom_name = in.readString();
        creator_id = in.readString();
        security_level = in.readString();
        chatroom_id = in.readString();
        businessId = in.readString();
    }

    public static final Creator<Chatroom> CREATOR = new Creator<Chatroom>() {
        @Override
        public Chatroom createFromParcel(Parcel in) {
            return new Chatroom(in);
        }

        @Override
        public Chatroom[] newArray(int size) {
            return new Chatroom[size];
        }
    };

    public Chatroom() {

    }

    public String getChatroom_name() {
        return chatroom_name;
    }

    public void setChatroom_name(String chatroom_name) {
        this.chatroom_name = chatroom_name;
    }

    public String getCreator_id() {
        return creator_id;
    }

    public void setCreator_id(String creator_id) {
        this.creator_id = creator_id;
    }

    public String getSecurity_level() {
        return security_level;
    }

    public void setSecurity_level(String security_level) {
        this.security_level = security_level;
    }

    public String getChatroom_id() {
        return chatroom_id;
    }

    public void setChatroom_id(String chatroom_id) {
        this.chatroom_id = chatroom_id;
    }

    public Map<String, ChatroomUsers> getUsers() {
        return users;
    }

    public void setUsers(Map<String, ChatroomUsers> users) {
        this.users = users;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(chatroom_name);
        dest.writeString(creator_id);
        dest.writeString(security_level);
        dest.writeString(chatroom_id);
        dest.writeString(businessId);
    }

}
