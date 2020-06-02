package com.allandroidprojects.ecomsample.util;

public enum ProductOrderStatus {
    PENDING("Pending"), ACCEPTED("Confirmed"), CANCELLED("Cancelled"),
    HISTORY("History"), DELETE("Deleted"), TO_REVIEW("Review");

    private String description;

    ProductOrderStatus(String description){
        this.description = description;
    }

    public String get(){
        return this.description;
    }

}