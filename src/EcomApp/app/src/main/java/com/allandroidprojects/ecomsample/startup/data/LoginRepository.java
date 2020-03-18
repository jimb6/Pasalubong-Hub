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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
public class LoginRepository {

    private static volatile LoginRepository instance;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private LoginDataSource dataSource;

    private FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
    private CollectionReference usersRef = rootRef.collection("USERS");

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

    public MutableLiveData<LoggedInUser> createUserInFirestoreIfNotExists(final LoggedInUser authenticatedUser) {
        final MutableLiveData<LoggedInUser> newUserMutableLiveData = new MutableLiveData<>();
        final DocumentReference uidRef = usersRef.document(authenticatedUser.getUserId());
        uidRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> uidTask) {
                if (uidTask.isSuccessful()) {
                    DocumentSnapshot document = uidTask.getResult();
                    if (!document.exists()) {
                        uidRef.set(authenticatedUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> userCreationTask) {
                                if (userCreationTask.isSuccessful()) {
                                    authenticatedUser.isCreated = true;
                                    newUserMutableLiveData.setValue(authenticatedUser);
                                } else {
                                    System.err.println(userCreationTask.getException().getMessage());
                                }
                            }
                        });
                    } else {
                        newUserMutableLiveData.setValue(authenticatedUser);
                    }
                } else {
                    System.err.println(uidTask.getException().getMessage());
                }
            }
        });
        return newUserMutableLiveData;
    }

    // If user credentials will be cached in local storage, it is recommended it be encrypted
    // @see https://developer.android.com/training/articles/keystore
    private LoggedInUser user = null;

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
                            String photoUrl = "";
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
            String photoUrl = "";
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
