package com.allandroidprojects.ecomsample.ui.common.components;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.allandroidprojects.ecomsample.R;
import com.allandroidprojects.ecomsample.data.models.Product;
import com.allandroidprojects.ecomsample.data.models.Rating;
import com.allandroidprojects.ecomsample.data.models.Result;
import com.allandroidprojects.ecomsample.data.viewmodel.product.ItemDetailsViewModel;
import com.allandroidprojects.ecomsample.ui.composer.merchant.messaging.ChatroomActivity;
import com.allandroidprojects.ecomsample.ui.composer.user.merchant.MerchantProfileActivity;
import com.allandroidprojects.ecomsample.ui.composer.user.product.CartListActivity;
import com.allandroidprojects.ecomsample.ui.startup.ViewPagerActivity;
import com.allandroidprojects.ecomsample.util.RatingType;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import it.sephiroth.android.library.numberpicker.NumberPicker;

public class ItemDetailsActivity extends AppCompatActivity {
    int imagePosition;
    String stringImageUri;
    public static final String STRING_IMAGE_URI = "ImageUri";
    public static final String STRING_IMAGE_POSITION = "ImagePosition";
    public static Product item;
    private TextView textViewAddToCart, textProductName, textDescription, textPrice, textWholeSeller, textStock, textBuyNow, textRating;
    private LinearLayout layout_message, layout_store, layout_actions;
    private ItemDetailsViewModel viewModel;
    private NumberPicker numberPicker;
    private SimpleDraweeView mImageView;
    private static ViewPager viewPager;
    private static TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);
        initializeViewModel();
        initializeComponents();

        viewPager = findViewById(R.id.viewpager);
        tabLayout = findViewById(R.id.tabs);

        if (viewPager != null) {
            tabLayout.setupWithViewPager(viewPager);
        }

        setupViewPager(viewPager);

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
        textRating.setText(calculateAverageRatings(item.getRatings()) + "*");
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

    double calculateAverageRatings(ArrayList<Rating> ratings){
        double avg = 0;
        for (Rating rate : ratings)
            avg+= rate.getRating();
        return avg / (double) ratings.size();
    }

    private void initializeComponents() {
        mImageView = findViewById(R.id.image1);
        textViewAddToCart = findViewById(R.id.tvAddToCart);
        textProductName = findViewById(R.id.tvProductName);
        textDescription = findViewById(R.id.tvDescription);
        textPrice = findViewById(R.id.tvPrice);
        textWholeSeller = findViewById(R.id.tvWholeSeller);
        textStock = findViewById(R.id.tvStock);
        textBuyNow = findViewById(R.id.tvBuyNow);
        layout_message = findViewById(R.id.layout_Message);
        layout_store = findViewById(R.id.layout_store);
        layout_actions = findViewById(R.id.layout_actions);
        textRating = findViewById(R.id.text_ratings);
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

        layout_store.setOnClickListener(v -> {
            Intent intent = new Intent(ItemDetailsActivity.this, MerchantProfileActivity.class);
            intent.putExtra("OWNERID", item.getBusinessOwnerId());
            startActivity(intent);
        });
    }

    private void initializeViewModel() {
        viewModel = ViewModelProviders.of(this).get(ItemDetailsViewModel.class);
    }

    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getSupportFragmentManager());
        Bundle bundle = new Bundle();

        RatingsListFragment fragment = new RatingsListFragment();
        bundle = new Bundle();
        bundle.putInt("RATE", RatingType.FIVE.getRatingValue());
        fragment.setArguments(bundle);
        adapter.addFragment(fragment, "5(★)");

        fragment = new RatingsListFragment();
        bundle = new Bundle();
        bundle.putInt("RATE", RatingType.FIVE.getRatingValue());
        fragment.setArguments(bundle);
        adapter.addFragment(fragment, "4(★)");

        fragment = new RatingsListFragment();
        bundle = new Bundle();
        bundle.putInt("RATE",RatingType.FIVE.getRatingValue());
        fragment.setArguments(bundle);
        adapter.addFragment(fragment, "3(★)");

        fragment = new RatingsListFragment();
        bundle = new Bundle();
        bundle.putInt("RATE", RatingType.FIVE.getRatingValue());
        fragment.setArguments(bundle);
        adapter.addFragment(fragment, "2(★)");

        fragment = new RatingsListFragment();
        bundle = new Bundle();
        bundle.putInt("RATE", RatingType.FIVE.getRatingValue());
        fragment.setArguments(bundle);
        adapter.addFragment(fragment, "1(★)");

        viewPager.setAdapter(adapter);

    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return super.getItemPosition(object);
        }
    }

}
