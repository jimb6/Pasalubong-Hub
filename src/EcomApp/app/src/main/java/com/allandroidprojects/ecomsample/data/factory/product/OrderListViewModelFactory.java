package com.allandroidprojects.ecomsample.data.factory.product;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.allandroidprojects.ecomsample.data.repository.OrderRepository;
import com.allandroidprojects.ecomsample.data.repository.ProductRepository;
import com.allandroidprojects.ecomsample.data.viewmodel.account.ShopViewModel;
import com.allandroidprojects.ecomsample.data.viewmodel.product.ProductListViewModel;
import com.allandroidprojects.ecomsample.ui.composer.merchant.ordermanagement.OrderListViewModel;

public class OrderListViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private Context context;
    public OrderListViewModelFactory(Context context){
        this.context = context;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(OrderListViewModel.class)){
            return (T) new OrderListViewModel(OrderRepository.getInstance(context));
        }else{
            throw new IllegalArgumentException(("Unknown ViewModel class"));
        }
    }
}
