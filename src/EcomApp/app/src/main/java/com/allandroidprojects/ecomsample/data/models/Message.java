package com.allandroidprojects.ecomsample.data.models;

import com.allandroidprojects.ecomsample.data.models.MessageProduct;

public class Message/*and this one is for custom content type (in this case - voice message)*/ {

    private String senderId;
    private String receiverId;
    private String message;
    private String createdAt;
    private MessageProduct product;
    private String businessId;
    private String userId;

    public Message(){}

    public Message(String senderId, String receiverId, String message, String createdAt){
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.message = message;
        this.createdAt = createdAt;
    }

    public Message(String senderId, String receiverId, String message, String createdAt, MessageProduct product, String businessId, String userId){
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.message = message;
        this.createdAt = createdAt;
        this.product = product;
        this.businessId = businessId;
        this.userId = userId;
    }

    public Message(String senderId, String receiverId, String message, String createdAt, String businessId, String userId){
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.message = message;
        this.createdAt = createdAt;
        this.businessId = businessId;
        this.userId = userId;
    }

    public Message(String senderId, String message, String createdAt){
        this.senderId = senderId;
        this.message = message;
        this.createdAt = createdAt;
    }

    public Message(String message, String createdAt) {
        this.message = message;
        this.createdAt = createdAt;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public MessageProduct getProduct() {
        return product;
    }

    public void setProduct(MessageProduct product) {
        this.product = product;
    }
}
