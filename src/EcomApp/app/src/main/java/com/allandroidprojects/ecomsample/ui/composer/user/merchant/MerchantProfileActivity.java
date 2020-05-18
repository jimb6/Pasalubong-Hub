package com.allandroidprojects.ecomsample.ui.composer.user.merchant;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.allandroidprojects.ecomsample.R;
import com.allandroidprojects.ecomsample.data.factory.account.ShopViewModelFactory;
import com.allandroidprojects.ecomsample.data.models.Business;
import com.allandroidprojects.ecomsample.data.models.Result;
import com.allandroidprojects.ecomsample.data.viewmodel.account.ShopViewModel;
import com.allandroidprojects.ecomsample.ui.startup.ViewPagerActivity;
import com.allandroidprojects.ecomsample.ui.composer.user.merchant.maps.MapFragment;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class MerchantProfileActivity extends AppCompatActivity {


    private static ViewPager viewPager;
    private static TabLayout tabLayout;
    private SimpleDraweeView cover;
    private View loadingView;

    private ShopViewModel viewmodel;

    private String businessId;
    public static Business business;


    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getSupportFragmentManager());
        Bundle bundle = new Bundle();

        AboutStoreFragment about = new AboutStoreFragment();
        bundle = new Bundle();
        about.setArguments(bundle);
        adapter.addFragment(about, "ABOUT");

        ReviewStoreFragment fragment = new ReviewStoreFragment();
        bundle = new Bundle();
        fragment.setArguments(bundle);
        adapter.addFragment(fragment, "REVIEWS");

        MapFragment map = new MapFragment();
        bundle = new Bundle();
        map.setArguments(bundle);
        adapter.addFragment(map, "MAP LOCATION");

        viewPager.setAdapter(adapter);

    }

    private void initializeViewModel(){
        viewmodel = ViewModelProviders.of(this, new ShopViewModelFactory()).get(ShopViewModel.class);
    }

    private void setupBusinessInfo(){
        viewmodel.getBusinessInfo(businessId);
        viewmodel.getBusinessInfo().observe(this, b -> {
            if (b instanceof Result.Success){
                business = (Business) ((Result.Success) b).getData();
                setTitle(business.getBusinessName());
                cover.setImageURI(business.getBusinessPhotos().get(0));
                cover.setOnClickListener(v->{
                    Intent intent = new Intent(MerchantProfileActivity.this, ViewPagerActivity.class);
                    intent.putExtra("position", 0);
                    intent.putStringArrayListExtra("images", business.getBusinessPhotos());
                    startActivity(intent);
                });
                setupViewPager(viewPager);
                tabLayout.invalidate();
            }else{
                Toast.makeText(this, "Business Not Found", Toast.LENGTH_SHORT).show();
                MerchantProfileActivity.this.finish();
            }
//            loadingView.setVisibility(View.GONE);
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewPager = findViewById(R.id.viewpager);
        tabLayout = findViewById(R.id.tabs);

        if (viewPager != null) {
            tabLayout.setupWithViewPager(viewPager);
        }

        cover = findViewById(R.id.cover);
//        loadingView = findViewById(R.id.loadinglayout);

        if(getIntent().hasExtra("OWNERID")){
            this.businessId = getIntent().getStringExtra("OWNERID");
        }else{
            finish();
        }
        initializeViewModel();
        setupBusinessInfo();
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
    }
}
