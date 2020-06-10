package com.allandroidprojects.ecomsample.data.models;



public class MessageUser {

    private String id;
    private String name;
    private String avatar;
    private boolean online;
    private String lastMessageRead;
    private int lastMessageSeen;

    public MessageUser(String id, String name, String avatar, boolean online) {
        this.id = id;
        this.name = name;
        this.avatar = avatar;
        this.online = online;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAvatar() {
        return avatar;
    }

    public boolean isOnline() {
        return online;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public String getLastMessageRead() {
        return lastMessageRead;
    }

    public void setLastMessageRead(String lastMessageRead) {
        this.lastMessageRead = lastMessageRead;
    }

    public int getLastMessageSeen() {
        return lastMessageSeen;
    }

    public void setLastMessageSeen(int lastMessageSeen) {
        this.lastMessageSeen = lastMessageSeen;
    }
}