package com.allandroidprojects.ecomsample.mvvm.factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.allandroidprojects.ecomsample.mvvm.view_model.ShopViewModel;
import com.allandroidprojects.ecomsample.mvvm.repository.ShopRepository;

public class ShopViewModelFactory implements ViewModelProvider.Factory {
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ShopViewModel.class)){
            return (T) new ShopViewModel(ShopRepository.getInstance());
        }else{
            throw new IllegalArgumentException(("Unknown ViewModel class"));
        }
    }
}
