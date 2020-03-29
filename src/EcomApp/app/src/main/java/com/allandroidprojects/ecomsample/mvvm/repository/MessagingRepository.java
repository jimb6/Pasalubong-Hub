package com.allandroidprojects.ecomsample.mvvm.repository;

public class MessagingRepository {

    public static volatile MessagingRepository instance;
    public static MessagingRepository getInstance(){
        if (instance == null)
            return new MessagingRepository();
        return instance;
    }
}
