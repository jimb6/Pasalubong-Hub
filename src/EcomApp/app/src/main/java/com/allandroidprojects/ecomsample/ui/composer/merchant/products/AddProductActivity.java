package com.allandroidprojects.ecomsample.ui.composer.merchant.products;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.allandroidprojects.ecomsample.R;
import com.allandroidprojects.ecomsample.data.models.Product;

public class AddProductActivity extends AppCompatActivity {

    private Product product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getIntent().hasExtra("PRODUCT")) {
            product = getIntent().getParcelableExtra("PRODUCT");
        }
    }

    public Product getProduct() {
        return this.product;
    }

}
