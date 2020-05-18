package com.allandroidprojects.ecomsample.data.factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.allandroidprojects.ecomsample.data.view_model.ProductViewModel;
import com.allandroidprojects.ecomsample.data.view_model.ShopViewModel;
import com.allandroidprojects.ecomsample.data.repository.ShopRepository;

public class ProductViewModelFactory implements ViewModelProvider.Factory {
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ShopViewModel.class)){
            return (T) new ProductViewModel(ShopRepository.getInstance());
        }else{
            throw new IllegalArgumentException(("Unknown ViewModel class"));
        }
    }
}