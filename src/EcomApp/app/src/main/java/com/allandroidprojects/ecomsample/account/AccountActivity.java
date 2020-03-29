package com.allandroidprojects.ecomsample.account;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;

import com.allandroidprojects.ecomsample.R;
import com.allandroidprojects.ecomsample.model.Business;
import com.allandroidprojects.ecomsample.model.LoggedInUser;
import com.allandroidprojects.ecomsample.shop.ShopActivity;
import com.allandroidprojects.ecomsample.startup.data.Result;
import com.allandroidprojects.ecomsample.startup.ui.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class AccountActivity extends AppCompatActivity {

    private TextView account_home_button, profile_name, profile_email, profile_mobile, profile_address, profile_name_header, profile_email_header, shop_view;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private LoggedInUser user;
    private AccountViewModel accountViewModel;
    private ProgressBar loadingProgressBar;
    private boolean hasBusiness;
    private Business userBusiness;
    private FirebaseUser firebaseUser;
    public static boolean isActivityRunning = false;

    //        private GoogleCredentials credentials = GoogleCredentials.getApplicationDefault();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initializeViewModel();
        initializeComponents();

        checkAuthenticationState();
        setupPreferences();
        checkUserBusiness();
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }

    private void checkAuthenticationState(){
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String photo = firebaseUser.getPhotoUrl() == null?
                "https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcQMX7u2vT0EXHHAobJCKBcqwJAfFKWpgdZ59McdkiYVyVeU_27H" :
                firebaseUser.getPhotoUrl().toString();
        user = new LoggedInUser(firebaseUser.getUid(), firebaseUser.getDisplayName(), firebaseUser.getEmail(), photo);
        if(firebaseUser == null){
            Intent intent = new Intent(AccountActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }else{

        }
    }

    private void checkUserBusiness() {
        loadingProgressBar.setVisibility(View.VISIBLE);
        accountViewModel.getBusiness(user);
        accountViewModel.getBusinessResult().observe(AccountActivity.this, business -> {
            loadingProgressBar.setVisibility(View.GONE);
            if (business instanceof Result.Success) {
                shop_view.setText(R.string.has_business_text);
                hasBusiness = true;
                userBusiness = (Business) ((Result.Success) business).getData();
            } else {
                shop_view.setText(R.string.no_business_text);
                hasBusiness = false;
            }
            shop_view.setClickable(true);
        });
    }

    private void initializeViewModel() {
        accountViewModel = ViewModelProviders.of(this, new AccountViewModelFactory())
                .get(AccountViewModel.class);
    }

    private void setupPreferences() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        user = new LoggedInUser(firebaseUser.getUid(), firebaseUser.getDisplayName(), firebaseUser.getEmail(), firebaseUser.getPhotoUrl().getPath());

        profile_name.setText(user.getDisplayName() == null ? "No Name" : user.getDisplayName());
        profile_email.setText(user.getEmail());
        profile_name_header.setText(user.getDisplayName() == null ? "No Name" : user.getDisplayName());
        profile_email_header.setText(user.getEmail());
    }

    private void initializeComponents() {
        loadingProgressBar = findViewById(R.id.progressBar2);
        account_home_button = findViewById(R.id.account_home_button);
        account_home_button.setOnClickListener(v -> {
            onBackPressed();
        });
        profile_name = findViewById(R.id.profile_name);
        profile_mobile = findViewById(R.id.profile_mobile);
        profile_email = findViewById(R.id.profile_email);
        profile_address = findViewById(R.id.profile_address);
        profile_email_header = findViewById(R.id.profile_email_header);
        profile_name_header = findViewById(R.id.profile_name_header);
        shop_view = findViewById(R.id.shop_view);
        shop_view.setOnClickListener(v -> {
            if (hasBusiness)
                goToStore();
            else
                saveStore(new Business(user));
        });
    }

    private LoggedInUser getUserFromIntent() {
        return (LoggedInUser) getIntent().getSerializableExtra("USER");
    }

    private void goToStore() {
        Intent intent = new Intent(AccountActivity.this, ShopActivity.class);
        intent.putExtra("BUSINESS", userBusiness);
        startActivity(intent);
    }

    private void saveStore(Business business) {
        loadingProgressBar.setVisibility(View.VISIBLE);
        accountViewModel.createNewBusiness(business);
        accountViewModel.getSaveBusinessResult().observe(AccountActivity.this, result -> {
            loadingProgressBar.setVisibility(View.GONE);
            if (result instanceof Result.Success) {
                Intent intent = new Intent(AccountActivity.this, ShopActivity.class);
                intent.putExtra("BUSINESS", (Business) (((Result.Success) result).getData()));
                startActivity(intent);
            } else {
                Toast.makeText(this, result.toString(), Toast.LENGTH_SHORT);
            }
        });
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        checkUserBusiness();
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
