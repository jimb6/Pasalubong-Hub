package com.allandroidprojects.ecomsample.product;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.allandroidprojects.ecomsample.R;
import com.allandroidprojects.ecomsample.product.ui.buyproduct.BuyProductFragment;

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
