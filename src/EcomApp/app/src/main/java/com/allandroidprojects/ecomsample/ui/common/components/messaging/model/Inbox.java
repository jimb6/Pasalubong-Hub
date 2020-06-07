package com.allandroidprojects.ecomsample.ui.common.components.messaging.model;

import java.util.List;
import java.util.Map;

public class Inbox {

    private String chatroomId;
    private String creatorId;
    private String createdAt;
    private String updatedAt;
    private String chatroomname;
    private String inboxImage;
    private String lastmessage;
    private Map<String, Object> tokens;
    private Map<String, Object> users;
    private Map<String, Object> messages;

    public String getChatroomId() {
        return chatroomId;
    }

    public void setChatroomId(String chatroomId) {
        this.chatroomId = chatroomId;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Map<String, Object> getTokens() {
        return tokens;
    }

    public void setTokens(Map<String, Object>  tokens) {
        this.tokens = tokens;
    }

    public Map<String, Object> getUsers() {
        return users;
    }

    public void setUsers(Map<String, Object> users) {
        this.users = users;
    }

    public Map<String, Object> getMessages() {
        return messages;
    }

    public void setMessages(Map<String, Object> messages) {
        this.messages = messages;
    }

    public String getChatroomname() {
        return chatroomname;
    }

    public void setChatroomname(String chatroomname) {
        this.chatroomname = chatroomname;
    }

    public String getInboxImage() {
        return inboxImage;
    }

    public void setInboxImage(String inboxImage) {
        this.inboxImage = inboxImage;
    }

    public String getLastmessage() {
        return lastmessage;
    }

    public void setLastmessage(String lastmessage) {
        this.lastmessage = lastmessage;
    }
}
