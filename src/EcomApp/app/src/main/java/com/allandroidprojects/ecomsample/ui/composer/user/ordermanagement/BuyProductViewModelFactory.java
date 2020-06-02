package com.allandroidprojects.ecomsample.ui.composer.user.ordermanagement;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.allandroidprojects.ecomsample.data.repository.OrderRepository;

public class BuyProductViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private Context context;
    public BuyProductViewModelFactory(Context context){
        this.context = context;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(BuyProductViewModel.class)){
            return (T) new BuyProductViewModel(OrderRepository.getInstance(context));
        }else{
            throw new IllegalArgumentException(("Unknown ViewModel class"));
        }
    }
}
