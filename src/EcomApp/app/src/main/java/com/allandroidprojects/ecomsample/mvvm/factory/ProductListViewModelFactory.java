package com.allandroidprojects.ecomsample.mvvm.factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.allandroidprojects.ecomsample.mvvm.repository.ProductListRepository;
import com.allandroidprojects.ecomsample.mvvm.view_model.ProductListViewModel;
import com.allandroidprojects.ecomsample.mvvm.view_model.ShopViewModel;

public class ProductListViewModelFactory implements ViewModelProvider.Factory {
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ShopViewModel.class)){
            return (T) new ProductListViewModel(ProductListRepository.getInstance());
        }else{
            throw new IllegalArgumentException(("Unknown ViewModel class"));
        }
    }
}
