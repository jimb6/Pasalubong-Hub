package com.allandroidprojects.ecomsample.startup.data;

import androidx.lifecycle.MutableLiveData;

import com.allandroidprojects.ecomsample.startup.data.model.LoggedInUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegistrationRepository {

    private static volatile RegistrationRepository instance;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    private FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
    private CollectionReference usersRef = rootRef.collection("USERS");

    private RegistrationRepository() {

    }

    public static RegistrationRepository getInstance() {
        if (instance == null) {
            instance = new RegistrationRepository();
        }
        return instance;
    }

    public MutableLiveData<LoggedInUser> firebaseRegistrationWithEmailAndPassword(String email, String password)  {
        final MutableLiveData<LoggedInUser> userRegistrationMutableLiveData = new MutableLiveData<>();
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                        LoggedInUser loggedInUser = new LoggedInUser();
                        loggedInUser.isAuthenticated = true;
                        String uid = firebaseUser.getUid();
                        String name = firebaseUser.getDisplayName();
                        String useremail = firebaseUser.getEmail();
                        String photoUrl = "";
                        LoggedInUser user = new LoggedInUser(uid, name, useremail, photoUrl);
                        user.isAuthenticated = true;
                        user.isNew = true;
                        userRegistrationMutableLiveData.setValue(loggedInUser);
                    } else {
                        // If sign in fails, display a message to the user.
                        LoggedInUser user = new LoggedInUser();
                        user.userStatus = task.getException().getMessage();
                        user.isAuthenticated = false;
                        userRegistrationMutableLiveData.setValue(user);
                    }

                    // ...
                });
        return userRegistrationMutableLiveData;
    }
}
