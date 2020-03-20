package com.allandroidprojects.ecomsample.startup.ui.registration;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.allandroidprojects.ecomsample.R;
import com.allandroidprojects.ecomsample.startup.MainActivity;
import com.allandroidprojects.ecomsample.model.LoggedInUser;
import com.allandroidprojects.ecomsample.startup.ui.login.LoginActivity;

public class RegistrationActivity extends AppCompatActivity {

    private EditText usernameEditText, passwordEditText, confirmPasswordEditText;
    private Button btnSignup;
    private TextView linkLogin, emailVerification;
    private ProgressBar loadingProgressBar;

    private RegistrationViewModel registrationViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        initRegistrationViewModel();
        initComponents();

    }

    private void initRegistrationViewModel() {
        registrationViewModel = ViewModelProviders.of(this, new RegistrationViewModelFactory())
                .get(RegistrationViewModel.class);
    }

    private void initComponents() {
        usernameEditText = findViewById(R.id.inputUsername);
        passwordEditText = findViewById(R.id.inputPassword);
        confirmPasswordEditText = findViewById(R.id.input_confirm_password);
        btnSignup = findViewById(R.id.btn_signup);
        linkLogin = findViewById(R.id.link_login);
        emailVerification = findViewById(R.id.email_verification);
        loadingProgressBar = findViewById(R.id.regLoading);

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
                registrationViewModel.registrationDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString(), confirmPasswordEditText.getText().toString());
            }
        };

        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        confirmPasswordEditText.addTextChangedListener(afterTextChangedListener);
        linkLogin.setOnClickListener(v -> {
            showLoginForm();
        });

        registrationViewModel.getRegistrationFormState().observe(this, new Observer<RegistrationFromState>() {
            @Override
            public void onChanged(RegistrationFromState registrationFromState) {
                if (registrationFromState == null) {
                    return;
                }
                btnSignup.setEnabled(registrationFromState.isDataValid());
                if (registrationFromState.getUsernameError() != null)
                    usernameEditText.setError(getString(registrationFromState.getUsernameError()));
                if (registrationFromState.getPasswordError() != null)
                    passwordEditText.setError(getString(registrationFromState.getPasswordError()));
                if (registrationFromState.getConfirmPasswordError() != null)
                    confirmPasswordEditText.setError(getString(registrationFromState.getConfirmPasswordError()));
            }
        });

        btnSignup.setOnClickListener(v -> {
            loadingProgressBar.setVisibility(View.VISIBLE);
            registrationViewModel.register(usernameEditText.getText().toString(),
                    passwordEditText.getText().toString());
            registrationViewModel.registrationResult().observe(RegistrationActivity.this, user -> {
                if (user.isAuthenticated) {
                    Toast.makeText(this, ("Welcome! " + user.getDisplayName()==null?user.getEmail():user.getDisplayName()), Toast.LENGTH_SHORT).show();
                    goToMainActivity(user);
                } else {
                    Toast.makeText(this, user.userStatus, Toast.LENGTH_SHORT).show();
                }
                loadingProgressBar.setVisibility(View.GONE);
            });
        });

    }

    private void goToMainActivity(LoggedInUser user) {
        Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
        intent.putExtra("USER", user);
        startActivity(intent);
        loadingProgressBar.setVisibility(View.GONE);
        finish();
    }

    private void showLoginForm() {
        startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
        finish();
    }
}
