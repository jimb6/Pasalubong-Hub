package com.allandroidprojects.ecomsample.util;

public enum ProductOrderStatus {
    PENDING("Pending"), ACCEPTED("Confirmed"), CANCELLED("Cancelled"),
    INVALID("Invalid"), TO_PICKUP("To Pickup"), TO_RATE("To Rate"),
    TO_PAY("To Pay"), TO_RECEIVE("To Receive"), TO_SHIP("To Ship"),
    HISTORY("History");

    private String description;

    ProductOrderStatus(String description){
        this.description = description;
    }

    public String get(){
        return this.description;
    }
}