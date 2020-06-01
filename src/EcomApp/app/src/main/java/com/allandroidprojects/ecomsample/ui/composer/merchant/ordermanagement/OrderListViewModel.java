package com.allandroidprojects.ecomsample.ui.composer.merchant.ordermanagement;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.allandroidprojects.ecomsample.data.models.ProductOrder;
import com.allandroidprojects.ecomsample.data.models.Result;
import com.allandroidprojects.ecomsample.data.repository.OrderRepository;

public class OrderListViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    private static volatile OrderRepository repository;
    private MutableLiveData<Result<ProductOrder>> orders;

    public OrderListViewModel(OrderRepository repository){
        this.repository = repository;
    }

    public void getMerchantOrders(String category){
        orders = repository.getMerchantOrderByCategory(category);
    }
}
