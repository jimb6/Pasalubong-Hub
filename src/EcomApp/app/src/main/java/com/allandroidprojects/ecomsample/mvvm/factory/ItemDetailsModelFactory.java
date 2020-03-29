package com.allandroidprojects.ecomsample.mvvm.factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.allandroidprojects.ecomsample.mvvm.repository.ProductListRepository;
import com.allandroidprojects.ecomsample.mvvm.view_model.CartListViewModel;
import com.allandroidprojects.ecomsample.mvvm.view_model.ItemDetailsViewModel;

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
