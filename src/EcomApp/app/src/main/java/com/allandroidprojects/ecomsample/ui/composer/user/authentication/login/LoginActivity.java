package com.allandroidprojects.ecomsample.ui.composer.user.authentication.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.allandroidprojects.ecomsample.R;
import com.allandroidprojects.ecomsample.ui.composer.user.authentication.registration.RegistrationActivity;
import com.allandroidprojects.ecomsample.ui.composer.user.startup.MainActivity;
import com.allandroidprojects.ecomsample.data.models.fcm.Chatroom;
import com.allandroidprojects.ecomsample.data.models.LoggedInUser;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private SignInButton googleSignInButton;
    private TextView register;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private View loadingProgressBar;
    private GoogleSignInClient googleSignInClient;
    private int RC_SIGN_IN = 0;
    public static boolean isActivityRunning = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initLoginViewModel();
        initComponents();
        initGoogleSignInClient();
        checkIfUserIsAuthenticated();

        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                loginButton.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getUsernameError() != null) {
                    usernameEditText.setError(getString(loginFormState.getUsernameError()));
                }
                if (loginFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(loginFormState.getPasswordError()));
                }
            }
        });
//
        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loginViewModel.login(usernameEditText.getText().toString(),
                            passwordEditText.getText().toString());
                }
                return false;
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                usernameEditText.setVisibility(View.GONE);
                passwordEditText.setVisibility(View.GONE);
                loginButton.setVisibility(View.GONE);
                googleSignInButton.setVisibility(View.GONE);
                loginViewModel.login(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
                loginViewModel.getLoginResult().observe(LoginActivity.this, user -> {
                    if (user.isAuthenticated) {
                        goToMainActivity(user);
                    } else {
                        showLoginFailed("Invalid Username or Password. Please Try again");
                    }
                    loadingProgressBar.setVisibility(View.GONE);
                    usernameEditText.setVisibility(View.VISIBLE);
                    passwordEditText.setVisibility(View.VISIBLE);
                    googleSignInButton.setVisibility(View.VISIBLE);
                    loginButton.setVisibility(View.VISIBLE);
                });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        isActivityRunning = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isActivityRunning = false;
    }

    private void ifEmailVerivied(LoggedInUser user){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        Intent intent = getIntent();
        String emailLink = intent.getData().toString();

// Confirm the link is a sign-in with email link.
        if (auth.isSignInWithEmailLink(emailLink)) {
            // Retrieve this from wherever you stored it
            String email = user.getEmail();
            // The client SDK will parse the code from the link for you.
            auth.signInWithEmailLink(email, emailLink)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                AuthResult result = task.getResult();
                                // You can access the new user via result.getUser()
                                // Additional user info profile *not* available via:
                                // result.getAdditionalUserInfo().getProfile() == null
                                // You can check if the user is new or existing:
                                // result.getAdditionalUserInfo().isNewUser()
                            } else {
                            }
                        }
                    });
        }

    }
    private void checkIfUserIsAuthenticated() {
        loginViewModel.checkIfUserIsAuthenticated();
        loginViewModel.isUserAuthenticatedLiveData.observe(this, user -> {
            if (user.isAuthenticated) {
                goToMainActivity(user);
            }
        });
    }

    private void getUserFromDatabase(String uid) {
        loginViewModel.setUid(uid);
        loginViewModel.userLiveData.observe(this, user -> {
            goToMainActivity(user);
            finish();
        });
    }

    private void initComponents() {
        register = findViewById(R.id.tvRegister);
        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.login);
        loadingProgressBar = findViewById(R.id.hubProgressBar);
        googleSignInButton = findViewById(R.id.google_sign_in_button);
        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                usernameEditText.setVisibility(View.GONE);
                passwordEditText.setVisibility(View.GONE);
                loginButton.setVisibility(View.GONE);
                googleSignInButton.setVisibility(View.GONE);
                LoginActivity.this.signIn();
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRegistrationForm();
            }
        });
    }

    private void initLoginViewModel(){
        loginViewModel = ViewModelProviders.of(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);
    }

    private void initGoogleSignInClient() {
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
    }

    private void signIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void getGoogleAuthCredential(GoogleSignInAccount googleSignInAccount) {
        String googleTokenId = googleSignInAccount.getIdToken();
        AuthCredential googleAuthCredential = GoogleAuthProvider.getCredential(googleTokenId, null);
        signInWithGoogleAuthCredential(googleAuthCredential);
    }

    private void signInWithGoogleAuthCredential(AuthCredential googleAuthCredential) {
        loginViewModel.signInWithGoogle(googleAuthCredential);
        loginViewModel.authenticatedUserLiveData.observe(this, new Observer<LoggedInUser>() {
            @Override
            public void onChanged(LoggedInUser authenticatedUser) {
                if (authenticatedUser.isNew) {
                    LoginActivity.this.createNewUser(authenticatedUser);
                } else if (authenticatedUser.isAuthenticated) {
                    LoginActivity.this.goToMainActivity(authenticatedUser);
                } else {
                    Toast.makeText(getApplicationContext(), authenticatedUser.userStatus, Toast.LENGTH_SHORT).show();
                }
                loadingProgressBar.setVisibility(View.GONE);
            }
        });
    }

    private void createNewUser(LoggedInUser authenticatedUser) {
        loginViewModel.createUser(authenticatedUser);
        loginViewModel.createdUserLiveData.observe(this, user -> {
            if (user.isCreated) {
                Toast.makeText(getApplicationContext(), user.getDisplayName(), Toast.LENGTH_SHORT).show();
                goToMainActivity(user);
            }
        });
    }

    private void goToMainActivity(LoggedInUser user) {

        Intent intent = getIntent();
        Intent toIntent = new Intent(LoginActivity.this, MainActivity.class);
        if (intent.hasExtra(getString(R.string.intent_chatroom))){
            Chatroom chatroom = intent.getParcelableExtra(getString(R.string.intent_chatroom));
            toIntent.putExtra(getString(R.string.intent_chatroom), chatroom);
        }

        toIntent.putExtra("USER", user);
        startActivity(toIntent);
        loadingProgressBar.setVisibility(View.GONE);
        finish();
    }

    private void showRegistrationForm(){
        startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
        finish();
    }

    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = getString(R.string.welcome) + model.getDisplayName();
        // TODO : initiate successful logged in experience
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    private void showLoginFailed(String errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount googleSignInAccount = task.getResult(ApiException.class);
                if (googleSignInAccount != null) {
                    getGoogleAuthCredential(googleSignInAccount);
                }
            } catch (ApiException e) {
                System.err.println(e.getMessage());
            }
        }
    }
}
