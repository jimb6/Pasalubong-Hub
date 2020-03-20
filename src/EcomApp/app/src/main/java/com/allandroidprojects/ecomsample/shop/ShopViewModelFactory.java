package com.allandroidprojects.ecomsample.shop;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.allandroidprojects.ecomsample.shop.data.ShopRepository;

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
