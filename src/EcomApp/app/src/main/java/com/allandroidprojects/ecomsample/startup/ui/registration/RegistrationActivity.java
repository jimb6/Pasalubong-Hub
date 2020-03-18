package com.allandroidprojects.ecomsample.startup.ui.registration;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.allandroidprojects.ecomsample.R;
import com.allandroidprojects.ecomsample.startup.ui.login.LoginActivity;

public class RegistrationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        final TextView link_login = findViewById(R.id.link_login);
        link_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoginForm();
            }
        });

    }

    private void initFunction(){

    }

    private void showLoginForm(){
        startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
        finish();
    }
}
