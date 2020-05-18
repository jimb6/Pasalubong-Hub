package com.allandroidprojects.ecomsample.ui.composer.merchant.messaging;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.allandroidprojects.ecomsample.R;

public class MessagingFragment extends Fragment {

    private MessegaingViewModel mViewModel;

    public static MessagingFragment newInstance() {
        return new MessagingFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.messegaing_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(MessegaingViewModel.class);
        // TODO: Use the ViewModel
    }

}
