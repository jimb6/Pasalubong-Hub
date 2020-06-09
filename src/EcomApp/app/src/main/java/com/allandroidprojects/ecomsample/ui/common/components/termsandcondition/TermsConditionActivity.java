package com.allandroidprojects.ecomsample.ui.common.components.termsandcondition;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;

import com.allandroidprojects.ecomsample.R;
import com.allandroidprojects.ecomsample.data.factory.account.AccountViewModelFactory;
import com.allandroidprojects.ecomsample.data.models.Business;
import com.allandroidprojects.ecomsample.data.models.Result;
import com.allandroidprojects.ecomsample.data.viewmodel.account.AccountViewModel;
import com.allandroidprojects.ecomsample.ui.composer.merchant.startup.MerchantActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class TermsConditionActivity extends AppCompatActivity {

    private Business userBusiness;
    private AccountViewModel accountViewModel;
    private FirebaseUser firebaseUser;
    private TextView decline, accept, terms;


    private void goToStore() {
        Intent intent = new Intent(TermsConditionActivity.this, MerchantActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("BUSINESS", userBusiness);
        startActivity(intent);
    }

    private void createNewBusiness() {
        Business business = new Business();
        business.setBusinessName(firebaseUser.getDisplayName());
        business.setBusinessAddress("");
        business.setOwnerId(firebaseUser.getUid());
        business.setBusinessEmail(firebaseUser.getEmail());
        ArrayList<String> photos = new ArrayList<>();
        photos.add(String.valueOf(firebaseUser.getPhotoUrl()));
        business.setBusinessPhotos(photos);
        business.setCoverUri(photos.get(0));
        saveStore(business);
    }

    private void initializeViewModel() {
        accountViewModel = ViewModelProviders.of(this, new AccountViewModelFactory())
                .get(AccountViewModel.class);
    }
    private void saveStore(Business business) {
        accountViewModel.createNewBusiness(business);
        accountViewModel.getSaveBusinessResult().observe(this, result -> {
            if (result instanceof Result.Success) {
                userBusiness = (Business) ((Result.Success) result).getData();
                goToStore();
            } else {
                Toast.makeText(this, result.toString(), Toast.LENGTH_SHORT);
            }
        });
    }

    private void readTerms() {
        StringBuilder text = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(getAssets().open("TermsAndCons.txt")))) {

            // do reading, usually loop until end of file reading
            String mLine;
            while ((mLine = reader.readLine()) != null) {
                text.append(mLine);
                text.append('\n');
            }
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "Error reading file!", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        terms.setText((CharSequence) text);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_condition);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("PasHub Terms and Conition");

        initializeViewModel();

        Intent intent = getIntent();
        intent.getParcelableArrayExtra("BUSINESS");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        decline = findViewById(R.id.tvDecline);
        accept = findViewById(R.id.tvAccept);
        terms = findViewById(R.id.terms);

        decline.setOnClickListener(v -> {
            finish();
        });

        accept.setOnClickListener(v -> {
            createNewBusiness();
        });

//        readTerms();

    }
}
