package com.allandroidprojects.ecomsample.ui.common.components;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.allandroidprojects.ecomsample.R;

public class RatingsListFragment extends Fragment {

    private RatingsListViewModel mViewModel;

    public static RatingsListFragment newInstance() {
        return new RatingsListFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.ratings_list_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(RatingsListViewModel.class);
        // TODO: Use the ViewModel
    }

}
