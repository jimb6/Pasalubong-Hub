package com.allandroidprojects.ecomsample.shop;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.allandroidprojects.ecomsample.R;
import com.allandroidprojects.ecomsample.model.Business;
import com.allandroidprojects.ecomsample.shop.data.DataResult;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentChange;

public class ShopActivity extends AppCompatActivity {

    private ShopViewModel shopViewModel;
    private DocumentChange documentChange;
    private Business myBusiness;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        initializeViewModel();
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                 R.id.navigation_dashboard, R.id.navigation_product, R.id.navigation_notifications, R.id.navigation_messages)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
        
        myBusiness = getUserFromIntent();
        registerBusinessEvent();

//  Badge
//        BadgeDrawable messageBadge = navView.getOrCreateBadge(R.id.navigation_messages);
//        messageBadge.setVisible(true);
//        messageBadge.setNumber(11);
//
//        BadgeDrawable notifBadge = navView.getOrCreateBadge(R.id.navigation_notifications);
//        notifBadge.setVisible(true);
//        notifBadge.setNumber(5);
//
//        BadgeDrawable productBadge = navView.getOrCreateBadge(R.id.navigation_product);
//        productBadge.setVisible(true);
//        productBadge.setNumber(2);
    }

    private Business getUserFromIntent() {
        return (Business) getIntent().getSerializableExtra("BUSINESS");
    }


    private void registerBusinessEvent(){
        shopViewModel.businessDataChange(myBusiness);
        shopViewModel.getBusinessDocumentChangesResult().observe(this, docs -> {
            if (docs instanceof DataResult.Modified){
                Toast.makeText(ShopActivity.this, ((DataResult.Modified) docs).getData().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initializeViewModel(){
        shopViewModel = ViewModelProviders.of(this, new ShopViewModelFactory()).get(ShopViewModel.class);
    }



}
