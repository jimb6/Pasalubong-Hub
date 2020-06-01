package com.allandroidprojects.ecomsample.data.factory.product;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.allandroidprojects.ecomsample.data.repository.OrderRepository;
import com.allandroidprojects.ecomsample.data.viewmodel.product.ItemDetailsViewModel;

public class ItemDetailsModelFactory extends ViewModelProvider.NewInstanceFactory {

    private Context context;
    public ItemDetailsModelFactory(Context context){
        this.context = context;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ItemDetailsViewModel.class)){
            return (T) new ItemDetailsViewModel(OrderRepository.getInstance(context));
        }else{
            throw new IllegalArgumentException(("Unknown ViewModel class"));
        }
    }
}
