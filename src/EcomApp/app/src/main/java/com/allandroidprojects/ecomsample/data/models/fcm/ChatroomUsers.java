package com.allandroidprojects.ecomsample.data.models.fcm;

public class ChatroomUsers{
    String id;
    String userProfileImage;
    String lastUnseenMessage;
    String lastSeenMessageNumber;

    public ChatroomUsers(String id, String userProfileImage, String lastUnseenMessage, String lastSeenMessageNumber) {
        this.id = id;
        this.userProfileImage = userProfileImage;
        this.lastSeenMessageNumber = lastSeenMessageNumber;
        this.lastUnseenMessage = lastUnseenMessage;
    }

    public String getId() {
        return id;
    }

    public String getUserProfileImage() {
        return userProfileImage;
    }

    public String getLastUnseenMessage() {
        return lastUnseenMessage;
    }

    public String getLastSeenMessageNumber() {
        return lastSeenMessageNumber;
    }
}
