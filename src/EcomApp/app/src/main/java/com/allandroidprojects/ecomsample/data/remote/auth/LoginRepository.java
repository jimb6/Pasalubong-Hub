package com.allandroidprojects.ecomsample.data.remote.auth;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.allandroidprojects.ecomsample.data.models.LoggedInUser;
import com.allandroidprojects.ecomsample.data.models.LoginDataSource;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
public class LoginRepository {

    private static volatile LoginRepository instance;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase databaseRef = FirebaseDatabase.getInstance();
    private LoginDataSource dataSource;

    private FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
    private CollectionReference usersRef = rootRef.collection("users");

    // private constructor : singleton access
    private LoginRepository(LoginDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public static LoginRepository getInstance(LoginDataSource dataSource) {
        if (instance == null) {
            instance = new LoginRepository(dataSource);
        }
        return instance;
    }

    public boolean isLoggedIn() {
        return user != null;
    }



    public MutableLiveData<LoggedInUser> firebaseSignInWithGoogle(AuthCredential googleAuthCredential) {
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
                        Uri photoUrl = firebaseUser.getPhotoUrl();
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


    /*
     *   Create user if user is not exists in firebase firestore...
     */
    public MutableLiveData<LoggedInUser> createUserInFirestoreIfNotExists(final LoggedInUser authenticatedUser) {
        final MutableLiveData<LoggedInUser> newUserMutableLiveData = new MutableLiveData<>();
        final DocumentReference uidRef = usersRef.document(authenticatedUser.getUserId());

        Map<String, Object> authData = new HashMap<>();
        authData.put("displayName", authenticatedUser.getDisplayName());
        authData.put("email", authenticatedUser.getEmail());
        authData.put("isCreated", authenticatedUser.isAuthenticated);
        authData.put("isNew", authenticatedUser.isNew);
        authData.put("photoUrl", String.valueOf(authenticatedUser.getPhotoUrl()));
        authData.put("userId", authenticatedUser.getUserId());
        authData.put("security_level", "10");
        authData.put("userStatus", authenticatedUser.userStatus);

        uidRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> uidTask) {
                if (uidTask.isSuccessful()) {
                    DocumentSnapshot document = uidTask.getResult();
                    if (document != null) {
                        if (!document.exists()) {
                            uidRef.set(authData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> userCreationTask) {
                                    if (userCreationTask.isSuccessful()) {
                                        authenticatedUser.isCreated = true;
                                        createuserInFirebaseIfNotExists(authenticatedUser);
                                        newUserMutableLiveData.setValue(authenticatedUser);
                                    } else {
                                        System.err.println(userCreationTask.getException().getMessage());
                                    }
                                }

                            });
                        } else {
                            newUserMutableLiveData.setValue(authenticatedUser);
                        }
                    }
                } else {
                    System.err.println(uidTask.getException().getMessage());
                }
            }

        });
        return newUserMutableLiveData;
    }

    public void createuserInFirebaseIfNotExists(final LoggedInUser authenticatedUser){
        final MutableLiveData<LoggedInUser> newUserMutableLiveData = new MutableLiveData<>();
        final DatabaseReference mDatabase = databaseRef.getReference();
        mDatabase.child("users").child(authenticatedUser.getUserId()).setValue(user);
    }

    // If user credentials will be cached in local storage, it is recommended it be encrypted
    // @see https://developer.android.com/training/articles/keystore
    private LoggedInUser user = null;



    public void logout() {
        user = null;
        dataSource.logout();
    }

    private void setLoggedInUser(LoggedInUser user) {
        this.user = user;
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
    }

    public MutableLiveData<LoggedInUser> loginResult = new MutableLiveData<>();

    public MutableLiveData<LoggedInUser> login(String username, String password) {
        final MutableLiveData<LoggedInUser> authenticatedUserMutableData = new MutableLiveData<>();

        // handle login
        firebaseAuth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            boolean isNewUser = task.getResult().getAdditionalUserInfo().isNewUser();
                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                            // Sign in success, update UI with the signed-in user's information
                            String uid = firebaseUser.getUid();
                            String name = firebaseUser.getDisplayName();
                            String email = firebaseUser.getEmail();
                            Uri photoUrl = firebaseUser.getPhotoUrl();
                            LoggedInUser user = new LoggedInUser(uid, name, email, photoUrl);
                            user.isAuthenticated = true;
                            authenticatedUserMutableData.setValue(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            user = new LoggedInUser();
                            user.isAuthenticated = false;
                            user.userStatus = task.getException().getMessage();
                            authenticatedUserMutableData.setValue(user);
                        }
                    }
                });
        return authenticatedUserMutableData;
    }

    public MutableLiveData<LoggedInUser> checkIfUserIsAuthenticatedInFirebase() {
        MutableLiveData<LoggedInUser> isUserAuthenticateInFirebaseMutableLiveData = new MutableLiveData<>();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            user = new LoggedInUser();
            user.isAuthenticated = false;
            isUserAuthenticateInFirebaseMutableLiveData.setValue(user);
        } else {
            String uid = firebaseUser.getUid();
            String name = firebaseUser.getDisplayName();
            String email = firebaseUser.getEmail();
            Uri photoUrl = firebaseUser.getPhotoUrl();
            user = new LoggedInUser(uid, name, email, photoUrl);
            user.isAuthenticated = true;
            isUserAuthenticateInFirebaseMutableLiveData.setValue(user);
        }
        return isUserAuthenticateInFirebaseMutableLiveData;
    }

    public MutableLiveData<LoggedInUser> addUserToLiveData(String uid) {
        MutableLiveData<LoggedInUser> userMutableLiveData = new MutableLiveData<>();
        usersRef.document(uid).get().addOnCompleteListener(userTask -> {
            if (userTask.isSuccessful()) {
                DocumentSnapshot document = userTask.getResult();
                if(document.exists()) {
                    LoggedInUser user = document.toObject(LoggedInUser.class);
                    userMutableLiveData.setValue(user);
                }
            } else {
                System.err.println(userTask.getException().getMessage());
            }
        });
        return userMutableLiveData;
    }
}
