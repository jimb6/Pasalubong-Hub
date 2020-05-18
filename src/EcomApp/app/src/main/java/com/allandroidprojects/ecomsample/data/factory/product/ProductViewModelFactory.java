package com.allandroidprojects.ecomsample.data.factory.product;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.allandroidprojects.ecomsample.data.repository.account.ShopRepository;
import com.allandroidprojects.ecomsample.data.viewmodel.account.ShopViewModel;
import com.allandroidprojects.ecomsample.data.viewmodel.product.ProductViewModel;

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
