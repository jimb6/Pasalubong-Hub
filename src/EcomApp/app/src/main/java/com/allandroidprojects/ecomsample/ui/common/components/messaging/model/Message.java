package com.allandroidprojects.ecomsample.ui.common.components.messaging.model;

import com.allandroidprojects.ecomsample.data.models.Product;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class Message/*and this one is for custom content type (in this case - voice message)*/ {

    private String id;
    private String text;
    private Date createdAt;

    public Message(){}

    public Message(String id, String text, Date createdAt){
        this.id = id;
        this.text = text;
        this.createdAt = createdAt;
    }

    public Message(String text, Date createdAt) {
        this.text = text;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
