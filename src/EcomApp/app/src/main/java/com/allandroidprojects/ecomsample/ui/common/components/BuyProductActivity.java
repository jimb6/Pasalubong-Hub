package com.allandroidprojects.ecomsample.ui.common.components;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.allandroidprojects.ecomsample.R;

public class BuyProductActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.buy_product_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, BuyProductFragment.newInstance())
                    .commitNow();
        }
    }
}
