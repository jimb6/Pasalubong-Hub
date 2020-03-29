package com.allandroidprojects.ecomsample.product;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.allandroidprojects.ecomsample.R;
import com.allandroidprojects.ecomsample.fragments.ViewPagerActivity;
import com.allandroidprojects.ecomsample.messages.ChatroomActivity;
import com.allandroidprojects.ecomsample.model.product.Product;
import com.allandroidprojects.ecomsample.mvvm.view_model.ItemDetailsViewModel;
import com.allandroidprojects.ecomsample.options.CartListActivity;
import com.allandroidprojects.ecomsample.shop.ui.chat_room.MessagingActivity;
import com.allandroidprojects.ecomsample.startup.data.Result;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.snackbar.Snackbar;

import it.sephiroth.android.library.numberpicker.NumberPicker;

public class ItemDetailsActivity extends AppCompatActivity {
    int imagePosition;
    String stringImageUri;
    public static final String STRING_IMAGE_URI = "ImageUri";
    public static final String STRING_IMAGE_POSITION = "ImagePosition";
    private Product item;
    private TextView textViewAddToCart, textProductName, textDescription, textPrice, textWholeSeller, textStock, textBuyNow;
    private LinearLayout layout_message, layout_store, layout_actions;
    private ItemDetailsViewModel viewModel;
    private NumberPicker numberPicker;
    private SimpleDraweeView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);
        initializeViewModel();
        initializeComponents();

//        TextView textViewBuyNow = (TextView)findViewById(R.id.text_action_bottom2);

        //Getting image uri from previous screen
        if (getIntent() != null) {
//            stringImageUri = getIntent().getStringExtra(ProductListFragment.STRING_IMAGE_URI);
            imagePosition = getIntent().getIntExtra(STRING_IMAGE_URI, 0);
            item = getIntent().getParcelableExtra("product");
        }
//        Uri uri = Uri.parse(stringImageUri);
        assert item != null;
        mImageView.setImageURI(item.getImageUrls().get(0));
        textProductName.setText(item.getProductname());
        textDescription.setText(item.getProductDescription());
        textPrice.setText("₱ " + item.getPrice());
        textWholeSeller.setText("Whole Seller: " + item.getWholeSeller());
        textStock.setText("Stock: " + item.getStock());
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ItemDetailsActivity.this, ViewPagerActivity.class);
                intent.putExtra("position", imagePosition);
                intent.putStringArrayListExtra("images", item.getImageUrls());
                startActivity(intent);
            }
        });

        textViewAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.saveToCartList(item);
                viewModel.getCart().observe(ItemDetailsActivity.this, result -> {
                    if (result instanceof Result.Success) {
//                        Toast.makeText(ItemDetailsActivity.this, "Product Added to Cart Successfully!", Toast.LENGTH_SHORT).show();
                        Snackbar.make(layout_actions, R.string.item_add_to_cart_success, Snackbar.LENGTH_LONG)
                                .setAction(R.string.go_to_cart, v -> startActivity(new Intent(ItemDetailsActivity.this, CartListActivity.class)));
                    } else
                        Snackbar.make(layout_actions, R.string.item_add_to_cart_error, Snackbar.LENGTH_LONG);
                });
//                ImageUrlUtils imageUrlUtils = new ImageUrlUtils();
//                imageUrlUtils.addCartListImageUri(stringImageUri);
//                Toast.makeText(ItemDetailsActivity.this,"Item added to cart.",Toast.LENGTH_SHORT).show();
//                MainActivity.notificationCountCart++;
//                NotificationCountSetClass.setNotifyCount(MainActivity.notificationCountCart);
            }
        });

//        textViewBuyNow.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ImageUrlUtils imageUrlUtils = new ImageUrlUtils();
//                imageUrlUtils.addCartListImageUri(stringImageUri);
//                MainActivity.notificationCountCart++;
//                NotificationCountSetClass.setNotifyCount(MainActivity.notificationCountCart);
//                startActivity(new Intent(ItemDetailsActivity.this, CartListActivity.class));
//
//            }
//        });
    }

    private void initializeComponents() {
        mImageView = (SimpleDraweeView) findViewById(R.id.image1);
        textViewAddToCart = (TextView) findViewById(R.id.tvAddToCart);
        textProductName = findViewById(R.id.tvProductName);
        textDescription = findViewById(R.id.tvDescription);
        textPrice = findViewById(R.id.tvPrice);
        textWholeSeller = findViewById(R.id.tvWholeSeller);
        textStock = findViewById(R.id.tvStock);
        textBuyNow = findViewById(R.id.tvBuyNow);
        layout_message = findViewById(R.id.layout_Message);
        layout_store = findViewById(R.id.layout_store);
        layout_actions = findViewById(R.id.layout_actions);
//        numberPicker = (NumberPicker) findViewById(R.id.numberPicker);

        textBuyNow.setOnClickListener(v ->{
            Intent intent = new Intent(ItemDetailsActivity.this, BuyProductActivity.class);
            intent.putExtra("product", item);
            startActivity(intent);
        });

        layout_message.setOnClickListener(v ->{
            Intent intent = new Intent(ItemDetailsActivity.this, ChatroomActivity.class);
            intent.putExtra("product", item);
            startActivity(intent);
        });
    }

    private void initializeViewModel() {
        viewModel = ViewModelProviders.of(this).get(ItemDetailsViewModel.class);
    }

}
