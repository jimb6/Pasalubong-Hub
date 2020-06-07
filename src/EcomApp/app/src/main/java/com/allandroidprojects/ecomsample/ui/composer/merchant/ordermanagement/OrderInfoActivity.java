package com.allandroidprojects.ecomsample.ui.composer.merchant.ordermanagement;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.allandroidprojects.ecomsample.R;
import com.allandroidprojects.ecomsample.data.models.ProductOrder;
import com.allandroidprojects.ecomsample.ui.startup.ViewPagerActivity;
import com.facebook.drawee.view.SimpleDraweeView;

public class OrderInfoActivity extends AppCompatActivity {

    private ProductOrder order;
    private TextView tvClose, itemName, itemAmount,
            itemQuantity, itemDescription, itemStatus, itemEmail, itemDate;
    private SimpleDraweeView image;
    private View message;


    private void setupOrderInformation(ProductOrder order){
        itemName.setText(order.getProduct().getProductname());
        itemAmount.setText(String.valueOf(order.getProduct().getPrice()));
        itemStatus.setText(String.valueOf(order.getStatus()));
        itemEmail.setText(order.getCustomerEmail());
        itemDate.setText(order.getDate_ordered());
        image.setImageURI(Uri.parse(order.getProduct().getImageUrls().get(0)));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_info);

        order = getIntent().getParcelableExtra("ORDER");
        itemName = findViewById(R.id.itemName);
        itemAmount = findViewById(R.id.itemPrice);
        itemStatus = findViewById(R.id.itemStatus);
        itemEmail = findViewById(R.id.itemEmail);
        itemDate = findViewById(R.id.itemDateordered);
        image = findViewById(R.id.image);
        tvClose = findViewById(R.id.tvClose);
        tvClose.setOnClickListener(v ->{
            finish();
        });
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderInfoActivity.this, ViewPagerActivity.class);
                intent.putExtra("position", 0);
                intent.putStringArrayListExtra("images", order.getProduct().getImageUrls());
                startActivity(intent);
            }
        });

        setupOrderInformation(order);

    }
}
