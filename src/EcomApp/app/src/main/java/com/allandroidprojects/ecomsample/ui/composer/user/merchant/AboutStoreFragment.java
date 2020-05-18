package com.allandroidprojects.ecomsample.user.merchant;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.allandroidprojects.ecomsample.R;
import com.allandroidprojects.ecomsample.data.models.Business;
import com.facebook.drawee.view.SimpleDraweeView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AboutStoreFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AboutStoreFragment extends Fragment {

    private View root;
    private TextView businessName, businessAddress, businessEmail;
    private SimpleDraweeView photos;

    private Business business;

    public AboutStoreFragment() {
        // Required empty public constructor
    }

    public static AboutStoreFragment newInstance(String param1, String param2) {
        AboutStoreFragment fragment = new AboutStoreFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    private void setupPreferences(){
        businessName.setText(business.getBusinessName());
        businessEmail.setText(business.getBusinessEmail());
        businessAddress.setText(business.getBusinessAddress());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root =  inflater.inflate(R.layout.fragment_about_store, container, false);
        business = MerchantProfileActivity.business;

        businessName = root.findViewById(R.id.tvBusiness);
        businessEmail = root.findViewById(R.id.tvBusinessEmail);
        businessAddress = root.findViewById(R.id.tvBusinessAddress);

        setupPreferences();

        return root;
    }
}
