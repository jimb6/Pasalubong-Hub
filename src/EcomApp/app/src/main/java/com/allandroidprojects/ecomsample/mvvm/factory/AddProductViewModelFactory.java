package com.allandroidprojects.ecomsample.mvvm.factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.allandroidprojects.ecomsample.mvvm.repository.AddProductRepository;
import com.allandroidprojects.ecomsample.mvvm.view_model.AddProductViewModel;


public class AddProductViewModelFactory implements ViewModelProvider.Factory {
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(AddProductViewModel.class)){
            return (T) new AddProductViewModel(AddProductRepository.getInstance());
        }else{
            throw new IllegalArgumentException(("Unknown ViewModel class"));
        }
    }


}
