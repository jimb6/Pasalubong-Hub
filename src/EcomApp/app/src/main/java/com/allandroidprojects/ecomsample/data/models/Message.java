package com.allandroidprojects.ecomsample.data.models;

public class Message {
    private String text;
    private int position;
    private String timestamp;
    private Product product;
    private boolean belongsToCurrentUser;


    public Message(String text, String timestamp, boolean belongsToCurrentUser) {
        this.text = text;
        this.timestamp = timestamp;
        this.belongsToCurrentUser = belongsToCurrentUser;
    }

    public Message(String text, String timestamp, boolean belongsToCurrentUser, Product product) {
        this.text = text;
        this.timestamp = timestamp;
        this.belongsToCurrentUser = belongsToCurrentUser;
        this.product = product;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public Message(Product product) {
        this.product = product;
    }

    public String getText() {
        return text;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public boolean isBelongsToCurrentUser() {
        return belongsToCurrentUser;
    }

    public Product getProduct() {
        return product;
    }
}
