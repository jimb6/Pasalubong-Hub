package com.allandroidprojects.ecomsample.data.factory.product;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.allandroidprojects.ecomsample.data.repository.ProductRepository;
import com.allandroidprojects.ecomsample.data.viewmodel.product.AddProductViewModel;


public class AddProductViewModelFactory implements ViewModelProvider.Factory {
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(AddProductViewModel.class)){
            return (T) new AddProductViewModel(ProductRepository.getInstance().getInstance());
        }else{
            throw new IllegalArgumentException(("Unknown ViewModel class"));
        }
    }


}
