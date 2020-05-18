package com.allandroidprojects.ecomsample.ui.common.components;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.allandroidprojects.ecomsample.R;

public class EmptyActivity extends AppCompatActivity {

    private Button homepage;
    public static boolean isActivityRunning = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty);
        initializeComponent();
    }

    private void initializeComponent() {
        homepage = findViewById(R.id.home_button);
        homepage.setOnClickListener(v -> {
            onBackPressed();
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
}
