package com.allandroidprojects.ecomsample.data.factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.allandroidprojects.ecomsample.data.repository.AddProductRepository;
import com.allandroidprojects.ecomsample.data.view_model.AddProductViewModel;


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
