package com.allandroidprojects.ecomsample.data.models;

public class MessageProduct {

    private String id;
    private String name;
    private String description;
    private String price;
    private String image;


    public MessageProduct(String id, String name, String description, String price, String image) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getPrice() {
        return price;
    }

    public String getImage() {
        return image;
    }
}
