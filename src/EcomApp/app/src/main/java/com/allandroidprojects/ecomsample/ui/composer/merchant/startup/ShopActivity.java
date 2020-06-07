package com.allandroidprojects.ecomsample.ui.composer.merchant.startup;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.allandroidprojects.ecomsample.R;
import com.allandroidprojects.ecomsample.data.factory.account.ShopViewModelFactory;
import com.allandroidprojects.ecomsample.data.models.Business;
import com.allandroidprojects.ecomsample.data.models.fcm.Chatroom;
import com.allandroidprojects.ecomsample.data.models.LoggedInUser;
import com.allandroidprojects.ecomsample.data.models.Result;
import com.allandroidprojects.ecomsample.data.viewmodel.account.ShopViewModel;
import com.allandroidprojects.ecomsample.ui.common.components.messaging.MessagingActivity;
import com.allandroidprojects.ecomsample.ui.composer.user.authentication.login.LoginActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;

public class ShopActivity extends AppCompatActivity {

    private ShopViewModel shopViewModel;
    private DocumentChange documentChange;
    public static Business myBusiness;
    public static LoggedInUser user;
    public static FirebaseUser firebaseUser;
    public static boolean isActivityRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        BottomNavigationView navView = findViewById(R.id.nav_view);

        initializeViewModel();

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_dashboard,
                R.id.navigation_product,
                R.id.navigation_notifications,
                R.id.navigation_messages,
                R.id.navigation_shop)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        myBusiness = getUserFromIntent();
        checkAuthenticationState();
        getMyBusinessPreference();
        getPendingIntent();

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

    private void getMyBusinessPreference() {
        shopViewModel.validateBusiness(myBusiness);
        shopViewModel.getBusinessPreferences().observe(this, result -> {
            if (result instanceof Result.Success){
                myBusiness = (Business) ((Result.Success) result).getData();
                registerBusinessEvent();
            }
        });
    }

    private void checkAuthenticationState(){
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        user = new LoggedInUser(firebaseUser.getUid(), firebaseUser.getDisplayName(), firebaseUser.getEmail(), firebaseUser.getPhotoUrl());
        if(firebaseUser == null){
            Intent intent = new Intent(ShopActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }else{
        }
    }

    private void getPendingIntent(){
        Intent intent = getIntent();
        if (intent.hasExtra(getString(R.string.intent_chatroom))){
            Chatroom chatroom = (Chatroom) intent.getSerializableExtra(getString(R.string.intent_chatroom));
            Intent chatroomIntent = new Intent(ShopActivity.this, MessagingActivity.class);
            chatroomIntent.putExtra(getString(R.string.intent_chatroom), chatroom);
            startActivity(chatroomIntent);
        }
    }

    private Business getUserFromIntent() {
        return (Business) getIntent().getParcelableExtra("BUSINESS");
    }


    private void registerBusinessEvent(){
//        shopViewModel.businessDataChange(myBusiness);
//        shopViewModel.getBusinessDocumentChangesResult().observe(this, docs -> {
//            if (docs instanceof DataResult.Modified){
//                Toast.makeText(ShopActivity.this, ((DataResult.Modified) docs).getData().toString(), Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    private void initializeViewModel(){
        shopViewModel = ViewModelProviders.of(this, new ShopViewModelFactory()).get(ShopViewModel.class);
    }


    @Override
    protected void onStart() {
        super.onStart();
        isActivityRunning = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isActivityRunning = false;
    }



}
