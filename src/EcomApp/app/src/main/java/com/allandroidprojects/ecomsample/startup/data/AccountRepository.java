package com.allandroidprojects.ecomsample.startup.data;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.allandroidprojects.ecomsample.startup.data.model.LoggedInUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AccountRepository {

    private static volatile AccountRepository instance;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    public static AccountRepository getInstance() {
        if (instance == null) {
            instance = new AccountRepository();
        }
        return instance;
    }

    public MutableLiveData<LoggedInUser> accountValidation(AuthCredential googleAuthCredential) {
        final MutableLiveData<LoggedInUser> authenticatedUserMutableLiveData = new MutableLiveData<>();
        firebaseAuth.signInWithCredential(googleAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> authTask) {
                if (authTask.isSuccessful()) {
                    boolean isNewUser = authTask.getResult().getAdditionalUserInfo().isNewUser();
                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                    if (firebaseUser != null) {
                        String uid = firebaseUser.getUid();
                        String name = firebaseUser.getDisplayName();
                        String email = firebaseUser.getEmail();
                        String photoUrl = firebaseUser.getPhotoUrl().getPath();
                        LoggedInUser user = new LoggedInUser(uid, name, email, photoUrl);
                        user.isAuthenticated = true;
                        user.isNew = isNewUser;
                        authenticatedUserMutableLiveData.setValue(user);
                    }
                } else {
                    String error = authTask.getException().getMessage();
                    LoggedInUser user = new LoggedInUser();
                    user.isAuthenticated = false;
                    user.userStatus = error;
                    authenticatedUserMutableLiveData.setValue(user);
                }
            }
        });
        return authenticatedUserMutableLiveData;
    }
}
