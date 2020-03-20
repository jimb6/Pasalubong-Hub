package com.allandroidprojects.ecomsample.model;

public class Business extends LoggedInUser{

    private String businessName;
    private Address businessAddress;

    public Business(LoggedInUser user){
        super(user);
    }

    public Business(LoggedInUser user, String businessName, Address businessAddress){
        super(user);
        this.businessAddress = businessAddress;
        this.businessName = businessName;
    }

    public String getBusinessName() {
        return businessName;
    }

    public Address getBusinessAddress() {
        return businessAddress;
    }

}
