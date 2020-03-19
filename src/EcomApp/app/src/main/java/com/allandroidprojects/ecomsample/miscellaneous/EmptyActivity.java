package com.allandroidprojects.ecomsample.miscellaneous;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.allandroidprojects.ecomsample.R;

public class EmptyActivity extends AppCompatActivity {

    private Button homepage;

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
}
