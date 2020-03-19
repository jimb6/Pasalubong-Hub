package com.allandroidprojects.ecomsample.account;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.allandroidprojects.ecomsample.R;
import com.allandroidprojects.ecomsample.startup.data.model.LoggedInUser;

public class AccountActivity extends AppCompatActivity {

    private TextView account_home_button, profile_name, profile_email, profile_mobile, profile_address, profile_name_header, profile_email_header;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initializeComponents();

        LoggedInUser user = getUserFromIntent();
        setupPreferences(user);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        
        
    }

    private void setupPreferences(LoggedInUser user) {
        profile_name.setText(user.getDisplayName()==null?"No Name":user.getDisplayName());
        profile_email.setText(user.getEmail());
        profile_name_header.setText(user.getDisplayName()==null?"No Name":user.getDisplayName());
        profile_email_header.setText(user.getEmail());
    }

    private void initializeComponents() {
        account_home_button = findViewById(R.id.account_home_button);
        account_home_button.setOnClickListener(v->{onBackPressed();});
        profile_name = findViewById(R.id.profile_name);
        profile_mobile = findViewById(R.id.profile_mobile);
        profile_email = findViewById(R.id.profile_email);
        profile_address = findViewById(R.id.profile_address);
        profile_email_header = findViewById(R.id.profile_email_header);
        profile_name_header = findViewById(R.id.profile_name_header);
    }

    private LoggedInUser getUserFromIntent() {
        return (LoggedInUser) getIntent().getSerializableExtra("USER");
    }
}
