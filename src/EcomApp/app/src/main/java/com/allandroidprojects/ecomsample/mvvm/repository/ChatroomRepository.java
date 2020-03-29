package com.allandroidprojects.ecomsample.mvvm.repository;

import com.google.firebase.auth.FirebaseAuth;

public class ChatroomRepository {

    private static volatile ChatroomRepository instance;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    public static ChatroomRepository getInstance() {
        if (instance == null) {
            instance = new ChatroomRepository();
        }
        return instance;
    }

}
