package com.allandroidprojects.ecomsample.ui.composer.merchant.startup;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.allandroidprojects.ecomsample.R;
import com.allandroidprojects.ecomsample.data.models.Business;
import com.allandroidprojects.ecomsample.data.models.LoggedInUser;
import com.allandroidprojects.ecomsample.data.viewmodel.account.ShopViewModel;
import com.allandroidprojects.ecomsample.interfaces.IDataHelper;
import com.allandroidprojects.ecomsample.ui.composer.merchant.account.ShopInfoFragment;
import com.allandroidprojects.ecomsample.ui.composer.merchant.dashboard.DashboardFragment;
import com.allandroidprojects.ecomsample.ui.composer.merchant.main.SectionsPagerAdapter;
import com.allandroidprojects.ecomsample.ui.composer.merchant.notifications.NotificationsFragment;
import com.allandroidprojects.ecomsample.ui.composer.merchant.ordermanagement.OrderManagement;
import com.allandroidprojects.ecomsample.ui.composer.merchant.products.ProductFragment;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;

public class MerchantActivity extends AppCompatActivity implements IDataHelper {

    public static Business myBusiness;
    public static LoggedInUser user;
    public static FirebaseUser firebaseUser;
    public static boolean isActivityRunning = false;
    private ShopViewModel shopViewModel;
    private DocumentChange documentChange;
    private TextView title;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant);

//        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        viewPager = findViewById(R.id.view_pager);
//        viewPager.setAdapter(sectionsPagerAdapter);

//        TabLayout tabs = findViewById(R.id.tabs);
////        tabs.setupWithViewPager(viewPager);
//
        if (viewPager != null) {
            setupViewPager(viewPager);
//            tabs.setupWithViewPager(viewPager);
        }

        myBusiness = getUserFromIntent();
        title = findViewById(R.id.title);
        title.setText("HUB - " + myBusiness.getBusinessName());

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.view_pager, new DashboardFragment(), "DASHBOARD")
                .addToBackStack(null)
                .commit();

        getPendingIntent();
    }

    public boolean isBusinessValid(){
        if (myBusiness.getBusinessName() != null || myBusiness.getBusinessName().equals("")){
            return false;
        }

        if (myBusiness.getBusinessAddress() != null || myBusiness.getBusinessName().equals("")){
            return false;
        }

        if (myBusiness.getBusinessEmail() != null || myBusiness.getBusinessName().equals("")){
            return false;
        }
        return true;
    }

    private void getPendingIntent(){
        Intent intent = getIntent();
        if (intent.hasExtra(getString(R.string.intent_order_reference))){
            String reference = intent.getStringExtra(getString(R.string.intent_order_reference));
            if(viewPager != null)
                viewPager.setCurrentItem(2);
        }
    }

    public ViewPager getViewPager() {
        if (null == viewPager) {
            viewPager = (ViewPager) findViewById(R.id.view_pager);
        }
        return viewPager;
    }

    private void setupViewPager(ViewPager viewPager) {
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        Bundle bundle = new Bundle();

        DashboardFragment dashfragment = new DashboardFragment();
        dashfragment.setArguments(bundle);
        adapter.addFragment(dashfragment, getString(R.string.item_shop_1));

        ProductFragment productfragment = new ProductFragment();
        productfragment.setArguments(bundle);
        adapter.addFragment(productfragment, getString(R.string.item_shop_2));

        OrderManagement orderfragment = new OrderManagement();
        bundle = new Bundle();
        orderfragment.setArguments(bundle);
        adapter.addFragment(orderfragment, getString(R.string.item_shop_3));

        NotificationsFragment notiffragment = new NotificationsFragment();
        bundle = new Bundle();
        notiffragment.setArguments(bundle);
        adapter.addFragment(notiffragment, getString(R.string.item_shop_4));

        NotificationsFragment messagingfragment = new NotificationsFragment();
        bundle = new Bundle();
        messagingfragment.setArguments(bundle);
        adapter.addFragment(messagingfragment, getString(R.string.item_shop_5));

        ShopInfoFragment infofragment = new ShopInfoFragment();
        bundle = new Bundle();
        infofragment.setArguments(bundle);
        adapter.addFragment(infofragment, getString(R.string.item_shop_6));

        viewPager.setAdapter(adapter);

    }

    @Override
    public void onDataComplete(boolean hasContents) {

    }

    private Business getUserFromIntent() {
        return (Business) getIntent().getParcelableExtra("BUSINESS");
    }

    @Override
    public void onBackPressed() {
        if (getViewPager().getCurrentItem() == 0)
            super.onBackPressed();
        else
            getViewPager().setCurrentItem(0);
    }

}