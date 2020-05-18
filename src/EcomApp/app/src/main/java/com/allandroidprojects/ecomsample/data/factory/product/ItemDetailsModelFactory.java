package com.allandroidprojects.ecomsample.data.factory.product;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.allandroidprojects.ecomsample.data.remote.product.ProductListRepository;
import com.allandroidprojects.ecomsample.data.viewmodel.product.ItemDetailsViewModel;

public class ItemDetailsModelFactory implements ViewModelProvider.Factory {
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ItemDetailsViewModel.class)){
            return (T) new ItemDetailsViewModel(ProductListRepository.getInstance());
        }else{
            throw new IllegalArgumentException(("Unknown ViewModel class"));
        }
    }
}
